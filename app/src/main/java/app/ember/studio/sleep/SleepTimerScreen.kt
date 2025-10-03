package app.ember.studio.sleep

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
import app.ember.core.ui.components.GlassMorphismCard
import app.ember.core.ui.components.SpringMorphingButton
import app.ember.core.ui.components.FlameBurstEffect
import app.ember.studio.R
import app.ember.studio.sleep.SleepTimerUiState
import app.ember.studio.sleep.SleepTimerEndAction

/**
 * Premium Sleep Timer Screen
 * 
 * Features:
 * - Visual timer with flame animations
 * - Quick duration presets
 * - Custom time picker
 * - Fade-out options
 * - End action selection
 * - Premium visual design with glass morphism
 * - Smooth animations and micro-interactions
 */
@Composable
fun SleepTimerScreen(
    modifier: Modifier = Modifier,
    sleepTimerState: SleepTimerUiState,
    onQuickDurationSelected: (Int) -> Unit,
    onHoursChanged: (String) -> Unit,
    onMinutesChanged: (String) -> Unit,
    onFadeToggle: (Boolean) -> Unit,
    onEndActionSelected: (SleepTimerEndAction) -> Unit,
    onStartTimer: () -> Unit,
    onCancelTimer: () -> Unit,
    onDismissMessage: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmberInk)
            .padding(Spacing16)
    ) {
        // Header
        SleepTimerHeader(
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Timer Display
        SleepTimerDisplay(
            hours = sleepTimerState.customHoursInput,
            minutes = sleepTimerState.customMinutesInput,
            isActive = sleepTimerState.isRunning,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Quick Duration Presets
        SleepTimerPresets(
            selectedDuration = sleepTimerState.selectedQuickDurationMinutes ?: 0,
            onDurationSelected = onQuickDurationSelected,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Custom Time Picker
        SleepTimerCustomTime(
            hours = sleepTimerState.customHoursInput,
            minutes = sleepTimerState.customMinutesInput,
            onHoursChanged = onHoursChanged,
            onMinutesChanged = onMinutesChanged,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Options
        SleepTimerOptions(
            fadeEnabled = sleepTimerState.fadeEnabled,
            endAction = sleepTimerState.endAction,
            onFadeToggle = onFadeToggle,
            onEndActionSelected = onEndActionSelected,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Action Buttons
        SleepTimerActions(
            isActive = sleepTimerState.isRunning,
            onStart = onStartTimer,
            onCancel = onCancelTimer,
            modifier = Modifier.fillMaxWidth()
        )
    }
    
    // Status Message
    if (sleepTimerState.statusMessage != null) {
        SleepTimerStatusMessage(
            message = sleepTimerState.statusMessage,
            onDismiss = onDismissMessage
        )
    }
}

@Composable
private fun SleepTimerHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Timer,
            contentDescription = "Sleep Timer",
            tint = EmberFlame,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.width(Spacing12))
        
        Column {
            Text(
                text = "Sleep Timer",
                style = MaterialTheme.typography.headlineSmall,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Set a timer to stop playback",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun SleepTimerDisplay(
    hours: String,
    minutes: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.05f else 1.0f,
        animationSpec = tween(MotionTransition, easing = EasingSpring), label = "scale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) EmberFlame.copy(alpha = 0.1f) else EmberCard,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "backgroundColor"
    )
    
    Card(
        modifier = modifier.scale(scale),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing24),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Timer icon
            Icon(
                imageVector = Icons.Filled.Timer,
                contentDescription = "Timer",
                tint = if (isActive) EmberFlame else TextMuted,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Time display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                TimeDisplay(
                    value = hours,
                    label = "Hours",
                    isActive = isActive
                )
                
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineLarge,
                    color = if (isActive) EmberFlame else TextMuted,
                    fontWeight = FontWeight.Bold
                )
                
                TimeDisplay(
                    value = minutes,
                    label = "Minutes",
                    isActive = isActive
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing8))
            
            // Status text
            Text(
                text = if (isActive) "Timer Active" else "Timer Ready",
                color = if (isActive) EmberFlame else TextMuted,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TimeDisplay(
    value: String,
    label: String,
    isActive: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            color = if (isActive) EmberFlame else TextStrong,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
    }
}

@Composable
private fun SleepTimerPresets(
    selectedDuration: Int,
    onDurationSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Quick Presets",
            style = MaterialTheme.typography.titleMedium,
            color = TextStrong,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(Spacing12))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing8)
        ) {
            val presets = listOf(5, 10, 15, 30, 45, 60)
            
            presets.forEach { minutes ->
                PresetButton(
                    minutes = minutes,
                    isSelected = selectedDuration == minutes,
                    onClick = { onDurationSelected(minutes) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PresetButton(
    minutes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) EmberFlame else EmberCard,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "backgroundColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else TextStrong,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "textColor"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(RadiusMD)
    ) {
        Text(
            text = "${minutes}m",
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SleepTimerCustomTime(
    hours: String,
    minutes: String,
    onHoursChanged: (String) -> Unit,
    onMinutesChanged: (String) -> Unit,
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
                text = "Custom Time",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing12)
            ) {
                // Hours input
                OutlinedTextField(
                    value = hours,
                    onValueChange = onHoursChanged,
                    label = { Text("Hours", color = TextMuted) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmberFlame,
                        unfocusedBorderColor = EmberOutline,
                        focusedTextColor = TextStrong,
                        unfocusedTextColor = TextStrong
                    ),
                    singleLine = true
                )
                
                // Minutes input
                OutlinedTextField(
                    value = minutes,
                    onValueChange = onMinutesChanged,
                    label = { Text("Minutes", color = TextMuted) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmberFlame,
                        unfocusedBorderColor = EmberOutline,
                        focusedTextColor = TextStrong,
                        unfocusedTextColor = TextStrong
                    ),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
private fun SleepTimerOptions(
    fadeEnabled: Boolean,
    endAction: SleepTimerEndAction,
    onFadeToggle: (Boolean) -> Unit,
    onEndActionSelected: (SleepTimerEndAction) -> Unit,
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
                text = "Options",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Fade out option
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Fade Out",
                        color = TextStrong,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Gradually reduce volume",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
                
                Switch(
                    checked = fadeEnabled,
                    onCheckedChange = onFadeToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = EmberFlame,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = EmberOutline
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // End action selection
            Text(
                text = "End Action",
                color = TextStrong,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(Spacing8))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing4)
            ) {
                items(SleepTimerEndAction.values()) { action ->
                    EndActionItem(
                        action = action,
                        isSelected = endAction == action,
                        onClick = { onEndActionSelected(action) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EndActionItem(
    action: SleepTimerEndAction,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) EmberFlame.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "backgroundColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) EmberFlame else TextStrong,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "textColor"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor, RoundedCornerShape(RadiusSM))
            .padding(Spacing8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = EmberFlame,
                unselectedColor = TextMuted
            )
        )
        
        Spacer(modifier = Modifier.width(Spacing8))
        
        Column {
            Text(
                text = getEndActionDisplayName(action),
                color = textColor,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = getEndActionDescription(action),
                color = TextMuted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SleepTimerActions(
    isActive: Boolean,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing8)
    ) {
        if (isActive) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Error),
                shape = RoundedCornerShape(RadiusLG)
            ) {
                Icon(
                    imageVector = Icons.Filled.Stop,
                    contentDescription = "Cancel",
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing8))
                
                Text(
                    text = "Cancel Timer",
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Button(
                onClick = onStart,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = EmberFlame),
                shape = RoundedCornerShape(RadiusLG)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Start",
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing8))
                
                Text(
                    text = "Start Timer",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SleepTimerStatusMessage(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing16),
        colors = CardDefaults.cardColors(containerColor = EmberFlame.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation2)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Info",
                tint = EmberFlame,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(Spacing8))
            
            Text(
                text = message,
                color = TextStrong,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Dismiss",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun getEndActionDisplayName(action: SleepTimerEndAction): String = when (action) {
    SleepTimerEndAction.StopPlayback -> "Stop Playback"
    SleepTimerEndAction.PausePlayback -> "Pause Playback"
    SleepTimerEndAction.StopAfterTrack -> "Stop After Track"
    SleepTimerEndAction.StopAfterQueue -> "Stop After Queue"
}

private fun getEndActionDescription(action: SleepTimerEndAction): String = when (action) {
    SleepTimerEndAction.StopPlayback -> "Stop the current track"
    SleepTimerEndAction.PausePlayback -> "Pause the current track"
    SleepTimerEndAction.StopAfterTrack -> "Stop after current track ends"
    SleepTimerEndAction.StopAfterQueue -> "Stop after queue ends"
}
