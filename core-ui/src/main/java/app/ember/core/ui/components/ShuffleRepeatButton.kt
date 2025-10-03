package app.ember.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Premium shuffle/repeat button with icon morphing animations
 */
@Composable
fun ShuffleRepeatButton(
    shuffleMode: ShuffleMode = ShuffleMode.Off,
    repeatMode: PlayerRepeatMode = PlayerRepeatMode.Off,
    onShuffleToggle: () -> Unit = {},
    onRepeatToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing16)
    ) {
        // Shuffle button
        ShuffleButton(
            mode = shuffleMode,
            onClick = onShuffleToggle
        )
        
        // Repeat button
        RepeatButton(
            mode = repeatMode,
            onClick = onRepeatToggle
        )
    }
}

@Composable
private fun ShuffleButton(
    mode: ShuffleMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showShuffleEffect by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = AnimationGentle,
        label = "shuffleScale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (mode == ShuffleMode.On) 15f else 0f,
        animationSpec = AnimationStandard,
        label = "shuffleRotation"
    )
    
    LaunchedEffect(mode) {
        if (mode == ShuffleMode.On) {
            showShuffleEffect = true
        }
    }
    
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    onClick()
                    if (mode == ShuffleMode.Off) {
                        showShuffleEffect = true
                    }
                }
            )
            .semantics {
                role = Role.Button
                contentDescription = when (mode) {
                    ShuffleMode.Off -> "Enable shuffle"
                    ShuffleMode.On -> "Disable shuffle"
                }
            }
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Shuffle effect background
        AnimatedVisibility(
            visible = showShuffleEffect && mode == ShuffleMode.On,
            enter = scaleIn(animationSpec = AnimationGentle) + fadeIn(),
            exit = scaleOut(animationSpec = AnimationGentle) + fadeOut()
        ) {
            Canvas(
                modifier = Modifier.size(48.dp)
            ) {
                drawShuffleEffect(
                    center = Offset(size.width / 2, size.height / 2),
                    isActive = mode == ShuffleMode.On
                )
            }
        }
        
        // Icon with rotation
        Icon(
            imageVector = Icons.Filled.Share, // TODO: Replace with proper shuffle icon
            contentDescription = null,
            tint = if (mode == ShuffleMode.On) EmberFlame else TextMuted,
            modifier = Modifier
                .size(24.dp)
                .rotate(rotation)
        )
    }
}

@Composable
private fun RepeatButton(
    mode: PlayerRepeatMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRepeatEffect by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = AnimationGentle,
        label = "repeatScale"
    )
    
    val pulseScale by animateFloatAsState(
        targetValue = if (mode != PlayerRepeatMode.Off) 1.1f else 1f,
        animationSpec = AnimationStandard,
        label = "repeatPulse"
    )
    
    LaunchedEffect(mode) {
        if (mode != PlayerRepeatMode.Off) {
            showRepeatEffect = true
        }
    }
    
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    onClick()
                    if (mode == PlayerRepeatMode.Off) {
                        showRepeatEffect = true
                    }
                }
            )
            .semantics {
                role = Role.Button
                contentDescription = when (mode) {
                    PlayerRepeatMode.Off -> "Enable repeat"
                    PlayerRepeatMode.One -> "Repeat one song"
                    PlayerRepeatMode.All -> "Repeat all songs"
                }
            }
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Repeat effect background
        AnimatedVisibility(
            visible = showRepeatEffect && mode != PlayerRepeatMode.Off,
            enter = scaleIn(animationSpec = AnimationGentle) + fadeIn(),
            exit = scaleOut(animationSpec = AnimationGentle) + fadeOut()
        ) {
            Canvas(
                modifier = Modifier.size(48.dp)
            ) {
                drawRepeatEffect(
                    center = Offset(size.width / 2, size.height / 2),
                    mode = mode
                )
            }
        }
        
        // Icon with morphing
        Box(
            modifier = Modifier.scale(pulseScale),
            contentAlignment = Alignment.Center
        ) {
            when (mode) {
                PlayerRepeatMode.Off -> {
                    Icon(
                        imageVector = Icons.Filled.Share, // TODO: Replace with proper repeat icon
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
                PlayerRepeatMode.One -> {
                    Icon(
                        imageVector = Icons.Filled.Share, // TODO: Replace with proper repeat one icon
                        contentDescription = null,
                        tint = EmberFlame,
                        modifier = Modifier.size(24.dp)
                    )
                }
                PlayerRepeatMode.All -> {
                    Icon(
                        imageVector = Icons.Filled.Share, // TODO: Replace with proper repeat icon
                        contentDescription = null,
                        tint = EmberFlame,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawShuffleEffect(
    center: Offset,
    isActive: Boolean
) {
    if (!isActive) return
    
    // Draw shuffle arrows effect
    val colors = listOf(
        EmberFlame.copy(alpha = 0.3f),
        EmberFlameGlow.copy(alpha = 0.2f),
        AccentIce.copy(alpha = 0.15f)
    )
    
    // Draw multiple arrows in different directions
    repeat(4) {
        val angle = (it * 90f) * (Math.PI / 180f)
        val distance = size.minDimension / 4
        val arrowX = center.x + cos(angle).toFloat() * distance
        val arrowY = center.y + sin(angle).toFloat() * distance
        
        drawCircle(
            color = colors[it % colors.size],
            radius = 3.dp.toPx(),
            center = Offset(arrowX, arrowY)
        )
    }
    
    // Draw connecting lines
    val lineColor = EmberFlame.copy(alpha = 0.2f)
    drawLine(
        color = lineColor,
        start = Offset(center.x - 8.dp.toPx(), center.y),
        end = Offset(center.x + 8.dp.toPx(), center.y),
        strokeWidth = 2.dp.toPx()
    )
    drawLine(
        color = lineColor,
        start = Offset(center.x, center.y - 8.dp.toPx()),
        end = Offset(center.x, center.y + 8.dp.toPx()),
        strokeWidth = 2.dp.toPx()
    )
}

private fun DrawScope.drawRepeatEffect(
    center: Offset,
    mode: PlayerRepeatMode
) {
    if (mode == PlayerRepeatMode.Off) return
    
    val colors = listOf(
        EmberFlame.copy(alpha = 0.4f),
        EmberFlameGlow.copy(alpha = 0.3f),
        AccentIce.copy(alpha = 0.2f)
    )
    
    // Draw concentric circles for repeat effect
    for (i in 0..2) {
        val radius = (size.minDimension / 2) * (0.2f + i * 0.15f)
        val color = colors[i % colors.size]
        
        drawCircle(
            color = color,
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )
    }
    
    // Draw repeat indicator dots
    when (mode) {
        PlayerRepeatMode.One -> {
            // Single dot in center
            drawCircle(
                color = EmberFlame.copy(alpha = 0.8f),
                radius = 4.dp.toPx(),
                center = center
            )
        }
        PlayerRepeatMode.All -> {
            // Multiple dots around the circle
            repeat(8) {
                val angle = (it * 45f) * (Math.PI / 180f)
                val distance = size.minDimension / 3
                val dotX = center.x + cos(angle).toFloat() * distance
                val dotY = center.y + sin(angle).toFloat() * distance
                
                drawCircle(
                    color = AccentIce.copy(alpha = 0.6f),
                    radius = 2.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
            }
        }
        else -> {}
    }
}

enum class ShuffleMode {
    Off, On
}

enum class PlayerRepeatMode {
    Off, One, All
}
