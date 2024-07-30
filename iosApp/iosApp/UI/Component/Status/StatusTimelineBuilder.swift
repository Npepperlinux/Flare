import SwiftUI
import shared

struct StatusTimelineComponent: View {
    @Environment(\.horizontalSizeClass) private var horizontalSizeClass
    let data: UiState<LazyPagingItems<UiTimeline>>
    var body: some View {
        switch onEnum(of: data) {
        case .success(let success):
            if (success.data.loadState.refresh is Paging_commonLoadState.Loading ||
                success.data.loadState.prepend is Paging_commonLoadState.Loading) &&
                success.data.itemCount == 0 {
                ForEach(0...10, id: \.self) { _ in
                    StatusPlaceHolder()
                        .if(horizontalSizeClass != .compact) { view in
                            view.padding([.horizontal])
                        }
                }
            } else if (success.data.loadState.refresh is Paging_commonLoadState.Error ||
                       success.data.loadState.prepend is Paging_commonLoadState.Error) &&
                        success.data.itemCount == 0 {
                Text("timeline_load_error", comment: "Timeline loading error")
            } else if success.data.itemCount == 0 {
                Text("timeline_load_empty", comment: "Timeline is empty")
            } else {
                StatusTimeline(
                    pagingSource: success.data
                )
            }
        case .error(let error):
            Text("timeline_load_error", comment: "Timeline loading error")
        case .loading:
            ForEach(0...10, id: \.self) { _ in
                StatusPlaceHolder()
                    .if(horizontalSizeClass != .compact) { view in
                        view.padding([.horizontal])
                    }
            }
        }
    }
}

struct StatusTimeline: View {
    @Environment(\.openURL) private var openURL
    @Environment(\.horizontalSizeClass) private var horizontalSizeClass
    let pagingSource: LazyPagingItems<UiTimeline>
    var body: some View {
        if (pagingSource.loadState.refresh is Paging_commonLoadState.Loading ||
            pagingSource.loadState.prepend is Paging_commonLoadState.Loading) &&
            pagingSource.itemCount == 0 {
            ForEach(0...10, id: \.self) { _ in
                StatusPlaceHolder()
                    .if(horizontalSizeClass != .compact) { view in
                        view.padding([.horizontal])
                    }
            }
        } else if (pagingSource.loadState.refresh is Paging_commonLoadState.Error ||
                   pagingSource.loadState.prepend is Paging_commonLoadState.Error) &&
                    pagingSource.itemCount == 0 {
            Text("timeline_load_error", comment: "Timeline loading error")
        } else if pagingSource.itemCount == 0 {
            Text("timeline_load_empty", comment: "Timeline is empty")
        } else {
            ForEach(1...pagingSource.itemCount, id: \.self) { index in
                let data = pagingSource.peek(index: Int32(index - 1))
                VStack {
                    if let status = data {
                        StatusItemView(
                            data: status
                        )
                    } else {
                        StatusPlaceHolder()
                    }
                }
                .onAppear {
                    pagingSource.get(index: index - 1)
                }
                .if(horizontalSizeClass != .compact) { view in
                    view.padding([.horizontal])
                }
            }
        }
    }
}

struct StatusItemView: View {
    @Environment(\.openURL) private var openURL
    let data: UiTimeline
    var body: some View {
        if let topMessage = data.topMessage {
            let icon = switch topMessage.icon {
            case .retweet: "arrow.left.arrow.right"
            case .follow: "person.badge.plus"
            case .favourite: "star"
            case .mention: "at"
            case .poll: "list.bullet"
            case .edit: "pencil"
            case .info: "app"
            case .reply: "arrowshape.turn.up.left"
            }
            let text = switch onEnum(of: topMessage.type) {
            case .bluesky(let data):
                switch onEnum(of: data) {
                case .follow(_): String(localized: "bluesky_notification_follow")
                case .like(_): String(localized: "bluesky_notification_like")
                case .mention(_): String(localized: "bluesky_notification_mention")
                case .quote(_): String(localized: "bluesky_notification_quote")
                case .reply(_): String(localized: "bluesky_notification_reply")
                case .repost(_): String(localized: "bluesky_notification_repost")
                }
            case .mastodon(let data):
                switch onEnum(of: data) {
                case .favourite(_): String(localized: "mastodon_notification_favourite")
                case .follow(_): String(localized: "mastodon_notification_follow")
                case .followRequest(_): String(localized: "mastodon_notification_follow_request")
                case .mention(_): String(localized: "mastodon_notification_mention")
                case .poll(_): String(localized: "mastodon_notification_poll")
                case .reblogged(_): String(localized: "mastodon_notification_reblog")
                case .status(_): String(localized: "mastodon_notification_status")
                case .update(_): String(localized: "mastodon_notification_update")
                }
            case .misskey(let data):
                switch onEnum(of: data) {
                case .achievementEarned(_):  String(localized: "misskey_notification_achievement_earned")
                case .app(_): String(localized: "misskey_notification_app")
                case .follow(_): String(localized: "misskey_notification_follow")
                case .followRequestAccepted(_): String(localized: "misskey_notification_follow_request_accepted")
                case .mention(_): String(localized: "misskey_notification_mention")
                case .pollEnded(_): String(localized: "misskey_notification_poll_ended")
                case .quote(_): String(localized: "misskey_notification_quote")
                case .reaction(_): String(localized: "misskey_notification_reaction")
                case .receiveFollowRequest(_): String(localized: "misskey_notification_receive_follow_request")
                case .renote(_): String(localized: "misskey_notification_renote")
                case .reply(_): String(localized: "misskey_notification_reply")
                }
            case .vVO(let data):
                switch onEnum(of: data) {
                case .custom(let message): message.message
                case .like(_): String(localized: "vvo_notification_like")
                }
            case .xQT(let data):
                switch onEnum(of: data) {
                case .custom(let message): message.message
                case .mention(_): String(localized: "xqt_notification_mention")
                case .retweet(_): String(localized: "xqt_notification_retweet")
                }
            }
            StatusRetweetHeaderComponent(iconSystemName: icon, nameMarkdown: topMessage.user?.name.markdown, text: text)
                .onTapGesture {
                    topMessage.user?.onClicked(.init(launcher: AppleUriLauncher(openURL: openURL)))
                }
        }
        if let content = data.content {
            switch onEnum(of: content) {
            case .status(let data): CommonStatusComponent(
                data: data,
                onMediaClick: { index, preview in
                    
                }
            ).onTapGesture {
                data.onClicked(.init(launcher: AppleUriLauncher(openURL: openURL)))
            }
            case .user(let data):
                UserComponent(
                    user: data.value,
                    onUserClicked: {
                        data.value.onClicked(.init(launcher: AppleUriLauncher(openURL: openURL)))
                    }
                )
            case .userList(let data): EmptyView()
            }
        }
        EmptyView()
    }
}

struct StatusPlaceHolder: View {
    var body: some View {
        StatusItemView(
            data: createSampleStatus(
                user: createSampleUser()
            )
        )
        .redacted(reason: .placeholder)
    }
}
