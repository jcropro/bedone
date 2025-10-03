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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.design.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Per-song EQ profile management with persistence
 */
@Composable
fun PerSongEqProfile(
    songTitle: String,
    songArtist: String,
    hasCustomProfile: Boolean,
    isProfileActive: Boolean,
    onSaveProfile: () -> Unit,
    onLoadProfile: () -> Unit,
    onDeleteProfile: () -> Unit,
    onShareProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showActions by remember { mutableStateOf(false) }
    
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
                .padding(Spacing16)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Per-Song Profile",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextStrong,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$songTitle — $songArtist",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Status indicator
                ProfileStatusIndicator(
                    hasProfile = hasCustomProfile,
                    isActive = isProfileActive
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            HorizontalDivider(
                color = TextMuted.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (hasCustomProfile) {
                    // Load profile button
                    ProfileActionButton(
                        icon = Icons.Filled.Check,
                        label = "Load",
                        isActive = isProfileActive,
                        onClick = onLoadProfile,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(Spacing8))
                    
                    // Share profile button
                    ProfileActionButton(
                        icon = Icons.Filled.Share,
                        label = "Share",
                        isActive = false,
                        onClick = onShareProfile,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(Spacing8))
                    
                    // Delete profile button
                    ProfileActionButton(
                        icon = Icons.Filled.Delete,
                        label = "Delete",
                        isActive = false,
                        onClick = onDeleteProfile,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    // Save profile button
                    ProfileActionButton(
                        icon = Icons.Filled.Check,
                        label = "Save",
                        isActive = false,
                        onClick = onSaveProfile,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStatusIndicator(
    hasProfile: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val glowAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = AnimationStandard,
        label = "glowAlpha"
    )
    
    Box(
        modifier = modifier.size(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(32.dp)
        ) {
            drawStatusIndicator(
                hasProfile = hasProfile,
                isActive = isActive,
                glowAlpha = glowAlpha
            )
        }
    }
}

@Composable
private fun ProfileActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "buttonScale"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = AnimationStandard,
        label = "glowAlpha"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = label
            },
        contentAlignment = Alignment.Center
    ) {
        // Background card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isActive) {
                    EmberFlame.copy(alpha = 0.15f)
                } else {
                    EmberElev1.copy(alpha = 0.5f)
                }
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isActive) Elevation2 else Elevation1
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(Spacing12),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) EmberFlame else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.height(Spacing4))
                
                // Label
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) EmberFlame else TextMuted,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 10.sp
                )
            }
        }
        
        // Glow effect for active button
        if (isActive) {
            Canvas(
                modifier = Modifier.size(48.dp)
            ) {
                drawButtonGlow(
                    center = Offset(size.width / 2, size.height / 2),
                    alpha = glowAlpha
                )
            }
        }
    }
}

private fun DrawScope.drawStatusIndicator(
    hasProfile: Boolean,
    isActive: Boolean,
    glowAlpha: Float
) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = 8.dp.toPx()
    
    when {
        isActive -> {
            // Active profile - flame circle with glow
            drawCircle(
                color = EmberFlame.copy(alpha = 0.9f),
                radius = radius,
                center = center
            )
            
            // Inner highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = radius * 0.6f,
                center = center
            )
            
            // Glow effect
            if (glowAlpha > 0) {
                drawCircle(
                    color = EmberFlame.copy(alpha = glowAlpha * 0.3f),
                    radius = radius * 1.5f,
                    center = center
                )
            }
        }
        hasProfile -> {
            // Has profile but not active - muted circle
            drawCircle(
                color = TextMuted.copy(alpha = 0.6f),
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }
        else -> {
            // No profile - empty circle
            drawCircle(
                color = TextMuted.copy(alpha = 0.3f),
                radius = radius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

private fun DrawScope.drawButtonGlow(
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
    repeat(4) {
        val angle = (it * 90f) * (Math.PI / 180f)
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
