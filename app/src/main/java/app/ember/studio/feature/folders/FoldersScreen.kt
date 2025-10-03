package app.ember.studio.feature.folders

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
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
data class Folder(
    val id: String,
    val name: String,
    val path: String,
    val fileCount: Int,
    val subfolderCount: Int,
    val totalSizeBytes: Long,
    val lastModified: Long,
    val isExpanded: Boolean = false,
    val parentId: String? = null,
    val children: List<Folder> = emptyList()
)

enum class FolderSortOption(val displayName: String) {
    Name("Name"),
    FileCount("File Count"),
    Size("Size"),
    LastModified("Last Modified")
}

enum class FolderFilterOption(val displayName: String) {
    RecentlyModified("Recently Modified"),
    LargeFolders("Large Folders"),
    EmptyFolders("Empty Folders"),
    MusicOnly("Music Only"),
    VideoOnly("Video Only")
}

// Helper functions
private fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024
    val mb = kb / 1024
    val gb = mb / 1024
    
    return when {
        gb > 0 -> "${gb}GB"
        mb > 0 -> "${mb}MB"
        kb > 0 -> "${kb}KB"
        else -> "${bytes}B"
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
 * Folders Screen - Comprehensive folder browsing and management
 * 
 * Features:
 * - Top app bar with integrated tabs
 * - Primary actions (Shuffle All, Play All)
 * - Folder hierarchy with expand/collapse
 * - Folder cards with file counts, sizes, last modified
 * - Multi-select mode with bulk actions
 * - Sort and filter options
 * - Empty state with helpful message
 */
@Composable
fun FoldersScreen(
    modifier: Modifier = Modifier,
    folders: List<Folder> = emptyList(),
    selectedFolders: Set<String> = emptySet(),
    sortBy: FolderSortOption = FolderSortOption.Name,
    sortDirection: SortDirection = SortDirection.Ascending,
    activeFilters: Set<FolderFilterOption> = emptySet(),
    isMultiSelectMode: Boolean = false,
    searchQuery: String = "",
    selectedTab: LibraryTab = LibraryTab.Folders,
    isLoading: Boolean = false,
    onFolderClick: (Folder) -> Unit = {},
    onFolderLongClick: (Folder) -> Unit = {},
    onFolderExpand: (Folder) -> Unit = {},
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onShuffleAllClick: () -> Unit = {},
    onPlayAllClick: () -> Unit = {},
    onFilterChange: (Set<FolderFilterOption>) -> Unit = {},
    onSelectionChange: (Set<String>) -> Unit = {},
    onSortChange: (FolderSortOption, SortDirection) -> Unit = { _, _ -> },
    onTabSelected: (LibraryTab) -> Unit = {}
) {
    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FoldersTopBar(
                isMultiSelectMode = isMultiSelectMode,
                selectedCount = selectedFolders.size,
                totalFolders = folders.size,
                selectedTab = selectedTab,
                sortBy = sortBy,
                sortDirection = sortDirection,
                onSortClick = { showSortSheet = true },
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
                FolderFilterChips(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    activeFilters = activeFilters,
                    onFilterChange = onFilterChange
                )
            }

            // Folders Content
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = EmberFlame
                    )
                }
            } else if (folders.isEmpty()) {
                EmptyFoldersState(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(folders) { folder ->
                        FolderCard(
                            folder = folder,
                            isSelected = folder.id in selectedFolders,
                            isMultiSelectMode = isMultiSelectMode,
                            onClick = { onFolderClick(folder) },
                            onLongClick = { onFolderLongClick(folder) },
                            onExpandClick = { onFolderExpand(folder) },
                            onSelectionToggle = { 
                                if (folder.id in selectedFolders) {
                                    onSelectionChange(selectedFolders - folder.id)
                                } else {
                                    onSelectionChange(selectedFolders + folder.id)
                                }
                            }
                        )
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
private fun FolderCard(
    modifier: Modifier = Modifier,
    folder: Folder,
    isSelected: Boolean,
    isMultiSelectMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onExpandClick: () -> Unit,
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
            // Folder icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmberFlame.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (folder.isExpanded) Icons.Filled.FolderOpen else Icons.Filled.Folder,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Folder info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextStrong,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${folder.fileCount} files • ${folder.subfolderCount} folders",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${formatFileSize(folder.totalSizeBytes)} • ${formatLastModified(folder.lastModified)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Selection indicator or expand/kebab menu
            if (isMultiSelectMode) {
                Icon(
                    imageVector = Icons.Filled.Checklist,
                    contentDescription = if (isSelected) "Selected" else "Not selected",
                    tint = if (isSelected) EmberFlame else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Expand/Collapse button (if has children)
                    if (folder.children.isNotEmpty()) {
                        IconButton(onClick = onExpandClick) {
                            Icon(
                                imageVector = if (folder.isExpanded) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = if (folder.isExpanded) "Collapse" else "Expand",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // Kebab menu
                    IconButton(onClick = { /* TODO: Show folder menu */ }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Folder options",
                            tint = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFoldersState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Folder,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Folders Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = TextStrong
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Folders will appear here once you scan your music library",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun FolderFilterChips(
    modifier: Modifier = Modifier,
    activeFilters: Set<FolderFilterOption>,
    onFilterChange: (Set<FolderFilterOption>) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp)
    ) {
        items(FolderFilterOption.values()) { filter ->
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
fun FoldersScreenPreview() {
    EmberTheme {
        FoldersScreen(
            folders = listOf(
                Folder("1", "Music", "/storage/Music", 150, 5, 1024000000L, System.currentTimeMillis() - 86400000L),
                Folder("2", "Podcasts", "/storage/Podcasts", 45, 2, 512000000L, System.currentTimeMillis() - 172800000L),
                Folder("3", "Audiobooks", "/storage/Audiobooks", 12, 1, 256000000L, System.currentTimeMillis() - 259200000L)
            )
        )
    }
}
