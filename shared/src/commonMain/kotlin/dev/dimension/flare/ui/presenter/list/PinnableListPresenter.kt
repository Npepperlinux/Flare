package dev.dimension.flare.ui.presenter.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.dimension.flare.common.collectAsState
import dev.dimension.flare.data.datasource.bluesky.BlueskyDataSource
import dev.dimension.flare.data.datasource.microblog.ListDataSource
import dev.dimension.flare.data.repository.accountServiceProvider
import dev.dimension.flare.model.AccountType
import dev.dimension.flare.ui.model.UiList
import dev.dimension.flare.ui.model.UiState
import dev.dimension.flare.ui.model.flatMap
import dev.dimension.flare.ui.model.map
import dev.dimension.flare.ui.model.mapNotNull
import dev.dimension.flare.ui.model.toUi
import dev.dimension.flare.ui.presenter.PresenterBase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class PinnableListPresenter(
    private val accountType: AccountType,
) : PresenterBase<PinnableListState>() {
    @Composable
    override fun body(): PinnableListState {
        val serviceState = accountServiceProvider(accountType = accountType)
        val items =
            serviceState
                .mapNotNull { service ->
                    remember(service) {
                        service
                            .takeIf {
                                it is ListDataSource
                            }?.let {
                                it as ListDataSource
                            }?.myList
                    }?.collectAsState()?.toUi()
                }.flatMap { it }

        val bsky =
            serviceState
                .mapNotNull { service ->
                    remember(service) {
                        service
                            .takeIf {
                                it is BlueskyDataSource
                            }?.let {
                                it as BlueskyDataSource
                            }?.myFeeds
                    }?.collectAsState()?.toUi()
                }.flatMap { it }

        val result =
            if (bsky is UiState.Success) {
                items.map {
                    it + bsky.data
                }
            } else {
                items
            }

        return object : PinnableListState {
            override val items =
                result.map {
                    it.toImmutableList()
                }
        }
    }
}

interface PinnableListState {
    val items: UiState<ImmutableList<UiList>>
}