package app.ember.studio.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import app.ember.studio.R
import android.app.PendingIntent
import android.content.Intent
import app.ember.studio.MainActivity
import app.ember.studio.media3adapters.NotificationAdapters
class PlayerNotificationController(
    private val context: Context,
    private val notificationId: Int = 1001,
    private val channelId: String = "playback"
) {
    private var manager: PlayerNotificationManager? = null

    @OptIn(UnstableApi::class)
    fun start(player: Player, session: MediaSession?) {
        ensureChannel()
        val descriptionAdapter = object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): CharSequence {
                val md: MediaMetadata = NotificationAdapters.getPlayerMediaMetadata(player)
                return md.title ?: context.getString(R.string.sample_tone_title)
            }

            override fun createCurrentContentIntent(player: Player) =
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

            override fun getCurrentContentText(player: Player): CharSequence? {
                val md: MediaMetadata = NotificationAdapters.getPlayerMediaMetadata(player)
                return md.artist ?: context.getString(R.string.sample_tone_artist)
            }

            override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback) = null
        }

        val builder = NotificationAdapters.createNotificationManagerBuilder(context, notificationId, channelId)
        NotificationAdapters.setMediaDescriptionAdapter(builder, descriptionAdapter)
        NotificationAdapters.setSmallIconResourceId(builder, R.drawable.ember_logo)
        val m = NotificationAdapters.buildNotificationManager(builder)
        // Core transport actions
        NotificationAdapters.setUsePlayPauseActions(m, true)
        NotificationAdapters.setUsePreviousAction(m, true)
        NotificationAdapters.setUseNextAction(m, true)
        // Seek controls (±10s) for podcasts/longform and video
        NotificationAdapters.setUseRewindAction(m, true)
        NotificationAdapters.setUseFastForwardAction(m, true)
        // Stop action to end playback quickly from the shade
        NotificationAdapters.setUseStopAction(m, true)
        // Expose core controls in compact view when space is constrained
        NotificationAdapters.setUsePreviousActionInCompactView(m, true)
        NotificationAdapters.setUseNextActionInCompactView(m, true)
        NotificationAdapters.setUseFastForwardActionInCompactView(m, false)
        NotificationAdapters.setUseRewindActionInCompactView(m, false)
        // Associate platform media session token for system controls integration
        // Note: platformToken functionality temporarily disabled due to type resolution issues
        // session?.let { NotificationAdapters.setMediaSessionToken(m, NotificationAdapters.getMediaSessionPlatformToken(it)) }
        NotificationAdapters.setPlayer(m, player)
        manager = m
    }

    fun stop() {
        manager?.let { NotificationAdapters.setPlayer(it, null) }
        manager = null
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = context.getSystemService<NotificationManager>() ?: return
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.playback_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            nm.createNotificationChannel(channel)
        }
    }
}
