import Awesome
import MarkdownUI
import shared
import SwiftUI

struct HomeAppBar: ToolbarContent {
    @Environment(\.horizontalSizeClass) var horizontalSizeClass
    let router: Router
    let accountType: AccountType
    @Binding var showSettings: Bool
    @Binding var showLogin: Bool
    @Binding var selectedHomeTab: Int
    @State private var showTabSettings = false
    @State private var showUserSettings = false
    @ObservedObject var timelineStore: TimelineStore
    @ObservedObject var tabSettingsStore: TabSettingsStore
    @State private var scrollOffset: CGFloat = 0
    @State private var scrollReader: ScrollViewProxy?

    init(router: Router,
         accountType: AccountType,
         showSettings: Binding<Bool>,
         showLogin: Binding<Bool>,
         selectedHomeTab: Binding<Int>,
         timelineStore: TimelineStore,
         tabSettingsStore: TabSettingsStore)
    {
        self.router = router
        self.accountType = accountType
        _showSettings = showSettings
        _showLogin = showLogin
        _selectedHomeTab = selectedHomeTab
        self.timelineStore = timelineStore
        self.tabSettingsStore = tabSettingsStore
    }

    private func onTabSelected(_ tab: FLTabItem) {
        withAnimation {
            timelineStore.updateCurrentPresenter(for: tab)
        }
    }

    var body: some ToolbarContent {
        if !(accountType is AccountTypeGuest) {
            // 左边的用户头像按钮
            ToolbarItem(placement: .navigation) {
                Button {
                    showSettings = true
                } label: {
                    if let user = tabSettingsStore.currentUser {
                        UserAvatar(data: user.avatar, size: 32)
                            .clipShape(Circle())
                            .padding(.leading, 8)
                    } else {
                        userAvatarPlaceholder(size: 32)
                            .clipShape(Circle())
                            .padding(.leading, 8)
                    }
                }
            }

            // 中间的标签栏
            ToolbarItem(placement: .principal) {
                ScrollViewReader { proxy in
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 24) {
                            ForEach(tabSettingsStore.availableTabs, id: \.key) { tab in
                                Button(action: {
                                    onTabSelected(tab)
                                    // 滚动到选中的标签
                                    withAnimation {
                                        proxy.scrollTo(tab.key, anchor: .center)
                                    }
                                }) {
                                    VStack(spacing: 4) {
                                        switch tab.metaData.title {
                                        case let .text(title):
                                            Text(title)
                                                .font(.system(size: 16))
                                                .foregroundColor(timelineStore.selectedTabKey == tab.key ? .primary : .gray)
                                                .fontWeight(timelineStore.selectedTabKey == tab.key ? .semibold : .regular)
                                        case let .localized(key):
                                            Text(NSLocalizedString(key, comment: ""))
                                                .font(.system(size: 16))
                                                .foregroundColor(timelineStore.selectedTabKey == tab.key ? .primary : .gray)
                                                .fontWeight(timelineStore.selectedTabKey == tab.key ? .semibold : .regular)
                                        }

                                        Rectangle()
                                            .fill(timelineStore.selectedTabKey == tab.key ? Color.accentColor : Color.clear)
                                            .frame(height: 2)
                                            .frame(width: 24)
                                    }
                                    .id(tab.key)
                                }
                            }
                        }
                        .padding(.horizontal)
                    }
                    .frame(maxWidth: UIScreen.main.bounds.width - 120)
                    .frame(height: 44)
                    .onAppear {
                        // 如果有选中的标签，滚动到该标签
                        if let selectedKey = timelineStore.selectedTabKey {
                            withAnimation {
                                proxy.scrollTo(selectedKey, anchor: .center)
                            }
                        }
                    }
                }
            }

            // 右边的设置按钮
            ToolbarItem(placement: .primaryAction) {
                Button {
                    showTabSettings = true
                } label: {
                    Image(systemName: "line.3.horizontal")
                        .foregroundColor(.primary)
                        .frame(width: 32, height: 32)
                        .padding(.trailing, 8)
                        .padding(.top, -7)
                }
                .sheet(isPresented: $showTabSettings) {
                    HomeAppBarSettingsView(store: tabSettingsStore)
                }
            }
        } else {
            // 访客模式下显示登录按钮
            ToolbarItem(placement: .primaryAction) {
                Button {
                    showLogin = true
                } label: {
                    Text("Login")
                }
            }
        }
    }
}
