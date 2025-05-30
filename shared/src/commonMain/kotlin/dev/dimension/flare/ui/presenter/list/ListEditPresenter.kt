package dev.dimension.flare.ui.presenter.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.dimension.flare.common.refreshSuspend
import dev.dimension.flare.data.datasource.microblog.ListDataSource
import dev.dimension.flare.data.datasource.microblog.ListMetaData
import dev.dimension.flare.data.datasource.microblog.ListMetaDataType
import dev.dimension.flare.data.repository.AccountRepository
import dev.dimension.flare.data.repository.accountServiceProvider
import dev.dimension.flare.model.AccountType
import dev.dimension.flare.ui.model.UiState
import dev.dimension.flare.ui.model.map
import dev.dimension.flare.ui.model.onSuccess
import dev.dimension.flare.ui.presenter.PresenterBase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Presenter for editing lists.
 */
public class ListEditPresenter(
    private val accountType: AccountType,
    private val listId: String,
) : PresenterBase<EditListState>(),
    KoinComponent {
    private val accountRepository: AccountRepository by inject()

    @Composable
    override fun body(): EditListState {
        val scope = rememberCoroutineScope()
        val serviceState = accountServiceProvider(accountType = accountType, repository = accountRepository)
        val listInfoState =
            remember(
                accountType,
                listId,
            ) {
                ListInfoPresenter(accountType, listId)
            }.body()
        val state =
            remember(
                accountType,
                listId,
            ) {
                EditListMemberPresenter(accountType, listId)
            }.body()
        val memberState =
            remember(
                accountType,
                listId,
            ) {
                ListMembersPresenter(accountType, listId)
            }.body()
        return object :
            EditListState,
            EditListMemberState by state,
            ListMembersState by memberState,
            ListInfoState by listInfoState {
            override val supportedMetaData =
                serviceState.map {
                    require(it is ListDataSource)
                    it.supportedMetaData
                }

            override fun refresh() {
                scope.launch {
                    memberInfo.refreshSuspend()
                }
            }

            override suspend fun updateList(listMetaData: ListMetaData) {
                serviceState.onSuccess {
                    require(it is ListDataSource)
                    it.updateList(listId, listMetaData)
                }
            }
        }
    }
}

@Immutable
public interface EditListState :
    EditListMemberState,
    ListMembersState,
    ListInfoState {
    public val supportedMetaData: UiState<ImmutableList<ListMetaDataType>>

    public fun refresh()

    public suspend fun updateList(listMetaData: ListMetaData)
}
