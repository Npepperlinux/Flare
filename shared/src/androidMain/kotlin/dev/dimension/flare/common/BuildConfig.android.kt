package dev.dimension.flare.common


internal actual object BuildConfig {
    actual val debug: Boolean
        get() = dev.dimension.flare.shared.BuildConfig.DEBUG
}