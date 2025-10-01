package app.ember.studio.media

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import app.ember.studio.testing.awaitMediaSession
import kotlin.math.abs
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.media3.session.MediaController

@RunWith(AndroidJUnit4::class)
class MediaControllerSpeedTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before fun setUp() { scenario = ActivityScenario.launch(MainActivity::class.java) }
    @After fun tearDown() { scenario.close() }

    @Test
    fun controller_canSetPlaybackSpeed() {
        scenario.onActivity { activity ->
            val session = awaitMediaSession(activity)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try {
                val target = 1.5f
                controller.setPlaybackSpeed(target)
                Thread.sleep(200)
                val actual = controller.playbackParameters.speed
                assert(abs(actual - target) < 0.01f)
            } finally {
                controller.release()
            }
        }
    }
}

