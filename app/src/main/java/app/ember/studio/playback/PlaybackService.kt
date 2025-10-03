package app.ember.studio.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import app.ember.studio.notifications.PlayerNotificationController
import app.ember.studio.media3adapters.PlaybackServiceAdapters
import app.ember.studio.R

/**
 * Foreground service hosting the Media3 MediaSession and playback notification.
 * It adopts the app-wide Player provided via [PlaybackEngine].
 */
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var notificationController: PlayerNotificationController? = null

    override fun onCreate() {
        super.onCreate()
        ensureChannel()
        // If a player already exists, initialize immediately; otherwise, wait for it.
        val adopt: (androidx.media3.common.Player) -> Unit = { player ->
            if (mediaSession == null) {
                mediaSession = PlaybackServiceAdapters.buildMediaSession(this, player)
                PlaybackEngine.session = mediaSession
                notificationController = PlayerNotificationController(this).also {
                    it.start(player, mediaSession)
                }
                // Move service to foreground with a minimal ongoing notification if needed.
                // PlayerNotificationManager will manage the real notification; post a stub to satisfy OS.
                tryStartForeground()
            }
        }
        PlaybackEngine.onPlayerAvailable(adopt)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        notificationController?.stop()
        notificationController = null
        PlaybackServiceAdapters.releaseMediaSession(mediaSession)
        mediaSession = null
        PlaybackEngine.session = null
        super.onDestroy()
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = getSystemService<NotificationManager>() ?: return
            val channel = NotificationChannel(
                DEFAULT_CHANNEL_ID,
                getString(R.string.notification_channel_playback),
                NotificationManager.IMPORTANCE_LOW
            )
            nm.createNotificationChannel(channel)
        }
    }

    private fun tryStartForeground() {
        if (Build.VERSION.SDK_INT >= 26) {
            val notification: Notification = Notification.Builder(this, DEFAULT_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title_playback))
                .setContentText("")
                .setSmallIcon(app.ember.studio.R.drawable.ember_logo)
                .setOngoing(true)
                .build()
            try {
                startForeground(DEFAULT_NOTIFICATION_ID, notification)
            } catch (_: Throwable) {
                // Ignore; PlayerNotificationManager will promote when it posts
            }
        }
    }

    companion object {
        private const val DEFAULT_CHANNEL_ID = "playback"
        private const val DEFAULT_NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = android.content.Intent(context, PlaybackService::class.java)
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}

