package app.ember.studio.sleep

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SleepTimerBootRearmTest {

    private lateinit var context: Context
    private lateinit var repo: SleepTimerPreferencesRepository

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        repo = SleepTimerPreferencesRepository(context.sleepTimerPreferencesDataStore)
        cancelExistingAlarm()
        clearPreferences()
    }

    @After
    fun tearDown() {
        cancelExistingAlarm()
        clearPreferences()
    }

    @Test
    fun bootReceiver_rearms_whenFeatureEnabled() = runBlocking {
        val endAt = System.currentTimeMillis() + 30_000L
        repo.updatePreferences(
            SleepTimerPreferences(
                activeEndTimestampMillis = endAt,
                activeFadeEnabled = false,
                activeEndAction = SleepTimerEndAction.PausePlayback,
                activeOriginalVolume = 0.5f,
                rearmOnBootEnabled = true
            )
        )

        sendBootCompleted()

        val pi = getExistingAlarmPI()
        assertNotNull("Expected sleep timer alarm PendingIntent after BOOT_COMPLETED", pi)
    }

    @Test
    fun bootReceiver_doesNotRearm_whenFeatureDisabled() = runBlocking {
        val endAt = System.currentTimeMillis() + 30_000L
        repo.updatePreferences(
            SleepTimerPreferences(
                activeEndTimestampMillis = endAt,
                activeFadeEnabled = false,
                activeEndAction = SleepTimerEndAction.PausePlayback,
                activeOriginalVolume = 0.5f,
                rearmOnBootEnabled = false
            )
        )

        sendBootCompleted()

        val pi = getExistingAlarmPI()
        assertNull("Expected no sleep timer alarm PendingIntent when feature disabled", pi)
    }

    @Test
    fun bootReceiver_respects_minThreshold() = runBlocking {
        // Below default 15 minute threshold — should not rearm
        val endAt = System.currentTimeMillis() + (5 * 60 * 1000L)
        repo.updatePreferences(
            SleepTimerPreferences(
                activeEndTimestampMillis = endAt,
                activeFadeEnabled = false,
                activeEndAction = SleepTimerEndAction.PausePlayback,
                activeOriginalVolume = 0.5f,
                rearmOnBootEnabled = true,
                rearmMinMinutes = 15
            )
        )

        sendBootCompleted()

        val pi = getExistingAlarmPI()
        assertNull("Expected no sleep timer alarm PendingIntent for short timers", pi)
    }

    @Test
    fun bootReceiver_rearms_whenThresholdReduced() = runBlocking {
        // Set threshold to 5 minutes; a 6-minute timer should rearm
        val endAt = System.currentTimeMillis() + (6 * 60 * 1000L)
        repo.updatePreferences(
            SleepTimerPreferences(
                activeEndTimestampMillis = endAt,
                activeFadeEnabled = false,
                activeEndAction = SleepTimerEndAction.PausePlayback,
                activeOriginalVolume = 0.5f,
                rearmOnBootEnabled = true,
                rearmMinMinutes = 5
            )
        )

        sendBootCompleted()

        val pi = getExistingAlarmPI()
        assertNotNull("Expected sleep timer alarm PendingIntent with reduced threshold", pi)
    }

    private fun sendBootCompleted() {
        val intent = Intent(Intent.ACTION_BOOT_COMPLETED).apply {
            component = ComponentName(context, SleepTimerBootReceiver::class.java)
        }
        context.sendBroadcast(intent)
        // Allow broadcast processing
        Thread.sleep(250)
    }

    private fun getExistingAlarmPI(): PendingIntent? {
        val intent = Intent(context, SleepTimerAlarmReceiver::class.java).apply {
            action = SleepTimerAlarmReceiver.ACTION
        }
        val flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, 0, intent, flags)
    }

    private fun cancelExistingAlarm() {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = getExistingAlarmPI()
        if (pi != null) {
            am.cancel(pi)
            pi.cancel()
        }
    }

    private fun clearPreferences() = runBlocking {
        repo.updatePreferences(SleepTimerPreferences())
    }
}
