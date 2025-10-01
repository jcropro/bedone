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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme

@Composable
fun NowPlayingScaffold(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    durationMs: Long,
    positionMs: Long,
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    coverArt: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section: Album art and metadata
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            androidx.compose.animation.AnimatedVisibility(visible = coverArt != null, enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                ) {
                    coverArt?.invoke()
                    // Gradient scrim over album art for text readability
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
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

        // Seek bar
        val total = durationMs.coerceAtLeast(1L).toFloat()
        val pos = positionMs.coerceIn(0L, durationMs).toFloat()
        Slider(
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatTime(durationMs),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

        Spacer(modifier = Modifier.height(24.dp))

        // Context row (e.g., lyrics, queue, equalizer) - Placeholder
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Lyrics */ }) {
                Text("🎵", style = MaterialTheme.typography.titleLarge)
            }
            IconButton(onClick = { /* TODO: Queue */ }) {
                Text("📋", style = MaterialTheme.typography.titleLarge)
            }
            IconButton(onClick = { /* TODO: Equalizer */ }) {
                Text("🎛", style = MaterialTheme.typography.titleLarge)
            }
        }
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

@Preview(name = "NowPlaying - Light")
@Composable
private fun PreviewNowPlayingLight() {
    EmberTheme(darkTheme = false) {
        NowPlayingScaffold(
            title = "Song Title",
            artist = "Artist Name",
            durationMs = 300000,
            positionMs = 120000,
            isPlaying = true,
            onTogglePlayPause = {},
            onPlayNext = {},
            onPlayPrevious = {},
            onSeekTo = {},
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

@Preview(name = "NowPlaying - Dark")
@Composable
private fun PreviewNowPlayingDark() {
    EmberTheme(darkTheme = true) {
        NowPlayingScaffold(
            title = "Song Title",
            artist = "Artist Name",
            durationMs = 300000,
            positionMs = 120000,
            isPlaying = false,
            onTogglePlayPause = {},
            onPlayNext = {},
            onPlayPrevious = {},
            onSeekTo = {},
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
