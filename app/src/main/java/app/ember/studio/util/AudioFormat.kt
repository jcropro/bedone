package app.ember.studio.util

import java.util.Locale

/**
 * Formats the center frequency reported by the audio equalizer into a concise label.
 */
fun formatFrequencyLabel(centerFrequencyMilliHertz: Int): String {
    val hertz = centerFrequencyMilliHertz / 1000
    return if (hertz >= 1000) {
        val kilohertz = hertz / 1000f
        if (kilohertz >= 10f) {
            String.format(Locale.US, "%.0f kHz", kilohertz)
        } else {
            String.format(Locale.US, "%.1f kHz", kilohertz)
        }
    } else {
        String.format(Locale.US, "%d Hz", hertz)
    }
}
