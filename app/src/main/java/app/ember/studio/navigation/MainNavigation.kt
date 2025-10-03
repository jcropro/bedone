package app.ember.studio.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
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
import app.ember.studio.LibraryTab
import app.ember.studio.player.MiniPlayerDock
import app.ember.studio.player.MiniPlayerState

/**
 * Main Navigation - MASTER_BLUEPRINT compliant
 * 
 * CRITICAL: NO BOTTOM TABS - Top app bar + tabs only as per blueprint
 * Tabs: Songs, Playlists, Folders, Albums, Artists, Genres, Audiobooks, Podcasts, Videos
 * Underline: 2-3px with animated slide; color is warm or theme accent
 * Title: on left (bold); Actions: on right (Themes, Search, Settings)
 * Hamburger: on left opens drawer
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            EmberNavigationDrawer(
                selectedDestination = selectedDrawerDestination,
                onDestinationSelected = { destination ->
                    onDrawerDestinationSelected(destination)
                    scope.launch { drawerState.close() }
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                // Top app bar with integrated tabs for library - MASTER_BLUEPRINT compliant
                if (selectedDrawerDestination == DrawerDestinationId.Library) {
                    LibraryTopAppBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        onNavigateToNowPlaying = onNavigateToNowPlaying,
                        onNavigateToSettings = onNavigateToSettings,
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        }
                    )
                }
            },
            bottomBar = {
                // Mini-player persistent dock - MASTER_BLUEPRINT compliant
                // Docked at bottom; 64-72dp height
                // Left: circular art; center: title/artist; right: play/pause and queue icon
                // Progress bar runs along top edge (1-2dp)
                MiniPlayerDock(
                    trackTitle = miniPlayerState.trackTitle,
                    trackArtist = miniPlayerState.trackArtist,
                    albumArtUrl = miniPlayerState.albumArtUrl,
                    isPlaying = miniPlayerState.isPlaying,
                    onPlayPauseClick = onPlayPauseClick,
                    onSkipNextClick = onSkipNextClick,
                    onSkipPreviousClick = onSkipPreviousClick,
                    onExpandClick = onNavigateToNowPlaying
                )
            }
        ) { paddingValues ->
        // Main content area - MASTER_BLUEPRINT compliant
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
            DrawerDestinationId.Settings -> {
                ComprehensiveSettingsScreen(
                    modifier = Modifier.padding(paddingValues),
                    settingsState = settingsState ?: PlayerViewModel.SettingsUiState(),
                    themeState = themeState ?: ThemeUiState(),
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
                    onToggleAmoledBlack = onToggleAmoledBlack ?: {}
                )
            }
            DrawerDestinationId.Widgets -> {
                WidgetGalleryScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                // Placeholder for other destinations
                Text(
                    text = "Coming Soon",
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}}
