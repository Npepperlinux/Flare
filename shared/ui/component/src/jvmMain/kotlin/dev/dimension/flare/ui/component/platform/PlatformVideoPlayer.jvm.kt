package dev.dimension.flare.ui.component.platform

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

@Composable
internal actual fun PlatformVideoPlayer(
    uri: String,
    previewUri: String?,
    contentDescription: String?,
    modifier: Modifier,
    muted: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
    aspectRatio: Float?,
    contentScale: ContentScale,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    autoPlay: Boolean,
    remainingTimeContent: @Composable (BoxScope.(Long) -> Unit)?,
    loadingPlaceholder: @Composable BoxScope.() -> Unit,
) {
}
