package app.ember.studio.sleep

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val SLEEP_TIMER_DATASTORE_NAME = "sleep_timer_preferences"
private const val QUEUE_DELIMITER = "|"

private val SelectedQuickDurationKey = intPreferencesKey("sleep_timer_quick_duration_minutes")
private val CustomHoursInputKey = stringPreferencesKey("sleep_timer_custom_hours")
private val CustomMinutesInputKey = stringPreferencesKey("sleep_timer_custom_minutes")
private val FadeEnabledKey = booleanPreferencesKey("sleep_timer_fade_enabled")
private val EndActionKey = stringPreferencesKey("sleep_timer_end_action")

// Active timer persistence (for resume)
private val ActiveEndTimestampKey = longPreferencesKey("sleep_timer_active_end_timestamp")
private val ActiveFadeEnabledKey = booleanPreferencesKey("sleep_timer_active_fade_enabled")
private val ActiveEndActionKey = stringPreferencesKey("sleep_timer_active_end_action")
private val ActiveOriginalVolumeKey = floatPreferencesKey("sleep_timer_active_original_volume")

// Feature flag: whether to re-arm timers on BOOT_COMPLETED
private val RearmOnBootEnabledKey = booleanPreferencesKey("sleep_timer_rearm_on_boot_enabled")
private val RearmMinMinutesKey = intPreferencesKey("sleep_timer_rearm_min_minutes")

// Pending action persistence (stop-after-track/queue)
private val PendingActionTypeKey = stringPreferencesKey("sleep_timer_pending_action_type")
private val PendingTrackIdKey = stringPreferencesKey("sleep_timer_pending_track_id")
private val PendingQueueSnapshotKey = stringPreferencesKey("sleep_timer_pending_queue_snapshot")
private val PendingQueueIndexKey = intPreferencesKey("sleep_timer_pending_queue_index")

// UI message (toast/snackbar style)
private val StatusMessageKey = stringPreferencesKey("sleep_timer_status_message")

val Context.sleepTimerPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SLEEP_TIMER_DATASTORE_NAME
)

enum class SleepTimerPendingActionType {
    None,
    StopAfterTrack,
    StopAfterQueue
}

data class SleepTimerPendingActionPreferences(
    val type: SleepTimerPendingActionType = SleepTimerPendingActionType.None,
    val trackId: String? = null,
    val queueSnapshot: List<String> = emptyList(),
    val queueIndex: Int = 0
)

data class SleepTimerPreferences(
    val selectedQuickDurationMinutes: Int? = null,
    val customHoursInput: String = "",
    val customMinutesInput: String = "",
    val fadeEnabled: Boolean = false,
    val endAction: SleepTimerEndAction = SleepTimerEndAction.PausePlayback,
    val activeEndTimestampMillis: Long? = null,
    val activeFadeEnabled: Boolean = false,
    val activeEndAction: SleepTimerEndAction? = null,
    val activeOriginalVolume: Float? = null,
    val rearmOnBootEnabled: Boolean = false,
    val rearmMinMinutes: Int = 15,
    val pendingAction: SleepTimerPendingActionPreferences = SleepTimerPendingActionPreferences(),
    val statusMessage: String? = null
)

class SleepTimerPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    val preferences: Flow<SleepTimerPreferences> =
        dataStore.data
            .catch { throwable ->
                if (throwable is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw throwable
                }
            }
            .map(::mapSleepTimerPreferences)

    suspend fun updatePreferences(preferences: SleepTimerPreferences) {
        dataStore.edit { mutable ->
            val quickMinutes = preferences.selectedQuickDurationMinutes
            if (quickMinutes != null) {
                mutable[SelectedQuickDurationKey] = quickMinutes
            } else {
                mutable.remove(SelectedQuickDurationKey)
            }

            if (preferences.customHoursInput.isNotEmpty()) {
                mutable[CustomHoursInputKey] = preferences.customHoursInput
            } else {
                mutable.remove(CustomHoursInputKey)
            }

            if (preferences.customMinutesInput.isNotEmpty()) {
                mutable[CustomMinutesInputKey] = preferences.customMinutesInput
            } else {
                mutable.remove(CustomMinutesInputKey)
            }

            mutable[FadeEnabledKey] = preferences.fadeEnabled
            mutable[EndActionKey] = preferences.endAction.name

            // Active timer fields (for resume)
            val activeEnd = preferences.activeEndTimestampMillis
            if (activeEnd != null) {
                mutable[ActiveEndTimestampKey] = activeEnd
                mutable[ActiveFadeEnabledKey] = preferences.activeFadeEnabled

                val activeAction = preferences.activeEndAction
                if (activeAction != null) {
                    mutable[ActiveEndActionKey] = activeAction.name
                } else {
                    mutable.remove(ActiveEndActionKey)
                }

                val originalVolume = preferences.activeOriginalVolume
                if (originalVolume != null) {
                    mutable[ActiveOriginalVolumeKey] = originalVolume
                } else {
                    mutable.remove(ActiveOriginalVolumeKey)
                }
            } else {
                mutable.remove(ActiveEndTimestampKey)
                mutable.remove(ActiveFadeEnabledKey)
                mutable.remove(ActiveEndActionKey)
                mutable.remove(ActiveOriginalVolumeKey)
            }

            // Pending action
            when (preferences.pendingAction.type) {
                SleepTimerPendingActionType.None -> {
                    mutable.remove(PendingActionTypeKey)
                    mutable.remove(PendingTrackIdKey)
                    mutable.remove(PendingQueueSnapshotKey)
                    mutable.remove(PendingQueueIndexKey)
                }
                SleepTimerPendingActionType.StopAfterTrack -> {
                    mutable[PendingActionTypeKey] = SleepTimerPendingActionType.StopAfterTrack.name
                    val trackId = preferences.pendingAction.trackId
                    if (!trackId.isNullOrEmpty()) {
                        mutable[PendingTrackIdKey] = trackId
                    } else {
                        mutable.remove(PendingTrackIdKey)
                    }
                    mutable.remove(PendingQueueSnapshotKey)
                    mutable.remove(PendingQueueIndexKey)
                }
                SleepTimerPendingActionType.StopAfterQueue -> {
                    mutable[PendingActionTypeKey] = SleepTimerPendingActionType.StopAfterQueue.name
                    val snapshot = preferences.pendingAction.queueSnapshot
                    if (snapshot.isNotEmpty()) {
                        mutable[PendingQueueSnapshotKey] = snapshot.joinToString(QUEUE_DELIMITER)
                        mutable[PendingQueueIndexKey] = preferences.pendingAction.queueIndex
                    } else {
                        mutable.remove(PendingQueueSnapshotKey)
                        mutable.remove(PendingQueueIndexKey)
                    }
                    mutable.remove(PendingTrackIdKey)
                }
            }

            val statusMessage = preferences.statusMessage
            if (statusMessage != null) {
                mutable[StatusMessageKey] = statusMessage
            } else {
                mutable.remove(StatusMessageKey)
            }

            // Feature flag
            mutable[RearmOnBootEnabledKey] = preferences.rearmOnBootEnabled
            // Minimum threshold (persist always; clamp to sane bounds)
            val minMinutes = preferences.rearmMinMinutes.coerceIn(1, 240)
            mutable[RearmMinMinutesKey] = minMinutes
        }
    }
}

internal fun mapSleepTimerPreferences(preferences: Preferences): SleepTimerPreferences {
    val quickMinutes = preferences[SelectedQuickDurationKey]
    val customHours = preferences[CustomHoursInputKey] ?: ""
    val customMinutes = preferences[CustomMinutesInputKey] ?: ""
    val fadeEnabled = preferences[FadeEnabledKey] ?: false

    val endActionName = preferences[EndActionKey]
    val endAction = SleepTimerEndAction.entries.firstOrNull { it.name == endActionName }
        ?: SleepTimerEndAction.PausePlayback

    // Active timer
    val activeEndTimestamp = preferences[ActiveEndTimestampKey]
    val activeFadeEnabled = preferences[ActiveFadeEnabledKey] ?: false
    val activeEndActionName = preferences[ActiveEndActionKey]
    val activeEndAction = activeEndActionName?.let { name ->
        SleepTimerEndAction.entries.firstOrNull { it.name == name }
    }
    val activeOriginalVolume = preferences[ActiveOriginalVolumeKey]
    val rearmOnBootEnabled = preferences[RearmOnBootEnabledKey] ?: false
    val rearmMinMinutes = (preferences[RearmMinMinutesKey] ?: 15).coerceIn(1, 240)

    // Pending action
    val pendingTypeName = preferences[PendingActionTypeKey]
    val pendingType = SleepTimerPendingActionType.entries.firstOrNull { it.name == pendingTypeName }
        ?: SleepTimerPendingActionType.None
    val pendingTrackId = preferences[PendingTrackIdKey]
    val pendingQueueSnapshot = preferences[PendingQueueSnapshotKey]
        ?.takeIf { it.isNotEmpty() }
        ?.split(QUEUE_DELIMITER)
        ?.filter { it.isNotEmpty() }
        ?: emptyList()
    val pendingQueueIndex = preferences[PendingQueueIndexKey] ?: 0

    val statusMessage = preferences[StatusMessageKey]

    val pendingAction = when (pendingType) {
        SleepTimerPendingActionType.None -> SleepTimerPendingActionPreferences()
        SleepTimerPendingActionType.StopAfterTrack -> {
            val trackId = pendingTrackId ?: return SleepTimerPreferences(
                selectedQuickDurationMinutes = quickMinutes,
                customHoursInput = customHours,
                customMinutesInput = customMinutes,
                fadeEnabled = fadeEnabled,
                endAction = endAction,
                activeEndTimestampMillis = activeEndTimestamp,
                activeFadeEnabled = activeFadeEnabled,
                activeEndAction = activeEndAction,
                activeOriginalVolume = activeOriginalVolume,
                statusMessage = statusMessage
            )
            SleepTimerPendingActionPreferences(
                type = SleepTimerPendingActionType.StopAfterTrack,
                trackId = trackId
            )
        }
        SleepTimerPendingActionType.StopAfterQueue -> SleepTimerPendingActionPreferences(
            type = SleepTimerPendingActionType.StopAfterQueue,
            queueSnapshot = pendingQueueSnapshot,
            queueIndex = pendingQueueIndex
        )
    }

    return SleepTimerPreferences(
        selectedQuickDurationMinutes = quickMinutes,
        customHoursInput = customHours,
        customMinutesInput = customMinutes,
        fadeEnabled = fadeEnabled,
        endAction = endAction,
        activeEndTimestampMillis = activeEndTimestamp,
        activeFadeEnabled = activeFadeEnabled,
        activeEndAction = activeEndAction,
        activeOriginalVolume = activeOriginalVolume,
        rearmOnBootEnabled = rearmOnBootEnabled,
        rearmMinMinutes = rearmMinMinutes,
        pendingAction = pendingAction,
        statusMessage = statusMessage
    )
}
