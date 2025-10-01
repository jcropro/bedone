package app.ember.studio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchModelsTest {
    private val sampleCorpus = SearchCorpus(
        songs = listOf(
            SongSummary(
                id = "song-1",
                title = "Molten Heart",
                artist = "The Embers",
                album = "Glow",
                durationMs = 180_000,
                rawResId = null,
                addedTimestampMs = 0L
            ),
            SongSummary(
                id = "song-2",
                title = "Dawn Chorus",
                artist = "Aurora Fields",
                album = "Sunrise",
                durationMs = 210_000,
                rawResId = null,
                addedTimestampMs = 0L
            )
        ),
        playlists = listOf(
            PlaylistSummary(
                id = "playlist-1",
                title = "Morning Mix",
                trackCount = 12,
                totalDurationMs = 2_700_000
            )
        ),
        folders = listOf(
            FolderSummary(
                id = "folder-1",
                name = "Audiobooks",
                path = "/storage/Audiobooks",
                trackCount = 24
            )
        ),
        albums = listOf(
            AlbumSummary(
                id = "album-1",
                title = "Glow",
                artist = "The Embers",
                trackCount = 8
            )
        ),
        artists = listOf(
            ArtistSummary(
                id = "artist-1",
                name = "The Embers",
                albumCount = 2,
                trackCount = 18
            )
        ),
        genres = listOf(
            GenreSummary(
                id = "genre-1",
                name = "Ambient",
                trackCount = 42
            )
        ),
        longform = listOf(
            LongformItem(
                id = "longform-1",
                title = "Deep Focus Session",
                category = LongformCategory.Podcast,
                durationMs = 3_600_000,
                source = "Focus Studio"
            )
        ),
        videos = listOf(
            VideoSummary(
                id = "video-1",
                title = "Molten Heart (Live)",
                durationMs = 240_000
            )
        )
    )

    @Test
    fun searchLibrary_matchesAcrossFields() {
        val results = searchLibrary(sampleCorpus, "Molten")

        assertEquals(1, results.songs.size)
        assertEquals("song-1", results.songs.first().id)
        assertEquals(1, results.videos.size)
        assertTrue(results.playlists.isEmpty())
    }

    @Test
    fun searchLibrary_isCaseInsensitive() {
        val results = searchLibrary(sampleCorpus, "aurora")

        assertEquals(1, results.songs.size)
        assertEquals("song-2", results.songs.first().id)
    }

    @Test
    fun searchLibrary_supportsMultiTermQueriesAcrossFields() {
        val results = searchLibrary(sampleCorpus, "Glow Embers")

        assertEquals(1, results.songs.size)
        assertEquals("song-1", results.songs.first().id)
        assertEquals(1, results.albums.size)
        assertEquals("album-1", results.albums.first().id)
    }

    @Test
    fun searchLibrary_handlesPunctuationInQueries() {
        val results = searchLibrary(sampleCorpus, "Molten-Heart")

        assertEquals(1, results.songs.size)
        assertEquals("song-1", results.songs.first().id)
        assertEquals(1, results.videos.size)
        assertEquals("video-1", results.videos.first().id)
    }

    @Test
    fun searchLibrary_matchesCollapsedPunctuationInIndexedText() {
        val corpus = SearchCorpus(
            songs = listOf(
                SongSummary(
                    id = "sos-song",
                    title = "S.O.S",
                    artist = "Signal Fire",
                    album = "Call Signs",
                    durationMs = 150_000,
                    rawResId = null,
                    addedTimestampMs = 0L
                )
            ),
            playlists = emptyList(),
            folders = emptyList(),
            albums = emptyList(),
            artists = emptyList(),
            genres = emptyList(),
            longform = emptyList(),
            videos = emptyList()
        )

        val contiguousQueryResults = searchLibrary(corpus, "sos")
        val punctuatedQueryResults = searchLibrary(corpus, "S.O.S")

        assertEquals(1, contiguousQueryResults.songs.size)
        assertEquals("sos-song", contiguousQueryResults.songs.first().id)
        assertEquals(1, punctuatedQueryResults.songs.size)
        assertEquals("sos-song", punctuatedQueryResults.songs.first().id)
    }

    @Test
    fun searchLibrary_matchesDiacriticsInsensitive() {
        val corpus = SearchCorpus(
            songs = listOf(
                SongSummary(
                    id = "accent-song",
                    title = "Café del Mar",
                    artist = "Relaxé",
                    album = "Sunset Sessions",
                    durationMs = 200_000,
                    rawResId = null,
                    addedTimestampMs = 0L
                )
            ),
            playlists = emptyList(),
            folders = emptyList(),
            albums = emptyList(),
            artists = emptyList(),
            genres = emptyList(),
            longform = emptyList(),
            videos = emptyList()
        )

        val results = searchLibrary(corpus, "Cafe del")

        assertEquals(1, results.songs.size)
        assertEquals("accent-song", results.songs.first().id)
    }

    @Test
    fun searchLibrary_returnsEmptyResultsForBlankQuery() {
        val results = searchLibrary(sampleCorpus, "   ")

        assertTrue(results.isEmpty)
    }
}
