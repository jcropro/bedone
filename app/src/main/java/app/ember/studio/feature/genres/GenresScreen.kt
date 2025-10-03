package app.ember.studio.feature.genres

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
import androidx.compose.material.icons.filled.MusicNote
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
import app.ember.studio.navigation.LibraryTab
import app.ember.studio.feature.songs.SortDirection

// Data classes
data class Genre(
    val id: String,
    val name: String,
    val trackCount: Int,
    val albumCount: Int,
    val artistCount: Int,
    val artworkUri: String? = null,
    val color: Color? = null // Genre-specific accent color
)

enum class GenreSortOption(val displayName: String) {
    Name("Name"),
    TrackCount("Track Count"),
    AlbumCount("Album Count"),
    ArtistCount("Artist Count")
}

enum class GenreFilterOption(val displayName: String) {
    RecentlyAdded("Recently Added"),
    MostTracks("Most Tracks"),
    MostAlbums("Most Albums"),
    MostArtists("Most Artists"),
    Popular("Popular")
}

enum class GenreViewMode(val displayName: String) {
    Grid("Grid"),
    List("List")
}

/**
 * Genres Screen - Comprehensive genre browsing and management
 * 
 * Features:
 * - Top app bar with integrated tabs
 * - View mode toggle (Grid/List)
 * - Primary actions (Shuffle All, Play All)
 * - Genre cards with color coding, name, track/album/artist counts
 * - Multi-select mode with bulk actions
 * - Sort and filter options
 * - Empty state with helpful message
 */
@Composable
fun GenresScreen(
    modifier: Modifier = Modifier,
    genres: List<Genre> = emptyList(),
    selectedGenres: Set<String> = emptySet(),
    sortBy: GenreSortOption = GenreSortOption.Name,
    sortDirection: SortDirection = SortDirection.Ascending,
    activeFilters: Set<GenreFilterOption> = emptySet(),
    viewMode: GenreViewMode = GenreViewMode.Grid,
    isMultiSelectMode: Boolean = false,
    searchQuery: String = "",
    selectedTab: LibraryTab = LibraryTab.Genres,
    isLoading: Boolean = false,
    onGenreClick: (Genre) -> Unit = {},
    onGenreLongClick: (Genre) -> Unit = {},
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onShuffleAllClick: () -> Unit = {},
    onPlayAllClick: () -> Unit = {},
    onViewModeToggle: () -> Unit = {},
    onFilterChange: (Set<GenreFilterOption>) -> Unit = {},
    onSelectionChange: (Set<String>) -> Unit = {},
    onSortChange: (GenreSortOption, SortDirection) -> Unit = { _, _ -> },
    onTabSelected: (LibraryTab) -> Unit = {}
) {
    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            GenresTopBar(
                isMultiSelectMode = isMultiSelectMode,
                selectedCount = selectedGenres.size,
                totalGenres = genres.size,
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
                GenreFilterChips(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    activeFilters = activeFilters,
                    onFilterChange = onFilterChange
                )
            }

            // Genres Content
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = EmberFlame
                    )
                }
            } else if (genres.isEmpty()) {
                EmptyGenresState(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                when (viewMode) {
                    GenreViewMode.Grid -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(160.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(genres) { genre ->
                                GenreGridCard(
                                    genre = genre,
                                    isSelected = genre.id in selectedGenres,
                                    isMultiSelectMode = isMultiSelectMode,
                                    onClick = { onGenreClick(genre) },
                                    onLongClick = { onGenreLongClick(genre) },
                                    onSelectionToggle = { 
                                        if (genre.id in selectedGenres) {
                                            onSelectionChange(selectedGenres - genre.id)
                                        } else {
                                            onSelectionChange(selectedGenres + genre.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    GenreViewMode.List -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(genres) { genre ->
                                GenreListCard(
                                    genre = genre,
                                    isSelected = genre.id in selectedGenres,
                                    isMultiSelectMode = isMultiSelectMode,
                                    onClick = { onGenreClick(genre) },
                                    onLongClick = { onGenreLongClick(genre) },
                                    onSelectionToggle = { 
                                        if (genre.id in selectedGenres) {
                                            onSelectionChange(selectedGenres - genre.id)
                                        } else {
                                            onSelectionChange(selectedGenres + genre.id)
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
private fun GenreGridCard(
    modifier: Modifier = Modifier,
    genre: Genre,
    isSelected: Boolean,
    isMultiSelectMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSelectionToggle: () -> Unit
) {
    val genreColor = genre.color ?: EmberFlame
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = if (isMultiSelectMode) onSelectionToggle else onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) genreColor.copy(alpha = 0.1f) else EmberCard
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Genre icon with color coding
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(genreColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = genreColor,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Genre info
            Text(
                text = genre.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = TextStrong,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Text(
                text = "${genre.trackCount} tracks",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Text(
                text = "${genre.albumCount} albums • ${genre.artistCount} artists",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun GenreListCard(
    modifier: Modifier = Modifier,
    genre: Genre,
    isSelected: Boolean,
    isMultiSelectMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSelectionToggle: () -> Unit
) {
    val genreColor = genre.color ?: EmberFlame
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = if (isMultiSelectMode) onSelectionToggle else onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) genreColor.copy(alpha = 0.1f) else EmberCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Genre icon with color coding
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(genreColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = genreColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Genre info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = genre.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextStrong,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${genre.trackCount} tracks • ${genre.albumCount} albums • ${genre.artistCount} artists",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Selection indicator or kebab menu
            if (isMultiSelectMode) {
                Icon(
                    imageVector = Icons.Filled.Checklist,
                    contentDescription = if (isSelected) "Selected" else "Not selected",
                    tint = if (isSelected) genreColor else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                IconButton(onClick = { /* TODO: Show genre menu */ }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Genre options",
                        tint = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyGenresState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MusicNote,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Genres Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = TextStrong
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Genres will appear here once you scan your music library",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun GenreFilterChips(
    modifier: Modifier = Modifier,
    activeFilters: Set<GenreFilterOption>,
    onFilterChange: (Set<GenreFilterOption>) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp)
    ) {
        items(GenreFilterOption.values()) { filter ->
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
fun GenresScreenPreview() {
    EmberTheme {
        GenresScreen(
            genres = listOf(
                Genre("1", "Electronic", 45, 8, 12, color = Color(0xFF9C27B0)),
                Genre("2", "Rock", 32, 6, 8, color = Color(0xFFE91E63)),
                Genre("3", "Jazz", 28, 5, 6, color = Color(0xFF2196F3))
            )
        )
    }
}
