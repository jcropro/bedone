package app.ember.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme

/**
 * Enhanced lyrics display with synchronization and visual effects
 */
@Composable
fun LyricsDisplay(
    modifier: Modifier = Modifier,
    lyricsText: String,
    currentPositionMs: Long,
    isPlaying: Boolean,
    onSeekTo: (Long) -> Unit = {},
    showTimestamps: Boolean = true,
    enableSynchronization: Boolean = true
) {
    var parsedLines by remember { mutableStateOf<List<LyricsLine>>(emptyList()) }
    var currentLineIndex by remember { mutableStateOf(-1) }
    
    // Parse lyrics when text changes
    LaunchedEffect(lyricsText) {
        parsedLines = parseLyricsText(lyricsText)
    }
    
    // Update current line based on position
    LaunchedEffect(currentPositionMs, parsedLines) {
        if (enableSynchronization && parsedLines.isNotEmpty()) {
            currentLineIndex = findCurrentLineIndex(parsedLines, currentPositionMs)
        }
    }
    
    if (parsedLines.isEmpty()) {
        EmptyLyricsState(modifier = modifier)
    } else {
        LyricsContent(
            modifier = modifier,
            lyricsLines = parsedLines,
            currentLineIndex = currentLineIndex,
            onSeekTo = onSeekTo,
            showTimestamps = showTimestamps
        )
    }
}

@Composable
private fun LyricsContent(
    modifier: Modifier = Modifier,
    lyricsLines: List<LyricsLine>,
    currentLineIndex: Int,
    onSeekTo: (Long) -> Unit,
    showTimestamps: Boolean
) {
    val listState = rememberLazyListState()
    
    // Auto-scroll to current line
    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex >= 0) {
            listState.animateScrollToItem(currentLineIndex)
        }
    }
    
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(lyricsLines) { index, line ->
            LyricsLineItem(
                line = line,
                isCurrent = index == currentLineIndex,
                isPast = index < currentLineIndex,
                isFuture = index > currentLineIndex,
                showTimestamp = showTimestamps,
                onClick = { onSeekTo(line.timestampMs) }
            )
        }
    }
}

@Composable
private fun LyricsLineItem(
    line: LyricsLine,
    isCurrent: Boolean,
    isPast: Boolean,
    isFuture: Boolean,
    showTimestamp: Boolean,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = when {
            isCurrent -> 1f
            isPast -> 0.6f
            isFuture -> 0.4f
            else -> 0.4f
        },
        animationSpec = tween(durationMillis = 300),
        label = "lyrics-alpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isCurrent) 1.05f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "lyrics-scale"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showTimestamp) {
            Text(
                text = line.timestampText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                modifier = Modifier.width(60.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        
        if (isCurrent) {
            // Current line indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                // Empty content for indicator dot
            }
            Spacer(modifier = Modifier.width(12.dp))
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }
        
        Text(
            text = line.text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isCurrent) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
            },
            fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun EmptyLyricsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎵",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No lyrics available",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "Add lyrics to see them synchronized with the music",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// Helper functions
private fun parseLyricsText(lyricsText: String): List<LyricsLine> {
    if (lyricsText.isBlank()) return emptyList()
    
    val lines = lyricsText.split("\n")
    val parsedLines = mutableListOf<LyricsLine>()
    
    for (line in lines) {
        val trimmedLine = line.trim()
        if (trimmedLine.isBlank()) continue
        
        // Try to parse timestamp format [mm:ss.xxx] or [mm:ss]
        val timestampRegex = Regex("""\[(\d{1,2}):(\d{2})(?:\.(\d{1,3}))?\]\s*(.*)""")
        val match = timestampRegex.find(trimmedLine)
        
        if (match != null) {
            val minutes = match.groupValues[1].toInt()
            val seconds = match.groupValues[2].toInt()
            val milliseconds = match.groupValues[3].let { 
                if (it.isNotEmpty()) it.padEnd(3, '0').toInt() else 0 
            }
            val text = match.groupValues[4].trim()
            
            val timestampMs = (minutes * 60 + seconds) * 1000L + milliseconds
            parsedLines.add(LyricsLine(timestampMs, text))
        } else {
            // Line without timestamp - add with previous timestamp or 0
            val timestamp = if (parsedLines.isNotEmpty()) {
                parsedLines.last().timestampMs + 1000L // 1 second after previous
            } else {
                0L
            }
            parsedLines.add(LyricsLine(timestamp, trimmedLine))
        }
    }
    
    return parsedLines.sortedBy { it.timestampMs }
}

private fun findCurrentLineIndex(lyricsLines: List<LyricsLine>, positionMs: Long): Int {
    if (lyricsLines.isEmpty()) return -1
    
    // Find the last line that hasn't started yet
    for (i in lyricsLines.indices) {
        if (lyricsLines[i].timestampMs > positionMs) {
            return (i - 1).coerceAtLeast(0)
        }
    }
    
    // If we're past all lyrics, return the last line
    return lyricsLines.lastIndex
}

data class LyricsLine(
    val timestampMs: Long,
    val text: String
) {
    val timestampText: String
        get() = formatTimestamp(timestampMs)
    
    private fun formatTimestamp(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

@Preview(showBackground = true)
@Composable
private fun LyricsDisplayPreview() {
    EmberTheme {
        LyricsDisplay(
            lyricsText = """
[00:00.000] Verse 1
[00:15.500] This is a sample song
[00:20.000] With synchronized lyrics
[00:25.500] That will highlight as we play
[00:30.000] 
[00:35.000] Chorus
[00:40.000] Sing along with the music
[00:45.000] Follow the highlighted words
[00:50.000] Enjoy the synchronized experience
""",
            currentPositionMs = 20000L,
            isPlaying = true
        )
    }
}
