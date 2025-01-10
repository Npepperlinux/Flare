package dev.dimension.flare.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal actual object PlatformColorScheme {
    actual val primary: Color
        @Composable
        get() = MaterialTheme.colorScheme.primary
    actual val error: Color
        @Composable
        get() = MaterialTheme.colorScheme.error
    actual val caption: Color
        @Composable
        get() = MaterialTheme.colorScheme.onSurfaceVariant
    actual val outline: Color
        @Composable
        get() = MaterialTheme.colorScheme.outline.copy(alpha = MediumAlpha)
    actual val card: Color
        @Composable
        get() = MaterialTheme.colorScheme.surface
    public actual val cardAlt: Color
        @Composable
        get() = MaterialTheme.colorScheme.surfaceVariant
}
