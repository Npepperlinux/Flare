import Foundation
import os.log
import shared
import SwiftUI

// 这个地方缓存ProfilePresenter 没用的。
class ProfilePresenterWrapper: ObservableObject {
    //  - Properties
    let presenter: ProfilePresenter
//    @Published private(set) var tabStore: ProfileTabStore

    //  - Init
    init(accountType: AccountType, userKey: MicroBlogKey?) {
        os_log("[📔][ProfilePresenterWrapper - init]初始化: accountType=%{public}@, userKey=%{public}@", log: .default, type: .debug, String(describing: accountType), userKey?.description ?? "nil")

        presenter = .init(accountType: accountType, userKey: userKey)
//        self.tabStore = ProfileTabStore(accountType: accountType, userKey: userKey)
    }

    //  - Memory Management
    func handleMemoryWarning() {
        os_log("[📔][ProfilePresenterWrapper]处理内存警告", log: .default, type: .debug)
//        tabStore.handleMemoryWarning()
    }

    func handleBackground() {
        os_log("[📔][ProfilePresenterWrapper]处理后台", log: .default, type: .debug)
//        tabStore.handleBackground()
    }
}

//  - Presenter Extensions
extension ProfilePresenter {
//    var tabs: [ProfileStateTab] {
//        if case .success(let tabs) = onEnum(of: models.value.tabs) {
//            var result: [ProfileStateTab] = []
//            for i in 0..<tabs.data.size {
//                result.append(tabs.data.get(index: i))
//            }
//            os_log("[📔][ProfilePresenter]获取标签页: count=%{public}d", log: .default, type: .debug, result.count)
//            return result
//        }
//        os_log("[📔][ProfilePresenter]获取标签页: 空", log: .default, type: .debug)
//        return []
//    }

//    var mediaState: PagingState<ProfileMedia> {
//        let state = models.value.mediaState
//        os_log("[📔][ProfilePresenter]获取媒体状态: %{public}@", log: .default, type: .debug, String(describing: state))
//        return state
//    }
//
    var userState: UiState<UiProfile> {
        let state = models.value.userState
        os_log("[📔][ProfilePresenter]获取用户状态: %{public}@", log: .default, type: .debug, String(describing: state))
        return state
    }

    var relationState: UiState<UiRelation> {
        let state = models.value.relationState
        os_log("[📔][ProfilePresenter]获取关系状态: %{public}@", log: .default, type: .debug, String(describing: state))
        return state
    }

    var isMe: UiState<KotlinBoolean> {
        let state = models.value.isMe
        os_log("[📔][ProfilePresenter]获取是否是本人: %{public}@", log: .default, type: .debug, String(describing: state))
        return state
    }

    var actions: UiState<ImmutableListWrapper<ProfileAction>> {
        let state = models.value.actions
        os_log("[📔][ProfilePresenter]获取操作列表: %{public}@", log: .default, type: .debug, String(describing: state))
        return state
    }

    var isGuestMode: Bool {
        let state = models.value.isGuestMode
        os_log("[📔][ProfilePresenter]获取是否是访客模式: %{public}@", log: .default, type: .debug, String(describing: state))
        return state
    }

    var canSendMessage: UiState<KotlinBoolean> {
        let state = models.value.canSendMessage
        os_log("[📔][ProfilePresenter]获取是否可以发送消息: %{public}@", log: .default, type: .debug, String(describing: state))
        return state
    }
}
