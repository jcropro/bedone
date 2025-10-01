package app.ember.studio.sleep

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SleepTimerControllerTest {

    @Test
    fun startTimerCountsDownAndPausesPlayback() = runTest {
        val callbacks = FakeCallbacks()
        val controller = createController(callbacks)

        controller.selectEndAction(SleepTimerEndAction.PausePlayback)
        controller.selectQuickDuration(1)
        controller.startTimer()

        advanceTimeBy(1_000L)
        runCurrent()
        assertEquals(59_000L, controller.state.value.remainingMillis)

        advanceTimeBy(59_000L)
        advanceUntilIdle()

        assertFalse(controller.state.value.isRunning)
        assertEquals(1, callbacks.pauseInvocations)
        assertEquals(0, callbacks.stopInvocations)
    }

    @Test
    fun fadeEnabledScalesVolumeNearCompletion() = runTest {
        val callbacks = FakeCallbacks()
        val controller = createController(callbacks)

        controller.selectQuickDuration(1)
        controller.setFadeEnabled(true)
        controller.startTimer()

        assertEquals(1f, callbacks.volume)

        advanceTimeBy(40_000L)
        runCurrent()
        val fadedVolume = callbacks.volume
        assertTrue(fadedVolume in 0f..1f)
        assertTrue(fadedVolume < 1f)

        advanceTimeBy(20_000L)
        advanceUntilIdle()

        assertEquals(1f, callbacks.volume)
        assertEquals(1, callbacks.pauseInvocations)
    }

    @Test
    fun cancelTimerRestoresVolumeAndSelection() = runTest {
        val callbacks = FakeCallbacks()
        val controller = createController(callbacks)

        controller.selectQuickDuration(1)
        controller.setFadeEnabled(true)
        controller.startTimer()

        advanceTimeBy(40_000L)
        runCurrent()
        assertTrue(callbacks.volume < 1f)

        controller.cancelTimer()
        advanceUntilIdle()

        assertEquals(1f, callbacks.volume)
        assertFalse(controller.state.value.isRunning)
        assertTrue(controller.state.value.isStartEnabled)
    }

    @Test
    fun customMinutesClampToSixtyMinuteWindow() = runTest {
        val callbacks = FakeCallbacks()
        val controller = createController(callbacks)

        controller.updateCustomMinutes("99")

        assertEquals("59", controller.state.value.customMinutesInput)
        assertEquals(59 * 60_000L, controller.state.value.configuredDurationMillis)
    }

    @Test
    fun stopAfterTrackInvokesCallbackOnCompletion() = runTest {
        val callbacks = FakeCallbacks()
        val controller = createController(callbacks)

        controller.selectEndAction(SleepTimerEndAction.StopAfterTrack)
        controller.selectQuickDuration(1)
        controller.startTimer()

        advanceTimeBy(60_000L)
        advanceUntilIdle()

        assertEquals(1, callbacks.stopAfterTrackInvocations)
        assertEquals(0, callbacks.stopAfterQueueInvocations)
    }

    @Test
    fun stopAfterQueueInvokesCallbackOnCompletion() = runTest {
        val callbacks = FakeCallbacks()
        val controller = createController(callbacks)

        controller.selectEndAction(SleepTimerEndAction.StopAfterQueue)
        controller.selectQuickDuration(1)
        controller.startTimer()

        advanceTimeBy(60_000L)
        advanceUntilIdle()

        assertEquals(1, callbacks.stopAfterQueueInvocations)
        assertEquals(0, callbacks.stopAfterTrackInvocations)
    }

    @Test
    fun restoreFromPreferencesUpdatesStateWhenIdle() = runTest {
        val callbacks = FakeCallbacks()
        val controller = createController(callbacks)

        controller.restoreFromPreferences(
            SleepTimerPreferences(
                selectedQuickDurationMinutes = null,
                customHoursInput = "1",
                customMinutesInput = "45",
                fadeEnabled = true,
                endAction = SleepTimerEndAction.StopPlayback
            )
        )

        val state = controller.state.value
        assertNull(state.selectedQuickDurationMinutes)
        assertEquals("1", state.customHoursInput)
        assertEquals("45", state.customMinutesInput)
        assertTrue(state.fadeEnabled)
        assertEquals(SleepTimerEndAction.StopPlayback, state.endAction)
        assertEquals(105 * 60_000L, state.configuredDurationMillis)
    }

    @Test
    fun restoreFromPreferencesIgnoredWhileRunning() = runTest {
        val callbacks = FakeCallbacks()
        val controller = createController(callbacks)

        controller.selectQuickDuration(1)
        controller.startTimer()

        controller.restoreFromPreferences(
            SleepTimerPreferences(
                selectedQuickDurationMinutes = 10,
                customHoursInput = "2",
                customMinutesInput = "15",
                fadeEnabled = false,
                endAction = SleepTimerEndAction.StopPlayback
            )
        )

        val state = controller.state.value
        assertEquals(1, state.selectedQuickDurationMinutes)
    }

    @Test
    fun statusMessageHelpersUpdateState() = runTest {
        val callbacks = FakeCallbacks()
        val controller = createController(callbacks)

        controller.showStatusMessage("Timer complete")
        assertEquals("Timer complete", controller.state.value.statusMessage)

        controller.clearStatusMessage()
        assertNull(controller.state.value.statusMessage)
    }

    @Test
    fun resumeTimerCountsDownAndInvokesEndAction() = runTest {
        val callbacks = FakeCallbacks()
        val controller = createController(callbacks)

        val endTimestamp = testScheduler.currentTime + 30_000L
        controller.resumeTimer(
            endTimestampMillis = endTimestamp,
            fadeEnabled = false,
            endAction = SleepTimerEndAction.StopPlayback,
            originalVolume = 0.75f
        )

        advanceTimeBy(30_000L)
        advanceUntilIdle()

        assertEquals(1, callbacks.stopInvocations)
        assertFalse(controller.state.value.isRunning)
        assertNull(controller.state.value.scheduledEndTimestampMillis)
        assertEquals(0.75f, callbacks.volume)
    }

    @Test
    fun resumeTimerRestoresOriginalVolumeBeforeCountdown() = runTest {
        val callbacks = FakeCallbacks(initialVolume = 0.15f)
        val controller = createController(callbacks)

        val endTimestamp = testScheduler.currentTime + 10_000L
        controller.resumeTimer(
            endTimestampMillis = endTimestamp,
            fadeEnabled = true,
            endAction = SleepTimerEndAction.StopPlayback,
            originalVolume = 0.6f
        )

        assertEquals(0.6f, callbacks.volume)
        assertTrue(controller.state.value.isRunning)
        assertEquals(0.6f, controller.state.value.originalVolume)
    }

    private fun TestScope.createController(
        callbacks: FakeCallbacks,
        initialState: SleepTimerUiState = SleepTimerUiState(quickDurationsMinutes = listOf(1, 5, 10))
    ): SleepTimerController {
        val dispatcher = StandardTestDispatcher(testScheduler)
        return SleepTimerController(
            scope = this,
            callbacks = callbacks,
            dispatcher = dispatcher,
            tickerIntervalMillis = 1_000L,
            fadeDurationMillis = 30_000L,
            now = { testScheduler.currentTime },
            initialState = initialState
        )
    }

    private class FakeCallbacks(initialVolume: Float = 1f) : SleepTimerCallbacks {
        var volume: Float = initialVolume
            private set
        var pauseInvocations: Int = 0
            private set
        var stopInvocations: Int = 0
            private set
        var stopAfterTrackInvocations: Int = 0
            private set
        var stopAfterQueueInvocations: Int = 0
            private set

        override val currentVolume: Float
            get() = volume

        override fun setVolume(volume: Float) {
            this.volume = volume
        }

        override fun pausePlayback() {
            pauseInvocations += 1
        }

        override fun stopPlayback() {
            stopInvocations += 1
        }

        override fun stopAfterCurrentTrack() {
            stopAfterTrackInvocations += 1
        }

        override fun stopAfterQueue() {
            stopAfterQueueInvocations += 1
        }
    }
}
