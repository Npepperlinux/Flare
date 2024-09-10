package dev.dimension.flare.data.datasource.bluesky

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.cachedIn
import androidx.paging.map
import app.bsky.actor.GetProfileQueryParams
import app.bsky.actor.PreferencesUnion
import app.bsky.actor.PutPreferencesRequest
import app.bsky.actor.SavedFeed
import app.bsky.actor.Type
import app.bsky.embed.Images
import app.bsky.embed.ImagesImage
import app.bsky.embed.Record
import app.bsky.feed.GetFeedGeneratorQueryParams
import app.bsky.feed.GetFeedGeneratorsQueryParams
import app.bsky.feed.GetPostsQueryParams
import app.bsky.feed.Post
import app.bsky.feed.PostEmbedUnion
import app.bsky.feed.PostReplyRef
import app.bsky.feed.ViewerState
import app.bsky.graph.GetListQueryParams
import app.bsky.graph.GetListsQueryParams
import app.bsky.graph.MuteActorRequest
import app.bsky.graph.UnmuteActorRequest
import app.bsky.unspecced.GetPopularFeedGeneratorsQueryParams
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneNotNull
import com.atproto.moderation.CreateReportRequest
import com.atproto.moderation.CreateReportRequestSubjectUnion
import com.atproto.moderation.Token
import com.atproto.repo.ApplyWritesDelete
import com.atproto.repo.ApplyWritesRequest
import com.atproto.repo.ApplyWritesRequestWriteUnion
import com.atproto.repo.CreateRecordRequest
import com.atproto.repo.CreateRecordResponse
import com.atproto.repo.DeleteRecordRequest
import com.atproto.repo.ListRecordsQueryParams
import com.atproto.repo.ListRecordsRecord
import com.atproto.repo.PutRecordRequest
import com.atproto.repo.StrongRef
import com.benasher44.uuid.uuid4
import dev.dimension.flare.common.CacheData
import dev.dimension.flare.common.Cacheable
import dev.dimension.flare.common.FileItem
import dev.dimension.flare.common.MemCacheable
import dev.dimension.flare.data.database.app.AppDatabase
import dev.dimension.flare.data.database.cache.CacheDatabase
import dev.dimension.flare.data.database.cache.mapper.Bluesky
import dev.dimension.flare.data.database.cache.mapper.toDbUser
import dev.dimension.flare.data.database.cache.model.StatusContent
import dev.dimension.flare.data.database.cache.model.updateStatusUseCase
import dev.dimension.flare.data.datasource.microblog.BlueskyComposeData
import dev.dimension.flare.data.datasource.microblog.ComposeConfig
import dev.dimension.flare.data.datasource.microblog.ComposeData
import dev.dimension.flare.data.datasource.microblog.ComposeProgress
import dev.dimension.flare.data.datasource.microblog.MemoryPagingSource
import dev.dimension.flare.data.datasource.microblog.MicroblogDataSource
import dev.dimension.flare.data.datasource.microblog.NotificationFilter
import dev.dimension.flare.data.datasource.microblog.ProfileAction
import dev.dimension.flare.data.datasource.microblog.StatusEvent
import dev.dimension.flare.data.datasource.microblog.memoryPager
import dev.dimension.flare.data.datasource.microblog.relationKeyWithUserKey
import dev.dimension.flare.data.datasource.microblog.timelinePager
import dev.dimension.flare.data.network.bluesky.getService
import dev.dimension.flare.data.repository.LocalFilterRepository
import dev.dimension.flare.model.MicroBlogKey
import dev.dimension.flare.model.PlatformType
import dev.dimension.flare.ui.model.UiAccount
import dev.dimension.flare.ui.model.UiHashtag
import dev.dimension.flare.ui.model.UiList
import dev.dimension.flare.ui.model.UiProfile
import dev.dimension.flare.ui.model.UiRelation
import dev.dimension.flare.ui.model.UiState
import dev.dimension.flare.ui.model.UiTimeline
import dev.dimension.flare.ui.model.UiUserV2
import dev.dimension.flare.ui.model.mapper.bskyJson
import dev.dimension.flare.ui.model.mapper.render
import dev.dimension.flare.ui.model.toUi
import dev.dimension.flare.ui.presenter.status.action.BlueskyReportStatusState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Handle
import sh.christian.ozone.api.Nsid
import sh.christian.ozone.api.response.AtpResponse

@OptIn(ExperimentalPagingApi::class)
class BlueskyDataSource(
    override val account: UiAccount.Bluesky,
) : MicroblogDataSource,
    KoinComponent,
    StatusEvent.Bluesky {
    private val database: CacheDatabase by inject()
    private val appDatabase: AppDatabase by inject()
    private val localFilterRepository: LocalFilterRepository by inject()
    private val coroutineScope: CoroutineScope by inject()

    override fun homeTimeline(
        pageSize: Int,
        pagingKey: String,
        scope: CoroutineScope,
    ): Flow<PagingData<UiTimeline>> =
        timelinePager(
            pageSize = pageSize,
            pagingKey = pagingKey,
            accountKey = account.accountKey,
            database = database,
            filterFlow = localFilterRepository.getFlow(forTimeline = true),
            scope = scope,
            mediator =
                HomeTimelineRemoteMediator(
                    account.getService(appDatabase),
                    account.accountKey,
                    database,
                    pagingKey,
                ),
        )

    override fun notification(
        type: NotificationFilter,
        pageSize: Int,
        pagingKey: String,
        scope: CoroutineScope,
    ): Flow<PagingData<UiTimeline>> =
        timelinePager(
            pageSize = pageSize,
            pagingKey = pagingKey,
            accountKey = account.accountKey,
            database = database,
            filterFlow = localFilterRepository.getFlow(forNotification = true),
            scope = scope,
            mediator =
                when (type) {
                    NotificationFilter.All ->
                        NotificationRemoteMediator(
                            account.getService(appDatabase),
                            account.accountKey,
                            database,
                            pagingKey,
                        )

                    else -> throw IllegalArgumentException("Unsupported notification filter")
                },
        )

    override val supportedNotificationFilter: List<NotificationFilter>
        get() = listOf(NotificationFilter.All)

    override fun userByAcct(acct: String): CacheData<UiUserV2> {
        val (name, host) = MicroBlogKey.valueOf(acct)
        return Cacheable(
            fetchSource = {
                val user =
                    account
                        .getService(appDatabase)
                        .getProfile(GetProfileQueryParams(actor = Handle(handle = name)))
                        .requireResponse()
                        .toDbUser(account.accountKey.host)
                database.dbUserQueries.insert(
                    user_key = user.user_key,
                    platform_type = user.platform_type,
                    name = user.name,
                    handle = user.handle,
                    content = user.content,
                    host = user.host,
                )
            },
            cacheSource = {
                database.dbUserQueries
                    .findByHandleAndHost(name, host, PlatformType.Bluesky)
                    .asFlow()
                    .mapToOneNotNull(Dispatchers.IO)
                    .mapNotNull { it.render(account.accountKey) }
            },
        )
    }

    override fun userById(id: String): CacheData<UiProfile> =
        Cacheable(
            fetchSource = {
                val user =
                    account
                        .getService(appDatabase)
                        .getProfile(GetProfileQueryParams(actor = Did(did = id)))
                        .requireResponse()
                        .toDbUser(account.accountKey.host)
                database.dbUserQueries.insert(
                    user_key = user.user_key,
                    platform_type = user.platform_type,
                    name = user.name,
                    handle = user.handle,
                    content = user.content,
                    host = user.host,
                )
            },
            cacheSource = {
                database.dbUserQueries
                    .findByKey(MicroBlogKey(id, account.accountKey.host))
                    .asFlow()
                    .mapToOneNotNull(Dispatchers.IO)
                    .mapNotNull { it.render(account.accountKey) }
            },
        )

    override fun relation(userKey: MicroBlogKey): Flow<UiState<UiRelation>> =
        MemCacheable(
            relationKeyWithUserKey(userKey),
        ) {
            val user =
                account
                    .getService(appDatabase)
                    .getProfile(GetProfileQueryParams(actor = Did(did = userKey.id)))
                    .requireResponse()
            UiRelation(
                following = user.viewer?.following?.atUri != null,
                isFans = user.viewer?.followedBy?.atUri != null,
                blocking = user.viewer?.blockedBy ?: false,
                muted = user.viewer?.muted ?: false,
            )
        }.toUi()

    override fun userTimeline(
        userKey: MicroBlogKey,
        scope: CoroutineScope,
        pageSize: Int,
        mediaOnly: Boolean,
        pagingKey: String,
    ): Flow<PagingData<UiTimeline>> =
        timelinePager(
            pageSize = pageSize,
            pagingKey = pagingKey,
            accountKey = account.accountKey,
            database = database,
            filterFlow = localFilterRepository.getFlow(forTimeline = true),
            scope = scope,
            mediator =
                UserTimelineRemoteMediator(
                    account.getService(appDatabase),
                    account.accountKey,
                    database,
                    userKey,
                    pagingKey,
                    onlyMedia = mediaOnly,
                ),
        )

    override fun context(
        statusKey: MicroBlogKey,
        scope: CoroutineScope,
        pageSize: Int,
        pagingKey: String,
    ): Flow<PagingData<UiTimeline>> =
        timelinePager(
            pageSize = pageSize,
            pagingKey = pagingKey,
            accountKey = account.accountKey,
            database = database,
            filterFlow = localFilterRepository.getFlow(forTimeline = true),
            scope = scope,
            mediator =
                StatusDetailRemoteMediator(
                    statusKey,
                    account.getService(appDatabase),
                    account.accountKey,
                    database,
                    pagingKey,
                    statusOnly = false,
                ),
        )

    override fun status(statusKey: MicroBlogKey): CacheData<UiTimeline> {
        val pagingKey = "status_only_$statusKey"
        val service = account.getService(appDatabase)
        return Cacheable(
            fetchSource = {
                val result =
                    service
                        .getPosts(
                            GetPostsQueryParams(
                                persistentListOf(AtUri(statusKey.id)),
                            ),
                        ).requireResponse()
                        .posts
                        .firstOrNull()
                        .let {
                            listOfNotNull(it)
                        }
                Bluesky.savePost(
                    account.accountKey,
                    pagingKey,
                    database,
                    result,
                )
            },
            cacheSource = {
                database.dbStatusQueries
                    .get(statusKey, account.accountKey)
                    .asFlow()
                    .mapToOneNotNull(Dispatchers.IO)
                    .mapNotNull { it.content.render(account.accountKey, this) }
            },
        )
    }

    override suspend fun compose(
        data: ComposeData,
        progress: (ComposeProgress) -> Unit,
    ) {
        require(data is BlueskyComposeData)
        val maxProgress = data.medias.size + 1
        val service = data.account.getService(appDatabase)
        val mediaBlob =
            data.medias
                .mapIndexedNotNull { index, item ->
                    service
                        .uploadBlob(item.readBytes())
                        .also {
                            progress(ComposeProgress(index + 1, maxProgress))
                        }.maybeResponse()
                }.map {
                    it.blob
                }
        val post =
            Post(
                text = data.content,
                createdAt = Clock.System.now(),
                embed =
                    data.quoteId
                        ?.let { quoteId ->
                            service
                                .getPosts(GetPostsQueryParams(persistentListOf(AtUri(quoteId))))
                                .maybeResponse()
                                ?.posts
                                ?.firstOrNull()
                        }?.let { item ->
                            PostEmbedUnion.Record(
                                Record(
                                    StrongRef(
                                        uri = item.uri,
                                        cid = item.cid,
                                    ),
                                ),
                            )
                        } ?: mediaBlob.takeIf { it.any() }?.let { blobs ->
                        PostEmbedUnion.Images(
                            Images(
                                blobs
                                    .map { blob ->
                                        ImagesImage(image = blob, alt = "")
                                    }.toImmutableList(),
                            ),
                        )
                    },
                reply =
                    data.inReplyToID
                        ?.let { inReplyToID ->
                            service
                                .getPosts(GetPostsQueryParams(persistentListOf(AtUri(inReplyToID))))
                                .maybeResponse()
                                ?.posts
                                ?.firstOrNull()
                        }?.let { item ->
                            val post: Post = item.record.bskyJson()
                            val root =
                                post.reply?.root?.let { root ->
                                    StrongRef(
                                        uri = root.uri,
                                        cid = root.cid,
                                    )
                                } ?: StrongRef(
                                    uri = item.uri,
                                    cid = item.cid,
                                )
                            PostReplyRef(
                                parent =
                                    StrongRef(
                                        uri = item.uri,
                                        cid = item.cid,
                                    ),
                                root = root,
                            )
                        },
            )
        service.createRecord(
            CreateRecordRequest(
                repo = Did(did = data.account.accountKey.id),
                collection = Nsid("app.bsky.feed.post"),
                record = post.bskyJson(),
            ),
        )
    }

    suspend fun report(
        statusKey: MicroBlogKey,
        reason: BlueskyReportStatusState.ReportReason,
    ) {
        runCatching {
            val service = account.getService(appDatabase)
            val post =
                service
                    .getPosts(GetPostsQueryParams(persistentListOf(AtUri(statusKey.id))))
                    .maybeResponse()
                    ?.posts
                    ?.firstOrNull()
            if (post != null) {
                service.createReport(
                    CreateReportRequest(
                        reasonType =
                            when (reason) {
                                BlueskyReportStatusState.ReportReason.Spam -> Token.REASON_SPAM
                                BlueskyReportStatusState.ReportReason.Violation -> Token.REASON_VIOLATION
                                BlueskyReportStatusState.ReportReason.Misleading -> Token.REASON_MISLEADING
                                BlueskyReportStatusState.ReportReason.Sexual -> Token.REASON_SEXUAL
                                BlueskyReportStatusState.ReportReason.Rude -> Token.REASON_RUDE
                                BlueskyReportStatusState.ReportReason.Other -> Token.REASON_OTHER
                            },
                        subject =
                            CreateReportRequestSubjectUnion.RepoStrongRef(
                                value =
                                    StrongRef(
                                        uri = post.uri,
                                        cid = post.cid,
                                    ),
                            ),
                    ),
                )
            }
        }
    }

    override fun reblog(
        statusKey: MicroBlogKey,
        cid: String,
        uri: String,
        repostUri: String?,
    ) {
        coroutineScope.launch {
            updateStatusUseCase<StatusContent.Bluesky>(
                statusKey = statusKey,
                accountKey = account.accountKey,
                cacheDatabase = database,
            ) { content ->
                val newUri =
                    if (repostUri != null) {
                        null
                    } else {
                        AtUri("")
                    }
                val count =
                    if (repostUri != null) {
                        (content.data.repostCount ?: 0) - 1
                    } else {
                        (content.data.repostCount ?: 0) + 1
                    }.coerceAtLeast(0)
                content.copy(
                    data =
                        content.data.copy(
                            viewer =
                                content.data.viewer?.copy(
                                    repost = newUri,
                                ) ?: ViewerState(
                                    repost = newUri,
                                ),
                            repostCount = count,
                        ),
                )
            }
            runCatching {
                val service = account.getService(appDatabase)
                if (repostUri != null) {
                    service.deleteRecord(
                        DeleteRecordRequest(
                            repo = Did(did = account.accountKey.id),
                            collection = Nsid("app.bsky.feed.repost"),
                            rkey = repostUri.substringAfterLast('/'),
                        ),
                    )
                } else {
                    val result =
                        service
                            .createRecord(
                                CreateRecordRequest(
                                    repo = Did(did = account.accountKey.id),
                                    collection = Nsid("app.bsky.feed.repost"),
                                    record =
                                        app.bsky.feed
                                            .Repost(
                                                subject =
                                                    StrongRef(
                                                        uri = AtUri(uri),
                                                        cid = Cid(cid),
                                                    ),
                                                createdAt = Clock.System.now(),
                                            ).bskyJson(),
                                ),
                            ).requireResponse()
                    updateStatusUseCase<StatusContent.Bluesky>(
                        statusKey = statusKey,
                        accountKey = account.accountKey,
                        cacheDatabase = database,
                    ) { content ->
                        content.copy(
                            data =
                                content.data.copy(
                                    viewer =
                                        content.data.viewer?.copy(
                                            repost = AtUri(result.uri.atUri),
                                        ) ?: ViewerState(
                                            repost = AtUri(result.uri.atUri),
                                        ),
                                ),
                        )
                    }
                }
            }.onFailure {
                updateStatusUseCase<StatusContent.Bluesky>(
                    statusKey = statusKey,
                    accountKey = account.accountKey,
                    cacheDatabase = database,
                ) { content ->
                    val count =
                        if (repostUri != null) {
                            (content.data.repostCount ?: 0) + 1
                        } else {
                            (content.data.repostCount ?: 0) - 1
                        }.coerceAtLeast(0)
                    content.copy(
                        data =
                            content.data.copy(
                                viewer =
                                    content.data.viewer?.copy(
                                        repost = repostUri?.let { it1 -> AtUri(it1) },
                                    ) ?: ViewerState(
                                        repost = repostUri?.let { it1 -> AtUri(it1) },
                                    ),
                                repostCount = count,
                            ),
                    )
                }
            }
        }
    }

    override fun like(
        statusKey: MicroBlogKey,
        cid: String,
        uri: String,
        likedUri: String?,
    ) {
        coroutineScope.launch {
            updateStatusUseCase<StatusContent.Bluesky>(
                statusKey = statusKey,
                accountKey = account.accountKey,
                cacheDatabase = database,
            ) { content ->
                val newUri =
                    if (likedUri != null) {
                        null
                    } else {
                        AtUri("")
                    }
                val count =
                    if (likedUri != null) {
                        (content.data.likeCount ?: 0) - 1
                    } else {
                        (content.data.likeCount ?: 0) + 1
                    }.coerceAtLeast(0)
                content.copy(
                    data =
                        content.data.copy(
                            viewer =
                                content.data.viewer?.copy(
                                    like = newUri,
                                ) ?: ViewerState(
                                    like = newUri,
                                ),
                            likeCount = count,
                        ),
                )
            }
            runCatching {
                if (likedUri != null) {
                    deleteLikeRecord(likedUri)
                } else {
                    val result =
                        createLikeRecord(cid, uri)
                    updateStatusUseCase<StatusContent.Bluesky>(
                        statusKey = statusKey,
                        accountKey = account.accountKey,
                        cacheDatabase = database,
                    ) { content ->
                        content.copy(
                            data =
                                content.data.copy(
                                    viewer =
                                        content.data.viewer?.copy(
                                            like = AtUri(result.uri.atUri),
                                        ) ?: ViewerState(
                                            like = AtUri(result.uri.atUri),
                                        ),
                                ),
                        )
                    }
                }
            }.onFailure {
                updateStatusUseCase<StatusContent.Bluesky>(
                    statusKey = statusKey,
                    accountKey = account.accountKey,
                    cacheDatabase = database,
                ) { content ->
                    val count =
                        if (likedUri != null) {
                            (content.data.likeCount ?: 0) + 1
                        } else {
                            (content.data.likeCount ?: 0) - 1
                        }.coerceAtLeast(0)
                    content.copy(
                        data =
                            content.data.copy(
                                viewer =
                                    content.data.viewer?.copy(
                                        like = likedUri?.let { it1 -> AtUri(it1) },
                                    ) ?: ViewerState(
                                        like = likedUri?.let { it1 -> AtUri(it1) },
                                    ),
                                likeCount = count,
                            ),
                    )
                }
            }
        }
    }

    private suspend fun createLikeRecord(
        cid: String,
        uri: String,
    ): CreateRecordResponse {
        val service = account.getService(appDatabase)
        val result =
            service
                .createRecord(
                    CreateRecordRequest(
                        repo = Did(did = account.accountKey.id),
                        collection = Nsid("app.bsky.feed.like"),
                        record =
                            app.bsky.feed
                                .Like(
                                    subject =
                                        StrongRef(
                                            uri = AtUri(uri),
                                            cid = Cid(cid),
                                        ),
                                    createdAt = Clock.System.now(),
                                ).bskyJson(),
                    ),
                ).requireResponse()
        return result
    }

    private suspend fun deleteLikeRecord(likedUri: String): AtpResponse<Unit> {
        val service = account.getService(appDatabase)
        return service.deleteRecord(
            DeleteRecordRequest(
                repo = Did(did = account.accountKey.id),
                collection = Nsid("app.bsky.feed.like"),
                rkey = likedUri.substringAfterLast('/'),
            ),
        )
    }

    override suspend fun deleteStatus(statusKey: MicroBlogKey) {
        runCatching {
            val service = account.getService(appDatabase)
            service.deleteRecord(
                DeleteRecordRequest(
                    repo = Did(did = account.accountKey.id),
                    collection = Nsid("app.bsky.feed.post"),
                    rkey = statusKey.id.substringAfterLast('/'),
                ),
            )
            // delete status from cache
            database.dbStatusQueries.delete(
                status_key = statusKey,
                account_key = account.accountKey,
            )
            database.dbPagingTimelineQueries.deleteStatus(
                account_key = account.accountKey,
                status_key = statusKey,
            )
        }
    }

    suspend fun unfollow(userKey: MicroBlogKey) {
        val key = relationKeyWithUserKey(userKey)
        MemCacheable.updateWith<UiRelation>(
            key = key,
        ) {
            it.copy(
                following = false,
            )
        }
        runCatching {
            val service = account.getService(appDatabase)
            val user =
                service
                    .getProfile(GetProfileQueryParams(actor = Did(did = userKey.id)))
                    .requireResponse()

            val followRepo = user.viewer?.following?.atUri
            if (followRepo != null) {
                service.deleteRecord(
                    DeleteRecordRequest(
                        repo = Did(did = account.accountKey.id),
                        collection = Nsid("app.bsky.graph.follow"),
                        rkey = followRepo.substringAfterLast('/'),
                    ),
                )
            }
        }.onFailure {
            MemCacheable.updateWith<UiRelation>(
                key = key,
            ) {
                it.copy(
                    following = true,
                )
            }
        }
    }

    suspend fun follow(userKey: MicroBlogKey) {
        val key = relationKeyWithUserKey(userKey)
        MemCacheable.updateWith<UiRelation>(
            key = key,
        ) {
            it.copy(
                following = true,
            )
        }
        runCatching {
            val service = account.getService(appDatabase)
            service.createRecord(
                CreateRecordRequest(
                    repo = Did(did = account.accountKey.id),
                    collection = Nsid("app.bsky.graph.follow"),
                    record =
                        app.bsky.graph
                            .Follow(
                                subject = Did(userKey.id),
                                createdAt = Clock.System.now(),
                            ).bskyJson(),
                ),
            )
        }.onFailure {
            MemCacheable.updateWith<UiRelation>(
                key = key,
            ) {
                it.copy(
                    following = false,
                )
            }
        }
    }

    suspend fun block(userKey: MicroBlogKey) {
        val key = relationKeyWithUserKey(userKey)
        MemCacheable.updateWith<UiRelation>(
            key = key,
        ) {
            it.copy(
                blocking = true,
            )
        }
        runCatching {
            val service = account.getService(appDatabase)
            service.createRecord(
                CreateRecordRequest(
                    repo = Did(did = account.accountKey.id),
                    collection = Nsid("app.bsky.graph.block"),
                    record =
                        app.bsky.graph
                            .Block(
                                subject = Did(userKey.id),
                                createdAt = Clock.System.now(),
                            ).bskyJson(),
                ),
            )
        }.onFailure {
            MemCacheable.updateWith<UiRelation>(
                key = key,
            ) {
                it.copy(
                    blocking = false,
                )
            }
        }
    }

    suspend fun unblock(userKey: MicroBlogKey) {
        val key = relationKeyWithUserKey(userKey)
        MemCacheable.updateWith<UiRelation>(
            key = key,
        ) {
            it.copy(
                blocking = false,
            )
        }
        runCatching {
            val service = account.getService(appDatabase)
            val user =
                service
                    .getProfile(GetProfileQueryParams(actor = Did(did = userKey.id)))
                    .requireResponse()

            val blockRepo = user.viewer?.blocking?.atUri
            if (blockRepo != null) {
                service.deleteRecord(
                    DeleteRecordRequest(
                        repo = Did(did = account.accountKey.id),
                        collection = Nsid("app.bsky.graph.block"),
                        rkey = blockRepo.substringAfterLast('/'),
                    ),
                )
            }
        }.onFailure {
            MemCacheable.updateWith<UiRelation>(
                key = key,
            ) {
                it.copy(
                    blocking = true,
                )
            }
        }
    }

    suspend fun mute(userKey: MicroBlogKey) {
        val key = relationKeyWithUserKey(userKey)
        MemCacheable.updateWith<UiRelation>(
            key = key,
        ) {
            it.copy(
                muted = true,
            )
        }
        runCatching {
            val service = account.getService(appDatabase)
            service.muteActor(MuteActorRequest(actor = Did(did = userKey.id)))
        }.onFailure {
            MemCacheable.updateWith<UiRelation>(
                key = key,
            ) {
                it.copy(
                    muted = false,
                )
            }
        }
    }

    suspend fun unmute(userKey: MicroBlogKey) {
        val key = relationKeyWithUserKey(userKey)
        MemCacheable.updateWith<UiRelation>(
            key = key,
        ) {
            it.copy(
                muted = false,
            )
        }
        runCatching {
            val service = account.getService(appDatabase)
            service.unmuteActor(UnmuteActorRequest(actor = Did(did = userKey.id)))
        }.onFailure {
            MemCacheable.updateWith<UiRelation>(
                key = key,
            ) {
                it.copy(
                    muted = true,
                )
            }
        }
    }

    override fun searchStatus(
        query: String,
        scope: CoroutineScope,
        pageSize: Int,
        pagingKey: String,
    ): Flow<PagingData<UiTimeline>> {
        val service = account.getService(appDatabase)
        return timelinePager(
            pageSize = pageSize,
            pagingKey = pagingKey,
            accountKey = account.accountKey,
            database = database,
            filterFlow = localFilterRepository.getFlow(forSearch = true),
            scope = scope,
            mediator =
                SearchStatusRemoteMediator(
                    service,
                    database,
                    account.accountKey,
                    pagingKey,
                    query,
                ),
        )
    }

    override fun searchUser(
        query: String,
        scope: CoroutineScope,
        pageSize: Int,
    ): Flow<PagingData<UiUserV2>> {
        val service = account.getService(appDatabase)
        return Pager(
            config = PagingConfig(pageSize = pageSize),
        ) {
            SearchUserPagingSource(
                service,
                account.accountKey,
                query,
            )
        }.flow.cachedIn(scope)
    }

    override fun discoverUsers(pageSize: Int): Flow<PagingData<UiUserV2>> {
        val service = account.getService(appDatabase)
        return Pager(
            config = PagingConfig(pageSize = pageSize),
        ) {
            TrendsUserPagingSource(
                service,
                account.accountKey,
            )
        }.flow
    }

    override fun discoverHashtags(pageSize: Int): Flow<PagingData<UiHashtag>> =
        throw UnsupportedOperationException("Bluesky does not support discover hashtags")

    override fun discoverStatuses(
        pageSize: Int,
        scope: CoroutineScope,
        pagingKey: String,
    ): Flow<PagingData<UiTimeline>> = throw UnsupportedOperationException("Bluesky does not support discover statuses")

    override fun composeConfig(statusKey: MicroBlogKey?): ComposeConfig =
        ComposeConfig(
            text = ComposeConfig.Text(300),
            media = ComposeConfig.Media(4, true),
        )

    override suspend fun follow(
        userKey: MicroBlogKey,
        relation: UiRelation,
    ) {
        when {
            relation.following -> unfollow(userKey)
            relation.blocking -> unblock(userKey)
            else -> follow(userKey)
        }
    }

    override fun profileActions(): List<ProfileAction> =
        listOf(
            object : ProfileAction.Mute {
                override suspend fun invoke(
                    userKey: MicroBlogKey,
                    relation: UiRelation,
                ) {
                    if (relation.muted) {
                        unmute(userKey)
                    } else {
                        mute(userKey)
                    }
                }

                override fun relationState(relation: UiRelation): Boolean = relation.muted
            },
            object : ProfileAction.Block {
                override suspend fun invoke(
                    userKey: MicroBlogKey,
                    relation: UiRelation,
                ) {
                    if (relation.blocking) {
                        unblock(userKey)
                    } else {
                        block(userKey)
                    }
                }

                override fun relationState(relation: UiRelation): Boolean = relation.blocking
            },
        )

    private val preferences: MemCacheable<List<PreferencesUnion>> by lazy {
        MemCacheable(
            key = "preferences_${account.accountKey}",
        ) {
            val service = account.getService(appDatabase)
            service
                .getPreferences()
                .maybeResponse()
                ?.preferences
                .orEmpty()
        }
    }

    private val myFeedsKey = "my_feeds_${account.accountKey}"

    val myFeeds: MemCacheable<ImmutableList<UiList>> by lazy {
        MemCacheable(
            key = myFeedsKey,
        ) {
            val service = account.getService(appDatabase)
            val preferences =
                service
                    .getPreferences()
                    .maybeResponse()
                    ?.preferences
                    .orEmpty()
            val items =
                preferences
                    .filterIsInstance<PreferencesUnion.SavedFeedsPrefV2>()
                    .firstOrNull()
                    ?.value
                    ?.items
                    ?.filter {
                        it.type == Type.FEED
                    }.orEmpty()
            service
                .getFeedGenerators(
                    GetFeedGeneratorsQueryParams(
                        feeds =
                            items
                                .map { AtUri(it.value) }
                                .toImmutableList(),
                    ),
                ).maybeResponse()
                ?.feeds
                ?.map {
                    it.render(account.accountKey)
                }.orEmpty()
                .toImmutableList()
        }
    }

    fun popularFeeds(
        query: String?,
        scope: CoroutineScope,
    ): Flow<PagingData<Pair<UiList, Boolean>>> =
        Pager(
            config = PagingConfig(pageSize = 20),
        ) {
            object : PagingSource<String, UiList>() {
                override fun getRefreshKey(state: PagingState<String, UiList>): String? = null

                override suspend fun load(params: LoadParams<String>): LoadResult<String, UiList> {
                    val service = account.getService(appDatabase)
                    val result =
                        service
                            .getPopularFeedGenerators(
                                GetPopularFeedGeneratorsQueryParams(
                                    limit = params.loadSize.toLong(),
                                    cursor = params.key,
                                    query = query,
                                ),
                            ).maybeResponse()
                    return LoadResult.Page(
                        data =
                            result
                                ?.feeds
                                ?.map {
                                    it.render(account.accountKey)
                                }.orEmpty(),
                        prevKey = null,
                        nextKey = result?.cursor,
                    )
                }
            }
        }.flow
            .cachedIn(scope)
            .let { feeds ->
                combine(
                    feeds,
                    MemCacheable.subscribe<ImmutableList<UiList>>(myFeedsKey),
                ) { popular, my ->
                    popular.map { item ->
                        item to my.any { it.id == item.id }
                    }
                }
            }.cachedIn(scope)

    private fun feedInfoKey(uri: String) = "feed_info_$uri"

    fun feedInfo(uri: String): MemCacheable<UiList> =
        MemCacheable(
            key = feedInfoKey(uri),
        ) {
            val service = account.getService(appDatabase)
            service
                .getFeedGenerator(
                    GetFeedGeneratorQueryParams(
                        feed = AtUri(uri),
                    ),
                ).requireResponse()
                .view
                .render(account.accountKey)
        }

    fun feedTimeline(
        uri: String,
        pageSize: Int = 20,
        scope: CoroutineScope,
    ): Flow<PagingData<UiTimeline>> {
        val pagingKey = "feed_timeline_$uri"
        return timelinePager(
            pageSize = pageSize,
            pagingKey = pagingKey,
            accountKey = account.accountKey,
            database = database,
            filterFlow = localFilterRepository.getFlow(forTimeline = true),
            scope = scope,
            mediator =
                FeedTimelineRemoteMediator(
                    service = account.getService(appDatabase),
                    accountKey = account.accountKey,
                    database = database,
                    uri = uri,
                    pagingKey = pagingKey,
                ),
        )
    }

    suspend fun subscribeFeed(data: UiList) {
        MemCacheable.updateWith<ImmutableList<UiList>>(
            key = myFeedsKey,
        ) {
            (it + data).toImmutableList()
        }
        runCatching {
            val service = account.getService(appDatabase)
            val currentPreferences = service.getPreferences().requireResponse()
            val feedInfo =
                service
                    .getFeedGenerator(GetFeedGeneratorQueryParams(feed = AtUri(data.id)))
                    .requireResponse()
            val newPreferences = currentPreferences.preferences.toMutableList()
            val prefIndex = newPreferences.indexOfFirst { it is PreferencesUnion.SavedFeedsPref }
            if (prefIndex != -1) {
                val pref = newPreferences[prefIndex] as PreferencesUnion.SavedFeedsPref
                val newPref =
                    pref.value.copy(
                        saved = (pref.value.saved + feedInfo.view.uri).toImmutableList(),
                        pinned = (pref.value.pinned + feedInfo.view.uri).toImmutableList(),
                    )
                newPreferences[prefIndex] = PreferencesUnion.SavedFeedsPref(newPref)
            }
            val prefV2Index =
                newPreferences.indexOfFirst { it is PreferencesUnion.SavedFeedsPrefV2 }
            if (prefV2Index != -1) {
                val pref = newPreferences[prefV2Index] as PreferencesUnion.SavedFeedsPrefV2
                val newPref =
                    pref.value.copy(
                        items =
                            (
                                pref.value.items +
                                    SavedFeed(
                                        type = Type.FEED,
                                        value = feedInfo.view.uri.atUri,
                                        pinned = true,
                                        id = uuid4().toString(),
                                    )
                            ).toImmutableList(),
                    )
                newPreferences[prefV2Index] = PreferencesUnion.SavedFeedsPrefV2(newPref)
            }

            service.putPreferences(
                request =
                    PutPreferencesRequest(
                        preferences = newPreferences.toImmutableList(),
                    ),
            )
            myFeeds.refresh()
        }.onFailure {
            MemCacheable.updateWith<ImmutableList<UiList>>(
                key = myFeedsKey,
            ) {
                it.filterNot { item -> item.id == data.id }.toImmutableList()
            }
        }
    }

    suspend fun unsubscribeFeed(data: UiList) {
        MemCacheable.updateWith<ImmutableList<UiList>>(
            key = myFeedsKey,
        ) {
            it.filterNot { item -> item.id == data.id }.toImmutableList()
        }
        runCatching {
            val service = account.getService(appDatabase)
            val currentPreferences = service.getPreferences().requireResponse()
            val feedInfo =
                service
                    .getFeedGenerator(GetFeedGeneratorQueryParams(feed = AtUri(data.id)))
                    .requireResponse()
            val newPreferences = currentPreferences.preferences.toMutableList()
            val prefIndex = newPreferences.indexOfFirst { it is PreferencesUnion.SavedFeedsPref }
            if (prefIndex != -1) {
                val pref = newPreferences[prefIndex] as PreferencesUnion.SavedFeedsPref
                val newPref =
                    pref.value.copy(
                        saved =
                            pref.value.saved
                                .filterNot { it == feedInfo.view.uri }
                                .toImmutableList(),
                        pinned =
                            pref.value.pinned
                                .filterNot { it == feedInfo.view.uri }
                                .toImmutableList(),
                    )
                newPreferences[prefIndex] = PreferencesUnion.SavedFeedsPref(newPref)
            }
            val prefV2Index =
                newPreferences.indexOfFirst { it is PreferencesUnion.SavedFeedsPrefV2 }
            if (prefV2Index != -1) {
                val pref = newPreferences[prefV2Index] as PreferencesUnion.SavedFeedsPrefV2
                val newPref =
                    pref.value.copy(
                        items =
                            pref.value.items
                                .filterNot { it.value == feedInfo.view.uri.atUri }
                                .toImmutableList(),
                    )
                newPreferences[prefV2Index] = PreferencesUnion.SavedFeedsPrefV2(newPref)
            }
            service.putPreferences(
                request =
                    PutPreferencesRequest(
                        preferences = newPreferences.toImmutableList(),
                    ),
            )
            myFeeds.refresh()
        }.onFailure {
            MemCacheable.updateWith<ImmutableList<UiList>>(
                key = myFeedsKey,
            ) {
                (it + data).toImmutableList()
            }
        }
    }

    suspend fun favouriteFeed(data: UiList) {
        MemCacheable.update(
            key = feedInfoKey(data.id),
            value =
                data.copy(
                    liked = !data.liked,
                ),
        )
        val service = account.getService(appDatabase)
        runCatching {
            val feedInfo =
                service
                    .getFeedGenerator(GetFeedGeneratorQueryParams(feed = AtUri(data.id)))
                    .requireResponse()
            val likedUri =
                feedInfo.view.viewer
                    ?.like
                    ?.atUri
            if (likedUri != null) {
                deleteLikeRecord(likedUri)
            } else {
                createLikeRecord(cid = feedInfo.view.cid.cid, uri = feedInfo.view.uri.atUri)
            }
        }.onFailure {
            MemCacheable.update(
                key = feedInfoKey(data.id),
                value =
                    data.copy(
                        liked = data.liked,
                    ),
            )
        }
    }

    private val myListKey = "my_list_${account.accountKey}"

    val myList: MemCacheable<ImmutableList<UiList>> by lazy {
        MemCacheable(
            key = myListKey,
        ) {
            val service = account.getService(appDatabase)
            service
                .getLists(
                    params = GetListsQueryParams(actor = Did(did = account.accountKey.id)),
                ).requireResponse()
                .lists
                .map {
                    it.render(account.accountKey)
                }.toImmutableList()
        }
    }

    private fun listInfoKey(uri: String) = "list_info_$uri"

    fun listInfo(uri: String): MemCacheable<UiList> =
        MemCacheable(
            key = listInfoKey(uri),
        ) {
            val service = account.getService(appDatabase)
            service
                .getList(
                    GetListQueryParams(
                        list = AtUri(uri),
                    ),
                ).requireResponse()
                .list
                .render(account.accountKey)
        }

    fun listTimeline(
        uri: String,
        pageSize: Int = 20,
        scope: CoroutineScope,
    ): Flow<PagingData<UiTimeline>> {
        val pagingKey = "list_timeline_$uri"
        return timelinePager(
            pageSize = pageSize,
            pagingKey = pagingKey,
            accountKey = account.accountKey,
            database = database,
            filterFlow = localFilterRepository.getFlow(forTimeline = true),
            scope = scope,
            mediator =
                ListTimelineRemoteMediator(
                    service = account.getService(appDatabase),
                    accountKey = account.accountKey,
                    database = database,
                    uri = uri,
                    pagingKey = pagingKey,
                ),
        )
    }

    suspend fun createList(
        title: String,
        description: String?,
        icon: FileItem?,
    ) {
        runCatching {
            val service = account.getService(appDatabase)
            val iconInfo =
                if (icon != null) {
                    service.uploadBlob(icon.readBytes()).maybeResponse()
                } else {
                    null
                }
            val record =
                app.bsky.graph.List(
                    purpose = app.bsky.graph.Token.CURATELIST,
                    name = title,
                    description = description,
                    avatar = iconInfo?.blob,
                    createdAt = Clock.System.now(),
                )
            service.createRecord(
                request =
                    CreateRecordRequest(
                        repo = Did(did = account.accountKey.id),
                        collection = Nsid("app.bsky.graph.list"),
                        record = record.bskyJson(),
                    ),
            )
            myList.refresh()
        }
    }

    suspend fun deleteList(uri: String) {
        runCatching {
            val service = account.getService(appDatabase)
            val id = uri.substringAfterLast('/')
            service.applyWrites(
                request =
                    ApplyWritesRequest(
                        repo = Did(did = account.accountKey.id),
                        writes =
                            persistentListOf(
                                ApplyWritesRequestWriteUnion.Delete(
                                    value =
                                        ApplyWritesDelete(
                                            collection = Nsid("app.bsky.graph.list"),
                                            rkey = id,
                                        ),
                                ),
                            ),
                    ),
            )
            myList.refresh()
        }
    }

    suspend fun updateList(
        uri: String,
        title: String,
        description: String?,
        icon: FileItem?,
    ) {
        runCatching {
            val service = account.getService(appDatabase)
            val currentInfo: app.bsky.graph.List =
                service
                    .getRecord(
                        params =
                            com.atproto.repo.GetRecordQueryParams(
                                collection = Nsid("app.bsky.graph.list"),
                                repo = Did(did = account.accountKey.id),
                                rkey = uri.substringAfterLast('/'),
                            ),
                    ).requireResponse()
                    .bskyJson()

            val iconInfo =
                if (icon != null) {
                    service.uploadBlob(icon.readBytes()).maybeResponse()
                } else {
                    null
                }
            val newRecord =
                currentInfo
                    .copy(
                        name = title,
                        description = description,
                    ).let {
                        if (iconInfo != null) {
                            it.copy(avatar = iconInfo.blob)
                        } else {
                            it
                        }
                    }
            service.putRecord(
                request =
                    PutRecordRequest(
                        repo = Did(did = account.accountKey.id),
                        collection = Nsid("app.bsky.graph.list"),
                        rkey = uri.substringAfterLast('/'),
                        record = newRecord.bskyJson(),
                    ),
            )
            myList.refresh()
        }
    }

    private fun listMemberKey(listId: String) = "listMembers_$listId"

    fun listMembers(
        listUri: String,
        pageSize: Int = 20,
        scope: CoroutineScope,
    ): Flow<PagingData<UiUserV2>> =
        memoryPager(
            pageSize = pageSize,
            pagingKey = listMemberKey(listUri),
            scope = scope,
            mediator =
                object : RemoteMediator<Int, UiUserV2>() {
                    override suspend fun load(
                        loadType: LoadType,
                        state: PagingState<Int, UiUserV2>,
                    ): MediatorResult {
                        val service = account.getService(appDatabase)
                        try {
                            if (loadType == LoadType.PREPEND) {
                                return MediatorResult.Success(endOfPaginationReached = true)
                            }
                            val key =
                                if (loadType == LoadType.REFRESH) {
                                    null
                                } else {
                                    MemoryPagingSource
                                        .get<UiUserV2>(key = listMemberKey(listUri))
                                        ?.lastOrNull()
                                        ?.key
                                        ?.id
                                }
                            val result =
                                service
                                    .getList(
                                        params =
                                            GetListQueryParams(
                                                list = AtUri(listUri),
                                                cursor = key,
                                                limit = state.config.pageSize.toLong(),
                                            ),
                                    ).maybeResponse()
                                    ?.items
                                    ?.map {
                                        it.subject.render(account.accountKey)
                                    } ?: emptyList()

                            if (loadType == LoadType.REFRESH) {
                                MemoryPagingSource.update(
                                    key = listMemberKey(listUri),
                                    value = result.toImmutableList(),
                                )
                            } else if (loadType == LoadType.APPEND) {
                                MemoryPagingSource.append(
                                    key = listMemberKey(listUri),
                                    value = result.toImmutableList(),
                                )
                            }

                            return MediatorResult.Success(
                                endOfPaginationReached = result.isEmpty(),
                            )
                        } catch (e: Exception) {
                            return MediatorResult.Error(e)
                        }
                    }
                },
        )

    suspend fun addMember(
        listUri: String,
        userKey: MicroBlogKey,
    ) {
        runCatching {
            val service = account.getService(appDatabase)
            val user =
                service
                    .getProfile(GetProfileQueryParams(actor = Did(did = userKey.id)))
                    .requireResponse()
                    .render(account.accountKey)

            MemoryPagingSource.updateWith(
                key = listMemberKey(listUri),
            ) {
                (listOf(user) + it)
                    .distinctBy {
                        it.key
                    }.toImmutableList()
            }
            service.createRecord(
                CreateRecordRequest(
                    repo = Did(did = account.accountKey.id),
                    collection = Nsid("app.bsky.graph.listitem"),
                    record =
                        app.bsky.graph
                            .Listitem(
                                list = AtUri(listUri),
                                subject = Did(userKey.id),
                                createdAt = Clock.System.now(),
                            ).bskyJson(),
                ),
            )
        }
    }

    suspend fun removeMember(
        listUri: String,
        userKey: MicroBlogKey,
    ) {
        runCatching {
            val service = account.getService(appDatabase)
            MemoryPagingSource.updateWith<UiUserV2>(
                key = listMemberKey(listUri),
            ) {
                it
                    .filter { user -> user.key.id != userKey.id }
                    .toImmutableList()
            }
            var record: ListRecordsRecord? = null
            var cursor: String? = null
            while (record == null) {
                val response =
                    service
                        .listRecords(
                            params =
                                ListRecordsQueryParams(
                                    repo = Did(did = account.accountKey.id),
                                    collection = Nsid("app.bsky.graph.listitem"),
                                    limit = 100,
                                    cursor = cursor,
                                ),
                        ).requireResponse()
                if (response.cursor == null || response.records.isEmpty()) {
                    break
                }
                cursor = response.cursor
                record =
                    response.records
                        .firstOrNull {
                            val item: app.bsky.graph.Listitem = it.value.bskyJson()
                            item.list.atUri == listUri && item.subject.did == userKey.id
                        }
            }
            if (record != null) {
                service.deleteRecord(
                    DeleteRecordRequest(
                        repo = Did(did = account.accountKey.id),
                        collection = Nsid("app.bsky.graph.listitem"),
                        rkey = record.uri.atUri.substringAfterLast('/'),
                    ),
                )
            }
        }
    }
}

internal inline fun <reified T, reified R> T.bskyJson(): R = bskyJson.decodeFromJsonElement(bskyJson.encodeToJsonElement(this))
