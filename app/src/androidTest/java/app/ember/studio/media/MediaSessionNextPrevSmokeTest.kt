package app.ember.studio.media

import androidx.media3.session.MediaController
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import kotlinx.coroutines.runBlocking
import app.ember.studio.testing.awaitMediaSession
import app.ember.studio.testing.seedMultiItemQueue
import app.ember.studio.testing.takeScreenshot
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaSessionNextPrevSmokeTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() { scenario = ActivityScenario.launch(MainActivity::class.java) }

    @After
    fun tearDown() { scenario.close() }

    @Test
    fun controller_canInvokeNextPrev() = runBlocking {
        scenario.onActivity { activity ->
            seedMultiItemQueue(activity)
            val session = awaitMediaSession(activity)
            val controller = MediaController.Builder(activity, session.token).buildAsync().get()
            try {
                takeScreenshot("before_next_prev")
                controller.seekToNextMediaItem()
                takeScreenshot("after_next")
                controller.seekToPreviousMediaItem()
                takeScreenshot("after_prev")
            } finally {
                controller.release()
            }
        }
    }
}
