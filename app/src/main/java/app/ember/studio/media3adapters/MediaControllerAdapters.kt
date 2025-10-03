package app.ember.studio.media3adapters

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController

/**
 * Adapter functions for MediaController unstable API usage.
 * All unstable Media3 calls are centralized here to maintain lint compliance.
 */
object MediaControllerAdapters {

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setShuffleModeEnabled(controller: MediaController, enabled: Boolean) {
        controller.shuffleModeEnabled = enabled
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setRepeatMode(controller: MediaController, mode: Int) {
        controller.repeatMode = mode
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setPlaybackSpeed(controller: MediaController, speed: Float) {
        controller.setPlaybackSpeed(speed)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun seekToNextMediaItem(controller: MediaController) {
        controller.seekToNextMediaItem()
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun seekToPreviousMediaItem(controller: MediaController) {
        controller.seekToPreviousMediaItem()
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun hasNextMediaItem(controller: MediaController): Boolean {
        return controller.hasNextMediaItem()
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun hasPreviousMediaItem(controller: MediaController): Boolean {
        return controller.hasPreviousMediaItem()
    }
}
