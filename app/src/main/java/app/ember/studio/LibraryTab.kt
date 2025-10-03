package app.ember.studio

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector
import app.ember.studio.R

/**
 * Library Tab enumeration - MASTER_BLUEPRINT compliant
 * 
 * Tabs: Songs, Playlists, Folders, Albums, Artists, Genres, Audiobooks, Podcasts, Videos
 * These are the exact tabs specified in the MASTER_BLUEPRINT.md
 */
enum class LibraryTab(
    val icon: ImageVector,
    @StringRes val titleRes: Int
) {
    Songs(
        icon = Icons.Filled.LibraryMusic,
        titleRes = R.string.tab_songs
    ),
    Playlists(
        icon = Icons.AutoMirrored.Filled.PlaylistPlay,
        titleRes = R.string.tab_playlists
    ),
    Folders(
        icon = Icons.Filled.Folder,
        titleRes = R.string.tab_folders
    ),
    Albums(
        icon = Icons.Filled.MusicNote,
        titleRes = R.string.tab_albums
    ),
    Artists(
        icon = Icons.Filled.Person,
        titleRes = R.string.tab_artists
    ),
    Genres(
        icon = Icons.Filled.Category,
        titleRes = R.string.tab_genres
    ),
    Audiobooks(
        icon = Icons.AutoMirrored.Filled.MenuBook,
        titleRes = R.string.tab_audiobooks
    ),
    Podcasts(
        icon = Icons.Filled.Radio,
        titleRes = R.string.tab_podcasts
    ),
    Videos(
        icon = Icons.Filled.VideoLibrary,
        titleRes = R.string.tab_videos
    )
}
