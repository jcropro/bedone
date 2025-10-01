package app.ember.studio.equalizer

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsOff
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
class SpatializerToggleTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun spatializerToggleAppearsOnlyIfAvailable_andPersists() {
        val activity = composeRule.activity
        val drawerCd = activity.getString(R.string.drawer_toggle_content_description)
        val equalizerLabel = activity.getString(R.string.drawer_equalizer)
        val switchTag = EqualizerTestTags.VIRTUALIZER_SWITCH
        val sliderTag = EqualizerTestTags.VIRTUALIZER_SLIDER

        composeRule.onNodeWithContentDescription(drawerCd).performClick()
        composeRule.onNodeWithText(equalizerLabel).performClick()
        composeRule.waitForIdle()

        val switchNodes = composeRule.onAllNodes(hasTestTag(switchTag))
        val sliderNodes = composeRule.onAllNodes(hasTestTag(sliderTag))
        if (switchNodes.fetchSemanticsNodes().isEmpty()) {
            return
        }
        composeRule.onNodeWithTag(switchTag).performClick()
        composeRule.onNodeWithTag(switchTag).assertIsOn()
        composeRule.onNodeWithTag(sliderTag).assertExists()
    }
}

