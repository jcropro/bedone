package app.ember.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.*

/**
 * MASTER_BLUEPRINT compliant skeleton loading components
 * 
 * Features:
 * - Shimmer animation with proper timing
 * - Proper spacing and sizing
 * - Brand-consistent colors
 * - Accessibility support
 */

/**
 * Shimmer animation for skeleton loading
 */
@Composable
private fun ShimmerEffect(
    modifier: Modifier = Modifier,
    color: Color = TextMuted.copy(alpha = 0.3f),
    shimmerColor: Color = TextMuted.copy(alpha = 0.6f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EasingStandard),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Box(
        modifier = modifier
            .background(color)
            .clip(RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(shimmerColor.copy(alpha = alpha))
        )
    }
}

/**
 * Song list skeleton - shows loading state for songs list
 */
@Composable
fun SongListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 8
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) {
            SongItemSkeleton()
        }
    }
}

@Composable
private fun SongItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Artwork placeholder
        ShimmerEffect(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Title and subtitle
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
            )
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Duration placeholder
        ShimmerEffect(
            modifier = Modifier
                .width(40.dp)
                .height(12.dp)
        )
    }
}

/**
 * Playlist list skeleton
 */
@Composable
fun PlaylistListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 6
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) {
            PlaylistItemSkeleton()
        }
    }
}

@Composable
private fun PlaylistItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Playlist icon placeholder
        ShimmerEffect(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Title and track count
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
            )
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(14.dp)
            )
        }
    }
}

/**
 * Album grid skeleton
 */
@Composable
fun AlbumGridSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 12
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) {
            AlbumItemSkeleton()
        }
    }
}

@Composable
private fun AlbumItemSkeleton() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Album artwork placeholder
        ShimmerEffect(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Album title
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Artist name
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(14.dp)
        )
    }
}

/**
 * Artist grid skeleton
 */
@Composable
fun ArtistGridSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 12
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) {
            ArtistItemSkeleton()
        }
    }
}

@Composable
private fun ArtistItemSkeleton() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Artist avatar placeholder
        ShimmerEffect(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Artist name
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Album count
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(14.dp)
        )
    }
}

/**
 * Genre chips skeleton
 */
@Composable
fun GenreChipsSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 12
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(itemCount) {
            GenreChipSkeleton()
        }
    }
}

@Composable
private fun GenreChipSkeleton() {
    ShimmerEffect(
        modifier = Modifier
            .width(80.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

/**
 * Folder list skeleton
 */
@Composable
fun FolderListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 8
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) {
            FolderItemSkeleton()
        }
    }
}

@Composable
private fun FolderItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Folder icon placeholder
        ShimmerEffect(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Folder name and path
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
            )
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Track count
        ShimmerEffect(
            modifier = Modifier
                .width(40.dp)
                .height(12.dp)
        )
    }
}

/**
 * Audiobook list skeleton
 */
@Composable
fun AudiobookListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 6
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) {
            AudiobookItemSkeleton()
        }
    }
}

@Composable
private fun AudiobookItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cover placeholder
        ShimmerEffect(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Title, author, and narrator
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(16.dp)
            )
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
            )
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Duration
        ShimmerEffect(
            modifier = Modifier
                .width(50.dp)
                .height(12.dp)
        )
    }
}

/**
 * Podcast list skeleton
 */
@Composable
fun PodcastListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 6
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) {
            PodcastItemSkeleton()
        }
    }
}

@Composable
private fun PodcastItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Artwork placeholder
        ShimmerEffect(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Title, series, and date
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(16.dp)
            )
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
            )
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Duration
        ShimmerEffect(
            modifier = Modifier
                .width(50.dp)
                .height(12.dp)
        )
    }
}

/**
 * Video list skeleton
 */
@Composable
fun VideoListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 6
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) {
            VideoItemSkeleton()
        }
    }
}

@Composable
private fun VideoItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail placeholder
        ShimmerEffect(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Title and duration
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(16.dp)
            )
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Duration
        ShimmerEffect(
            modifier = Modifier
                .width(50.dp)
                .height(12.dp)
        )
    }
}
