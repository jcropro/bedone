package app.ember.studio.library

import android.content.Context
import android.provider.MediaStore
import app.ember.studio.VideoSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoLibraryRepository {
    suspend fun scan(context: Context): List<VideoSummary> = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DURATION
        )
        val sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC"
        val videos = mutableListOf<VideoSummary>()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        try {
            resolver.query(uri, projection, null, null, sortOrder)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol).toString()
                    val title = cursor.getString(titleCol) ?: ""
                    val duration = cursor.getLong(durationCol)
                    videos += VideoSummary(
                        id = "video:$id",
                        title = title,
                        durationMs = duration
                    )
                }
            }
        } catch (_: SecurityException) {
            // Missing READ_MEDIA_VIDEO on API 33+; return empty and let UI show empty state.
        }
        videos
    }
}

