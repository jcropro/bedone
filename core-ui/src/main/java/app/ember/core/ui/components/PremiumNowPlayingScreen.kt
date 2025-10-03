package app.ember.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.AnimationFast
import app.ember.core.ui.design.AnimationOvershoot
import app.ember.core.ui.design.EmberFlame
import app.ember.core.ui.design.RadiusLG
import app.ember.core.ui.design.Spacing16
import app.ember.core.ui.design.Spacing24
import app.ember.core.ui.design.TextMuted
import app.ember.core.ui.design.TextStrong
import app.ember.core.ui.theme.EmberTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Enhanced Now Playing Screen with premium micro-interactions
 * 
 * Features:
 * - Flame burst particles on play/pause
 * - Ember fill gradient sweep for like button
 * - Icon morph animations
 * - Waveform scrub with FFT glow
 * - EQ sliders with spring animations
 */
@Composable
fun PremiumNowPlayingScreen(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    durationMs: Long,
    positionMs: Long,
    isPlaying: Boolean,
    isLiked: Boolean = false,
    waveformData: List<Float> = emptyList(),
    eqBands: List<Float> = listOf(0f, 0f, 0f, 0f, 0f), // 5-band EQ
    onTogglePlayPause: () -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onToggleLike: () -> Unit,
    onEqBandChange: (Int, Float) -> Unit,
    coverArt: @Composable (() -> Unit)? = null
) {
    var showFlameBurst by remember { mutableStateOf(false) }
    var showLikeAnimation by remember { mutableStateOf(false) }
    
    // Flame burst animation trigger
    LaunchedEffect(isPlaying) {
        showFlameBurst = true
        delay(800)
        showFlameBurst = false
    }
    
    // Like animation trigger
    LaunchedEffect(isLiked) {
        if (isLiked) {
            showLikeAnimation = true
            delay(600)
            showLikeAnimation = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(Spacing24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing24)
    ) {
        // Album art with flame burst overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            // Album art
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(RadiusLG))
            ) {
                coverArt?.invoke()
                
                // Gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.4f)
                                )
                            )
                        )
                )
            }
            
            // Flame burst particles overlay
            if (showFlameBurst) {
                FlameBurstOverlay(
                    modifier = Modifier.matchParentSize()
                )
            }
            
            // Song metadata
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(Spacing16)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = TextStrong
                )
                Text(
                    text = artist,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextMuted
                )
            }
        }

        // Enhanced waveform scrubber with premium animations
        WaveformScrubEffect(
            progress = if (durationMs > 0) positionMs.toFloat() / durationMs.toFloat() else 0f,
            isScrubbing = false,
            waveformData = waveformData,
            onProgressChange = { progress ->
                onSeekTo((progress * durationMs).toLong())
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Transport controls with flame burst
        PremiumTransportControls(
            isPlaying = isPlaying,
            isLiked = isLiked,
            onTogglePlayPause = onTogglePlayPause,
            onPlayNext = onPlayNext,
            onPlayPrevious = onPlayPrevious,
            onToggleLike = onToggleLike,
            modifier = Modifier.fillMaxWidth()
        )

        // EQ sliders with premium spring animations
        if (eqBands.isNotEmpty()) {
            PremiumEQSliders(
                bands = eqBands,
                onBandChange = onEqBandChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Flame burst particle system for play/pause interactions
 */
@Composable
private fun FlameBurstOverlay(
    modifier: Modifier = Modifier
) {
    val particles = remember { generateFlameParticles(12) }
    
    Box(modifier = modifier) {
        particles.forEach { particle ->
            FlameParticle(
                particle = particle,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Individual flame particle with physics-based animation
 */
@Composable
private fun FlameParticle(
    particle: FlameParticleData,
    modifier: Modifier = Modifier
) {
    val animatable = remember { Animatable(0f) }
    
    LaunchedEffect(particle) {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = particle.duration,
                easing = FastOutSlowInEasing
            )
        )
    }
    
    Canvas(modifier = modifier) {
        val progress = animatable.value
        val alpha = (1f - progress).coerceAtLeast(0f)
        val scale = 0.5f + (progress * 1.5f)
        
        val x = particle.startX + (particle.velocityX * progress * size.width)
        val y = particle.startY + (particle.velocityY * progress * size.height)
        
        drawCircle(
            color = particle.color.copy(alpha = alpha),
            radius = particle.size * scale,
            center = Offset(x, y)
        )
    }
}

/**
 * Enhanced waveform scrubber with FFT glow effect
 */
@Composable
private fun WaveformScrubber(
    waveformData: List<Float>,
    positionMs: Long,
    durationMs: Long,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (durationMs > 0) positionMs.toFloat() / durationMs.toFloat() else 0f
    
    Box(
        modifier = modifier
            .height(60.dp)
            .pointerInput(Unit) {
                detectDragGestures { _, _ ->
                    // Handle drag gestures for seeking
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerY = size.height / 2
            val barWidth = size.width / waveformData.size.coerceAtLeast(1)
            
            // Draw waveform bars
            waveformData.forEachIndexed { index, amplitude ->
                val x = index * barWidth
                val height = amplitude * size.height * 0.8f
                val isActive = index < (waveformData.size * progress)
                
                val color = if (isActive) EmberFlame else TextMuted.copy(alpha = 0.3f)
                
                drawRect(
                    color = color,
                    topLeft = Offset(x, centerY - height / 2),
                    size = androidx.compose.ui.geometry.Size(barWidth * 0.8f, height)
                )
            }
            
            // Draw FFT glow effect at current position
            val glowX = size.width * progress
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        EmberFlame.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    radius = 100f
                ),
                radius = 100f,
                center = Offset(glowX, centerY)
            )
        }
        
        // Time indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(positionMs),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Text(
                text = formatTime(durationMs),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}

/**
 * Premium transport controls with morphing icons and animations
 */
@Composable
private fun PremiumTransportControls(
    isPlaying: Boolean,
    isLiked: Boolean,
    onTogglePlayPause: () -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit,
    onToggleLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous button
        IconButton(onClick = onPlayPrevious) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Previous",
                tint = TextMuted,
                modifier = Modifier.scale(-1f, 1f) // Flip horizontally
            )
        }
        
        // Play/Pause button with flame burst
        FlameBurstEffect(
            isActive = isPlaying,
            modifier = Modifier
        ) {
            SpringMorphingButton(
                isActive = isPlaying,
                activeIcon = {
                    Text(
                        text = "⏸",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                inactiveIcon = {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White
                    )
                },
                onClick = onTogglePlayPause
            )
        }
        
        // Next button
        IconButton(onClick = onPlayNext) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Next",
                tint = TextMuted
            )
        }
        
        // Like button with gradient sweep animation
        GradientSweepEffect(
            isActive = isLiked,
            modifier = Modifier
        ) {
            EmberLikeButton(
                isLiked = isLiked,
                onClick = onToggleLike
            )
        }
    }
}

/**
 * Like button with ember fill gradient sweep animation
 */
@Composable
private fun EmberLikeButton(
    isLiked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sweepProgress by animateFloatAsState(
        targetValue = if (isLiked) 1f else 0f,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "likeSweep"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isLiked) EmberFlame else TextMuted,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "likeColor"
    )
    
    Box(modifier = modifier) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isLiked) "Unlike" else "Like",
                tint = iconColor
            )
        }
        
        // Ember fill sweep overlay
        if (isLiked) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            EmberFlame.copy(alpha = sweepProgress * 0.3f),
                            Color.Transparent
                        ),
                        radius = radius * sweepProgress
                    ),
                    radius = radius * sweepProgress,
                    center = center
                )
            }
        }
    }
}

/**
 * Premium EQ sliders with spring animations
 */
@Composable
private fun PremiumEQSliders(
    bands: List<Float>,
    onBandChange: (Int, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val bandLabels = listOf("60Hz", "250Hz", "1kHz", "4kHz", "16kHz")
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing16)
    ) {
        Text(
            text = "Equalizer",
            style = MaterialTheme.typography.titleMedium,
            color = TextStrong
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bands.forEachIndexed { index, value ->
                AnimatedEQSlider(
                    value = value,
                    onValueChange = { onBandChange(index, it) },
                    valueRange = -12f..12f,
                    label = bandLabels.getOrElse(index) { "" },
                    isActive = value != 0f,
                    modifier = Modifier.width(40.dp)
                )
            }
        }
    }
}

/**
 * Data class for flame particle properties
 */
private data class FlameParticleData(
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val size: Float,
    val color: Color,
    val duration: Int
)

/**
 * Generate random flame particles for burst effect
 */
private fun generateFlameParticles(count: Int): List<FlameParticleData> {
    return (0 until count).map {
        FlameParticleData(
            startX = 0.5f + (Random.nextFloat() - 0.5f) * 0.3f,
            startY = 0.5f + (Random.nextFloat() - 0.5f) * 0.3f,
            velocityX = (Random.nextFloat() - 0.5f) * 0.4f,
            velocityY = -Random.nextFloat() * 0.3f - 0.1f,
            size = Random.nextFloat() * 8f + 4f,
            color = when (Random.nextInt(3)) {
                0 -> EmberFlame
                1 -> Color(0xFFFF9E3D) // EmberFlameGlow
                else -> Color(0xFFFFD700) // Gold sparkle
            },
            duration = Random.nextInt(400) + 600
        )
    }
}

/**
 * Format time helper function
 */
private fun formatTime(ms: Long): String {
    val seconds = ms / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@Preview(name = "Premium Now Playing - Light")
@Composable
private fun PreviewPremiumNowPlayingLight() {
    EmberTheme(useDarkTheme = false) {
        PremiumNowPlayingScreen(
            title = "Song Title",
            artist = "Artist Name",
            durationMs = 300000,
            positionMs = 120000,
            isPlaying = true,
            isLiked = true,
            waveformData = (0..50).map { Random.nextFloat() }.toList(),
            eqBands = listOf(2f, -1f, 0f, 3f, -2f),
            onTogglePlayPause = {},
            onPlayNext = {},
            onPlayPrevious = {},
            onSeekTo = {},
            onToggleLike = {},
            onEqBandChange = { _, _ -> },
            coverArt = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎵",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
        )
    }
}

@Preview(name = "Premium Now Playing - Dark")
@Composable
private fun PreviewPremiumNowPlayingDark() {
    EmberTheme(useDarkTheme = true) {
        PremiumNowPlayingScreen(
            title = "Song Title",
            artist = "Artist Name",
            durationMs = 300000,
            positionMs = 120000,
            isPlaying = false,
            isLiked = false,
            waveformData = (0..50).map { Random.nextFloat() }.toList(),
            eqBands = listOf(0f, 0f, 0f, 0f, 0f),
            onTogglePlayPause = {},
            onPlayNext = {},
            onPlayPrevious = {},
            onSeekTo = {},
            onToggleLike = {},
            onEqBandChange = { _, _ -> },
            coverArt = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎵",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
        )
    }
}
