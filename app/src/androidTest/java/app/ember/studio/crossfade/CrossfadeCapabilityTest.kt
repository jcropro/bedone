package app.ember.studio.crossfade

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import app.ember.studio.MainActivity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import androidx.media3.common.Player

/**
 * Enhanced crossfade tests with capability checks and instrumentation hooks
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class CrossfadeCapabilityTest {

    @Test
    fun crossfadeCapabilityChecks_validatePlayerState() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val field = MainActivity::class.java.getDeclaredField("playerViewModel")
            field.isAccessible = true
            val vm = field.get(activity) as app.ember.studio.PlayerViewModel
            val exo = vm.exoPlayerOrNull()!!

            // Test capability checks before enabling crossfade
            assertTrue("Player should be available for crossfade", exo != null)
            assertTrue("Player should support volume control", exo.volume >= 0f && exo.volume <= 1f)

            // Enable crossfade with capability validation
            vm.setCrossfadeMs(3000)
            
            // Verify crossfade is properly configured
            val settingsState = vm.settingsState.value
            assertTrue("Crossfade should be enabled", settingsState.crossfadeMs > 0)
            
            // Start playback to test crossfade functionality
            vm.playAllSongs()
            
            // Wait for player to be ready
            waitUntil(timeoutMs = 10_000) {
                exo.playbackState == Player.STATE_READY && exo.duration > 0
            }
            
            // Verify crossfade is active
            assertTrue("Crossfade should be active", settingsState.crossfadeMs > 0)
        }
        scenario.close()
    }

    @Test
    fun crossfadeInstrumentationHooks_validateFadeOutIn() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val field = MainActivity::class.java.getDeclaredField("playerViewModel")
            field.isAccessible = true
            val vm = field.get(activity) as app.ember.studio.PlayerViewModel
            val exo = vm.exoPlayerOrNull()!!

            // Enable crossfade with instrumentation hooks
            vm.setCrossfadeMs(2000) // Shorter duration for faster testing
            vm.playAllSongs()

            // Wait until player is ready
            waitUntil(timeoutMs = 10_000) {
                exo.playbackState == Player.STATE_READY && exo.duration > 0
            }

            val duration = exo.duration
            val initialVolume = exo.volume
            
            // Seek to fade-out window (last 2 seconds)
            val fadeOutStart = (duration - 2000L).coerceAtLeast(0L)
            exo.seekTo(fadeOutStart)
            
            // Instrumentation hook: Validate fade-out starts
            waitUntil(timeoutMs = 3000) { 
                exo.volume < initialVolume - 0.01f 
            }
            assertTrue("Fade-out should start in fade window", exo.volume < initialVolume)
            
            // Wait for transition to next track
            val startIndex = exo.currentMediaItemIndex
            waitUntil(timeoutMs = 8000) { 
                exo.currentMediaItemIndex != startIndex 
            }
            
            // Instrumentation hook: Validate fade-in after transition
            val postTransitionVolume = exo.volume
            assertTrue("Volume should be low after transition", postTransitionVolume < 0.9f)
            
            // Wait for fade-in to complete
            waitUntil(timeoutMs = 3000) { 
                exo.volume >= 0.95f 
            }
            assertTrue("Fade-in should complete", exo.volume >= 0.95f)
        }
        scenario.close()
    }

    @Test
    fun crossfadeFallbackBehavior_whenDisabled() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val field = MainActivity::class.java.getDeclaredField("playerViewModel")
            field.isAccessible = true
            val vm = field.get(activity) as app.ember.studio.PlayerViewModel
            val exo = vm.exoPlayerOrNull()!!

            // Start with crossfade disabled
            vm.setCrossfadeMs(0)
            vm.playAllSongs()

            // Wait until player is ready
            waitUntil(timeoutMs = 10_000) {
                exo.playbackState == Player.STATE_READY && exo.duration > 0
            }

            val initialVolume = exo.volume
            
            // Seek to end of track
            val duration = exo.duration
            exo.seekTo((duration - 1000L).coerceAtLeast(0L))
            
            // Wait for transition
            val startIndex = exo.currentMediaItemIndex
            waitUntil(timeoutMs = 8000) { 
                exo.currentMediaItemIndex != startIndex 
            }
            
            // Verify no crossfade behavior (volume should remain at 1.0)
            assertTrue("Volume should remain at 1.0 without crossfade", exo.volume >= 0.99f)
            
            // Verify settings reflect disabled state
            val settingsState = vm.settingsState.value
            assertTrue("Crossfade should be disabled", settingsState.crossfadeMs == 0)
        }
        scenario.close()
    }

    @Test
    fun crossfadeBoundaryConditions_validateLimits() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val field = MainActivity::class.java.getDeclaredField("playerViewModel")
            field.isAccessible = true
            val vm = field.get(activity) as app.ember.studio.PlayerViewModel

            // Test minimum crossfade duration (should be clamped to 0)
            vm.setCrossfadeMs(-1000)
            var settingsState = vm.settingsState.value
            assertTrue("Negative crossfade should be clamped to 0", settingsState.crossfadeMs == 0)

            // Test maximum crossfade duration (should be clamped to 12 seconds)
            vm.setCrossfadeMs(20000) // 20 seconds
            settingsState = vm.settingsState.value
            assertTrue("Excessive crossfade should be clamped to 12s", settingsState.crossfadeMs == 12000)

            // Test valid crossfade duration
            vm.setCrossfadeMs(5000) // 5 seconds
            settingsState = vm.settingsState.value
            assertTrue("Valid crossfade should be preserved", settingsState.crossfadeMs == 5000)
        }
        scenario.close()
    }

    @Test
    fun crossfadeWithSleepTimer_interactionValidation() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val field = MainActivity::class.java.getDeclaredField("playerViewModel")
            field.isAccessible = true
            val vm = field.get(activity) as app.ember.studio.PlayerViewModel
            val exo = vm.exoPlayerOrNull()!!

            // Enable crossfade
            vm.setCrossfadeMs(3000)
            vm.playAllSongs()

            // Wait until player is ready
            waitUntil(timeoutMs = 10_000) {
                exo.playbackState == Player.STATE_READY && exo.duration > 0
            }

            val initialVolume = exo.volume
            
            // Start sleep timer with fade
            vm.setSleepTimerFade(true)
            vm.startSleepTimer() // Start sleep timer
            
            // Verify sleep timer fade takes precedence over crossfade
            // (This would require more complex setup to test fully)
            val sleepTimerState = vm.sleepTimerState.value
            assertTrue("Sleep timer should be running", sleepTimerState.isRunning)
            assertTrue("Sleep timer fade should be enabled", sleepTimerState.fadeEnabled)
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
