package dev.dimension.flare.data.datasource.mastodon

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import dev.dimension.flare.common.BaseRemoteMediator
import dev.dimension.flare.data.database.cache.CacheDatabase
import dev.dimension.flare.data.database.cache.connect
import dev.dimension.flare.data.database.cache.mapper.Mastodon
import dev.dimension.flare.data.database.cache.model.DbPagingTimelineWithStatus
import dev.dimension.flare.data.network.mastodon.MastodonService
import dev.dimension.flare.data.network.mastodon.api.model.NotificationTypes
import dev.dimension.flare.model.MicroBlogKey

@OptIn(ExperimentalPagingApi::class)
internal class MentionRemoteMediator(
    private val service: MastodonService,
    private val database: CacheDatabase,
    private val accountKey: MicroBlogKey,
    private val pagingKey: String,
) : BaseRemoteMediator<Int, DbPagingTimelineWithStatus>() {
    override suspend fun doLoad(
        loadType: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>,
    ): MediatorResult {
        val response =
            when (loadType) {
                LoadType.REFRESH -> {
                    service
                        .notification(
                            limit = state.config.pageSize,
                            exclude_types = NotificationTypes.entries.filter { it != NotificationTypes.Mention },
                        )
                }
                LoadType.PREPEND -> {
                    val firstItem = state.firstItemOrNull()
                    service.notification(
                        limit = state.config.pageSize,
                        min_id = firstItem?.timeline?.statusKey?.id,
                        exclude_types = NotificationTypes.entries.filter { it != NotificationTypes.Mention },
                    )
                }

                LoadType.APPEND -> {
                    val lastItem =
                        database.pagingTimelineDao().getLastPagingTimeline(pagingKey)
                            ?: return MediatorResult.Success(
                                endOfPaginationReached = true,
                            )
                    service.notification(
                        limit = state.config.pageSize,
                        max_id = lastItem.timeline.statusKey.id,
                        exclude_types = NotificationTypes.entries.filter { it != NotificationTypes.Mention },
                    )
                }
            }

        database.connect {
            if (loadType == LoadType.REFRESH) {
                database.pagingTimelineDao().delete(pagingKey = pagingKey, accountKey = accountKey)
            }
            Mastodon.save(
                database = database,
                accountKey = accountKey,
                pagingKey = pagingKey,
                data = response,
            )
        }

        return MediatorResult.Success(
            endOfPaginationReached = response.isEmpty(),
        )
    }
}
