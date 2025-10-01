package app.ember.studio.notifications

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import app.ember.studio.MainActivity
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.media3.session.MediaController
import app.ember.studio.testing.awaitMediaSession

@RunWith(AndroidJUnit4::class)
@LargeTest
class PlayerNotificationAdvancedActionsTest {
    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun fastForward_rewind_and_stop() {
        // Seed queue and start playback
        scenario.onActivity { activity ->
            try {
                val field = MainActivity::class.java.getDeclaredField("playerViewModel")
                field.isAccessible = true
                val vm = field.get(activity) as app.ember.studio.PlayerViewModel
                vm.playAllSongs()
            } catch (_: Throwable) { }
        }

        scenario.onActivity { activity ->
            val session = awaitMediaSession(activity)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try {
                controller.play()
                // Seek to 30s so rewind is meaningful
                controller.seekTo(30_000)
            } finally { controller.release() }
        }

        device.openNotification()

        fun clickByAnyLabel(vararg labels: String): Boolean {
            for (l in labels) {
                val obj = device.wait(Until.findObject(By.descContains(l)), 1200)
                    ?: device.wait(Until.findObject(By.textContains(l)), 800)
                if (obj != null) { obj.click(); return true }
            }
            return false
        }

        // Capture current position, tap Fast forward, expect ~+10s
        var before = 0L
        var after = 0L
        scenario.onActivity { activity ->
            val session = awaitMediaSession(activity)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try { before = controller.currentPosition } finally { controller.release() }
        }
        clickByAnyLabel("Fast forward", "Fast-forward")
        Thread.sleep(500)
        scenario.onActivity { activity ->
            val session = awaitMediaSession(activity)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try { after = controller.currentPosition } finally { controller.release() }
        }
        assertTrue("Expected fast forward to increase position by ~10s; before=$before after=$after",
            after >= before + 8_000 && after <= before + 15_000)

        // Tap Rewind, expect ~-10s
        clickByAnyLabel("Rewind")
        Thread.sleep(500)
        var afterRewind = 0L
        scenario.onActivity { activity ->
            val session = awaitMediaSession(activity)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try { afterRewind = controller.currentPosition } finally { controller.release() }
        }
        assertTrue("Expected rewind to decrease position by ~10s; after=$after afterRewind=$afterRewind",
            afterRewind <= after - 8_000)

        // Tap Stop, expect not playing
        clickByAnyLabel("Stop")
        Thread.sleep(400)
        var isPlaying = true
        scenario.onActivity { activity ->
            val session = awaitMediaSession(activity)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try { isPlaying = controller.playWhenReady && controller.isPlaying } finally { controller.release() }
        }
        assertTrue("Expected stop to cease playback", !isPlaying)
    }
}

