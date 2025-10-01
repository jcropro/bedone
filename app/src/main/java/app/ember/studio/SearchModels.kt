package app.ember.studio

import androidx.annotation.StringRes
import java.text.Normalizer
import java.util.Locale

enum class SearchBucket(@StringRes val labelRes: Int) {
    All(R.string.search_bucket_all),
    Songs(R.string.search_bucket_songs),
    Playlists(R.string.search_bucket_playlists),
    Folders(R.string.search_bucket_folders),
    Albums(R.string.search_bucket_albums),
    Artists(R.string.search_bucket_artists),
    Genres(R.string.search_bucket_genres),
    Longform(R.string.search_bucket_longform),
    Videos(R.string.search_bucket_videos)
}

data class SearchResults(
    val songs: List<SongSummary> = emptyList(),
    val playlists: List<PlaylistSummary> = emptyList(),
    val folders: List<FolderSummary> = emptyList(),
    val albums: List<AlbumSummary> = emptyList(),
    val artists: List<ArtistSummary> = emptyList(),
    val genres: List<GenreSummary> = emptyList(),
    val longform: List<LongformItem> = emptyList(),
    val videos: List<VideoSummary> = emptyList()
) {
    val totalCount: Int
        get() =
            songs.size +
                playlists.size +
                folders.size +
                albums.size +
                artists.size +
                genres.size +
                longform.size +
                videos.size

    val isEmpty: Boolean get() = totalCount == 0
}

data class SearchUiState(
    val query: String = "",
    val bucket: SearchBucket = SearchBucket.All,
    val results: SearchResults = SearchResults(),
    val isEmpty: Boolean = false
)

data class SearchCorpus(
    val songs: List<SongSummary>,
    val playlists: List<PlaylistSummary>,
    val folders: List<FolderSummary>,
    val albums: List<AlbumSummary>,
    val artists: List<ArtistSummary>,
    val genres: List<GenreSummary>,
    val longform: List<LongformItem>,
    val videos: List<VideoSummary>
)

fun searchLibrary(corpus: SearchCorpus, query: String): SearchResults {
    val terms = buildSearchTerms(query)
    if (terms.isEmpty()) return SearchResults()
    val collapsedTerms = terms.map { it.replace(" ", "") }

    fun matches(vararg values: String?): Boolean {
        val combined = values.filterNot { it.isNullOrBlank() }.joinToString(separator = " ")
        if (combined.isBlank()) return false
        val normalized = normalizeForSearch(combined)
        if (normalized.isEmpty()) return false
        val normalizedCollapsed = normalized.replace(" ", "")
        return terms.indices.all { index ->
            val term = terms[index]
            val collapsedTerm = collapsedTerms[index]
            normalized.contains(term) || normalizedCollapsed.contains(collapsedTerm)
        }
    }

    val songs = corpus.songs.filter { song ->
        matches(song.title, song.artist, song.album)
    }
    val playlists = corpus.playlists.filter { playlist ->
        matches(playlist.title)
    }
    val folders = corpus.folders.filter { folder ->
        matches(folder.name, folder.path)
    }
    val albums = corpus.albums.filter { album ->
        matches(album.title, album.artist)
    }
    val artists = corpus.artists.filter { artist ->
        matches(artist.name)
    }
    val genres = corpus.genres.filter { genre ->
        matches(genre.name)
    }
    val longform = corpus.longform.filter { item ->
        matches(item.title, item.source, item.category.name)
    }
    val videos = corpus.videos.filter { video ->
        matches(video.title)
    }

    return SearchResults(
        songs = songs,
        playlists = playlists,
        folders = folders,
        albums = albums,
        artists = artists,
        genres = genres,
        longform = longform,
        videos = videos
    )
}

private fun buildSearchTerms(rawQuery: String): List<String> {
    if (rawQuery.isBlank()) return emptyList()
    return WHITESPACE_REGEX
        .split(rawQuery.trim())
        .flatMap { token ->
            val normalized = normalizeForSearch(token)
            if (normalized.isEmpty()) {
                emptyList()
            } else {
                WHITESPACE_REGEX.split(normalized).filter { it.isNotEmpty() }
            }
        }
}

private fun normalizeForSearch(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return ""
    val normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
    val withoutDiacritics = DIACRITICS_REGEX.replace(normalized, "")
    val withoutPunctuation = PUNCTUATION_REGEX.replace(withoutDiacritics, " ")
    val collapsedWhitespace = WHITESPACE_REGEX.replace(withoutPunctuation, " ").trim()
    return collapsedWhitespace.lowercase(Locale.US)
}

private val WHITESPACE_REGEX = "\\s+".toRegex()
private val DIACRITICS_REGEX = "\\p{M}+".toRegex()
private val PUNCTUATION_REGEX = "[\\p{Punct}]".toRegex()
