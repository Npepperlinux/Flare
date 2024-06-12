package dev.dimension.flare.data.datasource.vvo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.dimension.flare.data.network.vvo.VVOService
import dev.dimension.flare.data.repository.LoginExpiredException
import dev.dimension.flare.model.MicroBlogKey
import dev.dimension.flare.ui.model.UiUser
import dev.dimension.flare.ui.model.mapper.toUi

internal class SearchUserPagingSource(
    private val service: VVOService,
    private val accountKey: MicroBlogKey,
    private val query: String,
) : PagingSource<Int, UiUser>() {
    private val containerId by lazy {
        "100103type=3&q=$query&t="
    }

    override fun getRefreshKey(state: PagingState<Int, UiUser>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UiUser> {
        try {
            val config = service.config()
            if (config.data?.login != true) {
                return LoadResult.Error(
                    LoginExpiredException,
                )
            }
            val response =
                service.getContainerIndex(
                    containerId = containerId,
                    pageType = "searchall",
                    page = params.key,
                )
            val users =
                response.data
                    ?.cards
                    ?.flatMap {
                        it.cardGroup.orEmpty()
                    }?.mapNotNull {
                        it.user
                    }.orEmpty()
            return LoadResult.Page(
                data = users.map { it.toUi(accountKey = accountKey) },
                prevKey = null,
                nextKey = if (users.isEmpty()) null else params.key?.plus(1),
            )
        } catch (e: Throwable) {
            return LoadResult.Error(e)
        }
    }
}
