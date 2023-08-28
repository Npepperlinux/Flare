package dev.dimension.flare.ui.component.status.bluesky

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.dimension.flare.R
import dev.dimension.flare.ui.component.AvatarComponent
import dev.dimension.flare.ui.component.HtmlText
import dev.dimension.flare.ui.component.status.StatusRetweetHeaderComponent
import dev.dimension.flare.ui.model.UiStatus
import dev.dimension.flare.ui.theme.MediumAlpha

@Composable
internal fun BlueskyNotificationComponent(
    data: UiStatus.BlueskyNotification,
    event: BlueskyStatusEvent,
    modifier: Modifier = Modifier,
) {
    when (data.reason) {
        "like" -> {
            NotificationComponent(
                data = data,
                event = event,
                modifier = modifier,
                icon = Icons.Default.Favorite,
                text = stringResource(id = R.string.mastodon_notification_item_favourited_your_status),
            )
        }

        "repost" -> {
            NotificationComponent(
                data = data,
                event = event,
                modifier = modifier,
                icon = Icons.Default.SyncAlt,
                text = stringResource(id = R.string.mastodon_notification_item_reblogged_your_status),
            )
        }

        "follow" -> {
            NotificationComponent(
                data = data,
                event = event,
                modifier = modifier,
                icon = Icons.Default.PersonAdd,
                text = stringResource(id = R.string.mastodon_notification_item_followed_you),
            )
        }

        "mention" -> {
            NotificationComponent(
                data = data,
                event = event,
                modifier = modifier,
                icon = Icons.Default.PersonAdd,
                text = stringResource(id = R.string.misskey_notification_item_mentioned_you),
            )
        }

        "reply" -> {
            NotificationComponent(
                data = data,
                event = event,
                modifier = modifier,
                icon = Icons.Default.Reply,
                text = stringResource(id = R.string.misskey_notification_item_replied_to_you),
            )
        }

        "quote" -> {
            NotificationComponent(
                data = data,
                event = event,
                modifier = modifier,
                icon = Icons.Default.PersonAdd,
                text = stringResource(id = R.string.misskey_notification_item_quoted_your_status),
            )
        }

        else -> {
            UnknwonNotificationComponent(
                data = data,
                event = event,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun NotificationComponent(
    data: UiStatus.BlueskyNotification,
    icon: ImageVector,
    text: String,
    event: BlueskyStatusEvent,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        StatusRetweetHeaderComponent(
            icon = icon,
            user = data.user,
            text = text,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (data.user != null) {
                AvatarComponent(
                    data = data.user.avatarUrl,
                    modifier = Modifier
                        .clickable {
                            event.onUserClick(data.user.userKey)
                        },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    HtmlText(
                        element = data.user.nameElement,
                        layoutDirection = data.user.nameDirection,
                        modifier = Modifier
                            .clickable {
                                event.onUserClick(data.user.userKey)
                            },
                    )
                    Text(
                        text = data.user.handle,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .alpha(MediumAlpha)
                            .clickable {
                                event.onUserClick(data.user.userKey)
                            },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun UnknwonNotificationComponent(
    data: UiStatus.BlueskyNotification,
    event: BlueskyStatusEvent,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(text = stringResource(id = R.string.bluesky_notification_unknown_message, data.reason))
        },
        supportingContent = {
            Text(text = stringResource(id = R.string.bluesky_notification_unknown_message2))
        },
    )
}