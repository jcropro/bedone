package app.ember.studio.feature.videos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.EmberCard
import app.ember.core.ui.design.EmberFlame
import app.ember.core.ui.design.TextMuted
import app.ember.core.ui.design.TextStrong
import app.ember.core.ui.theme.EmberTheme
import app.ember.studio.LibraryTab
import app.ember.core.ui.components.VideosEmptyState
import app.ember.core.ui.components.VideoGridSkeleton
import app.ember.studio.feature.songs.SortDirection

// Data classes
data class VideoItem(
    val id: String,
    val title: String,
    val artist: String? = null,
    val durationMs: Long,
    val fileSizeBytes: Long,
    val resolution: String? = null,
    val frameRate: Int? = null,
    val bitrate: Int? = null,
    val codec: String? = null,
    val thumbnailUri: String? = null,
    val lastModified: Long,
    val isMusicVideo: Boolean = false,
    val isLiveRecording: Boolean = false
)

enum class VideoSortOption(val displayName: String) {
    Title("Title"),
    Artist("Artist"),
    Duration("Duration"),
    FileSize("File Size"),
    Resolution("Resolution"),
    LastModified("Last Modified")
}

enum class VideoFilterOption(val displayName: String) {
    RecentlyAdded("Recently Added"),
    MusicVideos("Music Videos"),
    LiveRecordings("Live Recordings"),
    HighResolution("High Resolution"),
    LargeFiles("Large Files"),
    SmallFiles("Small Files")
}

enum class VideoViewMode(val displayName: String) {
    Grid("Grid"),
    List("List")
}

// Helper functions
private fun formatDuration(ms: Long): String {
    val hours = ms / (1000 * 60 * 60)
    val minutes = (ms % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (ms % (1000 * 60)) / 1000
    
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m ${seconds}s"
        else -> "${seconds}s"
    }
}

private fun formatFileSize(bytes: Long): String {
    val mb = bytes / (1024 * 1024)
    val gb = mb / 1024
    
    return when {
        gb > 0 -> "${gb}GB"
        mb > 0 -> "${mb}MB"
        else -> "${bytes / 1024}KB"
    }
}

private fun formatLastModified(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val days = diff / (24 * 60 * 60 * 1000)
    
    return when {
        days == 0L -> "Today"
        days == 1L -> "Yesterday"
        days < 7L -> "${days} days ago"
        days < 30L -> "${days / 7} weeks ago"
        else -> "${days / 30} months ago"
    }
}

/**
 * Videos Screen - Comprehensive video browsing and management
 * 
 * Features:
 * - Top app bar with integrated tabs
 * - View mode toggle (Grid/List)
 * - Primary actions (Shuffle All, Play All)
 * - Video cards with duration, resolution, file size info
 * - Multi-select mode with bulk actions
 * - Sort and filter options
 * - Empty state with helpful message
 */
@Composable
fun VideosScreen(
    modifier: Modifier = Modifier,
    videos: List<VideoItem> = emptyList(),
    selectedVideos: Set<String> = emptySet(),
    sortBy: VideoSortOption = VideoSortOption.Title,
    sortDirection: SortDirection = SortDirection.Ascending,
    activeFilters: Set<VideoFilterOption> = emptySet(),
    viewMode: VideoViewMode = VideoViewMode.Grid,
    isMultiSelectMode: Boolean = false,
    searchQuery: String = "",
    selectedTab: LibraryTab = LibraryTab.Videos,
    isLoading: Boolean = false,
    onVideoClick: (VideoItem) -> Unit = {},
    onVideoLongClick: (VideoItem) -> Unit = {},
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onShuffleAllClick: () -> Unit = {},
    onPlayAllClick: () -> Unit = {},
    onViewModeToggle: () -> Unit = {},
    onFilterChange: (Set<VideoFilterOption>) -> Unit = {},
    onSelectionChange: (Set<String>) -> Unit = {},
    onSortChange: (VideoSortOption, SortDirection) -> Unit = { _, _ -> },
    onTabSelected: (LibraryTab) -> Unit = {}
) {
    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VideosTopBar(
                isMultiSelectMode = isMultiSelectMode,
                selectedCount = selectedVideos.size,
                totalVideos = videos.size,
                selectedTab = selectedTab,
                sortBy = sortBy,
                sortDirection = sortDirection,
                viewMode = viewMode,
                onSortClick = { showSortSheet = true },
                onSelectClick = onSelectClick,
                onSearchClick = onSearchClick,
                onSettingsClick = onSettingsClick,
                onViewModeToggle = onViewModeToggle,
                onBackClick = if (isMultiSelectMode) onSelectClick else null,
                onTabSelected = onTabSelected
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Primary Actions Row
            AnimatedVisibility(
                visible = !isMultiSelectMode,
                enter = fadeIn(tween(200)) + slideInVertically(
                    initialOffsetY = { -6 },
                    animationSpec = tween(200)
                ),
                exit = fadeOut(tween(150)) + slideOutVertically(
                    targetOffsetY = { -6 },
                    animationSpec = tween(150)
                )
            ) {
                ShufflePlayPills(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onShuffleAllClick = onShuffleAllClick,
                    onPlayAllClick = onPlayAllClick
                )
            }

            // Filter Chips Row
            AnimatedVisibility(
                visible = !isMultiSelectMode,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(150))
            ) {
                VideoFilterChips(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    activeFilters = activeFilters,
                    onFilterChange = onFilterChange
                )
            }

            // Videos Content
            if (isLoading) {
                VideoGridSkeleton(
                    modifier = Modifier.fillMaxSize(),
                    itemCount = 12
                )
            } else if (videos.isEmpty()) {
                VideosEmptyState(
                    modifier = Modifier.fillMaxSize(),
                    onScanLibrary = { /* TODO: Implement scan library */ },
                    onImportVideos = { /* TODO: Implement import videos */ }
                )
            } else {
                when (viewMode) {
                    VideoViewMode.Grid -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 160.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(videos) { video ->
                                VideoGridCard(
                                    video = video,
                                    isSelected = video.id in selectedVideos,
                                    isMultiSelectMode = isMultiSelectMode,
                                    onClick = { onVideoClick(video) },
                                    onLongClick = { onVideoLongClick(video) },
                                    onSelectionToggle = { 
                                        if (video.id in selectedVideos) {
                                            onSelectionChange(selectedVideos - video.id)
                                        } else {
                                            onSelectionChange(selectedVideos + video.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    VideoViewMode.List -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(videos) { video ->
                                VideoListCard(
                                    video = video,
                                    isSelected = video.id in selectedVideos,
                                    isMultiSelectMode = isMultiSelectMode,
                                    onClick = { onVideoClick(video) },
                                    onLongClick = { onVideoLongClick(video) },
                                    onSelectionToggle = { 
                                        if (video.id in selectedVideos) {
                                            onSelectionChange(selectedVideos - video.id)
                                        } else {
                                            onSelectionChange(selectedVideos + video.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShufflePlayPills(
    modifier: Modifier = Modifier,
    onShuffleAllClick: () -> Unit,
    onPlayAllClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Shuffle All Pill
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = EmberFlame,
            onClick = onShuffleAllClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Shuffle All",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Play All Pill
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            onClick = onPlayAllClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Play All",
                    color = EmberFlame,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun VideoGridCard(
    modifier: Modifier = Modifier,
    video: VideoItem,
    isSelected: Boolean,
    isMultiSelectMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSelectionToggle: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = if (isMultiSelectMode) onSelectionToggle else onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EmberFlame.copy(alpha = 0.1f) else EmberCard
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Video thumbnail placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EmberFlame.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.VideoLibrary,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Video info
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = TextStrong,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            if (video.artist != null) {
                Text(
                    text = video.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            
            Text(
                text = "${formatDuration(video.durationMs)} • ${formatFileSize(video.fileSizeBytes)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // Video type indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (video.isMusicVideo) {
                    Text(
                        text = "MV",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmberFlame,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (video.isLiveRecording) {
                    Text(
                        text = "LIVE",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (video.resolution != null) {
                    Text(
                        text = video.resolution,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoListCard(
    modifier: Modifier = Modifier,
    video: VideoItem,
    isSelected: Boolean,
    isMultiSelectMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSelectionToggle: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = if (isMultiSelectMode) onSelectionToggle else onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EmberFlame.copy(alpha = 0.1f) else EmberCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Video thumbnail placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EmberFlame.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.VideoLibrary,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Video info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextStrong,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (video.artist != null) {
                    Text(
                        text = video.artist,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${formatDuration(video.durationMs)} • ${formatFileSize(video.fileSizeBytes)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Video type indicators and resolution
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (video.isMusicVideo) {
                        Text(
                            text = "Music Video",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmberFlame,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (video.isLiveRecording) {
                        Text(
                            text = "Live Recording",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (video.resolution != null) {
                        Text(
                            text = video.resolution,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "Modified: ${formatLastModified(video.lastModified)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            // Selection indicator or kebab menu
            if (isMultiSelectMode) {
                Icon(
                    imageVector = Icons.Filled.Checklist,
                    contentDescription = if (isSelected) "Selected" else "Not selected",
                    tint = if (isSelected) EmberFlame else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                IconButton(onClick = { /* TODO: Show video menu */ }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Video options",
                        tint = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyVideosState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.VideoLibrary,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Videos Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = TextStrong
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Videos will appear here once you scan your music library",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun VideoFilterChips(
    modifier: Modifier = Modifier,
    activeFilters: Set<VideoFilterOption>,
    onFilterChange: (Set<VideoFilterOption>) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp)
    ) {
        items(VideoFilterOption.values()) { filter ->
            val isActive = filter in activeFilters
            Surface(
                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                color = if (isActive) EmberFlame else MaterialTheme.colorScheme.surface,
                onClick = {
                    if (isActive) {
                        onFilterChange(activeFilters - filter)
                    } else {
                        onFilterChange(activeFilters + filter)
                    }
                }
            ) {
                Text(
                    text = filter.displayName,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (isActive) Color.White else TextMuted,
                    fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideosScreenPreview() {
    EmberTheme {
        VideosScreen(
            videos = listOf(
                VideoItem("1", "Bohemian Rhapsody", "Queen", 355000L, 150000000L, "1080p", 30, 5000, "H.264", lastModified = System.currentTimeMillis() - 86400000L, isMusicVideo = true),
                VideoItem("2", "Live Concert Recording", "The Beatles", 3600000L, 800000000L, "720p", 24, 3000, "H.264", lastModified = System.currentTimeMillis() - 172800000L, isLiveRecording = true),
                VideoItem("3", "Music Video Collection", "Various Artists", 1800000L, 400000000L, "4K", 60, 8000, "H.265", lastModified = System.currentTimeMillis() - 259200000L, isMusicVideo = true)
            )
        )
    }
}
