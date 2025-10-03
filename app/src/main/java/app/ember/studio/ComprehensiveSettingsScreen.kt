package app.ember.studio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.*
import app.ember.core.ui.theme.ThemeUiState
import app.ember.studio.R

/**
 * Comprehensive Settings Screen
 * 
 * Implements Section 10 from the Golden Blueprint:
 * - Theme (Dark/Light/Auto + accent choice)
 * - Library scanning (folders, MP4 linking rules, >20m routing)
 * - Playback (crossfade, skip silence, speed defaults, replaygain)
 * - Video/PIP behavior
 * - EQ global presets; per-song overrides list
 * - Ringtone/Clip Studio settings
 * - Accessibility (font scale tips, haptic toggle)
 * - Privacy (crash logs opt-in only), Export/Import settings
 */
@Composable
fun ComprehensiveSettingsScreen(
    settingsState: PlayerViewModel.SettingsUiState,
    themeState: ThemeUiState,
    onToggleRearmOnBootEnabled: (Boolean) -> Unit,
    onSelectRearmMinMinutes: (Int) -> Unit,
    onToggleSkipSilenceEnabled: (Boolean) -> Unit,
    onSelectCrossfadeMs: (Int) -> Unit,
    onSelectLongformThresholdMinutes: (Int) -> Unit,
    onToggleUseHaptics: (Boolean) -> Unit,
    onRescanLibrary: () -> Unit,
    onOpenScanImport: () -> Unit,
    onSelectThemeOption: (Int) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onToggleDynamicColor: (Boolean) -> Unit,
    onToggleAmoledBlack: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = EmberInk,
        topBar = {
            SettingsTopBar()
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Spacing16),
            verticalArrangement = Arrangement.spacedBy(Spacing24)
        ) {
            // Theme Section
            item {
                SettingsSection(
                    title = "Theme",
                    icon = Icons.Filled.Palette,
                    content = {
                        ThemeSettingsContent(
                            themeState = themeState,
                            onSelectThemeOption = onSelectThemeOption,
                            onToggleDarkTheme = onToggleDarkTheme,
                            onToggleDynamicColor = onToggleDynamicColor,
                            onToggleAmoledBlack = onToggleAmoledBlack
                        )
                    }
                )
            }
            
            // Library Section
            item {
                SettingsSection(
                    title = "Library",
                    icon = Icons.Filled.LibraryMusic,
                    content = {
                        LibrarySettingsContent(
                            settingsState = settingsState,
                            onSelectLongformThresholdMinutes = onSelectLongformThresholdMinutes,
                            onRescanLibrary = onRescanLibrary,
                            onOpenScanImport = onOpenScanImport
                        )
                    }
                )
            }
            
            // Playback Section
            item {
                SettingsSection(
                    title = "Playback",
                    icon = Icons.Filled.PlayArrow,
                    content = {
                        PlaybackSettingsContent(
                            settingsState = settingsState,
                            onToggleSkipSilenceEnabled = onToggleSkipSilenceEnabled,
                            onSelectCrossfadeMs = onSelectCrossfadeMs
                        )
                    }
                )
            }
            
            // Video/PIP Section
            item {
                SettingsSection(
                    title = "Video & PIP",
                    icon = Icons.Filled.VideoLibrary,
                    content = {
                        VideoSettingsContent()
                    }
                )
            }
            
            // Equalizer Section
            item {
                SettingsSection(
                    title = "Equalizer",
                    icon = Icons.Filled.Equalizer,
                    content = {
                        EqualizerSettingsContent()
                    }
                )
            }
            
            // Ringtone Studio Section
            item {
                SettingsSection(
                    title = "Ringtone Studio",
                    icon = Icons.Filled.ContentCut,
                    content = {
                        RingtoneStudioSettingsContent()
                    }
                )
            }
            
            // Accessibility Section
            item {
                SettingsSection(
                    title = "Accessibility",
                    icon = Icons.Filled.Accessibility,
                    content = {
                        AccessibilitySettingsContent(
                            settingsState = settingsState,
                            onToggleUseHaptics = onToggleUseHaptics
                        )
                    }
                )
            }
            
            // Privacy Section
            item {
                SettingsSection(
                    title = "Privacy & Data",
                    icon = Icons.Filled.Security,
                    content = {
                        PrivacySettingsContent()
                    }
                )
            }
            
            // About Section
            item {
                SettingsSection(
                    title = "About",
                    icon = Icons.Filled.Info,
                    content = {
                        AboutSettingsContent()
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(EmberInk)
            .padding(Spacing16)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextStrong
            ),
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = EmberCard
        ),
        shape = RoundedCornerShape(RadiusLG),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing12)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextStrong
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing16))
            content()
        }
    }
}

@Composable
private fun ThemeSettingsContent(
    themeState: ThemeUiState,
    onSelectThemeOption: (Int) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onToggleDynamicColor: (Boolean) -> Unit,
    onToggleAmoledBlack: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing16)
    ) {
        // Theme Mode Selection
        Text(
            text = "Theme Mode",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = TextStrong
            )
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing8)
        ) {
            val themeOptions = listOf("Dark", "Light", "Auto")
            themeOptions.forEachIndexed { index, option ->
                FilterChip(
                    selected = themeState.selectedOptionIndex == index,
                    onClick = { onSelectThemeOption(index) },
                    label = { Text(text = option) },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmberFlame.copy(alpha = 0.2f),
                        selectedLabelColor = EmberFlame
                    )
                )
            }
        }
        
        // Dynamic Color Toggle
        SettingsSwitchItem(
            title = "Dynamic Color",
            subtitle = "Use system accent colors",
            checked = themeState.useDynamicColor,
            onCheckedChange = onToggleDynamicColor
        )
        
        // AMOLED Black Toggle
        SettingsSwitchItem(
            title = "AMOLED Black",
            subtitle = "True black backgrounds for OLED displays",
            checked = themeState.useAmoledBlack,
            onCheckedChange = onToggleAmoledBlack
        )
    }
}

@Composable
private fun LibrarySettingsContent(
    settingsState: PlayerViewModel.SettingsUiState,
    onSelectLongformThresholdMinutes: (Int) -> Unit,
    onRescanLibrary: () -> Unit,
    onOpenScanImport: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing16)
    ) {
        // Long-form Threshold
        Text(
            text = "Long-form Threshold",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = TextStrong
            )
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing8)
        ) {
            settingsState.longformThresholdOptions.forEach { minutes ->
                FilterChip(
                    selected = settingsState.longformThresholdMinutes == minutes,
                    onClick = { onSelectLongformThresholdMinutes(minutes) },
                    label = { Text(text = "${minutes}m") },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmberFlame.copy(alpha = 0.2f),
                        selectedLabelColor = EmberFlame
                    )
                )
            }
        }
        
        // Library Actions
        SettingsActionItem(
            title = "Rescan Library",
            subtitle = "Refresh your music library",
            onClick = onRescanLibrary
        )
        
        SettingsActionItem(
            title = "Manage Folders",
            subtitle = "Choose which folders to scan",
            onClick = onOpenScanImport
        )
    }
}

@Composable
private fun PlaybackSettingsContent(
    settingsState: PlayerViewModel.SettingsUiState,
    onToggleSkipSilenceEnabled: (Boolean) -> Unit,
    onSelectCrossfadeMs: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing16)
    ) {
        // Skip Silence
        SettingsSwitchItem(
            title = "Skip Silence",
            subtitle = "Automatically skip silent parts",
            checked = settingsState.skipSilenceEnabled,
            onCheckedChange = onToggleSkipSilenceEnabled
        )
        
        // Crossfade
        Text(
            text = "Crossfade Duration",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = TextStrong
            )
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing8)
        ) {
            settingsState.crossfadeOptions.forEach { ms ->
                val label = if (ms == 0) "Off" else "${ms / 1000}s"
                FilterChip(
                    selected = settingsState.crossfadeMs == ms,
                    onClick = { onSelectCrossfadeMs(ms) },
                    label = { Text(text = label) },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmberFlame.copy(alpha = 0.2f),
                        selectedLabelColor = EmberFlame
                    )
                )
            }
        }
    }
}

@Composable
private fun VideoSettingsContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing16)
    ) {
        SettingsSwitchItem(
            title = "Auto PIP",
            subtitle = "Enter picture-in-picture automatically",
            checked = true,
            onCheckedChange = { }
        )
        
        SettingsSwitchItem(
            title = "Video Linking",
            subtitle = "Allow linking MP4s to audio tracks",
            checked = true,
            onCheckedChange = { }
        )
    }
}

@Composable
private fun EqualizerSettingsContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing16)
    ) {
        SettingsSwitchItem(
            title = "Global Equalizer",
            subtitle = "Apply EQ to all audio",
            checked = false,
            onCheckedChange = { }
        )
        
        SettingsActionItem(
            title = "Manage Presets",
            subtitle = "Create and edit EQ presets",
            onClick = { }
        )
        
        SettingsActionItem(
            title = "Per-Song Overrides",
            subtitle = "View and manage individual track settings",
            onClick = { }
        )
    }
}

@Composable
private fun RingtoneStudioSettingsContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing16)
    ) {
        SettingsSwitchItem(
            title = "Auto Fade",
            subtitle = "Apply fade effects automatically",
            checked = true,
            onCheckedChange = { }
        )
        
        SettingsActionItem(
            title = "Export Format",
            subtitle = "Choose ringtone format (MP3, AAC)",
            onClick = { }
        )
    }
}

@Composable
private fun AccessibilitySettingsContent(
    settingsState: PlayerViewModel.SettingsUiState,
    onToggleUseHaptics: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing16)
    ) {
        SettingsSwitchItem(
            title = "Haptic Feedback",
            subtitle = "Vibration for interactions",
            checked = settingsState.useHaptics,
            onCheckedChange = onToggleUseHaptics
        )
        
        SettingsActionItem(
            title = "Font Scale",
            subtitle = "Adjust text size for readability",
            onClick = { }
        )
        
        SettingsActionItem(
            title = "TalkBack Support",
            subtitle = "Screen reader compatibility",
            onClick = { }
        )
    }
}

@Composable
private fun PrivacySettingsContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing16)
    ) {
        SettingsSwitchItem(
            title = "Crash Reports",
            subtitle = "Help improve the app (opt-in only)",
            checked = false,
            onCheckedChange = { }
        )
        
        SettingsActionItem(
            title = "Export Settings",
            subtitle = "Backup your preferences",
            onClick = { }
        )
        
        SettingsActionItem(
            title = "Import Settings",
            subtitle = "Restore from backup",
            onClick = { }
        )
    }
}

@Composable
private fun AboutSettingsContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing16)
    ) {
        SettingsActionItem(
            title = "App Version",
            subtitle = "1.0.0 (Build 1)",
            onClick = { }
        )
        
        SettingsActionItem(
            title = "Privacy Policy",
            subtitle = "How we handle your data",
            onClick = { }
        )
        
        SettingsActionItem(
            title = "Open Source Licenses",
            subtitle = "Third-party libraries",
            onClick = { }
        )
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextStrong
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted
                )
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = EmberFlame,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = TextMuted.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun SettingsActionItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextStrong
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted
                )
            )
        }
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}
