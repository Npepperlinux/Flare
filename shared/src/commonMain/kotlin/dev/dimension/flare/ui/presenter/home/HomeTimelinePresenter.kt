package dev.dimension.flare.ui.presenter.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.compose.collectAsLazyPagingItems
import dev.dimension.flare.common.PagingState
import dev.dimension.flare.common.toPagingState
import dev.dimension.flare.data.datasource.microblog.AuthenticatedMicroblogDataSource
import dev.dimension.flare.data.repository.AccountRepository
import dev.dimension.flare.data.repository.accountServiceProvider
import dev.dimension.flare.model.AccountType
import dev.dimension.flare.ui.model.UiTimeline
import dev.dimension.flare.ui.model.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

public class HomeTimelinePresenter(
    private val accountType: AccountType,
) : TimelinePresenter(),
    KoinComponent {
    private val accountRepository: AccountRepository by inject()

    @Composable
    override fun listState(): PagingState<UiTimeline> {
        val scope = rememberCoroutineScope()
        val serviceState = accountServiceProvider(accountType = accountType, repository = accountRepository)
        return serviceState
            .map { service ->
                remember(service) {
                    val pagingKey =
                        if (service is AuthenticatedMicroblogDataSource) {
                            "home_${service.accountKey}"
                        } else {
                            "home"
                        }
                    service.homeTimeline(scope = scope, pagingKey = pagingKey)
                }.collectAsLazyPagingItems()
            }.toPagingState()
    }
}
