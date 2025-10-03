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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.res.painterResource
import app.ember.core.ui.R
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
 * Premium Share Sheet with multiple sharing options
 */
@Composable
fun ShareSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    songTitle: String,
    songArtist: String,
    onShareOriginal: () -> Unit,
    onShareCopy: () -> Unit,
    onShareClip: () -> Unit,
    onSharePlaylist: () -> Unit,
    onShareCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 300, easing = EasingStandard)
        ) + fadeIn(animationSpec = tween(durationMillis = 200, easing = EasingStandard)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 300, easing = EasingStandard)
        ) + fadeOut(animationSpec = tween(durationMillis = 200, easing = EasingStandard))
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = EmberInk.copy(alpha = 0.8f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Share content
                ShareContent(
                    songTitle = songTitle,
                    songArtist = songArtist,
                    onShareOriginal = onShareOriginal,
                    onShareCopy = onShareCopy,
                    onShareClip = onShareClip,
                    onSharePlaylist = onSharePlaylist,
                    onShareCard = onShareCard,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun ShareContent(
    songTitle: String,
    songArtist: String,
    onShareOriginal: () -> Unit,
    onShareCopy: () -> Unit,
    onShareClip: () -> Unit,
    onSharePlaylist: () -> Unit,
    onShareCard: () -> Unit,
    onDismiss: () -> Unit
) {
    val shareOptions = listOf(
        ShareOption(
            id = "original",
            title = "Share Original",
            description = "Share the original audio file",
            icon = painterResource(R.drawable.ic_audio_file),
            onClick = onShareOriginal
        ),
        ShareOption(
            id = "copy",
            title = "Copy Link",
            description = "Copy a link to this song",
            icon = painterResource(R.drawable.ic_link),
            onClick = onShareCopy
        ),
        ShareOption(
            id = "clip",
            title = "Share Clip",
            description = "Share a 30-second preview",
            icon = painterResource(R.drawable.ic_content_copy),
            onClick = onShareClip
        ),
        ShareOption(
            id = "playlist",
            title = "Add to Playlist",
            description = "Add this song to a playlist",
            icon = painterResource(R.drawable.ic_playlist_add),
            onClick = onSharePlaylist
        ),
        ShareOption(
            id = "card",
            title = "Share Card",
            description = "Share a beautiful music card",
            icon = painterResource(R.drawable.ic_content_copy),
            onClick = onShareCard
        )
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(Spacing16),
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = EmberCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            ShareHeader(
                songTitle = songTitle,
                songArtist = songArtist,
                onDismiss = onDismiss
            )
            
            HorizontalDivider(
                color = TextMuted.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            // Share options
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing16),
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                items(shareOptions) { option ->
                    ShareOptionItem(
                        option = option
                    )
                }
                
                // Bottom padding
                item {
                    Spacer(modifier = Modifier.height(Spacing24))
                }
            }
        }
    }
}

@Composable
private fun ShareHeader(
    songTitle: String,
    songArtist: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Share",
                style = MaterialTheme.typography.headlineSmall,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$songTitle — $songArtist",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .size(40.dp)
                .semantics {
                    role = Role.Button
                    contentDescription = "Close share sheet"
                }
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ShareOptionItem(
    option: ShareOption,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "optionScale"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = AnimationStandard,
        label = "glowAlpha"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = option.onClick
            )
            .semantics {
                role = Role.Button
                contentDescription = "${option.title}: ${option.description}"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmberElev1.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with glow effect
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.size(48.dp)
                ) {
                    drawShareIconGlow(
                        center = Offset(size.width / 2, size.height / 2),
                        alpha = glowAlpha
                    )
                }
                
                Icon(
                    painter = option.icon,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing16))
            
            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextStrong,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = option.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
            
            // Arrow indicator
            Icon(
                painter = painterResource(R.drawable.ic_content_copy),
                contentDescription = null,
                tint = TextMuted.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Press glow effect
        if (isPressed) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawPressGlow(
                    alpha = glowAlpha
                )
            }
        }
    }
}

private fun DrawScope.drawShareIconGlow(
    center: Offset,
    alpha: Float
) {
    if (alpha > 0) {
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
}

private fun DrawScope.drawPressGlow(
    alpha: Float
) {
    val glowColor = EmberFlame.copy(alpha = alpha * 0.1f)
    
    // Draw subtle glow around the entire card
    drawRect(
        color = glowColor,
        topLeft = Offset.Zero,
        size = size
    )
    
    // Draw accent line on the left
    drawRect(
        color = EmberFlame.copy(alpha = alpha * 0.3f),
        topLeft = Offset.Zero,
        size = androidx.compose.ui.geometry.Size(
            width = 4.dp.toPx(),
            height = size.height
        )
    )
}

data class ShareOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: Painter,
    val onClick: () -> Unit
)
