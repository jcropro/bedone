package app.ember.studio.art

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.annotation.ColorInt
import androidx.palette.graphics.Palette

object ArtworkColor {
    @ColorInt
    fun computeDominantColor(context: Context, mediaUri: String?): Int? {
        if (mediaUri.isNullOrEmpty()) return null
        val mmr = MediaMetadataRetriever()
        return try {
            mmr.setDataSource(context, android.net.Uri.parse(mediaUri))
            val art = mmr.embeddedPicture ?: return null
            val bmp = BitmapFactory.decodeByteArray(art, 0, art.size) ?: return null
            val palette = Palette.from(bmp).clearFilters().generate()
            palette.getVibrantColor(palette.getDominantColor(0))
        } catch (_: Throwable) {
            null
        } finally {
            try { mmr.release() } catch (_: Throwable) {}
        }
    }
}

