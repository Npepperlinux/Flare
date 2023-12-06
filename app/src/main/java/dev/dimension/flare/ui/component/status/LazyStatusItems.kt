package dev.dimension.flare.ui.component.status

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import dev.dimension.flare.R
import dev.dimension.flare.common.LazyPagingItemsProxy
import dev.dimension.flare.model.MicroBlogKey
import dev.dimension.flare.ui.component.status.bluesky.BlueskyNotificationComponent
import dev.dimension.flare.ui.component.status.bluesky.BlueskyStatusComponent
import dev.dimension.flare.ui.component.status.bluesky.BlueskyStatusEvent
import dev.dimension.flare.ui.component.status.mastodon.MastodonNotificationComponent
import dev.dimension.flare.ui.component.status.mastodon.MastodonStatusComponent
import dev.dimension.flare.ui.component.status.mastodon.MastodonStatusEvent
import dev.dimension.flare.ui.component.status.mastodon.StatusPlaceholder
import dev.dimension.flare.ui.component.status.misskey.MisskeyNotificationComponent
import dev.dimension.flare.ui.component.status.misskey.MisskeyStatusComponent
import dev.dimension.flare.ui.component.status.misskey.MisskeyStatusEvent
import dev.dimension.flare.ui.model.UiMedia
import dev.dimension.flare.ui.model.UiState
import dev.dimension.flare.ui.model.UiStatus
import dev.dimension.flare.ui.model.onError
import dev.dimension.flare.ui.model.onLoading
import dev.dimension.flare.ui.model.onSuccess
import dev.dimension.flare.ui.theme.DisabledAlpha
import dev.dimension.flare.ui.theme.screenHorizontalPadding

context(LazyListScope, UiState<LazyPagingItemsProxy<UiStatus>>, StatusEvent)
internal fun status() {
    onSuccess { lazyPagingItems ->
        if (
            (
                lazyPagingItems.loadState.refresh == LoadState.Loading ||
                    lazyPagingItems.loadState.prepend == LoadState.Loading
            ) &&
            lazyPagingItems.itemCount == 0
        ) {
            items(10) {
                Column {
                    StatusPlaceholder(
                        modifier = Modifier.padding(horizontal = screenHorizontalPadding),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        modifier = Modifier.alpha(DisabledAlpha),
                    )
                }
            }
        } else if ((
                lazyPagingItems.loadState.refresh is LoadState.Error ||
                    lazyPagingItems.loadState.prepend is LoadState.Error
            ) &&
            lazyPagingItems.itemCount == 0
        ) {
            item {
                Column(
                    modifier =
                        Modifier
                            .fillParentMaxSize()
                            .clickable {
                                lazyPagingItems.retry()
                            },
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.MoodBad,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.status_loadmore_error_retry),
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        } else if (lazyPagingItems.itemCount == 0) {
            item {
                Column(
                    modifier =
                        Modifier
                            .fillParentMaxSize()
                            .clickable {
                                lazyPagingItems.refresh()
                            },
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EmojiEmotions,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.status_empty),
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        } else {
            with(lazyPagingItems) {
                statusItems()
            }
            if (lazyPagingItems.itemCount > 0) {
                when (lazyPagingItems.loadState.append) {
                    is LoadState.Error ->
                        item {
                            Column(
                                modifier =
                                    Modifier
                                        .clickable {
                                            lazyPagingItems.retry()
                                        }
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = stringResource(R.string.status_loadmore_error),
                                )
                                Text(text = stringResource(id = R.string.status_loadmore_error_retry))
                            }
                        }

                    LoadState.Loading ->
                        items(10) {
                            Column {
                                StatusPlaceholder(
                                    modifier = Modifier.padding(horizontal = screenHorizontalPadding),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(
                                    modifier = Modifier.alpha(DisabledAlpha),
                                )
                            }
                        }

                    is LoadState.NotLoading ->
                        item {
                            Column(
                                modifier =
                                    Modifier
                                        .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.status_loadmore_end),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                }
            }
        }
    }
    onLoading {
        items(10) {
            Column {
                StatusPlaceholder(
                    modifier = Modifier.padding(horizontal = screenHorizontalPadding),
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.alpha(DisabledAlpha),
                )
            }
        }
    }
    onError {
    }
}

context(LazyListScope, LazyPagingItemsProxy<UiStatus>, StatusEvent)
private fun statusItems() {
    items(
        itemCount,
        key =
            itemKey {
                it.itemKey
            },
        contentType =
            itemContentType {
                it.itemType
            },
    ) {
        Column {
            val item = get(it)
            StatusItem(item, this@StatusEvent)
            if (it != itemCount - 1) {
                HorizontalDivider(
                    modifier = Modifier.alpha(DisabledAlpha),
                )
            }
        }
    }
}

@Composable
internal fun StatusItem(
    item: UiStatus?,
    event: StatusEvent,
    horizontalPadding: Dp = screenHorizontalPadding,
) {
    when (item) {
        is UiStatus.Mastodon ->
            MastodonStatusComponent(
                data = item,
                event = event.mastodonStatusEvent,
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )

        is UiStatus.MastodonNotification ->
            MastodonNotificationComponent(
                data = item,
                event = event.mastodonStatusEvent,
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )

        null -> {
            StatusPlaceholder(
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        is UiStatus.Misskey ->
            MisskeyStatusComponent(
                data = item,
                event = event.misskeyStatusEvent,
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )

        is UiStatus.MisskeyNotification ->
            MisskeyNotificationComponent(
                data = item,
                event = event.misskeyStatusEvent,
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )

        is UiStatus.Bluesky ->
            BlueskyStatusComponent(
                data = item,
                event = event.blueskyStatusEvent,
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )

        is UiStatus.BlueskyNotification ->
            BlueskyNotificationComponent(
                data = item,
                event = event.blueskyStatusEvent,
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
    }
}

internal data class StatusEvent(
    val mastodonStatusEvent: MastodonStatusEvent,
    val misskeyStatusEvent: MisskeyStatusEvent,
    val blueskyStatusEvent: BlueskyStatusEvent,
) {
    companion object {
        val empty =
            StatusEvent(
                mastodonStatusEvent = EmptyStatusEvent,
                misskeyStatusEvent = EmptyStatusEvent,
                blueskyStatusEvent = EmptyStatusEvent,
            )
    }
}

internal data object EmptyStatusEvent : MastodonStatusEvent, MisskeyStatusEvent, BlueskyStatusEvent {
    override fun onStatusClick(data: UiStatus.Bluesky) = Unit

    override fun onStatusClick(data: UiStatus.Misskey) = Unit

    override fun onReactionClick(
        data: UiStatus.Misskey,
        reaction: UiStatus.Misskey.EmojiReaction,
    ) = Unit

    override fun onReplyClick(data: UiStatus.Misskey) = Unit

    override fun onReblogClick(data: UiStatus.Misskey) = Unit

    override fun onQuoteClick(data: UiStatus.Misskey) = Unit

    override fun onAddReactionClick(data: UiStatus.Misskey) = Unit

    override fun onDeleteClick(data: UiStatus.Misskey) = Unit

    override fun onReportClick(data: UiStatus.Misskey) = Unit

    override fun onStatusClick(status: UiStatus.Mastodon) = Unit

    override fun onReplyClick(status: UiStatus.Mastodon) = Unit

    override fun onReblogClick(status: UiStatus.Mastodon) = Unit

    override fun onLikeClick(status: UiStatus.Mastodon) = Unit

    override fun onBookmarkClick(status: UiStatus.Mastodon) = Unit

    override fun onMediaClick(media: UiMedia) = Unit

    override fun onUserClick(userKey: MicroBlogKey) = Unit

    override fun onDeleteClick(status: UiStatus.Mastodon) = Unit

    override fun onReportClick(status: UiStatus.Mastodon) = Unit

    override fun onReplyClick(data: UiStatus.Bluesky) = Unit

    override fun onReblogClick(data: UiStatus.Bluesky) = Unit

    override fun onQuoteClick(data: UiStatus.Bluesky) = Unit

    override fun onLikeClick(data: UiStatus.Bluesky) = Unit

    override fun onReportClick(data: UiStatus.Bluesky) = Unit

    override fun onDeleteClick(data: UiStatus.Bluesky) = Unit
}
