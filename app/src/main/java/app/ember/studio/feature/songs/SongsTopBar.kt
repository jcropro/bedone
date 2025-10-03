package app.ember.studio.feature.songs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import app.ember.core.ui.design.EmberFlame
import app.ember.core.ui.design.TextMuted
import app.ember.core.ui.design.TextStrong
import app.ember.core.ui.theme.EmberTheme

/**
 * Songs Top Bar matching the reference image layout
 * 
 * Features:
 * - Hamburger menu on left
 * - Integrated scrollable tabs in center (Songs, Playlist, Folders, Albums, Artist)
 * - Search and Settings icons on right
 * - Secondary info row below with song count and Sort/Select actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsTopBar(
    modifier: Modifier = Modifier,
    isMultiSelectMode: Boolean = false,
    selectedCount: Int = 0,
    totalSongs: Int = 0,
    selectedTab: LibraryTab = LibraryTab.Songs,
    sortBy: SortOption = SortOption.SongName,
    sortDirection: SortDirection = SortDirection.Ascending,
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    onMenuClick: () -> Unit = {},
    onTabSelected: (LibraryTab) -> Unit = {},
    // Advanced library actions
    onLibraryScanClick: () -> Unit = {},
    onSmartPlaylistClick: () -> Unit = {},
    onMetadataEditorClick: () -> Unit = {},
    onLibraryStatisticsClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        // Main Top App Bar
        CenterAlignedTopAppBar(
            title = {
                if (isMultiSelectMode) {
                    Text(
                        text = "$selectedCount selected",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    // Integrated scrollable tabs
                    IntegratedScrollableTabs(
                        selectedTab = selectedTab,
                        onTabSelected = onTabSelected
                    )
                }
            },
            navigationIcon = {
                if (isMultiSelectMode && onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                } else {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu"
                        )
                    }
                }
            },
            actions = {
                if (!isMultiSelectMode) {
                    // Advanced library actions
                    IconButton(onClick = onLibraryScanClick) {
                        Icon(
                            imageVector = Icons.Filled.FolderOpen,
                            contentDescription = "Library Scan"
                        )
                    }
                    
                    IconButton(onClick = onSmartPlaylistClick) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "Smart Playlist"
                        )
                    }
                    
                    IconButton(onClick = onMetadataEditorClick) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Metadata Editor"
                        )
                    }
                    
                    IconButton(onClick = onLibraryStatisticsClick) {
                        Icon(
                            imageVector = Icons.Filled.Analytics,
                            contentDescription = "Library Statistics"
                        )
                    }
                    
                    // Search button
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                    
                    // Settings button
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
        
        // Secondary info row (only in normal mode)
        if (!isMultiSelectMode) {
            SecondaryInfoRow(
                totalSongs = totalSongs,
                sortBy = sortBy,
                sortDirection = sortDirection,
                onSortClick = onSortClick,
                onSelectClick = onSelectClick
            )
        }
    }
}

@Composable
private fun IntegratedScrollableTabs(
    modifier: Modifier = Modifier,
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit
) {
    val listState = rememberLazyListState()
    
    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        items(LibraryTab.values()) { tab ->
            TabItem(
                tab = tab,
                isSelected = selectedTab == tab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

@Composable
private fun TabItem(
    modifier: Modifier = Modifier,
    tab: LibraryTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = tab.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) TextStrong else TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // Underline indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(2.dp)
                    .background(
                        color = EmberFlame,
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        } else {
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
private fun SecondaryInfoRow(
    modifier: Modifier = Modifier,
    totalSongs: Int,
    sortBy: SortOption,
    sortDirection: SortDirection,
    onSortClick: () -> Unit,
    onSelectClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Song count
        Text(
            text = "$totalSongs Songs",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )
        
        // Sort and Select actions
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sort button with preview
            SortButton(
                sortBy = sortBy,
                sortDirection = sortDirection,
                onClick = onSortClick
            )
            
            // Select button
            IconButton(onClick = onSelectClick) {
                Icon(
                    imageVector = Icons.Filled.Checklist,
                    contentDescription = "Select songs"
                )
            }
        }
    }
}

@Composable
private fun SortButton(
    modifier: Modifier = Modifier,
    sortBy: SortOption,
    sortDirection: SortDirection,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Sort,
            contentDescription = "Sort",
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Library tabs enum matching the reference image
 */
enum class LibraryTab(val title: String) {
    Songs("Songs"),
    Playlist("Playlist"),
    Folders("Folders"),
    Albums("Albums"),
    Artist("Artist")
}

@Preview(showBackground = true)
@Composable
fun SongsTopBarPreview() {
    EmberTheme {
        Column {
            SongsTopBar(
                isMultiSelectMode = false,
                totalSongs = 41,
                selectedTab = LibraryTab.Songs,
                sortBy = SortOption.ArtistName,
                sortDirection = SortDirection.Ascending
            )
            Spacer(modifier = Modifier.height(16.dp))
            SongsTopBar(
                isMultiSelectMode = true,
                selectedCount = 5,
                totalSongs = 41,
                onBackClick = {}
            )
        }
    }
}