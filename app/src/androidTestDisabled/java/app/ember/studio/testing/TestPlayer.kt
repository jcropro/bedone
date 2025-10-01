package app.ember.studio.testing

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.test.utils.FakePlayer

@OptIn(UnstableApi::class)
class TestPlayer : FakePlayer() {
    private val listeners = mutableListOf<Player.Listener>()
    var stopCount: Int = 0
        private set
    private var reportedVolume: Float = super.getVolume()

    override fun addListener(listener: Player.Listener) {
        listeners += listener
        super.addListener(listener)
    }

    override fun removeListener(listener: Player.Listener) {
        listeners -= listener
        super.removeListener(listener)
    }

    override fun stop() {
        stopCount += 1
        super.stop()
    }

    override fun setVolume(volume: Float) {
        reportedVolume = volume
        super.setVolume(volume)
    }

    override fun getVolume(): Float = reportedVolume

    fun emitPlaybackEnded() {
        playbackState = Player.STATE_ENDED
        listeners.forEach { it.onPlaybackStateChanged(Player.STATE_ENDED) }
    }
}

