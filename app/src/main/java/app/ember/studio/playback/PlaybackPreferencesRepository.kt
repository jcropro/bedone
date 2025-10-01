package app.ember.studio.playback

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val PLAYBACK_DATASTORE_NAME = "playback_preferences"

private val PlaybackSpeedKey = floatPreferencesKey("playback_speed")
private val ShuffleEnabledKey = booleanPreferencesKey("shuffle_enabled")
private val RepeatModeKey = intPreferencesKey("repeat_mode")
// Settings
private val SkipSilenceEnabledKey = booleanPreferencesKey("skip_silence_enabled")
private val CrossfadeMsKey = intPreferencesKey("crossfade_ms")
private val LongformThresholdMinutesKey = intPreferencesKey("longform_threshold_minutes")
private val UseHapticsEnabledKey = booleanPreferencesKey("use_haptics_enabled")

val Context.playbackPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = PLAYBACK_DATASTORE_NAME
)

data class PlaybackPreferences(
    val playbackSpeed: Float = 1.0f,
    val shuffleEnabled: Boolean = false,
    // 0=OFF, 1=ONE, 2=ALL to align with ExoPlayer constants mapping (we'll normalize in VM)
    val repeatMode: Int = 0,
    val skipSilenceEnabled: Boolean = false,
    val crossfadeMs: Int = 0,
    val longformThresholdMinutes: Int = 20,
    val useHapticsEnabled: Boolean = true
)

class PlaybackPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val preferences: Flow<PlaybackPreferences> =
        dataStore.data
            .catch { throwable ->
                if (throwable is IOException) emit(emptyPreferences()) else throw throwable
            }
            .map { prefs ->
                val speed = prefs[PlaybackSpeedKey] ?: 1.0f
                val shuffle = prefs[ShuffleEnabledKey] ?: false
                val repeat = prefs[RepeatModeKey] ?: 0
                val skipSilence = prefs[SkipSilenceEnabledKey] ?: false
                val crossfade = (prefs[CrossfadeMsKey] ?: 0).coerceIn(0, 12_000)
                val thresholdMin = (prefs[LongformThresholdMinutesKey] ?: 20).coerceIn(5, 120)
                val haptics = prefs[UseHapticsEnabledKey] ?: true
                PlaybackPreferences(
                    playbackSpeed = speed,
                    shuffleEnabled = shuffle,
                    repeatMode = repeat,
                    skipSilenceEnabled = skipSilence,
                    crossfadeMs = crossfade,
                    longformThresholdMinutes = thresholdMin,
                    useHapticsEnabled = haptics
                )
            }

    suspend fun setPlaybackSpeed(speed: Float) {
        dataStore.edit { prefs ->
            prefs[PlaybackSpeedKey] = speed
        }
    }

    suspend fun setShuffleEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[ShuffleEnabledKey] = enabled }
    }

    suspend fun setRepeatMode(mode: Int) {
        dataStore.edit { prefs -> prefs[RepeatModeKey] = mode }
    }

    suspend fun setSkipSilenceEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[SkipSilenceEnabledKey] = enabled }
    }

    suspend fun setCrossfadeMs(ms: Int) {
        val clamped = ms.coerceIn(0, 12_000)
        dataStore.edit { prefs -> prefs[CrossfadeMsKey] = clamped }
    }

    suspend fun setLongformThresholdMinutes(minutes: Int) {
        val clamped = minutes.coerceIn(5, 120)
        dataStore.edit { prefs -> prefs[LongformThresholdMinutesKey] = clamped }
    }

    suspend fun setUseHapticsEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[UseHapticsEnabledKey] = enabled }
    }
}
