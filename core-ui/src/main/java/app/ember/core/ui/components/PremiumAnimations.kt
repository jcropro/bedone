package app.ember.core.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.*
import kotlinx.coroutines.delay

/**
 * Premium Animation Components for Ember Audio Player
 * 
 * Features:
 * - Sophisticated micro-interactions
 * - Flame burst particle effects
 * - Gradient sweep animations
 * - Spring-based morphing
 * - Glass morphism effects
 * - Premium visual feedback
 */

// ============================================================================
// FLAME BURST PARTICLE EFFECT
// ============================================================================

/**
 * Creates a flame burst particle effect on tap
 * Perfect for play/pause buttons and primary actions
 */
@Composable
fun FlameBurstEffect(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    particleCount: Int = 8,
    burstRadius: Float = 60f,
    particleSize: Float = 4f,
    duration: Int = 600,
    content: @Composable () -> Unit
) {
    var triggerBurst by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "flameBurst")
    
    // Trigger burst when isActive changes
    LaunchedEffect(isActive) {
        if (isActive) {
            triggerBurst = true
            delay(duration.toLong())
            triggerBurst = false
        }
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Main content
        content()
        
        // Particle burst overlay
        if (triggerBurst) {
            FlameBurstOverlay(
                particleCount = particleCount,
                burstRadius = burstRadius,
                particleSize = particleSize,
                duration = duration
            )
        }
    }
}

@Composable
private fun FlameBurstOverlay(
    particleCount: Int,
    burstRadius: Float,
    particleSize: Float,
    duration: Int
) {
    val particles = remember { (0 until particleCount).map { (360f / particleCount) * it } }
    
    particles.forEachIndexed { index, angle ->
        val progress = remember { Animatable(0f) }
        
        LaunchedEffect(key1 = Unit) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(duration, easing = FastOutSlowInEasing)
            )
        }
        
        val offsetX = (burstRadius * progress.value * kotlin.math.cos(Math.toRadians(angle.toDouble()))).toFloat()
        val offsetY = (burstRadius * progress.value * kotlin.math.sin(Math.toRadians(angle.toDouble()))).toFloat()
        val alpha = 1f - progress.value
        val scale = 0.5f + (progress.value * 0.7f)
        
        Box(
            modifier = Modifier
                .offset(offsetX.dp, offsetY.dp)
                .size(particleSize.dp)
                .graphicsLayer {
                    this.alpha = alpha
                    this.scaleX = scale
                    this.scaleY = scale
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(EmberFlame, EmberWarm1),
                        radius = particleSize
                    ),
                    shape = CircleShape
                )
        )
    }
}

// ============================================================================
// GRADIENT SWEEP ANIMATION
// ============================================================================

/**
 * Creates a gradient sweep effect for buttons and cards
 * Perfect for like buttons, favorite actions, and premium interactions
 */
@Composable
fun GradientSweepEffect(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    sweepColor: Color = EmberFlame,
    sweepDuration: Int = 800,
    content: @Composable () -> Unit
) {
    var triggerSweep by remember { mutableStateOf(false) }
    val sweepProgress = remember { Animatable(0f) }
    
    // Trigger sweep when isActive changes
    LaunchedEffect(isActive) {
        if (isActive) {
            triggerSweep = true
            sweepProgress.snapTo(0f)
            sweepProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(sweepDuration, easing = FastOutSlowInEasing)
            )
            triggerSweep = false
        }
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Main content
        content()
        
        // Sweep overlay
        if (triggerSweep) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = (1f - sweepProgress.value) * 0.6f
                    }
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                sweepColor.copy(alpha = 0.3f),
                                sweepColor.copy(alpha = 0.6f),
                                sweepColor.copy(alpha = 0.3f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(1f, 1f)
                        )
                    )
            )
        }
    }
}

// ============================================================================
// SPRING MORPHING BUTTON
// ============================================================================

/**
 * A button that morphs with spring animations
 * Perfect for play/pause, shuffle, and repeat buttons
 */
@Composable
fun SpringMorphingButton(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    activeIcon: @Composable () -> Unit,
    inactiveIcon: @Composable () -> Unit,
    onClick: () -> Unit,
    backgroundColor: Color = EmberFlame,
    activeBackgroundColor: Color = EmberWarm1,
    contentColor: Color = Color.White
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )
    
    val background by animateColorAsState(
        targetValue = if (isActive) activeBackgroundColor else backgroundColor,
        animationSpec = tween(MotionTransition, easing = EasingStandard),
        label = "buttonBackground"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isActive) 360f else 0f,
        animationSpec = tween(MotionTransition, easing = FastOutSlowInEasing),
        label = "buttonRotation"
    )
    
    IconButton(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .graphicsLayer {
                rotationZ = rotation
            }
            .shadow(
                elevation = if (isActive) Elevation2 else Elevation1,
                shape = CircleShape,
                ambientColor = backgroundColor.copy(alpha = 0.3f),
                spotColor = backgroundColor.copy(alpha = 0.3f)
            )
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(background, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = isActive,
                transitionSpec = {
                    scaleIn(animationSpec = tween(MotionTap)) + fadeIn() togetherWith
                    scaleOut(animationSpec = tween(MotionTap)) + fadeOut()
                },
                label = "iconContent"
            ) { active ->
                if (active) {
                    activeIcon()
                } else {
                    inactiveIcon()
                }
            }
        }
    }
}

// ============================================================================
// GLASS MORPHISM CARD
// ============================================================================

/**
 * A premium glass morphism card with subtle animations
 * Perfect for music cards, settings panels, and premium UI elements
 */
@Composable
fun GlassMorphismCard(
    modifier: Modifier = Modifier,
    isHovered: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val elevation by animateDpAsState(
        targetValue = if (isHovered) Elevation2 else Elevation1,
        animationSpec = tween(MotionTransition, easing = EasingStandard),
        label = "cardElevation"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.02f else 1.0f,
        animationSpec = tween(MotionTransition, easing = EasingStandard),
        label = "cardScale"
    )
    
    Card(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(RadiusLG),
                ambientColor = EmberFlame.copy(alpha = 0.1f),
                spotColor = EmberFlame.copy(alpha = 0.1f)
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = EmberCard.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(RadiusLG)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.05f)
                        ),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(1f, 1f)
                    )
                )
        ) {
            content()
        }
    }
}

// ============================================================================
// WAVEFORM SCRUB ANIMATION
// ============================================================================

/**
 * Animated waveform scrub effect for progress bars
 * Perfect for audio progress indicators and seek bars
 */
@Composable
fun WaveformScrubEffect(
    modifier: Modifier = Modifier,
    progress: Float,
    isScrubbing: Boolean = false,
    waveformData: List<Float> = emptyList(),
    onProgressChange: (Float) -> Unit = {}
) {
    val density = LocalDensity.current
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(MotionTransition, easing = EasingStandard),
        label = "waveformProgress"
    )
    
    val scrubScale by animateFloatAsState(
        targetValue = if (isScrubbing) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scrubScale"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(
                color = EmberOutline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(RadiusPill)
            )
    ) {
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(EmberFlame, EmberWarm1)
                    ),
                    shape = RoundedCornerShape(RadiusPill)
                )
        )
        
        // Waveform visualization
        if (waveformData.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                waveformData.forEachIndexed { index, amplitude ->
                    val barHeight = (amplitude * 2f).dp.coerceAtLeast(1.dp)
                    val isActive = index < (waveformData.size * animatedProgress).toInt()
                    
                    val animatedHeight by animateDpAsState(
                        targetValue = if (isActive) barHeight else 1.dp,
                        animationSpec = tween(MotionTap, easing = FastOutSlowInEasing),
                        label = "barHeight$index"
                    )
                    
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(animatedHeight)
                            .background(
                                color = if (isActive) EmberFlame else EmberOutline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(1.dp)
                            )
                    )
                }
            }
        }
        
        // Scrub handle (disabled for now - needs parent width measurement)
        /*
        Box(
            modifier = Modifier
                .offset(x = (animatedProgress * 100).dp) // Placeholder offset
                .size(12.dp)
                .scale(scrubScale)
                .background(
                    color = EmberFlame,
                    shape = CircleShape
                )
                .shadow(
                    elevation = Elevation1,
                    shape = CircleShape,
                    ambientColor = EmberFlame.copy(alpha = 0.3f)
                )
        )
        */
    }
}

// ============================================================================
// EQ SLIDER ANIMATION
// ============================================================================

/**
 * Animated EQ slider with spring feedback
 * Perfect for equalizer controls and audio settings
 */
@Composable
fun AnimatedEQSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    label: String = "",
    isActive: Boolean = false
) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "eqValue"
    )
    
    val sliderScale by animateFloatAsState(
        targetValue = if (isActive) 1.05f else 1.0f,
        animationSpec = tween(MotionTransition, easing = EasingStandard),
        label = "sliderScale"
    )
    
    Column(
        modifier = modifier.scale(sliderScale)
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        
        Slider(
            value = animatedValue,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = EmberFlame,
                activeTrackColor = EmberFlame,
                inactiveTrackColor = EmberOutline.copy(alpha = 0.3f)
            ),
            modifier = Modifier.height(48.dp)
        )
        
        Text(
            text = animatedValue.toInt().toString(),
            style = MaterialTheme.typography.bodySmall,
            color = TextStrong,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// ============================================================================
// PREMIUM LOADING INDICATOR
// ============================================================================

/**
 * Premium loading indicator with flame animation
 * Perfect for library scanning and data loading
 */
@Composable
fun PremiumLoadingIndicator(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    message: String = "Loading..."
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(animationSpec = tween(MotionReveal)) + fadeIn(),
        exit = scaleOut(animationSpec = tween(MotionReveal)) + fadeOut()
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                        scaleX = scale
                        scaleY = scale
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(EmberFlame, EmberWarm1),
                            radius = 24f
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}
