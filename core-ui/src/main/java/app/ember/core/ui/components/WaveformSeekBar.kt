package app.ember.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Premium waveform seek bar with flame-accented scrubber
 */
@Composable
fun WaveformSeekBar(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    modifier: Modifier = Modifier,
    waveformData: List<Float>? = null,
    isPlaying: Boolean = false
) {
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Generate waveform data if not provided
    val waveform = waveformData ?: remember {
        generateWaveformData(200) // Generate 200 data points
    }
    
    val progress = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
    val scrubberPosition = progress.coerceIn(0f, 1f)
    
    val scrubberGlow by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0.7f,
        animationSpec = AnimationGentle,
        label = "scrubberGlow"
    )
    
    val waveformAnimation by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.8f,
        animationSpec = AnimationStandard,
        label = "waveformAnimation"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(EmberCard.copy(alpha = 0.3f))
            .pointerInput(valueRange) {
                detectDragGestures(
                    onDragStart = { /* Handle drag start */ },
                    onDragEnd = { /* Handle drag end */ }
                ) { change, _ ->
                    val newValue = (change.position.x / size.width)
                        .coerceIn(0f, 1f) * (valueRange.endInclusive - valueRange.start) + valueRange.start
                    onValueChange(newValue)
                }
            }
            .semantics {
                role = Role.Button
                contentDescription = "Seek to ${(progress * 100).toInt()}%"
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxWidth()
        ) {
            drawWaveformSeekBar(
                waveform = waveform,
                progress = scrubberPosition,
                scrubberGlow = scrubberGlow,
                waveformAnimation = waveformAnimation,
                isPlaying = isPlaying
            )
        }
    }
}

private fun DrawScope.drawWaveformSeekBar(
    waveform: List<Float>,
    progress: Float,
    scrubberGlow: Float,
    waveformAnimation: Float,
    isPlaying: Boolean
) {
    val barHeight = size.height
    val barWidth = size.width
    val centerY = barHeight / 2
    
    // Draw background waveform (played portion)
    drawWaveformBars(
        waveform = waveform,
        startX = 0f,
        endX = barWidth * progress,
        centerY = centerY,
        color = EmberFlame.copy(alpha = 0.6f * waveformAnimation),
        isActive = true
    )
    
    // Draw foreground waveform (unplayed portion)
    drawWaveformBars(
        waveform = waveform,
        startX = barWidth * progress,
        endX = barWidth,
        centerY = centerY,
        color = TextMuted.copy(alpha = 0.4f * waveformAnimation),
        isActive = false
    )
    
    // Draw scrubber line with glow effect
    drawScrubberLine(
        x = barWidth * progress,
        centerY = centerY,
        glowIntensity = scrubberGlow,
        isPlaying = isPlaying
    )
    
    // Draw flame particles around scrubber when playing
    if (isPlaying) {
        drawFlameParticles(
            centerX = barWidth * progress,
            centerY = centerY,
            intensity = waveformAnimation
        )
    }
}

private fun DrawScope.drawWaveformBars(
    waveform: List<Float>,
    startX: Float,
    endX: Float,
    centerY: Float,
    color: Color,
    isActive: Boolean
) {
    val barWidth = size.width
    val barCount = waveform.size
    val barSpacing = barWidth / barCount
    val maxBarHeight = size.height * 0.8f
    
    for (i in waveform.indices) {
        val barX = startX + (i * barSpacing)
        if (barX >= startX && barX <= endX) {
            val amplitude = waveform[i]
            val barHeight = maxBarHeight * amplitude * (if (isActive) 1f else 0.6f)
            
            // Draw bar with gradient effect
            val barColor = if (isActive) {
                androidx.compose.ui.graphics.lerp(
                    EmberFlame.copy(alpha = 0.8f),
                    EmberFlameGlow.copy(alpha = 0.6f),
                    amplitude
                )
            } else {
                color
            }
            
            drawRect(
                color = barColor,
                topLeft = Offset(barX, centerY - barHeight / 2),
                size = androidx.compose.ui.geometry.Size(
                    width = max(1.dp.toPx(), barSpacing * 0.8f),
                    height = barHeight
                )
            )
        }
    }
}

private fun DrawScope.drawScrubberLine(
    x: Float,
    centerY: Float,
    glowIntensity: Float,
    isPlaying: Boolean
) {
    val lineHeight = size.height * 0.9f
    val lineWidth = 3.dp.toPx()
    
    // Draw glow effect
    if (glowIntensity > 0.5f) {
        val glowColor = EmberFlame.copy(alpha = glowIntensity * 0.3f)
        drawRect(
            color = glowColor,
            topLeft = Offset(x - lineWidth * 2, centerY - lineHeight / 2),
            size = androidx.compose.ui.geometry.Size(
                width = lineWidth * 4,
                height = lineHeight
            )
        )
    }
    
    // Draw main scrubber line
    val lineColor = if (isPlaying) {
        EmberFlame.copy(alpha = glowIntensity)
    } else {
        AccentIce.copy(alpha = glowIntensity)
    }
    
    drawRect(
        color = lineColor,
        topLeft = Offset(x - lineWidth / 2, centerY - lineHeight / 2),
        size = androidx.compose.ui.geometry.Size(
            width = lineWidth,
            height = lineHeight
        )
    )
    
    // Draw scrubber handle
    val handleRadius = 6.dp.toPx()
    drawCircle(
        color = lineColor,
        radius = handleRadius,
        center = Offset(x, centerY)
    )
    
    // Draw inner highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = handleRadius * 0.6f,
        center = Offset(x, centerY)
    )
}

private fun DrawScope.drawFlameParticles(
    centerX: Float,
    centerY: Float,
    intensity: Float
) {
    val particleCount = (intensity * 8).toInt()
    
    repeat(particleCount) {
        val angle = (it * 45f) * (Math.PI / 180f)
        val distance = 12.dp.toPx() + Random.nextFloat() * 8.dp.toPx()
        val particleX = centerX + kotlin.math.cos(angle).toFloat() * distance
        val particleY = centerY + kotlin.math.sin(angle).toFloat() * distance
        
        val particleColor = androidx.compose.ui.graphics.lerp(
            EmberFlame.copy(alpha = intensity * 0.6f),
            AccentIce.copy(alpha = intensity * 0.4f),
            Random.nextFloat()
        )
        
        drawCircle(
            color = particleColor,
            radius = 2.dp.toPx() + Random.nextFloat() * 2.dp.toPx(),
            center = Offset(particleX, particleY)
        )
    }
}

private fun generateWaveformData(count: Int): List<Float> {
    return (0 until count).map { i ->
        // Generate realistic waveform data with varying amplitudes
        val baseAmplitude = 0.3f + 0.4f * kotlin.math.sin(i * 0.1f)
        val noise = Random.nextFloat() * 0.2f - 0.1f
        (baseAmplitude + noise).coerceIn(0.1f, 1f)
    }
}
