package app.ember.studio.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.design.*
import app.ember.studio.LibraryTab
import app.ember.studio.R

/**
 * Library Top App Bar with integrated scrollable tabs
 * Implements MASTER_BLUEPRINT specification: Top app bar + tabs (never bottom tabs)
 * 
 * Features:
 * - Hamburger menu on left
 * - Title and scrollable tabs in center
 * - Actions on right (Search, Settings)
 * - Animated underline for active tab
 * - Proper spacing and typography
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryTopAppBar(
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit,
    onNavigateToNowPlaying: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onMenuClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title
                Text(
                    text = "Library",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextStrong,
                    modifier = Modifier.weight(1f)
                )
                
                // Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing8),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = TextStrong
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = TextStrong
                        )
                    }
                }
            }
        },
        navigationIcon = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ember Logo - MASTER_BLUEPRINT compliant
                Icon(
                    painter = painterResource(id = R.drawable.ic_ember_logo),
                    contentDescription = "Ember",
                    tint = EmberFlame,
                    modifier = Modifier.size(32.dp)
                )
                
                // Hamburger Menu
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu",
                        tint = TextStrong
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = EmberInk,
            titleContentColor = TextStrong,
            navigationIconContentColor = TextStrong,
            actionIconContentColor = TextStrong
        )
    )
    
    // Scrollable tabs below the title
    LibraryScrollableTabs(
        selectedTab = selectedTab,
        onTabSelected = onTabSelected
    )
}

/**
 * Scrollable tabs component with animated underline
 * Follows MASTER_BLUEPRINT specifications for tab design
 */
@Composable
private fun LibraryScrollableTabs(
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(EmberInk)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing16, vertical = Spacing8),
            horizontalArrangement = Arrangement.spacedBy(Spacing8)
        ) {
            items(LibraryTab.values()) { tab ->
                LibraryTabItem(
                    tab = tab,
                    isSelected = tab == selectedTab,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
        
        // Animated underline for active tab
        AnimatedTabUnderline(
            selectedTab = selectedTab,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Individual tab item with proper styling
 */
@Composable
private fun LibraryTabItem(
    tab: LibraryTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = AnimationStandard,
        label = "tabBackground"
    )
    
    val textColor = if (isSelected) EmberFlame else TextMuted
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(RadiusPill))
            .background(
                if (isSelected) EmberFlame.copy(alpha = 0.12f) else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = Spacing16, vertical = Spacing8)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = stringResource(tab.titleRes),
                tint = textColor,
                modifier = Modifier.size(IconSize)
            )
            Text(
                text = stringResource(tab.titleRes),
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Animated underline that slides to the active tab
 * Implements the 2-3px underline with animated slide as per blueprint
 */
@Composable
private fun AnimatedTabUnderline(
    selectedTab: LibraryTab,
    modifier: Modifier = Modifier
) {
    val underlineWidth by animateFloatAsState(
        targetValue = 1f,
        animationSpec = AnimationStandard,
        label = "underlineWidth"
    )
    
    Box(
        modifier = modifier
            .height(2.dp)
            .background(EmberInk)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(120.dp) // Approximate tab width
                .background(
                    EmberFlame,
                    RoundedCornerShape(RadiusSM)
                )
                .alpha(underlineWidth)
        )
    }
}
