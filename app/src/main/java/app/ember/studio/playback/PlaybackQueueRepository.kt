package app.ember.studio.playback

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DATASTORE_NAME = "playback_queue_preferences"

private val QueueCsvKey = stringPreferencesKey("queue_csv")
private val CurrentIndexKey = intPreferencesKey("current_index")
private val NowPlayingIdKey = stringPreferencesKey("now_playing_id")
private val PositionMsKey = longPreferencesKey("position_ms")
private val PlayWhenReadyKey = booleanPreferencesKey("play_when_ready")

val Context.playbackQueuePreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME
)

data class PlaybackQueuePreferences(
    val queueOrder: List<String> = emptyList(),
    val currentIndex: Int = 0,
    val nowPlayingId: String? = null,
    val positionMs: Long = 0L,
    val playWhenReady: Boolean = false
)

class PlaybackQueueRepository(
    private val dataStore: DataStore<Preferences>
) {
    val preferences: Flow<PlaybackQueuePreferences> =
        dataStore.data
            .catch { throwable ->
                if (throwable is IOException) emit(emptyPreferences()) else throw throwable
            }
            .map { prefs ->
                val csv = prefs[QueueCsvKey]
                val order = csv?.split('|')?.filter { it.isNotBlank() } ?: emptyList()
                PlaybackQueuePreferences(
                    queueOrder = order,
                    currentIndex = prefs[CurrentIndexKey] ?: 0,
                    nowPlayingId = prefs[NowPlayingIdKey],
                    positionMs = prefs[PositionMsKey] ?: 0L,
                    playWhenReady = prefs[PlayWhenReadyKey] ?: false
                )
            }

    suspend fun update(preferences: PlaybackQueuePreferences) {
        dataStore.edit { prefs ->
            if (preferences.queueOrder.isEmpty()) {
                prefs.remove(QueueCsvKey)
                prefs.remove(CurrentIndexKey)
                prefs.remove(NowPlayingIdKey)
                prefs.remove(PositionMsKey)
                prefs.remove(PlayWhenReadyKey)
            } else {
                prefs[QueueCsvKey] = preferences.queueOrder.joinToString("|")
                prefs[CurrentIndexKey] = preferences.currentIndex
                preferences.nowPlayingId?.let { prefs[NowPlayingIdKey] = it } ?: prefs.remove(NowPlayingIdKey)
                prefs[PositionMsKey] = preferences.positionMs.coerceAtLeast(0L)
                prefs[PlayWhenReadyKey] = preferences.playWhenReady
            }
        }
    }
}

