package app.ember.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.*
import app.ember.core.ui.theme.EmberTheme
import kotlinx.coroutines.delay

/**
 * Enhanced Now Playing screen with palette-driven backdrop and premium visuals
 */
@Composable
fun NowPlayingV2(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    durationMs: Long,
    positionMs: Long,
    isPlaying: Boolean,
    isLiked: Boolean = false,
    playbackSpeed: Float = 1.0f,
    sleepTimerMinutes: Int? = null,
    shuffleMode: ShuffleMode = ShuffleMode.Off,
    repeatMode: PlayerRepeatMode = PlayerRepeatMode.Off,
    onTogglePlayPause: () -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onLikeToggle: () -> Unit = {},
    onQueueClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onSpeedClick: () -> Unit = {},
    onTimerClick: () -> Unit = {},
    onEqualizerClick: () -> Unit = {},
    onShuffleToggle: () -> Unit = {},
    onRepeatToggle: () -> Unit = {},
    paletteColor: Color? = null,
    coverArt: @Composable (() -> Unit)? = null,
    queueItems: List<QueueItem> = emptyList(),
    currentQueueIndex: Int = 0,
    isQueueVisible: Boolean = false,
    onQueueItemClick: (Int) -> Unit = {},
    onQueueItemRemove: (Int) -> Unit = {},
    onQueueItemReorder: (Int, Int) -> Unit = { _, _ -> },
    onQueueDismiss: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Palette-driven backdrop
        PaletteBackdrop(
            modifier = Modifier.fillMaxSize(),
            paletteColor = paletteColor
        )
        
        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section: Album art and metadata
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.animation.AnimatedVisibility(visible = coverArt != null, enter = fadeIn(), exit = fadeOut()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.large)
                    ) {
                        coverArt?.invoke()
                        // Gradient scrim over album art for text readability
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                                        startY = 0f,
                                        endY = Float.POSITIVE_INFINITY
                                    )
                                )
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Flame-accented seek bar
            val total = durationMs.coerceAtLeast(1L).toFloat()
            val pos = positionMs.coerceIn(0L, durationMs).toFloat()
            FlameSeekBar(
                value = pos,
                onValueChange = { onSeekTo(it.toLong()) },
                valueRange = 0f..total,
                modifier = Modifier.fillMaxWidth()
            )

            // Time indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(positionMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = formatTime(durationMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Transport controls
            RowControls(
                isPlaying = isPlaying,
                onTogglePlayPause = onTogglePlayPause,
                onPlayNext = onPlayNext,
                onPlayPrevious = onPlayPrevious
            )

            Spacer(modifier = Modifier.height(Spacing16))

            // Shuffle/Repeat controls
            ShuffleRepeatButton(
                shuffleMode = shuffleMode,
                repeatMode = repeatMode,
                onShuffleToggle = onShuffleToggle,
                onRepeatToggle = onRepeatToggle,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing24))

            // Secondary actions tray
            SecondaryActionsTray(
                isLiked = isLiked,
                playbackSpeed = playbackSpeed,
                sleepTimerMinutes = sleepTimerMinutes,
                onLikeToggle = onLikeToggle,
                onQueueClick = onQueueClick,
                onShareClick = onShareClick,
                onSpeedClick = onSpeedClick,
                onTimerClick = onTimerClick,
                onEqualizerClick = onEqualizerClick
            )
        }
        
        // Queue bottom sheet
        QueueBottomSheet(
            isVisible = isQueueVisible,
            onDismiss = onQueueDismiss,
            queueItems = queueItems,
            currentIndex = currentQueueIndex,
            onItemClick = onQueueItemClick,
            onItemRemove = onQueueItemRemove,
            onItemReorder = onQueueItemReorder
        )
    }
}

@Composable
private fun RowControls(
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPlayPrevious) {
            Text("⏮", style = MaterialTheme.typography.titleLarge)
        }
        FilledIconButton(onClick = onTogglePlayPause) {
            androidx.compose.animation.AnimatedVisibility(visible = isPlaying, enter = fadeIn(), exit = fadeOut()) {
                Text("⏸", style = MaterialTheme.typography.titleLarge)
            }
            androidx.compose.animation.AnimatedVisibility(visible = !isPlaying, enter = fadeIn(), exit = fadeOut()) {
                Text("▶", style = MaterialTheme.typography.titleLarge)
            }
        }
        IconButton(onClick = onPlayNext) {
            Text("⏭", style = MaterialTheme.typography.titleLarge)
        }
    }
}

private fun formatTime(ms: Long): String {
    val seconds = ms / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@Preview(name = "NowPlayingV2 - Light")
@Composable
private fun PreviewNowPlayingV2Light() {
    EmberTheme(useDarkTheme = false) {
        NowPlayingV2(
            title = "Song Title",
            artist = "Artist Name",
            durationMs = 300000,
            positionMs = 120000,
            isPlaying = true,
            isLiked = true,
            playbackSpeed = 1.5f,
            sleepTimerMinutes = 30,
            shuffleMode = ShuffleMode.On,
            repeatMode = PlayerRepeatMode.All,
            onTogglePlayPause = {},
            onPlayNext = {},
            onPlayPrevious = {},
            onSeekTo = {},
            onLikeToggle = {},
            onQueueClick = {},
            onShareClick = {},
            onSpeedClick = {},
            onTimerClick = {},
            onEqualizerClick = {},
            onShuffleToggle = {},
            onRepeatToggle = {},
            paletteColor = Color(0xFF7B5CE6),
            queueItems = listOf(
                QueueItem("1", "Song Title", "Artist Name", 180000),
                QueueItem("2", "Next Song", "Another Artist", 240000),
                QueueItem("3", "Third Song", "Third Artist", 200000)
            ),
            currentQueueIndex = 0,
            isQueueVisible = false,
            onQueueItemClick = {},
            onQueueItemRemove = {},
            onQueueItemReorder = { _, _ -> },
            onQueueDismiss = {},
            coverArt = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎵",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
        )
    }
}

@Preview(name = "NowPlayingV2 - Dark")
@Composable
private fun PreviewNowPlayingV2Dark() {
    EmberTheme(useDarkTheme = true) {
        NowPlayingV2(
            title = "Song Title",
            artist = "Artist Name",
            durationMs = 300000,
            positionMs = 120000,
            isPlaying = false,
            isLiked = false,
            playbackSpeed = 1.0f,
            sleepTimerMinutes = null,
            shuffleMode = ShuffleMode.Off,
            repeatMode = PlayerRepeatMode.Off,
            onTogglePlayPause = {},
            onPlayNext = {},
            onPlayPrevious = {},
            onSeekTo = {},
            onLikeToggle = {},
            onQueueClick = {},
            onShareClick = {},
            onSpeedClick = {},
            onTimerClick = {},
            onEqualizerClick = {},
            onShuffleToggle = {},
            onRepeatToggle = {},
            paletteColor = Color(0xFF00D8FF),
            queueItems = listOf(
                QueueItem("1", "Song Title", "Artist Name", 180000),
                QueueItem("2", "Next Song", "Another Artist", 240000)
            ),
            currentQueueIndex = 1,
            isQueueVisible = false,
            onQueueItemClick = {},
            onQueueItemRemove = {},
            onQueueItemReorder = { _, _ -> },
            onQueueDismiss = {},
            coverArt = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎵",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
        )
    }
}

