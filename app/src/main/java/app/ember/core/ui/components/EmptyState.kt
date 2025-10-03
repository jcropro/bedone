package app.ember.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.studio.R
import app.ember.core.ui.design.*

/**
 * MASTER_BLUEPRINT compliant Empty State component
 * 
 * Features:
 * - Faint flame illustration (brand illustration)
 * - Animated breathing effect
 * - Clear messaging with typography hierarchy
 * - Primary CTA button
 * - Secondary action button
 * - Proper spacing and alignment
 */
@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    primaryActionText: String,
    secondaryActionText: String? = null,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: () -> Unit = {},
    illustrationType: EmptyStateIllustration = EmptyStateIllustration.Flame
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Brand illustration with breathing animation
        EmptyStateIllustration(
            type = illustrationType,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Title - MASTER_BLUEPRINT: Display 32-44pt (700)
        Text(
            text = title,
            fontSize = TypographyDisplaySmall, // 32sp
            fontWeight = TypographyDisplayWeight, // Bold (700)
            color = TextStrong,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subtitle - MASTER_BLUEPRINT: Subtitle 13-14pt (500 @70% opacity)
        Text(
            text = subtitle,
            fontSize = TypographySubtitleMedium, // 14sp
            fontWeight = TypographySubtitleWeight, // Medium (500)
            color = TextMuted.copy(alpha = TypographySubtitleOpacity), // 70% opacity
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Primary CTA button
        Button(
            onClick = onPrimaryAction,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmberFlame,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = primaryActionText,
                fontSize = TypographyBodyLarge, // 16sp
                fontWeight = TypographyBodyWeight, // Medium (500)
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Secondary action button (if provided)
        if (secondaryActionText != null) {
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onSecondaryAction,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextStrong
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = TextMuted.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = secondaryActionText,
                    fontSize = TypographyBodyMedium, // 14sp
                    fontWeight = TypographyBodyWeight, // Medium (500)
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Empty state illustration types
 */
enum class EmptyStateIllustration {
    Flame,      // Default flame illustration
    Folder,     // Folder illustration for scan/import
    Refresh     // Refresh illustration for rescan
}

/**
 * Animated brand illustration for empty states
 */
@Composable
private fun EmptyStateIllustration(
    type: EmptyStateIllustration,
    modifier: Modifier = Modifier
) {
    // Breathing animation - subtle scale and alpha changes
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EasingStandard),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EasingStandard),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            EmptyStateIllustration.Flame -> {
                // Faint flame illustration with breathing effect
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alpha)
                ) {
                    val w = size.width
                    val h = size.height
                    val center = Offset(w * 0.5f, h * 0.5f)
                    
                    // Soft flame glow
                    val flameBrush = Brush.radialGradient(
                        colors = listOf(
                            EmberFlame.copy(alpha = 0.15f),
                            EmberFlame.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = w.coerceAtLeast(h) * 0.4f
                    )
                    
                    drawCircle(
                        brush = flameBrush,
                        radius = w.coerceAtLeast(h) * 0.4f,
                        center = center
                    )
                    
                    // Subtle flame shape
                    withTransform({
                        translate(center.x - w * 0.3f, center.y - h * 0.2f)
                        scale(0.6f, 0.6f)
                    }) {
                        // Simple flame path
                        drawRect(
                            color = EmberFlame.copy(alpha = 0.1f),
                            topLeft = Offset(0f, h * 0.3f),
                            size = androidx.compose.ui.geometry.Size(w * 0.6f, h * 0.4f)
                        )
                    }
                }
                
                // Brand glyph overlay
                Icon(
                    painter = painterResource(id = R.drawable.ic_ember_logo),
                    contentDescription = null,
                    tint = EmberFlame.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(48.dp)
                        .alpha(alpha)
                        .scale(scale)
                )
            }
            
            EmptyStateIllustration.Folder -> {
                // Folder illustration
                Icon(
                    imageVector = Icons.Filled.FolderOpen,
                    contentDescription = null,
                    tint = TextMuted.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(64.dp)
                        .alpha(alpha)
                        .scale(scale)
                )
            }
            
            EmptyStateIllustration.Refresh -> {
                // Refresh illustration
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = TextMuted.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(64.dp)
                        .alpha(alpha)
                        .scale(scale)
                )
            }
        }
    }
}

/**
 * Predefined empty states for common scenarios
 */
@Composable
fun SongsEmptyState(
    modifier: Modifier = Modifier,
    onScanLibrary: () -> Unit = {},
    onImportMusic: () -> Unit = {}
) {
    EmptyState(
        modifier = modifier,
        title = "No Songs Found",
        subtitle = "Your music library is empty. Scan your device or import music files to get started.",
        primaryActionText = "Scan Library",
        secondaryActionText = "Import Music",
        onPrimaryAction = onScanLibrary,
        onSecondaryAction = onImportMusic,
        illustrationType = EmptyStateIllustration.Flame
    )
}

@Composable
fun PlaylistsEmptyState(
    modifier: Modifier = Modifier,
    onCreatePlaylist: () -> Unit = {},
    onImportPlaylist: () -> Unit = {}
) {
    EmptyState(
        modifier = modifier,
        title = "No Playlists",
        subtitle = "Create your first playlist or import existing playlists to organize your music.",
        primaryActionText = "Create Playlist",
        secondaryActionText = "Import Playlist",
        onPrimaryAction = onCreatePlaylist,
        onSecondaryAction = onImportPlaylist,
        illustrationType = EmptyStateIllustration.Flame
    )
}

@Composable
fun AlbumsEmptyState(
    modifier: Modifier = Modifier,
    onScanLibrary: () -> Unit = {},
    onImportMusic: () -> Unit = {}
) {
    EmptyState(
        modifier = modifier,
        title = "No Albums",
        subtitle = "No albums found in your library. Scan your device to discover albums from your music collection.",
        primaryActionText = "Scan Library",
        secondaryActionText = "Import Music",
        onPrimaryAction = onScanLibrary,
        onSecondaryAction = onImportMusic,
        illustrationType = EmptyStateIllustration.Flame
    )
}

@Composable
fun ArtistsEmptyState(
    modifier: Modifier = Modifier,
    onScanLibrary: () -> Unit = {},
    onImportMusic: () -> Unit = {}
) {
    EmptyState(
        modifier = modifier,
        title = "No Artists",
        subtitle = "No artists found in your library. Scan your device to discover artists from your music collection.",
        primaryActionText = "Scan Library",
        secondaryActionText = "Import Music",
        onPrimaryAction = onScanLibrary,
        onSecondaryAction = onImportMusic,
        illustrationType = EmptyStateIllustration.Flame
    )
}

@Composable
fun GenresEmptyState(
    modifier: Modifier = Modifier,
    onScanLibrary: () -> Unit = {},
    onImportMusic: () -> Unit = {}
) {
    EmptyState(
        modifier = modifier,
        title = "No Genres",
        subtitle = "No genres found in your library. Scan your device to discover genres from your music collection.",
        primaryActionText = "Scan Library",
        secondaryActionText = "Import Music",
        onPrimaryAction = onScanLibrary,
        onSecondaryAction = onImportMusic,
        illustrationType = EmptyStateIllustration.Flame
    )
}

@Composable
fun FoldersEmptyState(
    modifier: Modifier = Modifier,
    onChooseFolders: () -> Unit = {},
    onScanLibrary: () -> Unit = {}
) {
    EmptyState(
        modifier = modifier,
        title = "No Folders",
        subtitle = "No music folders selected. Choose folders to scan or let Ember automatically discover your music.",
        primaryActionText = "Choose Folders",
        secondaryActionText = "Auto Scan",
        onPrimaryAction = onChooseFolders,
        onSecondaryAction = onScanLibrary,
        illustrationType = EmptyStateIllustration.Folder
    )
}

@Composable
fun AudiobooksEmptyState(
    modifier: Modifier = Modifier,
    onScanLibrary: () -> Unit = {},
    onImportAudiobooks: () -> Unit = {}
) {
    EmptyState(
        modifier = modifier,
        title = "No Audiobooks",
        subtitle = "No audiobooks found in your library. Scan your device or import audiobook files to get started.",
        primaryActionText = "Scan Library",
        secondaryActionText = "Import Audiobooks",
        onPrimaryAction = onScanLibrary,
        onSecondaryAction = onImportAudiobooks,
        illustrationType = EmptyStateIllustration.Flame
    )
}

@Composable
fun PodcastsEmptyState(
    modifier: Modifier = Modifier,
    onScanLibrary: () -> Unit = {},
    onImportPodcasts: () -> Unit = {}
) {
    EmptyState(
        modifier = modifier,
        title = "No Podcasts",
        subtitle = "No podcasts found in your library. Scan your device or import podcast files to get started.",
        primaryActionText = "Scan Library",
        secondaryActionText = "Import Podcasts",
        onPrimaryAction = onScanLibrary,
        onSecondaryAction = onImportPodcasts,
        illustrationType = EmptyStateIllustration.Flame
    )
}

@Composable
fun VideosEmptyState(
    modifier: Modifier = Modifier,
    onScanLibrary: () -> Unit = {},
    onImportVideos: () -> Unit = {}
) {
    EmptyState(
        modifier = modifier,
        title = "No Videos",
        subtitle = "No videos found in your library. Scan your device or import video files to get started.",
        primaryActionText = "Scan Library",
        secondaryActionText = "Import Videos",
        onPrimaryAction = onScanLibrary,
        onSecondaryAction = onImportVideos,
        illustrationType = EmptyStateIllustration.Flame
    )
}
