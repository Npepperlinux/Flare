package dev.dimension.flare.ui.presenter.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.dimension.flare.common.collectAsState
import dev.dimension.flare.data.repository.activeAccountServicePresenter
import dev.dimension.flare.ui.model.UiState
import dev.dimension.flare.ui.model.UiUser
import dev.dimension.flare.ui.model.flatMap
import dev.dimension.flare.ui.model.toUi
import dev.dimension.flare.ui.presenter.PresenterBase

class ActiveAccountPresenter : PresenterBase<ActiveAccountState>() {
    @Composable
    override fun body(): ActiveAccountState {
        val user =
            activeAccountServicePresenter().flatMap { (service, account) ->
                remember(account.accountKey) {
                    service.userById(account.accountKey.id)
                }.collectAsState().toUi()
            }
        return object : ActiveAccountState {
            override val user = user
        }
    }
}

interface ActiveAccountState {
    val user: UiState<UiUser>
}