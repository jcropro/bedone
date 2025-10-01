package app.ember.studio

import org.junit.Assert.assertEquals
import org.junit.Test

class LongformHeuristicsTest {

    @Test
    fun `existing category is honored`() {
        val result = suggestLongformCategory(
            title = "Daily Spark Episode 4",
            source = "Spark Radio",
            existingCategory = LongformCategory.Podcast,
            durationMs = 35 * 60 * 1000L
        )

        assertEquals(LongformCategory.Podcast, result)
    }

    @Test
    fun `podcast tokens are detected`() {
        val result = suggestLongformCategory(
            title = "Molten Show Episode 12",
            source = "Helix Radio",
            existingCategory = LongformCategory.Unassigned,
            durationMs = 45 * 60 * 1000L
        )

        assertEquals(LongformCategory.Podcast, result)
    }

    @Test
    fun `audiobook tokens are detected`() {
        val result = suggestLongformCategory(
            title = "Molten Chapter 3",
            source = "Molten Saga",
            existingCategory = LongformCategory.Unassigned,
            durationMs = 75 * 60 * 1000L
        )

        assertEquals(LongformCategory.Audiobook, result)
    }

    @Test
    fun `long duration defaults to audiobook`() {
        val result = suggestLongformCategory(
            title = "Three Hour Ambient Journey",
            source = "Ember Archive",
            existingCategory = LongformCategory.Unassigned,
            durationMs = LONGFORM_AUDIOBOOK_DURATION_MS + 1_000L
        )

        assertEquals(LongformCategory.Audiobook, result)
    }

    @Test
    fun `unknown tokens remain unassigned`() {
        val result = suggestLongformCategory(
            title = "Soundscape Session",
            source = "Ember Archive",
            existingCategory = LongformCategory.Unassigned,
            durationMs = 30 * 60 * 1000L
        )

        assertEquals(LongformCategory.Unassigned, result)
    }
}
