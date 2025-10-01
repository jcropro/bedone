package app.ember.studio.imports

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.ember.studio.R
import app.ember.studio.SongSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException

private const val DATASTORE_NAME = "saf_folders"
private val FoldersJsonKey = stringPreferencesKey("folders_json")

val Context.safFolderDataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

class FolderImportRepository(private val dataStore: DataStore<Preferences>) {

    val folders: Flow<List<Uri>> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            val json = prefs[FoldersJsonKey] ?: "[]"
            val arr = try { JSONArray(json) } catch (_: Throwable) { JSONArray() }
            buildList {
                for (i in 0 until arr.length()) {
                    val s = arr.optString(i)
                    if (s.isNullOrBlank()) continue
                    add(Uri.parse(s))
                }
            }
        }

    suspend fun addFolder(uri: Uri) {
        dataStore.edit { prefs ->
            val json = prefs[FoldersJsonKey] ?: "[]"
            val arr = try { JSONArray(json) } catch (_: Throwable) { JSONArray() }
            val s = uri.toString()
            var exists = false
            for (i in 0 until arr.length()) {
                if (arr.optString(i) == s) { exists = true; break }
            }
            if (!exists) {
                arr.put(s)
                prefs[FoldersJsonKey] = arr.toString()
            }
        }
    }

    suspend fun removeFolder(uri: Uri) {
        dataStore.edit { prefs ->
            val json = prefs[FoldersJsonKey] ?: "[]"
            val arr = try { JSONArray(json) } catch (_: Throwable) { JSONArray() }
            val s = uri.toString()
            val out = JSONArray()
            for (i in 0 until arr.length()) {
                val v = arr.optString(i)
                if (v != s) out.put(v)
            }
            prefs[FoldersJsonKey] = out.toString()
        }
    }

    /**
     * Scans SAF‑chosen folders for audio documents (MIME type starting with "audio/").
     * Returns lightweight SongSummary entries using the document content Uri as the media Uri and
     * a stable id of the form "saf:<docId>".
     */
    suspend fun scanAudio(context: Context): List<SongSummary> = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val folders = try { folders.firstOrNull() ?: emptyList() } catch (_: Throwable) { emptyList() }
        if (folders.isEmpty()) return@withContext emptyList()

        val results = mutableListOf<SongSummary>()
        val seen = HashSet<String>()
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED
        )

        fun titleFromName(name: String?): String {
            if (name.isNullOrBlank()) return context.getString(R.string.sample_tone_title)
            val dot = name.lastIndexOf('.')
            return if (dot > 0) name.substring(0, dot) else name
        }

        folders.forEach { treeUri ->
            val rootId = try { DocumentsContract.getTreeDocumentId(treeUri) } catch (_: Throwable) { null } ?: return@forEach
            val queue = ArrayDeque<String>()
            queue.add(rootId)
            var guard = 0
            while (queue.isNotEmpty() && guard < 50_000) { // safety guard
                guard++
                val docId = queue.removeFirst()
                val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, docId)
                resolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                    val nameCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                    val typeCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
                    val modCol = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
                    while (cursor.moveToNext()) {
                        val childId = cursor.getString(idCol)
                        val name = cursor.getString(nameCol)
                        val mime = cursor.getString(typeCol) ?: ""
                        if (mime == DocumentsContract.Document.MIME_TYPE_DIR) {
                            queue.add(childId)
                            continue
                        }
                        if (!mime.startsWith("audio/")) continue
                        val key = "${treeUri}@${childId}"
                        if (!seen.add(key)) continue
                        val contentUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, childId)
                        val lastMod = if (modCol >= 0) cursor.getLong(modCol) else 0L
                        results += SongSummary(
                            id = "saf:$childId",
                            title = titleFromName(name),
                            artist = "",
                            album = "",
                            durationMs = 0L,
                            rawResId = null,
                            uri = contentUri.toString(),
                            addedTimestampMs = lastMod.takeIf { it > 0L } ?: 0L
                        )
                    }
                }
            }
        }
        results
    }
}
