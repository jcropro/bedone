package app.ember.studio.sleep

import android.text.format.DateFormat
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import app.ember.studio.R
import app.ember.studio.SleepTimerScreen
import app.ember.studio.SleepTimerTestTags
import java.util.Calendar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.runner.RunWith
import kotlinx.coroutines.flow.MutableStateFlow

@RunWith(AndroidJUnit4::class)
class SleepTimerScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun quickChipSelectionInvokesCallbackAndUpdatesSelection() {
        var selectedQuick: Int? = null
        composeRule.setContent {
            var uiState by remember {
                mutableStateOf(
                    SleepTimerUiState(
                        quickDurationsMinutes = listOf(5, 10)
                    )
                )
            }
            MaterialTheme {
                SleepTimerScreen(
                    state = uiState,
                    onQuickDurationSelected = { minutes ->
                        selectedQuick = minutes
                        uiState = uiState.copy(
                            selectedQuickDurationMinutes = minutes,
                            customHoursInput = "",
                            customMinutesInput = ""
                        )
                    },
                    onCustomHoursChanged = { },
                    onCustomMinutesChanged = { },
                    onFadeToggle = { },
                    onEndActionSelected = { },
                    onStart = {},
                    onCancel = {},
                    onMessageDismiss = {},
                )
            }
        }

        composeRule.onNodeWithTag("${SleepTimerTestTags.QUICK_CHIP_PREFIX}5").performClick()
        composeRule.waitForIdle()

        assertEquals(5, selectedQuick)
        composeRule.onNodeWithTag("${SleepTimerTestTags.QUICK_CHIP_PREFIX}5").assertIsSelected()
    }

    @Test
    fun customInputsPropagateChanges() {
        var hoursInput: String? = null
        var minutesInput: String? = null
        composeRule.setContent {
            var uiState by remember { mutableStateOf(SleepTimerUiState()) }
            MaterialTheme {
                SleepTimerScreen(
                    state = uiState,
                    onQuickDurationSelected = { },
                    onCustomHoursChanged = { value ->
                        hoursInput = value
                        uiState = uiState.copy(
                            selectedQuickDurationMinutes = null,
                            customHoursInput = value
                        )
                    },
                    onCustomMinutesChanged = { value ->
                        minutesInput = value
                        uiState = uiState.copy(
                            selectedQuickDurationMinutes = null,
                            customMinutesInput = value
                        )
                    },
                    onFadeToggle = { },
                    onEndActionSelected = { },
                    onStart = {},
                    onCancel = {},
                    onMessageDismiss = {},
                )
            }
        }

        composeRule.onNodeWithTag(SleepTimerTestTags.HOURS_FIELD).performClick()
        composeRule.onNodeWithTag(SleepTimerTestTags.HOURS_FIELD).performTextInput("2")
        composeRule.onNodeWithTag(SleepTimerTestTags.MINUTES_FIELD).performClick()
        composeRule.onNodeWithTag(SleepTimerTestTags.MINUTES_FIELD).performTextInput("30")
        composeRule.waitForIdle()

        assertEquals("2", hoursInput)
        assertEquals("30", minutesInput)
    }

    @Test
    fun fadeToggleCallsCallback() {
        var fadeEnabled = false
        composeRule.setContent {
            MaterialTheme {
                SleepTimerScreen(
                    state = SleepTimerUiState(fadeEnabled = fadeEnabled),
                    onQuickDurationSelected = { },
                    onCustomHoursChanged = { },
                    onCustomMinutesChanged = { },
                    onFadeToggle = { toggled -> fadeEnabled = toggled },
                    onEndActionSelected = { },
                    onStart = {},
                    onCancel = {},
                    onMessageDismiss = {},
                )
            }
        }

        composeRule.onNodeWithTag(SleepTimerTestTags.FADE_SWITCH).assertIsOff()
        composeRule.onNodeWithTag(SleepTimerTestTags.FADE_SWITCH).performClick()
        composeRule.waitForIdle()

        assertTrue(fadeEnabled)
        composeRule.onNodeWithTag(SleepTimerTestTags.FADE_SWITCH).assertIsOn()
    }

    @Test
    fun endActionSelectionInvokesCallback() {
        var selectedAction: SleepTimerEndAction? = null
        composeRule.setContent {
            var uiState by remember { mutableStateOf(SleepTimerUiState()) }
            MaterialTheme {
                SleepTimerScreen(
                    state = uiState,
                    onQuickDurationSelected = { },
                    onCustomHoursChanged = { },
                    onCustomMinutesChanged = { },
                    onFadeToggle = { },
                    onEndActionSelected = { action ->
                        selectedAction = action
                        uiState = uiState.copy(endAction = action)
                    },
                    onStart = {},
                    onCancel = {},
                    onMessageDismiss = {},
                )
            }
        }

        val stopTag = "${SleepTimerTestTags.END_ACTION_PREFIX}${SleepTimerEndAction.StopPlayback.name}"
        composeRule.onNodeWithTag(stopTag).performClick()
        composeRule.waitForIdle()

        assertEquals(SleepTimerEndAction.StopPlayback, selectedAction)
    }

    @Test
    fun countdownVisibleWhenRunning() {
        composeRule.setContent {
            MaterialTheme {
                SleepTimerScreen(
                    state = SleepTimerUiState(
                        isRunning = true,
                        remainingMillis = 60_000L
                    ),
                    onQuickDurationSelected = { },
                    onCustomHoursChanged = { },
                    onCustomMinutesChanged = { },
                    onFadeToggle = { },
                    onEndActionSelected = { },
                    onStart = {},
                    onCancel = {},
                    onMessageDismiss = {},
                )
            }
        }

        kotlin.test.assertTrue(
            composeRule.onAllNodes(hasTestTag(SleepTimerTestTags.COUNTDOWN)).fetchSemanticsNodes().isNotEmpty()
        )
    }

    @Test
    fun scheduleSummaryReflectsLocalizedTimeAndActionLabel() {
        val endTimestamp = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2025)
            set(Calendar.MONTH, Calendar.MARCH)
            set(Calendar.DAY_OF_MONTH, 15)
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val expectedTime = DateFormat.getTimeFormat(composeRule.activity).format(endTimestamp)
        val expectedActionLabel = composeRule.activity.getString(SleepTimerEndAction.StopPlayback.labelRes)
        val expectedSummary = composeRule.activity.getString(
            R.string.sleep_timer_schedule_summary,
            expectedTime,
            expectedActionLabel
        )

        composeRule.setContent {
            MaterialTheme {
                SleepTimerScreen(
                    state = SleepTimerUiState(
                        isRunning = true,
                        remainingMillis = 45 * 60_000L,
                        configuredDurationMillis = 45 * 60_000L,
                        scheduledEndTimestampMillis = endTimestamp,
                        endAction = SleepTimerEndAction.StopPlayback
                    ),
                    onQuickDurationSelected = { },
                    onCustomHoursChanged = { },
                    onCustomMinutesChanged = { },
                    onFadeToggle = { },
                    onEndActionSelected = { },
                    onStart = {},
                    onCancel = {},
                    onMessageDismiss = {},
                )
            }
        }

        kotlin.test.assertTrue(
            composeRule.onAllNodes(androidx.compose.ui.test.hasText(expectedSummary), useUnmergedTree = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        )
    }

    @Test
    fun resumedTimerRestoresFadeAndEndActionState() {
        lateinit var setState: (SleepTimerUiState) -> Unit

        composeRule.setContent {
            var uiState by remember { mutableStateOf(SleepTimerUiState()) }
            DisposableEffect(Unit) {
                setState = { newState -> uiState = newState }
                onDispose { }
            }
            MaterialTheme {
                SleepTimerScreen(
                    state = uiState,
                    onQuickDurationSelected = { },
                    onCustomHoursChanged = { },
                    onCustomMinutesChanged = { },
                    onFadeToggle = { toggled ->
                        uiState = uiState.copy(fadeEnabled = toggled)
                    },
                    onEndActionSelected = { action ->
                        uiState = uiState.copy(endAction = action)
                    },
                    onStart = {},
                    onCancel = {},
                    onMessageDismiss = {},
                )
            }
        }

        composeRule.waitForIdle()

        val resumedAction = SleepTimerEndAction.StopAfterQueue
        val resumedEndTag = "${SleepTimerTestTags.END_ACTION_PREFIX}${resumedAction.name}"
        val pauseEndTag = "${SleepTimerTestTags.END_ACTION_PREFIX}${SleepTimerEndAction.PausePlayback.name}"
        val endTimestamp = System.currentTimeMillis() + 30 * 60_000L

        composeRule.runOnIdle {
            setState(
                SleepTimerUiState(
                    isRunning = true,
                    remainingMillis = 25 * 60_000L,
                    configuredDurationMillis = 45 * 60_000L,
                    scheduledEndTimestampMillis = endTimestamp,
                    fadeEnabled = true,
                    endAction = resumedAction
                )
            )
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag(SleepTimerTestTags.FADE_SWITCH).assertIsOn()

        composeRule.onNode(
            hasAnyAncestor(hasTestTag(resumedEndTag)) and isSelectable()
        ).assertIsOn()

        composeRule.onNode(
            hasAnyAncestor(hasTestTag(pauseEndTag)) and isSelectable()
        ).assertIsOff()
    }

    @Test
    fun resumedTimerRestoresCustomDurationInputsWithoutQuickSelection() {
        val restoredHours = "1"
        val restoredMinutes = "25"
        val restoredDurationMinutes = 85
        val restoredDurationMillis = restoredDurationMinutes * 60_000L
        val endTimestamp = System.currentTimeMillis() + restoredDurationMillis

        lateinit var setState: (SleepTimerUiState) -> Unit

        composeRule.setContent {
            var uiState by remember { mutableStateOf(SleepTimerUiState()) }
            DisposableEffect(Unit) {
                setState = { newState -> uiState = newState }
                onDispose { }
            }
            MaterialTheme {
                SleepTimerScreen(
                    state = uiState,
                    onQuickDurationSelected = { minutes ->
                        uiState = uiState.copy(
                            selectedQuickDurationMinutes = minutes,
                            customHoursInput = "",
                            customMinutesInput = ""
                        )
                    },
                    onCustomHoursChanged = { value ->
                        uiState = uiState.copy(
                            selectedQuickDurationMinutes = null,
                            customHoursInput = value
                        )
                    },
                    onCustomMinutesChanged = { value ->
                        uiState = uiState.copy(
                            selectedQuickDurationMinutes = null,
                            customMinutesInput = value
                        )
                    },
                    onFadeToggle = { toggled ->
                        uiState = uiState.copy(fadeEnabled = toggled)
                    },
                    onEndActionSelected = { action ->
                        uiState = uiState.copy(endAction = action)
                    },
                    onStart = {},
                    onCancel = {},
                    onMessageDismiss = {},
                )
            }
        }

        composeRule.waitForIdle()

        val quickFiveTag = "${SleepTimerTestTags.QUICK_CHIP_PREFIX}${SleepTimerDefaults.quickDurationsMinutes.first()}"

        composeRule.runOnIdle {
            setState(
                SleepTimerUiState(
                    quickDurationsMinutes = SleepTimerDefaults.quickDurationsMinutes,
                    selectedQuickDurationMinutes = null,
                    customHoursInput = restoredHours,
                    customMinutesInput = restoredMinutes,
                    fadeEnabled = true,
                    endAction = SleepTimerEndAction.StopPlayback,
                    isRunning = true,
                    remainingMillis = restoredDurationMillis,
                    configuredDurationMillis = restoredDurationMillis,
                    scheduledEndTimestampMillis = endTimestamp
                )
            )
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag(SleepTimerTestTags.HOURS_FIELD)
            .assertTextEquals(restoredHours)
        composeRule.onNodeWithTag(SleepTimerTestTags.MINUTES_FIELD)
            .assertTextEquals(restoredMinutes)
        composeRule.onNodeWithTag(quickFiveTag)
            .assertIsNotSelected()
    }

    @Test
    fun statusMessageChipAutoDismissesAfterAcknowledgement() {
        var dismissed = false
        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.setContent {
                MaterialTheme {
                    SleepTimerScreen(
                        state = SleepTimerUiState(statusMessage = "Timer finished"),
                        onQuickDurationSelected = { },
                        onCustomHoursChanged = { },
                        onCustomMinutesChanged = { },
                        onFadeToggle = { },
                        onEndActionSelected = { },
                        onStart = {},
                        onCancel = {},
                        onMessageDismiss = { dismissed = true },
                    )
                }
            }

            kotlin.test.assertTrue(
                composeRule.onAllNodes(hasTestTag(SleepTimerTestTags.STATUS_MESSAGE)).fetchSemanticsNodes().isNotEmpty()
            )
            composeRule.onNodeWithTag(SleepTimerTestTags.STATUS_MESSAGE).performClick()

            composeRule.mainClock.advanceTimeBy(SleepTimerDefaults.statusMessageAutoDismissMillis - 1)
            composeRule.runOnIdle { assertFalse(dismissed) }

            composeRule.mainClock.advanceTimeBy(1)
            composeRule.waitForIdle()

            assertTrue(dismissed)
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test
    fun statusMessagePersistsThroughConfigChangeAndAutoDismissesAfterAcknowledgement() {
        val stateFlow = MutableStateFlow(SleepTimerUiState(statusMessage = "Timer finished"))
        var dismissCount = 0
        val dismissAction = {
            dismissCount += 1
            stateFlow.value = stateFlow.value.copy(statusMessage = null)
        }

        composeRule.mainClock.autoAdvance = false
        try {
            setSleepTimerContent(stateFlow, dismissAction)
            composeRule.waitForIdle()

            kotlin.test.assertTrue(
                composeRule.onAllNodes(hasTestTag(SleepTimerTestTags.STATUS_MESSAGE)).fetchSemanticsNodes().isNotEmpty()
            )

            setSleepTimerContent(stateFlow, dismissAction)
            composeRule.waitForIdle()

            composeRule.onNodeWithTag(SleepTimerTestTags.STATUS_MESSAGE).assertExists()

            composeRule.onNodeWithTag(SleepTimerTestTags.STATUS_MESSAGE).performClick()

            composeRule.mainClock.advanceTimeBy(SleepTimerDefaults.statusMessageAutoDismissMillis - 1)
            composeRule.runOnIdle { assertEquals(0, dismissCount) }

            composeRule.mainClock.advanceTimeBy(1)
            composeRule.waitForIdle()

            composeRule.runOnIdle { assertEquals(1, dismissCount) }
            kotlin.test.assertTrue(
                composeRule.onAllNodes(hasTestTag(SleepTimerTestTags.STATUS_MESSAGE)).fetchSemanticsNodes().isEmpty()
            )
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    private fun setSleepTimerContent(
        stateFlow: MutableStateFlow<SleepTimerUiState>,
        onMessageDismiss: () -> Unit,
    ) {
        composeRule.setContent {
            MaterialTheme {
                val state by stateFlow.collectAsState()
                SleepTimerScreen(
                    state = state,
                    onQuickDurationSelected = { },
                    onCustomHoursChanged = { },
                    onCustomMinutesChanged = { },
                    onFadeToggle = { },
                    onEndActionSelected = { },
                    onStart = {},
                    onCancel = {},
                    onMessageDismiss = onMessageDismiss,
                )
            }
        }
    }
}
