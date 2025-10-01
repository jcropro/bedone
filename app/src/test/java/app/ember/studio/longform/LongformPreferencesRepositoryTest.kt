package app.ember.studio.longform

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LongformPreferencesRepositoryTest {
    @Test
    fun parse_and_serialize_roundtrip() {
        val input = mapOf(
            "id1" to LongformPref(bookmarkMs = 120_000L, defaultSpeed = 1.5f),
            "id2" to LongformPref(bookmarkMs = null, defaultSpeed = 0.75f)
        )
        val json = LongformPreferencesRepository.serialize(input)
        val parsed = LongformPreferencesRepository.parse(json)
        assertEquals(input.keys, parsed.keys)
        assertEquals(input["id1"]?.bookmarkMs, parsed["id1"]?.bookmarkMs)
        assertEquals(input["id1"]?.defaultSpeed, parsed["id1"]?.defaultSpeed)
        assertEquals(input["id2"]?.bookmarkMs, parsed["id2"]?.bookmarkMs)
        assertEquals(input["id2"]?.defaultSpeed, parsed["id2"]?.defaultSpeed)
    }
}

