package app.ember.studio.feature.artists

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
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
import app.ember.core.ui.components.ArtistsEmptyState
import app.ember.core.ui.components.ArtistGridSkeleton
import app.ember.studio.feature.songs.SortDirection

// Data classes
data class Artist(
    val id: String,
    val name: String,
    val albumCount: Int,
    val trackCount: Int,
    val artworkUri: String? = null,
    val genre: String? = null,
    val yearRange: String? = null // e.g., "2018-2023"
)

enum class ArtistSortOption(val displayName: String) {
    Name("Name"),
    AlbumCount("Album Count"),
    TrackCount("Track Count"),
    RecentlyAdded("Recently Added")
}

enum class ArtistFilterOption(val displayName: String) {
    RecentlyAdded("Recently Added"),
    MostAlbums("Most Albums"),
    MostTracks("Most Tracks"),
    Singles("Singles"),
    VariousArtists("Various Artists")
}

enum class ArtistViewMode(val displayName: String) {
    Grid("Grid"),
    List("List")
}

/**
 * Artists Screen - Comprehensive artist browsing and management
 * 
 * Features:
 * - Top app bar with integrated tabs
 * - View mode toggle (Grid/List)
 * - Primary actions (Shuffle All, Play All)
 * - Artist cards with avatar, name, album count, track count
 * - Multi-select mode with bulk actions
 * - Sort and filter options
 * - Empty state with helpful message
 */
@Composable
fun ArtistsScreen(
    modifier: Modifier = Modifier,
    artists: List<Artist> = emptyList(),
    selectedArtists: Set<String> = emptySet(),
    sortBy: ArtistSortOption = ArtistSortOption.Name,
    sortDirection: SortDirection = SortDirection.Ascending,
    activeFilters: Set<ArtistFilterOption> = emptySet(),
    viewMode: ArtistViewMode = ArtistViewMode.Grid,
    isMultiSelectMode: Boolean = false,
    searchQuery: String = "",
    selectedTab: LibraryTab = LibraryTab.Artists,
    isLoading: Boolean = false,
    onArtistClick: (Artist) -> Unit = {},
    onArtistLongClick: (Artist) -> Unit = {},
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onShuffleAllClick: () -> Unit = {},
    onPlayAllClick: () -> Unit = {},
    onViewModeToggle: () -> Unit = {},
    onFilterChange: (Set<ArtistFilterOption>) -> Unit = {},
    onSelectionChange: (Set<String>) -> Unit = {},
    onSortChange: (ArtistSortOption, SortDirection) -> Unit = { _, _ -> },
    onTabSelected: (LibraryTab) -> Unit = {}
) {
    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ArtistsTopBar(
                isMultiSelectMode = isMultiSelectMode,
                selectedCount = selectedArtists.size,
                totalArtists = artists.size,
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
                ArtistFilterChips(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    activeFilters = activeFilters,
                    onFilterChange = onFilterChange
                )
            }

            // Artists Content
            if (isLoading) {
                ArtistGridSkeleton(
                    modifier = Modifier.fillMaxSize(),
                    itemCount = 12
                )
            } else if (artists.isEmpty()) {
                ArtistsEmptyState(
                    modifier = Modifier.fillMaxSize(),
                    onScanLibrary = { /* TODO: Implement scan library */ },
                    onImportMusic = { /* TODO: Implement import music */ }
                )
            } else {
                when (viewMode) {
                    ArtistViewMode.Grid -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(160.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(artists) { artist ->
                                ArtistGridCard(
                                    artist = artist,
                                    isSelected = artist.id in selectedArtists,
                                    isMultiSelectMode = isMultiSelectMode,
                                    onClick = { onArtistClick(artist) },
                                    onLongClick = { onArtistLongClick(artist) },
                                    onSelectionToggle = { 
                                        if (artist.id in selectedArtists) {
                                            onSelectionChange(selectedArtists - artist.id)
                                        } else {
                                            onSelectionChange(selectedArtists + artist.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    ArtistViewMode.List -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(artists) { artist ->
                                ArtistListCard(
                                    artist = artist,
                                    isSelected = artist.id in selectedArtists,
                                    isMultiSelectMode = isMultiSelectMode,
                                    onClick = { onArtistClick(artist) },
                                    onLongClick = { onArtistLongClick(artist) },
                                    onSelectionToggle = { 
                                        if (artist.id in selectedArtists) {
                                            onSelectionChange(selectedArtists - artist.id)
                                        } else {
                                            onSelectionChange(selectedArtists + artist.id)
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
private fun ArtistGridCard(
    modifier: Modifier = Modifier,
    artist: Artist,
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
            // Artist avatar placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(EmberFlame.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Artist info
            Text(
                text = artist.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = TextStrong,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Text(
                text = "${artist.albumCount} albums • ${artist.trackCount} tracks",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            if (artist.yearRange != null) {
                Text(
                    text = artist.yearRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ArtistListCard(
    modifier: Modifier = Modifier,
    artist: Artist,
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
            // Artist avatar placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(EmberFlame.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Artist info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextStrong,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${artist.albumCount} albums • ${artist.trackCount} tracks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (artist.yearRange != null) {
                    Text(
                        text = artist.yearRange,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                IconButton(onClick = { /* TODO: Show artist menu */ }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Artist options",
                        tint = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyArtistsState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Artists Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = TextStrong
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Artists will appear here once you scan your music library",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ArtistFilterChips(
    modifier: Modifier = Modifier,
    activeFilters: Set<ArtistFilterOption>,
    onFilterChange: (Set<ArtistFilterOption>) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp)
    ) {
        items(ArtistFilterOption.values()) { filter ->
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
fun ArtistsScreenPreview() {
    EmberTheme {
        ArtistsScreen(
            artists = listOf(
                Artist("1", "Ember Ensemble", 3, 25, yearRange = "2020-2023"),
                Artist("2", "Synth Wave", 2, 15, yearRange = "2021-2023"),
                Artist("3", "Folk Collective", 4, 32, yearRange = "2018-2023")
            )
        )
    }
}
