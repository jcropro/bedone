package app.ember.studio.feature.audiobooks

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
import androidx.compose.material.icons.automirrored.filled.MenuBook
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
import app.ember.studio.navigation.LibraryTab
import app.ember.studio.feature.songs.SortDirection

// Data classes
data class Audiobook(
    val id: String,
    val title: String,
    val author: String,
    val narrator: String? = null,
    val chapterCount: Int,
    val totalDurationMs: Long,
    val currentPositionMs: Long = 0L,
    val isCompleted: Boolean = false,
    val artworkUri: String? = null,
    val genre: String? = null,
    val year: Int? = null,
    val rating: Float? = null
)

enum class AudiobookSortOption(val displayName: String) {
    Title("Title"),
    Author("Author"),
    Duration("Duration"),
    RecentlyPlayed("Recently Played"),
    Progress("Progress")
}

enum class AudiobookFilterOption(val displayName: String) {
    RecentlyPlayed("Recently Played"),
    InProgress("In Progress"),
    Completed("Completed"),
    Unplayed("Unplayed"),
    Longest("Longest"),
    Shortest("Shortest")
}

enum class AudiobookViewMode(val displayName: String) {
    Grid("Grid"),
    List("List")
}

// Helper functions
private fun formatDuration(ms: Long): String {
    val hours = ms / (1000 * 60 * 60)
    val minutes = (ms % (1000 * 60 * 60)) / (1000 * 60)
    return "${hours}h ${minutes}m"
}

private fun formatProgress(currentMs: Long, totalMs: Long): String {
    if (totalMs == 0L) return "0%"
    val percentage = (currentMs * 100 / totalMs).toInt()
    return "${percentage}%"
}

/**
 * Audiobooks Screen - Comprehensive audiobook browsing and management
 * 
 * Features:
 * - Top app bar with integrated tabs
 * - View mode toggle (Grid/List)
 * - Primary actions (Shuffle All, Play All)
 * - Audiobook cards with progress indicators, chapter info, ratings
 * - Multi-select mode with bulk actions
 * - Sort and filter options
 * - Empty state with helpful message
 */
@Composable
fun AudiobooksScreen(
    modifier: Modifier = Modifier,
    audiobooks: List<Audiobook> = emptyList(),
    selectedAudiobooks: Set<String> = emptySet(),
    sortBy: AudiobookSortOption = AudiobookSortOption.Title,
    sortDirection: SortDirection = SortDirection.Ascending,
    activeFilters: Set<AudiobookFilterOption> = emptySet(),
    viewMode: AudiobookViewMode = AudiobookViewMode.Grid,
    isMultiSelectMode: Boolean = false,
    searchQuery: String = "",
    selectedTab: LibraryTab = LibraryTab.Audiobooks,
    isLoading: Boolean = false,
    onAudiobookClick: (Audiobook) -> Unit = {},
    onAudiobookLongClick: (Audiobook) -> Unit = {},
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onShuffleAllClick: () -> Unit = {},
    onPlayAllClick: () -> Unit = {},
    onViewModeToggle: () -> Unit = {},
    onFilterChange: (Set<AudiobookFilterOption>) -> Unit = {},
    onSelectionChange: (Set<String>) -> Unit = {},
    onSortChange: (AudiobookSortOption, SortDirection) -> Unit = { _, _ -> },
    onTabSelected: (LibraryTab) -> Unit = {}
) {
    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AudiobooksTopBar(
                isMultiSelectMode = isMultiSelectMode,
                selectedCount = selectedAudiobooks.size,
                totalAudiobooks = audiobooks.size,
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
                AudiobookFilterChips(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    activeFilters = activeFilters,
                    onFilterChange = onFilterChange
                )
            }

            // Audiobooks Content
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = EmberFlame
                    )
                }
            } else if (audiobooks.isEmpty()) {
                EmptyAudiobooksState(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                when (viewMode) {
                    AudiobookViewMode.Grid -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 160.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(audiobooks) { audiobook ->
                                AudiobookGridCard(
                                    audiobook = audiobook,
                                    isSelected = audiobook.id in selectedAudiobooks,
                                    isMultiSelectMode = isMultiSelectMode,
                                    onClick = { onAudiobookClick(audiobook) },
                                    onLongClick = { onAudiobookLongClick(audiobook) },
                                    onSelectionToggle = { 
                                        if (audiobook.id in selectedAudiobooks) {
                                            onSelectionChange(selectedAudiobooks - audiobook.id)
                                        } else {
                                            onSelectionChange(selectedAudiobooks + audiobook.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    AudiobookViewMode.List -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(audiobooks) { audiobook ->
                                AudiobookListCard(
                                    audiobook = audiobook,
                                    isSelected = audiobook.id in selectedAudiobooks,
                                    isMultiSelectMode = isMultiSelectMode,
                                    onClick = { onAudiobookClick(audiobook) },
                                    onLongClick = { onAudiobookLongClick(audiobook) },
                                    onSelectionToggle = { 
                                        if (audiobook.id in selectedAudiobooks) {
                                            onSelectionChange(selectedAudiobooks - audiobook.id)
                                        } else {
                                            onSelectionChange(selectedAudiobooks + audiobook.id)
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
private fun AudiobookGridCard(
    modifier: Modifier = Modifier,
    audiobook: Audiobook,
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
            // Audiobook cover placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EmberFlame.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Audiobook info
            Text(
                text = audiobook.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = TextStrong,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Text(
                text = audiobook.author,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Text(
                text = "${audiobook.chapterCount} chapters • ${formatDuration(audiobook.totalDurationMs)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // Progress indicator
            if (audiobook.currentPositionMs > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatProgress(audiobook.currentPositionMs, audiobook.totalDurationMs),
                    style = MaterialTheme.typography.bodySmall,
                    color = EmberFlame,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AudiobookListCard(
    modifier: Modifier = Modifier,
    audiobook: Audiobook,
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
            // Audiobook cover placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EmberFlame.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Audiobook info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = audiobook.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextStrong,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = audiobook.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${audiobook.chapterCount} chapters • ${formatDuration(audiobook.totalDurationMs)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Progress indicator
                if (audiobook.currentPositionMs > 0) {
                    Text(
                        text = formatProgress(audiobook.currentPositionMs, audiobook.totalDurationMs),
                        style = MaterialTheme.typography.bodySmall,
                        color = EmberFlame,
                        fontWeight = FontWeight.Medium
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
                IconButton(onClick = { /* TODO: Show audiobook menu */ }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Audiobook options",
                        tint = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyAudiobooksState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.MenuBook,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Audiobooks Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = TextStrong
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Audiobooks will appear here once you scan your music library",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun AudiobookFilterChips(
    modifier: Modifier = Modifier,
    activeFilters: Set<AudiobookFilterOption>,
    onFilterChange: (Set<AudiobookFilterOption>) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp)
    ) {
        items(AudiobookFilterOption.values()) { filter ->
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
fun AudiobooksScreenPreview() {
    EmberTheme {
        AudiobooksScreen(
            audiobooks = listOf(
                Audiobook("1", "The Great Gatsby", "F. Scott Fitzgerald", "Jake Gyllenhaal", 9, 180000000L, 45000000L),
                Audiobook("2", "1984", "George Orwell", "Simon Prebble", 12, 210000000L, 0L),
                Audiobook("3", "To Kill a Mockingbird", "Harper Lee", "Sissy Spacek", 15, 195000000L, 195000000L, true)
            )
        )
    }
}
