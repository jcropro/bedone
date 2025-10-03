package app.ember.studio.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.design.*
import app.ember.core.ui.components.GlassMorphismCard
import app.ember.core.ui.components.GradientSweepEffect
import app.ember.studio.R

/**
 * Premium Theme Studio Screen
 * 
 * Features:
 * - Live theme preview
 * - Color customization
 * - Typography adjustments
 * - Motion settings
 * - Accessibility options
 * - Premium visual design with glass morphism
 * - Smooth animations and micro-interactions
 */
@Composable
fun ThemeStudioScreen(
    modifier: Modifier = Modifier,
    themeState: ThemeStudioState,
    onThemeOptionSelected: (Int) -> Unit,
    onDarkThemeToggle: (Boolean) -> Unit,
    onDynamicColorToggle: (Boolean) -> Unit,
    onAmoledBlackToggle: (Boolean) -> Unit,
    onCustomColorChange: (String, Color) -> Unit,
    onTypographyScaleChange: (Float) -> Unit,
    onMotionReductionToggle: (Boolean) -> Unit,
    onResetToDefault: () -> Unit,
    onSaveTheme: () -> Unit,
    onExportTheme: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmberInk)
            .padding(Spacing16)
    ) {
        // Header
        ThemeStudioHeader(
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Live Preview
        ThemePreview(
            themeState = themeState,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Theme Options
        ThemeOptions(
            selectedTheme = themeState.selectedTheme,
            onThemeSelected = onThemeOptionSelected,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Color Customization
        ColorCustomization(
            customColors = themeState.customColors,
            onColorChange = onCustomColorChange,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Typography Settings
        TypographySettings(
            scaleFactor = themeState.typographyScale,
            onScaleChange = onTypographyScaleChange,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Motion Settings
        MotionSettings(
            reduceMotion = themeState.reduceMotion,
            onMotionToggle = onMotionReductionToggle,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Action Buttons
        ThemeStudioActions(
            onReset = onResetToDefault,
            onSave = onSaveTheme,
            onExport = onExportTheme,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ThemeStudioHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Palette,
            contentDescription = "Theme Studio",
            tint = EmberFlame,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.width(Spacing12))
        
        Column {
            Text(
                text = "Theme Studio",
                style = MaterialTheme.typography.headlineSmall,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Customize your visual experience",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun ThemePreview(
    themeState: ThemeStudioState,
    modifier: Modifier = Modifier
) {
    GlassMorphismCard(
        modifier = modifier,
        isHovered = false,
        onClick = null
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Text(
                text = "Live Preview",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Preview components
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing12)
            ) {
                // Sample button
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = EmberFlame),
                    shape = RoundedCornerShape(RadiusMD)
                ) {
                    Text("Sample Button", color = Color.White)
                }
                
                // Sample card with glass morphism
                GlassMorphismCard(
                    modifier = Modifier.weight(1f),
                    isHovered = false,
                    onClick = null
                ) {
                    Text(
                        text = "Sample Card",
                        modifier = Modifier.padding(Spacing8),
                        color = TextStrong
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Sample text
            Text(
                text = "Sample Text",
                style = MaterialTheme.typography.bodyLarge,
                color = TextStrong
            )
            
            Text(
                text = "Sample muted text",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun ThemeOptions(
    selectedTheme: Int,
    onThemeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Theme Options",
            style = MaterialTheme.typography.titleMedium,
            color = TextStrong,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(Spacing12))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing8)
        ) {
            items(ThemeOption.values()) { option ->
                ThemeOptionCard(
                    option = option,
                    isSelected = selectedTheme == option.id,
                    onClick = { onThemeSelected(option.id) }
                )
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    option: ThemeOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) EmberFlame.copy(alpha = 0.1f) else EmberCard,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "backgroundColor"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) EmberFlame else EmberOutline,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "borderColor"
    )
    
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(Spacing12),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Theme color preview
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(option.primaryColor, RoundedCornerShape(RadiusSM))
            )
            
            Spacer(modifier = Modifier.height(Spacing8))
            
            Text(
                text = option.name,
                color = TextStrong,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ColorCustomization(
    customColors: Map<String, Color>,
    onColorChange: (String, Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Text(
                text = "Color Customization",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Color picker grid
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                items(ColorCustomizationOption.values()) { colorOption ->
                    ColorPickerItem(
                        option = colorOption,
                        currentColor = customColors[colorOption.key] ?: colorOption.defaultColor,
                        onColorChange = { onColorChange(colorOption.key, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorPickerItem(
    option: ColorCustomizationOption,
    currentColor: Color,
    onColorChange: (Color) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.name,
                color = TextStrong,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = option.description,
                color = TextMuted,
                fontSize = 12.sp
            )
        }
        
        // Color preview
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(currentColor, RoundedCornerShape(RadiusSM))
                .clickable { /* TODO: Open color picker */ }
        )
    }
}

@Composable
private fun TypographySettings(
    scaleFactor: Float,
    onScaleChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Text(
                text = "Typography",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Scale factor slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Text Scale",
                        color = TextStrong,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "${(scaleFactor * 100).toInt()}%",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing8))
                
                Slider(
                    value = scaleFactor,
                    onValueChange = onScaleChange,
                    valueRange = 0.8f..1.5f,
                    colors = SliderDefaults.colors(
                        thumbColor = EmberFlame,
                        activeTrackColor = EmberFlame,
                        inactiveTrackColor = EmberOutline
                    )
                )
            }
        }
    }
}

@Composable
private fun MotionSettings(
    reduceMotion: Boolean,
    onMotionToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Text(
                text = "Motion",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Reduce motion toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reduce Motion",
                        color = TextStrong,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Minimize animations for accessibility",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
                
                Switch(
                    checked = reduceMotion,
                    onCheckedChange = onMotionToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = EmberFlame,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = EmberOutline
                    )
                )
            }
        }
    }
}

@Composable
private fun ThemeStudioActions(
    onReset: () -> Unit,
    onSave: () -> Unit,
    onExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing8)
    ) {
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
        ) {
            Text("Reset")
        }
        
        OutlinedButton(
            onClick = onExport,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentIce)
        ) {
            Text("Export")
        }
        
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = EmberFlame),
            shape = RoundedCornerShape(RadiusLG)
        ) {
            Text(
                text = "Save Theme",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Data models
data class ThemeStudioState(
    val selectedTheme: Int = 0,
    val customColors: Map<String, Color> = emptyMap(),
    val typographyScale: Float = 1.0f,
    val reduceMotion: Boolean = false
)

data class ThemeOption(
    val id: Int,
    val name: String,
    val primaryColor: Color
) {
    companion object {
        fun values() = listOf(
            ThemeOption(0, "Ember", EmberFlame),
            ThemeOption(1, "Ocean", AccentIce),
            ThemeOption(2, "Forest", Color(0xFF4CAF50)),
            ThemeOption(3, "Sunset", Color(0xFFFF9800)),
            ThemeOption(4, "Midnight", Color(0xFF673AB7))
        )
    }
}

data class ColorCustomizationOption(
    val key: String,
    val name: String,
    val description: String,
    val defaultColor: Color
) {
    companion object {
        fun values() = listOf(
            ColorCustomizationOption("primary", "Primary", "Main accent color", EmberFlame),
            ColorCustomizationOption("secondary", "Secondary", "Secondary accent color", AccentIce),
            ColorCustomizationOption("background", "Background", "Main background color", EmberInk),
            ColorCustomizationOption("surface", "Surface", "Card and surface color", EmberCard)
        )
    }
}
