package app.ember.studio.media3adapters

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager

/**
 * Adapter functions for PlayerNotificationManager unstable API usage.
 * All unstable Media3 notification calls are centralized here to maintain lint compliance.
 */
object NotificationAdapters {

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getPlayerMediaMetadata(player: Player): MediaMetadata {
        return player.mediaMetadata
    }

    // Note: platformToken functionality temporarily disabled due to type resolution issues
    // @OptIn(UnstableApi::class)
    // @Suppress("UnsafeOptInUsageError")
    // fun getMediaSessionPlatformToken(session: MediaSession): Any {
    //     return session.platformToken
    // }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun createNotificationManagerBuilder(
        context: Context,
        notificationId: Int,
        channelId: String
    ): PlayerNotificationManager.Builder {
        return PlayerNotificationManager.Builder(context, notificationId, channelId)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setMediaDescriptionAdapter(
        builder: PlayerNotificationManager.Builder,
        adapter: PlayerNotificationManager.MediaDescriptionAdapter
    ) {
        builder.setMediaDescriptionAdapter(adapter)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setSmallIconResourceId(
        builder: PlayerNotificationManager.Builder,
        resourceId: Int
    ) {
        builder.setSmallIconResourceId(resourceId)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun buildNotificationManager(builder: PlayerNotificationManager.Builder): PlayerNotificationManager {
        return builder.build()
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setUsePlayPauseActions(manager: PlayerNotificationManager, enabled: Boolean) {
        manager.setUsePlayPauseActions(enabled)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setUsePreviousAction(manager: PlayerNotificationManager, enabled: Boolean) {
        manager.setUsePreviousAction(enabled)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setUseNextAction(manager: PlayerNotificationManager, enabled: Boolean) {
        manager.setUseNextAction(enabled)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setUseRewindAction(manager: PlayerNotificationManager, enabled: Boolean) {
        manager.setUseRewindAction(enabled)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setUseFastForwardAction(manager: PlayerNotificationManager, enabled: Boolean) {
        manager.setUseFastForwardAction(enabled)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setUseStopAction(manager: PlayerNotificationManager, enabled: Boolean) {
        manager.setUseStopAction(enabled)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setUsePreviousActionInCompactView(manager: PlayerNotificationManager, enabled: Boolean) {
        manager.setUsePreviousActionInCompactView(enabled)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setUseNextActionInCompactView(manager: PlayerNotificationManager, enabled: Boolean) {
        manager.setUseNextActionInCompactView(enabled)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setUseFastForwardActionInCompactView(manager: PlayerNotificationManager, enabled: Boolean) {
        manager.setUseFastForwardActionInCompactView(enabled)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setUseRewindActionInCompactView(manager: PlayerNotificationManager, enabled: Boolean) {
        manager.setUseRewindActionInCompactView(enabled)
    }

    // Note: setMediaSessionToken functionality temporarily disabled due to type resolution issues
    // @OptIn(UnstableApi::class)
    // @Suppress("UnsafeOptInUsageError")
    // fun setMediaSessionToken(manager: PlayerNotificationManager, token: Any) {
    //     @Suppress("UNCHECKED_CAST")
    //     manager.setMediaSessionToken(token as androidx.media3.session.MediaSession.Token)
    // }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setPlayer(manager: PlayerNotificationManager, player: Player?) {
        manager.setPlayer(player)
    }
}
