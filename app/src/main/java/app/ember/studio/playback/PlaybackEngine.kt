package app.ember.studio.playback

import androidx.media3.common.Player
import androidx.media3.session.MediaSession

/**
 * Lightweight global holder to bridge the app-owned Player into the MediaSessionService
 * without introducing a heavy DI framework. The ViewModel sets the player; the service
 * observes and creates a MediaSession + notification around it.
 */
object PlaybackEngine {
    @Volatile
    var player: Player? = null
        private set

    @Volatile
    var session: MediaSession? = null
        internal set

    private val listeners = mutableSetOf<(Player) -> Unit>()

    @Synchronized
    fun setPlayer(p: Player) {
        player = p
        listeners.forEach { it.invoke(p) }
    }

    @Synchronized
    fun onPlayerAvailable(listener: (Player) -> Unit) {
        player?.let { existing ->
            listener(existing)
            return
        }
        listeners.add(listener)
    }

    @Synchronized
    fun clearListener(listener: (Player) -> Unit) {
        listeners.remove(listener)
    }
}

