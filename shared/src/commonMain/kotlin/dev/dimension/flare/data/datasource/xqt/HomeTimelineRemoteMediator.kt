package dev.dimension.flare.data.datasource.xqt

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import dev.dimension.flare.common.BaseRemoteMediator
import dev.dimension.flare.common.encodeJson
import dev.dimension.flare.data.database.cache.CacheDatabase
import dev.dimension.flare.data.database.cache.connect
import dev.dimension.flare.data.database.cache.mapper.XQT
import dev.dimension.flare.data.database.cache.mapper.cursor
import dev.dimension.flare.data.database.cache.mapper.tweets
import dev.dimension.flare.data.database.cache.model.DbPagingTimelineWithStatus
import dev.dimension.flare.data.network.xqt.XQTService
import dev.dimension.flare.model.MicroBlogKey
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@OptIn(ExperimentalPagingApi::class)
internal class HomeTimelineRemoteMediator(
    private val service: XQTService,
    private val database: CacheDatabase,
    private val accountKey: MicroBlogKey,
    private val pagingKey: String,
) : BaseRemoteMediator<Int, DbPagingTimelineWithStatus>() {
    private var cursor: String? = null

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun doLoad(
        loadType: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>,
    ): MediatorResult {
        val response =
            when (loadType) {
                LoadType.REFRESH -> {
                    cursor = null
                    service
                        .getHomeLatestTimeline(
                            variables =
                                HomeTimelineRequest(
                                    count = state.config.pageSize.toLong(),
                                ).encodeJson(),
                        )
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(
                        endOfPaginationReached = true,
                    )
                }

                LoadType.APPEND -> {
                    service.getHomeLatestTimeline(
                        variables =
                            HomeTimelineRequest(
                                count = state.config.pageSize.toLong(),
                                cursor = cursor,
                            ).encodeJson(),
                    )
                }
            }.body()
        val instructions =
            response
                ?.data
                ?.home
                ?.homeTimelineUrt
                ?.instructions
                .orEmpty()
        cursor = instructions.cursor()
        val tweet = instructions.tweets()

        database.connect {
            if (loadType == LoadType.REFRESH) {
                database.pagingTimelineDao().delete(pagingKey = pagingKey, accountKey = accountKey)
            }
            XQT.save(
                accountKey = accountKey,
                pagingKey = pagingKey,
                database = database,
                tweet = tweet,
            )
        }
        return MediatorResult.Success(
            endOfPaginationReached = tweet.isEmpty(),
        )
    }
}

@OptIn(ExperimentalPagingApi::class)
internal class FeaturedTimelineRemoteMediator(
    private val service: XQTService,
    private val database: CacheDatabase,
    private val accountKey: MicroBlogKey,
    private val pagingKey: String,
) : BaseRemoteMediator<Int, DbPagingTimelineWithStatus>() {
    private var cursor: String? = null

    override suspend fun doLoad(
        loadType: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>,
    ): MediatorResult {
        val response =
            when (loadType) {
                LoadType.REFRESH -> {
                    cursor = null
                    service
                        .getHomeTimeline(
                            variables =
                                HomeTimelineRequest(
                                    count = state.config.pageSize.toLong(),
                                ).encodeJson(),
                        )
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(
                        endOfPaginationReached = true,
                    )
                }

                LoadType.APPEND -> {
                    service.getHomeTimeline(
                        variables =
                            HomeTimelineRequest(
                                count = state.config.pageSize.toLong(),
                                cursor = cursor,
                            ).encodeJson(),
                    )
                }
            }.body()
        val instructions =
            response
                ?.data
                ?.home
                ?.homeTimelineUrt
                ?.instructions
                .orEmpty()
        cursor = instructions.cursor()
        val tweet = instructions.tweets()
        database.connect {
            if (loadType == LoadType.REFRESH) {
                database.pagingTimelineDao().delete(pagingKey = pagingKey, accountKey = accountKey)
            }
            XQT.save(
                accountKey = accountKey,
                pagingKey = pagingKey,
                database = database,
                tweet = tweet,
            )
        }
        return MediatorResult.Success(
            endOfPaginationReached = tweet.isEmpty(),
        )
    }
}

@OptIn(ExperimentalPagingApi::class)
internal class BookmarkTimelineRemoteMediator(
    private val service: XQTService,
    private val database: CacheDatabase,
    private val accountKey: MicroBlogKey,
    private val pagingKey: String,
) : BaseRemoteMediator<Int, DbPagingTimelineWithStatus>() {
    private var cursor: String? = null

    override suspend fun doLoad(
        loadType: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>,
    ): MediatorResult {
        val response =
            when (loadType) {
                LoadType.REFRESH -> {
                    cursor = null
                    service
                        .getBookmarks(
                            variables =
                                HomeTimelineRequest(
                                    count = state.config.pageSize.toLong(),
                                ).encodeJson(),
                        )
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(
                        endOfPaginationReached = true,
                    )
                }

                LoadType.APPEND -> {
                    service.getBookmarks(
                        variables =
                            HomeTimelineRequest(
                                count = state.config.pageSize.toLong(),
                                cursor = cursor,
                            ).encodeJson(),
                    )
                }
            }.body()
        val instructions =
            response
                ?.data
                ?.bookmarkTimelineV2
                ?.timeline
                ?.instructions
                .orEmpty()
        cursor = instructions.cursor()
        val tweet = instructions.tweets()
        database.connect {
            if (loadType == LoadType.REFRESH) {
                database.pagingTimelineDao().delete(pagingKey = pagingKey, accountKey = accountKey)
            }
            XQT.save(
                accountKey = accountKey,
                pagingKey = pagingKey,
                database = database,
                tweet = tweet,
            )
        }
        return MediatorResult.Success(
            endOfPaginationReached = tweet.isEmpty(),
        )
    }
}

@Serializable
internal data class HomeTimelineRequest(
    @Required
    val count: Long = 20,
    val cursor: String? = null,
    @Required
    val includePromotedContent: Boolean = false,
    @Required
    val latestControlAvailable: Boolean = false,
)
