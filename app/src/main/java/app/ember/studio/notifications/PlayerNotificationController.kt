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
import androidx.annotation.OptIn as AndroidOptIn
import androidx.media3.ui.PlayerNotificationManager
import app.ember.studio.R
import android.app.PendingIntent
import android.content.Intent
import app.ember.studio.MainActivity
@AndroidOptIn(UnstableApi::class)
@UnstableApi
class PlayerNotificationController(
    private val context: Context,
    private val notificationId: Int = 1001,
    private val channelId: String = "playback"
) {
    private var manager: PlayerNotificationManager? = null

    fun start(player: Player, session: MediaSession?) {
        ensureChannel()
        val descriptionAdapter = object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): CharSequence {
                val md: MediaMetadata = player.mediaMetadata
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
                val md: MediaMetadata = player.mediaMetadata
                return md.artist ?: context.getString(R.string.sample_tone_artist)
            }

            override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback) = null
        }

        val m = PlayerNotificationManager.Builder(context, notificationId, channelId)
            .setMediaDescriptionAdapter(descriptionAdapter)
            .setSmallIconResourceId(R.drawable.ic_launcher_foreground)
            .build()
        // Core transport actions
        m.setUsePlayPauseActions(true)
        m.setUsePreviousAction(true)
        m.setUseNextAction(true)
        // Seek controls (±10s) for podcasts/longform and video
        m.setUseRewindAction(true)
        m.setUseFastForwardAction(true)
        // Stop action to end playback quickly from the shade
        m.setUseStopAction(true)
        // Expose core controls in compact view when space is constrained
        m.setUsePreviousActionInCompactView(true)
        m.setUseNextActionInCompactView(true)
        m.setUseFastForwardActionInCompactView(false)
        m.setUseRewindActionInCompactView(false)
        // Associate platform media session token for system controls integration
        session?.let { m.setMediaSessionToken(it.platformToken) }
        m.setPlayer(player)
        manager = m
    }

    fun stop() {
        manager?.setPlayer(null)
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
