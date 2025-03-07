import os
import SwiftUI

struct FLNewMenuGestureModifier: ViewModifier {
    @ObservedObject var appState: FLNewAppState
    @State private var currentAppBarIndex: Int = 0

    // 添加判断向右滑动的方法
    private func isValidRightSwipe(_ value: DragGesture.Value) -> Bool {
        let translation = value.translation
        let distance = sqrt(pow(translation.width, 2) + pow(translation.height, 2))
        guard distance > 0 else { return false }

        // 计算方向向量，判断是否向右滑动（允许一定角度的偏差）
        let directionVector = (
            x: translation.width / distance,
            y: translation.height / distance
        )
        return directionVector.x > 0.7 // cos 45° ≈ 0.7
    }

    init(appState: FLNewAppState) {
        self.appState = appState
    }

    func body(content: Content) -> some View {
        content.simultaneousGesture(
            DragGesture(minimumDistance: 10, coordinateSpace: .local)
                .onChanged { value in

                    // 在第一个 tab 时才处理菜单手势
                    if currentAppBarIndex > 0 {
//                        os_log("[🖐️][GestureModifier] Drag ignored - not first appbar item",
//                               log: .default, type: .debug)
                        return
                    }

                    // 检查是否是向右滑动
                    guard isValidRightSwipe(value) else {
//                        os_log("[🖐️][GestureModifier] Drag ignored - not right direction",
//                               log: .default, type: .debug)
                        return
                    }

                    handleDragChange(value)
                }
                .onEnded { value in

                    // 在第一个 tab 时才处理菜单手势
                    if currentAppBarIndex > 0 {
                        return
                    }

                    // 检查是否是向右滑动
                    guard isValidRightSwipe(value) else {
//                        os_log("[🖐️][GestureModifier] Drag end ignored - not right direction",
//                               log: .default, type: .debug)
                        return
                    }

                    handleDragEnd(value)
                }
        )
        .onReceive(NotificationCenter.default.publisher(for: NSNotification.Name("AppBarIndexDidChange"))) { notification in
            if let index = notification.object as? Int {
                currentAppBarIndex = index
                os_log("[🖐️][GestureModifier] AppBar index updated: %{public}d",
                       log: .default, type: .debug, index)
            }
        }
    }

    private func handleDragChange(_ value: DragGesture.Value) {
        guard appState.gestureState.isGestureEnabled else {
            os_log("[🖐️][GestureModifier] Gesture not enabled", log: .default, type: .debug)
            return
        }

        let translation = value.translation
        let velocity = value.predictedEndTranslation.width - value.translation.width

        os_log("[🖐️][GestureModifier] Processing drag - Translation: %{public}f, Velocity: %{public}f",
               log: .default, type: .debug,
               translation.width, velocity)

        if translation.width > 0 {
            withAnimation(.spring()) {
                appState.isMenuOpen = true
            }
        }
    }

    private func handleDragEnd(_ value: DragGesture.Value) {
        let translation = value.translation.width
        let velocity = value.predictedEndTranslation.width - value.translation.width

        os_log("[🖐️][GestureModifier] Processing drag end - Translation: %{public}f, Velocity: %{public}f",
               log: .default, type: .debug,
               translation, velocity)

        withAnimation(.spring()) {
            if translation > UIScreen.main.bounds.width * 0.3 || velocity > 300 {
                os_log("[🖐️][GestureModifier] Opening menu", log: .default, type: .debug)
                appState.isMenuOpen = true
            } else {
                os_log("[🖐️][GestureModifier] Closing menu", log: .default, type: .debug)
                appState.isMenuOpen = false
            }
        }
    }
}

// - View Extension
extension View {
    func newMenuGesture(appState: FLNewAppState) -> some View {
        modifier(FLNewMenuGestureModifier(appState: appState))
    }
}

// - GeometryProxy Extension
private extension GeometryProxy {
    var uiView: UIView? {
        let mirror = Mirror(reflecting: self)
        for child in mirror.children {
            if let view = child.value as? UIView {
                return view
            }
        }
        return nil
    }
}
