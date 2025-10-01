package app.ember.studio.sleep

import androidx.annotation.StringRes
import app.ember.studio.R
import app.ember.studio.util.formatDuration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

private const val MILLIS_PER_MINUTE = 60_000L

/** Interface implemented by the playback host so the sleep timer can control audio output. */
interface SleepTimerCallbacks {
    val currentVolume: Float
    fun setVolume(volume: Float)
    fun pausePlayback()
    fun stopPlayback()
    fun stopAfterCurrentTrack()
    fun stopAfterQueue()
}

enum class SleepTimerEndAction(@StringRes val labelRes: Int) {
    PausePlayback(R.string.sleep_timer_end_pause),
    StopPlayback(R.string.sleep_timer_end_stop),
    StopAfterTrack(R.string.sleep_timer_end_stop_after_track),
    StopAfterQueue(R.string.sleep_timer_end_stop_after_queue)
}

object SleepTimerDefaults {
    val quickDurationsMinutes: List<Int> = listOf(5, 10, 15, 30, 45, 60, 90, 120)
    const val fadeDurationMillis: Long = 30_000L
    const val tickerIntervalMillis: Long = 1_000L
    const val maxHours: Int = 12
    const val statusMessageAutoDismissMillis: Long = 2_500L
}

data class SleepTimerUiState(
    val quickDurationsMinutes: List<Int> = SleepTimerDefaults.quickDurationsMinutes,
    val selectedQuickDurationMinutes: Int? = null,
    val customHoursInput: String = "",
    val customMinutesInput: String = "",
    val fadeEnabled: Boolean = false,
    val endAction: SleepTimerEndAction = SleepTimerEndAction.PausePlayback,
    val isRunning: Boolean = false,
    val remainingMillis: Long = 0L,
    val configuredDurationMillis: Long = 0L,
    // Extra fields used by UI + persistence/resume
    val scheduledEndTimestampMillis: Long? = null,
    val statusMessage: String? = null,
    val originalVolume: Float? = null
) {
    val remainingFormatted: String
        get() = if (isRunning) formatDuration(remainingMillis) else ""

    val isStartEnabled: Boolean
        get() = !isRunning && configuredDurationMillis > 0

    val isCancelEnabled: Boolean
        get() = isRunning
}

/**
 * Coordinates the lifecycle of the sleep timer and exposes immutable UI state for Compose.
 */
class SleepTimerController(
    private val scope: CoroutineScope,
    private val callbacks: SleepTimerCallbacks,
    private val dispatcher: CoroutineDispatcher,
    private val tickerIntervalMillis: Long = SleepTimerDefaults.tickerIntervalMillis,
    private val fadeDurationMillis: Long = SleepTimerDefaults.fadeDurationMillis,
    private val now: () -> Long = System::currentTimeMillis,
    initialState: SleepTimerUiState = SleepTimerUiState(),
) : SleepTimerControllerHandle {

    private val _state = MutableStateFlow(initialState.recomputeDuration())
    override val state: StateFlow<SleepTimerUiState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var originalVolume: Float = callbacks.currentVolume.coerceIn(0f, 1f)

    override fun selectQuickDuration(minutes: Int) {
        if (_state.value.isRunning) return
        updateState {
            it.copy(
                selectedQuickDurationMinutes = minutes,
                customHoursInput = "",
                customMinutesInput = ""
            )
        }
    }

    override fun updateCustomHours(input: String) {
        if (_state.value.isRunning) return
        val digits = input.filter(Char::isDigit).take(2)
        val clamped = digits.toIntOrNull()?.coerceIn(0, SleepTimerDefaults.maxHours)
        val normalized = when {
            digits.isEmpty() -> ""
            clamped == null -> digits
            else -> clamped.toString()
        }
        updateState {
            it.copy(
                selectedQuickDurationMinutes = null,
                customHoursInput = normalized
            )
        }
    }

    override fun updateCustomMinutes(input: String) {
        if (_state.value.isRunning) return
        val digits = input.filter(Char::isDigit).take(2)
        val clamped = digits.toIntOrNull()?.coerceIn(0, 59)
        val normalized = when {
            digits.isEmpty() -> ""
            clamped == null -> digits
            else -> clamped.toString()
        }
        updateState {
            it.copy(
                selectedQuickDurationMinutes = null,
                customMinutesInput = normalized
            )
        }
    }

    override fun setFadeEnabled(enabled: Boolean) {
        val wasEnabled = _state.value.fadeEnabled
        _state.update { it.copy(fadeEnabled = enabled) }
        if (wasEnabled && !enabled) {
            // If we just turned fading off during a running timer, restore volume immediately
            callbacks.setVolume(originalVolume)
        }
    }

    override fun selectEndAction(action: SleepTimerEndAction) {
        _state.update { it.copy(endAction = action) }
    }

    override fun startTimer() {
        val snapshot = _state.value
        if (snapshot.isRunning) return
        val duration = snapshot.configuredDurationMillis
        if (duration <= 0L) return

        cancelInternal(restoreSelection = false)

        val fadeEnabled = snapshot.fadeEnabled
        val endAction = snapshot.endAction

        originalVolume = callbacks.currentVolume.coerceIn(0f, 1f)
        val endTime = now() + duration

        _state.update {
            it.copy(
                isRunning = true,
                remainingMillis = duration,
                scheduledEndTimestampMillis = endTime,
                statusMessage = null,
                originalVolume = originalVolume
            )
        }

        // Set initial volume state
        applyVolumeForRemaining(duration, fadeEnabled)

        timerJob = scope.launch(dispatcher) {
            runCountdown(endTime, fadeEnabled, endAction)
        }
    }

    override fun resumeTimer(
        endTimestampMillis: Long,
        fadeEnabled: Boolean,
        endAction: SleepTimerEndAction,
        originalVolume: Float?
    ) {
        // Cancel any existing job but keep current selection
        cancelInternal(restoreSelection = false)

        // Restore original volume baseline first
        this.originalVolume = (originalVolume ?: callbacks.currentVolume).coerceIn(0f, 1f)
        callbacks.setVolume(this.originalVolume)

        val initialRemaining = max(0L, endTimestampMillis - now())
        _state.update {
            it.copy(
                isRunning = initialRemaining > 0,
                remainingMillis = initialRemaining,
                fadeEnabled = fadeEnabled,
                endAction = endAction,
                scheduledEndTimestampMillis = if (initialRemaining > 0) endTimestampMillis else null,
                statusMessage = null,
                originalVolume = this.originalVolume
            )
        }

        if (initialRemaining <= 0L) {
            return
        }

        // Avoid abrupt jump if already inside fade window at resume
        if (!fadeEnabled || initialRemaining >= fadeDurationMillis) {
            applyVolumeForRemaining(initialRemaining, fadeEnabled)
        }

        timerJob = scope.launch(dispatcher) {
            runCountdown(endTimestampMillis, fadeEnabled, endAction)
        }
    }

    override fun cancelTimer() {
        cancelInternal(restoreSelection = true)
    }

    override fun restoreFromPreferences(preferences: SleepTimerPreferences) {
        if (_state.value.isRunning) return
        _state.update { current ->
            current.copy(
                selectedQuickDurationMinutes = preferences.selectedQuickDurationMinutes,
                customHoursInput = if (preferences.selectedQuickDurationMinutes == null) {
                    preferences.customHoursInput
                } else {
                    ""
                },
                customMinutesInput = if (preferences.selectedQuickDurationMinutes == null) {
                    preferences.customMinutesInput
                } else {
                    ""
                },
                fadeEnabled = preferences.fadeEnabled,
                endAction = preferences.endAction
            ).recomputeDuration()
        }
    }

    override fun showStatusMessage(message: String) {
        _state.update { it.copy(statusMessage = message) }
    }

    override fun clearStatusMessage() {
        _state.update { it.copy(statusMessage = null) }
    }

    override fun clear() {
        cancelInternal(restoreSelection = false)
        _state.update {
            SleepTimerUiState(
                fadeEnabled = it.fadeEnabled,
                endAction = it.endAction,
                quickDurationsMinutes = it.quickDurationsMinutes
            )
        }
    }

    private suspend fun runCountdown(
        endTimestampMillis: Long,
        fadeEnabled: Boolean,
        endAction: SleepTimerEndAction
    ) {
        while (true) {
            val remaining = max(0L, endTimestampMillis - now())
            _state.update { state -> state.copy(remainingMillis = remaining) }
            applyVolumeForRemaining(remaining, fadeEnabled)

            if (remaining <= 0L) break
            delay(tickerIntervalMillis)
        }

        // At completion, ensure volume restored and apply end action
        applyVolumeForRemaining(0L, fadeEnabled)

        when (endAction) {
            SleepTimerEndAction.PausePlayback -> callbacks.pausePlayback()
            SleepTimerEndAction.StopPlayback -> callbacks.stopPlayback()
            SleepTimerEndAction.StopAfterTrack -> callbacks.stopAfterCurrentTrack()
            SleepTimerEndAction.StopAfterQueue -> callbacks.stopAfterQueue()
        }

        _state.update {
            it.copy(
                isRunning = false,
                remainingMillis = 0L,
                scheduledEndTimestampMillis = null,
                originalVolume = originalVolume
            ).recomputeDuration()
        }
    }

    private fun cancelInternal(restoreSelection: Boolean) {
        timerJob?.cancel()
        timerJob = null
        callbacks.setVolume(originalVolume)
        _state.update {
            val cleared = it.copy(
                isRunning = false,
                remainingMillis = 0L,
                scheduledEndTimestampMillis = null,
                originalVolume = originalVolume
            )
            if (restoreSelection) cleared.recomputeDuration() else cleared
        }
    }

    private fun updateState(transform: (SleepTimerUiState) -> SleepTimerUiState) {
        _state.update { current -> transform(current).recomputeDuration() }
    }

    private fun SleepTimerUiState.recomputeDuration(): SleepTimerUiState {
        val quickMinutes = selectedQuickDurationMinutes
        val duration = if (quickMinutes != null) {
            quickMinutes.toLong() * MILLIS_PER_MINUTE
        } else {
            val hours = customHoursInput.toIntOrNull()?.coerceAtLeast(0) ?: 0
            val minutes = customMinutesInput.toIntOrNull()?.coerceIn(0, 59) ?: 0
            ((hours * 60L) + minutes) * MILLIS_PER_MINUTE
        }
        return copy(configuredDurationMillis = duration)
    }

    private fun applyVolumeForRemaining(remainingMillis: Long, fadeEnabled: Boolean) {
        val clampedRemaining = remainingMillis.coerceAtLeast(0L)
        val targetVolume = when {
            clampedRemaining == 0L -> originalVolume
            !fadeEnabled || clampedRemaining >= fadeDurationMillis -> originalVolume
            else -> originalVolume * (clampedRemaining.toFloat() / fadeDurationMillis).coerceIn(0f, 1f)
        }
        callbacks.setVolume(targetVolume)
    }
}

