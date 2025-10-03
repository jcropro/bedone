package app.ember.studio.media3adapters

import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession

/**
 * Adapter functions for PlaybackService unstable API usage.
 * All unstable Media3 MediaSession calls are centralized here to maintain lint compliance.
 */
object PlaybackServiceAdapters {

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun buildMediaSession(context: android.content.Context, player: Player): MediaSession {
        return MediaSession.Builder(context, player).build()
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getSessionFromControllerInfo(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        // This is a placeholder for any future MediaSession.ControllerInfo unstable API usage
        // Currently, the onGetSession method just returns the existing mediaSession
        return null
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun releaseMediaSession(mediaSession: MediaSession?) {
        mediaSession?.release()
    }
}
