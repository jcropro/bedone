package app.ember.studio.feature.songs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import app.ember.studio.R
import app.ember.core.ui.theme.EmberTheme
import app.ember.core.ui.components.GlassMorphismCard
import app.ember.core.ui.design.TypographyBodyLarge
import app.ember.core.ui.design.TypographyBodyWeight
import app.ember.core.ui.design.TypographySubtitleMedium
import app.ember.core.ui.design.TypographySubtitleWeight
import app.ember.core.ui.design.TypographySubtitleOpacity
import app.ember.core.ui.design.TypographyLabelMedium
import app.ember.core.ui.design.TypographyLabelWeight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SongsList(
    modifier: Modifier = Modifier,
    songs: List<Song>,
    selectedSongs: Set<String> = emptySet(),
    isMultiSelectMode: Boolean = false,
    searchQuery: String = "",
    onSongClick: (Song) -> Unit = {},
    onSongLongClick: (Song) -> Unit = {},
    onSelectionChange: (Set<String>) -> Unit = {}
) {
    val listState = rememberLazyListState()
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(1.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Select All row (only in multi-select mode)
        if (isMultiSelectMode && songs.isNotEmpty()) {
            item {
                SelectAllRow(
                    totalSongs = songs.size,
                    selectedCount = selectedSongs.size,
                    onSelectAll = { 
                        onSelectionChange(songs.map { it.id }.toSet())
                    },
                    onClearAll = {
                        onSelectionChange(emptySet())
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        itemsIndexed(
            items = songs,
            key = { _, song -> song.id }
        ) { index, song ->
            SongItem(
                song = song,
                isSelected = selectedSongs.contains(song.id),
                isMultiSelectMode = isMultiSelectMode,
                onSongClick = onSongClick,
                onSongLongClick = onSongLongClick,
                onSelectionToggle = { 
                    val newSelection = if (selectedSongs.contains(song.id)) {
                        selectedSongs - song.id
                    } else {
                        selectedSongs + song.id
                    }
                    onSelectionChange(newSelection)
                }
            )
            
            if (index < songs.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
private fun SelectAllRow(
    modifier: Modifier = Modifier,
    totalSongs: Int,
    selectedCount: Int,
    onSelectAll: () -> Unit,
    onClearAll: () -> Unit
) {
    GlassMorphismCard(
        modifier = modifier.fillMaxWidth(),
        isHovered = false,
        onClick = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "$selectedCount of $totalSongs",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SongItem(
    modifier: Modifier = Modifier,
    song: Song,
    isSelected: Boolean = false,
    isMultiSelectMode: Boolean = false,
    onSongClick: (Song) -> Unit = {},
    onSongLongClick: (Song) -> Unit = {},
    onSelectionToggle: () -> Unit = {}
) {
    var showOverflowMenu by remember { mutableStateOf(false) }
    
    GlassMorphismCard(
        modifier = modifier.fillMaxWidth(),
        isHovered = isSelected,
        onClick = {
            if (isMultiSelectMode) {
                onSelectionToggle()
            } else {
                onSongClick(song)
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Artwork with Ember glyph
            SongArtwork(
                song = song,
                isSelected = isSelected,
                isMultiSelectMode = isMultiSelectMode
            )
            
            // Song info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = song.title,
                    fontSize = TypographyBodyLarge, // 16sp as per MASTER_BLUEPRINT
                    fontWeight = TypographyBodyWeight, // Medium (500) as per MASTER_BLUEPRINT
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${song.artist} • ${song.album}",
                    fontSize = TypographySubtitleMedium, // 14sp as per MASTER_BLUEPRINT
                    fontWeight = TypographySubtitleWeight, // Medium (500) as per MASTER_BLUEPRINT
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = TypographySubtitleOpacity), // 70% opacity as per MASTER_BLUEPRINT
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Duration/Date - MASTER_BLUEPRINT: Micro labels 12-13 (500) for meta info
            Text(
                text = formatDuration(song.duration),
                fontSize = TypographyLabelMedium, // 12sp as per MASTER_BLUEPRINT
                fontWeight = TypographyLabelWeight, // Medium (500) as per MASTER_BLUEPRINT
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Overflow menu
            IconButton(
                onClick = { showOverflowMenu = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SongArtwork(
    modifier: Modifier = Modifier,
    song: Song,
    isSelected: Boolean = false,
    isMultiSelectMode: Boolean = false
) {
    Box(
        modifier = modifier.size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        // MASTER_BLUEPRINT: 16dp art placeholder with brand glyph
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Brand glyph - Ember flame logo
            Icon(
                painter = painterResource(id = R.drawable.ic_ember_logo),
                contentDescription = "Song artwork",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Selection indicator
        if (isMultiSelectMode) {
            AnimatedVisibility(
                visible = isSelected,
                enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(tween(200)),
                label = "SelectionIndicator"
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        
        // Long audio badge
        if (song.isLongAudio) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { /* TODO: Open routing dialog */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "L",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Video badge
        if (song.hasVideo) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .clickable { /* TODO: Open video overlay */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "V",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val minutes = durationMs / 60000
    val seconds = (durationMs % 60000) / 1000
    return String.format("%d:%02d", minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun SongsListPreview() {
    EmberTheme {
        SongsList(
            songs = listOf(
                Song(
                    id = "1",
                    title = "Sample Song",
                    artist = "Sample Artist",
                    album = "Sample Album",
                    duration = 180000,
                    filePath = "/path/to/song.mp3",
                    dateAdded = System.currentTimeMillis(),
                    playCount = 5,
                    year = 2023,
                    size = 5000000,
                    bitrate = 320,
                    hasLyrics = true,
                    isLongAudio = false,
                    hasVideo = false
                ),
                Song(
                    id = "2",
                    title = "Long Audio Track",
                    artist = "Podcast Host",
                    album = "Weekly Podcast",
                    duration = 1200000, // 20 minutes
                    filePath = "/path/to/podcast.mp3",
                    dateAdded = System.currentTimeMillis(),
                    playCount = 2,
                    year = 2023,
                    size = 20000000,
                    bitrate = 128,
                    hasLyrics = false,
                    isLongAudio = true,
                    hasVideo = true
                )
            ),
            selectedSongs = setOf("1"),
            isMultiSelectMode = true
        )
    }
}
