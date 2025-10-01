@file:Suppress("DEPRECATION")

package app.ember.studio.audio

import android.content.Context
import android.media.AudioManager
import android.media.audiofx.Virtualizer
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Compatibility wrapper around the legacy [Virtualizer] audio effect.
 *
 * The platform implementation shipped with API 34 marks the original class as deprecated in
 * favour of a new framework API that is not yet exposed on the minimum supported SDK levels.
 * Rather than crash on newer builds we disable the feature when the deprecated implementation
 * is unavailable while keeping the interface that the view-model expects.
 */
interface VirtualizerHandle {
    var isEnabled: Boolean
    val strengthSupported: Boolean
    val roundedStrength: Int

    fun setStrength(strength: Int)

    fun release()
}

object VirtualizerCompat {
    /** Maximum effect strength defined by the legacy platform virtualizer implementation. */
    private const val MAX_STRENGTH = 1000

    fun create(context: Context, sessionId: Int): VirtualizerHandle? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ->
                // API 34+: Prefer the platform Spatializer. Use reflection to avoid class
                // verification issues on lower API levels and to keep minSdk at 24.
                SpatializerHandle.from(context)
            else -> LegacyVirtualizerHandle(sessionId)
        }
    }

    private class LegacyVirtualizerHandle(sessionId: Int) : VirtualizerHandle {
        private val delegate = Virtualizer(0, sessionId).apply { enabled = false }

        override var isEnabled: Boolean
            get() = delegate.enabled
            set(value) {
                delegate.enabled = value
            }

        override val strengthSupported: Boolean
            get() = delegate.strengthSupported

        override val roundedStrength: Int
            get() = delegate.roundedStrength.toInt()

        override fun setStrength(strength: Int) {
            if (!strengthSupported) return
            val clamped = strength.coerceIn(0, MAX_STRENGTH)
            delegate.setStrength(clamped.toShort())
        }

        override fun release() {
            delegate.release()
        }
    }

    /**
     * Minimal wrapper around the modern platform Spatializer (API 31+) using reflection to
     * avoid hard dependencies when running on older devices. Strength is not supported.
     */
    @RequiresApi(31)
    private class SpatializerHandle private constructor(
        private val spatializer: Any,
        private val isEnabledGetter: java.lang.reflect.Method,
        private val setEnabledSetter: java.lang.reflect.Method,
        private val isAvailableGetter: java.lang.reflect.Method
    ) : VirtualizerHandle {

        override var isEnabled: Boolean
            get() = (isEnabledGetter.invoke(spatializer) as? Boolean) == true
            set(value) {
                setEnabledSetter.invoke(spatializer, value)
            }

        // Spatializer does not expose effect strength to apps.
        override val strengthSupported: Boolean = false
        override val roundedStrength: Int = 0

        override fun setStrength(strength: Int) {
            // Map non-zero strength to enabled for compatibility if callers still invoke this.
            setEnabledSetter.invoke(spatializer, strength > 0)
        }

        override fun release() {
            // No explicit release API for Spatializer.
        }

        companion object {
            fun from(context: Context): VirtualizerHandle? {
                return try {
                    val am = context.getSystemService(AudioManager::class.java) ?: return null
                    val getSpatializer = AudioManager::class.java.methods.firstOrNull {
                        it.name == "getSpatializer" && it.parameterCount == 0
                    } ?: return null
                    val spatializer = getSpatializer.invoke(am) ?: return null

                    val clazz = spatializer.javaClass
                    val isAvailable = clazz.methods.firstOrNull { it.name == "isAvailable" && it.parameterCount == 0 }
                        ?: return null
                    val isEnabled = clazz.methods.firstOrNull { it.name == "isEnabled" && it.parameterCount == 0 }
                        ?: return null
                    val setEnabled = clazz.methods.firstOrNull { it.name == "setEnabled" && it.parameterCount == 1 }
                        ?: return null

                    val available = (isAvailable.invoke(spatializer) as? Boolean) == true
                    if (!available) return null

                    SpatializerHandle(
                        spatializer = spatializer,
                        isEnabledGetter = isEnabled,
                        setEnabledSetter = setEnabled,
                        isAvailableGetter = isAvailable
                    )
                } catch (_: Throwable) {
                    null
                }
            }
        }
    }
}
