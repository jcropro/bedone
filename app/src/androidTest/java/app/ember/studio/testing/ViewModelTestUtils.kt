package app.ember.studio.testing

import app.ember.studio.PlayerViewModel
import app.ember.studio.playback.PlaybackEngine

fun forceClear(vm: PlayerViewModel) {
    try {
        val m = PlayerViewModel::class.java.getDeclaredMethod("onCleared")
        m.isAccessible = true
        m.invoke(vm)
    } catch (_: Throwable) {
        // ignore in tests
    }
}

fun awaitMediaSession(activity: Any, timeoutMs: Long = 7000L): androidx.media3.session.MediaSession {
    val deadline = System.currentTimeMillis() + timeoutMs
    while (System.currentTimeMillis() < deadline) {
        // Try activity field first for backward compatibility
        try {
            val field = activity::class.java.getDeclaredField("mediaSession")
            field.isAccessible = true
            val session = field.get(activity) as? androidx.media3.session.MediaSession
            if (session != null) return session
        } catch (_: Throwable) {
            // ignore
        }
        // Fallback to service-backed session
        PlaybackEngine.session?.let { return it }
        try { Thread.sleep(100) } catch (_: InterruptedException) { break }
    }
    val fallback = PlaybackEngine.session
    requireNotNull(fallback) { "MediaSession was not available within timeout" }
    return fallback
}
