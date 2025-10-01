package app.ember.studio.sleep

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import app.ember.studio.R
import app.ember.studio.SleepTimerTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SleepTimerProcessDeathMessageTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun statusMessagePersistsAcrossActivityRecreation_andAutoDismissesAfterAcknowledgement() {
        val activity = composeRule.activity

        // Navigate to Sleep Timer
        val drawerCd = activity.getString(R.string.drawer_toggle_content_description)
        composeRule.onNodeWithContentDescription(drawerCd).performClick()
        val sleepLabel = activity.getString(R.string.drawer_sleep_timer)
        composeRule.onNodeWithText(sleepLabel).performClick()

        // Start a quick timer (5 min)
        val quickFiveTag = "${SleepTimerTestTags.QUICK_CHIP_PREFIX}5"
        composeRule.onNodeWithTag(quickFiveTag).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.sleep_timer_start)).performClick()

        // Status message should be visible
        kotlin.test.assertTrue(
            composeRule.onAllNodes(hasTestTag(SleepTimerTestTags.STATUS_MESSAGE)).fetchSemanticsNodes().isNotEmpty()
        )

        // Recreate activity (simulate process death approximation)
        composeRule.activity.runOnUiThread { composeRule.activity.recreate() }
        composeRule.waitForIdle()

        // Navigate back to Sleep Timer
        val newDrawerCd = composeRule.activity.getString(R.string.drawer_toggle_content_description)
        composeRule.onNodeWithContentDescription(newDrawerCd).performClick()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.drawer_sleep_timer)).performClick()

        // Status message persists
        kotlin.test.assertTrue(
            composeRule.onAllNodes(hasTestTag(SleepTimerTestTags.STATUS_MESSAGE)).fetchSemanticsNodes().isNotEmpty()
        )

        // Acknowledge message and wait for auto-dismiss
        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.onNodeWithTag(SleepTimerTestTags.STATUS_MESSAGE).performClick()
            composeRule.mainClock.advanceTimeBy(SleepTimerDefaults.statusMessageAutoDismissMillis.toLong())
            composeRule.waitForIdle()
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
        kotlin.test.assertTrue(
            composeRule.onAllNodes(hasTestTag(SleepTimerTestTags.STATUS_MESSAGE)).fetchSemanticsNodes().isEmpty()
        )
    }
}
