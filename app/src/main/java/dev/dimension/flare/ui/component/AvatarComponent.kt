package dev.dimension.flare.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.dimension.flare.data.model.AvatarShape
import dev.dimension.flare.data.model.LocalAppearanceSettings

@Composable
fun AvatarComponent(
    data: String?,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    val appearanceSettings = LocalAppearanceSettings.current
    NetworkImage(
        model = data,
        contentDescription = null,
        modifier =
            modifier
                .size(size)
                .clip(
                    when (appearanceSettings.avatarShape) {
                        AvatarShape.CIRCLE ->
                            CircleShape
                        AvatarShape.SQUARE ->
                            RoundedCornerShape(4.dp)
                    },
                ),
    )
}
