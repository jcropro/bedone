package app.ember.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.design.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Premium Flame Equalizer with molten gradient bars and ember glow caps
 */
@Composable
fun FlameEqualizer(
    bands: List<EqBand>,
    onBandChanged: (Int, Float) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    preset: EqPreset = EqPreset.Flat
) {
    val density = LocalDensity.current
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing16),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmberCard.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing20),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Equalizer",
                style = MaterialTheme.typography.headlineSmall,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // EQ Bands
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                bands.forEachIndexed { index, band ->
                    EqBandSlider(
                        band = band,
                        index = index,
                        onValueChanged = { value -> onBandChanged(index, value) },
                        isEnabled = isEnabled,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (index < bands.size - 1) {
                        Spacer(modifier = Modifier.width(Spacing8))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Frequency labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                bands.forEach { band ->
                    Text(
                        text = formatFrequency(band.frequency),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EqBandSlider(
    band: EqBand,
    index: Int,
    onValueChanged: (Float) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    var isDragging by remember { mutableStateOf(false) }
    var glowIntensity by remember { mutableStateOf(0f) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "bandScale"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = glowIntensity,
        animationSpec = AnimationStandard,
        label = "glowAlpha"
    )
    
    LaunchedEffect(isDragging) {
        if (isDragging) {
            glowIntensity = 1f
        } else {
            glowIntensity = 0f
        }
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Band label
        Text(
            text = band.label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(Spacing4))
        
        // Slider container
        Box(
            modifier = Modifier
                .size(width = 24.dp, height = 200.dp)
                .scale(scale)
                .pointerInput(index) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false }
                    ) { change, _ ->
                        val newValue = calculateBandValue(
                            change.position.y,
                            size.height.toFloat(),
                            band.minValue,
                            band.maxValue
                        )
                        onValueChanged(newValue)
                    }
                }
                .semantics {
                    role = Role.Button
                    contentDescription = "${band.label}: ${String.format("%.1f", band.value)}dB"
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawEqBandSlider(
                    band = band,
                    glowAlpha = glowAlpha,
                    isEnabled = isEnabled,
                    isDragging = isDragging
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing4))
        
        // Value display
        Text(
            text = "${String.format("%.1f", band.value)}dB",
            style = MaterialTheme.typography.labelSmall,
            color = if (band.value != 0f) EmberFlame else TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun DrawScope.drawEqBandSlider(
    band: EqBand,
    glowAlpha: Float,
    isEnabled: Boolean,
    isDragging: Boolean
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val barWidth = 8.dp.toPx()
    val maxHeight = size.height * 0.8f
    
    // Calculate bar height based on band value
    val normalizedValue = (band.value - band.minValue) / (band.maxValue - band.minValue)
    val barHeight = maxHeight * normalizedValue.coerceIn(0f, 1f)
    
    // Draw background bar
    val bgColor = if (isEnabled) {
        EmberElev1.copy(alpha = 0.3f)
    } else {
        TextMuted.copy(alpha = 0.2f)
    }
    
    drawRect(
        color = bgColor,
        topLeft = Offset(centerX - barWidth / 2, centerY - maxHeight / 2),
        size = androidx.compose.ui.geometry.Size(barWidth, maxHeight)
    )
    
    // Draw molten gradient bar
    if (isEnabled) {
        val moltenColors = listOf(
            EmberFlame.copy(alpha = 0.8f),
            EmberFlameGlow.copy(alpha = 0.6f),
            AccentIce.copy(alpha = 0.4f),
            EmberFlame.copy(alpha = 0.8f)
        )
        
        val moltenBrush = Brush.verticalGradient(
            colors = moltenColors,
            startY = centerY + maxHeight / 2,
            endY = centerY + maxHeight / 2 - barHeight
        )
        
        drawRect(
            brush = moltenBrush,
            topLeft = Offset(centerX - barWidth / 2, centerY + maxHeight / 2 - barHeight),
            size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
        )
        
        // Draw ember glow cap
        if (barHeight > 0) {
            drawEmberGlowCap(
                center = Offset(centerX, centerY + maxHeight / 2 - barHeight),
                glowAlpha = glowAlpha,
                isDragging = isDragging
            )
        }
        
        // Draw center line
        drawLine(
            color = TextMuted.copy(alpha = 0.5f),
            start = Offset(centerX - barWidth / 2, centerY),
            end = Offset(centerX + barWidth / 2, centerY),
            strokeWidth = 1.dp.toPx()
        )
    }
}

private fun DrawScope.drawEmberGlowCap(
    center: Offset,
    glowAlpha: Float,
    isDragging: Boolean
) {
    val capRadius = 6.dp.toPx()
    val glowRadius = capRadius * (1f + glowAlpha * 0.5f)
    
    // Outer glow ring
    if (glowAlpha > 0) {
        drawCircle(
            color = EmberFlame.copy(alpha = glowAlpha * 0.3f),
            radius = glowRadius,
            center = center
        )
    }
    
    // Main cap
    drawCircle(
        color = EmberFlame.copy(alpha = 0.9f),
        radius = capRadius,
        center = center
    )
    
    // Inner highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.4f),
        radius = capRadius * 0.6f,
        center = center
    )
    
    // Sparkle particles when dragging
    if (isDragging && glowAlpha > 0.5f) {
        repeat(4) {
            val angle = (it * 90f) * (Math.PI / 180f)
            val distance = capRadius * 1.5f
            val sparkleX = center.x + cos(angle).toFloat() * distance
            val sparkleY = center.y + sin(angle).toFloat() * distance
            
            drawCircle(
                color = AccentIce.copy(alpha = glowAlpha * 0.8f),
                radius = 2.dp.toPx(),
                center = Offset(sparkleX, sparkleY)
            )
        }
    }
}

private fun calculateBandValue(
    y: Float,
    height: Float,
    minValue: Float,
    maxValue: Float
): Float {
    val normalizedY = (height - y) / height
    return minValue + (maxValue - minValue) * normalizedY.coerceIn(0f, 1f)
}

private fun formatFrequency(frequency: Float): String {
    return when {
        frequency >= 1000f -> "${(frequency / 1000f).toInt()}k"
        else -> frequency.toInt().toString()
    }
}

data class EqBand(
    val label: String,
    val frequency: Float,
    val value: Float,
    val minValue: Float = -12f,
    val maxValue: Float = 12f
)

enum class EqPreset {
    Flat,
    Rock,
    Jazz,
    Classical,
    Pop,
    Custom
}
