import Foundation

struct AppearanceSettings: Codable, Changeable {
    var theme: Theme = Theme.auto
    var avatarShape: AvatarShape = AvatarShape.circle
    var showActions: Bool = true
    var showNumbers: Bool = true
    var showLinkPreview: Bool = true
    var showMedia: Bool = true
    var showSensitiveContent: Bool = false
    var swipeGestures: Bool = false
    var mastodon: Mastodon = Mastodon()
    var misskey: Misskey = Misskey()
    var bluesky: Bluesky = Bluesky()
    struct Mastodon: Codable, Changeable {
        var showVisibility: Bool = true
        var swipeLeft: SwipeActions = SwipeActions.reply
        var swipeRight: SwipeActions = SwipeActions.none
        enum SwipeActions: Codable {
            case none
            case reply
            case reblog
            case favourite
            case bookmark
        }
    }
    struct Misskey: Codable, Changeable {
        var showVisibility: Bool = true
        var showReaction: Bool = true
        var swipeLeft: SwipeActions = SwipeActions.reply
        var swipeRight: SwipeActions = SwipeActions.none
        enum SwipeActions: Codable {
            case none
            case reply
            case renote
            case favourite
        }
    }
    struct Bluesky: Codable, Changeable {
        var swipeLeft: SwipeActions = SwipeActions.reply
        var swipeRight: SwipeActions = SwipeActions.none
        enum SwipeActions: Codable {
            case none
            case reply
            case reblog
            case favourite
        }
    }
}

enum Theme: Codable {
    case auto
    case light
    case dark
}

enum AvatarShape: Codable {
    case circle
    case square
}

protocol Changeable {}

extension Changeable {
    func changing<T>(path: WritableKeyPath<Self, T>, to value: T) -> Self {
        var clone = self
        clone[keyPath: path] = value
        return clone
    }
}
