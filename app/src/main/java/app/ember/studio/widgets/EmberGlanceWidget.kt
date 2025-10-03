package app.ember.studio.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import app.ember.studio.MainActivity
import app.ember.studio.R
import app.ember.studio.playback.PlaybackEngine
import androidx.media3.common.Player

/**
 * Ember Audio Player Glance Widgets
 * 
 * Premium home screen widgets following the Golden Blueprint:
 * - Flame Minimal (1×2): Clean track info + play/pause
 * - Flame Card (2×2): Track info + controls + artwork
 * - Vinyl (2×2): Circular vinyl-style with track info
 * - Circular (1×1): Minimal circular play button
 * - Full Art (4×2): Large artwork + full controls
 * 
 * All widgets are theme-aware and follow Ember design tokens.
 */
class EmberGlanceWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val trackInfo = getCurrentTrackInfo()
                
                // For now, use Flame Card as default
                FlameCardWidget(trackInfo)
            }
        }
    }
    
    private fun getCurrentTrackInfo(): TrackInfo {
        val player = PlaybackEngine.player
        val isPlaying = player?.let { 
            it.playbackState == Player.STATE_READY && it.playWhenReady 
        } ?: false
        
        val currentTrack = player?.currentMediaItem?.mediaMetadata
        return TrackInfo(
            title = currentTrack?.title?.toString() ?: "No track",
            artist = currentTrack?.artist?.toString() ?: "Unknown artist",
            isPlaying = isPlaying,
            artworkUri = currentTrack?.artworkUri?.toString()
        )
    }
}

/**
 * Flame Minimal Widget (1×2) - Clean and minimal
 * Perfect for users who want essential controls without clutter
 */
@Composable
private fun FlameMinimalWidget(trackInfo: TrackInfo) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFF0B0B0C)))
            .cornerRadius(16.dp)
            .padding(12.dp)
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Track info
            Text(
                text = trackInfo.title,
                style = TextStyle(
                    color = ColorProvider(Color(0xFFF6F7FA)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Text(
                text = trackInfo.artist,
                style = TextStyle(
                    color = ColorProvider(Color(0xFFBAC1CC)),
                    fontSize = 12.sp
                )
            )
            
            Spacer(modifier = GlanceModifier.height(8.dp))
            
            // Play/Pause button
            Box(
                modifier = GlanceModifier
                    .size(40.dp)
                    .background(ColorProvider(Color(0xFFFF7A1A)))
                    .cornerRadius(20.dp)
                    .clickable { /* TODO: Handle play/pause */ }
            ) {
                Image(
                    provider = ImageProvider(
                        if (trackInfo.isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
                    ),
                    contentDescription = if (trackInfo.isPlaying) "Pause" else "Play",
                    modifier = GlanceModifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Flame Card Widget (2×2) - Full featured card
 * Complete track info with controls and artwork preview
 */
@Composable
private fun FlameCardWidget(trackInfo: TrackInfo) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFF0B0B0C)))
            .cornerRadius(16.dp)
            .padding(16.dp)
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            // Track info
            Text(
                text = trackInfo.title,
                style = TextStyle(
                    color = ColorProvider(Color(0xFFF6F7FA)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Text(
                text = trackInfo.artist,
                style = TextStyle(
                    color = ColorProvider(Color(0xFFBAC1CC)),
                    fontSize = 14.sp
                )
            )
            
            Spacer(modifier = GlanceModifier.height(12.dp))
            
            // Controls row
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Previous button
                Box(
                    modifier = GlanceModifier
                        .size(36.dp)
                        .background(ColorProvider(Color(0xFF1A1A1B)))
                        .cornerRadius(18.dp)
                        .clickable { /* TODO: Handle previous */ }
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_skip_previous),
                        contentDescription = "Previous",
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = GlanceModifier.width(8.dp))
                
                // Play/Pause button
                Box(
                    modifier = GlanceModifier
                        .size(44.dp)
                        .background(ColorProvider(Color(0xFFFF7A1A)))
                        .cornerRadius(22.dp)
                        .clickable { /* TODO: Handle play/pause */ }
                ) {
                    Image(
                        provider = ImageProvider(
                            if (trackInfo.isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
                        ),
                        contentDescription = if (trackInfo.isPlaying) "Pause" else "Play",
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = GlanceModifier.width(8.dp))
                
                // Next button
                Box(
                    modifier = GlanceModifier
                        .size(36.dp)
                        .background(ColorProvider(Color(0xFF1A1A1B)))
                        .cornerRadius(18.dp)
                        .clickable { /* TODO: Handle next */ }
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_skip_next),
                        contentDescription = "Next",
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * Vinyl Widget (2×2) - Circular vinyl-style design
 * Retro-inspired circular layout with track info
 */
@Composable
private fun VinylWidget(trackInfo: TrackInfo) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFF0B0B0C)))
            .cornerRadius(16.dp)
            .padding(16.dp)
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Vinyl circle
            Box(
                modifier = GlanceModifier
                    .size(80.dp)
                    .background(ColorProvider(Color(0xFF1A1A1B)))
                    .cornerRadius(40.dp)
                    .clickable { /* TODO: Handle play/pause */ }
            ) {
                // Inner circle (label)
                Box(
                    modifier = GlanceModifier
                        .size(40.dp)
                        .background(ColorProvider(Color(0xFFFF7A1A)))
                        .cornerRadius(20.dp)
                ) {
                    Image(
                        provider = ImageProvider(
                            if (trackInfo.isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
                        ),
                        contentDescription = if (trackInfo.isPlaying) "Pause" else "Play",
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
            }
            
            Spacer(modifier = GlanceModifier.height(12.dp))
            
            // Track info
            Text(
                text = trackInfo.title,
                style = TextStyle(
                    color = ColorProvider(Color(0xFFF6F7FA)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Text(
                text = trackInfo.artist,
                style = TextStyle(
                    color = ColorProvider(Color(0xFFBAC1CC)),
                    fontSize = 12.sp
                )
            )
        }
    }
}

/**
 * Circular Widget (1×1) - Minimal circular play button
 * Perfect for minimal home screen setups
 */
@Composable
private fun CircularWidget(trackInfo: TrackInfo) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFFF7A1A)))
            .cornerRadius(50.dp)
            .clickable { /* TODO: Handle play/pause */ }
    ) {
        Image(
            provider = ImageProvider(
                if (trackInfo.isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
            ),
            contentDescription = if (trackInfo.isPlaying) "Pause" else "Play",
            modifier = GlanceModifier.fillMaxSize()
        )
    }
}

/**
 * Full Art Widget (4×2) - Large artwork with full controls
 * Premium widget with artwork display and complete control set
 */
@Composable
private fun FullArtWidget(trackInfo: TrackInfo) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFF0B0B0C)))
            .cornerRadius(16.dp)
            .padding(16.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            // Artwork placeholder
            Box(
                modifier = GlanceModifier
                    .size(80.dp)
                    .background(ColorProvider(Color(0xFF1A1A1B)))
                    .cornerRadius(8.dp)
                    .clickable { /* TODO: Handle artwork click */ }
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_music_note),
                    contentDescription = "Track artwork",
                    modifier = GlanceModifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = GlanceModifier.width(16.dp))
            
            // Track info and controls
            Column(
                modifier = GlanceModifier.fillMaxSize()
            ) {
                // Track info
                Text(
                    text = trackInfo.title,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFF6F7FA)),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    text = trackInfo.artist,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFBAC1CC)),
                        fontSize = 14.sp
                    )
                )
                
                Spacer(modifier = GlanceModifier.height(12.dp))
                
                // Controls row
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Previous button
                    Box(
                        modifier = GlanceModifier
                            .size(36.dp)
                            .background(ColorProvider(Color(0xFF1A1A1B)))
                            .cornerRadius(18.dp)
                            .clickable { /* TODO: Handle previous */ }
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_skip_previous),
                            contentDescription = "Previous",
                            modifier = GlanceModifier.fillMaxSize()
                        )
                    }
                    
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    
                    // Play/Pause button
                    Box(
                        modifier = GlanceModifier
                            .size(44.dp)
                            .background(ColorProvider(Color(0xFFFF7A1A)))
                            .cornerRadius(22.dp)
                            .clickable { /* TODO: Handle play/pause */ }
                    ) {
                        Image(
                            provider = ImageProvider(
                                if (trackInfo.isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
                            ),
                            contentDescription = if (trackInfo.isPlaying) "Pause" else "Play",
                            modifier = GlanceModifier.fillMaxSize()
                        )
                    }
                    
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    
                    // Next button
                    Box(
                        modifier = GlanceModifier
                            .size(36.dp)
                            .background(ColorProvider(Color(0xFF1A1A1B)))
                            .cornerRadius(18.dp)
                            .clickable { /* TODO: Handle next */ }
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_skip_next),
                            contentDescription = "Next",
                            modifier = GlanceModifier.fillMaxSize()
                        )
                    }
                    
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    
                    // EQ button
                    Box(
                        modifier = GlanceModifier
                            .size(36.dp)
                            .background(ColorProvider(Color(0xFF1A1A1B)))
                            .cornerRadius(18.dp)
                            .clickable { /* TODO: Handle EQ */ }
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_equalizer),
                            contentDescription = "Equalizer",
                            modifier = GlanceModifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Track information data class
 */
data class TrackInfo(
    val title: String,
    val artist: String,
    val isPlaying: Boolean,
    val artworkUri: String? = null
)