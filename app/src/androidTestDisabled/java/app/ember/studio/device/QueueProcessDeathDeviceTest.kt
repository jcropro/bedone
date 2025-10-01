package app.ember.studio.device

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import app.ember.studio.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QueueProcessDeathDeviceTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun queueResumeSurvivesForceStopAndRelaunch() {
        val activity = composeRule.activity

        // Play all and go to next track
        composeRule.onNodeWithText(activity.getString(R.string.songs_play_all_button)).performClick()
        composeRule.onNodeWithContentDescription(activity.getString(R.string.mini_player_next_content_description)).performClick()

        val expectedTitle = "Ember Skyline"
        composeRule.onNodeWithText(expectedTitle).assertExists()

        // Force-stop and relaunch
        DeviceProcessDeathUtils.forceStopApp()
        DeviceProcessDeathUtils.relaunchMainActivity()
        composeRule.waitForIdle()

        // Assert still on the same track
        composeRule.onNodeWithText(expectedTitle).assertExists()
    }
}

