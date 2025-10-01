package app.ember.studio.sleep

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SleepTimerBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val repo = SleepTimerPreferencesRepository(context.sleepTimerPreferencesDataStore)
            val prefs = repo.preferences.first()
            val end = prefs.activeEndTimestampMillis
            val action = prefs.activeEndAction
            val shouldRearm = prefs.rearmOnBootEnabled
            val now = System.currentTimeMillis()
            val remaining = if (end != null) end - now else -1L
            val minMs = (prefs.rearmMinMinutes.coerceIn(1, 240)) * 60 * 1000L
            if (shouldRearm && end != null && action != null && remaining >= minMs) {
                SleepTimerAlarmScheduler(context).schedule(
                    endTimestampMillis = end,
                    fadeEnabled = prefs.activeFadeEnabled,
                    endAction = action,
                    originalVolume = prefs.activeOriginalVolume
                )
            }
        }
    }
}
