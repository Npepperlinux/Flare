package dev.dimension.flare.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.composefluent.FluentTheme
import io.github.kdroidfilter.nucleus.window.DecoratedWindowScope
import io.github.kdroidfilter.nucleus.window.TitleBar
import io.github.kdroidfilter.nucleus.window.styling.LocalTitleBarStyle
import org.apache.commons.lang3.SystemUtils

@Composable
internal fun DecoratedWindowScope.PlatformTitleBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit = {},
) {
    if (!SystemUtils.IS_OS_MAC_OSX) {
        TitleBar(
            style =
                LocalTitleBarStyle.current.copy(
                    colors =
                        LocalTitleBarStyle.current.colors.copy(
                            background =
                                FluentTheme.colors.background.mica.base
                                    .copy(alpha = 0f),
                            inactiveBackground = Color.Transparent,
                        ),
                ),
            modifier = modifier,
            content = {
                Row(
                    modifier = Modifier.align(Alignment.Start),
                ) {
                    content()
                }
            },
        )
    } else {
        Row(
            modifier =
                modifier
                    .padding(
                        start = 72.dp,
                    ),
        ) {
            content()
        }
    }
}
