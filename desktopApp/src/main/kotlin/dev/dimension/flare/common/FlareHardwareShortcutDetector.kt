package dev.dimension.flare.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isAltPressed
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.isShiftPressed
import androidx.compose.ui.util.fastFold
import androidx.compose.ui.util.fastForEach
import me.saket.telephoto.zoomable.HardwareShortcutDetector
import me.saket.telephoto.zoomable.HardwareShortcutDetector.ShortcutEvent
import me.saket.telephoto.zoomable.HardwareShortcutDetector.ShortcutEvent.PanDirection
import me.saket.telephoto.zoomable.HardwareShortcutDetector.ShortcutEvent.ZoomDirection
import org.apache.commons.lang3.SystemUtils
import kotlin.math.absoluteValue

internal object FlareHardwareShortcutDetector : HardwareShortcutDetector {
    override fun detectKey(event: KeyEvent): ShortcutEvent? {
        // Note for self: Some devices/peripherals have dedicated zoom buttons that map to Key.ZoomIn
        // and Key.ZoomOut. Examples include: Samsung Galaxy Camera, a motorcycle handlebar controller.
        if (event.key == Key.ZoomIn || event.isZoomInEvent()) {
            return ShortcutEvent.Zoom(ZoomDirection.In)
        } else if (event.key == Key.ZoomOut || (event.isZoomOutEvent())) {
            return ShortcutEvent.Zoom(ZoomDirection.Out)
        }

        val panDirection =
            when (event.key) {
                Key.DirectionUp -> PanDirection.Up
                Key.DirectionDown -> PanDirection.Down
                Key.DirectionLeft -> PanDirection.Left
                Key.DirectionRight -> PanDirection.Right
                else -> null
            }
        return when (panDirection) {
            null -> null
            else ->
                ShortcutEvent.Pan(
                    direction = panDirection,
                    panOffset = ShortcutEvent.DefaultPanOffset * if (event.isAltPressed) 10f else 1f,
                )
        }
    }

    private fun KeyEvent.isZoomInEvent(): Boolean = this.key == Key.Equals && isCtrlPressed()

    private fun KeyEvent.isZoomOutEvent(): Boolean = key == Key.Minus && isCtrlPressed()

    private fun KeyEvent.isCtrlPressed(): Boolean =
        if (SystemUtils.IS_OS_MAC_OSX) {
            isMetaPressed
        } else {
            isCtrlPressed
        }

    override fun detectScroll(event: PointerEvent): ShortcutEvent? {
        if (!event.isZoomEvent()) {
            val scroll = event.calculateScroll()
            return ShortcutEvent.Pan(
                direction =
                    when {
                        event.keyboardModifiers.isShiftPressed && scroll.y != 0f -> {
                            if (scroll.y < 0f) PanDirection.Left else PanDirection.Right
                        }
                        else -> {
                            if (scroll.x == 0f) {
                                if (scroll.y < 0f) PanDirection.Up else PanDirection.Down
                            } else {
                                if (scroll.x < 0f) PanDirection.Left else PanDirection.Right
                            }
                        }
                    },
                panOffset = ShortcutEvent.DefaultPanOffset * if (event.keyboardModifiers.isAltPressed) 10f else 1f,
            )
        } else {
            return when (val scrollY = event.calculateScroll().y) {
                0f -> null
                else ->
                    ShortcutEvent.Zoom(
                        direction = if (scrollY < 0f) ZoomDirection.In else ZoomDirection.Out,
                        centroid = event.calculateScrollCentroid(),
                        // Deltas observed on various platforms and mice:
                        // Android:
                        //   Logitech MX: -1.0 / +1.0
                        // macOS:
                        //   Logitech MX: -1.2 / +1.3
                        //   MacBook trackpad: -0.1 / 0.1
                        zoomFactor = (ShortcutEvent.DefaultZoomFactor / 2f) * scrollY.absoluteValue,
                    )
            }
        }
    }

    private fun PointerEvent.isZoomEvent(): Boolean =
        if (SystemUtils.IS_OS_MAC_OSX) {
            keyboardModifiers.isMetaPressed
        } else {
            keyboardModifiers.isCtrlPressed
        }

    private fun PointerEvent.calculateScroll(): Offset =
        changes.fastFold(Offset.Zero) { acc, c ->
            acc + c.scrollDelta
        }

    private fun PointerEvent.calculateScrollCentroid(): Offset {
        check(type == PointerEventType.Scroll)
        var centroid = Offset.Zero
        var centroidWeight = 0f
        changes.fastForEach { change ->
            val position = change.position
            centroid += position
            centroidWeight++
        }
        return when (centroidWeight) {
            0f -> Offset.Unspecified
            else -> centroid / centroidWeight
        }
    }
}
