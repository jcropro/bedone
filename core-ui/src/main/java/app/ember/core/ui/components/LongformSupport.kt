package app.ember.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme

/**
 * Longform content support for podcasts and audiobooks
 */
@Composable
fun LongformSupport(
    modifier: Modifier = Modifier,
    content: LongformContent,
    playbackSpeed: Float,
    currentPosition: Long,
    duration: Long,
    bookmarks: List<Bookmark>,
    onPlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onAddBookmark: () -> Unit,
    onRemoveBookmark: (Bookmark) -> Unit,
    onJumpToBookmark: (Bookmark) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Content header
        LongformHeader(content = content)
        
        HorizontalDivider()
        
        // Playback controls
        LongformPlaybackControls(
            playbackSpeed = playbackSpeed,
            currentPosition = currentPosition,
            duration = duration,
            onPlayPause = onPlayPause,
            onSeekTo = onSeekTo,
            onSpeedChange = onSpeedChange
        )
        
        HorizontalDivider()
        
        // Bookmarks section
        LongformBookmarks(
            bookmarks = bookmarks,
            onAddBookmark = onAddBookmark,
            onRemoveBookmark = onRemoveBookmark,
            onJumpToBookmark = onJumpToBookmark
        )
        
        HorizontalDivider()
        
        // Chapter navigation (for audiobooks)
        if (content.type == LongformType.Audiobook) {
            LongformChapters(
                chapters = content.chapters,
                currentChapter = content.currentChapter,
                onChapterSelected = { /* TODO: Implement chapter selection */ }
            )
        }
    }
}

@Composable
private fun LongformHeader(content: LongformContent) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Content icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when (content.type) {
                            LongformType.Podcast -> MaterialTheme.colorScheme.primary
                            LongformType.Audiobook -> MaterialTheme.colorScheme.secondary
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (content.type) {
                        LongformType.Podcast -> "🎙️"
                        LongformType.Audiobook -> "📚"
                    },
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            // Content info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = content.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${content.type.displayName} • ${formatDuration(content.duration)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LongformPlaybackControls(
    playbackSpeed: Float,
    currentPosition: Long,
    duration: Long,
    onPlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSpeedChange: (Float) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Playback Controls",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Speed control
        SpeedControlSection(
            playbackSpeed = playbackSpeed,
            onSpeedChange = onSpeedChange
        )
        
        // Progress and seek
        ProgressSection(
            currentPosition = currentPosition,
            duration = duration,
            onSeekTo = onSeekTo
        )
    }
}

@Composable
private fun SpeedControlSection(
    playbackSpeed: Float,
    onSpeedChange: (Float) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Playback Speed",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            speeds.forEach { speed ->
                FilterChip(
                    onClick = { onSpeedChange(speed) },
                    label = { Text("${speed}x") },
                    selected = playbackSpeed == speed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ProgressSection(
    currentPosition: Long,
    duration: Long,
    onSeekTo: (Long) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Progress",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { onSeekTo(it.toLong()) },
            valueRange = 0f..duration.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(currentPosition),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LongformBookmarks(
    bookmarks: List<Bookmark>,
    onAddBookmark: () -> Unit,
    onRemoveBookmark: (Bookmark) -> Unit,
    onJumpToBookmark: (Bookmark) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bookmarks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            OutlinedButton(onClick = onAddBookmark) {
                Text("Add Bookmark")
            }
        }
        
        if (bookmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No bookmarks yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bookmarks.forEach { bookmark ->
                    BookmarkItem(
                        bookmark = bookmark,
                        onRemove = { onRemoveBookmark(bookmark) },
                        onJumpTo = { onJumpToBookmark(bookmark) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarkItem(
    bookmark: Bookmark,
    onRemove: () -> Unit,
    onJumpTo: () -> Unit
) {
    Card(
        onClick = onJumpTo,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bookmark.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = formatDuration(bookmark.position),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            OutlinedButton(onClick = onRemove) {
                Text("Remove")
            }
        }
    }
}

@Composable
private fun LongformChapters(
    chapters: List<Chapter>,
    currentChapter: Int,
    onChapterSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Chapters",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chapters.forEachIndexed { index, chapter ->
                ChapterItem(
                    chapter = chapter,
                    isCurrent = index == currentChapter,
                    onClick = { onChapterSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun ChapterItem(
    chapter: Chapter,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isCurrent) {
            androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${chapter.number}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isCurrent) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isCurrent) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                Text(
                    text = formatDuration(chapter.duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCurrent) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

// Data classes
data class LongformContent(
    val title: String,
    val author: String,
    val type: LongformType,
    val duration: Long,
    val chapters: List<Chapter> = emptyList(),
    val currentChapter: Int = 0
)

data class Bookmark(
    val id: String,
    val title: String,
    val position: Long,
    val timestamp: Long
)

data class Chapter(
    val number: Int,
    val title: String,
    val duration: Long,
    val startPosition: Long
)

enum class LongformType(val displayName: String) {
    Podcast("Podcast"),
    Audiobook("Audiobook")
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    return when {
        hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
        else -> String.format("%d:%02d", minutes, seconds)
    }
}

@Preview(showBackground = true)
@Composable
private fun LongformSupportPreview() {
    EmberTheme {
        LongformSupport(
            content = LongformContent(
                title = "Sample Podcast Episode",
                author = "Podcast Host",
                type = LongformType.Podcast,
                duration = 3600000L
            ),
            playbackSpeed = 1.0f,
            currentPosition = 1800000L,
            duration = 3600000L,
            bookmarks = listOf(
                Bookmark("1", "Interesting point", 300000L, System.currentTimeMillis()),
                Bookmark("2", "Key insight", 1200000L, System.currentTimeMillis())
            ),
            onPlayPause = {},
            onSeekTo = {},
            onSpeedChange = {},
            onAddBookmark = {},
            onRemoveBookmark = {},
            onJumpToBookmark = {}
        )
    }
}
