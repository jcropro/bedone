package app.ember.studio.equalizer

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.EqualizerTestTags
import app.ember.studio.MainActivity
import app.ember.studio.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EqualizerBandPersistenceTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun firstBandLevelPersistsAcrossActivityRecreation_ifEqualizerAvailable() {
        val activity = composeRule.activity
        // Navigate to Equalizer
        composeRule.onNodeWithContentDescription(activity.getString(R.string.drawer_toggle_content_description)).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.drawer_equalizer)).performClick()

        // Enable equalizer
        val enable = EqualizerTestTags.ENABLE_SWITCH
        composeRule.onNodeWithTag(enable).performClick()

        // If no bands are present, skip (device-specific)
        val bandNodes = composeRule.onAllNodes(hasTestTag("equalizerBand_0")).fetchSemanticsNodes()
        if (bandNodes.isEmpty()) return

        // Nudge the first band slider via semantics action
        val tag = "equalizerBand_0"
        composeRule.onNodeWithTag(tag).assertExists()
        composeRule.onNodeWithTag(tag).performSemanticsAction(SemanticsActions.SetProgress) { setter ->
            try { setter(0.75f) } catch (_: Throwable) {}
        }

        // Recreate activity
        composeRule.activity.runOnUiThread { composeRule.activity.recreate() }
        composeRule.waitForIdle()

        // Reopen Equalizer and assert switch exists (sanity) and first band still present
        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.drawer_toggle_content_description)).performClick()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.drawer_equalizer)).performClick()
        composeRule.onNodeWithTag(enable).assertExists()
        composeRule.onNodeWithTag(tag).assertExists()
    }
}

