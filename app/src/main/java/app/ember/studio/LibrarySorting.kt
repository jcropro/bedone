package app.ember.studio

import androidx.annotation.StringRes

/** Sort fields available for the Songs tab. */
enum class SongSortField(
    @StringRes val labelRes: Int,
    val defaultDirection: SortDirection
) {
    Title(labelRes = R.string.song_sort_field_title, defaultDirection = SortDirection.Ascending),
    Added(labelRes = R.string.song_sort_field_added, defaultDirection = SortDirection.Descending),
    Duration(labelRes = R.string.song_sort_field_duration, defaultDirection = SortDirection.Ascending)
}

/** Sort direction shared across library tabs. */
enum class SortDirection(@StringRes val labelRes: Int) {
    Ascending(labelRes = R.string.sort_direction_ascending),
    Descending(labelRes = R.string.sort_direction_descending);

    fun toggled(): SortDirection = if (this == Ascending) Descending else Ascending
}

/** State holder describing the current Songs tab sort configuration. */
data class SongSortState(
    val field: SongSortField = SongSortField.Title,
    val direction: SortDirection = SongSortField.Title.defaultDirection
)

internal fun sortSongs(items: List<SongSummary>, sortState: SongSortState): List<SongSummary> {
    val comparator = when (sortState.field) {
        SongSortField.Title -> compareBy<SongSummary> { it.title.lowercase() }
        SongSortField.Added -> compareBy<SongSummary> { it.addedTimestampMs }
        SongSortField.Duration -> compareBy<SongSummary> { it.durationMs }
    }
    val sorted = items.sortedWith(comparator)
    return if (sortState.direction == SortDirection.Ascending) sorted else sorted.reversed()
}

internal fun sortAlbums(items: List<AlbumSummary>, direction: SortDirection): List<AlbumSummary> {
    val sorted = items.sortedBy { it.title.lowercase() }
    return if (direction == SortDirection.Ascending) sorted else sorted.reversed()
}

internal fun sortArtists(items: List<ArtistSummary>, direction: SortDirection): List<ArtistSummary> {
    val sorted = items.sortedBy { it.name.lowercase() }
    return if (direction == SortDirection.Ascending) sorted else sorted.reversed()
}
