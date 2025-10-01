package app.ember.studio.crossfade

import androidx.media3.common.Player

/**
 * Utility for checking crossfade capabilities and validating player state
 */
object CrossfadeCapabilityChecker {
    
    /**
     * Check if the player supports crossfade functionality
     */
    fun canEnableCrossfade(player: Player?): CrossfadeCapability {
        if (player == null) {
            return CrossfadeCapability.Unavailable("Player not available")
        }
        
        if (player.playbackState == Player.STATE_IDLE) {
            return CrossfadeCapability.Unavailable("Player not initialized")
        }
        
        if (player.mediaItemCount <= 1) {
            return CrossfadeCapability.Unavailable("Insufficient media items for crossfade")
        }
        
        if (!player.hasNextMediaItem()) {
            return CrossfadeCapability.Unavailable("No next media item available")
        }
        
        // Check if volume control is available
        try {
            val currentVolume = player.volume
            if (currentVolume < 0f || currentVolume > 1f) {
                return CrossfadeCapability.Unavailable("Volume control not available")
            }
        } catch (e: Exception) {
            return CrossfadeCapability.Unavailable("Volume control error: ${e.message}")
        }
        
        return CrossfadeCapability.Available
    }
    
    /**
     * Validate crossfade duration is within acceptable limits
     */
    fun validateCrossfadeDuration(durationMs: Long): CrossfadeValidation {
        return when {
            durationMs < 0 -> CrossfadeValidation.Invalid("Duration cannot be negative")
            durationMs > 12000 -> CrossfadeValidation.Invalid("Duration cannot exceed 12 seconds")
            durationMs > 0 && durationMs < 500 -> CrossfadeValidation.Warning("Very short duration may cause audio artifacts")
            else -> CrossfadeValidation.Valid
        }
    }
    
    /**
     * Check if crossfade should be disabled due to other audio effects
     */
    fun shouldDisableCrossfade(
        player: Player?,
        isSleepTimerActive: Boolean = false,
        isSleepTimerFadeActive: Boolean = false
    ): Boolean {
        if (isSleepTimerFadeActive) {
            return true // Sleep timer fade takes precedence
        }
        
        if (isSleepTimerActive) {
            return true // Sleep timer active, disable crossfade
        }
        
        // Check if player is in a state that conflicts with crossfade
        player?.let { p ->
            if (p.playbackState == Player.STATE_BUFFERING) {
                return true // Don't crossfade while buffering
            }
            
            if (p.playbackState == Player.STATE_ENDED) {
                return true // Don't crossfade when ended
            }
        }
        
        return false
    }
}

/**
 * Represents the capability status of crossfade functionality
 */
sealed class CrossfadeCapability {
    object Available : CrossfadeCapability()
    data class Unavailable(val reason: String) : CrossfadeCapability()
}

/**
 * Represents the validation result for crossfade duration
 */
sealed class CrossfadeValidation {
    object Valid : CrossfadeValidation()
    data class Warning(val message: String) : CrossfadeValidation()
    data class Invalid(val reason: String) : CrossfadeValidation()
}

/**
 * State object containing crossfade capability information
 */
data class CrossfadeCapabilityState(
    val capability: CrossfadeCapability,
    val validation: CrossfadeValidation,
    val shouldDisable: Boolean,
    val isEnabled: Boolean
) {
    val isAvailable: Boolean get() = capability is CrossfadeCapability.Available
    val hasWarning: Boolean get() = validation is CrossfadeValidation.Warning
    val hasError: Boolean get() = validation is CrossfadeValidation.Invalid
    val errorMessage: String? get() = (validation as? CrossfadeValidation.Invalid)?.reason
    val warningMessage: String? get() = (validation as? CrossfadeValidation.Warning)?.message
}
