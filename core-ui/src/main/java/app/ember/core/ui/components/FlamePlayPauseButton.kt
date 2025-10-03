package app.ember.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
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
 * Premium play/pause button with flame burst particles and ripple ring
 */
@Composable
fun FlamePlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val density = LocalDensity.current
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animated button scale
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = AnimationFast,
        label = "buttonScale"
    )
    
    // Flame burst particles animation
    val burstParticles = remember { Animatable(0f) }
    val rippleRing = remember { Animatable(0f) }
    
    // Trigger animations on play/pause
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            // Burst particles animation (180ms)
            burstParticles.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 180, easing = LinearEasing)
            )
            burstParticles.animateTo(0f, animationSpec = tween(durationMillis = 100))
        } else {
            // Ripple ring animation
            rippleRing.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            )
            rippleRing.animateTo(0f, animationSpec = tween(durationMillis = 100))
        }
    }
    
    // Get colors
    val flameColor = EmberFlame
    val flameGlowColor = EmberFlameGlow
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(surfaceColor)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = if (isPlaying) "Pause" else "Play"
            },
        contentAlignment = Alignment.Center
    ) {
        // Background glow effect
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val radius = size.minDimension / 2f
            
            // Draw background glow
            drawCircle(
                color = flameColor.copy(alpha = 0.1f),
                radius = radius * 1.2f,
                center = Offset(centerX, centerY)
            )
        }
        
        // Flame burst particles (≤24 particles, 180ms)
        if (burstParticles.value > 0f) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val maxRadius = size.minDimension / 2f * 1.5f
                
                // Generate random particles
                val particleCount = 24
                val random = Random(42) // Fixed seed for consistent animation
                
                for (i in 0 until particleCount) {
                    val angle = (i.toFloat() / particleCount) * 2 * kotlin.math.PI
                    val distance = maxRadius * burstParticles.value
                    val particleX = centerX + cos(angle).toFloat() * distance
                    val particleY = centerY + sin(angle).toFloat() * distance
                    
                    // Particle size decreases with distance
                    val particleSize = (1f - burstParticles.value) * 4f
                    
                    drawCircle(
                        color = flameColor.copy(alpha = 1f - burstParticles.value),
                        radius = particleSize,
                        center = Offset(particleX, particleY)
                    )
                }
            }
        }
        
        // Ripple ring effect
        if (rippleRing.value > 0f) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val baseRadius = size.minDimension / 2f
                val ringRadius = baseRadius + (rippleRing.value * baseRadius * 0.5f)
                
                drawCircle(
                    color = flameColor.copy(alpha = 1f - rippleRing.value),
                    radius = ringRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
        
        // Main button icon
        Icon(
            imageVector = if (isPlaying) Icons.Filled.PlayArrow else Icons.Filled.PlayArrow, // TODO: Add proper pause icon
            contentDescription = null,
            tint = flameColor,
            modifier = Modifier.size(32.dp)
        )
    }
}
