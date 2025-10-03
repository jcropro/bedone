package app.ember.core.ui.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.CubicBezierEasing

/**
 * Ember Design Tokens - Single source of truth for all design values
 * 
 * This file contains the canonical design tokens as specified in EMBER_GOLDEN_BLUEPRINT.md
 * All UI code must reference these tokens - no raw hex colors allowed.
 */

// ============================================================================
// BRAND COLORS (canonical tokens from MASTER_BLUEPRINT)
// ============================================================================

/** Primary (Ember) - exact specification from blueprint */
val EmberFlame = Color(0xFFFF7A1A)

/** Warm ramp colors from blueprint */
val EmberWarm1 = Color(0xFFFF9C4A) // #FF9C4A
val EmberWarm2 = Color(0xFFFF7A1A) // #FF7A1A (same as EmberFlame)
val EmberWarm3 = Color(0xFFD11F1F) // #D11F1F

/** Neutrals (Dark) - exact specifications from blueprint */
val EmberInk = Color(0xFF0B0C0E) // Surface
val EmberElevation = Color(0xFF121418) // Elevation
val EmberOutline = Color(0xFF2B2F36) // Outline

/** Cool accent for secondary affordances */
val AccentCool = Color(0xFF7AE1FF) // AccentCool from blueprint
val AccentIce = Color(0xFF7AD7F0) // Legacy alias for compatibility

/** Card/surface colors for compatibility */
val EmberCard = Color(0xFF16181C) // Card/surface color
val EmberElev1 = Color(0xFF1B1E23) // Elevated surface color
val EmberInk2 = Color(0xFF121316) // Alternative background

/** Flame glow for compatibility */
val EmberFlameGlow = Color(0xFFFF9E3D) // Flame glow - lighter orange for highlights

/** Text colors - exact specifications from blueprint */
val TextStrong = Color(0xFFE9EBEF) // Text from blueprint
val TextMuted = Color(0xFFB6BBC6) // Muted from blueprint
val TextDisabled = Color(0xFF7A828E) // Disabled text - low contrast

// ============================================================================
// SEMANTIC COLORS
// ============================================================================

/** Success state */
val Success = Color(0xFF2BD17E)

/** Warning state */
val Warning = Color(0xFFC83D)

/** Error state */
val Error = Color(0xFF5A5A)

// ============================================================================
// GLASS & GLOW (from MASTER_BLUEPRINT)
// ============================================================================

/** Glass specifications */
val GlassBlur = 8.dp // blur 8-12px
val GlassInnerShadow = Color.Black.copy(alpha = 0.14f) // inner shadow black@14% inset
val GlassBorder = Color.White.copy(alpha = 0.06f) // border white@6% for edge definition

/** Glow specifications */
val GlowBlur = 12.dp // additive orange/amber blur 12-20px
val GlowMaxOpacity = 0.12f // cap opacity at 12% on any control
val GlowTypicalOpacity = 0.06f // 6% typical

/** Shadows - ambient only, no harsh drops */
val ShadowY = 8.dp // Y=6 to 10
val ShadowBlur = 28.dp // blur 24-30

// ============================================================================
// GRADIENTS
// ============================================================================

/** Flame gradient - 35° angle with bloom glow */
val FlameGradient = Brush.linearGradient(
    colors = listOf(EmberFlame, EmberWarm1),
    start = androidx.compose.ui.geometry.Offset(0f, 0f),
    end = androidx.compose.ui.geometry.Offset(1f, 1f)
)

/** Radial glow overlay for active controls */
fun createGlowOverlay(centerColor: Color, radius: Float = 200f) = Brush.radialGradient(
    colors = listOf(
        centerColor.copy(alpha = GlowMaxOpacity),
        centerColor.copy(alpha = GlowTypicalOpacity),
        Color.Transparent
    ),
    radius = radius
)

// ============================================================================
// TYPOGRAPHY & LAYOUT GRID (from MASTER_BLUEPRINT)
// ============================================================================

/** Type scale (Material 3 tuned) */
val TypographyDisplay = 32.sp // Display: 32/38/44 (weight 700) for major headers
val TypographyTitle = 20.sp // Title: 20-22 (600)
val TypographySubtitle = 14.sp // supportive subtitle 14-16 (500) at 70% opacity
val TypographyBody = 14.sp // Body: 14-16 (500)
val TypographyMicro = 12.sp // Micro labels 12-13 (500) for meta info

/** Line heights - generous; do not compress */
val LineHeightBody = 1.4f // 1.3-1.4 for body

/** Grid - 8dp baseline; 4dp only for micro alignments */
val GridBase = 8.dp
val GridMicro = 4.dp

/** Touch targets - ≥48dp min; list items 64-72dp height */
val TouchTargetMin = 48.dp
val ListItemHeight = 64.dp

// ============================================================================
// SPACING SCALE (8-pt grid)
// ============================================================================

val Spacing4 = 4.dp
val Spacing8 = 8.dp
val Spacing12 = 12.dp
val Spacing16 = 16.dp
val Spacing20 = 20.dp
val Spacing24 = 24.dp
val Spacing32 = 32.dp
val Spacing40 = 40.dp

// ============================================================================
// RADII & ELEVATION
// ============================================================================

val RadiusXL = 24.dp
val RadiusLG = 16.dp
val RadiusMD = 12.dp
val RadiusSM = 8.dp
val RadiusPill = 999.dp

val Elevation1 = 3.dp
val Elevation2 = 8.dp
val Elevation3 = 16.dp

// ============================================================================
// MOTION SYSTEM (from MASTER_BLUEPRINT)
// ============================================================================

/** Global durations from blueprint */
val MotionTap = 120 // 120-240ms for taps/transitions
val MotionTransition = 240 // 120-240ms for taps/transitions
val MotionReveal = 320 // 280-380ms for complex reveals (e.g., queue)

/** Curves from blueprint */
val EasingStandard = CubicBezierEasing(0.2f, 0f, 0f, 1f) // Standard: cubic-bezier(0.2, 0, 0, 1)
val EasingDecel = CubicBezierEasing(0f, 0f, 0f, 1f) // Decel: cubic-bezier(0, 0, 0, 1)
val EasingSpring = CubicBezierEasing(0.78f, 0f, 0f, 1f) // Spring: damping 0.78, stiffness 350

/** Motion accessibility */
val MotionReduced = 0.85f // Reduce motion durations by 15%

// ============================================================================
// ANIMATION SPECS (from MASTER_BLUEPRINT)
// ============================================================================

/** Tap animation spec - 120ms */
val AnimationTap = tween<Float>(durationMillis = MotionTap, easing = EasingStandard)

/** Transition animation spec - 240ms */
val AnimationTransition = tween<Float>(durationMillis = MotionTransition, easing = EasingStandard)

/** Reveal animation spec - 320ms */
val AnimationReveal = tween<Float>(durationMillis = MotionReveal, easing = EasingDecel)

/** Spring animation spec for playful interactions */
val AnimationSpring = tween<Float>(durationMillis = MotionTransition, easing = EasingSpring)

/** Legacy animation specs for compatibility */
val AnimationFast = AnimationTap
val AnimationStandard = AnimationTransition
val AnimationGentle = AnimationReveal
val AnimationEmphasis = AnimationReveal
val AnimationOvershoot = AnimationSpring

// ============================================================================
// DIVIDERS & BORDERS
// ============================================================================

/** Hairline divider color on dark backgrounds */
val DividerDark = Color.White.copy(alpha = 0.08f)

/** Hairline divider color on light backgrounds */
val DividerLight = Color.Black.copy(alpha = 0.08f)

/** Border stroke width */
val BorderWidth = 1.dp

// ============================================================================
// ICONOGRAPHY
// ============================================================================

/** Standard icon size */
val IconSize = 24.dp

/** Icon stroke width */
val IconStroke = 1.75.dp

// ============================================================================
// HAPTIC FEEDBACK DURATIONS
// ============================================================================

/** Light tap haptic */
val HapticLight = 10L

/** Medium haptic */
val HapticMedium = 15L

/** Compound haptic (light + medium) */
val HapticCompound = listOf(HapticLight, HapticMedium)

/** Error haptic (short double) */
val HapticError = listOf(HapticLight, HapticLight)
