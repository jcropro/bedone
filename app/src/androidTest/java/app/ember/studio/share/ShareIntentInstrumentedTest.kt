package app.ember.studio.share

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.Intents.intended
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import app.ember.studio.MainActivity
import app.ember.studio.SongShareMessage
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ShareIntentInstrumentedTest {

    @Before fun setUp() { Intents.init() }
    @After fun tearDown() { Intents.release() }

    @Test
    fun share_with_stream_sends_chooser() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val method = MainActivity::class.java.getDeclaredMethod(
                "shareSongMessage",
                SongShareMessage::class.java,
                Uri::class.java
            )
            method.isAccessible = true
            method.invoke(activity, SongShareMessage("sub", "body"), Uri.parse("content://media/external/audio/media/42"))
        }
        intended(allOf(hasAction(Intent.ACTION_CHOOSER)))
        scenario.close()
    }

    @Test
    fun share_without_stream_sends_chooser() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val method = MainActivity::class.java.getDeclaredMethod(
                "shareSongMessage",
                SongShareMessage::class.java,
                Uri::class.java
            )
            method.isAccessible = true
            method.invoke(activity, SongShareMessage("sub", "body"), null)
        }
        intended(allOf(hasAction(Intent.ACTION_CHOOSER)))
        scenario.close()
    }
}

