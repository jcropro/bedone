package app.ember.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.*

/**
 * MASTER_BLUEPRINT compliant Loading Skeleton components
 * 
 * Features:
 * - Shimmer animation with brand colors
 * - Proper spacing and sizing
 * - Multiple skeleton types for different content
 * - Smooth transitions and animations
 * - Accessibility support
 */
@Composable
fun LoadingSkeleton(
    modifier: Modifier = Modifier,
    width: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp.Unspecified,
    height: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp.Unspecified,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(4.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EasingStandard),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        TextMuted.copy(alpha = 0.1f),
                        TextMuted.copy(alpha = 0.2f),
                        TextMuted.copy(alpha = 0.1f)
                    ),
                    start = androidx.compose.ui.geometry.Offset(
                        shimmerTranslate - 300f,
                        0f
                    ),
                    end = androidx.compose.ui.geometry.Offset(
                        shimmerTranslate,
                        0f
                    )
                )
            )
    )
}

/**
 * Song list skeleton - matches SongsList layout
 */
@Composable
fun SongListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 8
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(itemCount) {
            SongItemSkeleton()
            if (it < itemCount - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SongItemSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Artwork skeleton
        LoadingSkeleton(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Song info skeleton
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Title skeleton
            LoadingSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp),
                shape = RoundedCornerShape(2.dp)
            )

            // Artist/Album skeleton
            LoadingSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp),
                shape = RoundedCornerShape(2.dp)
            )
        }

        // Duration skeleton
        LoadingSkeleton(
            modifier = Modifier
                .width(40.dp)
                .height(12.dp),
            shape = RoundedCornerShape(2.dp)
        )

        // More button skeleton
        LoadingSkeleton(
            modifier = Modifier.size(24.dp),
            shape = CircleShape
        )
    }
}

/**
 * Album grid skeleton - matches AlbumsList layout
 */
@Composable
fun AlbumGridSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 12
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(itemCount / 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AlbumItemSkeleton(modifier = Modifier.weight(1f))
                AlbumItemSkeleton(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AlbumItemSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Album artwork skeleton
        LoadingSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(8.dp)
        )

        // Album title skeleton
        LoadingSkeleton(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(16.dp),
            shape = RoundedCornerShape(2.dp)
        )

        // Artist name skeleton
        LoadingSkeleton(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(14.dp),
            shape = RoundedCornerShape(2.dp)
        )
    }
}

/**
 * Artist grid skeleton - matches ArtistsList layout
 */
@Composable
fun ArtistGridSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 12
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(itemCount / 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ArtistItemSkeleton(modifier = Modifier.weight(1f))
                ArtistItemSkeleton(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ArtistItemSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Artist avatar skeleton
        LoadingSkeleton(
            modifier = Modifier.size(80.dp),
            shape = CircleShape
        )

        // Artist name skeleton
        LoadingSkeleton(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(16.dp),
            shape = RoundedCornerShape(2.dp)
        )

        // Album count skeleton
        LoadingSkeleton(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(12.dp),
            shape = RoundedCornerShape(2.dp)
        )
    }
}

/**
 * Playlist list skeleton - matches PlaylistsList layout
 */
@Composable
fun PlaylistListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 6
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) {
            PlaylistItemSkeleton()
        }
    }
}

@Composable
private fun PlaylistItemSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Playlist artwork skeleton
        LoadingSkeleton(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Playlist info skeleton
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Playlist name skeleton
            LoadingSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(18.dp),
                shape = RoundedCornerShape(2.dp)
            )

            // Track count skeleton
            LoadingSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(14.dp),
                shape = RoundedCornerShape(2.dp)
            )
        }

        // Duration skeleton
        LoadingSkeleton(
            modifier = Modifier
                .width(50.dp)
                .height(12.dp),
            shape = RoundedCornerShape(2.dp)
        )
    }
}

/**
 * Folder list skeleton - matches FoldersList layout
 */
@Composable
fun FolderListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 8
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) {
            FolderItemSkeleton()
        }
    }
}

@Composable
private fun FolderItemSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Folder icon skeleton
        LoadingSkeleton(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(4.dp)
        )

        // Folder info skeleton
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Folder name skeleton
            LoadingSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp),
                shape = RoundedCornerShape(2.dp)
            )

            // File count skeleton
            LoadingSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(14.dp),
                shape = RoundedCornerShape(2.dp)
            )
        }

        // Arrow skeleton
        LoadingSkeleton(
            modifier = Modifier.size(16.dp),
            shape = RoundedCornerShape(2.dp)
        )
    }
}

/**
 * Genre chips skeleton - matches GenresList layout
 */
@Composable
fun GenreChipsSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 12
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(itemCount) {
            GenreChipSkeleton()
        }
    }
}

@Composable
private fun GenreChipSkeleton(
    modifier: Modifier = Modifier
) {
    LoadingSkeleton(
        modifier = modifier
            .width(80.dp)
            .height(32.dp),
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Audiobook list skeleton - matches AudiobooksList layout
 */
@Composable
fun AudiobookListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 6
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) {
            AudiobookItemSkeleton()
        }
    }
}

@Composable
private fun AudiobookItemSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Audiobook artwork skeleton
        LoadingSkeleton(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Audiobook info skeleton
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Title skeleton
            LoadingSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(18.dp),
                shape = RoundedCornerShape(2.dp)
            )

            // Author skeleton
            LoadingSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp),
                shape = RoundedCornerShape(2.dp)
            )

            // Duration skeleton
            LoadingSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(12.dp),
                shape = RoundedCornerShape(2.dp)
            )
        }
    }
}

/**
 * Podcast list skeleton - matches PodcastsList layout
 */
@Composable
fun PodcastListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 6
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemCount) {
            PodcastItemSkeleton()
        }
    }
}

@Composable
private fun PodcastItemSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Podcast artwork skeleton
        LoadingSkeleton(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Podcast info skeleton
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Title skeleton
            LoadingSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(18.dp),
                shape = RoundedCornerShape(2.dp)
            )

            // Publisher skeleton
            LoadingSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp),
                shape = RoundedCornerShape(2.dp)
            )

            // Episode count skeleton
            LoadingSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(12.dp),
                shape = RoundedCornerShape(2.dp)
            )
        }
    }
}

/**
 * Video grid skeleton - matches VideosList layout
 */
@Composable
fun VideoGridSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 12
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(itemCount / 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VideoItemSkeleton(modifier = Modifier.weight(1f))
                VideoItemSkeleton(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun VideoItemSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Video thumbnail skeleton
        LoadingSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            shape = RoundedCornerShape(8.dp)
        )

        // Video title skeleton
        LoadingSkeleton(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(16.dp),
            shape = RoundedCornerShape(2.dp)
        )

        // Duration skeleton
        LoadingSkeleton(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(12.dp),
            shape = RoundedCornerShape(2.dp)
        )
    }
}

/**
 * Top bar skeleton - matches library top bars
 */
@Composable
fun TopBarSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Menu button skeleton
        LoadingSkeleton(
            modifier = Modifier.size(24.dp),
            shape = CircleShape
        )

        // Title skeleton
        LoadingSkeleton(
            modifier = Modifier
                .width(120.dp)
                .height(20.dp),
            shape = RoundedCornerShape(4.dp)
        )

        // Actions skeleton
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LoadingSkeleton(
                modifier = Modifier.size(24.dp),
                shape = CircleShape
            )
            LoadingSkeleton(
                modifier = Modifier.size(24.dp),
                shape = CircleShape
            )
        }
    }
}

/**
 * Filter chips skeleton - matches filter chip rows
 */
@Composable
fun FilterChipsSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(itemCount) {
            FilterChipSkeleton()
        }
    }
}

@Composable
private fun FilterChipSkeleton(
    modifier: Modifier = Modifier
) {
    LoadingSkeleton(
        modifier = modifier
            .width(100.dp)
            .height(32.dp),
        shape = RoundedCornerShape(16.dp)
    )
}
