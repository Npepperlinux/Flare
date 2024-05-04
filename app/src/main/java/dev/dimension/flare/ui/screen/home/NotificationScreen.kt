package dev.dimension.flare.ui.screen.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import dev.dimension.flare.R
import dev.dimension.flare.data.datasource.microblog.NotificationFilter
import dev.dimension.flare.model.AccountType
import dev.dimension.flare.molecule.producePresenter
import dev.dimension.flare.ui.component.AvatarComponent
import dev.dimension.flare.ui.component.FlareScaffold
import dev.dimension.flare.ui.component.LocalBottomBarHeight
import dev.dimension.flare.ui.component.RefreshContainer
import dev.dimension.flare.ui.component.ThemeWrapper
import dev.dimension.flare.ui.component.status.LazyStatusVerticalStaggeredGrid
import dev.dimension.flare.ui.component.status.StatusEvent
import dev.dimension.flare.ui.component.status.status
import dev.dimension.flare.ui.model.onSuccess
import dev.dimension.flare.ui.presenter.home.NotificationPresenter
import dev.dimension.flare.ui.presenter.home.NotificationState
import dev.dimension.flare.ui.presenter.home.UserPresenter
import dev.dimension.flare.ui.presenter.home.UserState
import dev.dimension.flare.ui.presenter.invoke
import dev.dimension.flare.ui.theme.screenHorizontalPadding
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalSharedTransitionApi::class)
@Destination<RootGraph>(
    wrappers = [ThemeWrapper::class],
)
@Composable
internal fun AnimatedVisibilityScope.NotificationRoute(
    accountType: AccountType,
    tabState: TabState,
    drawerState: DrawerState,
    sharedTransitionScope: SharedTransitionScope,
) = with(sharedTransitionScope) {
    val scope = rememberCoroutineScope()
    NotificationScreen(
        accountType = accountType,
        tabState = tabState,
        toQuickMenu = {
            scope.launch {
                drawerState.open()
            }
        },
    )
}

context(AnimatedVisibilityScope, SharedTransitionScope)
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class,
)
@Composable
private fun NotificationScreen(
    accountType: AccountType,
    tabState: TabState,
    toQuickMenu: () -> Unit,
) {
    val state by producePresenter(key = "notification_$accountType") {
        notificationPresenter(accountType = accountType)
    }
    val lazyListState = rememberLazyStaggeredGridState()
    RegisterTabCallback(tabState = tabState, lazyListState = lazyListState)
    val windowInfo = currentWindowAdaptiveInfo()
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    FlareScaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.home_tab_notifications_title))
                },
                scrollBehavior = topAppBarScrollBehavior,
                actions = {
                    if (windowInfo.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT) {
                        state.state.allTypes.onSuccess {
                            if (it.size > 1) {
                                NotificationFilterSelector(it, state.state)
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (LocalBottomBarHeight.current != 0.dp) {
                        state.user.onSuccess {
                            IconButton(
                                onClick = toQuickMenu,
                            ) {
                                AvatarComponent(it.avatarUrl, size = 24.dp)
                            }
                        }
                    }
                },
            )
        },
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        RefreshContainer(
            indicatorPadding = contentPadding,
            onRefresh = state::refresh,
            isRefreshing = state.isRefreshing,
            content = {
                LazyStatusVerticalStaggeredGrid(
                    state = lazyListState,
                    contentPadding = contentPadding,
                ) {
                    if (windowInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
                        state.state.allTypes.onSuccess {
                            if (it.size > 1) {
                                item(
                                    span = StaggeredGridItemSpan.FullLine,
                                ) {
                                    NotificationFilterSelector(
                                        it,
                                        state.state,
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = screenHorizontalPadding),
                                    )
                                }
                            }
                        }
                    }
                    with(state.state.listState) {
                        with(state.statusEvent) {
                            status()
                        }
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationFilterSelector(
    filters: ImmutableList<NotificationFilter>,
    notificationState: NotificationState,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier,
    ) {
        filters.forEachIndexed { index, notificationType ->
            SegmentedButton(
                selected = notificationState.notificationType == notificationType,
                onClick = {
                    notificationState.onNotificationTypeChanged(notificationType)
                },
                shape =
                    SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = filters.size,
                    ),
            ) {
                Text(text = stringResource(id = notificationType.title))
            }
        }
    }
}

private val NotificationFilter.title: Int
    get() =
        when (this) {
            NotificationFilter.All -> R.string.notification_tab_all_title
            NotificationFilter.Mention -> R.string.notification_tab_mentions_title
        }

@Composable
private fun notificationPresenter(
    accountType: AccountType,
    statusEvent: StatusEvent = koinInject(),
) = run {
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val accountState = remember { UserPresenter(accountType = accountType, userKey = null) }.invoke()
    val state = remember { NotificationPresenter(accountType = accountType) }.invoke()
    object : UserState by accountState {
        val state = state
        val statusEvent = statusEvent
        val isRefreshing = isRefreshing

        fun refresh() {
            scope.launch {
                isRefreshing = true
                state.refresh()
                isRefreshing = false
            }
        }
    }
}
