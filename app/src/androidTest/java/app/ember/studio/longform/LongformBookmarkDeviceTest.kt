package app.ember.studio.longform

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import app.ember.studio.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs
import androidx.media3.session.MediaController

@RunWith(AndroidJUnit4::class)
@LargeTest
class LongformBookmarkDeviceTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun resumeBookmark_seeksAndStartsPlayback() {
        scenario.onActivity { activity ->
            // Reflectively grab ViewModel and MediaSession like other tests do
            val vmField = MainActivity::class.java.getDeclaredField("playerViewModel").apply { isAccessible = true }
            val vm = vmField.get(activity)
            val mediaSessionField = MainActivity::class.java.getDeclaredField("mediaSession").apply { isAccessible = true }
            val session = mediaSessionField.get(activity) as androidx.media3.session.MediaSession

            // Play all, set a bookmark at 15s, then move away and pause
            val vmClass = vm::class.java
            vmClass.getMethod("playAllSongs").invoke(vm)
            Thread.sleep(500)
            vmClass.getMethod("seekTo", java.lang.Long.TYPE).invoke(vm, 15_000L)
            vmClass.getMethod("addLongformBookmarkHere").invoke(vm)
            vmClass.getMethod("seekTo", java.lang.Long.TYPE).invoke(vm, 30_000L)
            vmClass.getMethod("togglePlayPause").invoke(vm) // pause if playing

            // Resume from bookmark and verify
            vmClass.getMethod("resumeFromLongformBookmark").invoke(vm)
            Thread.sleep(600)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try {
                val pos = controller.currentPosition
                val playing = controller.playWhenReady
                assert(playing)
                // Allow small tolerance
                assert(abs(pos - 15_000L) < 2_500L)
            } finally {
                controller.release()
            }
        }
    }
}

