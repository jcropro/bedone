package app.ember.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.res.painterResource
import app.ember.core.ui.R
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
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
 * Premium Ringtone Studio with waveform UI and export functionality
 */
@Composable
fun RingtoneStudio(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    songTitle: String,
    songArtist: String,
    durationMs: Long,
    onExportRingtone: (Long, Long) -> Unit,
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
                // Ringtone studio content
                RingtoneStudioContent(
                    songTitle = songTitle,
                    songArtist = songArtist,
                    durationMs = durationMs,
                    onExportRingtone = onExportRingtone,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun RingtoneStudioContent(
    songTitle: String,
    songArtist: String,
    durationMs: Long,
    onExportRingtone: (Long, Long) -> Unit,
    onDismiss: () -> Unit
) {
    var startTimeMs by remember { mutableStateOf(0L) }
    var endTimeMs by remember { mutableStateOf(minOf(30000L, durationMs)) } // Default 30 seconds
    var isPlaying by remember { mutableStateOf(false) }
    
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
            RingtoneStudioHeader(
                songTitle = songTitle,
                songArtist = songArtist,
                onExport = { onExportRingtone(startTimeMs, endTimeMs) },
                onDismiss = onDismiss
            )
            
            HorizontalDivider(
                color = TextMuted.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing16),
                verticalArrangement = Arrangement.spacedBy(Spacing16)
            ) {
                // Waveform visualization
                RingtoneWaveform(
                    durationMs = durationMs,
                    startTimeMs = startTimeMs,
                    endTimeMs = endTimeMs,
                    isPlaying = isPlaying
                )
                
                // Playback controls
                RingtoneControls(
                    isPlaying = isPlaying,
                    onPlayPause = { isPlaying = !isPlaying },
                    onStop = { isPlaying = false }
                )
                
                // Time range controls
                RingtoneTimeControls(
                    durationMs = durationMs,
                    startTimeMs = startTimeMs,
                    endTimeMs = endTimeMs,
                    onStartTimeChanged = { startTimeMs = it },
                    onEndTimeChanged = { endTimeMs = it }
                )
                
                // Export button
                RingtoneExportButton(
                    startTimeMs = startTimeMs,
                    endTimeMs = endTimeMs,
                    onExport = { onExportRingtone(startTimeMs, endTimeMs) }
                )
            }
        }
    }
}

@Composable
private fun RingtoneStudioHeader(
    songTitle: String,
    songArtist: String,
    onExport: () -> Unit,
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
                text = "Ringtone Studio",
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
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing8)
        ) {
            // Export button
            IconButton(
                onClick = onExport,
                modifier = Modifier
                    .size(40.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Export ringtone"
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_file_download),
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(40.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Close ringtone studio"
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
}

@Composable
private fun RingtoneWaveform(
    durationMs: Long,
    startTimeMs: Long,
    endTimeMs: Long,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    // Waveform animation for playing state
    val waveformAnimation by rememberInfiniteTransition(label = "waveform").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveformAnimation"
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmberElev1.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing16)
        ) {
            Text(
                text = "Waveform",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                drawRingtoneWaveform(
                    durationMs = durationMs,
                    startTimeMs = startTimeMs,
                    endTimeMs = endTimeMs,
                    isPlaying = isPlaying,
                    waveformAnimation = waveformAnimation
                )
            }
        }
    }
}

@Composable
private fun RingtoneControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Play/Pause button
        IconButton(
            onClick = onPlayPause,
            modifier = Modifier
                .size(56.dp)
                .semantics {
                    role = Role.Button
                    contentDescription = if (isPlaying) "Pause preview" else "Play preview"
                }
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = EmberFlame,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(Spacing16))
        
        // Volume indicator
        Icon(
            painter = painterResource(R.drawable.ic_volume_up),
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun RingtoneTimeControls(
    durationMs: Long,
    startTimeMs: Long,
    endTimeMs: Long,
    onStartTimeChanged: (Long) -> Unit,
    onEndTimeChanged: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing12)
    ) {
        // Start time slider
        Column {
            Text(
                text = "Start Time: ${formatTime(startTimeMs)}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextStrong
            )
            Slider(
                value = startTimeMs.toFloat(),
                onValueChange = { onStartTimeChanged(it.toLong()) },
                valueRange = 0f..endTimeMs.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // End time slider
        Column {
            Text(
                text = "End Time: ${formatTime(endTimeMs)}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextStrong
            )
            Slider(
                value = endTimeMs.toFloat(),
                onValueChange = { onEndTimeChanged(it.toLong()) },
                valueRange = startTimeMs.toFloat()..durationMs.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RingtoneExportButton(
    startTimeMs: Long,
    endTimeMs: Long,
    onExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "exportScale"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = AnimationStandard,
        label = "glowAlpha"
    )
    
    val duration = endTimeMs - startTimeMs
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onExport
            )
            .semantics {
                role = Role.Button
                contentDescription = "Export ringtone (${formatTime(duration)})"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmberFlame.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing16),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_file_download),
                contentDescription = null,
                tint = EmberFlame,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(Spacing12))
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Export Ringtone",
                    style = MaterialTheme.typography.titleMedium,
                    color = EmberFlame,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Duration: ${formatTime(duration)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
        
        // Glow effect
        if (isPressed) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawExportGlow(
                    alpha = glowAlpha
                )
            }
        }
    }
}

private fun DrawScope.drawRingtoneWaveform(
    durationMs: Long,
    startTimeMs: Long,
    endTimeMs: Long,
    isPlaying: Boolean
) {
    val centerY = size.height / 2
    val barWidth = 2.dp.toPx()
    val barSpacing = 1.dp.toPx()
    val totalBars = (size.width / (barWidth + barSpacing)).toInt()
    
    // Draw waveform bars
    for (i in 0 until totalBars) {
        val x = i * (barWidth + barSpacing)
        val progress = i.toFloat() / totalBars
        val timeMs = (progress * durationMs).toLong()
        
        val isInRange = timeMs >= startTimeMs && timeMs <= endTimeMs
        val barHeight = if (isInRange) {
            (size.height * 0.8f * (0.3f + 0.7f * kotlin.math.sin(progress * Math.PI * 4).toFloat()))
        } else {
            size.height * 0.2f
        }
        
        val color = when {
            isInRange && isPlaying -> EmberFlame
            isInRange -> EmberFlame.copy(alpha = 0.7f)
            else -> TextMuted.copy(alpha = 0.3f)
        }
        
        drawRect(
            color = color,
            topLeft = Offset(x, centerY - barHeight / 2),
            size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
        )
    }
    
    // Draw selection indicators
    val startX = (startTimeMs.toFloat() / durationMs) * size.width
    val endX = (endTimeMs.toFloat() / durationMs) * size.width
    
    // Start indicator
    drawLine(
        color = AccentIce,
        start = Offset(startX, 0f),
        end = Offset(startX, size.height),
        strokeWidth = 2.dp.toPx()
    )
    
    // End indicator
    drawLine(
        color = AccentIce,
        start = Offset(endX, 0f),
        end = Offset(endX, size.height),
        strokeWidth = 2.dp.toPx()
    )
}

private fun DrawScope.drawExportGlow(
    alpha: Float
) {
    val glowColor = EmberFlame.copy(alpha = alpha * 0.2f)
    
    // Draw subtle glow around the button
    drawRect(
        color = glowColor,
        topLeft = Offset.Zero,
        size = size
    )
    
    // Draw accent line on the left
    drawRect(
        color = EmberFlame.copy(alpha = alpha * 0.5f),
        topLeft = Offset.Zero,
        size = androidx.compose.ui.geometry.Size(
            width = 4.dp.toPx(),
            height = size.height
        )
    )
}

private fun DrawScope.drawRingtoneWaveform(
    durationMs: Long,
    startTimeMs: Long,
    endTimeMs: Long,
    isPlaying: Boolean,
    waveformAnimation: Float
) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    val centerY = canvasHeight / 2
    
    // Background
    drawRect(
        color = EmberInk.copy(alpha = 0.1f),
        topLeft = Offset.Zero,
        size = size
    )
    
    // Generate waveform data (simulated)
    val waveformPoints = generateWaveformData(canvasWidth.toInt())
    
    // Draw waveform bars
    waveformPoints.forEachIndexed { index, amplitude ->
        val x = index * (canvasWidth / waveformPoints.size)
        val barWidth = (canvasWidth / waveformPoints.size) * 0.8f
        val barHeight = amplitude * canvasHeight * 0.8f
        
        // Selection highlighting
        val isInSelection = x >= (startTimeMs.toFloat() / durationMs.toFloat()) * canvasWidth &&
                           x <= (endTimeMs.toFloat() / durationMs.toFloat()) * canvasWidth
        
        val barColor = if (isInSelection) {
            EmberFlame.copy(alpha = 0.8f)
        } else {
            TextMuted.copy(alpha = 0.4f)
        }
        
        // Animated bars when playing
        val animatedHeight = if (isPlaying) {
            barHeight * (0.8f + 0.4f * sin(waveformAnimation * Math.PI * 2 + index * 0.1f).toFloat())
        } else {
            barHeight
        }
        
        drawRect(
            color = barColor,
            topLeft = Offset(x, centerY - animatedHeight / 2),
            size = androidx.compose.ui.geometry.Size(barWidth, animatedHeight)
        )
    }
    
    // Selection handles
    val startX = (startTimeMs.toFloat() / durationMs.toFloat()) * canvasWidth
    val endX = (endTimeMs.toFloat() / durationMs.toFloat()) * canvasWidth
    
    // Start handle
    drawRect(
        color = EmberFlame,
        topLeft = Offset(startX - 2.dp.toPx(), 0f),
        size = androidx.compose.ui.geometry.Size(4.dp.toPx(), canvasHeight)
    )
    
    // End handle
    drawRect(
        color = EmberFlame,
        topLeft = Offset(endX - 2.dp.toPx(), 0f),
        size = androidx.compose.ui.geometry.Size(4.dp.toPx(), canvasHeight)
    )
    
    // Selection area highlight
    drawRect(
        color = EmberFlame.copy(alpha = 0.1f),
        topLeft = Offset(startX, 0f),
        size = androidx.compose.ui.geometry.Size(endX - startX, canvasHeight)
    )
}

private fun generateWaveformData(width: Int): List<Float> {
    return (0 until width / 4).map { index ->
        // Generate realistic waveform data with varying amplitudes
        val baseAmplitude = 0.3f + 0.4f * sin(index * 0.1f).toFloat()
        val noise = (kotlin.random.Random.nextFloat() - 0.5f) * 0.2f
        (baseAmplitude + noise).coerceIn(0.1f, 1f)
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
