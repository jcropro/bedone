package app.ember.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.design.EmberFlame
import app.ember.core.ui.design.EmberInk
import app.ember.core.ui.design.TextStrong
import app.ember.core.ui.design.TextMuted
import app.ember.core.ui.design.Spacing16
import app.ember.core.ui.design.Spacing24
import app.ember.core.ui.design.RadiusLG
import app.ember.core.ui.design.AnimationStandard

/**
 * Widget Gallery Screen
 * 
 * Displays previews of all available widgets following the Golden Blueprint.
 * Users can see how widgets will look before adding them to their home screen.
 */
@Composable
fun WidgetGallery(
    modifier: Modifier = Modifier,
    onAddWidget: (WidgetSize) -> Unit = {},
    onClose: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmberInk)
            .padding(Spacing24)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Widget Gallery",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextStrong
                )
            )
            
            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Close",
                    tint = TextMuted
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        Text(
            text = "Choose a widget to add to your home screen",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextMuted
            )
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Widget previews
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing16)
        ) {
            items(WidgetSize.values()) { size ->
                WidgetPreviewCard(
                    size = size,
                    onClick = { onAddWidget(size) }
                )
            }
        }
    }
}

@Composable
private fun WidgetPreviewCard(
    size: WidgetSize,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(220),
        label = "widget_preview_scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF16181C)
        ),
        shape = RoundedCornerShape(RadiusLG)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            // Widget preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(size.previewHeight.dp)
                    .background(
                        Color(0xFF0B0B0C),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(Spacing16)
            ) {
                WidgetPreviewContent(size = size)
            }
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Widget info
            Column {
                Text(
                    text = size.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextStrong
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = size.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextMuted
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Tap to add to home screen",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = EmberFlame,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun WidgetPreviewContent(size: WidgetSize) {
    when (size) {
        WidgetSize.SMALL -> SmallWidgetPreview()
        WidgetSize.MEDIUM -> MediumWidgetPreview()
        WidgetSize.LARGE -> LargeWidgetPreview()
    }
}

@Composable
private fun SmallWidgetPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sample Track",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextStrong
            )
        )
        
        Text(
            text = "Artist Name",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextMuted
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(EmberFlame, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun MediumWidgetPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sample Track",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextStrong
            )
        )
        
        Text(
            text = "Artist Name",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextMuted
            )
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFF2E3140), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⏮",
                    color = TextStrong,
                    fontSize = 10.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(EmberFlame, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFF2E3140), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⏭",
                    color = TextStrong,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun LargeWidgetPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sample Track",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextStrong
            )
        )
        
        Text(
            text = "Artist Name",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextMuted
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF2E3140), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⏮",
                    color = TextStrong,
                    fontSize = 12.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(EmberFlame, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF2E3140), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⏭",
                    color = TextStrong,
                    fontSize = 12.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF2E3140), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎛",
                    color = TextStrong,
                    fontSize = 12.sp
                )
            }
        }
    }
}

enum class WidgetSize(
    val displayName: String,
    val description: String,
    val previewHeight: Int
) {
    SMALL(
        displayName = "Small Widget",
        description = "Track info + play/pause (1×2)",
        previewHeight = 80
    ),
    MEDIUM(
        displayName = "Medium Widget", 
        description = "Track info + controls + next (2×2)",
        previewHeight = 120
    ),
    LARGE(
        displayName = "Large Widget",
        description = "Full controls + EQ shortcut (4×2)",
        previewHeight = 160
    )
}
