package app.ember.studio.art

import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.LruCache
import androidx.annotation.ColorInt
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Tiny in-memory LRU cache for artwork palettes.
 *
 * Keys can be any stable media identifier (e.g., songId, albumId, artistKey).
 * Values include both dominant and vibrant colors derived from artwork.
 *
 * All palette computations run on Dispatchers.IO.
 */
object ArtworkPaletteCache {
    data class ArtworkPalette(
        @ColorInt val dominant: Int,
        @ColorInt val vibrant: Int
    )

    // A small LRU sized for UI usage; adjust if needed.
    private val cache = LruCache<String, ArtworkPalette>(64)

    fun clear() = cache.evictAll()

    fun getCached(key: String): ArtworkPalette? = cache.get(key)

    private fun put(key: String, value: ArtworkPalette?) {
        if (value != null) cache.put(key, value)
    }

    /**
     * Fetch palette for a song by a stable key and optional media [uri].
     * If absent in cache, computes from embedded artwork.
     */
    suspend fun getForSong(context: Context, key: String, uri: String?): ArtworkPalette? {
        getCached(key)?.let { return it }
        val computed = withContext(Dispatchers.IO) { computeFromUri(context, uri) }
        put(key, computed)
        return computed
    }

    /**
     * Fetch palette for an album. If [sampleSongUri] is provided, use it; otherwise
     * try to resolve MediaStore album art using a numeric album id when [albumKey]
     * looks like "album:123". If that fails, returns null and callers should fall back.
     */
    suspend fun getForAlbum(context: Context, albumKey: String, sampleSongUri: String? = null): ArtworkPalette? {
        getCached(albumKey)?.let { return it }
        val computed = withContext(Dispatchers.IO) {
            computeFromUri(context, sampleSongUri) ?: computeFromMediaStoreAlbum(context, albumKey)
        }
        put(albumKey, computed)
        return computed
    }

    /**
     * Fetch palette for an artist; best-effort using any [sampleSongUri].
     * We don't attempt combined palettes here – callers may blend/fallback.
     */
    suspend fun getForArtist(context: Context, artistKey: String, sampleSongUri: String? = null): ArtworkPalette? {
        getCached(artistKey)?.let { return it }
        val computed = withContext(Dispatchers.IO) { computeFromUri(context, sampleSongUri) }
        put(artistKey, computed)
        return computed
    }

    // ---- Helpers ------------------------------------------------------------------------------

    private fun computeFromUri(context: Context, uri: String?): ArtworkPalette? {
        if (uri.isNullOrEmpty()) return null
        val mmr = MediaMetadataRetriever()
        return try {
            mmr.setDataSource(context, Uri.parse(uri))
            val art = mmr.embeddedPicture ?: return null
            val bmp = BitmapFactory.decodeByteArray(art, 0, art.size) ?: return null
            val palette = Palette.from(bmp).clearFilters().generate()
            val dominant = palette.getDominantColor(0)
            val vibrant = palette.getVibrantColor(dominant)
            if (dominant == 0 && vibrant == 0) null else ArtworkPalette(dominant = dominant, vibrant = vibrant)
        } catch (_: Throwable) {
            null
        } finally {
            try { mmr.release() } catch (_: Throwable) {}
        }
    }

    private fun computeFromMediaStoreAlbum(context: Context, albumKey: String): ArtworkPalette? {
        // Expect keys like "album:123" when backed by MediaStore. Best-effort only.
        val idNum = albumKey.substringAfterLast(":", "").toLongOrNull() ?: return null
        val artUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, idNum)
        return try {
            context.contentResolver.openInputStream(artUri)?.use { input ->
                val bmp = BitmapFactory.decodeStream(input) ?: return null
                val palette = Palette.from(bmp).clearFilters().generate()
                val dominant = palette.getDominantColor(0)
                val vibrant = palette.getVibrantColor(dominant)
                if (dominant == 0 && vibrant == 0) null else ArtworkPalette(dominant = dominant, vibrant = vibrant)
            }
        } catch (_: Throwable) {
            null
        }
    }
}

