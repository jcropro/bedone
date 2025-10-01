package app.ember.studio.media

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import app.ember.studio.MainActivity
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.media3.session.MediaController
import app.ember.studio.testing.awaitMediaSession
import app.ember.studio.testing.seedMultiItemQueue
import app.ember.studio.testing.takeScreenshot

@RunWith(AndroidJUnit4::class)
@LargeTest
class MediaSessionSmokeTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun mediaController_canControlPlayback() = runBlocking {
        scenario.onActivity { activity ->
            seedMultiItemQueue(activity)
            val session = awaitMediaSession(activity)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try {
                controller.play()
                Thread.sleep(300)
                assertTrue(controller.playWhenReady)
                takeScreenshot("media_session_playing")
                controller.pause()
                Thread.sleep(300)
                assertTrue(!controller.playWhenReady)
                takeScreenshot("media_session_paused")
            } finally {
                controller.release()
            }
        }
    }
}
