package dev.dimension.flare.ui.presenter.home.mastodon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.dimension.flare.data.datasource.mastodon.MastodonDataSource
import dev.dimension.flare.data.repository.accountServiceProvider
import dev.dimension.flare.model.AccountType
import dev.dimension.flare.ui.model.UiState
import dev.dimension.flare.ui.model.map
import dev.dimension.flare.ui.presenter.home.TimelinePresenter
import dev.dimension.flare.ui.render.Render

class FavouriteTimelinePresenter(
    private val accountType: AccountType,
) : TimelinePresenter() {
    @Composable
    override fun listState(): UiState<LazyPagingItems<Render.Item>> {
        val scope = rememberCoroutineScope()
        val serviceState = accountServiceProvider(accountType = accountType)
        return serviceState.map { service ->
            remember(service) {
                require(service is MastodonDataSource)
                service.favouriteTimeline(scope = scope)
            }.collectAsLazyPagingItems()
        }
    }
}
