import Kingfisher
import os
import shared
import SwiftUI

// Secondary
struct TabItemRow: View {
    let tab: FLTabItem
    let store: AppBarTabSettingStore
    let isPrimary: Bool
    let defaultToggleValue: Bool

    // 本地状态用于防止频繁操作
    @State private var isProcessing = false

    // 添加日志记录器
    private let logger = Logger(subsystem: "com.flare.app", category: "TabItemRow")
    @Environment(FlareTheme.self) private var theme

    // 添加默认初始化器，默认值为true
    init(tab: FLTabItem, store: AppBarTabSettingStore, isPrimary: Bool, defaultToggleValue: Bool = true) {
        self.tab = tab
        self.store = store
        self.isPrimary = isPrimary
        self.defaultToggleValue = defaultToggleValue
    }

    var body: some View {
        HStack {
            // if !isPrimary {
            //     Image(systemName: "line.3.horizontal")
            //         .foregroundColor(Color.textTertiary)
            // }

            switch tab.metaData.icon {
            case let .material(iconName):
                if let materialIcon = FLMaterialIcon(rawValue: iconName) {
                    materialIcon.icon
                        .foregroundColor(theme.tintColor) // .interactiveActive
                }
            case let .mixed(icons):
                if let firstIcon = icons.first,
                   let materialIcon = FLMaterialIcon(rawValue: firstIcon)
                {
                    materialIcon.icon.foregroundColor(theme.tintColor)
//                        .foregroundColor(FColors.State.swiftUIActive)//interactiveActive
                }
            case .avatar:
                Image(systemName: "person.circle")
//                    .foregroundColor(FColors.State.swiftUIActive)
            }

            switch tab.metaData.title {
            case let .text(title):
                Text(title)
//                    .foregroundColor(FColors.State.swiftUIActive)
            case let .localized(key):
                Text(NSLocalizedString(key, comment: ""))
//                    .foregroundColor(FColors.Text.swiftUIPrimary)
            }

            Spacer()

            if !isPrimary {
                Toggle("", isOn: Binding(
                    get: {
                        let isEnabled = store.availableAppBarTabsItems.contains(where: { $0.key == tab.key })
                        logger.debug("Toggle GET: tab=\(tab.key), isEnabled=\(isEnabled), defaultValue=\(defaultToggleValue)")
                        return isEnabled ? true : defaultToggleValue
                    },
                    set: { _ in
                        if !isProcessing {
                            isProcessing = true
                            logger.debug("Toggle SET: tab=\(tab.key), 开始切换状态")
                            store.toggleTab(tab.key)
                            // 设置短暂延迟避免频繁操作
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                                isProcessing = false
                                logger.debug("Toggle SET: tab=\(tab.key), 切换完成")
                            }
                        }
                    }
                ))
//                .tint(FColors.State.swiftUIActive)
                .disabled(isProcessing)
            }
        }
    }
}

// list and feed items row
struct ListTabItemRowRow: View {
    let listId: String
    let title: String
    let store: AppBarTabSettingStore
    let onRequestEdit: (String, String) -> Void
    let isBlueskyFeed: Bool // 新增字段，标识是否为Bluesky Feed
    let defaultToggleValue: Bool // 添加默认值参数

    @Environment(FlareTheme.self) private var theme
    @State private var isProcessing = false
    @State private var currentTitle: String

    // 添加日志记录器
    private let logger = Logger(subsystem: "com.flare.app", category: "ListTabItemRowRow")

    init(listId: String, title: String, store: AppBarTabSettingStore, onRequestEdit: @escaping (String, String) -> Void, isBlueskyFeed: Bool = false, defaultToggleValue: Bool = true) {
        self.listId = listId
        self.title = title
        self.store = store
        self.onRequestEdit = onRequestEdit
        self.isBlueskyFeed = isBlueskyFeed
        self.defaultToggleValue = defaultToggleValue
        _currentTitle = State(initialValue: title)
    }

    var body: some View {
        HStack {
//            Image(systermName: "line.3.horizontal")
//                .foregroundColor(Color.textTertiary)

            if let listIconUrl = store.listIconUrls[listId], let url = URL(string: listIconUrl) {
                ListIconView(imageUrl: listIconUrl ?? "", size: 50, listId: listId)
                    .frame(width: 32, height: 32).clipShape(RoundedRectangle(cornerRadius: 8))

//                KFImage(url)
//                    .placeholder {
//                        RoundedRectangle(cornerRadius: 8)
//                            .fill(Color.gray.opacity(0.2))
//                    }
//                    .resizable()
//                    .aspectRatio(contentMode: .fill)
//                    .frame(width: 32, height: 32)
//                    .clipShape(RoundedRectangle(cornerRadius: 8))
            } else {
                ListIconView(imageUrl: "", size: 50, listId: listId).frame(width: 32, height: 32).clipShape(RoundedRectangle(cornerRadius: 8))
//                ZStack {
//                    RoundedRectangle(cornerRadius: 8)
//                        .fill(Color.blue.opacity(0.7))
//                    Image(systemName: isBlueskyFeed ? "square.grid.2x2" : "list.bullet")
//                        .foregroundColor(.white)
//                        .font(.system(size: 18))
//                }
//                .frame(width: 32, height: 32)
            }

            Text(store.listTitles[listId] ?? currentTitle)
                //     .foregroundColor(FColors.Text.swiftUIPrimary)
                .lineLimit(1)

            Spacer()

            HStack(spacing: 8) {
                Button(action: {
                    let titleToEdit = store.listTitles[listId] ?? currentTitle
                    onRequestEdit(listId, titleToEdit)
                }) {
                    Image(systemName: "pencil")
                        .foregroundColor(.blue)
                        .font(.system(size: 14))
                }
                .disabled(isProcessing)

                Toggle("", isOn: Binding(
                    get: {
                        let tabKey = isBlueskyFeed ?
                            "feed_\(store.accountType)_\(listId)" :
                            "list_\(store.accountType)_\(listId)"
                        let isEnabled = store.availableAppBarTabsItems.contains(where: { $0.key == tabKey })
                        logger.debug("Toggle GET: \(isBlueskyFeed ? "feed" : "list")=\(listId), isEnabled=\(isEnabled), defaultValue=\(defaultToggleValue)")
                        return isEnabled ? true : defaultToggleValue
                    },
                    set: { _ in
                        if !isProcessing {
                            isProcessing = true
                            let tabKey = isBlueskyFeed ?
                                "feed_\(store.accountType)_\(listId)" :
                                "list_\(store.accountType)_\(listId)"
                            logger.debug("Toggle SET: \(isBlueskyFeed ? "feed" : "list")=\(listId), 开始切换状态")
                            store.toggleTab(tabKey)
                            // 设置短暂延迟避免频繁操作
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                                isProcessing = false
                                logger.debug("Toggle SET: \(isBlueskyFeed ? "feed" : "list")=\(listId), 切换完成")
                            }
                        }
                    }
                ))
                //  .tint(theme.tintColor)
                .disabled(isProcessing)
                .frame(width: 44)
            }
            .padding(.leading, 8)
        }
        .onReceive(store.$listTitles) { _ in
            if let updatedTitle = store.listTitles[listId] {
                currentTitle = updatedTitle
            }
        }
    }
}
