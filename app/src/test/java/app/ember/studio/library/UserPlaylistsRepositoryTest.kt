package app.ember.studio.library

import org.junit.Assert.assertEquals
import org.junit.Test

class UserPlaylistsRepositoryTest {
    @Test
    fun parse_and_serialize_roundtrip() {
        val p1 = UserPlaylist(id = "user:1", title = "Favorites", items = listOf("media:10", "media:20"))
        val p2 = UserPlaylist(id = "user:2", title = "Chill", items = listOf("media:30"))
        val original = mapOf(p1.id to p1, p2.id to p2)
        val json = UserPlaylistsRepository.serialize(original)
        val parsed = UserPlaylistsRepository.parse(json)
        assertEquals(original.keys, parsed.keys)
        assertEquals(original[p1.id]?.title, parsed[p1.id]?.title)
        assertEquals(original[p1.id]?.items, parsed[p1.id]?.items)
        assertEquals(original[p2.id]?.title, parsed[p2.id]?.title)
        assertEquals(original[p2.id]?.items, parsed[p2.id]?.items)
    }
}

