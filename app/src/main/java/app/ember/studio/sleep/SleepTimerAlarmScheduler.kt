package app.ember.studio.sleep

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class SleepTimerAlarmScheduler(private val context: Context) : SleepTimerScheduler {

    override fun schedule(
        endTimestampMillis: Long,
        fadeEnabled: Boolean,
        endAction: SleepTimerEndAction,
        originalVolume: Float?
    ) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pi = createPendingIntent(context, fadeEnabled, endAction, originalVolume)
        val type = AlarmManager.RTC_WAKEUP
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            am.setExactAndAllowWhileIdle(type, endTimestampMillis, pi)
        } else {
            am.setExact(type, endTimestampMillis, pi)
        }
    }

    override fun cancel() {
        val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pi = createPendingIntent(context, false, SleepTimerEndAction.PausePlayback, null)
        am.cancel(pi)
    }

    private fun createPendingIntent(
        context: Context,
        fadeEnabled: Boolean,
        endAction: SleepTimerEndAction,
        originalVolume: Float?
    ): PendingIntent {
        val intent = Intent(context, SleepTimerAlarmReceiver::class.java).apply {
            action = SleepTimerAlarmReceiver.ACTION
            putExtra(SleepTimerAlarmReceiver.EXTRA_FADE_ENABLED, fadeEnabled)
            putExtra(SleepTimerAlarmReceiver.EXTRA_END_ACTION, endAction.name)
            originalVolume?.let { putExtra(SleepTimerAlarmReceiver.EXTRA_ORIGINAL_VOLUME, it) }
        }
        val flags = (PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return PendingIntent.getBroadcast(context, 0, intent, flags)
    }
}

