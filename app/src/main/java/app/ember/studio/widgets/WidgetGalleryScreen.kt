package app.ember.studio.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.*
import app.ember.studio.R
import app.ember.studio.widgets.EmberWidgetConfigureActivity

/**
 * Widget Gallery Screen
 * 
 * Implements Phase 7 from the Golden Blueprint:
 * - Beautiful defaults + customization
 * - Compose Glance widgets: Small (1×2), Medium (2×2), Large (4×2)
 * - Art + title/artist, play/pause, next; optional EQ shortcut
 * - Theme aware (System, Ember Dark, Ember Light) and accent toggle
 * - Settings: rounded style, compact style, glass style
 * - Previews in a dedicated Widgets gallery screen
 */
@Composable
fun WidgetGalleryScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedWidget by remember { mutableStateOf<WidgetType?>(null) }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = EmberInk,
        topBar = {
            WidgetGalleryTopBar()
        },
        floatingActionButton = {
            if (selectedWidget != null) {
                FloatingActionButton(
                    onClick = {
                        // Launch widget configuration
                        val intent = Intent(context, EmberWidgetConfigureActivity::class.java).apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
                        }
                        context.startActivity(intent)
                    },
                    containerColor = EmberFlame,
                    contentColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Widget"
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Spacing16),
            verticalArrangement = Arrangement.spacedBy(Spacing24)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "Widget Gallery",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextStrong
                        )
                    )
                    Spacer(modifier = Modifier.height(Spacing8))
                    Text(
                        text = "Choose a widget to add to your home screen",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextMuted
                        )
                    )
                }
            }
            
            // Widget Types
            items(WidgetType.values()) { widgetType ->
                WidgetPreviewCard(
                    widgetType = widgetType,
                    isSelected = selectedWidget == widgetType,
                    onClick = { selectedWidget = widgetType }
                )
            }
            
            // Widget Styles Section
            item {
                Column {
                    Text(
                        text = "Widget Styles",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextStrong
                        )
                    )
                    Spacer(modifier = Modifier.height(Spacing16))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing16)
                    ) {
                        items(WidgetStyle.values()) { style ->
                            WidgetStyleCard(style = style)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetGalleryTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(EmberInk)
            .padding(Spacing16)
    ) {
        Text(
            text = "Widget Gallery",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextStrong
            ),
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

@Composable
private fun WidgetPreviewCard(
    widgetType: WidgetType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EmberFlame.copy(alpha = 0.1f) else EmberCard
        ),
        shape = RoundedCornerShape(RadiusLG),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = widgetType.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextStrong
                        )
                    )
                    Spacer(modifier = Modifier.height(Spacing4))
                    Text(
                        text = widgetType.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextMuted
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Widget Preview
                WidgetPreview(
                    widgetType = widgetType,
                    modifier = Modifier.size(80.dp, 60.dp)
                )
            }
            
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing16),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tap to add to home screen",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = EmberFlame,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.width(Spacing8))
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = EmberFlame,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetPreview(
    widgetType: WidgetType,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = EmberInk2
        ),
        shape = RoundedCornerShape(RadiusMD)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (widgetType) {
                WidgetType.SMALL -> SmallWidgetPreview()
                WidgetType.MEDIUM -> MediumWidgetPreview()
                WidgetType.LARGE -> LargeWidgetPreview()
            }
        }
    }
}

@Composable
private fun SmallWidgetPreview() {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Album art placeholder
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(EmberFlame.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        // Track info
        Text(
            text = "Track",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextStrong,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Artist",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextMuted
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Play button
        IconButton(
            onClick = {},
            modifier = Modifier.size(20.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = EmberFlame,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun MediumWidgetPreview() {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Album art
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(EmberFlame.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Track",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextStrong,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Artist",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
            
            IconButton(
                onClick = {},
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            IconButton(
                onClick = {},
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun LargeWidgetPreview() {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Album art
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(EmberFlame.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Track Title",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextStrong,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Artist Name",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // EQ shortcut
            IconButton(
                onClick = {},
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Equalizer,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Full controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            IconButton(
                onClick = {},
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            IconButton(
                onClick = {},
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun WidgetStyleCard(
    style: WidgetStyle
) {
    Card(
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmberCard
        ),
        shape = RoundedCornerShape(RadiusLG)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Text(
                text = style.displayName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextStrong
                )
            )
            Spacer(modifier = Modifier.height(Spacing8))
            Text(
                text = style.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextMuted
                )
            )
        }
    }
}

enum class WidgetType(
    val title: String,
    val description: String,
    val size: String
) {
    SMALL(
        title = "Small Widget",
        description = "Track info + play/pause (1×2)",
        size = "1×2"
    ),
    MEDIUM(
        title = "Medium Widget", 
        description = "Track info + controls + next (2×2)",
        size = "2×2"
    ),
    LARGE(
        title = "Large Widget",
        description = "Full controls + EQ shortcut (4×2)",
        size = "4×2"
    )
}
