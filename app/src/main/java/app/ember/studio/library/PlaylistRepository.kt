package app.ember.studio.library

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import app.ember.studio.PlaylistSummary
import app.ember.studio.VideoSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * MediaStore-backed playlist repository. Returns playlist summaries and members.
 */
class PlaylistLibraryRepository {
    /**
     * Scans device playlists and returns summaries including track counts and total duration.
     */
    suspend fun scan(context: Context): List<PlaylistSummary> = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val playlists = mutableListOf<PlaylistSummary>()
        val projection = arrayOf(
            MediaStore.Audio.Playlists._ID,
            MediaStore.Audio.Playlists.NAME
        )
        val sortOrder = MediaStore.Audio.Playlists.NAME + " COLLATE NOCASE ASC"
        resolver.query(
            MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val name = cursor.getString(nameCol) ?: ""
                val (count, duration) = computePlaylistStats(context, id)
                playlists += PlaylistSummary(
                    id = "mspl:$id",
                    title = name,
                    trackCount = count,
                    totalDurationMs = duration
                )
            }
        }
        playlists
    }

    /**
     * Returns the audio IDs in the playlist in the stored order.
     */
    suspend fun members(context: Context, playlistId: Long): List<Long> = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val members = mutableListOf<Long>()
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        val projection = arrayOf(
            MediaStore.Audio.Playlists.Members.AUDIO_ID,
            MediaStore.Audio.Playlists.Members.PLAY_ORDER
        )
        val sort = MediaStore.Audio.Playlists.Members.PLAY_ORDER + " ASC"
        resolver.query(uri, projection, null, null, sort)?.use { cursor ->
            val audioIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID)
            while (cursor.moveToNext()) {
                val audioId = cursor.getLong(audioIdCol)
                members += audioId
            }
        }
        members
    }

    private fun computePlaylistStats(context: Context, playlistId: Long): Pair<Int, Long> {
        val resolver = context.contentResolver
        var count = 0
        var duration = 0L
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        val projection = arrayOf(
            MediaStore.Audio.Playlists.Members.AUDIO_ID,
            MediaStore.Audio.Media.DURATION
        )
        resolver.query(uri, projection, null, null, null)?.use { cursor ->
            val durationCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            while (cursor.moveToNext()) {
                count += 1
                if (durationCol >= 0) duration += (cursor.getLong(durationCol))
            }
        }
        return count to duration
    }
}

