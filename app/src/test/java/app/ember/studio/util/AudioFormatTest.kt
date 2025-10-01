package app.ember.studio.util

import kotlin.test.Test
import kotlin.test.assertEquals

class AudioFormatTest {
    @Test
    fun formatFrequencyLabel_handlesHertzBelowOneKilohertz() {
        assertEquals("60 Hz", formatFrequencyLabel(60_000))
        assertEquals("230 Hz", formatFrequencyLabel(230_000))
    }

    @Test
    fun formatFrequencyLabel_handlesKilohertzWithDecimal() {
        assertEquals("0 Hz", formatFrequencyLabel(0))
        assertEquals("0 Hz", formatFrequencyLabel(500))
        assertEquals("0 Hz", formatFrequencyLabel(999))
        assertEquals("1.0 kHz", formatFrequencyLabel(1_000_000))
        assertEquals("2.3 kHz", formatFrequencyLabel(2_300_000))
    }

    @Test
    fun formatFrequencyLabel_roundsHighKilohertz() {
        assertEquals("10 kHz", formatFrequencyLabel(10_000_000))
        assertEquals("15 kHz", formatFrequencyLabel(15_000_000))
    }
}
