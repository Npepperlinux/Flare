import SwiftUI
import shared

struct VVOStatusDetailScreen: View {
    @State private var presenter: VVOStatusDetailPresenter
    @Environment(\.horizontalSizeClass) private var horizontalSizeClass
    @State private var type: DetailStatusType = .comment
    private let statusKey: MicroBlogKey
    init(accountType: AccountType, statusKey: MicroBlogKey) {
        presenter = .init(accountType: accountType, statusKey: statusKey)
        self.statusKey = statusKey
    }
    var statusView: some View {
        Observing(presenter.models) { state in
            switch onEnum(of: state.status) {
            case .success(let data): StatusItemView(data: data.data, detailKey: statusKey)
            case .loading: StatusPlaceHolder()
            case .error: EmptyView()
            }
        }
    }
    var body: some View {
        Observing(presenter.models) { state in
            HStack {
                if horizontalSizeClass != .compact {
                    statusView
                }
                List {
                    if horizontalSizeClass == .compact {
                        statusView
                        Picker("notification_type", selection: $type) {
                            Text("status_detail_repost")
                                .tag(DetailStatusType.repost)
                            Text("status_detail_comment")
                                .tag(DetailStatusType.comment)
                        }
                        .pickerStyle(.segmented)
                        .listRowSeparator(.hidden)
                    }
                    switch type {
                    case .comment:
                        StatusTimelineComponent(data: state.comment, detailKey: nil)
                    case .repost:
                        StatusTimelineComponent(data: state.repost, detailKey: nil)
                    }
                }
                .listStyle(.plain)
            }
        }
        .navigationTitle("status_detail")
#if os(iOS)
        .navigationBarTitleDisplayMode(.inline)
#else
        .toolbar {
            ToolbarItem(placement: .confirmationAction) {
                Button(action: {
                    Task {
                        try? await viewModel.model.refresh()
                    }
                }, label: {
                    Image(systemName: "arrow.clockwise.circle")
                })
            }
        }
#endif
        .toolbar {
            if horizontalSizeClass != .compact {
                ToolbarItem(placement: .primaryAction) {
                    Picker("notification_type", selection: $type) {
                        Text("status_detail_repost")
                            .tag(DetailStatusType.repost)
                        Text("status_detail_comment")
                            .tag(DetailStatusType.comment)
                    }
                    .pickerStyle(.segmented)
                }
            }
        }
    }
}

enum DetailStatusType {
    case comment
    case repost
}
