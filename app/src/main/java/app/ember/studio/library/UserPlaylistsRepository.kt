package app.ember.studio.library

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException
import java.util.UUID

private const val DATASTORE_NAME = "user_playlists"
private val JsonKey = stringPreferencesKey("playlists_json")

val Context.userPlaylistsDataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

data class UserPlaylist(
    val id: String = "user:" + UUID.randomUUID().toString(),
    val title: String,
    val items: List<String>
)

class UserPlaylistsRepository(private val dataStore: DataStore<Preferences>) {
    val playlists: Flow<Map<String, UserPlaylist>> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            val json = prefs[JsonKey] ?: return@map emptyMap()
            parse(json)
        }

    suspend fun addOrReplace(playlist: UserPlaylist) {
        dataStore.edit { prefs ->
            val map = parse(prefs[JsonKey] ?: "{}").toMutableMap()
            map[playlist.id] = playlist
            prefs[JsonKey] = serialize(map)
        }
    }

    suspend fun updateItems(id: String, items: List<String>) {
        dataStore.edit { prefs ->
            val map = parse(prefs[JsonKey] ?: "{}").toMutableMap()
            val existing = map[id] ?: return@edit
            map[id] = existing.copy(items = items)
            prefs[JsonKey] = serialize(map)
        }
    }

    suspend fun removeItem(id: String, itemId: String) {
        dataStore.edit { prefs ->
            val map = parse(prefs[JsonKey] ?: "{}").toMutableMap()
            val existing = map[id] ?: return@edit
            val filtered = existing.items.filterNot { it == itemId }
            map[id] = existing.copy(items = filtered)
            prefs[JsonKey] = serialize(map)
        }
    }

    suspend fun addItem(id: String, itemId: String) {
        dataStore.edit { prefs ->
            val map = parse(prefs[JsonKey] ?: "{}").toMutableMap()
            val existing = map[id] ?: return@edit
            if (existing.items.contains(itemId)) return@edit
            map[id] = existing.copy(items = existing.items + itemId)
            prefs[JsonKey] = serialize(map)
        }
    }

    suspend fun ensurePlaylist(title: String, stableId: String? = null): String {
        var resultId: String? = null
        dataStore.edit { prefs ->
            val map = parse(prefs[JsonKey] ?: "{}").toMutableMap()
            // Try find by title (case-insensitive)
            val found = map.values.firstOrNull { it.title.equals(title, ignoreCase = true) }
            if (found != null) {
                resultId = found.id
            } else {
                val id = stableId ?: ("user:" + java.util.UUID.randomUUID().toString())
                map[id] = UserPlaylist(id = id, title = title, items = emptyList())
                prefs[JsonKey] = serialize(map)
                resultId = id
            }
        }
        return resultId!!
    }

    companion object {
        fun parse(json: String): Map<String, UserPlaylist> = try {
            val obj = org.json.JSONObject(json)
            val out = mutableMapOf<String, UserPlaylist>()
            obj.keys().forEach { key ->
                val o = obj.getJSONObject(key)
                val title = o.optString("title")
                val arr = o.optJSONArray("items")
                val items = buildList {
                    if (arr != null) {
                        for (i in 0 until arr.length()) add(arr.optString(i))
                    }
                }
                out[key] = UserPlaylist(id = key, title = title, items = items)
            }
            out
        } catch (_: Throwable) { emptyMap() }

        fun serialize(map: Map<String, UserPlaylist>): String {
            fun esc(s: String): String = s.replace("\\", "\\\\").replace("\"", "\\\"")
            val sb = StringBuilder()
            sb.append('{')
            var first = true
            for ((id, pl) in map) {
                if (!first) sb.append(',') else first = false
                sb.append('"').append(esc(id)).append('"').append(':')
                sb.append('{')
                sb.append("\"title\":\"").append(esc(pl.title)).append("\",")
                sb.append("\"items\":[")
                var firstItem = true
                for (i in pl.items) {
                    if (!firstItem) sb.append(',') else firstItem = false
                    sb.append('"').append(esc(i)).append('"')
                }
                sb.append(']')
                sb.append('}')
            }
            sb.append('}')
            return sb.toString()
        }

        /**
         * Parses an M3U file at [uri] and returns a simple user playlist by best-effort
         * mapping: lines not starting with '#' treated as paths/URIs; items become ids directly.
         * The caller should map entries to known song ids as needed after import.
         */
        fun parseM3U(context: Context, uri: Uri): UserPlaylist? {
            return try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    val lines = BufferedReader(InputStreamReader(input)).readLines()
                    val entries = lines.asSequence()
                        .map { it.trim() }
                        .filter { it.isNotEmpty() && !it.startsWith("#") }
                        .toList()
                    if (entries.isEmpty()) return null
                    val title = uri.lastPathSegment?.substringAfterLast('/') ?: "Imported playlist"
                    // Use the raw entries as provisional ids; the ViewModel maps these to song ids.
                    val items = entries
                    val id = "user:" + UUID.randomUUID().toString()
                    UserPlaylist(id = id, title = title, items = items)
                }
            } catch (_: Throwable) { null }
        }
    }
}
