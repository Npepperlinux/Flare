package dev.dimension.flare.ui.model.mapper

import com.fleeksoft.ksoup.nodes.Element
import dev.dimension.flare.data.database.cache.model.DbDirectMessageTimelineWithRoom
import dev.dimension.flare.data.database.cache.model.DbMessageItemWithUser
import dev.dimension.flare.data.database.cache.model.DbPagingTimelineWithStatus
import dev.dimension.flare.data.database.cache.model.DbUser
import dev.dimension.flare.data.database.cache.model.MessageContent
import dev.dimension.flare.data.database.cache.model.StatusContent
import dev.dimension.flare.data.database.cache.model.UserContent
import dev.dimension.flare.data.datasource.microblog.StatusEvent
import dev.dimension.flare.model.MicroBlogKey
import dev.dimension.flare.model.ReferenceType
import dev.dimension.flare.ui.model.UiDMItem
import dev.dimension.flare.ui.model.UiDMRoom
import dev.dimension.flare.ui.model.UiTimeline
import dev.dimension.flare.ui.render.toUi
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant

internal fun DbPagingTimelineWithStatus.render(event: StatusEvent): UiTimeline =
    status.status.data.content.render(
        timeline.accountKey,
        event,
        references =
            status.references
                .map { it.reference.referenceType to it.status.data.content }
                .toMap(),
    )

internal fun StatusContent.render(
    accountKey: MicroBlogKey,
    event: StatusEvent,
    references: Map<ReferenceType, StatusContent> = emptyMap(),
) = when (this) {
    is StatusContent.Mastodon ->
        data.render(
            accountKey = accountKey,
            event = event as StatusEvent.Mastodon,
            references = references,
            host = accountKey.host,
        )

    is StatusContent.MastodonNotification ->
        data.render(
            accountKey = accountKey,
            event = event as StatusEvent.Mastodon,
            references = references,
        )

    is StatusContent.Misskey ->
        data.render(
            accountKey = accountKey,
            event = event as StatusEvent.Misskey,
            references = references,
        )

    is StatusContent.MisskeyNotification ->
        data.render(
            accountKey = accountKey,
            event = event as StatusEvent.Misskey,
            references = references,
        )

    is StatusContent.BlueskyReason ->
        reason.render(
            accountKey = accountKey,
            event = event as StatusEvent.Bluesky,
            references = references,
        )

    is StatusContent.Bluesky ->
        data.render(
            accountKey = accountKey,
            event = event as StatusEvent.Bluesky,
        )

    is StatusContent.BlueskyNotification ->
        renderBlueskyNotification(
            accountKey = accountKey,
            event = event as StatusEvent.Bluesky,
            references = references,
        )

    is StatusContent.XQT ->
        data.render(
            accountKey = accountKey,
            event = event as StatusEvent.XQT,
            references = references,
        )

    is StatusContent.VVO ->
        data.render(
            accountKey = accountKey,
            event = event as StatusEvent.VVO,
        )

    is StatusContent.VVOComment ->
        data.render(
            accountKey = accountKey,
            event = event as StatusEvent.VVO,
        )
}

internal fun DbUser.render(accountKey: MicroBlogKey) =
    when (content) {
        is UserContent.Bluesky -> content.data.render(accountKey = accountKey)
        is UserContent.BlueskyLite -> content.data.render(accountKey = accountKey)
        is UserContent.Mastodon -> content.data.render(accountKey = accountKey, host = accountKey.host)
        is UserContent.Misskey -> content.data.render(accountKey = accountKey)
        is UserContent.MisskeyLite -> content.data.render(accountKey = accountKey)
        is UserContent.VVO -> content.data.render(accountKey = accountKey)
        is UserContent.XQT -> content.data.render(accountKey = accountKey)
    }

internal fun DbDirectMessageTimelineWithRoom.render(accountKey: MicroBlogKey) =
    UiDMRoom(
        key = room.room.roomKey,
        lastMessage = room.lastMessage?.render(accountKey),
        users =
            room.users
                .filter { it.reference.userKey != accountKey }
                .map { it.user.render(accountKey) }
                .toImmutableList(),
        unreadCount = timeline.unreadCount,
    )

internal fun DbMessageItemWithUser.render(accountKey: MicroBlogKey) =
    UiDMItem(
        key = message.messageKey,
        user = user.render(accountKey),
        timestamp = Instant.fromEpochMilliseconds(message.timestamp).toUi(),
        content =
            when (val content = message.content) {
                is MessageContent.Bluesky -> content.render(accountKey = accountKey)
                is MessageContent.Local ->
                    UiDMItem.Message.Text(
                        Element("span")
                            .apply {
                                appendText(content.text)
                            }.toUi(),
                    )
            },
        isFromMe = accountKey == message.userKey,
        sendState =
            when (val content = message.content) {
                is MessageContent.Local ->
                    when (content.state) {
                        MessageContent.Local.State.SENDING -> UiDMItem.SendState.Sending
                        MessageContent.Local.State.FAILED -> UiDMItem.SendState.Failed
                    }
                is MessageContent.Bluesky -> null
            },
    )
