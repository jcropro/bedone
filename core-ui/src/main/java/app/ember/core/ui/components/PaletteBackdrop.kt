package app.ember.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme
import kotlinx.coroutines.launch

/**
 * A backdrop that smoothly crossfades between palette-driven gradients.
 * Falls back to Ember gradient when no palette is available.
 */
@Composable
fun PaletteBackdrop(
    modifier: Modifier = Modifier,
    paletteColor: Color? = null,
    fallbackColor: Color = Color(0xFFFF7A1A), // Ember Orange
    crossfadeDurationMs: Int = 220
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    
    var currentGradient by remember { mutableStateOf(createEmberGradient(fallbackColor)) }
    var targetGradient by remember { mutableStateOf(createEmberGradient(fallbackColor)) }
    
    val gradientAnimatable = remember { Animatable(0f) }
    
    // Update target gradient when palette color changes
    LaunchedEffect(paletteColor) {
        val newGradient = if (paletteColor != null) {
            createPaletteGradient(paletteColor)
        } else {
            createEmberGradient(fallbackColor)
        }
        
        targetGradient = newGradient
        
        // Animate crossfade
        launch {
            gradientAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = crossfadeDurationMs,
                    easing = LinearEasing
                )
            )
            currentGradient = targetGradient
            gradientAnimatable.snapTo(0f)
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(currentGradient)
    )
}

/**
 * Creates a gradient using the palette color with subtle variations
 */
private fun createPaletteGradient(paletteColor: Color): Brush {
    val baseColor = paletteColor.toArgb()
    val darker = Color(baseColor).copy(alpha = 0.8f)
    val lighter = Color(baseColor).copy(alpha = 0.4f)
    
    return Brush.radialGradient(
        colors = listOf(
            darker,
            lighter,
            Color.Black.copy(alpha = 0.3f)
        ),
        radius = 800f
    )
}

/**
 * Creates the default Ember gradient
 */
private fun createEmberGradient(emberColor: Color): Brush {
    return Brush.radialGradient(
        colors = listOf(
            emberColor.copy(alpha = 0.6f),
            emberColor.copy(alpha = 0.3f),
            Color.Black.copy(alpha = 0.4f)
        ),
        radius = 800f
    )
}

@Preview(showBackground = true)
@Composable
private fun PaletteBackdropPreview() {
    EmberTheme {
        PaletteBackdrop(
            paletteColor = Color(0xFF7B5CE6) // Purple
        )
    }
}
