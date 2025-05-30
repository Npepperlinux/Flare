package dev.dimension.flare.data.datasource.xqt

import androidx.paging.PagingState
import dev.dimension.flare.common.BasePagingSource
import dev.dimension.flare.data.network.xqt.XQTService
import dev.dimension.flare.data.network.xqt.model.User
import dev.dimension.flare.model.MicroBlogKey
import dev.dimension.flare.ui.model.UiUserV2
import dev.dimension.flare.ui.model.mapper.render

internal class TrendsUserPagingSource(
    private val service: XQTService,
    private val accountKey: MicroBlogKey,
) : BasePagingSource<Int, UiUserV2>() {
    override fun getRefreshKey(state: PagingState<Int, UiUserV2>): Int? = null

    override suspend fun doLoad(params: LoadParams<Int>): LoadResult<Int, UiUserV2> {
        service
            .getUserRecommendations(
                limit = params.loadSize,
                userId = accountKey.id,
            ).mapNotNull {
                if (it.user != null && it.userID != null) {
                    User(
                        legacy = it.user,
                        restId = it.userID,
                    ).render(accountKey = accountKey)
                } else {
                    null
                }
            }.let {
                return LoadResult.Page(
                    data = it,
                    prevKey = null,
                    nextKey = null,
                )
            }
    }
}
