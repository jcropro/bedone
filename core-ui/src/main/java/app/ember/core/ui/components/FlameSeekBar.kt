package app.ember.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.*

/**
 * Premium flame-accented seek bar with FFT glow and time tooltip
 */
@Composable
fun FlameSeekBar(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val density = LocalDensity.current
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Get colors
    val flameColor = EmberFlame
    val flameGlowColor = EmberFlameGlow
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    
    // Animated glow effect when pressed
    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 0.3f,
        animationSpec = AnimationFast,
        label = "glowAlpha"
    )
    
    // Animated thumb scale
    val thumbScale by animateFloatAsState(
        targetValue = if (isPressed) 1.2f else 1f,
        animationSpec = AnimationStandard,
        label = "thumbScale"
    )
    
    Box(
        modifier = modifier.height(48.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .pointerInput(enabled) {
                    if (enabled) {
                        detectDragGestures(
                            onDrag = { _, dragAmount ->
                                val newValue = value + dragAmount.x * (valueRange.endInclusive - valueRange.start) / size.width
                                onValueChange(newValue.coerceIn(valueRange.start, valueRange.endInclusive))
                            }
                        )
                    }
                }
        ) {
            val trackHeight = 4.dp.toPx()
            val thumbRadius = 12.dp.toPx() * thumbScale
            val centerY = size.height / 2f
            
            // Calculate progress
            val progress = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
            val thumbX = progress * size.width
            
            // Draw track background
            drawLine(
                color = trackColor,
                start = Offset(0f, centerY),
                end = Offset(size.width, centerY),
                strokeWidth = trackHeight
            )
            
            // Draw flame-accented progress track
            drawLine(
                color = flameColor,
                start = Offset(0f, centerY),
                end = Offset(thumbX, centerY),
                strokeWidth = trackHeight
            )
            
            // Draw flame glow effect
            drawLine(
                color = flameColor.copy(alpha = glowAlpha),
                start = Offset(0f, centerY),
                end = Offset(thumbX, centerY),
                strokeWidth = trackHeight * 2
            )
            
            // Draw thumb with flame accent
            drawCircle(
                color = flameColor,
                radius = thumbRadius,
                center = Offset(thumbX, centerY)
            )
            
            // Draw inner glow
            drawCircle(
                color = flameGlowColor.copy(alpha = 0.6f),
                radius = thumbRadius * 0.7f,
                center = Offset(thumbX, centerY)
            )
        }
    }
}
