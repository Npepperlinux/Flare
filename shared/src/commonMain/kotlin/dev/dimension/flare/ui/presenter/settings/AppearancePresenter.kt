package dev.dimension.flare.ui.presenter.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import dev.dimension.flare.ui.model.UiState
import dev.dimension.flare.ui.model.UiTimeline
import dev.dimension.flare.ui.model.createSampleStatus
import dev.dimension.flare.ui.model.map
import dev.dimension.flare.ui.presenter.PresenterBase
import dev.dimension.flare.ui.presenter.home.ActiveAccountPresenter

public class AppearancePresenter : PresenterBase<AppearanceState>() {
    @Composable
    override fun body(): AppearanceState {
        val account =
            remember {
                ActiveAccountPresenter()
            }.body()
        return object : AppearanceState {
            override val sampleStatus =
                account.user.map {
                    createSampleStatus(it)
                }
        }
    }
}

@Immutable
public interface AppearanceState {
    public val sampleStatus: UiState<UiTimeline>
}
