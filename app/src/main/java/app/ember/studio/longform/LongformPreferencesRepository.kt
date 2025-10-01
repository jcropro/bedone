package app.ember.studio.longform

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.io.IOException

private const val DATASTORE_NAME = "longform_preferences"
private val JsonKey = stringPreferencesKey("longform_json")

val Context.longformPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

data class LongformPref(
    val bookmarkMs: Long? = null,
    val defaultSpeed: Float? = null
)

class LongformPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    val preferences: Flow<Map<String, LongformPref>> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            val json = prefs[JsonKey] ?: return@map emptyMap()
            parse(json)
        }

    suspend fun setBookmark(songId: String, positionMs: Long?) {
        dataStore.edit { prefs ->
            val map = parse(prefs[JsonKey] ?: "{}").toMutableMap()
            val current = map[songId] ?: LongformPref()
            map[songId] = current.copy(bookmarkMs = positionMs)
            prefs[JsonKey] = serialize(map)
        }
    }

    suspend fun setDefaultSpeed(songId: String, speed: Float?) {
        dataStore.edit { prefs ->
            val map = parse(prefs[JsonKey] ?: "{}").toMutableMap()
            val current = map[songId] ?: LongformPref()
            map[songId] = current.copy(defaultSpeed = speed)
            prefs[JsonKey] = serialize(map)
        }
    }

    companion object {
        fun parse(json: String): Map<String, LongformPref> = try {
            val obj = JSONObject(json)
            val out = mutableMapOf<String, LongformPref>()
            obj.keys().forEach { key ->
                val o = obj.getJSONObject(key)
                val bookmark = if (o.has("bookmarkMs")) o.optLong("bookmarkMs") else null
                val speed = if (o.has("defaultSpeed")) o.optDouble("defaultSpeed").toFloat() else null
                out[key] = LongformPref(
                    bookmarkMs = bookmark?.takeIf { it > 0L },
                    defaultSpeed = speed?.takeIf { it > 0f }
                )
            }
            out
        } catch (_: Throwable) { emptyMap() }

        fun serialize(map: Map<String, LongformPref>): String {
            fun esc(s: String): String = s.replace("\\", "\\\\").replace("\"", "\\\"")
            val sb = StringBuilder()
            sb.append('{')
            var first = true
            for ((id, pref) in map) {
                if (!first) sb.append(',') else first = false
                sb.append('"').append(esc(id)).append('"').append(':')
                sb.append('{')
                var innerFirst = true
                if (pref.bookmarkMs != null) {
                    if (!innerFirst) sb.append(',') else innerFirst = false
                    sb.append("\"bookmarkMs\":").append(pref.bookmarkMs)
                }
                if (pref.defaultSpeed != null) {
                    if (!innerFirst) sb.append(',') else innerFirst = false
                    sb.append("\"defaultSpeed\":").append(pref.defaultSpeed.toDouble())
                }
                sb.append('}')
            }
            sb.append('}')
            return sb.toString()
        }
    }
}
