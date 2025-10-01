package app.ember.studio.tag

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

private const val TAG_OVERLAY_DATASTORE = "tag_overlay_preferences"
private val OverlaysKey = stringPreferencesKey("tag_overlays_json")

val Context.tagOverlayDataStore: DataStore<Preferences> by preferencesDataStore(
    name = TAG_OVERLAY_DATASTORE
)

data class TagOverlay(
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null
)

class TagOverlayRepository(
    private val dataStore: DataStore<Preferences>
 ) {
    /** Flow of songId -> overlay map. */
    val overlays: Flow<Map<String, TagOverlay>> =
        dataStore.data
            .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
            .map { prefs ->
                val json = prefs[OverlaysKey] ?: return@map emptyMap()
                parseOverlays(json)
            }

    suspend fun setOverlay(songId: String, overlay: TagOverlay?) {
        dataStore.edit { prefs ->
            val current = prefs[OverlaysKey]
            val map = if (current.isNullOrBlank()) mutableMapOf<String, TagOverlay>() else parseOverlays(current).toMutableMap()
            if (overlay == null || (overlay.title.isNullOrBlank() && overlay.artist.isNullOrBlank() && overlay.album.isNullOrBlank())) {
                map.remove(songId)
            } else {
                map[songId] = overlay
            }
            prefs[OverlaysKey] = serializeOverlays(map)
        }
    }

    private fun parseOverlays(json: String): Map<String, TagOverlay> {
        return try {
            val obj = JSONObject(json)
            val result = mutableMapOf<String, TagOverlay>()
            obj.keys().forEach { key ->
                val o = obj.optJSONObject(key) ?: return@forEach
                result[key] = TagOverlay(
                    title = o.optString("title").takeIf { it.isNotBlank() },
                    artist = o.optString("artist").takeIf { it.isNotBlank() },
                    album = o.optString("album").takeIf { it.isNotBlank() }
                )
            }
            result
        } catch (_: Throwable) {
            emptyMap()
        }
    }

    private fun serializeOverlays(map: Map<String, TagOverlay>): String {
        val obj = JSONObject()
        map.forEach { (id, overlay) ->
            val o = JSONObject()
            overlay.title?.let { o.put("title", it) }
            overlay.artist?.let { o.put("artist", it) }
            overlay.album?.let { o.put("album", it) }
            obj.put(id, o)
        }
        return obj.toString()
    }
}

