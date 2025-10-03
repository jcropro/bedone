package app.ember.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.FilterChip
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.design.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Premium Equalizer screen with Flame EQ, presets, and per-song profiles
 */
@Composable
fun EqualizerScreen(
    bands: List<EqBand>,
    selectedPreset: EqPreset,
    isEnabled: Boolean,
    songTitle: String,
    songArtist: String,
    hasCustomProfile: Boolean,
    isProfileActive: Boolean,
    onBandChanged: (Int, Float) -> Unit,
    onPresetSelected: (EqPreset) -> Unit,
    onToggleEnabled: () -> Unit,
    onSaveProfile: () -> Unit,
    onLoadProfile: () -> Unit,
    onDeleteProfile: () -> Unit,
    onShareProfile: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBackgroundAnimation by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        showBackgroundAnimation = true
    }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = EmberInk
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Animated background
            if (showBackgroundAnimation) {
                AnimatedEqBackground()
            }
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing16),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                EqHeader(
                    isEnabled = isEnabled,
                    onToggleEnabled = onToggleEnabled,
                    onDismiss = onDismiss
                )
                
                Spacer(modifier = Modifier.height(Spacing24))
                
                // Main EQ
                FlameEqualizer(
                    bands = bands,
                    onBandChanged = onBandChanged,
                    isEnabled = isEnabled,
                    preset = selectedPreset
                )
                
                Spacer(modifier = Modifier.height(Spacing16))
                
                // Preset selector
                EqPresetSelector(
                    selectedPreset = selectedPreset,
                    onPresetSelected = onPresetSelected,
                    isEnabled = isEnabled
                )
                
                Spacer(modifier = Modifier.height(Spacing16))
                
                // Per-song profile
                PerSongEqProfile(
                    songTitle = songTitle,
                    songArtist = songArtist,
                    hasCustomProfile = hasCustomProfile,
                    isProfileActive = isProfileActive,
                    onSaveProfile = onSaveProfile,
                    onLoadProfile = onLoadProfile,
                    onDeleteProfile = onDeleteProfile,
                    onShareProfile = onShareProfile
                )
                
                Spacer(modifier = Modifier.height(Spacing24))
            }
        }
    }
}

@Composable
private fun EqHeader(
    isEnabled: Boolean,
    onToggleEnabled: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title
        Text(
            text = "Equalizer",
            style = MaterialTheme.typography.headlineMedium,
            color = TextStrong,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Enable/Disable toggle
            IconButton(
                onClick = onToggleEnabled,
                modifier = Modifier
                    .size(48.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = if (isEnabled) "Disable equalizer" else "Enable equalizer"
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = if (isEnabled) EmberFlame else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(48.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Close equalizer"
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedEqBackground() {
    val transition = rememberInfiniteTransition(label = "eqBackground")
    
    // Breathing flame watermark animation (scale 0.95-1.05 @ 6-8% opacity)
    val flameScale = transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = EasingStandard),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameScale"
    )
    
    val flameAlpha = transition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = EasingStandard),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameAlpha"
    )
    
    // Ember particles drift upward (4-6 per second)
    val particleShift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleShift"
    )
    
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        drawEqBackground(
            flameScale = flameScale.value,
            flameAlpha = flameAlpha.value,
            particleShift = particleShift.value
        )
    }
}

private fun DrawScope.drawEqBackground(
    flameScale: Float,
    flameAlpha: Float,
    particleShift: Float
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val maxRadius = size.maxDimension / 2
    
    // Breathing flame watermark (behind sliders)
    val flameSize = 120.dp.toPx() * flameScale
    val flameOffsetX = centerX - flameSize / 2
    val flameOffsetY = centerY - flameSize / 2
    
    // Draw flame watermark with breathing animation
    drawRect(
        color = EmberFlame.copy(alpha = flameAlpha),
        topLeft = Offset(flameOffsetX, flameOffsetY),
        size = androidx.compose.ui.geometry.Size(flameSize, flameSize)
    )
    
    // Draw flame icon in watermark
    val flameIconSize = 48.dp.toPx() * flameScale
    val flameIconX = centerX - flameIconSize / 2
    val flameIconY = centerY - flameIconSize / 2
    
    drawRect(
        color = Color.White.copy(alpha = flameAlpha * 0.5f),
        topLeft = Offset(flameIconX, flameIconY),
        size = androidx.compose.ui.geometry.Size(flameIconSize, flameIconSize)
    )
    
    // Ember particles drift upward (4-6 per second)
    val particleCount = 5
    repeat(particleCount) { i ->
        val particleX = centerX + (i - particleCount / 2) * 80.dp.toPx()
        val particleY = centerY + 200.dp.toPx() - (particleShift * 400.dp.toPx())
        val particleAlpha = (1f - particleShift) * 0.6f
        
        // Random particle size and color variation
        val particleSize = (2f + Random.nextFloat() * 3f).dp.toPx()
        val particleColor = when (Random.nextInt(3)) {
            0 -> EmberFlame.copy(alpha = particleAlpha)
            1 -> EmberFlameGlow.copy(alpha = particleAlpha)
            else -> AccentIce.copy(alpha = particleAlpha)
        }
        
        drawCircle(
            color = particleColor,
            radius = particleSize,
            center = Offset(particleX, particleY)
        )
    }
    
    // Subtle gradient overlay for depth
    val overlayBrush = Brush.radialGradient(
        colors = listOf(
            Color.Transparent,
            EmberInk.copy(alpha = 0.1f)
        ),
        center = Offset(centerX, centerY),
        radius = maxRadius
    )
    
    drawRect(brush = overlayBrush)
}

@Composable
private fun FlameEqualizer(
    bands: List<EqBand>,
    onBandChanged: (Int, Float) -> Unit,
    isEnabled: Boolean,
    preset: EqPreset
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation2)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Flame Equalizer",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // EQ Sliders with molten orb thumbs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                bands.forEachIndexed { index, band ->
                    MoltenEqSlider(
                        band = band,
                        isEnabled = isEnabled,
                        onValueChanged = { value ->
                            onBandChanged(index, value)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MoltenEqSlider(
    band: EqBand,
    isEnabled: Boolean,
    onValueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val sliderHeight = 200.dp
    val thumbSize = 24.dp
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Frequency label
        Text(
            text = "60Hz", // Placeholder frequency
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            fontSize = 10.sp
        )
        
        Spacer(modifier = Modifier.height(Spacing8))
        
        // Slider track
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(sliderHeight)
                .background(
                    color = EmberOutline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(RadiusPill)
                )
        ) {
            // Active track
            val activeHeight = sliderHeight * 0.5f // Placeholder height
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(activeHeight)
                    .background(
                        brush = FlameGradient,
                        shape = RoundedCornerShape(RadiusPill)
                    )
                    .align(Alignment.BottomCenter)
            )
            
            // Molten orb thumb
            MoltenOrbThumb(
                position = activeHeight,
                isEnabled = isEnabled,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing8))
        
        // Gain label
        Text(
            text = "+0dB", // Placeholder gain
            style = MaterialTheme.typography.bodySmall,
            color = if (isEnabled) TextStrong else TextMuted,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun MoltenOrbThumb(
    position: androidx.compose.ui.unit.Dp,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val orbSize = 24.dp
    
    // Heat shimmer animation
    val shimmer by rememberInfiniteTransition(label = "orbShimmer").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    Box(
        modifier = modifier
            .size(orbSize)
            .offset(y = -orbSize / 2)
    ) {
        // Molten orb with heat shimmer
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.minDimension / 2
            
            // Main orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        EmberFlame.copy(alpha = 0.9f),
                        EmberFlameGlow.copy(alpha = 0.7f),
                        AccentIce.copy(alpha = 0.5f)
                    ),
                    center = Offset(centerX, centerY),
                    radius = radius
                ),
                radius = radius,
                center = Offset(centerX, centerY)
            )
            
            // Heat shimmer effect
            val shimmerAlpha = (shimmer * 0.3f).coerceAtMost(0.3f)
            drawCircle(
                color = Color.White.copy(alpha = shimmerAlpha),
                radius = radius * 0.6f,
                center = Offset(centerX, centerY)
            )
        }
    }
}

@Composable
private fun EqPresetSelector(
    selectedPreset: EqPreset,
    onPresetSelected: (EqPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf("Custom", "Normal", "Rock", "Pop", "Jazz", "Classical")
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Presets",
            style = MaterialTheme.typography.titleSmall,
            color = TextStrong,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(Spacing12))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing8)
        ) {
            presets.forEach { preset ->
                FilterChip(
                    onClick = { /* Handle preset selection */ },
                    label = {
                        Text(
                            text = preset,
                            fontSize = 12.sp
                        )
                    },
                    selected = false, // Placeholder selection state
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

