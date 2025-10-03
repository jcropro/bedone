package app.ember.studio.settings

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
import app.ember.studio.R

/**
 * Detailed Settings Sections
 * 
 * Features:
 * - Theme settings with live preview
 * - Library management options
 * - Playback configuration
 * - Video settings
 * - Equalizer presets
 * - Ringtone studio settings
 * - Accessibility options
 * - Privacy and security
 * - Premium visual design with glass morphism
 * - Smooth animations and micro-interactions
 */

// ============================================================================
// THEME SETTINGS SECTION
// ============================================================================

@Composable
fun ThemeSettingsSection(
    modifier: Modifier = Modifier,
    themeSettings: ThemeSettings,
    onThemeOptionSelected: (Int) -> Unit,
    onDarkThemeToggle: (Boolean) -> Unit,
    onDynamicColorToggle: (Boolean) -> Unit,
    onAmoledBlackToggle: (Boolean) -> Unit,
    onCustomColorChange: (String, Color) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            SettingsSectionHeader(
                title = "Theme",
                icon = Icons.Filled.Palette,
                description = "Customize your visual experience"
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Theme selection
            ThemeSelection(
                selectedTheme = themeSettings.selectedTheme,
                onThemeSelected = onThemeOptionSelected
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            // Theme options
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                item {
                    SettingsToggle(
                        title = "Dark Theme",
                        description = "Use dark theme",
                        isEnabled = themeSettings.useDarkTheme,
                        onToggle = onDarkThemeToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Dynamic Color",
                        description = "Use system accent colors",
                        isEnabled = themeSettings.useDynamicColor,
                        onToggle = onDynamicColorToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "AMOLED Black",
                        description = "Use pure black for OLED displays",
                        isEnabled = themeSettings.useAmoledBlack,
                        onToggle = onAmoledBlackToggle
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeSelection(
    selectedTheme: Int,
    onThemeSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = "Theme Options",
            color = TextStrong,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(Spacing8))
        
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
            .width(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(Spacing8),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(option.primaryColor, RoundedCornerShape(RadiusSM))
            )
            
            Spacer(modifier = Modifier.height(Spacing4))
            
            Text(
                text = option.name,
                color = TextStrong,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================================
// LIBRARY SETTINGS SECTION
// ============================================================================

@Composable
fun LibrarySettingsSection(
    modifier: Modifier = Modifier,
    librarySettings: LibrarySettings,
    onAutoScanToggle: (Boolean) -> Unit,
    onHiddenFilesToggle: (Boolean) -> Unit,
    onVideoFilesToggle: (Boolean) -> Unit,
    onMaxScanDepthChange: (Int) -> Unit,
    onScanIntervalChange: (Int) -> Unit,
    onSmartPlaylistsToggle: (Boolean) -> Unit,
    onMetadataEditingToggle: (Boolean) -> Unit,
    onBackupMetadataToggle: (Boolean) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            SettingsSectionHeader(
                title = "Library",
                icon = Icons.Filled.LibraryMusic,
                description = "Manage your music library"
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                item {
                    SettingsToggle(
                        title = "Auto Scan on Startup",
                        description = "Automatically scan library when app starts",
                        isEnabled = librarySettings.autoScanOnStartup,
                        onToggle = onAutoScanToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Include Hidden Files",
                        description = "Scan files starting with '.'",
                        isEnabled = librarySettings.scanHiddenFiles,
                        onToggle = onHiddenFilesToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Include Video Files",
                        description = "Scan video files for audio tracks",
                        isEnabled = librarySettings.includeVideoFiles,
                        onToggle = onVideoFilesToggle
                    )
                }
                
                item {
                    SettingsSlider(
                        title = "Max Scan Depth",
                        description = "Maximum folder depth to scan",
                        value = librarySettings.maxScanDepth.toFloat(),
                        valueRange = 1f..20f,
                        onValueChange = { onMaxScanDepthChange(it.toInt()) }
                    )
                }
                
                item {
                    SettingsSlider(
                        title = "Scan Interval",
                        description = "Hours between automatic scans",
                        value = librarySettings.scanIntervalHours.toFloat(),
                        valueRange = 1f..168f,
                        onValueChange = { onScanIntervalChange(it.toInt()) }
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Smart Playlists",
                        description = "Enable dynamic playlist creation",
                        isEnabled = librarySettings.enableSmartPlaylists,
                        onToggle = onSmartPlaylistsToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Metadata Editing",
                        description = "Allow editing of track metadata",
                        isEnabled = librarySettings.enableMetadataEditing,
                        onToggle = onMetadataEditingToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Backup Metadata",
                        description = "Automatically backup metadata changes",
                        isEnabled = librarySettings.backupMetadata,
                        onToggle = onBackupMetadataToggle
                    )
                }
            }
        }
    }
}

// ============================================================================
// PLAYBACK SETTINGS SECTION
// ============================================================================

@Composable
fun PlaybackSettingsSection(
    modifier: Modifier = Modifier,
    playbackSettings: PlaybackSettings,
    onCrossfadeToggle: (Boolean) -> Unit,
    onCrossfadeDurationChange: (Int) -> Unit,
    onGaplessToggle: (Boolean) -> Unit,
    onReplayGainToggle: (Boolean) -> Unit,
    onReplayGainModeChange: (ReplayGainMode) -> Unit,
    onSkipSilenceToggle: (Boolean) -> Unit,
    onSkipSilenceThresholdChange: (Int) -> Unit,
    onLongformThresholdChange: (Int) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            SettingsSectionHeader(
                title = "Playback",
                icon = Icons.Filled.PlayArrow,
                description = "Configure audio playback behavior"
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                item {
                    SettingsToggle(
                        title = "Crossfade",
                        description = "Smooth transition between tracks",
                        isEnabled = playbackSettings.crossfadeEnabled,
                        onToggle = onCrossfadeToggle
                    )
                }
                
                item {
                    SettingsSlider(
                        title = "Crossfade Duration",
                        description = "Duration of crossfade in milliseconds",
                        value = playbackSettings.crossfadeDuration.toFloat(),
                        valueRange = 100f..5000f,
                        onValueChange = { onCrossfadeDurationChange(it.toInt()) }
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Gapless Playback",
                        description = "Play tracks without gaps",
                        isEnabled = playbackSettings.gaplessEnabled,
                        onToggle = onGaplessToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "ReplayGain",
                        description = "Normalize volume levels",
                        isEnabled = playbackSettings.replayGainEnabled,
                        onToggle = onReplayGainToggle
                    )
                }
                
                item {
                    SettingsDropdown(
                        title = "ReplayGain Mode",
                        description = "How to apply ReplayGain",
                        selectedValue = playbackSettings.replayGainMode,
                        options = ReplayGainMode.values(),
                        onValueChange = onReplayGainModeChange
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Skip Silence",
                        description = "Automatically skip silent parts",
                        isEnabled = playbackSettings.skipSilenceEnabled,
                        onToggle = onSkipSilenceToggle
                    )
                }
                
                item {
                    SettingsSlider(
                        title = "Skip Silence Threshold",
                        description = "Silence threshold in decibels",
                        value = playbackSettings.skipSilenceThreshold.toFloat(),
                        valueRange = -60f..-10f,
                        onValueChange = { onSkipSilenceThresholdChange(it.toInt()) }
                    )
                }
                
                item {
                    SettingsSlider(
                        title = "Longform Threshold",
                        description = "Minutes to consider audio as longform",
                        value = playbackSettings.longformThresholdMinutes.toFloat(),
                        valueRange = 5f..120f,
                        onValueChange = { onLongformThresholdChange(it.toInt()) }
                    )
                }
            }
        }
    }
}

// ============================================================================
// VIDEO SETTINGS SECTION
// ============================================================================

@Composable
fun VideoSettingsSection(
    modifier: Modifier = Modifier,
    videoSettings: VideoSettings,
    onVideoPlaybackToggle: (Boolean) -> Unit,
    onVideoQualityChange: (VideoQuality) -> Unit,
    onVideoCodecChange: (VideoCodec) -> Unit,
    onHardwareAccelerationToggle: (Boolean) -> Unit,
    onSubtitleToggle: (Boolean) -> Unit,
    onSubtitleSizeChange: (Int) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            SettingsSectionHeader(
                title = "Video",
                icon = Icons.Filled.VideoLibrary,
                description = "Configure video playback options"
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                item {
                    SettingsToggle(
                        title = "Video Playback",
                        description = "Enable video file playback",
                        isEnabled = videoSettings.videoPlaybackEnabled,
                        onToggle = onVideoPlaybackToggle
                    )
                }
                
                item {
                    SettingsDropdown(
                        title = "Video Quality",
                        description = "Default video quality",
                        selectedValue = videoSettings.defaultQuality,
                        options = VideoQuality.values(),
                        onValueChange = onVideoQualityChange
                    )
                }
                
                item {
                    SettingsDropdown(
                        title = "Video Codec",
                        description = "Preferred video codec",
                        selectedValue = videoSettings.preferredCodec,
                        options = VideoCodec.values(),
                        onValueChange = onVideoCodecChange
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Hardware Acceleration",
                        description = "Use hardware video decoding",
                        isEnabled = videoSettings.hardwareAcceleration,
                        onToggle = onHardwareAccelerationToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Subtitles",
                        description = "Show subtitles when available",
                        isEnabled = videoSettings.subtitlesEnabled,
                        onToggle = onSubtitleToggle
                    )
                }
                
                item {
                    SettingsSlider(
                        title = "Subtitle Size",
                        description = "Subtitle text size",
                        value = videoSettings.subtitleSize.toFloat(),
                        valueRange = 8f..24f,
                        onValueChange = { onSubtitleSizeChange(it.toInt()) }
                    )
                }
            }
        }
    }
}

// ============================================================================
// EQUALIZER SETTINGS SECTION
// ============================================================================

@Composable
fun EqualizerSettingsSection(
    modifier: Modifier = Modifier,
    equalizerSettings: EqualizerSettings,
    onEqualizerToggle: (Boolean) -> Unit,
    onPresetChange: (EqualizerPreset) -> Unit,
    onBandLevelChange: (Int, Int) -> Unit,
    onBassBoostChange: (Int) -> Unit,
    onVirtualizerChange: (Int) -> Unit,
    onReverbChange: (ReverbPreset) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            SettingsSectionHeader(
                title = "Equalizer",
                icon = Icons.Filled.Equalizer,
                description = "Configure audio equalizer settings"
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                item {
                    SettingsToggle(
                        title = "Equalizer",
                        description = "Enable audio equalizer",
                        isEnabled = equalizerSettings.equalizerEnabled,
                        onToggle = onEqualizerToggle
                    )
                }
                
                item {
                    SettingsDropdown(
                        title = "Preset",
                        description = "Equalizer preset",
                        selectedValue = equalizerSettings.selectedPreset,
                        options = EqualizerPreset.values(),
                        onValueChange = onPresetChange
                    )
                }
                
                item {
                    SettingsSlider(
                        title = "Bass Boost",
                        description = "Bass enhancement level",
                        value = equalizerSettings.bassBoost.toFloat(),
                        valueRange = 0f..100f,
                        onValueChange = { onBassBoostChange(it.toInt()) }
                    )
                }
                
                item {
                    SettingsSlider(
                        title = "Virtualizer",
                        description = "3D audio effect level",
                        value = equalizerSettings.virtualizer.toFloat(),
                        valueRange = 0f..100f,
                        onValueChange = { onVirtualizerChange(it.toInt()) }
                    )
                }
                
                item {
                    SettingsDropdown(
                        title = "Reverb",
                        description = "Reverb effect preset",
                        selectedValue = equalizerSettings.reverbPreset,
                        options = ReverbPreset.values(),
                        onValueChange = onReverbChange
                    )
                }
            }
        }
    }
}

// ============================================================================
// RINGTONE STUDIO SETTINGS SECTION
// ============================================================================

@Composable
fun RingtoneStudioSettingsSection(
    modifier: Modifier = Modifier,
    ringtoneSettings: RingtoneStudioSettings,
    onRingtoneCreationToggle: (Boolean) -> Unit,
    onFadeInToggle: (Boolean) -> Unit,
    onFadeOutToggle: (Boolean) -> Unit,
    onFadeDurationChange: (Int) -> Unit,
    onMaxLengthChange: (Int) -> Unit,
    onQualityChange: (RingtoneQuality) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            SettingsSectionHeader(
                title = "Ringtone Studio",
                icon = Icons.Filled.MusicNote,
                description = "Configure ringtone creation settings"
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                item {
                    SettingsToggle(
                        title = "Ringtone Creation",
                        description = "Allow creating ringtones from tracks",
                        isEnabled = ringtoneSettings.ringtoneCreationEnabled,
                        onToggle = onRingtoneCreationToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Fade In",
                        description = "Add fade-in effect to ringtones",
                        isEnabled = ringtoneSettings.fadeInEnabled,
                        onToggle = onFadeInToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Fade Out",
                        description = "Add fade-out effect to ringtones",
                        isEnabled = ringtoneSettings.fadeOutEnabled,
                        onToggle = onFadeOutToggle
                    )
                }
                
                item {
                    SettingsSlider(
                        title = "Fade Duration",
                        description = "Fade effect duration in milliseconds",
                        value = ringtoneSettings.fadeDuration.toFloat(),
                        valueRange = 100f..2000f,
                        onValueChange = { onFadeDurationChange(it.toInt()) }
                    )
                }
                
                item {
                    SettingsSlider(
                        title = "Max Length",
                        description = "Maximum ringtone length in seconds",
                        value = ringtoneSettings.maxLengthSeconds.toFloat(),
                        valueRange = 10f..60f,
                        onValueChange = { onMaxLengthChange(it.toInt()) }
                    )
                }
                
                item {
                    SettingsDropdown(
                        title = "Quality",
                        description = "Ringtone audio quality",
                        selectedValue = ringtoneSettings.quality,
                        options = RingtoneQuality.values(),
                        onValueChange = onQualityChange
                    )
                }
            }
        }
    }
}

// ============================================================================
// ACCESSIBILITY SETTINGS SECTION
// ============================================================================

@Composable
fun AccessibilitySettingsSection(
    modifier: Modifier = Modifier,
    accessibilitySettings: AccessibilitySettings,
    onHighContrastToggle: (Boolean) -> Unit,
    onLargeTextToggle: (Boolean) -> Unit,
    onReduceMotionToggle: (Boolean) -> Unit,
    onLargeTouchTargetsToggle: (Boolean) -> Unit,
    onVoiceControlToggle: (Boolean) -> Unit,
    onHapticFeedbackToggle: (Boolean) -> Unit,
    onAudioDescriptionsToggle: (Boolean) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            SettingsSectionHeader(
                title = "Accessibility",
                icon = Icons.Filled.Accessibility,
                description = "Make Ember accessible for everyone"
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                item {
                    SettingsToggle(
                        title = "High Contrast",
                        description = "Increase contrast for better visibility",
                        isEnabled = accessibilitySettings.highContrast,
                        onToggle = onHighContrastToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Large Text",
                        description = "Use larger text sizes",
                        isEnabled = accessibilitySettings.largeText,
                        onToggle = onLargeTextToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Reduce Motion",
                        description = "Minimize animations and transitions",
                        isEnabled = accessibilitySettings.reduceMotion,
                        onToggle = onReduceMotionToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Large Touch Targets",
                        description = "Increase minimum touch target size",
                        isEnabled = accessibilitySettings.largeTouchTargets,
                        onToggle = onLargeTouchTargetsToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Voice Control",
                        description = "Enable voice commands",
                        isEnabled = accessibilitySettings.voiceControl,
                        onToggle = onVoiceControlToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Haptic Feedback",
                        description = "Provide tactile feedback",
                        isEnabled = accessibilitySettings.hapticFeedback,
                        onToggle = onHapticFeedbackToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Audio Descriptions",
                        description = "Provide audio descriptions for visual elements",
                        isEnabled = accessibilitySettings.audioDescriptions,
                        onToggle = onAudioDescriptionsToggle
                    )
                }
            }
        }
    }
}

// ============================================================================
// PRIVACY SETTINGS SECTION
// ============================================================================

@Composable
fun PrivacySettingsSection(
    modifier: Modifier = Modifier,
    privacySettings: PrivacySettings,
    onAnalyticsToggle: (Boolean) -> Unit,
    onCrashReportingToggle: (Boolean) -> Unit,
    onUsageDataToggle: (Boolean) -> Unit,
    onLocationDataToggle: (Boolean) -> Unit,
    onDataRetentionChange: (DataRetentionPeriod) -> Unit,
    onClearData: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            SettingsSectionHeader(
                title = "Privacy",
                icon = Icons.Filled.PrivacyTip,
                description = "Control your data and privacy"
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                item {
                    SettingsToggle(
                        title = "Analytics",
                        description = "Help improve Ember with usage analytics",
                        isEnabled = privacySettings.analyticsEnabled,
                        onToggle = onAnalyticsToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Crash Reporting",
                        description = "Send crash reports to help fix bugs",
                        isEnabled = privacySettings.crashReportingEnabled,
                        onToggle = onCrashReportingToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Usage Data",
                        description = "Share anonymous usage statistics",
                        isEnabled = privacySettings.usageDataEnabled,
                        onToggle = onUsageDataToggle
                    )
                }
                
                item {
                    SettingsToggle(
                        title = "Location Data",
                        description = "Use location for local music discovery",
                        isEnabled = privacySettings.locationDataEnabled,
                        onToggle = onLocationDataToggle
                    )
                }
                
                item {
                    SettingsDropdown(
                        title = "Data Retention",
                        description = "How long to keep your data",
                        selectedValue = privacySettings.dataRetentionPeriod,
                        options = DataRetentionPeriod.values(),
                        onValueChange = onDataRetentionChange
                    )
                }
                
                item {
                    SettingsButton(
                        title = "Clear All Data",
                        description = "Remove all stored data and reset settings",
                        onClick = onClearData,
                        buttonText = "Clear Data",
                        isDestructive = true
                    )
                }
            }
        }
    }
}

// ============================================================================
// SHARED COMPONENTS
// ============================================================================

@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: ImageVector,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = EmberFlame,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(Spacing8))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun SettingsToggle(
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
private fun SettingsSlider(
    title: String,
    description: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
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
            
            Text(
                text = value.toInt().toString(),
                color = TextMuted,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing8))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = EmberFlame,
                activeTrackColor = EmberFlame,
                inactiveTrackColor = EmberOutline
            )
        )
    }
}

@Composable
private fun <T> SettingsDropdown(
    title: String,
    description: String,
    selectedValue: T,
    options: Array<T>,
    onValueChange: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
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
        
        Spacer(modifier = Modifier.height(Spacing8))
        
        OutlinedTextField(
            value = selectedValue.toString(),
            onValueChange = { },
            readOnly = true,
            label = { Text("Select Option") },
            trailingIcon = { 
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmberFlame,
                unfocusedBorderColor = EmberOutline,
                focusedTextColor = TextStrong,
                unfocusedTextColor = TextStrong
            )
        )
    }
}

@Composable
private fun SettingsButton(
    title: String,
    description: String,
    onClick: () -> Unit,
    buttonText: String,
    isDestructive: Boolean = false
) {
    Column {
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
        
        Spacer(modifier = Modifier.height(Spacing8))
        
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDestructive) Error else EmberFlame
            ),
            shape = RoundedCornerShape(RadiusMD)
        ) {
            Text(buttonText)
        }
    }
}

// ============================================================================
// DATA MODELS
// ============================================================================

data class ThemeSettings(
    val selectedTheme: Int = 0,
    val useDarkTheme: Boolean = true,
    val useDynamicColor: Boolean = false,
    val useAmoledBlack: Boolean = false,
    val customColors: Map<String, Color> = emptyMap()
)

data class LibrarySettings(
    val autoScanOnStartup: Boolean = true,
    val scanHiddenFiles: Boolean = false,
    val includeVideoFiles: Boolean = true,
    val maxScanDepth: Int = 10,
    val scanIntervalHours: Int = 24,
    val enableSmartPlaylists: Boolean = true,
    val enableMetadataEditing: Boolean = true,
    val backupMetadata: Boolean = true
)

data class PlaybackSettings(
    val crossfadeEnabled: Boolean = false,
    val crossfadeDuration: Int = 2000,
    val gaplessEnabled: Boolean = true,
    val replayGainEnabled: Boolean = false,
    val replayGainMode: ReplayGainMode = ReplayGainMode.Album,
    val skipSilenceEnabled: Boolean = false,
    val skipSilenceThreshold: Int = -40,
    val longformThresholdMinutes: Int = 20
)

data class VideoSettings(
    val videoPlaybackEnabled: Boolean = true,
    val defaultQuality: VideoQuality = VideoQuality.Auto,
    val preferredCodec: VideoCodec = VideoCodec.Auto,
    val hardwareAcceleration: Boolean = true,
    val subtitlesEnabled: Boolean = true,
    val subtitleSize: Int = 14
)

data class EqualizerSettings(
    val equalizerEnabled: Boolean = false,
    val selectedPreset: EqualizerPreset = EqualizerPreset.Flat,
    val bassBoost: Int = 0,
    val virtualizer: Int = 0,
    val reverbPreset: ReverbPreset = ReverbPreset.None
)

data class RingtoneStudioSettings(
    val ringtoneCreationEnabled: Boolean = true,
    val fadeInEnabled: Boolean = true,
    val fadeOutEnabled: Boolean = true,
    val fadeDuration: Int = 500,
    val maxLengthSeconds: Int = 30,
    val quality: RingtoneQuality = RingtoneQuality.High
)

data class AccessibilitySettings(
    val highContrast: Boolean = false,
    val largeText: Boolean = false,
    val reduceMotion: Boolean = false,
    val largeTouchTargets: Boolean = true,
    val voiceControl: Boolean = false,
    val hapticFeedback: Boolean = true,
    val audioDescriptions: Boolean = false
)

data class PrivacySettings(
    val analyticsEnabled: Boolean = true,
    val crashReportingEnabled: Boolean = true,
    val usageDataEnabled: Boolean = true,
    val locationDataEnabled: Boolean = false,
    val dataRetentionPeriod: DataRetentionPeriod = DataRetentionPeriod.OneYear
)

// Enums
enum class ReplayGainMode { Album, Track, None }
enum class VideoQuality { Auto, Low, Medium, High, Ultra }
enum class VideoCodec { Auto, H264, H265, VP9, AV1 }
enum class EqualizerPreset { Flat, Pop, Rock, Jazz, Classical, Custom }
enum class ReverbPreset { None, Room, Hall, Cathedral, Arena }
enum class RingtoneQuality { Low, Medium, High }
enum class DataRetentionPeriod { OneMonth, ThreeMonths, SixMonths, OneYear, Forever }

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
