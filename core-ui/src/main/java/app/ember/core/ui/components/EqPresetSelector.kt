package app.ember.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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

/**
 * Premium EQ preset selector with flame-accented animations
 */
@Composable
fun EqPresetSelector(
    selectedPreset: EqPreset,
    onPresetSelected: (EqPreset) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    val presets = listOf(
        EqPreset.Flat,
        EqPreset.Rock,
        EqPreset.Jazz,
        EqPreset.Classical,
        EqPreset.Pop,
        EqPreset.Custom
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing16),
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
                .padding(Spacing16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Presets",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing8),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = Spacing8)
            ) {
                items(presets) { preset ->
                    PresetChip(
                        preset = preset,
                        isSelected = preset == selectedPreset,
                        onClick = { onPresetSelected(preset) },
                        isEnabled = isEnabled
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetChip(
    preset: EqPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "chipScale"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = AnimationStandard,
        label = "glowAlpha"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                enabled = isEnabled
            )
            .semantics {
                role = Role.Button
                contentDescription = "${preset.name} preset"
            },
        contentAlignment = Alignment.Center
    ) {
        // Background card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) {
                    EmberFlame.copy(alpha = 0.15f)
                } else {
                    EmberElev1.copy(alpha = 0.5f)
                }
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) Elevation2 else Elevation1
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(Spacing12),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Preset icon
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier.size(32.dp)
                    ) {
                        drawPresetIcon(
                            preset = preset,
                            isSelected = isSelected,
                            glowAlpha = glowAlpha
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing4))
                
                // Preset name
                Text(
                    text = preset.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) EmberFlame else TextMuted,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
                )
            }
        }
        
        // Selection glow effect
        if (isSelected) {
            Canvas(
                modifier = Modifier.size(48.dp)
            ) {
                drawSelectionGlow(
                    center = Offset(size.width / 2, size.height / 2),
                    alpha = glowAlpha
                )
            }
        }
    }
}

private fun DrawScope.drawPresetIcon(
    preset: EqPreset,
    isSelected: Boolean,
    glowAlpha: Float
) {
    val center = Offset(size.width / 2, size.height / 2)
    val iconColor = if (isSelected) EmberFlame else TextMuted
    
    when (preset) {
        EqPreset.Flat -> {
            // Flat line
            drawLine(
                color = iconColor,
                start = Offset(center.x - 8.dp.toPx(), center.y),
                end = Offset(center.x + 8.dp.toPx(), center.y),
                strokeWidth = 2.dp.toPx()
            )
        }
        EqPreset.Rock -> {
            // Rock curve (V shape)
            drawLine(
                color = iconColor,
                start = Offset(center.x - 8.dp.toPx(), center.y - 4.dp.toPx()),
                end = Offset(center.x, center.y + 4.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = iconColor,
                start = Offset(center.x, center.y + 4.dp.toPx()),
                end = Offset(center.x + 8.dp.toPx(), center.y - 4.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
        }
        EqPreset.Jazz -> {
            // Jazz curve (smooth S shape)
            drawLine(
                color = iconColor,
                start = Offset(center.x - 8.dp.toPx(), center.y - 2.dp.toPx()),
                end = Offset(center.x - 4.dp.toPx(), center.y + 2.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = iconColor,
                start = Offset(center.x - 4.dp.toPx(), center.y + 2.dp.toPx()),
                end = Offset(center.x + 4.dp.toPx(), center.y - 2.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = iconColor,
                start = Offset(center.x + 4.dp.toPx(), center.y - 2.dp.toPx()),
                end = Offset(center.x + 8.dp.toPx(), center.y + 2.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
        }
        EqPreset.Classical -> {
            // Classical curve (gentle arc)
            drawLine(
                color = iconColor,
                start = Offset(center.x - 8.dp.toPx(), center.y),
                end = Offset(center.x - 4.dp.toPx(), center.y - 3.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = iconColor,
                start = Offset(center.x - 4.dp.toPx(), center.y - 3.dp.toPx()),
                end = Offset(center.x + 4.dp.toPx(), center.y - 3.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = iconColor,
                start = Offset(center.x + 4.dp.toPx(), center.y - 3.dp.toPx()),
                end = Offset(center.x + 8.dp.toPx(), center.y),
                strokeWidth = 2.dp.toPx()
            )
        }
        EqPreset.Pop -> {
            // Pop curve (smile shape)
            drawLine(
                color = iconColor,
                start = Offset(center.x - 8.dp.toPx(), center.y - 2.dp.toPx()),
                end = Offset(center.x, center.y + 3.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = iconColor,
                start = Offset(center.x, center.y + 3.dp.toPx()),
                end = Offset(center.x + 8.dp.toPx(), center.y - 2.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
        }
        EqPreset.Custom -> {
            // Custom icon (gear/settings)
            drawCircle(
                color = iconColor,
                radius = 6.dp.toPx(),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = iconColor,
                radius = 2.dp.toPx(),
                center = center
            )
        }
    }
    
    // Add glow effect for selected preset
    if (isSelected && glowAlpha > 0) {
        drawCircle(
            color = EmberFlame.copy(alpha = glowAlpha * 0.3f),
            radius = 16.dp.toPx(),
            center = center
        )
    }
}

private fun DrawScope.drawSelectionGlow(
    center: Offset,
    alpha: Float
) {
    // Draw pulsing glow rings
    val colors = listOf(
        EmberFlame.copy(alpha = alpha * 0.4f),
        EmberFlameGlow.copy(alpha = alpha * 0.3f),
        AccentIce.copy(alpha = alpha * 0.2f)
    )
    
    for (i in 0..2) {
        val radius = (size.minDimension / 2) * (0.6f + i * 0.2f)
        val color = colors[i % colors.size]
        
        drawCircle(
            color = color,
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )
    }
    
    // Draw sparkle particles
    repeat(6) {
        val angle = (it * 60f) * (Math.PI / 180f)
        val distance = size.minDimension / 3
        val sparkleX = center.x + cos(angle).toFloat() * distance
        val sparkleY = center.y + sin(angle).toFloat() * distance
        
        drawCircle(
            color = AccentIce.copy(alpha = alpha * 0.8f),
            radius = 2.dp.toPx(),
            center = Offset(sparkleX, sparkleY)
        )
    }
}
