package app.ember.studio.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
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
import app.ember.core.ui.components.GlassMorphismCard
import app.ember.studio.R
import app.ember.studio.DrawerDestination
import app.ember.studio.DrawerDestinationId

/**
 * Premium navigation drawer for Ember Audio Player
 * Implements the drawer structure as specified in MASTER_BLUEPRINT.md
 * 
 * Features:
 * - Ember branding at top
 * - Main navigation destinations
 * - Premium visual design with glass effects
 * - Smooth animations and interactions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmberNavigationDrawer(
    modifier: Modifier = Modifier,
    selectedDestination: DrawerDestinationId,
    onDestinationSelected: (DrawerDestinationId) -> Unit,
    onCloseDrawer: () -> Unit,
    destinations: List<DrawerDestination> = getDrawerDestinations()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            EmberDrawerContent(
                selectedDestination = selectedDestination,
                onDestinationSelected = { destination ->
                    onDestinationSelected(destination)
                    onCloseDrawer()
                },
                destinations = destinations,
                modifier = Modifier.fillMaxSize()
            )
        }
    ) {
        // Content will be provided by the parent
    }
}

@Composable
private fun EmberDrawerContent(
    modifier: Modifier = Modifier,
    selectedDestination: DrawerDestinationId,
    onDestinationSelected: (DrawerDestinationId) -> Unit,
    destinations: List<DrawerDestination>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmberInk)
            .padding(Spacing16)
    ) {
        // Ember branding header
        EmberDrawerHeader(
            modifier = Modifier.padding(bottom = Spacing24)
        )
        
        // Navigation destinations
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing8)
        ) {
            items(destinations) { destination ->
                DrawerDestinationItem(
                    destination = destination,
                    isSelected = destination.id == selectedDestination,
                    onClick = { onDestinationSelected(destination.id) }
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Footer with app version
        EmberDrawerFooter()
    }
}

@Composable
private fun EmberDrawerHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ember logo with glass morphism
        GlassMorphismCard(
            modifier = Modifier.size(48.dp),
            isHovered = false,
            onClick = null
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FlameGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Ember Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(Spacing12))
        
        Column {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Premium Audio Experience",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.alpha(0.8f)
            )
        }
    }
}

@Composable
private fun DrawerDestinationItem(
    destination: DrawerDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "backgroundColor"
    )
    
    val textColor = if (isSelected) EmberFlame else TextStrong
    val iconColor = if (isSelected) EmberFlame else TextMuted
    
    GlassMorphismCard(
        modifier = Modifier.fillMaxWidth(),
        isHovered = isSelected,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing12, vertical = Spacing12),
            verticalAlignment = Alignment.CenterVertically
        ) {
        Icon(
            imageVector = getDestinationIcon(destination.id),
            contentDescription = stringResource(destination.titleRes),
            tint = iconColor,
            modifier = Modifier.size(IconSize)
        )
        
        Spacer(modifier = Modifier.width(Spacing12))
        
        Text(
            text = stringResource(destination.titleRes),
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        
        if (isSelected) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Selected",
                tint = EmberFlame,
                modifier = Modifier.size(16.dp)
            )
        }
        }
    }
}

@Composable
fun EmberDrawerFooter(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(
            color = EmberOutline.copy(alpha = 0.3f),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = Spacing12)
        )
        
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.alpha(0.7f)
        )
        
        Text(
            text = "© 2024 Ember Studio",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.alpha(0.7f)
        )
    }
}

fun getDestinationIcon(destinationId: DrawerDestinationId): ImageVector {
    return when (destinationId) {
        DrawerDestinationId.Library -> Icons.Filled.LibraryMusic
        DrawerDestinationId.Equalizer -> Icons.Filled.GraphicEq
        DrawerDestinationId.SleepTimer -> Icons.Filled.Bedtime
        DrawerDestinationId.ThemeStudio -> Icons.Filled.Palette
        DrawerDestinationId.Widgets -> Icons.Filled.Widgets
        DrawerDestinationId.ScanImport -> Icons.Filled.FolderOpen
        DrawerDestinationId.Settings -> Icons.Filled.Settings
        DrawerDestinationId.Help -> Icons.AutoMirrored.Filled.Help
    }
}

fun getDrawerDestinations(): List<DrawerDestination> {
    return listOf(
        DrawerDestination(DrawerDestinationId.Library, R.string.drawer_library),
        DrawerDestination(DrawerDestinationId.Equalizer, R.string.drawer_equalizer),
        DrawerDestination(DrawerDestinationId.SleepTimer, R.string.drawer_sleep_timer),
        DrawerDestination(DrawerDestinationId.ThemeStudio, R.string.drawer_theme_studio),
        DrawerDestination(DrawerDestinationId.Widgets, R.string.drawer_widgets),
        DrawerDestination(DrawerDestinationId.ScanImport, R.string.drawer_scan_import),
        DrawerDestination(DrawerDestinationId.Settings, R.string.drawer_settings),
        DrawerDestination(DrawerDestinationId.Help, R.string.drawer_help)
    )
}
