package app.ember.studio.sleep

import kotlinx.coroutines.flow.StateFlow

/**
 * A stable, UI-friendly API for controlling and observing the Sleep Timer.
 *
 * Implemented by SleepTimerController and consumed by ViewModels / UI.
 * Keeping this as a separate interface avoids leaking implementation details.
 */
interface SleepTimerControllerHandle {
    /** Immutable state stream used by Compose to render the Sleep Timer UI. */
    val state: StateFlow<SleepTimerUiState>

    /** Select a quick duration (in minutes). Clears any custom HH:MM inputs. */
    fun selectQuickDuration(minutes: Int)

    /** Update the custom hours input (digits only; clamped/sanitized by implementation). */
    fun updateCustomHours(input: String)

    /** Update the custom minutes input (digits only; clamped/sanitized by implementation). */
    fun updateCustomMinutes(input: String)

    /** Toggle the fade-out behavior for the final seconds of the timer. */
    fun setFadeEnabled(enabled: Boolean)

    /** Choose what to do when the timer ends (pause/stop/stop-after-track/stop-after-queue). */
    fun selectEndAction(action: SleepTimerEndAction)

    /** Start a new timer using the current configuration. No-op if already running or zero duration. */
    fun startTimer()

    /**
     * Resume an already-scheduled timer.
     *
     * @param endTimestampMillis wall-clock timestamp when the timer should elapse
     * @param fadeEnabled whether volume fade-down is enabled while counting down
     * @param endAction action to perform when timer elapses
     * @param originalVolume optional volume to restore/apply as the baseline during countdown
     */
    fun resumeTimer(
        endTimestampMillis: Long,
        fadeEnabled: Boolean,
        endAction: SleepTimerEndAction,
        originalVolume: Float?
    )

    /** Cancel the current timer (if running) and restore selection/volume. */
    fun cancelTimer()

    /** Restore the editable configuration from persisted user preferences (no-op while running). */
    fun restoreFromPreferences(preferences: SleepTimerPreferences)

    /** Post a transient status message (e.g., “Timer set for 60 min”). */
    fun showStatusMessage(message: String)

    /** Clear any currently shown transient status message. */
    fun clearStatusMessage()

    /**
     * Reset all sleep-timer state (including selections) to defaults,
     * keeping immutable defaults (quick list, end action default) from implementation.
     */
    fun clear()
}
