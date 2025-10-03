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
import app.ember.core.ui.design.*
import app.ember.core.ui.R

/**
 * Ember Theme System - Token-driven Material 3 implementation
 * 
 * This theme system uses the canonical design tokens from Tokens.kt
 * and follows the Golden Blueprint specifications exactly.
 */

// ============================================================================
// COLOR SCHEMES (using canonical tokens)
// ============================================================================

private val LightColors: ColorScheme = lightColorScheme(
    primary = EmberFlame,
    onPrimary = Color.Black,
    secondary = Color(0xFF7B5CE6), // Purple accent
    onSecondary = Color.White,
    tertiary = AccentIce,
    onTertiary = Color.Black,
    background = Color(0xFFF8F9FC), // Light background
    onBackground = EmberInk,
    surface = Color.White,
    onSurface = EmberInk,
    surfaceVariant = Color(0xFFECEEF5),
    onSurfaceVariant = Color(0xFF2E3140),
    outline = TextMuted,
    error = Error,
    onError = Color.White
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = EmberFlame,
    onPrimary = Color.Black,
    secondary = Color(0xFF8F7BFF), // Purple accent
    onSecondary = Color.Black,
    tertiary = AccentIce,
    onTertiary = Color.Black,
    background = EmberInk,
    onBackground = TextStrong,
    surface = EmberInk2,
    onSurface = TextStrong,
    surfaceVariant = EmberCard,
    onSurfaceVariant = TextMuted,
    outline = Color(0xFF3E4255),
    error = Error,
    onError = Color.White
)

// ============================================================================
// SHAPES (using canonical radii)
// ============================================================================

val EmberShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(RadiusSM),
    small = RoundedCornerShape(RadiusMD),
    medium = RoundedCornerShape(RadiusLG),
    large = RoundedCornerShape(RadiusXL),
    extraLarge = RoundedCornerShape(RadiusXL)
)

// ============================================================================
// THEME COMPOSABLE
// ============================================================================

/** Main theme composable with token-driven colors */
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
    } else {
        baseScheme
    }

    MaterialTheme(
        colorScheme = appliedScheme,
        typography = EmberTypography,
        shapes = EmberShapes,
        content = content
    )
}

/** Simple theme composable for basic usage */
@Composable
fun EmberTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColors else LightColors
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = EmberTypography,
        shapes = EmberShapes,
        content = content
    )
}

// ============================================================================
// THEME STATE MANAGEMENT
// ============================================================================

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

@Immutable
data class ThemeOption(
    val labelRes: Int,
    val colorScheme: (Boolean) -> ColorScheme
)

fun defaultThemeOptions(): List<ThemeOption> = listOf(
    // 1. Ember (Default) - The signature Ember theme
    ThemeOption(
        labelRes = R.string.theme_ember,
        colorScheme = { useDarkTheme -> if (useDarkTheme) DarkColors else LightColors }
    ),
    
    // 2. Classic - Traditional Material Design colors
    ThemeOption(
        labelRes = R.string.theme_classic,
        colorScheme = { useDarkTheme -> 
            if (useDarkTheme) {
                darkColorScheme(
                    primary = Color(0xFF2196F3),
                    onPrimary = Color.Black,
                    secondary = Color(0xFF03DAC6),
                    onSecondary = Color.Black,
                    tertiary = Color(0xFFBB86FC),
                    onTertiary = Color.Black,
                    background = Color(0xFF121212),
                    onBackground = Color(0xFFE1E1E1),
                    surface = Color(0xFF1E1E1E),
                    onSurface = Color(0xFFE1E1E1),
                    surfaceVariant = Color(0xFF2C2C2C),
                    onSurfaceVariant = Color(0xFFB3B3B3),
                    outline = Color(0xFF4A4A4A),
                    error = Color(0xFFCF6679),
                    onError = Color.Black
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF1976D2),
                    onPrimary = Color.White,
                    secondary = Color(0xFF03DAC6),
                    onSecondary = Color.Black,
                    tertiary = Color(0xFF6200EE),
                    onTertiary = Color.White,
                    background = Color(0xFFFAFAFA),
                    onBackground = Color(0xFF1C1B1F),
                    surface = Color.White,
                    onSurface = Color(0xFF1C1B1F),
                    surfaceVariant = Color(0xFFE7E0EC),
                    onSurfaceVariant = Color(0xFF49454F),
                    outline = Color(0xFF79747E),
                    error = Color(0xFFBA1A1A),
                    onError = Color.White
                )
            }
        }
    ),
    
    // 3. Minimal - Clean, monochromatic design
    ThemeOption(
        labelRes = R.string.theme_minimal,
        colorScheme = { useDarkTheme -> 
            if (useDarkTheme) {
                darkColorScheme(
                    primary = Color(0xFFE0E0E0),
                    onPrimary = Color.Black,
                    secondary = Color(0xFFB0B0B0),
                    onSecondary = Color.Black,
                    tertiary = Color(0xFF909090),
                    onTertiary = Color.Black,
                    background = Color(0xFF000000),
                    onBackground = Color(0xFFE0E0E0),
                    surface = Color(0xFF0A0A0A),
                    onSurface = Color(0xFFE0E0E0),
                    surfaceVariant = Color(0xFF1A1A1A),
                    onSurfaceVariant = Color(0xFFB0B0B0),
                    outline = Color(0xFF404040),
                    error = Color(0xFFCF6679),
                    onError = Color.Black
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF424242),
                    onPrimary = Color.White,
                    secondary = Color(0xFF616161),
                    onSecondary = Color.White,
                    tertiary = Color(0xFF757575),
                    onTertiary = Color.White,
                    background = Color(0xFFFFFFFF),
                    onBackground = Color(0xFF1C1B1F),
                    surface = Color(0xFFFAFAFA),
                    onSurface = Color(0xFF1C1B1F),
                    surfaceVariant = Color(0xFFF5F5F5),
                    onSurfaceVariant = Color(0xFF49454F),
                    outline = Color(0xFF79747E),
                    error = Color(0xFFBA1A1A),
                    onError = Color.White
                )
            }
        }
    ),
    
    // 4. Ocean - Deep blue oceanic theme
    ThemeOption(
        labelRes = R.string.theme_ocean,
        colorScheme = { useDarkTheme -> 
            if (useDarkTheme) {
                darkColorScheme(
                    primary = Color(0xFF4FC3F7),
                    onPrimary = Color.Black,
                    secondary = Color(0xFF26C6DA),
                    onSecondary = Color.Black,
                    tertiary = Color(0xFF29B6F6),
                    onTertiary = Color.Black,
                    background = Color(0xFF0D1B2A),
                    onBackground = Color(0xFFE1F5FE),
                    surface = Color(0xFF1A2B3A),
                    onSurface = Color(0xFFE1F5FE),
                    surfaceVariant = Color(0xFF2A3B4A),
                    onSurfaceVariant = Color(0xFFB3E5FC),
                    outline = Color(0xFF4A5B6A),
                    error = Color(0xFFCF6679),
                    onError = Color.Black
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF0277BD),
                    onPrimary = Color.White,
                    secondary = Color(0xFF00ACC1),
                    onSecondary = Color.White,
                    tertiary = Color(0xFF0288D1),
                    onTertiary = Color.White,
                    background = Color(0xFFF8FDFF),
                    onBackground = Color(0xFF1C1B1F),
                    surface = Color.White,
                    onSurface = Color(0xFF1C1B1F),
                    surfaceVariant = Color(0xFFE0F2F1),
                    onSurfaceVariant = Color(0xFF49454F),
                    outline = Color(0xFF79747E),
                    error = Color(0xFFBA1A1A),
                    onError = Color.White
                )
            }
        }
    ),
    
    // 5. Forest - Natural green theme
    ThemeOption(
        labelRes = R.string.theme_forest,
        colorScheme = { useDarkTheme -> 
            if (useDarkTheme) {
                darkColorScheme(
                    primary = Color(0xFF81C784),
                    onPrimary = Color.Black,
                    secondary = Color(0xFF66BB6A),
                    onSecondary = Color.Black,
                    tertiary = Color(0xFF4CAF50),
                    onTertiary = Color.Black,
                    background = Color(0xFF0D1B0A),
                    onBackground = Color(0xFFE8F5E8),
                    surface = Color(0xFF1A2B1A),
                    onSurface = Color(0xFFE8F5E8),
                    surfaceVariant = Color(0xFF2A3B2A),
                    onSurfaceVariant = Color(0xFFB8E6B8),
                    outline = Color(0xFF4A5B4A),
                    error = Color(0xFFCF6679),
                    onError = Color.Black
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF2E7D32),
                    onPrimary = Color.White,
                    secondary = Color(0xFF388E3C),
                    onSecondary = Color.White,
                    tertiary = Color(0xFF43A047),
                    onTertiary = Color.White,
                    background = Color(0xFFF8FFF8),
                    onBackground = Color(0xFF1C1B1F),
                    surface = Color.White,
                    onSurface = Color(0xFF1C1B1F),
                    surfaceVariant = Color(0xFFE8F5E8),
                    onSurfaceVariant = Color(0xFF49454F),
                    outline = Color(0xFF79747E),
                    error = Color(0xFFBA1A1A),
                    onError = Color.White
                )
            }
        }
    ),
    
    // 6. Sunset - Warm orange and pink theme
    ThemeOption(
        labelRes = R.string.theme_sunset,
        colorScheme = { useDarkTheme -> 
            if (useDarkTheme) {
                darkColorScheme(
                    primary = Color(0xFFFF8A65),
                    onPrimary = Color.Black,
                    secondary = Color(0xFFFF7043),
                    onSecondary = Color.Black,
                    tertiary = Color(0xFFFF5722),
                    onTertiary = Color.Black,
                    background = Color(0xFF2A0D0A),
                    onBackground = Color(0xFFFFF3E0),
                    surface = Color(0xFF3A1D1A),
                    onSurface = Color(0xFFFFF3E0),
                    surfaceVariant = Color(0xFF4A2D2A),
                    onSurfaceVariant = Color(0xFFFFCCBC),
                    outline = Color(0xFF6A3D3A),
                    error = Color(0xFFCF6679),
                    onError = Color.Black
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFFE64A19),
                    onPrimary = Color.White,
                    secondary = Color(0xFFF57C00),
                    onSecondary = Color.White,
                    tertiary = Color(0xFFFF5722),
                    onTertiary = Color.White,
                    background = Color(0xFFFFF8F5),
                    onBackground = Color(0xFF1C1B1F),
                    surface = Color.White,
                    onSurface = Color(0xFF1C1B1F),
                    surfaceVariant = Color(0xFFFFF3E0),
                    onSurfaceVariant = Color(0xFF49454F),
                    outline = Color(0xFF79747E),
                    error = Color(0xFFBA1A1A),
                    onError = Color.White
                )
            }
        }
    ),
    
    // 7. Lavender - Soft purple theme
    ThemeOption(
        labelRes = R.string.theme_lavender,
        colorScheme = { useDarkTheme -> 
            if (useDarkTheme) {
                darkColorScheme(
                    primary = Color(0xFFBA68C8),
                    onPrimary = Color.Black,
                    secondary = Color(0xFFAB47BC),
                    onSecondary = Color.Black,
                    tertiary = Color(0xFF9C27B0),
                    onTertiary = Color.Black,
                    background = Color(0xFF1A0D1A),
                    onBackground = Color(0xFFF3E5F5),
                    surface = Color(0xFF2A1D2A),
                    onSurface = Color(0xFFF3E5F5),
                    surfaceVariant = Color(0xFF3A2D3A),
                    onSurfaceVariant = Color(0xFFE1BEE7),
                    outline = Color(0xFF5A4D5A),
                    error = Color(0xFFCF6679),
                    onError = Color.Black
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF7B1FA2),
                    onPrimary = Color.White,
                    secondary = Color(0xFF8E24AA),
                    onSecondary = Color.White,
                    tertiary = Color(0xFF9C27B0),
                    onTertiary = Color.White,
                    background = Color(0xFFFDF7FF),
                    onBackground = Color(0xFF1C1B1F),
                    surface = Color.White,
                    onSurface = Color(0xFF1C1B1F),
                    surfaceVariant = Color(0xFFF3E5F5),
                    onSurfaceVariant = Color(0xFF49454F),
                    outline = Color(0xFF79747E),
                    error = Color(0xFFBA1A1A),
                    onError = Color.White
                )
            }
        }
    ),
    
    // 8. Midnight - Deep dark theme with subtle accents
    ThemeOption(
        labelRes = R.string.theme_midnight,
        colorScheme = { useDarkTheme -> 
            if (useDarkTheme) {
                darkColorScheme(
                    primary = Color(0xFF6A6A6A),
                    onPrimary = Color.White,
                    secondary = Color(0xFF5A5A5A),
                    onSecondary = Color.White,
                    tertiary = Color(0xFF4A4A4A),
                    onTertiary = Color.White,
                    background = Color(0xFF000000),
                    onBackground = Color(0xFFE0E0E0),
                    surface = Color(0xFF050505),
                    onSurface = Color(0xFFE0E0E0),
                    surfaceVariant = Color(0xFF0A0A0A),
                    onSurfaceVariant = Color(0xFFB0B0B0),
                    outline = Color(0xFF2A2A2A),
                    error = Color(0xFFCF6679),
                    onError = Color.Black
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF424242),
                    onPrimary = Color.White,
                    secondary = Color(0xFF616161),
                    onSecondary = Color.White,
                    tertiary = Color(0xFF757575),
                    onTertiary = Color.White,
                    background = Color(0xFFFAFAFA),
                    onBackground = Color(0xFF1C1B1F),
                    surface = Color.White,
                    onSurface = Color(0xFF1C1B1F),
                    surfaceVariant = Color(0xFFF5F5F5),
                    onSurfaceVariant = Color(0xFF49454F),
                    outline = Color(0xFF79747E),
                    error = Color(0xFFBA1A1A),
                    onError = Color.White
                )
            }
        }
    ),
    
    // 9. Coral - Warm coral and teal theme
    ThemeOption(
        labelRes = R.string.theme_coral,
        colorScheme = { useDarkTheme -> 
            if (useDarkTheme) {
                darkColorScheme(
                    primary = Color(0xFFFF7043),
                    onPrimary = Color.Black,
                    secondary = Color(0xFF26C6DA),
                    onSecondary = Color.Black,
                    tertiary = Color(0xFFFF5722),
                    onTertiary = Color.Black,
                    background = Color(0xFF0D1B1A),
                    onBackground = Color(0xFFE0F2F1),
                    surface = Color(0xFF1A2B2A),
                    onSurface = Color(0xFFE0F2F1),
                    surfaceVariant = Color(0xFF2A3B3A),
                    onSurfaceVariant = Color(0xFFB2DFDB),
                    outline = Color(0xFF4A5B5A),
                    error = Color(0xFFCF6679),
                    onError = Color.Black
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFFE64A19),
                    onPrimary = Color.White,
                    secondary = Color(0xFF00ACC1),
                    onSecondary = Color.White,
                    tertiary = Color(0xFFFF5722),
                    onTertiary = Color.White,
                    background = Color(0xFFF8FFFE),
                    onBackground = Color(0xFF1C1B1F),
                    surface = Color.White,
                    onSurface = Color(0xFF1C1B1F),
                    surfaceVariant = Color(0xFFE0F2F1),
                    onSurfaceVariant = Color(0xFF49454F),
                    outline = Color(0xFF79747E),
                    error = Color(0xFFBA1A1A),
                    onError = Color.White
                )
            }
        }
    ),
    
    // 10. Aurora - Northern lights inspired theme
    ThemeOption(
        labelRes = R.string.theme_aurora,
        colorScheme = { useDarkTheme -> 
            if (useDarkTheme) {
                darkColorScheme(
                    primary = Color(0xFF4CAF50),
                    onPrimary = Color.Black,
                    secondary = Color(0xFF00BCD4),
                    onSecondary = Color.Black,
                    tertiary = Color(0xFF9C27B0),
                    onTertiary = Color.Black,
                    background = Color(0xFF0A0D1A),
                    onBackground = Color(0xFFE8F5E8),
                    surface = Color(0xFF1A1D2A),
                    onSurface = Color(0xFFE8F5E8),
                    surfaceVariant = Color(0xFF2A2D3A),
                    onSurfaceVariant = Color(0xFFB8E6B8),
                    outline = Color(0xFF4A4D5A),
                    error = Color(0xFFCF6679),
                    onError = Color.Black
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF2E7D32),
                    onPrimary = Color.White,
                    secondary = Color(0xFF00ACC1),
                    onSecondary = Color.White,
                    tertiary = Color(0xFF7B1FA2),
                    onTertiary = Color.White,
                    background = Color(0xFFF8FFF8),
                    onBackground = Color(0xFF1C1B1F),
                    surface = Color.White,
                    onSurface = Color(0xFF1C1B1F),
                    surfaceVariant = Color(0xFFE8F5E8),
                    onSurfaceVariant = Color(0xFF49454F),
                    outline = Color(0xFF79747E),
                    error = Color(0xFFBA1A1A),
                    onError = Color.White
                )
            }
        }
    ),
    
    // 11. Rose Gold - Elegant rose gold theme
    ThemeOption(
        labelRes = R.string.theme_rose_gold,
        colorScheme = { useDarkTheme -> 
            if (useDarkTheme) {
                darkColorScheme(
                    primary = Color(0xFFE91E63),
                    onPrimary = Color.White,
                    secondary = Color(0xFFFF4081),
                    onSecondary = Color.Black,
                    tertiary = Color(0xFFF8BBD9),
                    onTertiary = Color.Black,
                    background = Color(0xFF1A0D0A),
                    onBackground = Color(0xFFFFF0F5),
                    surface = Color(0xFF2A1D1A),
                    onSurface = Color(0xFFFFF0F5),
                    surfaceVariant = Color(0xFF3A2D2A),
                    onSurfaceVariant = Color(0xFFFFCCD5),
                    outline = Color(0xFF5A4D4A),
                    error = Color(0xFFCF6679),
                    onError = Color.Black
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFFC2185B),
                    onPrimary = Color.White,
                    secondary = Color(0xFFE91E63),
                    onSecondary = Color.White,
                    tertiary = Color(0xFFF8BBD9),
                    onTertiary = Color.Black,
                    background = Color(0xFFFFF8FA),
                    onBackground = Color(0xFF1C1B1F),
                    surface = Color.White,
                    onSurface = Color(0xFF1C1B1F),
                    surfaceVariant = Color(0xFFFFF0F5),
                    onSurfaceVariant = Color(0xFF49454F),
                    outline = Color(0xFF79747E),
                    error = Color(0xFFBA1A1A),
                    onError = Color.White
                )
            }
        }
    )
)