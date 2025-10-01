package app.ember.studio

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.ember.studio.sleep.SleepTimerDefaults
import app.ember.studio.sleep.SleepTimerEndAction
import app.ember.studio.sleep.SleepTimerUiState
import app.ember.studio.R
import java.util.Date
import kotlinx.coroutines.delay

object SleepTimerTestTags {
    const val QUICK_CHIP_PREFIX = "sleepTimerQuickChip_"
    const val HOURS_FIELD = "sleepTimerHoursField"
    const val MINUTES_FIELD = "sleepTimerMinutesField"
    const val FADE_SWITCH = "sleepTimerFadeSwitch"
    const val COUNTDOWN = "sleepTimerCountdown"
    const val STATUS_MESSAGE = "sleepTimerStatusMessage"
    const val END_ACTION_PREFIX = "sleepTimerEndAction_"
    const val SCHEDULE_SUMMARY = "sleepTimerScheduleSummary"
}

@Composable
fun SleepTimerScreen(
    state: SleepTimerUiState,
    onQuickDurationSelected: (Int) -> Unit,
    onCustomHoursChanged: (String) -> Unit,
    onCustomMinutesChanged: (String) -> Unit,
    onFadeToggle: (Boolean) -> Unit,
    onEndActionSelected: (SleepTimerEndAction) -> Unit,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    onMessageDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val timeFormat = remember(context) { DateFormat.getTimeFormat(context) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(id = R.string.sleep_timer_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(id = R.string.sleep_timer_description),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(id = R.string.sleep_timer_quick_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.quickDurationsMinutes) { minutes ->
                    FilterChip(
                        selected = state.selectedQuickDurationMinutes == minutes,
                        onClick = { onQuickDurationSelected(minutes) },
                        label = {
                            Text(
                                text = stringResource(id = R.string.sleep_timer_minutes_short, minutes)
                            )
                        },
                        enabled = !state.isRunning,
                        modifier = Modifier.testTag("${SleepTimerTestTags.QUICK_CHIP_PREFIX}$minutes")
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(id = R.string.sleep_timer_custom_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = state.customHoursInput,
                    onValueChange = onCustomHoursChanged,
                    label = { Text(stringResource(id = R.string.sleep_timer_hours_label)) },
                    enabled = !state.isRunning,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag(SleepTimerTestTags.HOURS_FIELD)
                )
                OutlinedTextField(
                    value = state.customMinutesInput,
                    onValueChange = onCustomMinutesChanged,
                    label = { Text(stringResource(id = R.string.sleep_timer_minutes_label)) },
                    enabled = !state.isRunning,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag(SleepTimerTestTags.MINUTES_FIELD)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.sleep_timer_fade_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(id = R.string.sleep_timer_fade_supporting),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                checked = state.fadeEnabled,
                onCheckedChange = onFadeToggle,
                modifier = Modifier.testTag(SleepTimerTestTags.FADE_SWITCH)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(id = R.string.sleep_timer_end_action_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            SleepTimerEndAction.entries.forEach { action ->
                val isSelected = state.endAction == action
                Surface(
                    tonalElevation = if (isSelected) 6.dp else 0.dp,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("${SleepTimerTestTags.END_ACTION_PREFIX}${action.name}")
                        .clickable { onEndActionSelected(action) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onEndActionSelected(action) }
                        )
                        Text(
                            text = stringResource(id = action.labelRes),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        if (state.isRunning) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(id = R.string.sleep_timer_remaining_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = state.remainingFormatted,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .semantics { contentDescription = state.remainingFormatted }
                        .testTag(SleepTimerTestTags.COUNTDOWN)
                )
                state.scheduledEndTimestampMillis?.let { endTimestamp ->
                    val formattedTime = timeFormat.format(Date(endTimestamp))
                    val actionLabel = stringResource(id = state.endAction.labelRes)
                    Text(
                        text = stringResource(
                            id = R.string.sleep_timer_schedule_summary,
                            formattedTime,
                            actionLabel
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag(SleepTimerTestTags.SCHEDULE_SUMMARY)
                    )
                }
            }
        }

        state.statusMessage?.let { message ->
            var acknowledged by remember(message) { mutableStateOf(false) }

            LaunchedEffect(message, acknowledged) {
                if (acknowledged) {
                    delay(SleepTimerDefaults.statusMessageAutoDismissMillis)
                    onMessageDismiss()
                }
            }

            AssistChip(
                onClick = {
                    if (acknowledged) {
                        onMessageDismiss()
                    } else {
                        acknowledged = true
                    }
                },
                label = { Text(text = message) },
                leadingIcon = if (acknowledged) {
                    {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null
                        )
                    }
                } else {
                    null
                },
                modifier = Modifier.testTag(SleepTimerTestTags.STATUS_MESSAGE)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                enabled = state.isCancelEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.sleep_timer_cancel))
            }
            Button(
                onClick = onStart,
                enabled = state.isStartEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.sleep_timer_start))
            }
        }
    }
}
