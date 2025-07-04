package dev.dimension.flare.data.datasource.misskey

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import dev.dimension.flare.common.BaseRemoteMediator
import dev.dimension.flare.data.database.cache.CacheDatabase
import dev.dimension.flare.data.database.cache.connect
import dev.dimension.flare.data.database.cache.mapper.Misskey
import dev.dimension.flare.data.database.cache.model.DbPagingTimeline
import dev.dimension.flare.data.database.cache.model.DbPagingTimelineWithStatus
import dev.dimension.flare.data.network.misskey.MisskeyService
import dev.dimension.flare.data.network.misskey.api.model.IPinRequest
import dev.dimension.flare.data.network.misskey.api.model.NotesChildrenRequest
import dev.dimension.flare.model.AccountType
import dev.dimension.flare.model.MicroBlogKey
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.Uuid

@OptIn(ExperimentalPagingApi::class)
internal class StatusDetailRemoteMediator(
    private val statusKey: MicroBlogKey,
    private val database: CacheDatabase,
    private val accountKey: MicroBlogKey,
    private val service: MisskeyService,
    private val pagingKey: String,
    private val statusOnly: Boolean,
) : BaseRemoteMediator<Int, DbPagingTimelineWithStatus>() {
    private var page = 1

    override suspend fun doLoad(
        loadType: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>,
    ): MediatorResult {
        if (loadType == LoadType.PREPEND) {
            return MediatorResult.Success(
                endOfPaginationReached = true,
            )
        }
        if (!database.pagingTimelineDao().existsPaging(accountKey, pagingKey)) {
            val status = database.statusDao().get(statusKey, AccountType.Specific(accountKey)).firstOrNull()
            status?.let {
                database.connect {
                    database
                        .pagingTimelineDao()
                        .insertAll(
                            listOf(
                                DbPagingTimeline(
                                    accountType = AccountType.Specific(accountKey),
                                    statusKey = statusKey,
                                    pagingKey = pagingKey,
                                    sortId = 0,
                                    _id = Uuid.random().toString(),
                                ),
                            ),
                        )
                }
            }
        }
        val result =
            if (statusOnly) {
                val current =
                    service
                        .notesShow(
                            IPinRequest(noteId = statusKey.id),
                        )
                listOf(current)
            } else {
                val current =
                    if (loadType == LoadType.REFRESH) {
                        page = 0
                        service
                            .notesShow(
                                IPinRequest(noteId = statusKey.id),
                            )
                    } else {
                        page++
                        null
                    }
                val lastItem =
                    database.pagingTimelineDao().getLastPagingTimeline(pagingKey)?.takeIf {
                        it.timeline.statusKey != statusKey
                    }
                val children =
                    service
                        .notesChildren(
                            NotesChildrenRequest(
                                noteId = statusKey.id,
                                untilId = lastItem?.timeline?.statusKey?.id,
                                limit = state.config.pageSize,
                            ),
                        ).orEmpty()
                listOfNotNull(current?.reply, current) + children
            }.filterNotNull()
        database.connect {
            Misskey.save(
                database = database,
                accountKey = accountKey,
                pagingKey = pagingKey,
                data = result,
                sortIdProvider = {
                    val index = result.indexOf(it)
                    -(index + page * state.config.pageSize).toLong()
                },
            )
        }
        return MediatorResult.Success(
            endOfPaginationReached = true,
        )
    }
}
