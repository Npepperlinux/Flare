package dev.dimension.flare.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.materialkolor.rememberDynamicColorScheme
import dev.dimension.flare.data.model.LocalAppearanceSettings
import dev.dimension.flare.data.model.Theme

private val DarkColorScheme =
    darkColorScheme()

private val LightColorScheme =
    lightColorScheme()

@Composable
fun FlareTheme(
    darkTheme: Boolean = isDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = LocalAppearanceSettings.current.dynamicTheme,
    content: @Composable () -> Unit,
) {
    val seed = Color(LocalAppearanceSettings.current.colorSeed)
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> rememberDynamicColorScheme(seed, true)
            else -> rememberDynamicColorScheme(seed, false)
        }
    val view = LocalView.current
    if (!view.isInEditMode && view.context is Activity) {
        SideEffect {
            val window = (view.context as Activity).window
//            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            content.invoke()
        },
    )
}

@Composable
private fun isDarkTheme(): Boolean =
    LocalAppearanceSettings.current.theme == Theme.DARK ||
        (LocalAppearanceSettings.current.theme == Theme.SYSTEM && isSystemInDarkTheme())
