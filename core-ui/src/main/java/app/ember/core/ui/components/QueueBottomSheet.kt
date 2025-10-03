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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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
import kotlin.random.Random

/**
 * Premium queue bottom sheet with drag-and-drop reordering
 */
@Composable
fun QueueBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    queueItems: List<QueueItem>,
    currentIndex: Int = 0,
    onItemClick: (Int) -> Unit = {},
    onItemRemove: (Int) -> Unit = {},
    onItemReorder: (Int, Int) -> Unit = { _, _ -> },
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
                // Queue content
                QueueContent(
                    queueItems = queueItems,
                    currentIndex = currentIndex,
                    onItemClick = onItemClick,
                    onItemRemove = onItemRemove,
                    onItemReorder = onItemReorder,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun QueueContent(
    queueItems: List<QueueItem>,
    currentIndex: Int,
    onItemClick: (Int) -> Unit,
    onItemRemove: (Int) -> Unit,
    onItemReorder: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val listState = rememberLazyListState()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
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
            QueueHeader(
                itemCount = queueItems.size,
                onDismiss = onDismiss
            )
            
            HorizontalDivider(
                color = TextMuted.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            // Queue list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing16),
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                itemsIndexed(queueItems) { index, item ->
                    QueueItemRow(
                        item = item,
                        index = index,
                        isCurrent = index == currentIndex,
                        isPlaying = index == currentIndex,
                        onClick = { onItemClick(index) },
                        onRemove = { onItemRemove(index) },
                        onReorder = { fromIndex, toIndex -> onItemReorder(fromIndex, toIndex) }
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
private fun QueueHeader(
    itemCount: Int,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Queue",
                style = MaterialTheme.typography.headlineSmall,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$itemCount songs",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
        
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .semantics {
                    role = Role.Button
                    contentDescription = "Close queue"
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
private fun QueueItemRow(
    item: QueueItem,
    index: Int,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onReorder: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = AnimationGentle,
        label = "itemScale"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isCurrent) 1f else 0f,
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
                onClick = onClick
            )
            .pointerInput(index) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false }
                ) { change, _ ->
                    // Handle drag reordering logic here
                    // This would typically involve calculating drop zones
                }
            }
            .semantics {
                role = Role.Button
                contentDescription = "${item.title} by ${item.artist}"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) {
                EmberFlame.copy(alpha = 0.1f)
            } else {
                EmberElev1.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrent) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drag handle
            Icon(
                imageVector = Icons.Filled.Share, // TODO: Replace with proper drag handle icon
                contentDescription = "Drag to reorder",
                tint = TextMuted.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(Spacing12))
            
            // Track number or play indicator
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isCurrent) {
                    // Playing indicator with animation
                    PlayingIndicator(isPlaying = isPlaying)
                } else {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(Spacing12))
            
            // Track info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrent) EmberFlame else TextStrong,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Duration
            Text(
                text = formatDuration(item.durationMs),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier.padding(start = Spacing8)
            )
            
            // Remove button
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Remove from queue"
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = TextMuted.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        // Current track glow effect
        if (isCurrent) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawCurrentTrackGlow(
                    alpha = glowAlpha
                )
            }
        }
    }
}

@Composable
private fun PlayingIndicator(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val pulseScale by animateFloatAsState(
        targetValue = if (isPlaying) 1.2f else 1f,
        animationSpec = AnimationStandard,
        label = "pulseScale"
    )
    
    Box(
        modifier = modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulse ring
        if (isPlaying) {
            Canvas(
                modifier = Modifier.size(24.dp)
            ) {
                drawPulseRing(
                    center = Offset(size.width / 2, size.height / 2),
                    scale = pulseScale
                )
            }
        }
        
        // Play icon
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = EmberFlame,
            modifier = Modifier.size(16.dp)
        )
    }
}

private fun DrawScope.drawCurrentTrackGlow(
    alpha: Float
) {
    val glowColor = EmberFlame.copy(alpha = alpha * 0.2f)
    
    // Draw subtle glow around the entire card
    drawRect(
        color = glowColor,
        topLeft = Offset.Zero,
        size = size
    )
    
    // Draw accent line on the left
    drawRect(
        color = EmberFlame.copy(alpha = alpha),
        topLeft = Offset.Zero,
        size = androidx.compose.ui.geometry.Size(
            width = 4.dp.toPx(),
            height = size.height
        )
    )
}

private fun DrawScope.drawPulseRing(
    center: Offset,
    scale: Float
) {
    val radius = (size.minDimension / 2) * scale
    val color = EmberFlame.copy(alpha = 0.6f / scale)
    
    drawCircle(
        color = color,
        radius = radius,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

data class QueueItem(
    val id: String,
    val title: String,
    val artist: String,
    val durationMs: Long,
    val albumArt: String? = null
)
