package app.ember.studio.sleep

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import app.ember.studio.PlayerViewModel
import app.ember.studio.R
import app.ember.studio.SampleLibrary
import app.ember.studio.SleepTimerTestTags
import app.ember.studio.di.PlayerViewModelProvider
import app.ember.studio.testing.TestPlayer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SleepTimerStopAfterQueueUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var testPlayer: TestPlayer
    private lateinit var controller: UiTestSleepTimerController

    @Before
    fun setUp() {
        PlayerViewModelProvider.dependenciesFactory = { _ ->
            testPlayer = TestPlayer()
            controller = UiTestSleepTimerController(System::currentTimeMillis)
            PlayerViewModel.Dependencies(
                playerFactory = { _ -> testPlayer },
                sleepTimerControllerFactory = { _, callbacks ->
                    controller.attachCallbacks(callbacks)
                    controller
                }
            )
        }
    }

    @After
    fun tearDown() {
        PlayerViewModelProvider.clear()
    }

    @Test
    fun stopAfterQueue_completesAfterFinishingQueue_andClearsPending() {
        val activity = composeRule.activity

        // Navigate to Sleep Timer
        composeRule.onNodeWithContentDescription(activity.getString(R.string.drawer_toggle_content_description)).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.drawer_sleep_timer)).performClick()

        // Choose 5 minutes quick chip and Stop After Queue, then start
        composeRule.onNodeWithTag("${SleepTimerTestTags.QUICK_CHIP_PREFIX}5").performClick()
        composeRule.onNodeWithTag("${SleepTimerTestTags.END_ACTION_PREFIX}${SleepTimerEndAction.StopAfterQueue.name}").performClick()
        composeRule.onNodeWithText(activity.getString(R.string.sleep_timer_start)).performClick()

        // Fire stop-after-queue pending action
        controller.fireStopAfterQueue()

        // Navigate to Library and Play All
        composeRule.onNodeWithContentDescription(activity.getString(R.string.drawer_toggle_content_description)).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.drawer_library)).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.songs_play_all_button)).performClick()

        // Emit playback ended for each track until stop invoked
        val trackCount = SampleLibrary.songs.size
        repeat(trackCount + 2) {
            if (testPlayer.stopCount > 0) return@repeat
            testPlayer.emitPlaybackEnded()
        }

        // Verify stopped once and message surfaced
        assert(testPlayer.stopCount == 1)

        // Navigate back to Sleep Timer and assert completion message chip exists
        composeRule.onNodeWithContentDescription(activity.getString(R.string.drawer_toggle_content_description)).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.drawer_sleep_timer)).performClick()
        val expectedMessage = activity.getString(R.string.sleep_timer_message_completed_stop_after_queue)
        kotlin.test.assertTrue(
            composeRule.onAllNodes(hasTestTag(SleepTimerTestTags.STATUS_MESSAGE)).fetchSemanticsNodes().isNotEmpty()
        )
        kotlin.test.assertTrue(
            composeRule.onAllNodes(androidx.compose.ui.test.hasText(expectedMessage)).fetchSemanticsNodes().isNotEmpty()
        )
    }
}

// Minimal controller for UI-driven tests mirroring the structure in unit tests
private class UiTestSleepTimerController(
    private val now: () -> Long,
    initialState: SleepTimerUiState = SleepTimerUiState()
) : SleepTimerControllerHandle {
    private val _state = kotlinx.coroutines.flow.MutableStateFlow(initialState)
    override val state: kotlinx.coroutines.flow.StateFlow<SleepTimerUiState> = _state
    private lateinit var callbacks: SleepTimerCallbacks

    fun attachCallbacks(callbacks: SleepTimerCallbacks) { this.callbacks = callbacks }

    override fun selectQuickDuration(minutes: Int) {
        _state.value = _state.value.copy(
            selectedQuickDurationMinutes = minutes,
            customHoursInput = "",
            customMinutesInput = "",
            configuredDurationMillis = minutes * 60_000L
        )
    }

    override fun updateCustomHours(input: String) {
        val hours = input.filter(Char::isDigit).toIntOrNull() ?: 0
        val totalMinutes = (hours * 60L) + (_state.value.customMinutesInput.toLongOrNull() ?: 0L)
        _state.value = _state.value.copy(
            selectedQuickDurationMinutes = null,
            customHoursInput = input.filter(Char::isDigit),
            configuredDurationMillis = totalMinutes * 60_000L
        )
    }

    override fun updateCustomMinutes(input: String) {
        val minutes = input.filter(Char::isDigit).toIntOrNull() ?: 0
        val totalMinutes = (_state.value.customHoursInput.toLongOrNull() ?: 0L) * 60L + minutes
        _state.value = _state.value.copy(
            selectedQuickDurationMinutes = null,
            customMinutesInput = input.filter(Char::isDigit),
            configuredDurationMillis = totalMinutes * 60_000L
        )
    }

    override fun setFadeEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(fadeEnabled = enabled)
    }

    override fun selectEndAction(action: SleepTimerEndAction) {
        _state.value = _state.value.copy(endAction = action)
    }

    override fun startTimer() {
        val endTimestamp = now() + _state.value.configuredDurationMillis
        _state.value = _state.value.copy(
            isRunning = true,
            remainingMillis = _state.value.configuredDurationMillis,
            scheduledEndTimestampMillis = endTimestamp,
            statusMessage = null,
            originalVolume = callbacks.currentVolume
        )
    }

    override fun resumeTimer(
        endTimestampMillis: Long,
        fadeEnabled: Boolean,
        endAction: SleepTimerEndAction,
        originalVolume: Float?
    ) {
        val remaining = (endTimestampMillis - now()).coerceAtLeast(0L)
        _state.value = _state.value.copy(
            isRunning = remaining > 0,
            fadeEnabled = fadeEnabled,
            endAction = endAction,
            remainingMillis = remaining,
            scheduledEndTimestampMillis = if (remaining > 0) endTimestampMillis else null,
            statusMessage = null,
            originalVolume = originalVolume
        )
        val targetVolume = originalVolume ?: callbacks.currentVolume
        callbacks.setVolume(targetVolume)
    }

    override fun cancelTimer() {
        _state.value = _state.value.copy(isRunning = false, remainingMillis = 0L, scheduledEndTimestampMillis = null)
    }

    override fun restoreFromPreferences(preferences: SleepTimerPreferences) {
        _state.value = _state.value.copy(
            selectedQuickDurationMinutes = preferences.selectedQuickDurationMinutes,
            customHoursInput = preferences.customHoursInput,
            customMinutesInput = preferences.customMinutesInput,
            fadeEnabled = preferences.fadeEnabled,
            endAction = preferences.endAction
        )
    }

    override fun showStatusMessage(message: String) {
        _state.value = _state.value.copy(statusMessage = message)
    }

    override fun clearStatusMessage() { _state.value = _state.value.copy(statusMessage = null) }

    override fun clear() { _state.value = SleepTimerUiState() }

    fun fireStopAfterQueue() {
        _state.value = _state.value.copy(isRunning = false, remainingMillis = 0L, scheduledEndTimestampMillis = null)
        callbacks.stopAfterQueue()
    }
}
