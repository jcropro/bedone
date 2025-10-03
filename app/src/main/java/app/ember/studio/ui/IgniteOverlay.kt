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
 * Premium Ember splash screen with flame animations and brand reveal
 * 
 * Features:
 * - Ember flame logo with scale animation
 * - Gradient sweep with bloom glow effect
 * - Brand wordmark and tagline reveal
 * - Smooth crossfade to main app
 * - 1100ms total duration with 320ms segments
 */
@Composable
fun IgniteOverlay(
    onFinished: () -> Unit
) {
    // One-shot: run ~1100ms then disappear (Gentle 320ms segments as per Golden Blueprint)
    var running by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(1100)
        running = false
        onFinished()
    }
    if (!running) return

    // Flame glyph scale animation (0.8→1.0 with overshoot)
    val scale by animateFloatAsState(
        targetValue = 1.0f,
        animationSpec = AnimationSpring,
        label = "flameScale"
    )

    // Ember gradient sweep with bloom
    val sweepAlpha by rememberInfiniteTransition(label = "emberSweep").animateFloat(
        initialValue = 0.0f, 
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            tween(MotionReveal, easing = EasingStandard), 
            RepeatMode.Restart
        ),
        label = "sweepAlpha"
    )

    // Text reveal animation
    val textAlpha by animateFloatAsState(
        targetValue = 1.0f,
        animationSpec = AnimationReveal,
        label = "textAlpha"
    )

    // Crossfade to Home (Standard transition timing)
    val crossfadeAlpha by animateFloatAsState(
        targetValue = 1.0f,
        animationSpec = AnimationTransition, 
        label = "crossfade"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EmberInk)
            .alpha(crossfadeAlpha)
    ) {
        // Ember gradient sweep with bloom glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w * 0.5f, h * 0.4f)
            
            withTransform({
                scale(scaleX = scale, scaleY = scale, pivot = center)
            }) {
                // Main flame gradient sweep (35° --amber-900 → --amber-700)
                val flameBrush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF7A1A).copy(alpha = 0.8f), // --amber-900
                        Color(0xFFFF9E3D).copy(alpha = 0.6f), // --amber-700
                        Color(0xFFFFB366).copy(alpha = 0.4f), // --amber-600
                        Color.Transparent
                    ),
                    center = center,
                    radius = w.coerceAtLeast(h) * 0.6f
                )
                
                // Bloom glow effect (15% bloom glow)
                drawCircle(
                    brush = flameBrush,
                    radius = w.coerceAtLeast(h) * 0.5f,
                    center = center,
                    alpha = sweepAlpha
                )
            }
        }

        // Brand reveal: Ember flame + wordmark
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flame glyph with scale animation
            Icon(
                painter = painterResource(id = R.drawable.ic_ember_logo),
                contentDescription = null,
                tint = EmberFlame,
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Brand wordmark with reveal animation
            Text(
                text = "Ember",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = TextStrong,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(textAlpha)
                    .scale(scale)
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
                    .alpha(textAlpha * 0.8f)
                    .scale(scale)
            )
        }

        // Subtle particle effects
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w * 0.5f, h * 0.4f)
            
            // Small sparkle particles
            repeat(8) { i ->
                val angle = (i * 45f) * (Math.PI / 180f)
                val radius = 80f + (i * 10f)
                val x = center.x + (radius * kotlin.math.cos(angle)).toFloat()
                val y = center.y + (radius * kotlin.math.sin(angle)).toFloat()
                
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = sweepAlpha * 0.6f),
                    radius = 3f + (i % 3),
                    center = Offset(x, y)
                )
            }
        }
    }
}
