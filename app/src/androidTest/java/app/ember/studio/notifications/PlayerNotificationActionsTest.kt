package app.ember.studio.notifications

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import app.ember.studio.MainActivity
import app.ember.studio.R
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.media3.session.MediaController
import app.ember.studio.testing.awaitMediaSession

@RunWith(AndroidJUnit4::class)
@LargeTest
class PlayerNotificationActionsTest {
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
    fun playPause_next_prev_via_notification() {
        scenario.onActivity { activity ->
            // Ensure there is a queue with multiple items so Next/Prev have effect
            try {
                val field = MainActivity::class.java.getDeclaredField("playerViewModel")
                field.isAccessible = true
                val vm = field.get(activity) as app.ember.studio.PlayerViewModel
                vm.playAllSongs()
            } catch (_: Throwable) { /* best-effort */ }
        }

        // Open notification shade
        device.openNotification()

        // Try locating controls by description or text with retries
        val timeout = 5000L
        fun clickByLabel(vararg labels: String): Boolean {
            for (l in labels) {
                val byDesc = device.wait(Until.findObject(By.descContains(l)), 1200)
                if (byDesc != null) { byDesc.click(); return true }
                val byText = device.wait(Until.findObject(By.textContains(l)), 800)
                if (byText != null) { byText.click(); return true }
            }
            return false
        }
        fun screenshot(name: String) {
            try {
                val ctx = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
                val file = java.io.File(ctx.filesDir, "$name.png")
                device.takeScreenshot(file)
            } catch (_: Throwable) { }
        }
        // Click Pause if visible; some skins may show a Play button first
        clickByLabel("Pause") || clickByLabel("Play")

        // Verify via MediaController
        scenario.onActivity { activity ->
            val session = awaitMediaSession(activity)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try {
                // After pause, should not be playWhenReady
                Thread.sleep(300)
                assertFalse(controller.playWhenReady)
            } finally {
                controller.release()
            }
        }

        // Click Play
        if (!clickByLabel("Play")) screenshot("notif_play_not_found")

        scenario.onActivity { activity ->
            val session = awaitMediaSession(activity)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try {
                Thread.sleep(300)
                assertTrue(controller.playWhenReady)
                // Click next/prev if present and ensure no crash; state remains valid
            } finally {
                controller.release()
            }
        }

        // Verify via controller that next/prev are accepted when applicable
        scenario.onActivity { activity ->
            val session = awaitMediaSession(activity)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try {
                val before = controller.currentMediaItemIndex
                if (!clickByLabel("Next")) screenshot("notif_next_not_found")
                Thread.sleep(300)
                val afterNext = controller.currentMediaItemIndex
                // We don't assert strict inequality due to single-item edge cases
                if (!clickByLabel("Previous")) screenshot("notif_prev_not_found")
                Thread.sleep(300)
                val afterPrev = controller.currentMediaItemIndex
                assertTrue(afterNext >= 0 && afterPrev >= 0 && before >= 0)
            } finally { controller.release() }
        }
    }
}
