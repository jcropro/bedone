package app.ember.studio.device

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
class SpatializerProcessDeathDeviceTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun toggleSurvivesForceStopAndRelaunch_ifPresent() {
        val activity = composeRule.activity

        // Navigate to Equalizer
        composeRule.onNodeWithContentDescription(activity.getString(R.string.drawer_toggle_content_description)).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.drawer_equalizer)).performClick()

        // Ensure equalizer enabled
        val enable = EqualizerTestTags.ENABLE_SWITCH
        composeRule.onNodeWithTag(enable).performClick()
        composeRule.onNodeWithTag(enable).assertIsOn()

        // If the Spatializer switch exists, turn it on
        val switchTag = EqualizerTestTags.VIRTUALIZER_SWITCH
        val hasSwitch = composeRule.onAllNodes(hasTestTag(switchTag)).fetchSemanticsNodes().isNotEmpty()
        if (!hasSwitch) return
        composeRule.onNodeWithTag(switchTag).performClick()
        composeRule.onNodeWithTag(switchTag).assertIsOn()

        // Force-stop and relaunch
        DeviceProcessDeathUtils.forceStopApp()
        DeviceProcessDeathUtils.relaunchMainActivity()
        composeRule.waitForIdle()

        // Navigate to Equalizer again
        composeRule.onNodeWithContentDescription(activity.getString(R.string.drawer_toggle_content_description)).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.drawer_equalizer)).performClick()

        // Assert both equalizer and virtualization are ON
        composeRule.onNodeWithTag(enable).assertIsOn()
        composeRule.onNodeWithTag(switchTag).assertIsOn()
    }
}

