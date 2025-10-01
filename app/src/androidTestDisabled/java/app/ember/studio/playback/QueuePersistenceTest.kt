package app.ember.studio.playback

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import app.ember.studio.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QueuePersistenceTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun queuePersistsAcrossRecreation() {
        val activity = composeRule.activity
        composeRule.onNodeWithText(activity.getString(R.string.songs_play_all_button)).performClick()
        val expectedTitle = "Ember Skyline"
        composeRule.onNodeWithText(expectedTitle).assertExists()
        composeRule.activity.runOnUiThread { composeRule.activity.recreate() }
        composeRule.waitForIdle()
        composeRule.onNodeWithText(expectedTitle).assertExists()
    }
}

