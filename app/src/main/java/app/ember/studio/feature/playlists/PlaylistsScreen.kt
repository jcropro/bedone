package app.ember.studio.feature.playlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme
import app.ember.studio.navigation.LibraryTab

// Data classes
data class Playlist(
    val id: String,
    val name: String,
    val trackCount: Int,
    val totalDurationMs: Long,
    val createdAt: Long = System.currentTimeMillis()
)

enum class PlaylistSortOption(val displayName: String) {
    Name("Name"),
    TrackCount("Track Count"),
    Duration("Duration"),
    Created("Created")
}

enum class PlaylistFilterOption(val displayName: String) {
    RecentlyAdded("Recently Added"),
    MostPlayed("Most Played"),
    Empty("Empty"),
    Smart("Smart Playlists")
}

/**
 * Playlists Screen - Comprehensive playlist management
 */
@Composable
fun PlaylistsScreen(
    modifier: Modifier = Modifier,
    playlists: List<Playlist> = emptyList(),
    selectedPlaylists: Set<String> = emptySet(),
    sortBy: PlaylistSortOption = PlaylistSortOption.Name,
    sortDirection: app.ember.studio.feature.songs.SortDirection = app.ember.studio.feature.songs.SortDirection.Ascending,
    activeFilters: Set<PlaylistFilterOption> = emptySet(),
    isMultiSelectMode: Boolean = false,
    searchQuery: String = "",
    selectedTab: LibraryTab = LibraryTab.Playlists,
    isLoading: Boolean = false,
    onPlaylistClick: (Playlist) -> Unit = {},
    onPlaylistLongClick: (Playlist) -> Unit = {},
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onShuffleAllClick: () -> Unit = {},
    onCreatePlaylistClick: () -> Unit = {},
    onFilterChange: (Set<PlaylistFilterOption>) -> Unit = {},
    onSelectionChange: (Set<String>) -> Unit = {},
    onSortChange: (PlaylistSortOption, app.ember.studio.feature.songs.SortDirection) -> Unit = { _, _ -> },
    onTabSelected: (LibraryTab) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            PlaylistsTopBar(
                isMultiSelectMode = isMultiSelectMode,
                selectedCount = selectedPlaylists.size,
                totalPlaylists = playlists.size,
                selectedTab = selectedTab,
                sortBy = sortBy,
                sortDirection = sortDirection,
                onSortClick = onSortClick,
                onSelectClick = onSelectClick,
                onSearchClick = onSearchClick,
                onSettingsClick = onSettingsClick,
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
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading playlists...")
                }
            } else if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No playlists found")
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${playlists.size} playlists")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistsScreenPreview() {
    EmberTheme {
        PlaylistsScreen(
            playlists = listOf(
                Playlist("1", "My Favorites", 25, 3600000),
                Playlist("2", "Workout Mix", 15, 2700000),
                Playlist("3", "Chill Vibes", 30, 5400000)
            )
        )
    }
}