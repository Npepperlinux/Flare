import Foundation
import SwiftUI
import shared

struct RouterView : View {
    @Bindable var sheetRouter = Router<SheetDestination>()
    var body: some View {
        SplashScreen { type in
            ZStack {
                switch type {
                case .home:
                    HomeScreen()
                case .login:
                    Text("Flare")
                case .splash:
                    Text("Flare")
                }
            }.sheet(isPresented: Binding(get: {
                type == .login
            }, set: { Value in
                
            }), content: {
                if type == .login {
                    NavigationStack(path: $sheetRouter.navPath) {
                        ServiceSelectScreen(
                            toHome: {
                                
                            }
                        )
                        .withSheetRouter {
                            sheetRouter.clearBackStack()
                        }

                    }
                    .interactiveDismissDisabled()
                }
            })
        }
    }
}

@Observable
class RouterViewModel : MoleculeViewModelProto {
    let presenter: SplashPresenter
    var model: __SplashType
    typealias Model = __SplashType
    typealias Presenter = SplashPresenter
    
    init() {
        presenter = SplashPresenter(toHome: {}, toLogin: {})
        model = presenter.models.value
    }
}

public enum TabDestination: Codable, Hashable {
    case profile(userKey: String)
    case statusDetail(statusKey: String)
    case profileWithUserNameAndHost(userName: String, host: String)
    case search(q: String)
}

public enum SheetDestination: Codable, Hashable {
    case settings
    case accountSettings
    case serviceSelection
}

@Observable
final class Router<T: Hashable>: ObservableObject {
    
    var navPath = NavigationPath()
    
    func navigate(to destination: T) {
        navPath.append(destination)
    }
    
    func navigateBack(count: Int = 1) {
        navPath.removeLast(count)
    }
    
    func navigateToRoot() {
        navPath.removeLast(navPath.count)
    }
    
    func clearBackStack() {
        navPath = NavigationPath()
    }
}

@MainActor
extension View {
    func withTabRouter() -> some View {
        navigationDestination(
            for: TabDestination.self
        ) { destination in
            switch destination {
            case let .profile(userKey):
                ProfileScreen(userKey: MicroBlogKey.companion.valueOf(str: userKey))
            case let .statusDetail(statusKey):
                Text("todo")
            case let .profileWithUserNameAndHost(userName, host):
                Text("todo")
            case let .search(data):
                Text("todo")
            }
        }
    }
    
    func withSheetRouter(toHome:@escaping () -> Void) -> some View {
        navigationDestination(for: SheetDestination.self) { destination in
            switch destination {
            case .serviceSelection:
                ServiceSelectScreen(toHome: toHome)
            case .settings:
                SettingsScreen()
            case .accountSettings:
                AccountsScreen()
            }
        }
    }
}
