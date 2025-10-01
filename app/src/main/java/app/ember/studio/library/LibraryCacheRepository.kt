package app.ember.studio.library

import android.content.Context
import java.io.File
import org.json.JSONArray
import org.json.JSONObject

class LibraryCacheRepository(private val context: Context) {
    private val cacheFile: File
        get() = File(context.filesDir, "library_index.json")

    fun load(): LibraryIndex? {
        return try {
            val file = cacheFile
            if (!file.exists()) return null
            val text = file.readText()
            if (text.isBlank()) return null
            val root = JSONObject(text)
            val songsArray = root.optJSONArray("songs") ?: return null
            val songs = buildList {
                for (i in 0 until songsArray.length()) {
                    val obj = songsArray.getJSONObject(i)
                    add(
                        SongItem(
                            id = obj.optString("id"),
                            title = obj.optString("title"),
                            artist = obj.optString("artist"),
                            album = obj.optString("album"),
                            durationMs = obj.optLong("durationMs"),
                            bucketDisplayName = obj.optString("bucket").takeIf { it.isNotEmpty() },
                            relativePath = obj.optString("relPath").takeIf { it.isNotEmpty() },
                            dateAddedSec = obj.optLong("addedSec").takeIf { it > 0L }
                        )
                    )
                }
            }
            LibraryIndex(songs = songs)
        } catch (_: Throwable) {
            null
        }
    }

    fun save(index: LibraryIndex) {
        try {
            val root = JSONObject()
            val songsArray = JSONArray()
            index.songs.forEach { item ->
                songsArray.put(
                    JSONObject()
                        .put("id", item.id)
                        .put("title", item.title)
                        .put("artist", item.artist)
                        .put("album", item.album)
                        .put("durationMs", item.durationMs)
                        .put("bucket", item.bucketDisplayName ?: "")
                        .put("relPath", item.relativePath ?: "")
                        .put("addedSec", item.dateAddedSec ?: 0L)
                )
            }
            root.put("songs", songsArray)
            cacheFile.parentFile?.mkdirs()
            cacheFile.writeText(root.toString())
        } catch (_: Throwable) {
            // Ignore cache write errors
        }
    }
}
