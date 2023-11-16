package dev.dimension.flare.ui.presenter.status.action

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dev.dimension.flare.data.repository.activeAccountServicePresenter
import dev.dimension.flare.model.MicroBlogKey
import dev.dimension.flare.ui.model.map
import dev.dimension.flare.ui.model.onSuccess
import dev.dimension.flare.ui.presenter.PresenterBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.rememberKoinInject

class DeleteStatusPresenter(
    private val statusKey: MicroBlogKey,
) : PresenterBase<DeleteStatusState>() {
    @Composable
    override fun body(): DeleteStatusState {
        val service =
            activeAccountServicePresenter().map { (service, _) ->
                service
            }
        // using io scope because it's a long-running operation
        val scope = rememberKoinInject<CoroutineScope>()
        return object : DeleteStatusState {
            override fun delete() {
                service.onSuccess {
                    scope.launch {
                        it.deleteStatus(statusKey)
                    }
                }
            }
        }
    }
}

interface DeleteStatusState {
    fun delete()
}