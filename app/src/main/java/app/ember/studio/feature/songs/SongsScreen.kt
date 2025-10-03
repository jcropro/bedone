package app.ember.studio.feature.songs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import app.ember.studio.library.LibraryScanScreen
import app.ember.studio.library.SmartPlaylistScreen
import app.ember.studio.library.MetadataEditorScreen
import app.ember.studio.library.LibraryStatisticsScreen
import app.ember.studio.library.ScanProgress
import app.ember.studio.library.SmartPlaylist
import app.ember.studio.library.TrackMetadata
import app.ember.studio.library.LibraryStatistics

@Composable
fun SongsScreen(
    modifier: Modifier = Modifier,
    songs: List<Song> = emptyList(),
    selectedSongs: Set<String> = emptySet(),
    sortBy: SortOption = SortOption.SongName,
    sortDirection: SortDirection = SortDirection.Ascending,
    activeFilters: Set<FilterOption> = emptySet(),
    isMultiSelectMode: Boolean = false,
    searchQuery: String = "",
    selectedTab: LibraryTab = LibraryTab.Songs,
    // Advanced library functionality
    showLibraryScan: Boolean = false,
    showSmartPlaylist: Boolean = false,
    showMetadataEditor: Boolean = false,
    showLibraryStatistics: Boolean = false,
    scanProgress: ScanProgress = ScanProgress(),
    smartPlaylist: SmartPlaylist = SmartPlaylist(),
    trackMetadata: List<TrackMetadata> = emptyList(),
    libraryStatistics: LibraryStatistics = LibraryStatistics(),
    onSongClick: (Song) -> Unit = {},
    onSongLongClick: (Song) -> Unit = {},
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onShuffleClick: () -> Unit = {},
    onPlayClick: () -> Unit = {},
    onFilterChange: (Set<FilterOption>) -> Unit = {},
    onSelectionChange: (Set<String>) -> Unit = {},
    onSortChange: (SortOption, SortDirection) -> Unit = { _, _ -> },
    onTabSelected: (LibraryTab) -> Unit = {},
    // Advanced library callbacks
    onLibraryScanClick: () -> Unit = {},
    onSmartPlaylistClick: () -> Unit = {},
    onMetadataEditorClick: () -> Unit = {},
    onLibraryStatisticsClick: () -> Unit = {},
    onStartScan: () -> Unit = {},
    onStopScan: () -> Unit = {},
    onAddFolder: () -> Unit = {},
    onRemoveFolder: (android.net.Uri) -> Unit = {},
    onToggleFolder: (android.net.Uri) -> Unit = {},
    onCreateSmartPlaylist: () -> Unit = {},
    onViewStatistics: () -> Unit = {},
    onFieldChange: (Int, String, String) -> Unit = { _, _, _ -> },
    onSaveChanges: () -> Unit = {},
    onDiscardChanges: () -> Unit = {},
    onSelectAll: () -> Unit = {},
    onDeselectAll: () -> Unit = {},
    onExportStatistics: () -> Unit = {},
    onRefreshStatistics: () -> Unit = {}
) {
    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // Show advanced library screens when requested
    when {
        showLibraryScan -> {
            LibraryScanScreen(
                modifier = modifier,
                scanProgress = scanProgress,
                onStartScan = onStartScan,
                onStopScan = onStopScan,
                onAddFolder = onAddFolder,
                onRemoveFolder = onRemoveFolder,
                onToggleFolder = onToggleFolder,
                onCreateSmartPlaylist = onCreateSmartPlaylist,
                onViewStatistics = onViewStatistics
            )
        }
        showSmartPlaylist -> {
            SmartPlaylistScreen(
                modifier = modifier,
                playlist = smartPlaylist,
                onNameChange = { /* TODO: Implement name change */ },
                onDescriptionChange = { /* TODO: Implement description change */ },
                onAddRule = { /* TODO: Implement add rule */ },
                onRemoveRule = { /* TODO: Implement remove rule */ },
                onUpdateRule = { _, _ -> /* TODO: Implement update rule */ },
                onSavePlaylist = { /* TODO: Implement save playlist */ },
                onCancel = { /* TODO: Implement cancel */ }
            )
        }
        showMetadataEditor -> {
            MetadataEditorScreen(
                modifier = modifier,
                tracks = trackMetadata,
                onFieldChange = onFieldChange,
                onSaveChanges = onSaveChanges,
                onDiscardChanges = onDiscardChanges,
                onSelectAll = onSelectAll,
                onDeselectAll = onDeselectAll
            )
        }
        showLibraryStatistics -> {
            LibraryStatisticsScreen(
                modifier = modifier,
                statistics = libraryStatistics,
                onExportStatistics = onExportStatistics,
                onRefreshStatistics = onRefreshStatistics
            )
        }
        else -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    SongsTopBar(
                        isMultiSelectMode = isMultiSelectMode,
                        selectedCount = selectedSongs.size,
                        totalSongs = songs.size,
                        selectedTab = selectedTab,
                        sortBy = sortBy,
                        sortDirection = sortDirection,
                        onSortClick = { showSortSheet = true },
                        onSelectClick = onSelectClick,
                        onSearchClick = onSearchClick,
                        onSettingsClick = onSettingsClick,
                        onBackClick = if (isMultiSelectMode) onSelectClick else null,
                        onTabSelected = onTabSelected,
                        // Advanced library actions
                        onLibraryScanClick = onLibraryScanClick,
                        onSmartPlaylistClick = onSmartPlaylistClick,
                        onMetadataEditorClick = onMetadataEditorClick,
                        onLibraryStatisticsClick = onLibraryStatisticsClick
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
                            onShuffleClick = onShuffleClick,
                            onPlayClick = onPlayClick
                        )
                    }

                    // Filter Chips Row
                    FilterChipRow(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        activeFilters = activeFilters,
                        onFilterChange = onFilterChange,
                        onShowAllFilters = { showFilterSheet = true }
                    )

                    // Songs List
                    SongsList(
                        modifier = Modifier.weight(1f),
                        songs = songs,
                        selectedSongs = selectedSongs,
                        isMultiSelectMode = isMultiSelectMode,
                        searchQuery = searchQuery,
                        onSongClick = onSongClick,
                        onSongLongClick = onSongLongClick,
                        onSelectionChange = onSelectionChange
                    )
                }
                
                // Multi-select bottom bar
                MultiSelectBottomBar(
                    selectedCount = selectedSongs.size,
                    onPlayClick = { /* TODO: Implement play selected */ },
                    onQueueNextClick = { /* TODO: Implement queue next */ },
                    onAddToPlaylistClick = { /* TODO: Implement add to playlist */ },
                    onShareClick = { /* TODO: Implement share */ },
                    onDeleteClick = { /* TODO: Implement delete */ }
                )
            }
        }
    }

    // Sort Sheet
    if (showSortSheet) {
        SortSheet(
            currentSort = sortBy,
            currentDirection = sortDirection,
            onSortChange = onSortChange,
            onDismiss = { showSortSheet = false }
        )
    }

    // Filter Sheet
    if (showFilterSheet) {
        FilterSheet(
            activeFilters = activeFilters,
            onFilterChange = onFilterChange,
            onDismiss = { showFilterSheet = false }
        )
    }
}

// Data models
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val filePath: String,
    val dateAdded: Long,
    val playCount: Int,
    val year: Int?,
    val size: Long,
    val bitrate: Int,
    val hasLyrics: Boolean,
    val isLongAudio: Boolean = duration >= 20 * 60 * 1000, // 20 minutes
    val hasVideo: Boolean = false,
    val videoPath: String? = null
)

enum class SortOption {
    SongName,
    ArtistName,
    AlbumName,
    FolderName,
    AddedTime,
    PlayCount,
    Year,
    Duration,
    Size,
    Random
}

enum class SortDirection {
    Ascending,
    Descending
}

enum class FilterOption {
    RecentlyAdded,
    Downloads,
    LongAudio,
    HasLyrics,
    HighBitrate,
    Mp3,
    Flac,
    Aac,
    Favorites
}

@Preview(showBackground = true)
@Composable
fun SongsScreenPreview() {
    EmberTheme {
        SongsScreen(
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
                )
            )
        )
    }
}
