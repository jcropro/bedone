package app.ember.studio.feature.folders

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.EmberFlame
import app.ember.core.ui.design.TextMuted
import app.ember.core.ui.design.TextStrong
import app.ember.core.ui.theme.EmberTheme
import app.ember.studio.LibraryTab
import app.ember.studio.feature.songs.SortDirection

/**
 * Folders Top Bar matching the other library screens layout
 * 
 * Features:
 * - Hamburger menu on left
 * - Integrated scrollable tabs in center
 * - Search and Settings icons on right
 * - Secondary info row below with folder count and Sort/Select actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoldersTopBar(
    modifier: Modifier = Modifier,
    isMultiSelectMode: Boolean = false,
    selectedCount: Int = 0,
    totalFolders: Int = 0,
    selectedTab: LibraryTab = LibraryTab.Folders,
    sortBy: FolderSortOption = FolderSortOption.Name,
    sortDirection: SortDirection = SortDirection.Ascending,
    onSortClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    onMenuClick: () -> Unit = {},
    onTabSelected: (LibraryTab) -> Unit = {}
) {
    Column(modifier = modifier) {
        // Main Top App Bar
        CenterAlignedTopAppBar(
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
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
                totalFolders = totalFolders,
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
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
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
            text = stringResource(tab.titleRes),
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
    totalFolders: Int,
    sortBy: FolderSortOption,
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
        // Folder count
        Text(
            text = "$totalFolders Folders",
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
                    contentDescription = "Select folders"
                )
            }
        }
    }
}

@Composable
private fun SortButton(
    modifier: Modifier = Modifier,
    sortBy: FolderSortOption,
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

@Preview(showBackground = true)
@Composable
fun FoldersTopBarPreview() {
    EmberTheme {
        Column {
            FoldersTopBar(
                isMultiSelectMode = false,
                totalFolders = 12,
                selectedTab = LibraryTab.Folders,
                sortBy = FolderSortOption.Name,
                sortDirection = SortDirection.Ascending
            )
            Spacer(modifier = Modifier.height(16.dp))
            FoldersTopBar(
                isMultiSelectMode = true,
                selectedCount = 2,
                totalFolders = 12,
                onBackClick = {}
            )
        }
    }
}
