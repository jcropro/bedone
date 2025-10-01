package app.ember.studio

import androidx.annotation.StringRes
import app.ember.studio.R

/**
 * Static sample content used to demonstrate the library shell before real scanning is wired up.
 */
object SampleLibrary {
    val songs: List<SongSummary> = listOf(
        SongSummary(
            id = "track_molten_dawn",
            title = "Molten Dawn",
            artist = "Ember Ensemble",
            album = "Glow Theory",
            durationMs = 215_000,
            rawResId = R.raw.sample_tone,
            addedTimestampMs = 1_721_260_800_000
        ),
        SongSummary(
            id = "track_cinder_groove",
            title = "Cinder Groove",
            artist = "Ashline",
            album = "Glow Theory",
            durationMs = 198_000,
            rawResId = R.raw.sample_tone,
            addedTimestampMs = 1_721_347_200_000
        ),
        SongSummary(
            id = "track_spark_echoes",
            title = "Spark Echoes",
            artist = "Radiant Fields",
            album = "Kindling",
            durationMs = 242_000,
            rawResId = R.raw.sample_tone,
            addedTimestampMs = 1_721_433_600_000
        ),
        SongSummary(
            id = "track_glass_forge",
            title = "Glass Forge",
            artist = "Helix Forge",
            album = "Molten Lines",
            durationMs = 264_000,
            rawResId = R.raw.sample_tone,
            addedTimestampMs = 1_721_520_000_000
        ),
        SongSummary(
            id = "track_ember_skyline",
            title = "Ember Skyline",
            artist = "City Sparks",
            album = "Night Copper",
            durationMs = 230_000,
            rawResId = R.raw.sample_tone,
            addedTimestampMs = 1_721_606_400_000
        )
    )

    val playlists: List<PlaylistSummary> = listOf(
        PlaylistSummary(
            id = "playlist_favourites",
            title = "My favourite",
            trackCount = 24,
            totalDurationMs = 5_400_000
        ),
        PlaylistSummary(
            id = "playlist_recently_added",
            title = "Recently added",
            trackCount = 12,
            totalDurationMs = 2_600_000
        ),
        PlaylistSummary(
            id = "playlist_workout",
            title = "Workout Fire",
            trackCount = 18,
            totalDurationMs = 4_020_000
        )
    )

    val folders: List<FolderSummary> = listOf(
        FolderSummary(
            id = "folder_downloads",
            name = "Downloads",
            path = "Storage/Downloads/Audio",
            trackCount = 32
        ),
        FolderSummary(
            id = "folder_field",
            name = "Field Recordings",
            path = "Storage/Music/Field",
            trackCount = 9
        )
    )

    val albums: List<AlbumSummary> = listOf(
        AlbumSummary(
            id = "album_glow_theory",
            title = "Glow Theory",
            artist = "Ember Ensemble",
            trackCount = 12
        ),
        AlbumSummary(
            id = "album_kindling",
            title = "Kindling",
            artist = "Radiant Fields",
            trackCount = 10
        ),
        AlbumSummary(
            id = "album_molten_lines",
            title = "Molten Lines",
            artist = "Helix Forge",
            trackCount = 11
        )
    )

    val artists: List<ArtistSummary> = listOf(
        ArtistSummary(
            id = "artist_ember_ensemble",
            name = "Ember Ensemble",
            albumCount = 2,
            trackCount = 24
        ),
        ArtistSummary(
            id = "artist_radiant_fields",
            name = "Radiant Fields",
            albumCount = 1,
            trackCount = 10
        ),
        ArtistSummary(
            id = "artist_city_sparks",
            name = "City Sparks",
            albumCount = 1,
            trackCount = 8
        )
    )

    val genres: List<GenreSummary> = listOf(
        GenreSummary(
            id = "genre_electronic",
            name = "Electronic",
            trackCount = 34
        ),
        GenreSummary(
            id = "genre_ambient",
            name = "Ambient",
            trackCount = 12
        ),
        GenreSummary(
            id = "genre_chillwave",
            name = "Chillwave",
            trackCount = 9
        )
    )

    val longformItems: List<LongformItem> = listOf(
        LongformItem(
            id = "longform_forge_stories",
            title = "Forge Stories",
            category = LongformCategory.Podcast,
            durationMs = 2_700_000,
            source = "Helix Radio"
        ),
        LongformItem(
            id = "longform_molten_chapter",
            title = "Molten Chapter 1",
            category = LongformCategory.Audiobook,
            durationMs = 4_500_000,
            source = "Molten Saga"
        ),
        LongformItem(
            id = "longform_soundscape",
            title = "Soundscape Session",
            category = LongformCategory.Unassigned,
            durationMs = 3_000_000,
            source = "Ember Archive"
        )
    )

    val videos: List<VideoSummary> = listOf(
        VideoSummary(
            id = "video_live_session",
            title = "Live Session — Ember Ensemble",
            durationMs = 360_000
        ),
        VideoSummary(
            id = "video_city_sparks",
            title = "City Sparks Visualizer",
            durationMs = 220_000
        )
    )
}

/** Library drawer destinations displayed in the navigation drawer. */
fun defaultDrawerDestinations(): List<DrawerDestination> = listOf(
    DrawerDestination(DrawerDestinationId.Library, R.string.drawer_library),
    DrawerDestination(DrawerDestinationId.Equalizer, R.string.drawer_equalizer),
    DrawerDestination(DrawerDestinationId.SleepTimer, R.string.drawer_sleep_timer),
    DrawerDestination(DrawerDestinationId.ThemeStudio, R.string.drawer_theme_studio),
    DrawerDestination(DrawerDestinationId.Widgets, R.string.drawer_widgets),
    DrawerDestination(DrawerDestinationId.ScanImport, R.string.drawer_scan_import),
    DrawerDestination(DrawerDestinationId.Settings, R.string.drawer_settings),
    DrawerDestination(DrawerDestinationId.Help, R.string.drawer_help)
)

/**
 * Summary objects used by the library shell. These will be replaced by real data when scanning lands.
 */
data class SongSummary(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val rawResId: Int?,
    val uri: String? = null,
    val addedTimestampMs: Long = 0L
)

data class PlaylistSummary(
    val id: String,
    val title: String,
    val trackCount: Int,
    val totalDurationMs: Long
)

data class FolderSummary(
    val id: String,
    val name: String,
    val path: String,
    val trackCount: Int
)

data class AlbumSummary(
    val id: String,
    val title: String,
    val artist: String,
    val trackCount: Int
)

data class ArtistSummary(
    val id: String,
    val name: String,
    val albumCount: Int,
    val trackCount: Int
)

data class GenreSummary(
    val id: String,
    val name: String,
    val trackCount: Int
)

data class LongformItem(
    val id: String,
    val title: String,
    val category: LongformCategory,
    val durationMs: Long,
    val source: String
)

enum class LongformCategory {
    Podcast,
    Audiobook,
    Unassigned
}

enum class LongformFilter(@StringRes val labelRes: Int) {
    All(R.string.longform_filter_all),
    Podcasts(R.string.longform_filter_podcasts),
    Audiobooks(R.string.longform_filter_audiobooks),
    Unassigned(R.string.longform_filter_unassigned)
}

data class VideoSummary(
    val id: String,
    val title: String,
    val durationMs: Long
)

data class DrawerDestination(
    val id: DrawerDestinationId,
    @StringRes val titleRes: Int
)

enum class DrawerDestinationId {
    Library,
    Equalizer,
    SleepTimer,
    ThemeStudio,
    Widgets,
    ScanImport,
    Settings,
    Help
}
