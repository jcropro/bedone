package app.ember.studio.library

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.design.*
import app.ember.studio.R

/**
 * Premium Library Statistics Screen
 * 
 * Features:
 * - Comprehensive library analytics
 * - Visual charts and progress indicators
 * - Premium visual design with glass morphism
 * - Smooth animations and micro-interactions
 * - Detailed breakdowns by category
 * - Export capabilities
 */
@Composable
fun LibraryStatisticsScreen(
    modifier: Modifier = Modifier,
    statistics: LibraryStatistics,
    onExportStatistics: () -> Unit,
    onRefreshStatistics: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmberInk)
            .padding(Spacing16)
    ) {
        // Header
        LibraryStatisticsHeader(
            modifier = Modifier.fillMaxWidth(),
            onExport = onExportStatistics,
            onRefresh = onRefreshStatistics
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Overview Cards
        LibraryOverviewCards(
            statistics = statistics,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Detailed Statistics
        LibraryDetailedStatistics(
            statistics = statistics,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LibraryStatisticsHeader(
    modifier: Modifier = Modifier,
    onExport: () -> Unit,
    onRefresh: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Analytics,
                contentDescription = "Library Statistics",
                tint = EmberFlame,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(Spacing12))
            
            Column {
                Text(
                    text = "Library Statistics",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextStrong,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Comprehensive library analytics",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing8)
        ) {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    tint = TextMuted
                )
            }
            
            IconButton(onClick = onExport) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "Export",
                    tint = EmberFlame
                )
            }
        }
    }
}

@Composable
private fun LibraryOverviewCards(
    statistics: LibraryStatistics,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing12)
    ) {
        // Total Tracks Card
        StatisticsCard(
            title = "Total Tracks",
            value = statistics.totalTracks.toString(),
            subtitle = "${statistics.totalDuration}",
            icon = Icons.Filled.MusicNote,
            color = EmberFlame,
            modifier = Modifier.weight(1f)
        )
        
        // Total Albums Card
        StatisticsCard(
            title = "Albums",
            value = statistics.totalAlbums.toString(),
            subtitle = "${statistics.uniqueArtists} artists",
            icon = Icons.Filled.Album,
            color = AccentIce,
            modifier = Modifier.weight(1f)
        )
        
        // Total Size Card
        StatisticsCard(
            title = "Library Size",
            value = formatFileSize(statistics.totalSizeBytes),
            subtitle = "${statistics.totalFolders} folders",
            icon = Icons.Filled.Storage,
            color = AccentCool,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatisticsCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(Spacing8))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = title,
                color = TextMuted,
                fontSize = 12.sp
            )
            
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun LibraryDetailedStatistics(
    statistics: LibraryStatistics,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing16)
    ) {
        // Genre Distribution
        item {
            GenreDistributionCard(
                genreDistribution = statistics.genreDistribution,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Year Distribution
        item {
            YearDistributionCard(
                yearDistribution = statistics.yearDistribution,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // File Format Distribution
        item {
            FormatDistributionCard(
                formatDistribution = statistics.formatDistribution,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Top Artists
        item {
            TopArtistsCard(
                topArtists = statistics.topArtists,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Top Albums
        item {
            TopAlbumsCard(
                topAlbums = statistics.topAlbums,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Storage Usage
        item {
            StorageUsageCard(
                storageUsage = statistics.storageUsage,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GenreDistributionCard(
    genreDistribution: List<GenreStatistic>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Text(
                text = "Genre Distribution",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            genreDistribution.take(5).forEach { genre ->
                GenreStatisticItem(
                    genre = genre,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (genre != genreDistribution.take(5).last()) {
                    Spacer(modifier = Modifier.height(Spacing8))
                }
            }
        }
    }
}

@Composable
private fun GenreStatisticItem(
    genre: GenreStatistic,
    modifier: Modifier = Modifier
) {
    val progress = if (genre.totalTracks > 0) {
        genre.trackCount.toFloat() / genre.totalTracks.toFloat()
    } else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "progress"
    )
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Genre color indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(genre.color, RoundedCornerShape(RadiusPill))
        )
        
        Spacer(modifier = Modifier.width(Spacing8))
        
        // Genre info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = genre.name,
                color = TextStrong,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "${genre.trackCount} tracks",
                color = TextMuted,
                fontSize = 12.sp
            )
        }
        
        // Progress bar
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(4.dp)
                .background(EmberOutline.copy(alpha = 0.3f), RoundedCornerShape(RadiusPill))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(genre.color, RoundedCornerShape(RadiusPill))
            )
        }
    }
}

@Composable
private fun YearDistributionCard(
    yearDistribution: List<YearStatistic>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Text(
                text = "Year Distribution",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            yearDistribution.take(5).forEach { year ->
                YearStatisticItem(
                    year = year,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (year != yearDistribution.take(5).last()) {
                    Spacer(modifier = Modifier.height(Spacing8))
                }
            }
        }
    }
}

@Composable
private fun YearStatisticItem(
    year: YearStatistic,
    modifier: Modifier = Modifier
) {
    val progress = if (year.totalTracks > 0) {
        year.trackCount.toFloat() / year.totalTracks.toFloat()
    } else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "progress"
    )
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = year.year.toString(),
            color = TextStrong,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(60.dp)
        )
        
        Spacer(modifier = Modifier.width(Spacing8))
        
        // Progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .background(EmberOutline.copy(alpha = 0.3f), RoundedCornerShape(RadiusPill))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(EmberFlame, RoundedCornerShape(RadiusPill))
            )
        }
        
        Spacer(modifier = Modifier.width(Spacing8))
        
        Text(
            text = "${year.trackCount}",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.width(40.dp)
        )
    }
}

@Composable
private fun FormatDistributionCard(
    formatDistribution: List<FormatStatistic>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Text(
                text = "File Format Distribution",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            formatDistribution.forEach { format ->
                FormatStatisticItem(
                    format = format,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (format != formatDistribution.last()) {
                    Spacer(modifier = Modifier.height(Spacing8))
                }
            }
        }
    }
}

@Composable
private fun FormatStatisticItem(
    format: FormatStatistic,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = format.extension.uppercase(),
            color = TextStrong,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "${format.trackCount} tracks",
            color = TextMuted,
            fontSize = 12.sp
        )
        
        Text(
            text = format.sizeFormatted,
            color = TextMuted,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun TopArtistsCard(
    topArtists: List<ArtistStatistic>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Text(
                text = "Top Artists",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            topArtists.take(5).forEachIndexed { index, artist ->
                TopArtistItem(
                    artist = artist,
                    rank = index + 1,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (index < topArtists.take(5).size - 1) {
                    Spacer(modifier = Modifier.height(Spacing8))
                }
            }
        }
    }
}

@Composable
private fun TopArtistItem(
    artist: ArtistStatistic,
    rank: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Text(
            text = "$rank",
            color = EmberFlame,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )
        
        Spacer(modifier = Modifier.width(Spacing8))
        
        // Artist info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = artist.name,
                color = TextStrong,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "${artist.trackCount} tracks • ${artist.albumCount} albums",
                color = TextMuted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun TopAlbumsCard(
    topAlbums: List<AlbumStatistic>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Text(
                text = "Top Albums",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            topAlbums.take(5).forEachIndexed { index, album ->
                TopAlbumItem(
                    album = album,
                    rank = index + 1,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (index < topAlbums.take(5).size - 1) {
                    Spacer(modifier = Modifier.height(Spacing8))
                }
            }
        }
    }
}

@Composable
private fun TopAlbumItem(
    album: AlbumStatistic,
    rank: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Text(
            text = "$rank",
            color = EmberFlame,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )
        
        Spacer(modifier = Modifier.width(Spacing8))
        
        // Album info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = album.name,
                color = TextStrong,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "${album.artist} • ${album.trackCount} tracks",
                color = TextMuted,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StorageUsageCard(
    storageUsage: StorageUsage,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Text(
                text = "Storage Usage",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Audio files
            StorageUsageItem(
                label = "Audio Files",
                size = storageUsage.audioSizeBytes,
                totalSize = storageUsage.totalSizeBytes,
                color = EmberFlame,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Spacing8))
            
            // Video files
            StorageUsageItem(
                label = "Video Files",
                size = storageUsage.videoSizeBytes,
                totalSize = storageUsage.totalSizeBytes,
                color = AccentIce,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Spacing8))
            
            // Other files
            StorageUsageItem(
                label = "Other Files",
                size = storageUsage.otherSizeBytes,
                totalSize = storageUsage.totalSizeBytes,
                color = AccentCool,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StorageUsageItem(
    label: String,
    size: Long,
    totalSize: Long,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (totalSize > 0) {
        size.toFloat() / totalSize.toFloat()
    } else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "progress"
    )
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = TextStrong,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = formatFileSize(size),
                color = TextMuted,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing4))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(EmberOutline.copy(alpha = 0.3f), RoundedCornerShape(RadiusPill))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(color, RoundedCornerShape(RadiusPill))
            )
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> String.format("%.1f GB", gb)
        mb >= 1 -> String.format("%.1f MB", mb)
        kb >= 1 -> String.format("%.1f KB", kb)
        else -> "$bytes B"
    }
}
