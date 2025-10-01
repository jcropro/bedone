package app.ember.studio.sleep

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.ember.studio.PlayerViewModel
import app.ember.studio.onboarding.OnboardingPreferencesRepository
import app.ember.studio.R
import app.ember.studio.sleep.SleepTimerEndAction
import app.ember.studio.sleep.SleepTimerPendingActionType
import app.ember.studio.sleep.SleepTimerPreferencesRepository
import app.ember.studio.sleep.SleepTimerPreferences
import app.ember.studio.sleep.SleepTimerUiState as SleepTimerState
import app.ember.studio.theme.ThemePreferencesRepository
import java.io.File
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import app.ember.studio.testing.forceClear

@RunWith(AndroidJUnit4::class)
class PlayerViewModelSleepTimerTest {

    private lateinit var application: Application
    private lateinit var scope: CoroutineScope
    private lateinit var themeRepository: ThemePreferencesRepository
    private lateinit var sleepRepository: SleepTimerPreferencesRepository
    private lateinit var onboardingRepository: OnboardingPreferencesRepository

    @BeforeTest
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        themeRepository = ThemePreferencesRepository(createDataStore("theme"))
        sleepRepository = SleepTimerPreferencesRepository(createDataStore("sleep"))
        onboardingRepository = OnboardingPreferencesRepository(createDataStore("onboarding"))
    }

    @AfterTest
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun stopAfterTrackStopsPlaybackWhenSongEnds() = runBlocking {
        val clock = FakeClock()
        val controller = TestSleepTimerController(clock::now)
        val player = app.ember.studio.testing.TestRecordingPlayer()
        val viewModel = createViewModel(clock, controller, player)

        try {
            viewModel.playAllSongs()
            viewModel.selectSleepTimerEndAction(SleepTimerEndAction.StopAfterTrack)
            viewModel.selectSleepTimerQuickDuration(5)
            viewModel.startSleepTimer()
            waitForIdle()

            controller.fireStopAfterTrack()
            waitForIdle()

            player.emitPlaybackEnded()
            waitForIdle()

            assertEquals(1, player.stopCount)
        } finally {
            forceClear(viewModel)
        }
    }

    @Test
    fun stopAfterQueueAdvancesThroughQueueThenStops() = runBlocking {
        val clock = FakeClock()
        val controller = TestSleepTimerController(clock::now)
        val player = app.ember.studio.testing.TestRecordingPlayer()
        val viewModel = createViewModel(clock, controller, player)

        try {
            viewModel.playAllSongs()
            viewModel.selectSleepTimerEndAction(SleepTimerEndAction.StopAfterQueue)
            viewModel.selectSleepTimerQuickDuration(5)
            viewModel.startSleepTimer()
            waitForIdle()

            controller.fireStopAfterQueue()
            waitForIdle()

            val trackCount = viewModel.homeState.value.songs.size
            repeat(trackCount) {
                player.emitPlaybackEnded()
                waitForIdle()
            }

            assertEquals(1, player.stopCount)
        } finally {
            forceClear(viewModel)
        }
    }

    @Test
    fun resumeTimerRestoresActiveCountdownFromPreferences() = runBlocking {
        val clock = FakeClock()
        val controller = TestSleepTimerController(clock::now)
        val player = app.ember.studio.testing.TestRecordingPlayer()
        player.volume = 0.2f
        val endTimestamp = clock.now() + 10 * 60_000L

        sleepRepository.updatePreferences(
            SleepTimerPreferences(
                selectedQuickDurationMinutes = 10,
                fadeEnabled = true,
                endAction = SleepTimerEndAction.StopPlayback,
                activeEndTimestampMillis = endTimestamp,
                activeFadeEnabled = true,
                activeEndAction = SleepTimerEndAction.StopPlayback,
                activeOriginalVolume = 0.8f
            )
        )

        val viewModel = createViewModel(clock, controller, player)
        try {
            waitForIdle()

            val state = controller.state.value
            assertTrue(state.isRunning)
            assertEquals(endTimestamp, state.scheduledEndTimestampMillis)
            assertTrue(state.remainingMillis > 0)
            assertTrue(state.fadeEnabled)
            assertEquals(SleepTimerEndAction.StopPlayback, state.endAction)
            assertEquals(0.8f, player.volume)
        } finally {
            forceClear(viewModel)
        }
    }

    @Test
    fun expiredTimerTriggersImmediateCompletionOnResume() = runBlocking {
        val clock = FakeClock()
        val controller = TestSleepTimerController(clock::now)
        val player = app.ember.studio.testing.TestRecordingPlayer()
        player.volume = 0.1f
        val durationMillis = 5 * 60_000L
        val endTimestamp = clock.now() + durationMillis

        sleepRepository.updatePreferences(
            SleepTimerPreferences(
                fadeEnabled = true,
                endAction = SleepTimerEndAction.StopPlayback,
                activeEndTimestampMillis = endTimestamp,
                activeFadeEnabled = true,
                activeEndAction = SleepTimerEndAction.StopPlayback,
                activeOriginalVolume = 0.9f
            )
        )

        clock.advanceBy(durationMillis + 1_000L)

        val viewModel = createViewModel(clock, controller, player)
        try {
            waitForIdle()

            assertEquals(1, player.stopCount)
            val statusMessage = controller.state.value.statusMessage
            assertEquals(
                application.getString(R.string.sleep_timer_message_completed_stop),
                statusMessage
            )
            assertFalse(controller.state.value.isRunning)
            assertNull(controller.state.value.scheduledEndTimestampMillis)
            assertEquals(0.9f, player.volume)
        } finally {
            forceClear(viewModel)
        }
    }

    @Test
    fun expiredStopAfterTrackSchedulesPendingActionOnResume() = runBlocking {
        val clock = FakeClock()
        val controller = TestSleepTimerController(clock::now)
        val player = app.ember.studio.testing.TestRecordingPlayer()
        player.volume = 0.2f
        val durationMillis = 5 * 60_000L
        val endTimestamp = clock.now() + durationMillis

        sleepRepository.updatePreferences(
            SleepTimerPreferences(
                fadeEnabled = true,
                endAction = SleepTimerEndAction.StopAfterTrack,
                activeEndTimestampMillis = endTimestamp,
                activeFadeEnabled = true,
                activeEndAction = SleepTimerEndAction.StopAfterTrack,
                activeOriginalVolume = 0.6f
            )
        )

        clock.advanceBy(durationMillis + 1_000L)

        val viewModel = createViewModel(clock, controller, player)
        try {
            waitForIdle()

            val expectedMessage = application.getString(
                R.string.sleep_timer_message_completed_stop_after_track
            )
            assertEquals(expectedMessage, controller.state.value.statusMessage)
            assertEquals(0.6f, player.volume)

            val pending = sleepRepository.preferences.first()
            assertEquals(SleepTimerPendingActionType.StopAfterTrack, pending.pendingAction.type)

            viewModel.playAllSongs()
            waitForIdle()

            player.emitPlaybackEnded()
            waitForIdle()

            assertEquals(1, player.stopCount)

            val cleared = sleepRepository.preferences.first()
            assertEquals(SleepTimerPendingActionType.None, cleared.pendingAction.type)
        } finally {
            forceClear(viewModel)
            waitForIdle()
        }
    }

    @Test
    fun expiredStopAfterQueueSchedulesPendingActionOnResume() = runBlocking {
        val clock = FakeClock()
        val controller = TestSleepTimerController(clock::now)
        val player = app.ember.studio.testing.TestRecordingPlayer()
        player.volume = 0.3f
        val durationMillis = 10 * 60_000L
        val endTimestamp = clock.now() + durationMillis

        sleepRepository.updatePreferences(
            SleepTimerPreferences(
                fadeEnabled = true,
                endAction = SleepTimerEndAction.StopAfterQueue,
                activeEndTimestampMillis = endTimestamp,
                activeFadeEnabled = true,
                activeEndAction = SleepTimerEndAction.StopAfterQueue,
                activeOriginalVolume = 0.8f
            )
        )

        clock.advanceBy(durationMillis + 1_000L)

        val viewModel = createViewModel(clock, controller, player)
        try {
            waitForIdle()

            val expectedMessage = application.getString(
                R.string.sleep_timer_message_completed_stop_after_queue
            )
            assertEquals(expectedMessage, controller.state.value.statusMessage)
            assertEquals(0.8f, player.volume)

            val pending = sleepRepository.preferences.first()
            assertEquals(SleepTimerPendingActionType.StopAfterQueue, pending.pendingAction.type)
            assertTrue(pending.pendingAction.queueSnapshot.isNotEmpty())
            assertEquals(0, pending.pendingAction.queueIndex)

            viewModel.playAllSongs()
            waitForIdle()

            val trackCount = viewModel.homeState.value.songs.size
            repeat(trackCount) {
                player.emitPlaybackEnded()
                waitForIdle()
                if (player.stopCount > 0) {
                    return@repeat
                }
            }

            assertEquals(1, player.stopCount)

            val cleared = sleepRepository.preferences.first()
            assertEquals(SleepTimerPendingActionType.None, cleared.pendingAction.type)
        } finally {
            forceClear(viewModel)
            waitForIdle()
        }
    }

    @Test
    fun startingTimerPersistsStatusMessageAndAllowsDismissal() = runBlocking {
        val clock = FakeClock()
        val controller = TestSleepTimerController(clock::now)
        val player = app.ember.studio.testing.TestRecordingPlayer()
        val viewModel = createViewModel(clock, controller, player)

        try {
            viewModel.selectSleepTimerQuickDuration(5)
            viewModel.startSleepTimer()
            waitForIdle()

            val expectedMessage = application.getString(
                R.string.sleep_timer_message_started,
                "5:00"
            )
            assertEquals(expectedMessage, controller.state.value.statusMessage)

            val persisted = sleepRepository.preferences.first()
            assertEquals(expectedMessage, persisted.statusMessage)

            viewModel.dismissSleepTimerStatusMessage()
            waitForIdle()

            assertNull(controller.state.value.statusMessage)
            val cleared = sleepRepository.preferences.first()
            assertNull(cleared.statusMessage)
        } finally {
            forceClear(viewModel)
            waitForIdle()
        }
    }

    @Test
    fun persistedQuickSelectionAndOptionsRestoreAfterProcessRestart() = runBlocking {
        val clock = FakeClock()
        val controller = TestSleepTimerController(clock::now)
        val player = app.ember.studio.testing.TestRecordingPlayer()
        val viewModel = createViewModel(clock, controller, player)

        try {
            viewModel.selectSleepTimerQuickDuration(45)
            viewModel.setSleepTimerFade(true)
            viewModel.selectSleepTimerEndAction(SleepTimerEndAction.StopAfterQueue)
            waitForIdle()
        } finally {
            forceClear(viewModel)
            waitForIdle()
        }

        val restoredController = TestSleepTimerController(clock::now)
        val restoredPlayer = app.ember.studio.testing.TestRecordingPlayer()
        val restoredViewModel = createViewModel(clock, restoredController, restoredPlayer)

        try {
            waitForIdle()

            val restoredState = restoredController.state.value
            assertEquals(45, restoredState.selectedQuickDurationMinutes)
            assertTrue(restoredState.fadeEnabled)
            assertEquals(SleepTimerEndAction.StopAfterQueue, restoredState.endAction)
        } finally {
            forceClear(restoredViewModel)
            waitForIdle()
        }
    }

    @Test
    fun persistedCustomDurationRestoresAfterProcessRestart() = runBlocking {
        val clock = FakeClock()
        val controller = TestSleepTimerController(clock::now)
        val player = app.ember.studio.testing.TestRecordingPlayer()
        val viewModel = createViewModel(clock, controller, player)

        try {
            viewModel.updateSleepTimerHoursInput("1")
            viewModel.updateSleepTimerMinutesInput("30")
            viewModel.setSleepTimerFade(true)
            viewModel.selectSleepTimerEndAction(SleepTimerEndAction.StopPlayback)
            waitForIdle()
        } finally {
            forceClear(viewModel)
            waitForIdle()
        }

        val restoredController = TestSleepTimerController(clock::now)
        val restoredPlayer = app.ember.studio.testing.TestRecordingPlayer()
        val restoredViewModel = createViewModel(clock, restoredController, restoredPlayer)

        try {
            waitForIdle()

            val restoredState = restoredController.state.value
            assertNull(restoredState.selectedQuickDurationMinutes)
            assertEquals("1", restoredState.customHoursInput)
            assertEquals("30", restoredState.customMinutesInput)
            assertTrue(restoredState.fadeEnabled)
            assertEquals(SleepTimerEndAction.StopPlayback, restoredState.endAction)
        } finally {
            forceClear(restoredViewModel)
            waitForIdle()
        }
    }

    @Test
    fun cancellationMessagePersistsAcrossProcessRestart() = runBlocking {
        val clock = FakeClock()
        val controller = TestSleepTimerController(clock::now)
        val player = app.ember.studio.testing.TestRecordingPlayer()
        val viewModel = createViewModel(clock, controller, player)

        val expectedMessage = application.getString(R.string.sleep_timer_message_cancelled)

        try {
            viewModel.selectSleepTimerQuickDuration(5)
            viewModel.startSleepTimer()
            waitForIdle()

            viewModel.cancelSleepTimer()
            waitForIdle()

            assertEquals(expectedMessage, controller.state.value.statusMessage)
        } finally {
            forceClear(viewModel)
            waitForIdle()
        }

        val restoredController = TestSleepTimerController(clock::now)
        val restoredPlayer = app.ember.studio.testing.TestRecordingPlayer()
        val restoredViewModel = createViewModel(clock, restoredController, restoredPlayer)

        try {
            waitForIdle()

            assertEquals(expectedMessage, restoredController.state.value.statusMessage)

            restoredViewModel.dismissSleepTimerStatusMessage()
            waitForIdle()

            assertNull(restoredController.state.value.statusMessage)
            val cleared = sleepRepository.preferences.first()
            assertNull(cleared.statusMessage)
        } finally {
            forceClear(restoredViewModel)
            waitForIdle()
        }
    }

    @Test
    fun pendingStopAfterTrackPersistsAcrossProcessRestart() = runBlocking {
        val clock = FakeClock()
        val controller = TestSleepTimerController(clock::now)
        val player = app.ember.studio.testing.TestRecordingPlayer()
        val viewModel = createViewModel(clock, controller, player)

        try {
            viewModel.playAllSongs()
            waitForIdle()

            viewModel.selectSleepTimerEndAction(SleepTimerEndAction.StopAfterTrack)
            viewModel.selectSleepTimerQuickDuration(5)
            viewModel.startSleepTimer()
            waitForIdle()

            controller.fireStopAfterTrack()
            waitForIdle()
        } finally {
            forceClear(viewModel)
            waitForIdle()
        }

        val resumedController = TestSleepTimerController(clock::now)
        val resumedPlayer = app.ember.studio.testing.TestRecordingPlayer()
        val resumedViewModel = createViewModel(clock, resumedController, resumedPlayer)

        try {
            resumedViewModel.playAllSongs()
            waitForIdle()

            resumedPlayer.emitPlaybackEnded()
            waitForIdle()

            assertEquals(1, resumedPlayer.stopCount)

            val persisted = sleepRepository.preferences.first()
            assertEquals(SleepTimerPendingActionType.None, persisted.pendingAction.type)
        } finally {
            forceClear(resumedViewModel)
            waitForIdle()
        }
    }

    @Test
    fun pendingStopAfterQueuePersistsAcrossProcessRestart() = runBlocking {
        val clock = FakeClock()
        val controller = TestSleepTimerController(clock::now)
        val player = app.ember.studio.testing.TestRecordingPlayer()
        val viewModel = createViewModel(clock, controller, player)

        try {
            viewModel.playAllSongs()
            waitForIdle()

            viewModel.selectSleepTimerEndAction(SleepTimerEndAction.StopAfterQueue)
            viewModel.selectSleepTimerQuickDuration(5)
            viewModel.startSleepTimer()
            waitForIdle()

            controller.fireStopAfterQueue()
            waitForIdle()
        } finally {
            forceClear(viewModel)
            waitForIdle()
        }

        val resumedController = TestSleepTimerController(clock::now)
        val resumedPlayer = app.ember.studio.testing.TestRecordingPlayer()
        val resumedViewModel = createViewModel(clock, resumedController, resumedPlayer)

        try {
            resumedViewModel.playAllSongs()
            waitForIdle()

            val trackCount = resumedViewModel.homeState.value.songs.size
            repeat(trackCount) {
                resumedPlayer.emitPlaybackEnded()
                waitForIdle()
                if (resumedPlayer.stopCount > 0) {
                    return@repeat
                }
            }

            assertEquals(1, resumedPlayer.stopCount)

            val persisted = sleepRepository.preferences.first()
            assertEquals(SleepTimerPendingActionType.None, persisted.pendingAction.type)
        } finally {
            forceClear(resumedViewModel)
            waitForIdle()
        }
    }

    private fun createViewModel(
        clock: FakeClock,
        controller: TestSleepTimerController,
        player: app.ember.studio.testing.TestRecordingPlayer
    ): PlayerViewModel {
        val dependencies = PlayerViewModel.Dependencies(
            playerFactory = { player },
            themePreferencesRepository = themeRepository,
            onboardingPreferencesRepository = onboardingRepository,
            sleepTimerPreferencesRepository = sleepRepository,
            sleepTimerControllerFactory = { _, callbacks ->
                controller.attachCallbacks(callbacks)
                controller
            },
            ioDispatcher = Dispatchers.Main,
            clock = clock::now
        )
        return PlayerViewModel(application, dependencies)
    }

    private fun createDataStore(name: String) =
        PreferenceDataStoreFactory.create(scope = scope) {
            File(application.filesDir, "test-${name}-${UUID.randomUUID()}-prefs.pb")
        }

    private fun waitForIdle() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private class FakeClock {
        private var currentTime = System.currentTimeMillis()
        fun advanceBy(millis: Long) {
            currentTime += millis
        }
        fun now(): Long = currentTime
    }

    private class TestSleepTimerController(
        private val now: () -> Long,
        initialState: SleepTimerState = SleepTimerState()
    ) : SleepTimerControllerHandle {
        private val _state = MutableStateFlow(initialState)
        override val state: StateFlow<SleepTimerState> = _state.asStateFlow()
        private lateinit var callbacks: SleepTimerCallbacks

        fun attachCallbacks(callbacks: SleepTimerCallbacks) {
            this.callbacks = callbacks
        }

        override fun selectQuickDuration(minutes: Int) {
            _state.update {
                it.copy(
                    selectedQuickDurationMinutes = minutes,
                    customHoursInput = "",
                    customMinutesInput = "",
                    configuredDurationMillis = minutes * 60_000L
                )
            }
        }

        override fun updateCustomHours(input: String) {
            val hours = input.filter(Char::isDigit).toIntOrNull() ?: 0
            _state.update {
                val totalMinutes = (hours * 60L) + (it.customMinutesInput.toLongOrNull() ?: 0L)
                it.copy(
                    selectedQuickDurationMinutes = null,
                    customHoursInput = input.filter(Char::isDigit),
                    configuredDurationMillis = totalMinutes * 60_000L
                )
            }
        }

        override fun updateCustomMinutes(input: String) {
            val minutes = input.filter(Char::isDigit).toIntOrNull() ?: 0
            _state.update {
                val totalMinutes = (it.customHoursInput.toLongOrNull() ?: 0L) * 60L + minutes
                it.copy(
                    selectedQuickDurationMinutes = null,
                    customMinutesInput = input.filter(Char::isDigit),
                    configuredDurationMillis = totalMinutes * 60_000L
                )
            }
        }

        override fun setFadeEnabled(enabled: Boolean) {
            _state.update { it.copy(fadeEnabled = enabled) }
        }

        override fun selectEndAction(action: SleepTimerEndAction) {
            _state.update { it.copy(endAction = action) }
        }

        override fun startTimer() {
            val endTimestamp = now() + _state.value.configuredDurationMillis
            _state.update {
                it.copy(
                    isRunning = true,
                    remainingMillis = it.configuredDurationMillis,
                    scheduledEndTimestampMillis = endTimestamp,
                    statusMessage = null,
                    originalVolume = callbacks.currentVolume
                )
            }
        }

        override fun resumeTimer(
            endTimestampMillis: Long,
            fadeEnabled: Boolean,
            endAction: SleepTimerEndAction,
            originalVolume: Float?
        ) {
            val remaining = (endTimestampMillis - now()).coerceAtLeast(0L)
            _state.update {
                it.copy(
                    isRunning = remaining > 0,
                    fadeEnabled = fadeEnabled,
                    endAction = endAction,
                    remainingMillis = remaining,
                    scheduledEndTimestampMillis = if (remaining > 0) endTimestampMillis else null,
                    statusMessage = null,
                    originalVolume = originalVolume
                )
            }
            val targetVolume = originalVolume ?: callbacks.currentVolume
            callbacks.setVolume(targetVolume)
        }

        override fun cancelTimer() {
            _state.update { it.copy(isRunning = false, remainingMillis = 0L, scheduledEndTimestampMillis = null) }
        }

        override fun restoreFromPreferences(preferences: SleepTimerPreferences) {
            _state.update {
                it.copy(
                    selectedQuickDurationMinutes = preferences.selectedQuickDurationMinutes,
                    customHoursInput = preferences.customHoursInput,
                    customMinutesInput = preferences.customMinutesInput,
                    fadeEnabled = preferences.fadeEnabled,
                    endAction = preferences.endAction
                )
            }
        }

        override fun showStatusMessage(message: String) {
            _state.update { it.copy(statusMessage = message) }
        }

        override fun clearStatusMessage() {
            _state.update { it.copy(statusMessage = null) }
        }

        override fun clear() {
            _state.update { SleepTimerState() }
        }

        fun fireStopAfterTrack() {
            _state.update { it.copy(isRunning = false, remainingMillis = 0L, scheduledEndTimestampMillis = null) }
            callbacks.stopAfterCurrentTrack()
        }

        fun fireStopAfterQueue() {
            _state.update { it.copy(isRunning = false, remainingMillis = 0L, scheduledEndTimestampMillis = null) }
            callbacks.stopAfterQueue()
        }
    }

    // RecordingPlayer now provided by app.ember.studio.testing.TestRecordingPlayer
}
