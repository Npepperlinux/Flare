package dev.dimension.flare.ui.screen.status.action

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import dev.dimension.flare.model.MicroBlogKey
import dev.dimension.flare.molecule.producePresenter
import dev.dimension.flare.ui.component.NetworkImage
import dev.dimension.flare.ui.component.ThemeWrapper
import dev.dimension.flare.ui.model.onSuccess
import dev.dimension.flare.ui.presenter.status.action.MisskeyReactionPresenter

@Destination(
    style = DestinationStyleBottomSheet::class,
    deepLinks = [
        DeepLink(
            uriPattern = "flare://$FULL_ROUTE_PLACEHOLDER",
        ),
    ],
    wrappers = [ThemeWrapper::class],
)
@Composable
fun ColumnScope.MisskeyReactionRoute(
    statusKey: MicroBlogKey,
    navigator: DestinationsNavigator,
) {
    MisskeyReactionSheet(
        statusKey = statusKey,
        onBack = {
            navigator.navigateUp()
        },
    )
}

@Composable
fun ColumnScope.MisskeyReactionSheet(
    statusKey: MicroBlogKey,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by producePresenter(statusKey.toString()) {
        misskeyReactionPresenter(statusKey)
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(48.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        state.emojis.onSuccess {
            items(it) { emoji ->
                NetworkImage(
                    model = emoji.url,
                    contentDescription = emoji.shortcode,
                    contentScale = ContentScale.Fit,
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clickable {
                                state.select(emoji)
                                onBack.invoke()
                            },
                )
            }
        }
    }
}

@Composable
private fun misskeyReactionPresenter(statusKey: MicroBlogKey) =
    run {
        remember(statusKey) {
            MisskeyReactionPresenter(statusKey)
        }.invoke()
    }