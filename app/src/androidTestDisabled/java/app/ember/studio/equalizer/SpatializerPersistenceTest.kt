package app.ember.studio.equalizer

import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.EqualizerTestTags
import app.ember.studio.MainActivity
import app.ember.studio.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpatializerPersistenceTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun spatializerTogglePersistsAcrossRecreation_ifAvailable() {
        val activity = composeRule.activity
        composeRule.onNodeWithContentDescription(activity.getString(R.string.drawer_toggle_content_description)).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.drawer_equalizer)).performClick()
        val switchTag = EqualizerTestTags.VIRTUALIZER_SWITCH
        val hasSwitch = composeRule.onAllNodes(hasTestTag(switchTag)).fetchSemanticsNodes().isNotEmpty()
        if (!hasSwitch) return
        composeRule.onNodeWithTag(switchTag).performClick()
        composeRule.onNodeWithTag(switchTag).assertIsOn()
        composeRule.activity.runOnUiThread { composeRule.activity.recreate() }
        composeRule.waitForIdle()
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.drawer_toggle_content_description)).performClick()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.drawer_equalizer)).performClick()
        composeRule.onNodeWithTag(switchTag).assertIsOn()
    }
}

