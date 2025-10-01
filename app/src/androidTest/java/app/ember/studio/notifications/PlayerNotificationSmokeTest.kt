package app.ember.studio.notifications

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayerNotificationSmokeTest {

    private lateinit var context: Context
    private lateinit var player: ExoPlayer
    private var session: MediaSession? = null
    private lateinit var controller: PlayerNotificationController

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        player = ExoPlayer.Builder(context).build()
        controller = PlayerNotificationController(context)
        session = MediaSession.Builder(context, player).build()
        // Seed a simple media item so metadata is non-empty
        val md = MediaMetadata.Builder().setTitle("Test Title").setArtist("Test Artist").build()
        val item = MediaItem.Builder().setMediaMetadata(md).setUri("file:///dev/null").build()
        player.setMediaItem(item)
        player.prepare()
    }

    @After
    fun tearDown() {
        controller.stop()
        session?.release()
        player.release()
    }

    @Test
    fun start_stop_smoke() {
        controller.start(player, session)
        // Simple sanity checks: able to toggle play/pause and seek to next/previous positions without crash
        player.playWhenReady = true
        assertEquals(true, player.playWhenReady)
        player.playWhenReady = false
        assertEquals(false, player.playWhenReady)
        // next/previous on single item should be no-ops
        player.seekToNext()
        player.seekToPrevious()
        assertNotNull(player.mediaMetadata)
        controller.stop()
    }
}

