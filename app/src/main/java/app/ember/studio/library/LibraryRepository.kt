package app.ember.studio.library

import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class LibraryIndex(
    val songs: List<SongItem>,
)

data class SongItem(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val bucketDisplayName: String? = null,
    val relativePath: String? = null,
    val dateAddedSec: Long? = null
)

interface LibraryRepository {
    suspend fun scan(context: Context): LibraryIndex
}

class MediaStoreLibraryRepository : LibraryRepository {
    override suspend fun scan(context: Context): LibraryIndex = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.RELATIVE_PATH
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC"
        val songs = mutableListOf<SongItem>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        resolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dateAddedCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            val bucketCol = cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
            val relPathCol = cursor.getColumnIndex(MediaStore.MediaColumns.RELATIVE_PATH)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol).toString()
                val title = cursor.getString(titleCol) ?: ""
                val artist = cursor.getString(artistCol) ?: ""
                val album = cursor.getString(albumCol) ?: ""
                val duration = cursor.getLong(durationCol)
                val addedSec = if (dateAddedCol >= 0) cursor.getLong(dateAddedCol) else null
                val bucket = if (bucketCol >= 0) cursor.getString(bucketCol) else null
                val relPath = if (relPathCol >= 0) cursor.getString(relPathCol) else null
                songs += SongItem(
                    id = "media:$id",
                    title = title,
                    artist = artist,
                    album = album,
                    durationMs = duration,
                    bucketDisplayName = bucket,
                    relativePath = relPath,
                    dateAddedSec = addedSec
                )
            }
        }
        LibraryIndex(songs = songs)
    }
}
