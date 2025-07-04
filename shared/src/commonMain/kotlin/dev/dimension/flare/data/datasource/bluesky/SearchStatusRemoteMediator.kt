package dev.dimension.flare.data.datasource.bluesky

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import app.bsky.feed.SearchPostsQueryParams
import dev.dimension.flare.common.BaseRemoteMediator
import dev.dimension.flare.data.database.cache.CacheDatabase
import dev.dimension.flare.data.database.cache.connect
import dev.dimension.flare.data.database.cache.mapper.Bluesky
import dev.dimension.flare.data.database.cache.model.DbPagingTimelineWithStatus
import dev.dimension.flare.data.network.bluesky.BlueskyService
import dev.dimension.flare.model.MicroBlogKey

@OptIn(ExperimentalPagingApi::class)
internal class SearchStatusRemoteMediator(
    private val service: BlueskyService,
    private val database: CacheDatabase,
    private val accountKey: MicroBlogKey,
    private val pagingKey: String,
    private val query: String,
) : BaseRemoteMediator<Int, DbPagingTimelineWithStatus>() {
    var cursor: String? = null

    override suspend fun doLoad(
        loadType: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>,
    ): MediatorResult {
        val response =
            when (loadType) {
                LoadType.PREPEND -> {
                    return MediatorResult.Success(
                        endOfPaginationReached = true,
                    )
                }
                LoadType.REFRESH -> {
                    service.searchPosts(
                        SearchPostsQueryParams(
                            q = query,
                            limit = state.config.pageSize.toLong(),
                        ),
                    )
                }

                LoadType.APPEND -> {
                    service.searchPosts(
                        SearchPostsQueryParams(
                            q = query,
                            limit = state.config.pageSize.toLong(),
                            cursor = cursor,
                        ),
                    )
                }
            }.requireResponse()
        cursor = response.cursor

        database.connect {
            Bluesky.savePost(
                accountKey,
                pagingKey,
                database,
                response.posts,
            )
        }

        return MediatorResult.Success(
            endOfPaginationReached = cursor == null,
        )
    }
}
