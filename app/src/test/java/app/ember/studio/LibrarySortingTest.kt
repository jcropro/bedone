package app.ember.studio

import org.junit.Assert.assertEquals
import org.junit.Test

class LibrarySortingTest {
    private val songA = SongSummary(
        id = "a",
        title = "Afterglow",
        artist = "Artist A",
        album = "Album A",
        durationMs = 120_000,
        rawResId = null,
        addedTimestampMs = 2L
    )
    private val songB = SongSummary(
        id = "b",
        title = "Beacon",
        artist = "Artist B",
        album = "Album B",
        durationMs = 240_000,
        rawResId = null,
        addedTimestampMs = 3L
    )
    private val songC = SongSummary(
        id = "c",
        title = "Chroma",
        artist = "Artist C",
        album = "Album C",
        durationMs = 90_000,
        rawResId = null,
        addedTimestampMs = 1L
    )

    private val albumA = AlbumSummary(id = "a", title = "Aurora", artist = "Artist A", trackCount = 10)
    private val albumB = AlbumSummary(id = "b", title = "Bloom", artist = "Artist B", trackCount = 8)

    private val artistA = ArtistSummary(id = "a", name = "Aria", albumCount = 1, trackCount = 10)
    private val artistB = ArtistSummary(id = "b", name = "Coda", albumCount = 2, trackCount = 14)

    @Test
    fun `sortSongs sorts by title ascending by default`() {
        val state = SongSortState(field = SongSortField.Title, direction = SortDirection.Ascending)
        val sorted = sortSongs(listOf(songB, songC, songA), state)

        assertEquals(listOf("a", "b", "c"), sorted.map { it.id })
    }

    @Test
    fun `sortSongs sorts by title descending`() {
        val state = SongSortState(field = SongSortField.Title, direction = SortDirection.Descending)
        val sorted = sortSongs(listOf(songA, songB, songC), state)

        assertEquals(listOf("c", "b", "a"), sorted.map { it.id })
    }

    @Test
    fun `sortSongs sorts by added timestamp newest first`() {
        val state = SongSortState(field = SongSortField.Added, direction = SortDirection.Descending)
        val sorted = sortSongs(listOf(songA, songB, songC), state)

        assertEquals(listOf("b", "a", "c"), sorted.map { it.id })
    }

    @Test
    fun `sortSongs sorts by duration ascending`() {
        val state = SongSortState(field = SongSortField.Duration, direction = SortDirection.Ascending)
        val sorted = sortSongs(listOf(songA, songB, songC), state)

        assertEquals(listOf("c", "a", "b"), sorted.map { it.id })
    }

    @Test
    fun `sortAlbums honors sort direction`() {
        val ascending = sortAlbums(listOf(albumB, albumA), SortDirection.Ascending)
        val descending = sortAlbums(listOf(albumA, albumB), SortDirection.Descending)

        assertEquals(listOf("a", "b"), ascending.map { it.id })
        assertEquals(listOf("b", "a"), descending.map { it.id })
    }

    @Test
    fun `sortArtists honors sort direction`() {
        val ascending = sortArtists(listOf(artistB, artistA), SortDirection.Ascending)
        val descending = sortArtists(listOf(artistA, artistB), SortDirection.Descending)

        assertEquals(listOf("a", "b"), ascending.map { it.id })
        assertEquals(listOf("b", "a"), descending.map { it.id })
    }
}
