package dev.dimension.flare.data.datasource.guest.mastodon

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.dimension.flare.data.network.mastodon.GuestMastodonService
import dev.dimension.flare.model.MicroBlogKey
import dev.dimension.flare.ui.model.UiTimeline
import dev.dimension.flare.ui.model.mapper.renderGuest

internal class GuestStatusDetailPagingSource(
    private val service: GuestMastodonService,
    private val host: String,
    private val statusKey: MicroBlogKey,
    private val statusOnly: Boolean,
) : PagingSource<Int, UiTimeline>() {
    override fun getRefreshKey(state: PagingState<Int, UiTimeline>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UiTimeline> =
        try {
            val result =
                if (statusOnly) {
                    val current =
                        service.lookupStatus(
                            statusKey.id,
                        )
                    listOf(current)
                } else {
                    val context =
                        service.context(
                            statusKey.id,
                        )
                    val current =
                        service.lookupStatus(
                            statusKey.id,
                        )
                    context.ancestors.orEmpty() + listOf(current) + context.descendants.orEmpty()
                }

            LoadResult.Page(
                data = result.map { it.renderGuest(host = host) },
                prevKey = null,
                nextKey = null,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
}
