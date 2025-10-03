package app.ember.core.ui.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring

/**
 * Ember Design Tokens - Single source of truth for all design values
 * 
 * This file contains the canonical design tokens as specified in MASTER_BLUEPRINT.md
 * All UI code must reference these tokens - no raw hex colors allowed.
 * 
 * CRITICAL: These tokens must match MASTER_BLUEPRINT.md EXACTLY
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

/** Focus ring - gold rim from blueprint */
val FocusRing = Color(0xFFFFD27A).copy(alpha = 0.26f) // #FFD27A @ 26% opacity

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
// TYPOGRAPHY SYSTEM (MASTER_BLUEPRINT 1.3)
// ============================================================================

/** Display: 32/38/44 (weight 700) for major headers (e.g., "Songs") */
val TypographyDisplayLarge = 44.sp
val TypographyDisplayMedium = 38.sp
val TypographyDisplaySmall = 32.sp
val TypographyDisplayWeight = FontWeight.Bold // 700

/** Title: 20–22 (600), supportive subtitle 14–16 (500) at 70% opacity */
val TypographyTitleLarge = 22.sp
val TypographyTitleMedium = 20.sp
val TypographyTitleWeight = FontWeight.SemiBold // 600
val TypographySubtitleLarge = 16.sp
val TypographySubtitleMedium = 14.sp
val TypographySubtitleWeight = FontWeight.Medium // 500
val TypographySubtitleOpacity = 0.7f // 70% opacity

/** Body: 14–16 (500); Micro labels 12–13 (500) for meta info */
val TypographyBodyLarge = 16.sp
val TypographyBodyMedium = 14.sp
val TypographyBodyWeight = FontWeight.Medium // 500
val TypographyLabelLarge = 13.sp
val TypographyLabelMedium = 12.sp
val TypographyLabelWeight = FontWeight.Medium // 500

/** Line heights: generous; do not compress (e.g., 1.3–1.4 for body) */
val TypographyLineHeightDisplay = 1.2f
val TypographyLineHeightTitle = 1.3f
val TypographyLineHeightBody = 1.4f
val TypographyLineHeightLabel = 1.3f

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

// ============================================================================
// THEME LIBRARY (11 expertly curated themes from MASTER_BLUEPRINT)
// ============================================================================

/** Theme 1: Everyday Dark (Default) */
object EverydayDark {
    val Surface = Color(0xFF0B0C0E)
    val Elevation = Color(0xFF121418)
    val Text = Color(0xFFE9EBEF)
    val Muted = Color(0xFFB6BBC6)
    val Outline = Color(0xFF2B2F36)
    val Primary = Color(0xFFFF7A1A)
    val AccentCool = Color(0xFF7AE1FF)
    val AppBarGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0F1013), Color(0xFF191B20)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1f, 1f)
    )
}

/** Theme 2: Inferno Core */
object InfernoCore {
    val Surface = Color(0xFF0D0E12)
    val Elevation = Color(0xFF13151A)
    val Text = Color(0xFFE8EAEE)
    val Primary = Color(0xFFFF7A1A)
    val SoftAmber = Color(0xFFFF9C4A)
    val Neutral = Color(0xFF2C2F36)
    val PanelGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0F1013), Color(0xFF1A1C21)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1f, 1f)
    )
}

/** Theme 3: Obsidian Ember (Pro OLED) */
object ObsidianEmber {
    val Surface = Color(0xFF05070A)
    val CardGlass = Color(0x0FFFFFFF) // rgba(255,255,255,0.06)
    val Text = Color(0xFFF2F5FA)
    val AccentCool = Color(0xFF6FD7FF)
    val Primary = Color(0xFFFF7A1A)
    val BackgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF070A0F), Color(0xFF0E1117)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1f, 1f)
    )
}

/** Theme 4: Aurora Flame */
object AuroraFlame {
    val Surface = Color(0xFF0A0F12)
    val Text = Color(0xFFE8EEF2)
    val Teal = Color(0xFF15B8A9)
    val Ember = Color(0xFFFF7A1A)
    val Muted = Color(0xFF263139)
    val Gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0E222B), Color(0xFF102E38)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1f, 1f)
    )
}

/** Theme 5: Velvet Magma */
object VelvetMagma {
    val Surface = Color(0xFF120F17)
    val Elevation = Color(0xFF191425)
    val Text = Color(0xFFECE6F3)
    val Plum = Color(0xFF8E41C7)
    val Ember = Color(0xFFFF7A1A)
    val SoftLilac = Color(0xFFE8D3F9)
    val Gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1B1226), Color(0xFF2A1840)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1f, 1f)
    )
}

/** Theme 6: Solar Flare */
object SolarFlare {
    val Surface = Color(0xFF101113)
    val Elevation = Color(0xFF15171B)
    val Text = Color(0xFFF3EEE6)
    val Amber = Color(0xFFEFA64A)
    val Ember = Color(0xFFFF7A1A)
    val Metal = Color(0xFFFFD27A)
    val Gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0F0F12), Color(0xFF18191C)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1f, 1f)
    )
}

/** Theme 7: Midnight Oil */
object MidnightOil {
    val Surface = Color(0xFF0A0E18)
    val Elevation = Color(0xFF0F1424)
    val Text = Color(0xFFE6EAF6)
    val Indigo = Color(0xFF335AE1)
    val Ember = Color(0xFFFF7A1A)
    val Gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0B1021), Color(0xFF111732)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1f, 1f)
    )
}

/** Theme 8: Glacier Ember */
object GlacierEmber {
    val Surface = Color(0xFF0C1115)
    val Elevation = Color(0xFF141A20)
    val Text = Color(0xFFE9F3FA)
    val Ice = Color(0xFF6FBFEA)
    val Ember = Color(0xFFFF7A1A)
    val Gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0F1B22), Color(0xFF152531)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1f, 1f)
    )
}

/** Theme 9: Forest Ember */
object ForestEmber {
    val Surface = Color(0xFF0D1413)
    val Elevation = Color(0xFF121A19)
    val Text = Color(0xFFE8F1ED)
    val Emerald = Color(0xFF13AB7C)
    val Ember = Color(0xFFFF7A1A)
    val Copper = Color(0xFFC78B4A)
    val Gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0F1716), Color(0xFF14201D)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1f, 1f)
    )
}

/** Theme 10: Rose Quartz x Gold */
object RoseQuartzGold {
    val Surface = Color(0xFF121014)
    val Elevation = Color(0xFF18151B)
    val Text = Color(0xFFF9F1F4)
    val Rose = Color(0xFFDC7AA1)
    val Ember = Color(0xFFFF7A1A)
    val Petal = Color(0xFFFFE1EB)
    val Gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF171319), Color(0xFF201722)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1f, 1f)
    )
}

/** Theme 11: Cyber Ember */
object CyberEmber {
    val Surface = Color(0xFF0A0A12)
    val Elevation = Color(0xFF121527)
    val Text = Color(0xFFE8FBFE)
    val Aqua = Color(0xFF00CFE0)
    val Ember = Color(0xFFFF7A1A)
    val Gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0A0A12), Color(0xFF121527)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1f, 1f)
    )
}
