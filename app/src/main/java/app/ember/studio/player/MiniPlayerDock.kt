package app.ember.studio.player

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.design.*
import app.ember.core.ui.components.GlassMorphismCard
import app.ember.core.ui.components.FlameBurstEffect
import app.ember.core.ui.components.SpringMorphingButton
import app.ember.studio.R

/**
 * Premium mini-player dock for Ember Audio Player
 * Implements the persistent mini-player as specified in MASTER_BLUEPRINT.md
 * 
 * Features:
 * - Persistent dock at bottom of screen
 * - Album art with glass morphism effect
 * - Track info with smooth animations
 * - Play/pause button with flame accent
 * - Skip controls
 * - Tap to expand to full player
 * - Premium visual design with glow effects
 */
@Composable
fun MiniPlayerDock(
    modifier: Modifier = Modifier,
    trackTitle: String = "Unknown Track",
    trackArtist: String = "Unknown Artist",
    albumArtUrl: String? = null,
    isPlaying: Boolean = false,
    isVisible: Boolean = true,
    onPlayPauseClick: () -> Unit = {},
    onSkipNextClick: () -> Unit = {},
    onSkipPreviousClick: () -> Unit = {},
    onExpandClick: () -> Unit = {}
) {
    if (!isVisible) return
    
    val backgroundColor by animateColorAsState(
        targetValue = EmberInk.copy(alpha = 0.95f),
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "backgroundColor"
    )
    
    val playButtonScale by animateFloatAsState(
        targetValue = if (isPlaying) 1.1f else 1f,
        animationSpec = tween(MotionTap, easing = EasingSpring), label = "playButtonScale"
    )
    
    GlassMorphismCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing16, vertical = Spacing8),
        isHovered = isPlaying,
        onClick = onExpandClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art with glass effect
            MiniPlayerAlbumArt(
                albumArtUrl = albumArtUrl,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(Spacing12))
            
            // Track info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = trackTitle,
                    color = TextStrong,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = trackArtist,
                    color = TextMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing8))
            
            // Playback controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                // Skip previous
                IconButton(
                    onClick = onSkipPreviousClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Skip Previous",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Play/Pause button with flame burst effect
                FlameBurstEffect(
                    isActive = isPlaying,
                    modifier = Modifier
                ) {
                    SpringMorphingButton(
                        isActive = isPlaying,
                        activeIcon = {
                            Icon(
                                imageVector = Icons.Filled.Pause,
                                contentDescription = "Pause",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        inactiveIcon = {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = onPlayPauseClick,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                // Skip next
                IconButton(
                    onClick = onSkipNextClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Skip Next",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniPlayerAlbumArt(
    albumArtUrl: String?,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(MotionTransition, easing = EasingSpring),
        label = "albumArtScale"
    )
    
    GlassMorphismCard(
        modifier = modifier.scale(scale),
        isHovered = false,
        onClick = null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Always show flame gradient background for premium look
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = FlameGradient,
                        shape = RoundedCornerShape(RadiusMD)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = if (albumArtUrl != null) "Album Art" else "No Album Art",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Mini-player state for managing visibility and content
 */
data class MiniPlayerState(
    val isVisible: Boolean = false,
    val trackTitle: String = "",
    val trackArtist: String = "",
    val albumArtUrl: String? = null,
    val isPlaying: Boolean = false,
    val canSkipNext: Boolean = true,
    val canSkipPrevious: Boolean = true
)
