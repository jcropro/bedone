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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.design.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Secondary actions tray with premium micro-interactions
 */
@Composable
fun SecondaryActionsTray(
    modifier: Modifier = Modifier,
    isLiked: Boolean = false,
    playbackSpeed: Float = 1.0f,
    sleepTimerMinutes: Int? = null,
    onLikeToggle: () -> Unit = {},
    onQueueClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onSpeedClick: () -> Unit = {},
    onTimerClick: () -> Unit = {},
    onEqualizerClick: () -> Unit = {}
) {
    val density = LocalDensity.current
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing24, vertical = Spacing16),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Like button with ember fill animation
        LikeButton(
            isLiked = isLiked,
            onClick = onLikeToggle
        )
        
        // Queue button
        SecondaryActionButton(
            icon = Icons.Filled.Share, // TODO: Replace with proper queue icon
            contentDescription = "Queue",
            onClick = onQueueClick
        )
        
        // Share button
        SecondaryActionButton(
            icon = Icons.Filled.Share,
            contentDescription = "Share",
            onClick = onShareClick
        )
        
        // Speed button with indicator
        SpeedButton(
            speed = playbackSpeed,
            onClick = onSpeedClick
        )
        
        // Sleep timer button with indicator
        TimerButton(
            timerMinutes = sleepTimerMinutes,
            onClick = onTimerClick
        )
        
        // Equalizer button
        SecondaryActionButton(
            icon = Icons.Filled.Share, // TODO: Replace with proper equalizer icon
            contentDescription = "Equalizer",
            onClick = onEqualizerClick
        )
    }
}

@Composable
private fun LikeButton(
    isLiked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEmberFill by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (showEmberFill) 1.2f else 1f,
        animationSpec = AnimationGentle,
        label = "likeScale"
    )
    
    val emberFillAlpha by animateFloatAsState(
        targetValue = if (showEmberFill) 1f else 0f,
        animationSpec = AnimationStandard,
        label = "emberFillAlpha"
    )
    
    LaunchedEffect(isLiked) {
        if (isLiked) {
            showEmberFill = true
        }
    }
    
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    onClick()
                    if (!isLiked) {
                        showEmberFill = true
                    }
                }
            )
            .semantics {
                role = Role.Button
                contentDescription = if (isLiked) "Unlike" else "Like"
            },
        contentAlignment = Alignment.Center
    ) {
        // Ember fill animation background
        AnimatedVisibility(
            visible = showEmberFill,
            enter = scaleIn(animationSpec = AnimationGentle) + fadeIn(),
            exit = scaleOut(animationSpec = AnimationGentle) + fadeOut()
        ) {
            Canvas(
                modifier = Modifier.size(48.dp)
            ) {
                drawEmberFill(
                    center = Offset(size.width / 2, size.height / 2),
                    alpha = emberFillAlpha
                )
            }
        }
        
        // Icon
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = null,
            tint = if (isLiked) EmberFlame else TextMuted,
            modifier = Modifier.scale(scale)
        )
    }
}

@Composable
private fun SpeedButton(
    speed: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = "Playback Speed: ${speed}x"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Share, // TODO: Replace with proper speed icon
                contentDescription = null,
                tint = if (speed != 1.0f) EmberFlame else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            
            Text(
                text = "${speed}x",
                style = MaterialTheme.typography.labelSmall,
                color = if (speed != 1.0f) EmberFlame else TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TimerButton(
    timerMinutes: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = if (timerMinutes != null) "Sleep Timer: ${timerMinutes}min" else "Sleep Timer"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Share, // TODO: Replace with proper timer icon
                contentDescription = null,
                tint = if (timerMinutes != null) EmberFlame else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            
            if (timerMinutes != null) {
                Text(
                    text = "${timerMinutes}m",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmberFlame,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SecondaryActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = AnimationGentle,
        label = "buttonScale"
    )
    
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
            }
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun DrawScope.drawEmberFill(
    center: Offset,
    alpha: Float
) {
    val colors = listOf(
        EmberFlame.copy(alpha = alpha),
        EmberFlameGlow.copy(alpha = alpha * 0.7f),
        AccentIce.copy(alpha = alpha * 0.5f)
    )
    
    // Draw multiple concentric circles for ember effect
    for (i in 0..2) {
        val radius = (size.minDimension / 2) * (0.3f + i * 0.2f)
        val color = colors[i % colors.size]
        
        drawCircle(
            color = color,
            radius = radius,
            center = center
        )
    }
    
    // Add sparkle particles
    repeat(8) {
        val angle = (it * 45f) * (Math.PI / 180f)
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
