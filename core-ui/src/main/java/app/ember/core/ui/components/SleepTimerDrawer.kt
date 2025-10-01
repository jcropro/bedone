package app.ember.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme
import kotlinx.coroutines.delay

/**
 * Enhanced sleep timer drawer with premium visual design
 */
@Composable
fun SleepTimerDrawer(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    isRunning: Boolean,
    remainingTime: String,
    configuredDuration: String,
    quickDurations: List<Int>,
    selectedQuickDuration: Int?,
    customHours: String,
    customMinutes: String,
    fadeEnabled: Boolean,
    endAction: SleepTimerEndAction,
    onDismiss: () -> Unit,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    onQuickDurationSelected: (Int) -> Unit,
    onCustomHoursChanged: (String) -> Unit,
    onCustomMinutesChanged: (String) -> Unit,
    onFadeToggled: (Boolean) -> Unit,
    onEndActionChanged: (SleepTimerEndAction) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 200, easing = LinearEasing)
        ) + fadeOut()
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                SleepTimerHeader(
                    isRunning = isRunning,
                    remainingTime = remainingTime,
                    configuredDuration = configuredDuration,
                    onDismiss = onDismiss
                )
                
                HorizontalDivider()
                
                // Timer controls
                if (isRunning) {
                    SleepTimerRunningControls(
                        remainingTime = remainingTime,
                        onCancel = onCancel
                    )
                } else {
                    SleepTimerSetupControls(
                        quickDurations = quickDurations,
                        selectedQuickDuration = selectedQuickDuration,
                        customHours = customHours,
                        customMinutes = customMinutes,
                        fadeEnabled = fadeEnabled,
                        endAction = endAction,
                        onQuickDurationSelected = onQuickDurationSelected,
                        onCustomHoursChanged = onCustomHoursChanged,
                        onCustomMinutesChanged = onCustomMinutesChanged,
                        onFadeToggled = onFadeToggled,
                        onEndActionChanged = onEndActionChanged,
                        onStart = onStart
                    )
                }
            }
        }
    }
}

@Composable
private fun SleepTimerHeader(
    isRunning: Boolean,
    remainingTime: String,
    configuredDuration: String,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Sleep Timer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (isRunning) {
                Text(
                    text = "Time remaining: $remainingTime",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Set timer to automatically stop playback",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        OutlinedButton(onClick = onDismiss) {
            Text("Done")
        }
    }
}

@Composable
private fun SleepTimerRunningControls(
    remainingTime: String,
    onCancel: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Animated timer display
        SleepTimerDisplay(remainingTime = remainingTime)
        
        // Cancel button
        Button(
            onClick = onCancel,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Cancel Timer")
        }
    }
}

@Composable
private fun SleepTimerDisplay(remainingTime: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "timer-pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⏰",
                style = MaterialTheme.typography.displayLarge
            )
            
            Text(
                text = remainingTime,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = "Time Remaining",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SleepTimerSetupControls(
    quickDurations: List<Int>,
    selectedQuickDuration: Int?,
    customHours: String,
    customMinutes: String,
    fadeEnabled: Boolean,
    endAction: SleepTimerEndAction,
    onQuickDurationSelected: (Int) -> Unit,
    onCustomHoursChanged: (String) -> Unit,
    onCustomMinutesChanged: (String) -> Unit,
    onFadeToggled: (Boolean) -> Unit,
    onEndActionChanged: (SleepTimerEndAction) -> Unit,
    onStart: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Quick duration selection
        QuickDurationSection(
            quickDurations = quickDurations,
            selectedQuickDuration = selectedQuickDuration,
            onQuickDurationSelected = onQuickDurationSelected
        )
        
        // Custom duration
        CustomDurationSection(
            customHours = customHours,
            customMinutes = customMinutes,
            onCustomHoursChanged = onCustomHoursChanged,
            onCustomMinutesChanged = onCustomMinutesChanged
        )
        
        // Fade option
        FadeOptionSection(
            fadeEnabled = fadeEnabled,
            onFadeToggled = onFadeToggled
        )
        
        // End action
        EndActionSection(
            endAction = endAction,
            onEndActionChanged = onEndActionChanged
        )
        
        // Start button
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedQuickDuration != null || (customHours.isNotEmpty() || customMinutes.isNotEmpty())
        ) {
            Text("Start Sleep Timer")
        }
    }
}

@Composable
private fun QuickDurationSection(
    quickDurations: List<Int>,
    selectedQuickDuration: Int?,
    onQuickDurationSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Quick Duration",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickDurations.forEach { minutes ->
                FilterChip(
                    onClick = { onQuickDurationSelected(minutes) },
                    label = { Text("${minutes}m") },
                    selected = selectedQuickDuration == minutes,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CustomDurationSection(
    customHours: String,
    customMinutes: String,
    onCustomHoursChanged: (String) -> Unit,
    onCustomMinutesChanged: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Custom Duration",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = customHours,
                onValueChange = onCustomHoursChanged,
                label = { Text("Hours") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
            
            androidx.compose.material3.OutlinedTextField(
                value = customMinutes,
                onValueChange = onCustomMinutesChanged,
                label = { Text("Minutes") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }
    }
}

@Composable
private fun FadeOptionSection(
    fadeEnabled: Boolean,
    onFadeToggled: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Fade Out",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Gradually reduce volume before stopping",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = fadeEnabled,
                onCheckedChange = onFadeToggled
            )
        }
    }
}

@Composable
private fun EndActionSection(
    endAction: SleepTimerEndAction,
    onEndActionChanged: (SleepTimerEndAction) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "When Timer Ends",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        val endActions = listOf(
            SleepTimerEndAction.PausePlayback to "Pause playback",
            SleepTimerEndAction.StopPlayback to "Stop playback",
            SleepTimerEndAction.StopAfterTrack to "Stop after current track",
            SleepTimerEndAction.StopAfterQueue to "Stop after queue"
        )
        
        endActions.forEach { (action, label) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RadioButton(
                    selected = endAction == action,
                    onClick = { onEndActionChanged(action) }
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

enum class SleepTimerEndAction(val label: String) {
    PausePlayback("Pause playback"),
    StopPlayback("Stop playback"),
    StopAfterTrack("Stop after current track"),
    StopAfterQueue("Stop after queue")
}

@Preview(showBackground = true)
@Composable
private fun SleepTimerDrawerPreview() {
    EmberTheme {
        SleepTimerDrawer(
            isVisible = true,
            isRunning = false,
            remainingTime = "15:30",
            configuredDuration = "30 minutes",
            quickDurations = listOf(5, 10, 15, 30, 45, 60),
            selectedQuickDuration = 30,
            customHours = "",
            customMinutes = "",
            fadeEnabled = true,
            endAction = SleepTimerEndAction.PausePlayback,
            onDismiss = {},
            onStart = {},
            onCancel = {},
            onQuickDurationSelected = {},
            onCustomHoursChanged = {},
            onCustomMinutesChanged = {},
            onFadeToggled = {},
            onEndActionChanged = {}
        )
    }
}
