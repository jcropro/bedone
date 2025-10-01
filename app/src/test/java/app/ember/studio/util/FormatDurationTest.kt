package app.ember.studio.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatDurationTest {
    @Test
    fun `formats sub-minute durations`() {
        assertEquals("0:05", formatDuration(5_000))
    }

    @Test
    fun `formats minutes`() {
        assertEquals("3:20", formatDuration(200_000))
    }

    @Test
    fun `formats hours`() {
        assertEquals("1:03:05", formatDuration(3_785_000))
    }
}
