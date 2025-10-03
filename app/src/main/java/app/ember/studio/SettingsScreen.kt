package app.ember.studio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    state: PlayerViewModel.SettingsUiState,
    onToggleRearmOnBootEnabled: (Boolean) -> Unit,
    onSelectRearmMinMinutes: (Int) -> Unit,
    onToggleSkipSilenceEnabled: (Boolean) -> Unit,
    onSelectCrossfadeMs: (Int) -> Unit,
    onSelectLongformThresholdMinutes: (Int) -> Unit,
    onToggleUseHaptics: (Boolean) -> Unit,
    onRescanLibrary: () -> Unit,
    onOpenScanImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.drawer_settings),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider()
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.settings_rearm_on_boot_title)) },
            supportingContent = {
                Text(text = stringResource(R.string.settings_rearm_on_boot_subtitle))
            },
            trailingContent = {
                val desc = stringResource(R.string.settings_rearm_on_boot_title)
                Switch(
                    checked = state.rearmOnBootEnabled,
                    onCheckedChange = { onToggleRearmOnBootEnabled(it) },
                    modifier = Modifier.semantics { contentDescription = desc }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        ListItem(
            headlineContent = { Text(text = stringResource(R.string.settings_rearm_threshold_title)) },
            supportingContent = { Text(text = stringResource(R.string.settings_rearm_threshold_subtitle)) },
            trailingContent = {
                // Render selectable minute options as chips
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.rearmMinOptions.forEach { m ->
                        val label = stringResource(R.string.sleep_timer_minutes_short, m)
                        FilterChip(
                            selected = state.rearmMinMinutes == m,
                            onClick = { onSelectRearmMinMinutes(m) },
                            label = { Text(text = label) }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        // Haptic feedback toggle
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.settings_haptics_title)) },
            supportingContent = { Text(text = stringResource(R.string.settings_haptics_subtitle)) },
            trailingContent = {
                val desc = stringResource(R.string.settings_haptics_title)
                Switch(
                    checked = state.useHaptics,
                    onCheckedChange = onToggleUseHaptics,
                    modifier = Modifier.semantics { contentDescription = desc }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        // Skip silence
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.settings_skip_silence_title)) },
            supportingContent = { Text(text = stringResource(R.string.settings_skip_silence_subtitle)) },
            trailingContent = {
                val desc = stringResource(R.string.settings_skip_silence_title)
                Switch(
                    checked = state.skipSilenceEnabled,
                    onCheckedChange = onToggleSkipSilenceEnabled,
                    modifier = Modifier.semantics { contentDescription = desc }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Crossfade duration (placeholder if not supported)
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.settings_crossfade_title)) },
            supportingContent = { Text(text = stringResource(R.string.settings_crossfade_subtitle)) },
            trailingContent = {
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.crossfadeOptions.forEach { ms ->
                        val label = if (ms == 0) stringResource(R.string.off_label) else stringResource(R.string.seconds_short, ms / 1000)
                        FilterChip(
                            selected = state.crossfadeMs == ms,
                            onClick = { onSelectCrossfadeMs(ms) },
                            label = { Text(text = label) }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Long-form threshold minutes
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.settings_longform_threshold_title)) },
            supportingContent = { Text(text = stringResource(R.string.settings_longform_threshold_subtitle)) },
            trailingContent = {
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.longformThresholdOptions.forEach { m ->
                        val label = stringResource(R.string.sleep_timer_minutes_short, m)
                        FilterChip(
                            selected = state.longformThresholdMinutes == m,
                            onClick = { onSelectLongformThresholdMinutes(m) },
                            label = { Text(text = label) }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        // Library actions
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.songs_rescan_button)) },
            supportingContent = { Text(text = stringResource(R.string.settings_rescan_subtitle)) },
            trailingContent = {
                AssistChip(onClick = onRescanLibrary, label = { Text(text = stringResource(R.string.songs_rescan_button)) })
            },
            modifier = Modifier.fillMaxWidth()
        )

        ListItem(
            headlineContent = { Text(text = stringResource(R.string.settings_manage_folders_title)) },
            supportingContent = { Text(text = stringResource(R.string.settings_manage_folders_subtitle)) },
            trailingContent = {
                AssistChip(onClick = onOpenScanImport, label = { Text(text = stringResource(R.string.manage_label)) })
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
