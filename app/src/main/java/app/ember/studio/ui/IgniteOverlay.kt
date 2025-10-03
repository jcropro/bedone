package app.ember.studio.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import kotlinx.coroutines.delay
import app.ember.studio.R
import app.ember.core.ui.design.EmberInk
import app.ember.core.ui.design.EmberFlame
import app.ember.core.ui.design.TextStrong
import app.ember.core.ui.design.TextMuted
import app.ember.core.ui.design.AnimationSpring
import app.ember.core.ui.design.AnimationTransition
import app.ember.core.ui.design.AnimationReveal
import app.ember.core.ui.design.EasingStandard
import app.ember.core.ui.design.MotionTransition
import app.ember.core.ui.design.MotionReveal

/**
 * MASTER_BLUEPRINT compliant Warm Ignition splash sequence
 * 
 * Exact specification from 4.1 Splash → Warm Ignition:
 * - Sequence (≤1200ms): icon appears at 80% scale → slow warm-up (color from graphite to ember) → soft bloom → logo moves slightly upward as a radial ember reveal uncovers app background
 * - Audio: no sounds
 * - Reduced motion: static fade from icon to home
 */
@Composable
fun IgniteOverlay(
    onFinished: () -> Unit
) {
    // One-shot: run ≤1200ms then disappear (MASTER_BLUEPRINT specification)
    var running by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(1200) // ≤1200ms as per blueprint
        running = false
        onFinished()
    }
    if (!running) return

    // Phase 1: Icon appears at 80% scale (0ms → 200ms)
    val initialScale by animateFloatAsState(
        targetValue = 0.8f,
        animationSpec = tween(200, easing = EasingStandard),
        label = "initialScale"
    )

    // Phase 2: Slow warm-up - color from graphite to ember (200ms → 600ms)
    val warmUpProgress by animateFloatAsState(
        targetValue = 1.0f,
        animationSpec = tween(400, delayMillis = 200, easing = EasingStandard),
        label = "warmUp"
    )

    // Phase 3: Soft bloom (600ms → 800ms)
    val bloomAlpha by animateFloatAsState(
        targetValue = 1.0f,
        animationSpec = tween(200, delayMillis = 600, easing = EasingStandard),
        label = "bloom"
    )

    // Phase 4: Logo moves slightly upward + radial ember reveal (800ms → 1200ms)
    val logoOffset by animateFloatAsState(
        targetValue = -16f, // Move upward 16dp
        animationSpec = tween(400, delayMillis = 800, easing = EasingStandard),
        label = "logoOffset"
    )

    val radialRevealProgress by animateFloatAsState(
        targetValue = 1.0f,
        animationSpec = tween(400, delayMillis = 800, easing = EasingStandard),
        label = "radialReveal"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EmberInk)
    ) {
        // Radial ember reveal - uncovers app background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w * 0.5f, h * 0.5f)
            
            // Radial reveal mask - starts small and expands
            val revealRadius = (w.coerceAtLeast(h) * 0.8f) * radialRevealProgress
            
            // Create radial gradient from center
            val revealBrush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Transparent,
                    EmberInk.copy(alpha = 0.95f),
                    EmberInk
                ),
                center = center,
                radius = revealRadius
            )
            
            drawCircle(
                brush = revealBrush,
                radius = revealRadius,
                center = center
            )
        }

        // Soft bloom effect during warm-up phase
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w * 0.5f, h * 0.5f)
            
            if (bloomAlpha > 0f) {
                // Soft bloom glow effect
                val bloomBrush = Brush.radialGradient(
                    colors = listOf(
                        EmberFlame.copy(alpha = 0.12f * bloomAlpha),
                        EmberFlame.copy(alpha = 0.06f * bloomAlpha),
                        Color.Transparent
                    ),
                    center = center,
                    radius = w.coerceAtLeast(h) * 0.4f
                )
                
                drawCircle(
                    brush = bloomBrush,
                    radius = w.coerceAtLeast(h) * 0.4f,
                    center = center
                )
            }
        }

        // Brand reveal: Ember flame + wordmark
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = logoOffset.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flame glyph with warm-up color transition
            val iconColor = if (warmUpProgress > 0f) {
                // Transition from graphite (#2B2F36) to ember (#FF7A1A)
                Color(
                    red = 0x2B + (0xFF - 0x2B) * warmUpProgress,
                    green = 0x2F + (0x7A - 0x2F) * warmUpProgress,
                    blue = 0x36 + (0x1A - 0x36) * warmUpProgress
                )
            } else {
                Color(0xFF2B2F36) // Graphite color
            }
            
            Icon(
                painter = painterResource(id = R.drawable.ic_ember_logo),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .size(140.dp)
                    .scale(initialScale)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Brand wordmark with warm-up reveal
            Text(
                text = "Ember",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = if (warmUpProgress > 0.5f) TextStrong else TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(warmUpProgress)
                    .scale(initialScale)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tagline with delayed reveal
            Text(
                text = "Your Audio Player",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(warmUpProgress * 0.8f)
                    .scale(initialScale)
            )
        }
    }
}
