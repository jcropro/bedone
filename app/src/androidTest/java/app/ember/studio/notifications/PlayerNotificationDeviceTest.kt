package app.ember.studio.notifications

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import app.ember.studio.MainActivity
import app.ember.studio.R
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayerNotificationDeviceTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun notificationAppearsWhenPlaying() {
        val activity = composeRule.activity
        // Start playback via Play All
        composeRule.onNodeWithText(activity.getString(R.string.songs_play_all_button)).performClick()

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.openNotification()
        val appTitle = activity.getString(R.string.app_name)
        // Wait for any item that contains app title or sample title
        val sampleTitle = activity.getString(R.string.sample_tone_title)
        device.wait(Until.hasObject(By.textContains(appTitle).res("android:id/title")), 3000)
        // Accept either app title or track title present in shade
        val foundApp = device.findObject(By.textContains(appTitle)) != null
        val foundTitle = device.findObject(By.textContains(sampleTitle)) != null
        assert(foundApp || foundTitle)
    }
}

