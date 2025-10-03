package app.ember.studio.media3adapters

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession

/**
 * Adapter functions for MediaSession unstable API usage.
 * All unstable Media3 calls are centralized here to maintain lint compliance.
 */
object MediaSessionAdapters {

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun buildAsync(builder: androidx.media3.session.MediaController.Builder): com.google.common.util.concurrent.ListenableFuture<androidx.media3.session.MediaController> {
        return builder.buildAsync()
    }
}
