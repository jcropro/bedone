package app.ember.studio.media

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import app.ember.studio.testing.awaitMediaSession
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.media3.session.MediaController
import androidx.media3.common.Player
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class MediaControllerShuffleRepeatTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before fun setUp() { scenario = ActivityScenario.launch(MainActivity::class.java) }
    @After fun tearDown() { scenario.close() }

    @Test
    fun toggleShuffle_and_cycleRepeat() {
        scenario.onActivity { activity ->
            val session = awaitMediaSession(activity)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try {
                // Shuffle toggle
                controller.setShuffleModeEnabled(true)
                Thread.sleep(150)
                assertEquals(true, controller.shuffleModeEnabled)
                controller.setShuffleModeEnabled(false)
                Thread.sleep(150)
                assertEquals(false, controller.shuffleModeEnabled)

                // Repeat cycle OFF -> ALL -> ONE -> OFF
                controller.setRepeatMode(Player.REPEAT_MODE_ALL)
                Thread.sleep(150)
                assertEquals(Player.REPEAT_MODE_ALL, controller.repeatMode)
                controller.setRepeatMode(Player.REPEAT_MODE_ONE)
                Thread.sleep(150)
                assertEquals(Player.REPEAT_MODE_ONE, controller.repeatMode)
                controller.setRepeatMode(Player.REPEAT_MODE_OFF)
                Thread.sleep(150)
                assertEquals(Player.REPEAT_MODE_OFF, controller.repeatMode)
            } finally {
                controller.release()
            }
        }
    }
}

