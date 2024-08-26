package dev.dimension.flare.ui.presenter.home.mastodon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.compose.collectAsLazyPagingItems
import dev.dimension.flare.common.PagingState
import dev.dimension.flare.common.collectAsState
import dev.dimension.flare.common.refreshSuspend
import dev.dimension.flare.common.toPagingState
import dev.dimension.flare.data.datasource.mastodon.MastodonDataSource
import dev.dimension.flare.data.repository.accountServiceProvider
import dev.dimension.flare.model.AccountType
import dev.dimension.flare.ui.model.UiList
import dev.dimension.flare.ui.model.UiState
import dev.dimension.flare.ui.model.UiUserV2
import dev.dimension.flare.ui.model.flatMap
import dev.dimension.flare.ui.model.map
import dev.dimension.flare.ui.model.onSuccess
import dev.dimension.flare.ui.model.toUi
import dev.dimension.flare.ui.presenter.PresenterBase
import kotlinx.coroutines.launch

class EditListPresenter(
    private val accountType: AccountType,
    private val listId: String,
) : PresenterBase<EditListState>() {
    @Composable
    override fun body(): EditListState {
        val scope = rememberCoroutineScope()
        val serviceState = accountServiceProvider(accountType = accountType)
        val listInfo =
            serviceState.flatMap {
                remember(it) {
                    require(it is MastodonDataSource)
                    it.listInfo(listId)
                }.collectAsState().toUi()
            }

        val memberInfo =
            serviceState
                .map {
                    remember(it) {
                        require(it is MastodonDataSource)
                        it.listMembers(listId, scope = scope)
                    }.collectAsLazyPagingItems()
                }.toPagingState()

        return object : EditListState {
            override val listInfo = listInfo
            override val memberInfo = memberInfo

            override fun refresh() {
                scope.launch {
                    memberInfo.refreshSuspend()
                }
            }

            override fun addMember(userId: String) {
                serviceState.onSuccess {
                    scope.launch {
                        require(it is MastodonDataSource)
                        it.addMember(listId, userId)
                    }
                }
            }

            override fun removeMember(userId: String) {
                serviceState.onSuccess {
                    scope.launch {
                        require(it is MastodonDataSource)
                        it.removeMember(listId, userId)
                    }
                }
            }

            override suspend fun updateTitle(title: String) {
                serviceState.onSuccess {
                    require(it is MastodonDataSource)
                    it.updateList(listId, title)
                }
            }
        }
    }
}

interface EditListState {
    val listInfo: UiState<UiList>
    val memberInfo: PagingState<UiUserV2>

    fun refresh()

    fun addMember(userId: String)

    fun removeMember(userId: String)

    suspend fun updateTitle(title: String)
}
