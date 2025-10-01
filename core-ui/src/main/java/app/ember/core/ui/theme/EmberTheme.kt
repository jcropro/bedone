package app.ember.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Deterministic Material 3 color/shape system for Ember.
 * Keep this file clean and free of preview-only dependencies.
 */

private val Seed = Color(0xFFFF7A1A) // Ember orange

private val LightColors: ColorScheme = lightColorScheme(
    primary = Seed,
    onPrimary = Color.Black,
    secondary = Color(0xFF7B5CE6),
    onSecondary = Color.White,
    tertiary = Color(0xFF00D8FF),
    onTertiary = Color.Black,
    background = Color(0xFFF8F9FC),
    onBackground = Color(0xFF0F0F14),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F0F14),
    surfaceVariant = Color(0xFFECEEF5),
    onSurfaceVariant = Color(0xFF2E3140),
    outline = Color(0xFFB8BCCD)
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Seed,
    onPrimary = Color.Black,
    secondary = Color(0xFF8F7BFF),
    onSecondary = Color.Black,
    tertiary = Color(0xFF20E0FF),
    onTertiary = Color.Black,
    background = Color(0xFF0F0F14),
    onBackground = Color(0xFFECEEF5),
    surface = Color(0xFF12131A),
    onSurface = Color(0xFFECEEF5),
    surfaceVariant = Color(0xFF1C1E2A),
    onSurfaceVariant = Color(0xFFB8BCCD),
    outline = Color(0xFF3E4255)
)

val EmberShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/** App theme with selectable options + dark/light toggle */
@Composable
fun EmberAudioPlayerTheme(
    themeState: ThemeUiState,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val baseScheme: ColorScheme = when {
        themeState.useDynamicColor && Build.VERSION.SDK_INT >= 31 ->
            if (themeState.useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        else -> themeState.colorScheme
    }
    val appliedScheme = if (themeState.useAmoledBlack && themeState.useDarkTheme) {
        baseScheme.copy(
            background = Color.Black,
            surface = Color.Black,
            surfaceVariant = Color.Black
        )
    } else baseScheme
    MaterialTheme(
        colorScheme = appliedScheme,
        typography = EmberTypography,
        shapes = EmberShapes,
        content = content
    )
}

/** Simple overload for previews/one-offs that only care about dark/light. */
@Composable
fun EmberTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = scheme,
        typography = EmberTypography,
        shapes = EmberShapes,
        content = content
    )
}

@Immutable
data class ThemeOption(
    val id: String,
    val labelRes: Int,
    val lightScheme: ColorScheme,
    val darkScheme: ColorScheme
) {
    fun colorScheme(useDarkTheme: Boolean): ColorScheme =
        if (useDarkTheme) darkScheme else lightScheme
}

@Immutable
data class ThemeUiState(
    val options: List<ThemeOption> = defaultThemeOptions(),
    val selectedOptionIndex: Int = 0,
    val useDarkTheme: Boolean = true,
    val useDynamicColor: Boolean = false,
    val useAmoledBlack: Boolean = false
) {
    init {
        require(options.isNotEmpty()) { "ThemeUiState requires at least one theme option." }
    }

    val selectedOption: ThemeOption
        get() = options[selectedOptionIndex.coerceIn(0, options.lastIndex)]

    val colorScheme: ColorScheme
        get() = selectedOption.colorScheme(useDarkTheme)

    fun withDarkTheme(enabled: Boolean): ThemeUiState =
        if (useDarkTheme == enabled) this else copy(useDarkTheme = enabled)

    fun withSelectedOption(index: Int): ThemeUiState {
        val clampedIndex = index.coerceIn(0, options.lastIndex)
        return if (clampedIndex == selectedOptionIndex && index == selectedOptionIndex) {
            this
        } else {
            copy(selectedOptionIndex = clampedIndex)
        }
    }

    fun withDynamicColor(enabled: Boolean): ThemeUiState =
        if (useDynamicColor == enabled) this else copy(useDynamicColor = enabled)

    fun withAmoledBlack(enabled: Boolean): ThemeUiState =
        if (useAmoledBlack == enabled) this else copy(useAmoledBlack = enabled)
}

fun defaultThemeOptions(): List<ThemeOption> = listOf(
    ThemeOption(
        id = "ember-classic",
        labelRes = app.ember.core.ui.R.string.theme_option_ember,
        lightScheme = LightColors,
        darkScheme = DarkColors
    ),
    ThemeOption(
        id = "night-copper",
        labelRes = app.ember.core.ui.R.string.theme_option_night,
        lightScheme = LightColors.copy(primary = Color(0xFFFF8B42)),
        darkScheme = DarkColors.copy(primary = Color(0xFFFF8B42))
    ),
    ThemeOption(
        id = "mono-graphite",
        labelRes = app.ember.core.ui.R.string.theme_option_graphite,
        lightScheme = LightColors.copy(
            primary = Color(0xFF2E2E2E),
            secondary = Color(0xFF888888),
            tertiary = Color(0xFFBDBDBD)
        ),
        darkScheme = DarkColors.copy(
            primary = Color(0xFFEAEAEA),
            secondary = Color(0xFFBDBDBD),
            tertiary = Color(0xFF8E8E8E)
        )
    )
)
