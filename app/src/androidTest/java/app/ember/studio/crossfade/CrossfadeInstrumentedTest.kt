package app.ember.studio.crossfade

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import app.ember.studio.MainActivity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import androidx.media3.common.Player

@RunWith(AndroidJUnit4::class)
@LargeTest
class CrossfadeInstrumentedTest {

    @Test
    fun crossfade_fades_out_and_in_around_transition() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val field = MainActivity::class.java.getDeclaredField("playerViewModel")
            field.isAccessible = true
            val vm = field.get(activity) as app.ember.studio.PlayerViewModel
            val exo = vm.exoPlayerOrNull()!!

            // Enable short crossfade and start playback of all songs
            vm.setCrossfadeMs(3000)
            vm.playAllSongs()

            // Wait until player is ready with a valid duration
            waitUntil(timeoutMs = 10_000) {
                exo.playbackState == Player.STATE_READY && exo.duration > 0
            }
            val dur = exo.duration
            // Seek into last 2 seconds to enter fade-out window
            exo.seekTo((dur - 2_000L).coerceAtLeast(0L))

            // Expect volume to reduce below 1.0 within the fade window
            waitUntil(timeoutMs = 3_000) { exo.volume < 0.99f }
            assertTrue("Expected volume to start fading out", exo.volume < 0.99f)

            // Wait for transition to next item
            val startIndex = exo.currentMediaItemIndex
            waitUntil(timeoutMs = 8_000) { exo.currentMediaItemIndex != startIndex }

            // Shortly after transition, expect volume to be < 1.0 during fade-in window
            waitUntil(timeoutMs = 1_500) { exo.volume < 0.99f }
            assertTrue("Expected volume to be mid fade-in (<1)", exo.volume < 0.99f)

            // Disable crossfade; volume should restore to 1 fairly quickly
            vm.setCrossfadeMs(0)
            waitUntil(timeoutMs = 2_000) { exo.volume >= 0.99f }
            assertTrue("Expected volume to restore to 1 when crossfade off", exo.volume >= 0.99f)
        }
        scenario.close()
    }

    private fun waitUntil(timeoutMs: Long, check: () -> Boolean) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (check()) return
            try { Thread.sleep(50) } catch (_: InterruptedException) { break }
        }
    }
}

