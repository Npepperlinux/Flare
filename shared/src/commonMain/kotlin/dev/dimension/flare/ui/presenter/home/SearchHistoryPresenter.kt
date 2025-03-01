package dev.dimension.flare.ui.presenter.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.dimension.flare.common.ImmutableListWrapper
import dev.dimension.flare.data.repository.SearchHistoryRepository
import dev.dimension.flare.ui.model.UiSearchHistory
import dev.dimension.flare.ui.model.UiState
import dev.dimension.flare.ui.model.collectAsUiState
import dev.dimension.flare.ui.presenter.PresenterBase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

public class SearchHistoryPresenter :
    PresenterBase<SearchHistoryState>(),
    KoinComponent {
    private val repository: SearchHistoryRepository by inject()

    @Composable
    override fun body(): SearchHistoryState {
        val searchHistories by remember {
            repository.allSearchHistory
        }.collectAsUiState()

        return object : SearchHistoryState {
            override val searchHistories = searchHistories

            override fun addSearchHistory(keyword: String) {
                repository.addSearchHistory(keyword)
            }

            override fun deleteSearchHistory(keyword: String) {
                repository.deleteSearchHistory(keyword)
            }
        }
    }
}

@Immutable
public interface SearchHistoryState {
    public val searchHistories: UiState<ImmutableListWrapper<UiSearchHistory>>

    public fun addSearchHistory(keyword: String)

    public fun deleteSearchHistory(keyword: String)
}
