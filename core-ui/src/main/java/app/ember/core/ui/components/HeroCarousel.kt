package app.ember.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme
import kotlin.math.abs

/**
 * A hero carousel for displaying album art with smooth transitions
 */
@Composable
fun HeroCarousel(
    modifier: Modifier = Modifier,
    items: List<CarouselItem>,
    onItemClick: (Int) -> Unit = {},
    onItemLongPress: (Int) -> Unit = {}
) {
    if (items.isEmpty()) {
        EmptyCarousel(modifier = modifier)
        return
    }

    val pagerState = rememberPagerState(pageCount = { items.size })
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            CarouselItemCard(
                item = items[page],
                isActive = page == pagerState.currentPage,
                onClick = { onItemClick(page) },
                onLongPress = { onItemLongPress(page) }
            )
        }
        
        if (items.size > 1) {
            Spacer(modifier = Modifier.height(16.dp))
            CarouselIndicators(
                pageCount = items.size,
                currentPage = pagerState.currentPage
            )
        }
    }
}

@Composable
private fun CarouselItemCard(
    item: CarouselItem,
    isActive: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.9f,
        animationSpec = tween(durationMillis = 300),
        label = "carousel-scale"
    )
    
    Card(
        modifier = Modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { onClick() }
                ) { _, _ -> }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 8.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Album art or placeholder
            if (item.imageUrl != null) {
                Image(
                    painter = painterResource(id = item.imageUrl),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎵",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
            
            // Gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
            
            // Content overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.subtitle.isNotEmpty()) {
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun CarouselIndicators(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            val size = if (isActive) 12.dp else 8.dp
            val color = if (isActive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            }
            
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(size)
                    .background(
                        color = color,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun EmptyCarousel(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🎵",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "No music found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class CarouselItem(
    val title: String,
    val subtitle: String = "",
    val imageUrl: Int? = null // Resource ID for now, could be Uri in real implementation
)

@Preview(showBackground = true)
@Composable
private fun HeroCarouselPreview() {
    EmberTheme {
        val sampleItems = listOf(
            CarouselItem("Song Title 1", "Artist Name", null),
            CarouselItem("Song Title 2", "Artist Name", null),
            CarouselItem("Song Title 3", "Artist Name", null)
        )
        
        HeroCarousel(
            items = sampleItems,
            modifier = Modifier.padding(16.dp)
        )
    }
}