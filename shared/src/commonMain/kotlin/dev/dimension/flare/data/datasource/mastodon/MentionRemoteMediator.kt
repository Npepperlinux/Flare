package dev.dimension.flare.data.datasource.mastodon

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dev.dimension.flare.data.database.cache.CacheDatabase
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
) : RemoteMediator<Int, DbPagingTimelineWithStatus>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>,
    ): MediatorResult {
        return try {
            val response =
                when (loadType) {
                    LoadType.REFRESH -> {
                        service
                            .notification(
                                limit = state.config.pageSize,
                                exclude_types = NotificationTypes.entries.filter { it != NotificationTypes.Mention },
                            ).also {
                                database.pagingTimelineDao().delete(pagingKey = pagingKey, accountKey = accountKey)
                            }
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

            Mastodon.save(
                database = database,
                accountKey = accountKey,
                pagingKey = pagingKey,
                data = response,
            )

            MediatorResult.Success(
                endOfPaginationReached = response.isEmpty(),
            )
        } catch (e: Throwable) {
            MediatorResult.Error(e)
        }
    }
}
