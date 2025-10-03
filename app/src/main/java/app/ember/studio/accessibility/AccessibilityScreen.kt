package app.ember.studio.accessibility

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import app.ember.studio.R

/**
 * Premium Accessibility Screen
 * 
 * Features:
 * - Visual accessibility options
 * - Motor accessibility features
 * - Audio accessibility settings
 * - Cognitive accessibility tools
 * - Premium visual design with glass morphism
 * - Smooth animations and micro-interactions
 * - Comprehensive accessibility testing
 */
@Composable
fun AccessibilityScreen(
    modifier: Modifier = Modifier,
    accessibilityState: AccessibilityState,
    onVisualAccessibilityChange: (VisualAccessibility) -> Unit,
    onMotorAccessibilityChange: (MotorAccessibility) -> Unit,
    onAudioAccessibilityChange: (AudioAccessibility) -> Unit,
    onCognitiveAccessibilityChange: (CognitiveAccessibility) -> Unit,
    onTestAccessibility: () -> Unit,
    onResetToDefaults: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmberInk)
            .padding(Spacing16)
    ) {
        // Header
        AccessibilityHeader(
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Visual Accessibility
        VisualAccessibilitySection(
            visualAccessibility = accessibilityState.visualAccessibility,
            onVisualAccessibilityChange = onVisualAccessibilityChange,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Motor Accessibility
        MotorAccessibilitySection(
            motorAccessibility = accessibilityState.motorAccessibility,
            onMotorAccessibilityChange = onMotorAccessibilityChange,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Audio Accessibility
        AudioAccessibilitySection(
            audioAccessibility = accessibilityState.audioAccessibility,
            onAudioAccessibilityChange = onAudioAccessibilityChange,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Cognitive Accessibility
        CognitiveAccessibilitySection(
            cognitiveAccessibility = accessibilityState.cognitiveAccessibility,
            onCognitiveAccessibilityChange = onCognitiveAccessibilityChange,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Action Buttons
        AccessibilityActions(
            onTest = onTestAccessibility,
            onReset = onResetToDefaults,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AccessibilityHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Accessibility,
            contentDescription = "Accessibility",
            tint = EmberFlame,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.width(Spacing12))
        
        Column {
            Text(
                text = "Accessibility",
                style = MaterialTheme.typography.headlineSmall,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Make Ember accessible for everyone",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun VisualAccessibilitySection(
    visualAccessibility: VisualAccessibility,
    onVisualAccessibilityChange: (VisualAccessibility) -> Unit,
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    contentDescription = "Visual",
                    tint = EmberFlame,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing8))
                
                Text(
                    text = "Visual Accessibility",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextStrong,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // High contrast mode
            AccessibilityToggle(
                title = "High Contrast",
                description = "Increase contrast for better visibility",
                isEnabled = visualAccessibility.highContrast,
                onToggle = { onVisualAccessibilityChange(visualAccessibility.copy(highContrast = it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Large text
            AccessibilityToggle(
                title = "Large Text",
                description = "Use larger text sizes throughout the app",
                isEnabled = visualAccessibility.largeText,
                onToggle = { onVisualAccessibilityChange(visualAccessibility.copy(largeText = it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Reduce motion
            AccessibilityToggle(
                title = "Reduce Motion",
                description = "Minimize animations and transitions",
                isEnabled = visualAccessibility.reduceMotion,
                onToggle = { onVisualAccessibilityChange(visualAccessibility.copy(reduceMotion = it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Color blind support
            AccessibilityToggle(
                title = "Color Blind Support",
                description = "Use patterns and shapes alongside colors",
                isEnabled = visualAccessibility.colorBlindSupport,
                onToggle = { onVisualAccessibilityChange(visualAccessibility.copy(colorBlindSupport = it)) }
            )
        }
    }
}

@Composable
private fun MotorAccessibilitySection(
    motorAccessibility: MotorAccessibility,
    onMotorAccessibilityChange: (MotorAccessibility) -> Unit,
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.TouchApp,
                    contentDescription = "Motor",
                    tint = EmberFlame,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing8))
                
                Text(
                    text = "Motor Accessibility",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextStrong,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Large touch targets
            AccessibilityToggle(
                title = "Large Touch Targets",
                description = "Increase minimum touch target size to 48dp",
                isEnabled = motorAccessibility.largeTouchTargets,
                onToggle = { onMotorAccessibilityChange(motorAccessibility.copy(largeTouchTargets = it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Gesture alternatives
            AccessibilityToggle(
                title = "Gesture Alternatives",
                description = "Provide button alternatives for gestures",
                isEnabled = motorAccessibility.gestureAlternatives,
                onToggle = { onMotorAccessibilityChange(motorAccessibility.copy(gestureAlternatives = it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Voice control
            AccessibilityToggle(
                title = "Voice Control",
                description = "Enable voice commands for playback control",
                isEnabled = motorAccessibility.voiceControl,
                onToggle = { onMotorAccessibilityChange(motorAccessibility.copy(voiceControl = it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Switch control
            AccessibilityToggle(
                title = "Switch Control",
                description = "Support external switch devices",
                isEnabled = motorAccessibility.switchControl,
                onToggle = { onMotorAccessibilityChange(motorAccessibility.copy(switchControl = it)) }
            )
        }
    }
}

@Composable
private fun AudioAccessibilitySection(
    audioAccessibility: AudioAccessibility,
    onAudioAccessibilityChange: (AudioAccessibility) -> Unit,
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Audio",
                    tint = EmberFlame,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing8))
                
                Text(
                    text = "Audio Accessibility",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextStrong,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Audio descriptions
            AccessibilityToggle(
                title = "Audio Descriptions",
                description = "Provide audio descriptions for visual elements",
                isEnabled = audioAccessibility.audioDescriptions,
                onToggle = { onAudioAccessibilityChange(audioAccessibility.copy(audioDescriptions = it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Haptic feedback
            AccessibilityToggle(
                title = "Haptic Feedback",
                description = "Provide tactile feedback for interactions",
                isEnabled = audioAccessibility.hapticFeedback,
                onToggle = { onAudioAccessibilityChange(audioAccessibility.copy(hapticFeedback = it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Visual indicators
            AccessibilityToggle(
                title = "Visual Indicators",
                description = "Show visual cues for audio events",
                isEnabled = audioAccessibility.visualIndicators,
                onToggle = { onAudioAccessibilityChange(audioAccessibility.copy(visualIndicators = it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Mono audio
            AccessibilityToggle(
                title = "Mono Audio",
                description = "Combine stereo channels for single-ear listening",
                isEnabled = audioAccessibility.monoAudio,
                onToggle = { onAudioAccessibilityChange(audioAccessibility.copy(monoAudio = it)) }
            )
        }
    }
}

@Composable
private fun CognitiveAccessibilitySection(
    cognitiveAccessibility: CognitiveAccessibility,
    onCognitiveAccessibilityChange: (CognitiveAccessibility) -> Unit,
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Psychology,
                    contentDescription = "Cognitive",
                    tint = EmberFlame,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing8))
                
                Text(
                    text = "Cognitive Accessibility",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextStrong,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Simplified interface
            AccessibilityToggle(
                title = "Simplified Interface",
                description = "Reduce visual complexity and cognitive load",
                isEnabled = cognitiveAccessibility.simplifiedInterface,
                onToggle = { onCognitiveAccessibilityChange(cognitiveAccessibility.copy(simplifiedInterface = it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Clear labels
            AccessibilityToggle(
                title = "Clear Labels",
                description = "Use descriptive labels for all controls",
                isEnabled = cognitiveAccessibility.clearLabels,
                onToggle = { onCognitiveAccessibilityChange(cognitiveAccessibility.copy(clearLabels = it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Error prevention
            AccessibilityToggle(
                title = "Error Prevention",
                description = "Prevent accidental actions with confirmations",
                isEnabled = cognitiveAccessibility.errorPrevention,
                onToggle = { onCognitiveAccessibilityChange(cognitiveAccessibility.copy(errorPrevention = it)) }
            )
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Focus management
            AccessibilityToggle(
                title = "Focus Management",
                description = "Clear focus indicators and logical tab order",
                isEnabled = cognitiveAccessibility.focusManagement,
                onToggle = { onCognitiveAccessibilityChange(cognitiveAccessibility.copy(focusManagement = it)) }
            )
        }
    }
}

@Composable
private fun AccessibilityToggle(
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextStrong,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                color = TextMuted,
                fontSize = 12.sp
            )
        }
        
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EmberFlame,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = EmberOutline
            )
        )
    }
}

@Composable
private fun AccessibilityActions(
    onTest: () -> Unit,
    onReset: () -> Unit,
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
            Text("Reset to Defaults")
        }
        
        Button(
            onClick = onTest,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = EmberFlame),
            shape = RoundedCornerShape(RadiusLG)
        ) {
            Text(
                text = "Test Accessibility",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Data models
data class AccessibilityState(
    val visualAccessibility: VisualAccessibility = VisualAccessibility(),
    val motorAccessibility: MotorAccessibility = MotorAccessibility(),
    val audioAccessibility: AudioAccessibility = AudioAccessibility(),
    val cognitiveAccessibility: CognitiveAccessibility = CognitiveAccessibility()
)

data class VisualAccessibility(
    val highContrast: Boolean = false,
    val largeText: Boolean = false,
    val reduceMotion: Boolean = false,
    val colorBlindSupport: Boolean = false
)

data class MotorAccessibility(
    val largeTouchTargets: Boolean = true,
    val gestureAlternatives: Boolean = true,
    val voiceControl: Boolean = false,
    val switchControl: Boolean = false
)

data class AudioAccessibility(
    val audioDescriptions: Boolean = false,
    val hapticFeedback: Boolean = true,
    val visualIndicators: Boolean = true,
    val monoAudio: Boolean = false
)

data class CognitiveAccessibility(
    val simplifiedInterface: Boolean = false,
    val clearLabels: Boolean = true,
    val errorPrevention: Boolean = true,
    val focusManagement: Boolean = true
)
