package app.ember.studio.sleep

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SleepTimerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION) return
        val fadeEnabled = intent.getBooleanExtra(EXTRA_FADE_ENABLED, false)
        val endActionName = intent.getStringExtra(EXTRA_END_ACTION)
        val endAction = SleepTimerEndAction.entries.firstOrNull { it.name == endActionName }
            ?: SleepTimerEndAction.PausePlayback
        val originalVolume = intent.getFloatExtra(EXTRA_ORIGINAL_VOLUME, -1f).takeIf { it >= 0f }

        // Mark the timer as expired in preferences so the app can apply the action on next resume.
        val repo = SleepTimerPreferencesRepository(context.sleepTimerPreferencesDataStore)
        CoroutineScope(Dispatchers.IO).launch {
            val message = when (endAction) {
                SleepTimerEndAction.PausePlayback -> context.getString(app.ember.studio.R.string.sleep_timer_message_completed_pause)
                SleepTimerEndAction.StopPlayback -> context.getString(app.ember.studio.R.string.sleep_timer_message_completed_stop)
                SleepTimerEndAction.StopAfterTrack -> context.getString(app.ember.studio.R.string.sleep_timer_message_completed_stop_after_track)
                SleepTimerEndAction.StopAfterQueue -> context.getString(app.ember.studio.R.string.sleep_timer_message_completed_stop_after_queue)
            }
            repo.updatePreferences(
                SleepTimerPreferences(
                    // Keep selection inputs as-is; write only active/expired state and message
                    fadeEnabled = fadeEnabled,
                    endAction = endAction,
                    activeEndTimestampMillis = System.currentTimeMillis() - 1L,
                    activeFadeEnabled = fadeEnabled,
                    activeEndAction = endAction,
                    activeOriginalVolume = originalVolume,
                    statusMessage = message
                )
            )
        }
    }

    companion object {
        const val ACTION = "app.ember.studio.action.SLEEP_TIMER_ALARM"
        const val EXTRA_FADE_ENABLED = "extra_fade_enabled"
        const val EXTRA_END_ACTION = "extra_end_action"
        const val EXTRA_ORIGINAL_VOLUME = "extra_original_volume"
    }
}

