package dev.dimension.flare.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AvatarComponent(
    data: String?,
    modifier: Modifier = Modifier,
    size: Dp = AvatarComponentDefaults.size,
) {
    NetworkImage(
        model = data,
        contentDescription = null,
        modifier =
            Modifier
                .size(size)
                .clip(RoundedCornerShape(4.dp))
                .then(modifier),
    )
}

object AvatarComponentDefaults {
    val size = 44.dp
    val compatSize = 20.dp
}
