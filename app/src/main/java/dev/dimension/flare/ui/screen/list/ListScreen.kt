package dev.dimension.flare.ui.screen.list

import android.os.Parcelable
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CreateListRouteDestination
import com.ramcosta.composedestinations.generated.destinations.DeleteListRouteDestination
import com.ramcosta.composedestinations.generated.destinations.EditListRouteDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.EllipsisVertical
import compose.icons.fontawesomeicons.solid.Pen
import compose.icons.fontawesomeicons.solid.Plus
import compose.icons.fontawesomeicons.solid.Thumbtack
import compose.icons.fontawesomeicons.solid.ThumbtackSlash
import compose.icons.fontawesomeicons.solid.Trash
import dev.dimension.flare.R
import dev.dimension.flare.data.model.HomeTimelineTabItem
import dev.dimension.flare.data.model.IconType
import dev.dimension.flare.data.model.ListTimelineTabItem
import dev.dimension.flare.data.model.TabMetaData
import dev.dimension.flare.data.model.TitleType
import dev.dimension.flare.data.repository.SettingsRepository
import dev.dimension.flare.model.AccountType
import dev.dimension.flare.ui.component.FAIcon
import dev.dimension.flare.ui.component.FlareScaffold
import dev.dimension.flare.ui.component.FlareTopAppBar
import dev.dimension.flare.ui.component.RefreshContainer
import dev.dimension.flare.ui.component.ThemeWrapper
import dev.dimension.flare.ui.component.uiListItemComponent
import dev.dimension.flare.ui.model.UiList
import dev.dimension.flare.ui.model.collectAsUiState
import dev.dimension.flare.ui.model.flatMap
import dev.dimension.flare.ui.model.map
import dev.dimension.flare.ui.model.onSuccess
import dev.dimension.flare.ui.presenter.home.UserPresenter
import dev.dimension.flare.ui.presenter.invoke
import dev.dimension.flare.ui.presenter.list.AllListPresenter
import dev.dimension.flare.ui.presenter.list.AllListState
import dev.dimension.flare.ui.screen.home.TimelineRoute
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import moe.tlaster.precompose.molecule.producePresenter
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Destination<RootGraph>(
    wrappers = [ThemeWrapper::class],
)
@Composable
internal fun ListScreenRoute(
    navigator: DestinationsNavigator,
    accountType: AccountType,
    drawerState: DrawerState,
) {
    val scope = rememberCoroutineScope()
    val scaffoldNavigator =
        rememberListDetailPaneScaffoldNavigator<ListDetailPaneNavArgs>()
    NavigableListDetailPaneScaffold(
        navigator = scaffoldNavigator,
        listPane = {
            AnimatedPane {
                ListScreen(
                    accountType = accountType,
                    toList = { item ->
                        scope.launch {
                            scaffoldNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                ListDetailPaneNavArgs(
                                    id = item.id,
                                    title = item.title,
                                ),
                            )
                        }
                    },
                    createList = {
                        navigator.navigate(CreateListRouteDestination(accountType = accountType))
                    },
                    editList = {
                        navigator.navigate(
                            EditListRouteDestination(
                                accountType = accountType,
                                listId = it.id,
                            ),
                        )
                    },
                    deleteList = {
                        navigator.navigate(
                            DeleteListRouteDestination(
                                accountType = accountType,
                                listId = it.id,
                                title = it.title,
                            ),
                        )
                    },
                )
            }
        },
        detailPane = {
            AnimatedPane {
                scaffoldNavigator.currentDestination?.contentKey?.let { args ->
                    TimelineRoute(
                        navigator = navigator,
                        tabItem =
                            ListTimelineTabItem(
                                account = accountType,
                                listId = args.id,
                                metaData =
                                    TabMetaData(
                                        title = TitleType.Text(args.title),
                                        icon = IconType.Material(IconType.Material.MaterialIcon.List),
                                    ),
                            ),
                        drawerState = drawerState,
                    )
                }
            }
        },
    )
}

@Parcelize
private data class ListDetailPaneNavArgs(
    val id: String,
    val title: String,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListScreen(
    accountType: AccountType,
    toList: (UiList) -> Unit,
    createList: () -> Unit,
    editList: (UiList) -> Unit,
    deleteList: (UiList) -> Unit,
) {
    val state by producePresenter {
        presenter(accountType)
    }
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    FlareScaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            FlareTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.home_tab_list_title))
                },
                scrollBehavior = topAppBarScrollBehavior,
                actions = {
                    IconButton(
                        onClick = {
                            createList.invoke()
                        },
                    ) {
                        FAIcon(
                            imageVector = FontAwesomeIcons.Solid.Plus,
                            contentDescription = stringResource(id = R.string.list_add),
                        )
                    }
                },
            )
        },
    ) { contentPadding ->
        RefreshContainer(
            modifier =
                Modifier
                    .fillMaxSize(),
            indicatorPadding = contentPadding,
            isRefreshing = state.isRefreshing,
            onRefresh = state::refresh,
            content = {
                LazyColumn(
                    contentPadding = contentPadding,
                ) {
                    uiListItemComponent(
                        state.items,
                        toList,
                        trailingContent = { item ->
                            state.currentTabs.onSuccess { currentTabs ->
                                val isPinned =
                                    remember(
                                        item,
                                        currentTabs,
                                    ) {
                                        currentTabs.contains(item.id)
                                    }
                                IconButton(
                                    onClick = {
                                        if (isPinned) {
                                            state.unpinList(item)
                                        } else {
                                            state.pinList(item)
                                        }
                                    },
                                ) {
                                    AnimatedContent(isPinned) {
                                        if (it) {
                                            FAIcon(
                                                imageVector = FontAwesomeIcons.Solid.ThumbtackSlash,
                                                contentDescription = stringResource(id = R.string.tab_settings_add),
                                            )
                                        } else {
                                            FAIcon(
                                                imageVector = FontAwesomeIcons.Solid.Thumbtack,
                                                contentDescription = stringResource(id = R.string.tab_settings_remove),
                                            )
                                        }
                                    }
                                }
                            }
                            if (!item.readonly) {
                                var showDropdown by remember {
                                    mutableStateOf(false)
                                }
                                IconButton(onClick = { showDropdown = true }) {
                                    FAIcon(
                                        imageVector = FontAwesomeIcons.Solid.EllipsisVertical,
                                        contentDescription = stringResource(id = R.string.more),
                                    )
                                    DropdownMenu(
                                        expanded = showDropdown,
                                        onDismissRequest = { showDropdown = false },
                                    ) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = stringResource(id = R.string.list_edit),
                                                )
                                            },
                                            onClick = {
                                                editList(item)
                                                showDropdown = false
                                            },
                                            leadingIcon = {
                                                FAIcon(
                                                    imageVector = FontAwesomeIcons.Solid.Pen,
                                                    contentDescription = stringResource(id = R.string.list_edit),
                                                )
                                            },
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = stringResource(id = R.string.list_delete),
                                                    color = MaterialTheme.colorScheme.error,
                                                )
                                            },
                                            onClick = {
                                                deleteList(item)
                                                showDropdown = false
                                            },
                                            leadingIcon = {
                                                FAIcon(
                                                    imageVector = FontAwesomeIcons.Solid.Trash,
                                                    contentDescription = stringResource(id = R.string.list_delete),
                                                    tint = MaterialTheme.colorScheme.error,
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        },
                    )
                }
            },
        )
    }
}

@Composable
private fun presenter(
    accountType: AccountType,
    settingsRepository: SettingsRepository = koinInject(),
    appScope: CoroutineScope = koinInject(),
) = run {
    val tabSettings by settingsRepository.tabSettings.collectAsUiState()
    val accountState =
        remember(accountType) {
            UserPresenter(
                accountType = accountType,
                userKey = null,
            )
        }.invoke()
    val currentTabs =
        accountState.user.flatMap { user ->
            tabSettings.map {
                it.homeTabs
                    .getOrDefault(user.key, listOf(HomeTimelineTabItem(accountType = AccountType.Specific(user.key))))
                    .filterIsInstance<ListTimelineTabItem>()
                    .map { it.listId }
                    .toImmutableList()
            }
        }
    val state =
        remember(accountType) {
            AllListPresenter(accountType)
        }.invoke()

    object : AllListState by state {
        val currentTabs = currentTabs

        fun pinList(item: UiList) {
            accountState.user.onSuccess { user ->
                appScope.launch {
                    settingsRepository.updateTabSettings {
                        copy(
                            homeTabs =
                                homeTabs + (
                                    user.key to
                                        homeTabs[user.key].orEmpty().plus(
                                            ListTimelineTabItem(
                                                account = AccountType.Specific(user.key),
                                                listId = item.id,
                                                metaData =
                                                    TabMetaData(
                                                        title = TitleType.Text(item.title),
                                                        icon = IconType.Material(IconType.Material.MaterialIcon.List),
                                                    ),
                                            ),
                                        )
                                ),
                        )
                    }
                }
            }
        }

        fun unpinList(item: UiList) {
            accountState.user.onSuccess { user ->
                appScope.launch {
                    settingsRepository.updateTabSettings {
                        copy(
                            homeTabs =
                                homeTabs + (
                                    user.key to
                                        homeTabs[user.key].orEmpty().filter {
                                            if (it is ListTimelineTabItem) {
                                                it.listId != item.id
                                            } else {
                                                true
                                            }
                                        }
                                ),
                        )
                    }
                }
            }
        }
    }
}
