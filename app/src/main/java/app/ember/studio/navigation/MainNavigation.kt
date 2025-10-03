package app.ember.studio.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import app.ember.core.ui.design.EmberFlame
import app.ember.core.ui.design.EmberInk
import app.ember.core.ui.design.TextStrong
import app.ember.core.ui.design.TextMuted
import app.ember.studio.R
import app.ember.studio.feature.songs.SongsScreen
import app.ember.studio.feature.playlists.PlaylistsScreen
import app.ember.studio.feature.albums.AlbumsScreen
import app.ember.studio.feature.artists.ArtistsScreen
import app.ember.studio.feature.genres.GenresScreen
import app.ember.studio.feature.folders.FoldersScreen
import app.ember.studio.feature.audiobooks.AudiobooksScreen
import app.ember.studio.feature.podcasts.PodcastsScreen
import app.ember.studio.feature.videos.VideosScreen
import app.ember.studio.widgets.WidgetGalleryScreen
import app.ember.studio.ComprehensiveSettingsScreen
import app.ember.studio.PlayerViewModel
import app.ember.core.ui.theme.ThemeUiState
import app.ember.studio.DrawerDestinationId
import app.ember.studio.player.MiniPlayerDock
import app.ember.studio.player.MiniPlayerState

/**
 * Enhanced navigation structure for Ember Audio Player
 * Implements top app bar + tabs architecture as per MASTER_BLUEPRINT
 * NO BOTTOM TABS - Top app bar + tabs only
 */
@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    selectedDrawerDestination: DrawerDestinationId = DrawerDestinationId.Library,
    settingsState: PlayerViewModel.SettingsUiState? = null,
    themeState: ThemeUiState? = null,
    miniPlayerState: MiniPlayerState = MiniPlayerState(),
    onNavigateToNowPlaying: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onToggleRearmOnBootEnabled: ((Boolean) -> Unit)? = null,
    onSelectRearmMinMinutes: ((Int) -> Unit)? = null,
    onToggleSkipSilenceEnabled: ((Boolean) -> Unit)? = null,
    onSelectCrossfadeMs: ((Int) -> Unit)? = null,
    onSelectLongformThresholdMinutes: ((Int) -> Unit)? = null,
    onToggleUseHaptics: ((Boolean) -> Unit)? = null,
    onRescanLibrary: (() -> Unit)? = null,
    onOpenScanImport: (() -> Unit)? = null,
    onSelectThemeOption: ((Int) -> Unit)? = null,
    onToggleDarkTheme: ((Boolean) -> Unit)? = null,
    onToggleDynamicColor: ((Boolean) -> Unit)? = null,
    onToggleAmoledBlack: ((Boolean) -> Unit)? = null,
    onDrawerDestinationSelected: (DrawerDestinationId) -> Unit = {},
    onPlayPauseClick: () -> Unit = {},
    onSkipNextClick: () -> Unit = {},
    onSkipPreviousClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.Songs) }
    var isDrawerOpen by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Top app bar with integrated tabs for library
            if (selectedDrawerDestination == DrawerDestinationId.Library) {
                LibraryTopAppBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onNavigateToNowPlaying = onNavigateToNowPlaying,
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        },
        bottomBar = {
            // Mini-player persistent dock
            MiniPlayerDock(
                trackTitle = miniPlayerState.trackTitle,
                trackArtist = miniPlayerState.trackArtist,
                albumArtUrl = miniPlayerState.albumArtUrl,
                isPlaying = miniPlayerState.isPlaying,
                isVisible = miniPlayerState.isVisible,
                onPlayPauseClick = onPlayPauseClick,
                onSkipNextClick = onSkipNextClick,
                onSkipPreviousClick = onSkipPreviousClick,
                onExpandClick = onNavigateToNowPlaying
            )
        }
    ) { paddingValues ->
            when (selectedDrawerDestination) {
            DrawerDestinationId.Library -> {
                when (selectedTab) {
                    LibraryTab.Songs -> SongsScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                    LibraryTab.Playlists -> PlaylistsScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                    LibraryTab.Folders -> FoldersScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                    LibraryTab.Albums -> AlbumsScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                    LibraryTab.Artists -> ArtistsScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                    LibraryTab.Genres -> GenresScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                    LibraryTab.Audiobooks -> AudiobooksScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                    LibraryTab.Podcasts -> PodcastsScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                    LibraryTab.Videos -> VideosScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            DrawerDestinationId.Widgets -> {
                WidgetGalleryScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            DrawerDestinationId.Equalizer -> {
                EqualizerScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            DrawerDestinationId.SleepTimer -> {
                SleepTimerScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            DrawerDestinationId.ThemeStudio -> {
                ThemeStudioScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            DrawerDestinationId.ScanImport -> {
                ScanImportScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            DrawerDestinationId.Settings -> {
                if (settingsState != null && themeState != null) {
                    ComprehensiveSettingsScreen(
                        settingsState = settingsState,
                        themeState = themeState,
                        onToggleRearmOnBootEnabled = onToggleRearmOnBootEnabled ?: {},
                        onSelectRearmMinMinutes = onSelectRearmMinMinutes ?: {},
                        onToggleSkipSilenceEnabled = onToggleSkipSilenceEnabled ?: {},
                        onSelectCrossfadeMs = onSelectCrossfadeMs ?: {},
                        onSelectLongformThresholdMinutes = onSelectLongformThresholdMinutes ?: {},
                        onToggleUseHaptics = onToggleUseHaptics ?: {},
                        onRescanLibrary = onRescanLibrary ?: {},
                        onOpenScanImport = onOpenScanImport ?: {},
                        onSelectThemeOption = onSelectThemeOption ?: {},
                        onToggleDarkTheme = onToggleDarkTheme ?: {},
                        onToggleDynamicColor = onToggleDynamicColor ?: {},
                        onToggleAmoledBlack = onToggleAmoledBlack ?: {},
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    SettingsScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            DrawerDestinationId.Help -> {
                HelpScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

enum class LibraryTab(
    val titleRes: Int,
    val icon: ImageVector
) {
    Songs(R.string.tab_songs, Icons.Filled.LibraryMusic),
    Playlists(R.string.tab_playlists, Icons.AutoMirrored.Filled.PlaylistPlay),
    Folders(R.string.tab_folders, Icons.Filled.Folder),
    Albums(R.string.tab_albums, Icons.Filled.MusicNote),
    Artists(R.string.tab_artists, Icons.Filled.Person),
    Genres(R.string.tab_genres, Icons.AutoMirrored.Filled.QueueMusic),
    Audiobooks(R.string.tab_audiobooks, Icons.Filled.LibraryMusic),
    Podcasts(R.string.tab_podcasts, Icons.AutoMirrored.Filled.QueueMusic),
    Videos(R.string.tab_videos, Icons.Filled.VideoLibrary)
}

// Placeholder screens - these will be implemented with full functionality

// All library screens are now implemented with comprehensive functionality

// Drawer destination screens
@Composable
fun EqualizerScreen(modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = "Equalizer Screen",
        modifier = modifier
    )
}

@Composable
fun SleepTimerScreen(modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = "Sleep Timer Screen",
        modifier = modifier
    )
}

@Composable
fun ThemeStudioScreen(modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = "Theme Studio Screen",
        modifier = modifier
    )
}

@Composable
fun ScanImportScreen(modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = "Scan Import Screen",
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = "Settings Screen",
        modifier = modifier
    )
}

@Composable
fun HelpScreen(modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = "Help Screen",
        modifier = modifier
    )
}