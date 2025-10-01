package app.ember.studio.sleep

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SleepTimerPreferencesRepositoryTest {

    @Test
    fun mapSleepTimerPreferencesReturnsDefaultsWhenEmpty() {
        val preferences = mutablePreferencesOf()

        val mapped = mapSleepTimerPreferences(preferences)

        assertEquals(SleepTimerPreferences(), mapped)
        assertEquals(null, mapped.activeEndTimestampMillis)
        assertEquals(false, mapped.activeFadeEnabled)
        assertEquals(null, mapped.activeEndAction)
        assertEquals(null, mapped.activeOriginalVolume)
        assertEquals(SleepTimerPendingActionType.None, mapped.pendingAction.type)
        assertEquals(null, mapped.statusMessage)
    }

    @Test
    fun mapSleepTimerPreferencesReadsStoredValues() {
        val preferences = mutablePreferencesOf(
            intPreferencesKey("sleep_timer_quick_duration_minutes") to 30,
            stringPreferencesKey("sleep_timer_custom_hours") to "1",
            stringPreferencesKey("sleep_timer_custom_minutes") to "15",
            booleanPreferencesKey("sleep_timer_fade_enabled") to true,
            stringPreferencesKey("sleep_timer_end_action") to SleepTimerEndAction.StopAfterQueue.name,
            longPreferencesKey("sleep_timer_active_end_timestamp") to 42L,
            booleanPreferencesKey("sleep_timer_active_fade_enabled") to true,
            stringPreferencesKey("sleep_timer_active_end_action") to SleepTimerEndAction.StopPlayback.name,
            floatPreferencesKey("sleep_timer_active_original_volume") to 0.42f,
            stringPreferencesKey("sleep_timer_pending_action_type") to SleepTimerPendingActionType.StopAfterQueue.name,
            stringPreferencesKey("sleep_timer_pending_queue_snapshot") to "a|b|c",
            intPreferencesKey("sleep_timer_pending_queue_index") to 1,
            stringPreferencesKey("sleep_timer_status_message") to "Timer finished"
        )

        val mapped = mapSleepTimerPreferences(preferences)

        assertEquals(30, mapped.selectedQuickDurationMinutes)
        assertEquals("1", mapped.customHoursInput)
        assertEquals("15", mapped.customMinutesInput)
        assertTrue(mapped.fadeEnabled)
        assertEquals(SleepTimerEndAction.StopAfterQueue, mapped.endAction)
        assertEquals(42L, mapped.activeEndTimestampMillis)
        assertTrue(mapped.activeFadeEnabled)
        assertEquals(SleepTimerEndAction.StopPlayback, mapped.activeEndAction)
        assertEquals(0.42f, mapped.activeOriginalVolume)
        assertEquals(SleepTimerPendingActionType.StopAfterQueue, mapped.pendingAction.type)
        assertEquals(listOf("a", "b", "c"), mapped.pendingAction.queueSnapshot)
        assertEquals(1, mapped.pendingAction.queueIndex)
        assertEquals("Timer finished", mapped.statusMessage)
    }
}
