@file:androidx.annotation.OptIn(UnstableApi::class)

package app.ember.studio

import androidx.annotation.OptIn as AndroidOptIn

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay10
import androidx.compose.material.icons.outlined.Forward10
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.RepeatOne
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.outlined.VolumeOff
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import app.ember.core.ui.theme.EmberAudioPlayerTheme
import app.ember.studio.art.ArtworkColor
import app.ember.studio.art.ArtworkPaletteCache
import app.ember.core.ui.theme.ThemeUiState
import app.ember.studio.sleep.SleepTimerUiState
import app.ember.studio.sleep.SleepTimerEndAction
import app.ember.studio.util.formatDuration
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout

/**
 * NOTE:
 * - This file references many domain/UI types (e.g., HomeUiState, PlayerUiState, R.string.*)
 *   that are expected to exist elsewhere in the project.
 * - We provide only tiny local helpers where strictly necessary for compilation (e.g., playback speed label).
 */

// ---- App shell ---------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@AndroidOptIn(UnstableApi::class)
@UnstableApi
@Composable
fun EmberAudioPlayerApp(
    homeState: HomeUiState,
    playerState: PlayerUiState,
    equalizerState: EqualizerUiState,
    lyricsState: LyricsUiState,
    themeState: ThemeUiState,
    settingsState: PlayerViewModel.SettingsUiState,
    tagEditorState: TagEditorUiState,
    onboardingState: OnboardingUiState,
    sleepTimerState: SleepTimerUiState,
    globalStatusMessage: String?,
    onConsumeGlobalStatusMessage: () -> Unit,
    onSelectSleepTimerQuickDuration: (Int) -> Unit,
    onSleepTimerHoursChanged: (String) -> Unit,
    onSleepTimerMinutesChanged: (String) -> Unit,
    onSleepTimerFadeToggle: (Boolean) -> Unit,
    onSleepTimerEndActionSelected: (SleepTimerEndAction) -> Unit,
    onStartSleepTimer: () -> Unit,
    onCancelSleepTimer: () -> Unit,
    onDismissSleepTimerMessage: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onPlaybackSpeedSelected: (Float) -> Unit,
    onEqualizerEnabledChange: (Boolean) -> Unit,
    onBandLevelChange: (Int, Int) -> Unit,
    onPresetSelected: (Int) -> Unit,
    onBassBoostChange: (Int) -> Unit,
    onVirtualizerChange: (Int) -> Unit,
    onReverbSelected: (Short) -> Unit,
    onResetEqualizer: () -> Unit,
    onLyricsDraftChange: (String) -> Unit,
    onLyricsSave: () -> Unit,
    onLyricsClear: () -> Unit,
    onSampleLyrics: () -> Unit,
    onSelectThemeOption: (Int) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onToggleDynamicColor: (Boolean) -> Unit,
    onToggleAmoledBlack: (Boolean) -> Unit,
    onUpdateTagTitle: (String) -> Unit,
    onUpdateTagArtist: (String) -> Unit,
    onUpdateTagAlbum: (String) -> Unit,
    onSaveTags: () -> Unit,
    onResetTags: () -> Unit,
    onShareCurrentSong: () -> Unit,
    onShareSong: (String) -> Unit,
    onAddToFavorites: (String) -> Unit,
    onAddSongToPlaylist: (String, String) -> Unit,
    onCreatePlaylistAndAdd: (String, String) -> Unit,
    onTabSelected: (HomeTab) -> Unit,
    onDrawerDestinationSelected: (DrawerDestinationId) -> Unit,
    onSongSelected: (String) -> Unit,
    onPlayAllSongs: () -> Unit,
    onPlayAllVideos: () -> Unit,
    onRequestVideoPermission: () -> Unit,
    onPlayVideo: (String) -> Unit,
    isVideoPermissionGranted: Boolean,
    onPlayPlaylist: (String) -> Unit,
    onOpenPlaylistDetail: (String) -> Unit,
    onClosePlaylistDetail: () -> Unit,
    onImportPlaylist: () -> Unit,
    onMovePlaylistItemUp: (String, String) -> Unit,
    onMovePlaylistItemDown: (String, String) -> Unit,
    onRemoveFromUserPlaylist: (String, String) -> Unit,
    onExportUserPlaylist: (String) -> Unit,
    onVideoSortToggle: () -> Unit,
    onVideoSortFieldSelected: (VideoSortField) -> Unit,
    onToggleRearmOnBootEnabled: (Boolean) -> Unit,
    onSelectRearmMinMinutes: (Int) -> Unit,
    onToggleSkipSilenceEnabled: (Boolean) -> Unit,
    onSelectCrossfadeMs: (Int) -> Unit,
    onToggleUseHaptics: (Boolean) -> Unit,
    onSelectLongformThresholdMinutes: (Int) -> Unit,
    onShuffleAllSongs: () -> Unit,
    onRescanLibrary: () -> Unit,
    songSort: SongSortState,
    albumSortDirection: SortDirection,
    artistSortDirection: SortDirection,
    onSongSortFieldSelected: (SongSortField) -> Unit,
    onSongSortDirectionToggle: () -> Unit,
    onAlbumSortToggle: () -> Unit,
    onArtistSortToggle: () -> Unit,
    onToggleQueue: () -> Unit,
    onDismissQueue: () -> Unit,
    onRemoveFromQueue: (String) -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit,
    onAddBookmark: () -> Unit,
    onResumeBookmark: () -> Unit,
    onClearBookmark: () -> Unit,
    onSelectLongformFilter: (LongformFilter) -> Unit,
    onOnboardingWelcomeContinue: () -> Unit,
    onRequestAudioPermission: () -> Unit,
    onChooseFolders: () -> Unit,
    safFolders: List<String>,
    onRemoveSafFolder: (String) -> Unit,
    onAssignAllLongform: (LongformCategory) -> Unit,
    onChooseLongformIndividually: () -> Unit,
    onLongformSelectionChange: (String, LongformCategory) -> Unit,
    onApplyLongformSelection: () -> Unit,
    onSkipLongform: () -> Unit,
    onUndoLongformChange: () -> Unit,
    onConsumeOnboardingMessage: () -> Unit,
    onCompleteOnboarding: () -> Unit,
    onClearLongformBookmarkFor: (String) -> Unit,
    exoPlayer: ExoPlayer? = null,
    isShuffleEnabled: Boolean = false,
    repeatMode: RepeatUi = RepeatUi.Off,
    modifier: Modifier = Modifier
) {
    EmberAudioPlayerTheme(themeState = themeState) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val queueSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val nowPlayingSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val lyricsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val tagSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
        val showMessage: (String) -> Unit = { message ->
            if (message.isNotBlank()) {
                scope.launch { snackbarHostState.showSnackbar(message = message) }
            }
        }

        // Surface global one-shot messages from the ViewModel
        androidx.compose.runtime.LaunchedEffect(globalStatusMessage) {
            val msg = globalStatusMessage
            if (msg != null) {
                showMessage(msg)
                onConsumeGlobalStatusMessage()
            }
        }

        var showNowPlayingSheet by rememberSaveable { mutableStateOf(false) }
        var showLyricsSheet by rememberSaveable { mutableStateOf(false) }
        var showTagEditorSheet by rememberSaveable { mutableStateOf(false) }
        var isSearchVisible by rememberSaveable { mutableStateOf(false) }
        var searchQuery by rememberSaveable { mutableStateOf("") }
        var activeSearchBucket by rememberSaveable { mutableStateOf(SearchBucket.All) }

        val searchResults = remember(homeState, searchQuery) {
            if (searchQuery.isBlank()) {
                SearchResults()
            } else {
                searchLibrary(
                    SearchCorpus(
                        songs = homeState.songs,
                        playlists = homeState.playlists,
                        folders = homeState.folders,
                        albums = homeState.albums,
                        artists = homeState.artists,
                        genres = homeState.genres,
                        longform = homeState.longformItems,
                        videos = homeState.videos
                    ),
                    searchQuery
                )
            }
        }
        val isSearchEmpty = searchQuery.isNotBlank() && searchResults.isEmpty
        val searchUiState = remember(searchQuery, activeSearchBucket, searchResults, isSearchEmpty) {
            SearchUiState(
                query = searchQuery,
                bucket = activeSearchBucket,
                results = searchResults,
                isEmpty = isSearchEmpty
            )
        }

        if (homeState.isQueueVisible) {
            ModalBottomSheet(
                onDismissRequest = onDismissQueue,
                sheetState = queueSheetState
            ) {
                QueueSheet(
                    queueState = homeState.queue,
                    onSongSelected = onSongSelected,
                    onRemoveFromQueue = onRemoveFromQueue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
            }
        }

        if (showNowPlayingSheet) {
            ModalBottomSheet(
                onDismissRequest = { showNowPlayingSheet = false },
                sheetState = nowPlayingSheetState
            ) {
                NowPlayingScreen(
                    state = playerState,
                    player = exoPlayer,
                    onTogglePlayPause = onTogglePlayPause,
                    onToggleShuffle = onToggleShuffle,
                    onCycleRepeat = onCycleRepeat,
                    onSeekTo = onSeekTo,
                    onPlaybackSpeedSelected = onPlaybackSpeedSelected,
                    onAddBookmark = onAddBookmark,
                    onResumeBookmark = onResumeBookmark,
                    onClearBookmark = onClearBookmark,
                    onShowLyrics = { showLyricsSheet = true },
                    onShowTagEditor = { showTagEditorSheet = true },
                    isShuffleEnabled = isShuffleEnabled,
                    repeatMode = repeatMode,
                    useHaptics = settingsState.useHaptics,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )
            }
        }

        if (homeState.selectedPlaylist != null) {
            ModalBottomSheet(
                onDismissRequest = onClosePlaylistDetail,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                PlaylistDetailScreen(
                    state = homeState.selectedPlaylist,
                    onPlay = { onPlayPlaylist(homeState.selectedPlaylist.id) },
                    onShuffle = {},
                    onSongSelected = onSongSelected,
                    onMoveUp = { id -> homeState.selectedPlaylist?.let { onMovePlaylistItemUp(it.id, id) } },
                    onMoveDown = { id -> homeState.selectedPlaylist?.let { onMovePlaylistItemDown(it.id, id) } },
                    onRemove = { id -> homeState.selectedPlaylist?.let { onRemoveFromUserPlaylist(it.id, id) } },
                    onExport = homeState.selectedPlaylist?.let { pl -> if (pl.id.startsWith("user:")) { { onExportUserPlaylist(pl.id) } } else null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
            }
        }

        if (showLyricsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showLyricsSheet = false },
                sheetState = lyricsSheetState
            ) {
                LyricsScreen(
                    state = lyricsState,
                    onDraftChange = onLyricsDraftChange,
                    onSave = {
                        onLyricsSave()
                        showLyricsSheet = false
                    },
                    onClear = onLyricsClear,
                    onLoadSample = onSampleLyrics,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
            }
        }

        if (showTagEditorSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTagEditorSheet = false },
                sheetState = tagSheetState
            ) {
                TagEditorScreen(
                    state = tagEditorState,
                    onTitleChange = onUpdateTagTitle,
                    onArtistChange = onUpdateTagArtist,
                    onAlbumChange = onUpdateTagAlbum,
                    onSave = {
                        onSaveTags()
                        showTagEditorSheet = false
                    },
                    onReset = onResetTags,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
            }
        }

        Box(modifier = modifier.fillMaxSize()) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    DrawerContent(
                        destinations = homeState.drawerDestinations,
                        selected = homeState.selectedDrawerDestination,
                        onDestinationSelected = {
                            onDrawerDestinationSelected(it)
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            ) {
                Scaffold(
                    topBar = {
                        LibraryTopBar(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onSearch = {
                                activeSearchBucket = SearchBucket.All
                                isSearchVisible = true
                            },
                            onOpenThemeStudio = {
                                onDrawerDestinationSelected(DrawerDestinationId.ThemeStudio)
                            },
                            onOpenSettings = {
                                onDrawerDestinationSelected(DrawerDestinationId.Settings)
                            }
                        )
                    },
                    snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        MiniPlayer(
                            state = playerState,
                            onTogglePlayPause = onTogglePlayPause,
                            onPlayNext = onPlayNext,
                            onPlayPrevious = onPlayPrevious,
                            onToggleShuffle = onToggleShuffle,
                            onCycleRepeat = onCycleRepeat,
                            isShuffleEnabled = isShuffleEnabled,
                            repeatMode = repeatMode,
                            onOpenNowPlaying = { showNowPlayingSheet = true },
                            onShowQueue = onToggleQueue,
                            modifier = Modifier.navigationBarsPadding()
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    when (homeState.selectedDrawerDestination) {
                        DrawerDestinationId.Library -> LibraryContent(
                            homeState = homeState,
                            playerState = playerState,
                            equalizerState = equalizerState,
                            lyricsState = lyricsState,
                            themeState = themeState,
                            tagEditorState = tagEditorState,
                            onRescanLibrary = onRescanLibrary,
                            onChooseFolders = onChooseFolders,
                            onRequestAudioPermission = onRequestAudioPermission,
                            onTogglePlayPause = onTogglePlayPause,
                            onSeekTo = onSeekTo,
                            onPlaybackSpeedSelected = onPlaybackSpeedSelected,
                            onTabSelected = onTabSelected,
                            onSongSelected = {
                                onSongSelected(it)
                                showNowPlayingSheet = true
                            },
                            onShowLyrics = { showLyricsSheet = true },
                            onShowTagEditor = { showTagEditorSheet = true },
                            onEqualizerEnabledChange = onEqualizerEnabledChange,
                            onBandLevelChange = onBandLevelChange,
                            onPresetSelected = onPresetSelected,
                            onBassBoostChange = onBassBoostChange,
                            onVirtualizerChange = onVirtualizerChange,
                            onReverbSelected = onReverbSelected,
                            onPlayAllSongs = onPlayAllSongs,
                            onShuffleAllSongs = onShuffleAllSongs,
                            onShareCurrentSong = onShareCurrentSong,
                            onShareSong = onShareSong,
                            onAddToFavorites = onAddToFavorites,
                            onAddSongToPlaylist = onAddSongToPlaylist,
                            onCreatePlaylistAndAdd = onCreatePlaylistAndAdd,
                            onOpenPlaylistDetail = onOpenPlaylistDetail,
                            onImportPlaylist = onImportPlaylist,
                            onPlayPlaylist = onPlayPlaylist,
                            songSort = songSort,
                            albumSortDirection = albumSortDirection,
                            artistSortDirection = artistSortDirection,
                            onSongSortFieldSelected = onSongSortFieldSelected,
                            onSongSortDirectionToggle = onSongSortDirectionToggle,
                            onAlbumSortToggle = onAlbumSortToggle,
                            onArtistSortToggle = onArtistSortToggle,
                            onLyricsDraftChange = onLyricsDraftChange,
                            onLyricsSave = onLyricsSave,
                            onLyricsClear = onLyricsClear,
                            onSampleLyrics = onSampleLyrics,
                            onSelectThemeOption = onSelectThemeOption,
                            onToggleDarkTheme = onToggleDarkTheme,
                            onUpdateTagTitle = onUpdateTagTitle,
                            onUpdateTagArtist = onUpdateTagArtist,
                            onUpdateTagAlbum = onUpdateTagAlbum,
                            onSaveTags = onSaveTags,
                            onResetTags = onResetTags,
                            onSelectLongformFilter = onSelectLongformFilter,
                            onClearLongformBookmarkFor = onClearLongformBookmarkFor,
                            onVideoSortToggle = onVideoSortToggle,
                            onVideoSortFieldSelected = onVideoSortFieldSelected,
                            onPlayAllVideos = onPlayAllVideos,
                            onRequestVideoPermission = onRequestVideoPermission,
                            isVideoPermissionGranted = isVideoPermissionGranted,
                            onPlayVideo = onPlayVideo,
                            onShowMessage = showMessage,
                            contentPadding = innerPadding
                        )

                        DrawerDestinationId.Equalizer -> EqualizerScreen(
                            state = equalizerState,
                            onEqualizerEnabledChange = onEqualizerEnabledChange,
                            onBandLevelChange = onBandLevelChange,
                            onPresetSelected = onPresetSelected,
                            onBassBoostChange = onBassBoostChange,
                            onVirtualizerChange = onVirtualizerChange,
                            onReverbSelected = onReverbSelected,
                            onResetBands = onResetEqualizer,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        )

                        DrawerDestinationId.ThemeStudio -> ThemeScreen(
                            state = themeState,
                            onSelectOption = onSelectThemeOption,
                            onToggleDarkTheme = onToggleDarkTheme,
                            onToggleDynamicColor = onToggleDynamicColor,
                            onToggleAmoledBlack = onToggleAmoledBlack,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        )

                        DrawerDestinationId.SleepTimer -> SleepTimerScreen(
                            state = sleepTimerState,
                            onQuickDurationSelected = onSelectSleepTimerQuickDuration,
                            onCustomHoursChanged = onSleepTimerHoursChanged,
                            onCustomMinutesChanged = onSleepTimerMinutesChanged,
                            onFadeToggle = onSleepTimerFadeToggle,
                            onEndActionSelected = onSleepTimerEndActionSelected,
                            onStart = onStartSleepTimer,
                            onCancel = onCancelSleepTimer,
                            onMessageDismiss = onDismissSleepTimerMessage,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        )

                        DrawerDestinationId.Widgets -> PlaceholderScreen(
                            titleRes = R.string.drawer_widgets,
                            messageRes = R.string.placeholder_widgets,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )

                        DrawerDestinationId.ScanImport -> ScanImportScreen(
                            folders = safFolders,
                            onAddFolder = onChooseFolders,
                            onRemoveFolder = onRemoveSafFolder,
                            onRescanLibrary = onRescanLibrary,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )

                        DrawerDestinationId.Settings -> SettingsScreen(
                            state = settingsState,
                            onToggleRearmOnBootEnabled = onToggleRearmOnBootEnabled,
                            onSelectRearmMinMinutes = onSelectRearmMinMinutes,
                            onToggleSkipSilenceEnabled = onToggleSkipSilenceEnabled,
                            onSelectCrossfadeMs = onSelectCrossfadeMs,
                            onToggleUseHaptics = onToggleUseHaptics,
                            onSelectLongformThresholdMinutes = onSelectLongformThresholdMinutes,
                            onRescanLibrary = onRescanLibrary,
                            onOpenScanImport = { onDrawerDestinationSelected(DrawerDestinationId.ScanImport) },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        )

                        DrawerDestinationId.Help -> PlaceholderScreen(
                            titleRes = R.string.drawer_help,
                            messageRes = R.string.placeholder_help,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }
            if (onboardingState.isVisible) {
                OnboardingOverlay(
                    state = onboardingState,
                    themeState = themeState,
                    onWelcomeContinue = onOnboardingWelcomeContinue,
                    onRequestPermission = onRequestAudioPermission,
                    onChooseFolders = onChooseFolders,
                    onAssignAllLongform = onAssignAllLongform,
                    onChooseIndividually = onChooseLongformIndividually,
                    onSelectionChange = onLongformSelectionChange,
                    onApplySelection = onApplyLongformSelection,
                    onSkip = onSkipLongform,
                    onUndo = onUndoLongformChange,
                    onDismissMessage = onConsumeOnboardingMessage,
                    onComplete = onCompleteOnboarding,
                    onSelectThemeOption = onSelectThemeOption,
                    onToggleDarkTheme = onToggleDarkTheme
                )
            }
            if (isSearchVisible) {
                SearchOverlay(
                    state = searchUiState,
                    onDismiss = {
                        isSearchVisible = false
                    },
                    onQueryChange = { query ->
                        searchQuery = query
                    },
                    onClearQuery = {
                        searchQuery = ""
                    },
                    onBucketSelected = { bucket ->
                        activeSearchBucket = bucket
                    },
                    onSongSelected = { songId ->
                        onSongSelected(songId)
                        showNowPlayingSheet = true
                        isSearchVisible = false
                    },
                    onNavigateToTab = { tab ->
                        onDrawerDestinationSelected(DrawerDestinationId.Library)
                        onTabSelected(tab)
                        isSearchVisible = false
                        scope.launch {
                            val tabLabel = context.getString(tab.titleRes)
                            snackbarHostState.showSnackbar(message = context.getString(R.string.showing_tab, tabLabel))
                        }
                    }
                )
            }
        }
    }
}

// ---- Search overlay & results -------------------------------------------------------------------

@Composable
private fun SearchOverlay(
    state: SearchUiState,
    onDismiss: () -> Unit,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onBucketSelected: (SearchBucket) -> Unit,
    onSongSelected: (String) -> Unit,
    onNavigateToTab: (HomeTab) -> Unit
) {
    BackHandler(onBack = onDismiss)
    val overlayDescription = stringResource(R.string.search_overlay_content_description)
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = overlayDescription }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.search_close_content_description)
                    )
                }
                Text(
                    text = stringResource(R.string.search_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                placeholder = { Text(text = stringResource(R.string.search_field_placeholder)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onDismiss() }),
                trailingIcon = {
                    if (state.query.isNotBlank()) {
                        IconButton(onClick = onClearQuery) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(R.string.search_clear_content_description)
                            )
                        }
                    }
                }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SearchBucket.entries) { bucket ->
                    FilterChip(
                        selected = state.bucket == bucket,
                        onClick = { onBucketSelected(bucket) },
                        label = { Text(text = stringResource(bucket.labelRes)) }
                    )
                }
            }
            when {
                state.query.isBlank() -> {
                    SearchPlaceholder(textRes = R.string.search_hint)
                }

                state.isEmpty -> {
                    SearchPlaceholder(textRes = R.string.search_no_results)
                }

                else -> {
                    when (state.bucket) {
                        SearchBucket.All -> SearchAllResults(
                            results = state.results,
                            onSongSelected = onSongSelected,
                            onBucketSelected = onBucketSelected,
                            onNavigateToTab = onNavigateToTab
                        )

                        SearchBucket.Songs -> SearchSongsResults(
                            songs = state.results.songs,
                            onSongSelected = onSongSelected
                        )

                        SearchBucket.Playlists -> SearchPlaylistsResults(
                            playlists = state.results.playlists,
                            onNavigateToTab = onNavigateToTab
                        )

                        SearchBucket.Folders -> SearchFoldersResults(
                            folders = state.results.folders,
                            onNavigateToTab = onNavigateToTab
                        )

                        SearchBucket.Albums -> SearchAlbumsResults(
                            albums = state.results.albums,
                            onNavigateToTab = onNavigateToTab
                        )

                        SearchBucket.Artists -> SearchArtistsResults(
                            artists = state.results.artists,
                            onNavigateToTab = onNavigateToTab
                        )

                        SearchBucket.Genres -> SearchGenresResults(
                            genres = state.results.genres,
                            onNavigateToTab = onNavigateToTab
                        )

                        SearchBucket.Longform -> SearchLongformResults(
                            items = state.results.longform,
                            onNavigateToTab = onNavigateToTab
                        )

                        SearchBucket.Videos -> SearchVideosResults(
                            videos = state.results.videos,
                            onNavigateToTab = onNavigateToTab
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchPlaceholder(@StringRes textRes: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchAllResults(
    results: SearchResults,
    onSongSelected: (String) -> Unit,
    onBucketSelected: (SearchBucket) -> Unit,
    onNavigateToTab: (HomeTab) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (results.songs.isNotEmpty()) {
            item { SearchSectionHeader(titleRes = R.string.search_section_songs) }
            items(results.songs.take(4)) { song ->
                SongSearchRow(song = song, onClick = { onSongSelected(song.id) })
            }
            if (results.songs.size > 4) {
                item { ViewAllButton(onClick = { onNavigateToTab(HomeTab.Songs) }) }
            }
        }
        if (results.playlists.isNotEmpty()) {
            item { SearchSectionHeader(titleRes = R.string.search_section_playlists) }
            items(results.playlists.take(4)) { playlist ->
                PlaylistSearchRow(playlist = playlist, onClick = { onNavigateToTab(HomeTab.Playlists) })
            }
            if (results.playlists.size > 4) {
                item { ViewAllButton(onClick = { onNavigateToTab(HomeTab.Playlists) }) }
            }
        }
        if (results.folders.isNotEmpty()) {
            item { SearchSectionHeader(titleRes = R.string.search_section_folders) }
            items(results.folders.take(4)) { folder ->
                FolderSearchRow(folder = folder, onClick = { onNavigateToTab(HomeTab.Folders) })
            }
            if (results.folders.size > 4) {
                item { ViewAllButton(onClick = { onNavigateToTab(HomeTab.Folders) }) }
            }
        }
        if (results.albums.isNotEmpty()) {
            item { SearchSectionHeader(titleRes = R.string.search_section_albums) }
            items(results.albums.take(4)) { album ->
                AlbumSearchRow(album = album, onClick = { onNavigateToTab(HomeTab.Albums) })
            }
            if (results.albums.size > 4) {
                item { ViewAllButton(onClick = { onNavigateToTab(HomeTab.Albums) }) }
            }
        }
        if (results.artists.isNotEmpty()) {
            item { SearchSectionHeader(titleRes = R.string.search_section_artists) }
            items(results.artists.take(4)) { artist ->
                ArtistSearchRow(artist = artist, onClick = { onNavigateToTab(HomeTab.Artists) })
            }
            if (results.artists.size > 4) {
                item { ViewAllButton(onClick = { onNavigateToTab(HomeTab.Artists) }) }
            }
        }
        if (results.genres.isNotEmpty()) {
            item { SearchSectionHeader(titleRes = R.string.search_section_genres) }
            items(results.genres.take(4)) { genre ->
                GenreSearchRow(genre = genre, onClick = { onNavigateToTab(HomeTab.Genres) })
            }
            if (results.genres.size > 4) {
                item { ViewAllButton(onClick = { onNavigateToTab(HomeTab.Genres) }) }
            }
        }
        if (results.longform.isNotEmpty()) {
            item { SearchSectionHeader(titleRes = R.string.search_section_longform) }
            items(results.longform.take(4)) { item ->
                LongformSearchRow(item = item, onClick = { onNavigateToTab(HomeTab.Longform) })
            }
            if (results.longform.size > 4) {
                item { ViewAllButton(onClick = { onNavigateToTab(HomeTab.Longform) }) }
            }
        }
        if (results.videos.isNotEmpty()) {
            item { SearchSectionHeader(titleRes = R.string.search_section_videos) }
            items(results.videos.take(4)) { video ->
                VideoSearchRow(video = video, onClick = { onNavigateToTab(HomeTab.Videos) })
            }
            if (results.videos.size > 4) {
                item { ViewAllButton(onClick = { onNavigateToTab(HomeTab.Videos) }) }
            }
        }
    }
}

@Composable
private fun SearchSongsResults(
    songs: List<SongSummary>,
    onSongSelected: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(songs) { song ->
            SongSearchRow(song = song, onClick = { onSongSelected(song.id) })
        }
    }
}

@Composable
private fun SearchPlaylistsResults(
    playlists: List<PlaylistSummary>,
    onNavigateToTab: (HomeTab) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(playlists) { playlist ->
            PlaylistSearchRow(playlist = playlist, onClick = { onNavigateToTab(HomeTab.Playlists) })
        }
    }
}

@Composable
private fun SearchFoldersResults(
    folders: List<FolderSummary>,
    onNavigateToTab: (HomeTab) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(folders) { folder ->
            FolderSearchRow(folder = folder, onClick = { onNavigateToTab(HomeTab.Folders) })
        }
    }
}

@Composable
private fun SearchAlbumsResults(
    albums: List<AlbumSummary>,
    onNavigateToTab: (HomeTab) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(albums) { album ->
            AlbumSearchRow(album = album, onClick = { onNavigateToTab(HomeTab.Albums) })
        }
    }
}

@Composable
private fun SearchArtistsResults(
    artists: List<ArtistSummary>,
    onNavigateToTab: (HomeTab) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(artists) { artist ->
            ArtistSearchRow(artist = artist, onClick = { onNavigateToTab(HomeTab.Artists) })
        }
    }
}

@Composable
private fun SearchGenresResults(
    genres: List<GenreSummary>,
    onNavigateToTab: (HomeTab) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(genres) { genre ->
            GenreSearchRow(genre = genre, onClick = { onNavigateToTab(HomeTab.Genres) })
        }
    }
}

@Composable
private fun SearchLongformResults(
    items: List<LongformItem>,
    onNavigateToTab: (HomeTab) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            LongformSearchRow(item = item, onClick = { onNavigateToTab(HomeTab.Longform) })
        }
    }
}

@Composable
private fun SearchVideosResults(
    videos: List<VideoSummary>,
    onNavigateToTab: (HomeTab) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(videos) { video ->
            VideoSearchRow(video = video, onClick = { onNavigateToTab(HomeTab.Videos) })
        }
    }
}

@Composable
private fun SearchSectionHeader(@StringRes titleRes: Int) {
    Text(
        text = stringResource(titleRes),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ViewAllButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text = stringResource(R.string.search_view_all))
    }
}

@Composable
private fun SongSearchRow(song: SongSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        ListItem(
            headlineContent = {
                Text(text = song.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            supportingContent = {
                Text(
                    text = stringResource(R.string.song_row_subtitle, song.artist, song.album),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingContent = {
                Text(text = formatDuration(song.durationMs))
            }
        )
    }
}

@Composable
private fun PlaylistSearchRow(playlist: PlaylistSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        ListItem(
            headlineContent = {
                Text(text = playlist.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            supportingContent = {
                Text(
                    text = stringResource(
                        R.string.search_playlist_subtitle,
                        playlist.trackCount,
                        formatDuration(playlist.totalDurationMs)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    }
}

@Composable
private fun FolderSearchRow(folder: FolderSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        ListItem(
            headlineContent = {
                Text(text = folder.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            supportingContent = {
                Text(
                    text = stringResource(R.string.search_folder_subtitle, folder.path, folder.trackCount),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    }
}

@Composable
private fun AlbumSearchRow(album: AlbumSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        ListItem(
            headlineContent = {
                Text(text = album.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            supportingContent = {
                Text(
                    text = stringResource(R.string.search_album_subtitle, album.artist, album.trackCount),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    }
}

@Composable
private fun ArtistSearchRow(artist: ArtistSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        ListItem(
            headlineContent = {
                Text(text = artist.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            supportingContent = {
                Text(
                    text = stringResource(R.string.search_artist_subtitle, artist.albumCount, artist.trackCount),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    }
}

@Composable
private fun GenreSearchRow(genre: GenreSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        ListItem(
            headlineContent = {
                Text(text = genre.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            supportingContent = {
                Text(
                    text = stringResource(R.string.search_genre_subtitle, genre.trackCount),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    }
}

@Composable
private fun LongformSearchRow(item: LongformItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        ListItem(
            headlineContent = {
                Text(text = item.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            supportingContent = {
                Text(
                    text = stringResource(
                        R.string.search_longform_subtitle,
                        item.category.name,
                        formatDuration(item.durationMs)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    }
}

@Composable
private fun VideoSearchRow(video: VideoSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        ListItem(
            headlineContent = {
                Text(text = video.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            supportingContent = {
                Text(text = formatDuration(video.durationMs))
            }
        )
    }
}

// ---- Top app bar & drawer ----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryTopBar(
    onOpenDrawer: () -> Unit,
    onSearch: () -> Unit,
    onOpenThemeStudio: () -> Unit,
    onOpenSettings: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
                Text(
                    text = stringResource(R.string.app_tagline),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.drawer_toggle_content_description)
                )
            }
        },
        actions = {
            IconButton(onClick = onSearch) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.search_content_description)
                )
            }
            IconButton(onClick = onOpenThemeStudio) {
                Icon(
                    imageVector = Icons.Outlined.Palette,
                    contentDescription = stringResource(R.string.theme_studio_content_description)
                )
            }
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.settings_content_description)
                )
            }
        }
    )
}

@Composable
private fun DrawerContent(
    destinations: List<DrawerDestination>,
    selected: DrawerDestinationId,
    onDestinationSelected: (DrawerDestinationId) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        Text(
            text = stringResource(R.string.drawer_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        destinations.forEach { destination ->
            NavigationDrawerItem(
                label = { Text(text = stringResource(destination.titleRes)) },
                selected = destination.id == selected,
                onClick = { onDestinationSelected(destination.id) },
                icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

private val DrawerDestination.icon: ImageVector
    get() = when (id) {
        DrawerDestinationId.Library -> Icons.Outlined.LibraryMusic
        DrawerDestinationId.Equalizer -> Icons.Outlined.Equalizer
        DrawerDestinationId.SleepTimer -> Icons.Outlined.Timer
        DrawerDestinationId.ThemeStudio -> Icons.Outlined.Palette
        DrawerDestinationId.Widgets -> Icons.Outlined.Widgets
        DrawerDestinationId.ScanImport -> Icons.Outlined.QueueMusic
        DrawerDestinationId.Settings -> Icons.Outlined.Settings
        DrawerDestinationId.Help -> Icons.AutoMirrored.Outlined.HelpOutline
    }

// ---- Library & tabs ----------------------------------------------------------------------------

@Composable
@OptIn(ExperimentalMaterialApi::class)
@AndroidOptIn(UnstableApi::class)
@UnstableApi
private fun LibraryContent(
    homeState: HomeUiState,
    playerState: PlayerUiState,
    equalizerState: EqualizerUiState,
    lyricsState: LyricsUiState,
    themeState: ThemeUiState,
    tagEditorState: TagEditorUiState,
    onRescanLibrary: () -> Unit,
    onChooseFolders: () -> Unit,
    onRequestAudioPermission: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onPlaybackSpeedSelected: (Float) -> Unit,
    onTabSelected: (HomeTab) -> Unit,
    onSongSelected: (String) -> Unit,
    onShowLyrics: () -> Unit,
    onShowTagEditor: () -> Unit,
    onEqualizerEnabledChange: (Boolean) -> Unit,
    onBandLevelChange: (Int, Int) -> Unit,
    onPresetSelected: (Int) -> Unit,
    onBassBoostChange: (Int) -> Unit,
    onVirtualizerChange: (Int) -> Unit,
    onReverbSelected: (Short) -> Unit,
    onPlayAllSongs: () -> Unit,
    onShuffleAllSongs: () -> Unit,
    onShareCurrentSong: () -> Unit,
    onShareSong: (String) -> Unit,
    onAddToFavorites: (String) -> Unit,
    onAddSongToPlaylist: (String, String) -> Unit,
    onCreatePlaylistAndAdd: (String, String) -> Unit,
    onOpenPlaylistDetail: (String) -> Unit,
    onImportPlaylist: () -> Unit,
    onPlayPlaylist: (String) -> Unit,
    songSort: SongSortState,
    albumSortDirection: SortDirection,
    artistSortDirection: SortDirection,
    onSongSortFieldSelected: (SongSortField) -> Unit,
    onSongSortDirectionToggle: () -> Unit,
    onAlbumSortToggle: () -> Unit,
    onArtistSortToggle: () -> Unit,
    onLyricsDraftChange: (String) -> Unit,
    onLyricsSave: () -> Unit,
    onLyricsClear: () -> Unit,
    onSampleLyrics: () -> Unit,
    onSelectThemeOption: (Int) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onUpdateTagTitle: (String) -> Unit,
    onUpdateTagArtist: (String) -> Unit,
    onUpdateTagAlbum: (String) -> Unit,
    onSaveTags: () -> Unit,
    onResetTags: () -> Unit,
    onSelectLongformFilter: (LongformFilter) -> Unit,
    onClearLongformBookmarkFor: (String) -> Unit,
    onVideoSortToggle: () -> Unit,
    onVideoSortFieldSelected: (VideoSortField) -> Unit,
    onPlayAllVideos: () -> Unit,
    onRequestVideoPermission: () -> Unit,
    isVideoPermissionGranted: Boolean,
    onPlayVideo: (String) -> Unit,
    onShowMessage: (String) -> Unit,
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        ScrollableTabRow(
            selectedTabIndex = homeState.selectedTab.ordinal,
            edgePadding = 16.dp
        ) {
            HomeTab.entries.forEach { tab ->
                Tab(
                    selected = tab == homeState.selectedTab,
                    onClick = { onTabSelected(tab) },
                    text = { Text(text = stringResource(tab.titleRes)) }
                )
            }
        }

        when (homeState.selectedTab) {
            HomeTab.Songs -> SongsTab(
                songs = homeState.songs,
                playerState = playerState,
                onSongSelected = onSongSelected,
                onPlayAllSongs = onPlayAllSongs,
                onShuffleAllSongs = onShuffleAllSongs,
                onRescanLibrary = onRescanLibrary,
                onChooseFolders = onChooseFolders,
                onRequestAudioPermission = onRequestAudioPermission,
                onTogglePlayPause = onTogglePlayPause,
                onSeekTo = onSeekTo,
                onPlaybackSpeedSelected = onPlaybackSpeedSelected,
                onShowLyrics = onShowLyrics,
                onShowTagEditor = onShowTagEditor,
                onShareCurrentSong = onShareCurrentSong,
                onShareSong = onShareSong,
                onAddToFavorites = onAddToFavorites,
                onAddSongToPlaylist = onAddSongToPlaylist,
                onCreatePlaylistAndAdd = onCreatePlaylistAndAdd,
                onShowMessage = onShowMessage,
                sortState = songSort,
                onSortFieldSelected = onSongSortFieldSelected,
                onSortDirectionToggle = onSongSortDirectionToggle,
                isScanning = homeState.isScanning,
                lastScanTimestampMs = homeState.lastScanTimestampMs,
                librarySongCount = homeState.librarySongCount,
                isPermissionDenied = homeState.isPermissionDenied,
                userPlaylists = homeState.playlists.filter { it.id.startsWith("user:") }
            )

            HomeTab.Playlists -> PlaylistsTab(
                playlists = homeState.playlists,
                onPlay = onPlayPlaylist,
                onOpen = onOpenPlaylistDetail,
                onImport = onImportPlaylist
            )
            HomeTab.Folders -> FoldersTab(homeState.folders)
            HomeTab.Albums -> AlbumsTab(
                albums = homeState.albums,
                sortDirection = albumSortDirection,
                onToggleSortDirection = onAlbumSortToggle,
                findSampleSongUriFor = { album -> homeState.songs.firstOrNull { it.album == album.title }?.uri }
            )

            HomeTab.Artists -> ArtistsTab(
                artists = homeState.artists,
                sortDirection = artistSortDirection,
                onToggleSortDirection = onArtistSortToggle,
                findSampleSongUriFor = { artist -> homeState.songs.firstOrNull { it.artist == artist.name }?.uri }
            )

            HomeTab.Genres -> GenresTab(homeState.genres)

            HomeTab.Longform -> LongformTab(
                state = homeState,
                onFilterSelected = onSelectLongformFilter,
                onPlayNow = onSongSelected,
                onClearBookmarkFor = onClearLongformBookmarkFor
            )

            HomeTab.Videos -> VideosTab(
                videos = homeState.videos,
                sortDirection = homeState.videoSortDirection,
                onToggleSortDirection = onVideoSortToggle,
                onPlayAll = onPlayAllVideos,
                sortField = homeState.videoSortField,
                onSortFieldSelected = onVideoSortFieldSelected,
                onRequestPermission = onRequestVideoPermission,
                isPermissionGranted = isVideoPermissionGranted,
                onPlayVideo = onPlayVideo
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SongsTab(
    songs: List<SongSummary>,
    playerState: PlayerUiState,
    onSongSelected: (String) -> Unit,
    onPlayAllSongs: () -> Unit,
    onShuffleAllSongs: () -> Unit,
    onRescanLibrary: () -> Unit,
    onChooseFolders: () -> Unit,
    onRequestAudioPermission: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onPlaybackSpeedSelected: (Float) -> Unit,
    onShowLyrics: () -> Unit,
    onShowTagEditor: () -> Unit,
    onShareCurrentSong: () -> Unit,
    onShareSong: (String) -> Unit,
    onAddToFavorites: (String) -> Unit,
    onAddSongToPlaylist: (String, String) -> Unit,
    onCreatePlaylistAndAdd: (String, String) -> Unit,
    onShowMessage: (String) -> Unit,
    sortState: SongSortState,
    onSortFieldSelected: (SongSortField) -> Unit,
    onSortDirectionToggle: () -> Unit,
    isScanning: Boolean,
    lastScanTimestampMs: Long?,
    librarySongCount: Int,
    isPermissionDenied: Boolean,
    userPlaylists: List<PlaylistSummary>
) {
    val addToPlaylistSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    // Simplified: inline add-to-playlist UI deferred
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            NowPlayingCard(
                state = playerState,
                onTogglePlayPause = onTogglePlayPause,
                onSeekTo = onSeekTo,
                onPlaybackSpeedSelected = onPlaybackSpeedSelected,
                onShowLyrics = onShowLyrics,
                onShowTagEditor = onShowTagEditor,
                onShare = onShareCurrentSong
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Scan status row
            val context = androidx.compose.ui.platform.LocalContext.current
            val lastScanText = if (lastScanTimestampMs != null) {
                val fmt = android.text.format.DateFormat.getMediumDateFormat(context)
                val timeFmt = android.text.format.DateFormat.getTimeFormat(context)
                val ts = java.util.Date(lastScanTimestampMs)
                val whenText = fmt.format(ts) + " " + timeFmt.format(ts)
                context.getString(R.string.library_last_scanned, whenText, librarySongCount)
            } else null
            AnimatedVisibility(visible = isScanning, enter = fadeIn(), exit = fadeOut()) {
                Text(text = stringResource(R.string.library_scanning), style = MaterialTheme.typography.bodySmall)
            }
            AnimatedVisibility(visible = !isScanning && lastScanText != null, enter = fadeIn(), exit = fadeOut()) {
                Text(text = lastScanText!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!isScanning && isPermissionDenied) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = stringResource(R.string.library_permission_required), style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = onRequestAudioPermission, modifier = Modifier.height(40.dp)) {
                            Text(text = stringResource(R.string.songs_allow_access_button))
                        }
                        TextButton(onClick = onChooseFolders, modifier = Modifier.height(40.dp)) {
                            Text(text = stringResource(R.string.songs_choose_folders_button))
                        }
                    }
                }
            } else if (!isScanning && songs.isEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = stringResource(R.string.library_no_media_message), style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = onRescanLibrary, modifier = Modifier.height(40.dp)) {
                            Text(text = stringResource(R.string.songs_rescan_button))
                        }
                        TextButton(onClick = onChooseFolders, modifier = Modifier.height(40.dp)) {
                            Text(text = stringResource(R.string.songs_choose_folders_button))
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = onShuffleAllSongs,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(text = stringResource(R.string.songs_shuffle_all_button))
                }
                Button(
                    onClick = onPlayAllSongs,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(text = stringResource(R.string.songs_play_all_button))
                }
                TextButton(onClick = onRescanLibrary, modifier = Modifier.height(56.dp)) {
                    Text(text = stringResource(R.string.songs_rescan_button))
                }
            }
            Text(
                text = stringResource(R.string.songs_tab_count, songs.size),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            SongsSortRow(
                sortState = sortState,
                onSortFieldSelected = onSortFieldSelected,
                onSortDirectionToggle = onSortDirectionToggle,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        items(songs) { song ->
            val isCurrent = song.id == playerState.currentSongId
            val cardColors = if (isCurrent) {
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            }
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSongSelected(song.id) },
                colors = cardColors
            ) {
                ListItem(
                    leadingContent = {
                        // Gradient avatar driven by per-song palette (dominant/vibrant), cached and animated
                        val ctx = LocalContext.current
                        val palette by androidx.compose.runtime.produceState<ArtworkPaletteCache.ArtworkPalette?>(
                            initialValue = null,
                            song.id,
                            song.uri
                        ) {
                            value = ArtworkPaletteCache.getForSong(ctx, song.id, song.uri)
                        }
                        val baseP = MaterialTheme.colorScheme.primary
                        val baseS = MaterialTheme.colorScheme.secondary
                        val targetP = palette?.dominant?.let { Color(it) } ?: baseP
                        val targetS = palette?.vibrant?.let { Color(it) } ?: baseS
                        val p by animateColorAsState(targetValue = targetP, animationSpec = tween(600), label = "songP")
                        val s by animateColorAsState(targetValue = targetS, animationSpec = tween(600), label = "songS")
                        val brush = Brush.radialGradient(listOf(p.copy(alpha = 0.35f), s.copy(alpha = 0.2f), Color.Transparent))
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCurrent) {
                                val indicatorDescription = stringResource(
                                    if (playerState.isPlaying) R.string.songs_now_playing_indicator_playing else R.string.songs_now_playing_indicator_paused
                                )
                                Icon(
                                    imageVector = if (playerState.isPlaying) Icons.Outlined.Equalizer else Icons.Outlined.PlayArrow,
                                    contentDescription = indicatorDescription
                                )
                            }
                        }
                    },
                    headlineContent = {
                        Text(text = song.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.song_row_subtitle, song.artist, song.album),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    trailingContent = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = formatDuration(song.durationMs))
                            IconButton(onClick = { onShareSong(song.id) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = stringResource(
                                        R.string.share_song_row_content_description,
                                        song.title
                                    )
                                )
                            }
                            // Overflow menu for playlist actions
                            var menuOpen by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { menuOpen = true }) {
                                    Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = stringResource(R.string.more_options))
                                }
                                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                                    DropdownMenuItem(text = { Text(stringResource(R.string.songs_add_to_favorites)) }, onClick = {
                                        menuOpen = false
                                        onAddToFavorites(song.id)
                                        android.widget.Toast.makeText(
                                            context,
                                            context.getString(R.string.added_to_favorites_message, song.title),
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    })
                                    DropdownMenuItem(text = { Text(stringResource(R.string.songs_add_to_playlist)) }, onClick = {
                                        menuOpen = false
                                        onShowMessage(context.getString(R.string.playlists_import_button))
                                    })
                                }
                            }
                        }
                    }
                )
            }
        }
        // simplified add-to-playlist UI temporarily removed
    }
}

@Composable
private fun SongsSortRow(
    sortState: SongSortState,
    onSortFieldSelected: (SongSortField) -> Unit,
    onSortDirectionToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val directionIcon = if (sortState.direction == SortDirection.Ascending) {
        Icons.Outlined.ArrowUpward
    } else {
        Icons.Outlined.ArrowDownward
    }
    val directionContentDescription = songSortDirectionContentDescription(sortState)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.songs_sort_label),
            style = MaterialTheme.typography.labelMedium
        )
        Box {
            AssistChip(
                onClick = { menuExpanded = true },
                label = { Text(text = stringResource(sortState.field.labelRes)) },
                leadingIcon = { Icon(imageVector = Icons.AutoMirrored.Outlined.Sort, contentDescription = null) }
            )
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                SongSortField.entries.forEach { field ->
                    DropdownMenuItem(
                        text = { Text(text = stringResource(field.labelRes)) },
                        onClick = {
                            menuExpanded = false
                            onSortFieldSelected(field)
                        },
                        trailingIcon = {
                            if (field == sortState.field) {
                                Icon(imageVector = Icons.Outlined.Check, contentDescription = null)
                            }
                        }
                    )
                }
            }
        }
        AssistChip(
            onClick = onSortDirectionToggle,
            label = { Text(text = stringResource(sortState.direction.labelRes)) },
            leadingIcon = { Icon(imageVector = directionIcon, contentDescription = null) },
            modifier = Modifier.semantics { this.contentDescription = directionContentDescription }
        )
    }
}

@Composable
private fun songSortDirectionContentDescription(sortState: SongSortState): String {
    val resId = when (sortState.field) {
        SongSortField.Title -> if (sortState.direction == SortDirection.Ascending) {
            R.string.songs_sort_direction_title_ascending
        } else {
            R.string.songs_sort_direction_title_descending
        }

        SongSortField.Added -> if (sortState.direction == SortDirection.Descending) {
            R.string.songs_sort_direction_added_descending
        } else {
            R.string.songs_sort_direction_added_ascending
        }

        SongSortField.Duration -> if (sortState.direction == SortDirection.Ascending) {
            R.string.songs_sort_direction_duration_ascending
        } else {
            R.string.songs_sort_direction_duration_descending
        }
    }
    return stringResource(resId)
}

@Composable
private fun PlaylistsTab(
    playlists: List<PlaylistSummary>,
    onPlay: (String) -> Unit,
    onOpen: (String) -> Unit,
    onImport: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.playlists_tab_count, playlists.size),
                    style = MaterialTheme.typography.labelMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onImport) {
                        Icon(imageVector = Icons.Outlined.QueueMusic, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = stringResource(R.string.playlists_import_button))
                    }
                    TextButton(onClick = { onPlay("playlist_all_songs") }) {
                        Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = stringResource(R.string.play_action))
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))
        }
        items(playlists) { playlist ->
            ElevatedCard(onClick = { onOpen(playlist.id) }, modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text(text = playlist.title) },
                    supportingContent = {
                        Text(
                            text = stringResource(
                                R.string.playlist_row_subtitle,
                                playlist.trackCount,
                                formatDuration(playlist.totalDurationMs)
                            )
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { onPlay(playlist.id) }) {
                            Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = stringResource(R.string.play_action))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FoldersTab(folders: List<FolderSummary>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.folders_tab_count, folders.size),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(folders) { folder ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    leadingContent = {
                        val p = MaterialTheme.colorScheme.primary
                        val s = MaterialTheme.colorScheme.secondary
                        val brush = Brush.radialGradient(listOf(p.copy(alpha = 0.35f), s.copy(alpha = 0.2f), Color.Transparent))
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush)
                        ) {}
                    },
                    headlineContent = { Text(text = folder.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    supportingContent = {
                        Text(text = folder.path, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    trailingContent = {
                        Text(text = stringResource(R.string.folder_row_track_count, folder.trackCount))
                    }
                )
            }
        }
    }
}

@Composable
private fun AlbumsTab(
    albums: List<AlbumSummary>,
    sortDirection: SortDirection,
    onToggleSortDirection: () -> Unit,
    findSampleSongUriFor: (AlbumSummary) -> String? = { null }
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.albums_tab_count, albums.size),
                    style = MaterialTheme.typography.labelMedium
                )
                val icon = if (sortDirection == SortDirection.Ascending) {
                    Icons.Outlined.ArrowUpward
                } else {
                    Icons.Outlined.ArrowDownward
                }
                val contentDescription = stringResource(
                    if (sortDirection == SortDirection.Ascending) {
                        R.string.albums_sort_direction_ascending
                    } else {
                        R.string.albums_sort_direction_descending
                    }
                )
                AssistChip(
                    onClick = onToggleSortDirection,
                    label = { Text(text = stringResource(sortDirection.labelRes)) },
                    leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
                    modifier = Modifier.semantics { this.contentDescription = contentDescription }
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))
        }
        items(albums) { album ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    leadingContent = {
                        val ctx = LocalContext.current
                        val sampleUri = remember(album.id) { findSampleSongUriFor(album) }
                        val palette by androidx.compose.runtime.produceState<ArtworkPaletteCache.ArtworkPalette?>(
                            initialValue = null,
                            album.id,
                            sampleUri
                        ) {
                            value = ArtworkPaletteCache.getForAlbum(ctx, album.id, sampleUri)
                        }
                        val baseP = MaterialTheme.colorScheme.primary
                        val baseS = MaterialTheme.colorScheme.secondary
                        val targetP = palette?.dominant?.let { Color(it) } ?: baseP
                        val targetS = palette?.vibrant?.let { Color(it) } ?: baseS
                        val p by animateColorAsState(targetValue = targetP, animationSpec = tween(600), label = "albumP")
                        val s by animateColorAsState(targetValue = targetS, animationSpec = tween(600), label = "albumS")
                        val brush = Brush.radialGradient(listOf(p.copy(alpha = 0.35f), s.copy(alpha = 0.2f), Color.Transparent))
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Outlined.Album, contentDescription = null)
                        }
                    },
                    headlineContent = { Text(text = album.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    supportingContent = {
                        Text(text = album.artist, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    trailingContent = {
                        Text(text = stringResource(R.string.album_row_track_count, album.trackCount))
                    }
                )
            }
        }
    }
}

@Composable
private fun ArtistsTab(
    artists: List<ArtistSummary>,
    sortDirection: SortDirection,
    onToggleSortDirection: () -> Unit,
    findSampleSongUriFor: (ArtistSummary) -> String? = { null }
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.artists_tab_count, artists.size),
                    style = MaterialTheme.typography.labelMedium
                )
                val icon = if (sortDirection == SortDirection.Ascending) {
                    Icons.Outlined.ArrowUpward
                } else {
                    Icons.Outlined.ArrowDownward
                }
                val contentDescription = stringResource(
                    if (sortDirection == SortDirection.Ascending) {
                        R.string.artists_sort_direction_ascending
                    } else {
                        R.string.artists_sort_direction_descending
                    }
                )
                AssistChip(
                    onClick = onToggleSortDirection,
                    label = { Text(text = stringResource(sortDirection.labelRes)) },
                    leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
                    modifier = Modifier.semantics { this.contentDescription = contentDescription }
                )
            }
        }
        items(artists) { artist ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    leadingContent = {
                        val ctx = LocalContext.current
                        val sampleUri = remember(artist.id) { findSampleSongUriFor(artist) }
                        val palette by androidx.compose.runtime.produceState<ArtworkPaletteCache.ArtworkPalette?>(
                            initialValue = null,
                            artist.id,
                            sampleUri
                        ) {
                            value = ArtworkPaletteCache.getForArtist(ctx, artist.id, sampleUri)
                        }
                        val baseP = MaterialTheme.colorScheme.secondary
                        val baseS = MaterialTheme.colorScheme.tertiary
                        val targetP = palette?.dominant?.let { Color(it) } ?: baseP
                        val targetS = palette?.vibrant?.let { Color(it) } ?: baseS
                        val p by animateColorAsState(targetValue = targetP, animationSpec = tween(600), label = "artistP")
                        val s by animateColorAsState(targetValue = targetS, animationSpec = tween(600), label = "artistS")
                        val brush = Brush.radialGradient(listOf(p.copy(alpha = 0.35f), s.copy(alpha = 0.2f), Color.Transparent))
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Outlined.LibraryMusic, contentDescription = null)
                        }
                    },
                    headlineContent = { Text(text = artist.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    supportingContent = {
                        Text(text = stringResource(R.string.artist_row_detail, artist.albumCount, artist.trackCount))
                    }
                )
            }
        }
    }
}

@Composable
private fun GenresTab(genres: List<GenreSummary>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.genres_tab_count, genres.size),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(genres) { genre ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text(text = genre.name) },
                    trailingContent = {
                        Text(text = stringResource(R.string.genre_row_track_count, genre.trackCount))
                    }
                )
            }
        }
    }
}

@Composable
private fun LongformTab(
    state: HomeUiState,
    onFilterSelected: (LongformFilter) -> Unit,
    onPlayNow: (String) -> Unit,
    onClearBookmarkFor: (String) -> Unit
) {
    val defaultSongId = state.songs.firstOrNull()?.id
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LongformFilter.entries.forEach { filter ->
                FilterChip(
                    selected = state.longformFilter == filter,
                    onClick = { onFilterSelected(filter) },
                    label = { Text(text = stringResource(filter.labelRes)) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.filteredLongformItems) { item ->
                ElevatedCard(
                    onClick = { defaultSongId?.let(onPlayNow) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = defaultSongId != null
                ) {
                    val bookmark = state.longformBookmarks[item.id]
                    ListItem(
                        headlineContent = { Text(text = item.title) },
                        supportingContent = {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = stringResource(R.string.longform_row_source, item.source))
                                if (bookmark != null && bookmark > 0) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text(text = stringResource(R.string.longform_resume_bookmark, formatDuration(bookmark))) }
                                        )
                                        TextButton(onClick = { onClearBookmarkFor(item.id) }) {
                                            Text(text = stringResource(R.string.longform_clear_bookmark))
                                        }
                                    }
                                }
                            }
                        },
                        trailingContent = {
                            Text(text = formatDuration(item.durationMs))
                        }
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
@AndroidOptIn(UnstableApi::class)
@UnstableApi
private fun VideosTab(
    videos: List<VideoSummary>,
    sortDirection: SortDirection,
    onToggleSortDirection: () -> Unit,
    onPlayAll: () -> Unit,
    sortField: VideoSortField,
    onSortFieldSelected: (VideoSortField) -> Unit,
    onRequestPermission: () -> Unit,
    isPermissionGranted: Boolean,
    onPlayVideo: (String) -> Unit
) {
    if (videos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Outlined.VideoLibrary, contentDescription = null)
                Text(text = stringResource(R.string.videos_tab_empty), style = MaterialTheme.typography.bodyMedium)
                if (!isPermissionGranted) {
                    TextButton(onClick = onRequestPermission) {
                        Text(text = stringResource(R.string.grant_video_permission))
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.videos_tab_count, videos.size),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onPlayAll) {
                            Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.play_action))
                        }
                        val icon = if (sortDirection == SortDirection.Ascending) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward
                        val contentDescription = stringResource(
                            if (sortDirection == SortDirection.Ascending) R.string.videos_sort_direction_duration_ascending else R.string.videos_sort_direction_duration_descending
                        )
                        AssistChip(
                            onClick = onToggleSortDirection,
                            label = { Text(text = contentDescription) },
                            leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
                            modifier = Modifier.semantics { this.contentDescription = contentDescription }
                        )
                        val fieldLabel = if (sortField == VideoSortField.Duration) R.string.videos_sort_by_title else R.string.videos_sort_by_duration
                        AssistChip(
                            onClick = {
                                val next = if (sortField == VideoSortField.Duration) VideoSortField.Title else VideoSortField.Duration
                                onSortFieldSelected(next)
                            },
                            label = { Text(text = stringResource(fieldLabel)) }
                        )
                    }
                }
            }
            items(videos) { video ->
                ElevatedCard(onClick = { onPlayVideo(video.id) }, modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        leadingContent = {
                            Icon(imageVector = Icons.Outlined.VideoLibrary, contentDescription = null)
                        },
                        headlineContent = { Text(text = video.title) },
                        trailingContent = { Text(text = formatDuration(video.durationMs)) }
                    )
                }
            }
        }
    }
}

// ---- Now Playing & mini player -----------------------------------------------------------------

@Composable
private fun NowPlayingCard(
    state: PlayerUiState,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onPlaybackSpeedSelected: (Float) -> Unit,
    onShowLyrics: () -> Unit,
    onShowTagEditor: () -> Unit,
    onShare: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.now_playing_title), style = MaterialTheme.typography.titleMedium)
            Text(text = state.title, style = MaterialTheme.typography.headlineSmall)
            Text(text = stringResource(R.string.now_playing_artist_album, state.artist, state.album))
            PlaybackSlider(
                state = state,
                onSeekTo = onSeekTo,
                modifier = Modifier.fillMaxWidth()
            )
            PlaybackSpeedSelector(
                currentSpeed = state.playbackSpeed,
                speeds = state.availablePlaybackSpeeds,
                onSpeedSelected = onPlaybackSpeedSelected
            )
            val shareContentDescription = stringResource(R.string.share_song_content_description)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onTogglePlayPause) {
                    Text(
                        text = if (state.isPlaying) stringResource(R.string.pause_action) else stringResource(
                            R.string.play_action
                        )
                    )
                }
                AssistChip(onClick = onShowLyrics, label = { Text(text = stringResource(R.string.lyrics_tab)) })
                AssistChip(onClick = onShowTagEditor, label = { Text(text = stringResource(R.string.tags_tab)) })
                AssistChip(
                    onClick = onShare,
                    label = { Text(text = stringResource(R.string.share_song_action)) },
                    leadingIcon = { Icon(imageVector = Icons.Outlined.Share, contentDescription = null) },
                    modifier = Modifier.semantics { contentDescription = shareContentDescription }
                )
            }
        }
    }
}

@Composable
private fun MiniPlayer(
    state: PlayerUiState,
    onTogglePlayPause: () -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    isShuffleEnabled: Boolean,
    repeatMode: RepeatUi,
    onOpenNowPlaying: () -> Unit,
    onShowQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(tonalElevation = 3.dp, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenNowPlaying() }
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dominant color bar (best-effort from embedded album art)
            val ctx = LocalContext.current
            val songId = state.currentSongId
            val audioUri = remember(songId) {
                val parts = songId?.split(":")
                val idNum = parts?.getOrNull(1)?.toLongOrNull()
                if (idNum != null) {
                    val base = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    android.content.ContentUris.withAppendedId(base, idNum).toString()
                } else null
            }
            val dominantColor by androidx.compose.runtime.produceState<Int?>(initialValue = null, audioUri) {
                value = app.ember.studio.art.ArtworkColor.computeDominantColor(ctx, audioUri)
            }
            val color = dominantColor?.let { Color(it) } ?: MaterialTheme.colorScheme.primary
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(color))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.now_playing_artist_album, state.artist, state.album),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isShuffleEnabled) {
                        AssistChip(onClick = onToggleShuffle, label = { Text(text = stringResource(R.string.shuffle_active_label)) })
                    }
                    if (repeatMode != RepeatUi.Off) {
                        val label = if (repeatMode == RepeatUi.One) R.string.repeat_one_label else R.string.repeat_all_label
                        AssistChip(onClick = onCycleRepeat, label = { Text(text = stringResource(label)) })
                    }
                }
            }
            IconButton(onClick = onPlayPrevious, modifier = Modifier.testTag("miniPrev")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.mini_player_previous_content_description)
                )
            }
            IconButton(onClick = onTogglePlayPause, modifier = Modifier.testTag("miniPlayPause")) {
                val icon = if (state.isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow
                val description = if (state.isPlaying) {
                    stringResource(R.string.pause_content_description)
                } else {
                    stringResource(R.string.play_content_description)
                }
                Icon(imageVector = icon, contentDescription = description)
            }
            IconButton(onClick = onPlayNext, modifier = Modifier.testTag("miniNext")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = stringResource(R.string.mini_player_next_content_description)
                )
            }
            IconButton(onClick = onToggleShuffle) {
                val tintColor = if (isShuffleEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                Icon(imageVector = Icons.Outlined.Shuffle, contentDescription = stringResource(R.string.shuffle_toggle), tint = tintColor)
            }
            IconButton(onClick = onCycleRepeat) {
                val icon = when (repeatMode) {
                    RepeatUi.Off -> Icons.Outlined.Repeat
                    RepeatUi.All -> Icons.Outlined.Repeat
                    RepeatUi.One -> Icons.Outlined.RepeatOne
                }
                val tintColor = if (repeatMode != RepeatUi.Off) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                Icon(imageVector = icon, contentDescription = stringResource(R.string.repeat_cycle), tint = tintColor)
            }
            IconButton(onClick = onShowQueue) {
                Icon(
                    imageVector = Icons.Outlined.QueueMusic,
                    contentDescription = stringResource(R.string.mini_player_queue_content_description)
                )
            }
        }
    }
}

// ---- Queue --------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun QueueSheet(
    queueState: QueueUiState,
    onSongSelected: (String) -> Unit,
    onRemoveFromQueue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.queue_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
        queueState.nowPlaying?.let { nowPlaying ->
            item {
                Text(text = stringResource(R.string.queue_now_playing), style = MaterialTheme.typography.titleMedium)
                ListItem(
                    headlineContent = { Text(text = nowPlaying.title) },
                    supportingContent = {
                        Text(text = stringResource(R.string.song_row_subtitle, nowPlaying.artist, nowPlaying.album))
                    },
                    trailingContent = { Text(text = formatDuration(nowPlaying.durationMs)) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }
        }
        if (queueState.history.isNotEmpty()) {
            item {
                Text(text = stringResource(R.string.queue_history_header), style = MaterialTheme.typography.titleSmall)
            }
            items(queueState.history, key = { it.id }) { song ->
                ListItem(
                    headlineContent = { Text(text = song.title) },
                    supportingContent = { Text(text = song.artist) },
                    trailingContent = { Text(text = formatDuration(song.durationMs)) }
                )
            }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp)) }
        }
        item {
            Text(text = stringResource(R.string.queue_up_next_header), style = MaterialTheme.typography.titleSmall)
        }
        if (queueState.upNext.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.queue_empty_up_next),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(queueState.upNext, key = { it.id }) { song ->
                Column {
                    QueueUpNextRow(
                        song = song,
                        onSongSelected = onSongSelected,
                        onRemoveFromQueue = onRemoveFromQueue
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun QueueUpNextRow(
    song: SongSummary,
    onSongSelected: (String) -> Unit,
    onRemoveFromQueue: (String) -> Unit
) {
    val dismissState = rememberDismissState { value ->
        if (value == DismissValue.DismissedToEnd || value == DismissValue.DismissedToStart) {
            onRemoveFromQueue(song.id)
            true
        } else {
            false
        }
    }
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = { QueueDismissBackground(dismissState = dismissState) },
        dismissContent = {
            ListItem(
                modifier = Modifier.clickable { onSongSelected(song.id) },
                headlineContent = { Text(text = song.title) },
                supportingContent = { Text(text = song.artist) },
                trailingContent = {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = formatDuration(song.durationMs))
                        IconButton(onClick = { onRemoveFromQueue(song.id) }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.queue_remove_content_description)
                            )
                        }
                    }
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun QueueDismissBackground(dismissState: DismissState) {
    val backgroundColor = MaterialTheme.colorScheme.errorContainer
    val contentColor = MaterialTheme.colorScheme.onErrorContainer
    val alignment = when (dismissState.dismissDirection) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
        else -> Alignment.CenterEnd
    }
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 12.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(contentAlignment = alignment, modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                Text(text = stringResource(R.string.queue_remove_label), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// ---- Now Playing Sheet -------------------------------------------------------------------------

@Composable
@OptIn(ExperimentalMaterialApi::class)
@AndroidOptIn(UnstableApi::class)
private fun NowPlayingScreen(
    state: PlayerUiState,
    player: ExoPlayer?,
    onTogglePlayPause: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onPlaybackSpeedSelected: (Float) -> Unit,
    onAddBookmark: () -> Unit,
    onResumeBookmark: () -> Unit,
    onClearBookmark: () -> Unit,
    onShowLyrics: () -> Unit,
    onShowTagEditor: () -> Unit,
    isShuffleEnabled: Boolean,
    repeatMode: RepeatUi,
    useHaptics: Boolean,
    modifier: Modifier = Modifier
) {
    // Compute dominant color from embedded artwork (best-effort)
    val ctx = LocalContext.current
    val songId = state.currentSongId
    val audioUri = remember(songId) {
        val parts = songId?.split(":")
        val idNum = parts?.getOrNull(1)?.toLongOrNull()
        if (idNum != null) {
            val base = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            android.content.ContentUris.withAppendedId(base, idNum).toString()
        } else null
    }
    val dominantColorInt by androidx.compose.runtime.produceState<Int?>(initialValue = null, audioUri) {
        value = withContext(Dispatchers.IO) { app.ember.studio.art.ArtworkColor.computeDominantColor(ctx, audioUri) }
    }
    val dominant = dominantColorInt?.let { Color(it) }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedNowPlayingBackground(dominant)
        // Scrim for readability
        Box(modifier = Modifier.matchParentSize().background(MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        if (player != null && player.videoSize.width > 0 && player.videoSize.height > 0) {
            var aspectFill by rememberSaveable { mutableStateOf(false) }
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        useController = false
                        this.player = player
                        resizeMode = if (aspectFill) AspectRatioFrameLayout.RESIZE_MODE_ZOOM else AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                },
                update = { view ->
                    view.resizeMode = if (aspectFill) AspectRatioFrameLayout.RESIZE_MODE_ZOOM else AspectRatioFrameLayout.RESIZE_MODE_FIT
                },
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
            // Simple inline controls — mute toggle lives near the video
            var lastVolume by rememberSaveable { mutableStateOf(1f) }
            val isMuted = remember(player?.volume) { (player?.volume ?: 0f) <= 0f }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onTogglePlayPause) {
                    val icon = if (state.isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow
                    Icon(imageVector = icon, contentDescription = null)
                }
                IconButton(onClick = {
                    val newPos = (state.positionMs - 10_000L).coerceAtLeast(0L)
                    onSeekTo(newPos)
                }, modifier = Modifier.testTag("videoSkipBack10")) {
                    Icon(imageVector = Icons.Outlined.Replay10, contentDescription = stringResource(R.string.skip_back_10))
                }
                IconButton(onClick = {
                    val dur = state.durationMs.takeIf { it > 0 } ?: Long.MAX_VALUE
                    val newPos = (state.positionMs + 10_000L).coerceAtMost(dur)
                    onSeekTo(newPos)
                }, modifier = Modifier.testTag("videoSkipForward10")) {
                    Icon(imageVector = Icons.Outlined.Forward10, contentDescription = stringResource(R.string.skip_forward_10))
                }
                IconButton(onClick = { aspectFill = !aspectFill }, modifier = Modifier.testTag("videoAspectToggle")) {
                    val cd = if (aspectFill) R.string.video_aspect_fill else R.string.video_aspect_fit
                    Icon(imageVector = Icons.Outlined.AspectRatio, contentDescription = stringResource(cd))
                }
                IconButton(onClick = onToggleShuffle) {
                    val tintColor = if (isShuffleEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    Icon(imageVector = Icons.Outlined.Shuffle, contentDescription = stringResource(R.string.shuffle_toggle), tint = tintColor)
                }
                IconButton(onClick = onCycleRepeat) {
                    val icon = when (repeatMode) {
                        RepeatUi.Off -> Icons.Outlined.Repeat
                        RepeatUi.All -> Icons.Outlined.Repeat
                        RepeatUi.One -> Icons.Outlined.RepeatOne
                    }
                    val tintColor = if (repeatMode != RepeatUi.Off) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    Icon(imageVector = icon, contentDescription = stringResource(R.string.repeat_cycle), tint = tintColor)
                }
                IconButton(onClick = {
                    player?.let { p ->
                        if ((p.volume) <= 0f) {
                            // unmute
                            p.volume = lastVolume.coerceIn(0f, 1f).let { if (it == 0f) 1f else it }
                        } else {
                            // remember and mute
                            lastVolume = p.volume
                            p.volume = 0f
                        }
                    }
                }) {
                    if (isMuted) {
                        Icon(imageVector = Icons.Outlined.VolumeOff, contentDescription = "Unmute video")
                    } else {
                        Icon(imageVector = Icons.Outlined.VolumeUp, contentDescription = "Mute video")
                    }
                }
            }
            // Compact scrub + time labels specific to video surface
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = formatDuration(state.positionMs), style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = if (state.durationMs > 0) state.positionMs.toFloat() / state.durationMs.toFloat() else 0f,
                    onValueChange = { fraction ->
                        val target = (fraction.coerceIn(0f, 1f) * state.durationMs).toLong()
                        onSeekTo(target)
                    },
                    modifier = Modifier.weight(1f)
                )
                val remaining = (state.durationMs - state.positionMs).coerceAtLeast(0L)
                Text(text = "-" + formatDuration(remaining), style = MaterialTheme.typography.labelSmall)
            }

            // Quick playback speed toggles under video
            val speeds = state.availablePlaybackSpeeds
            if (speeds.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.playback_speed_menu_title), style = MaterialTheme.typography.labelMedium)
                    speeds.forEach { s ->
                        val selected = kotlin.math.abs(state.playbackSpeed - s) < 0.01f
                        FilterChip(
                            selected = selected,
                            onClick = { onPlaybackSpeedSelected(s) },
                            label = { Text(text = s.toPlaybackSpeedLabel()) }
                        )
                    }
                }
            }
        }
        // Audio artwork for audio-only content
        if (player == null || (player.videoSize.width == 0 && player.videoSize.height == 0)) {
            NowPlayingArtwork()
        }
        Text(text = state.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Text(text = stringResource(R.string.now_playing_artist_album, state.artist, state.album), color = MaterialTheme.colorScheme.onSurfaceVariant)
        PlaybackSlider(state = state, onSeekTo = onSeekTo, modifier = Modifier.fillMaxWidth())
        // Stylish primary transport controls
        val haptics = LocalHapticFeedback.current
        NowPlayingControlsPanel(
            isPlaying = state.isPlaying,
            onPlayPause = onTogglePlayPause,
            onPrev = { player?.seekToPreviousMediaItem() },
            onNext = { player?.seekToNextMediaItem() },
            onToggleShuffle = onToggleShuffle,
            onCycleRepeat = onCycleRepeat,
            isShuffleEnabled = isShuffleEnabled,
            repeatMode = repeatMode,
            useHaptics = useHaptics
        )
        PlaybackSpeedSelector(
            currentSpeed = state.playbackSpeed,
            speeds = state.availablePlaybackSpeeds,
            onSpeedSelected = onPlaybackSpeedSelected
        )
        // Longform bookmarks
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilledTonalButton(onClick = onAddBookmark) {
                Text(text = stringResource(R.string.longform_add_bookmark))
            }
            if (state.bookmarkPositionMs != null && state.bookmarkPositionMs > 0) {
                TextButton(onClick = onResumeBookmark) {
                    Text(text = stringResource(R.string.longform_resume_bookmark, formatDuration(state.bookmarkPositionMs)))
                }
                TextButton(onClick = onClearBookmark) {
                    Text(text = stringResource(R.string.longform_clear_bookmark))
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onTogglePlayPause) {
                Text(
                    text = if (state.isPlaying) stringResource(R.string.pause_action) else stringResource(
                        R.string.play_action
                    )
                )
            }
            TextButton(onClick = onShowLyrics) {
                Text(text = stringResource(R.string.lyrics_tab))
            }
            TextButton(onClick = onShowTagEditor) {
                Text(text = stringResource(R.string.tags_tab))
            }
        }
        }
    }
}

@Composable
private fun NowPlayingControlsPanel(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    isShuffleEnabled: Boolean,
    repeatMode: RepeatUi,
    useHaptics: Boolean,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    val shape = RoundedCornerShape(48.dp)
    ElevatedCard(shape = shape, modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .background(surfaceColor)
        ) {
            // Inner shadow overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.04f),
                            0.5f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.06f)
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (useHaptics) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPrev()
                    }, modifier = Modifier.size(56.dp)) {
                        Icon(imageVector = Icons.Outlined.SkipPrevious, contentDescription = null)
                    }
                    ElevatedCard(shape = RoundedCornerShape(48.dp)) {
                        IconButton(onClick = {
                            if (useHaptics) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            onPlayPause()
                        }, modifier = Modifier.size(72.dp)) {
                            val icon = if (isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow
                            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    IconButton(onClick = {
                        if (useHaptics) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNext()
                    }, modifier = Modifier.size(56.dp)) {
                        Icon(imageVector = Icons.Outlined.SkipNext, contentDescription = null)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (useHaptics) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggleShuffle()
                    }) {
                        val tintColor = if (isShuffleEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        Icon(imageVector = Icons.Outlined.Shuffle, contentDescription = stringResource(R.string.shuffle_toggle), tint = tintColor)
                    }
                    IconButton(onClick = {
                        if (useHaptics) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCycleRepeat()
                    }) {
                        val icon = when (repeatMode) {
                            RepeatUi.Off -> Icons.Outlined.Repeat
                            RepeatUi.All -> Icons.Outlined.Repeat
                            RepeatUi.One -> Icons.Outlined.RepeatOne
                        }
                        val tintColor = if (repeatMode != RepeatUi.Off) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        Icon(imageVector = icon, contentDescription = stringResource(R.string.repeat_cycle), tint = tintColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedNowPlayingBackground(dominant: Color?) {
    val base = dominant ?: MaterialTheme.colorScheme.primary
    val sTheme = MaterialTheme.colorScheme.secondary
    val tTheme = MaterialTheme.colorScheme.tertiary
    val colorTransition = updateTransition(targetState = base, label = "npBg")
    val p by colorTransition.animateColor(transitionSpec = { tween(durationMillis = 1200, easing = LinearEasing) }, label = "p") { it }
    val s by colorTransition.animateColor(transitionSpec = { tween(durationMillis = 1200, easing = LinearEasing) }, label = "s") { androidx.compose.ui.graphics.lerp(it, sTheme, 0.5f) }
    val t by colorTransition.animateColor(transitionSpec = { tween(durationMillis = 1200, easing = LinearEasing) }, label = "t") { androidx.compose.ui.graphics.lerp(it, tTheme, 0.5f) }
    val colors = listOf(
        p.copy(alpha = 0.55f),
        s.copy(alpha = 0.45f),
        t.copy(alpha = 0.40f),
        p.copy(alpha = 0.55f)
    )
    val infinite = rememberInfiniteTransition(label = "npGradient")
    val shift = infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "npShift"
    )
    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, shift.value / 3f),
        end = Offset(shift.value, shift.value / 2f)
    )
    Box(modifier = Modifier.fillMaxSize().background(brush))
}

@Composable
private fun NowPlayingArtwork() {
    val p = MaterialTheme.colorScheme.primary
    val s = MaterialTheme.colorScheme.secondary
    val brush = Brush.radialGradient(
        colors = listOf(p.copy(alpha = 0.25f), s.copy(alpha = 0.15f), Color.Transparent)
    )
    Box(
        modifier = Modifier
            .size(220.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(brush),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = Icons.Outlined.LibraryMusic, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
    }
}

// ---- Placeholder screens -----------------------------------------------------------------------

@Composable
private fun PlaylistDetailScreen(
    state: PlaylistDetailUiState?,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
    onSongSelected: (String) -> Unit,
    onMoveUp: (String) -> Unit = {},
    onMoveDown: (String) -> Unit = {},
    onRemove: (String) -> Unit = {},
    onExport: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (state == null) return
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = state.title, style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(R.string.playlist_detail_summary, state.items.size, formatDuration(state.totalDurationMs)),
            style = MaterialTheme.typography.bodyMedium
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onPlay) { Text(text = stringResource(R.string.play_action)) }
            FilledTonalButton(onClick = onShuffle) { Text(text = stringResource(R.string.songs_play_all_button)) }
            if (state.id.startsWith("user:") && onExport != null) {
                TextButton(onClick = onExport) { Text(text = stringResource(R.string.playlists_export_button)) }
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.items) { song ->
                ElevatedCard(
                    onClick = { onSongSelected(song.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(state.id, song.id) {
                            var accum = 0f
                            detectDragGestures(
                                onDragStart = { accum = 0f },
                                onDrag = { _, dragAmount ->
                                    accum += dragAmount.y
                                    if (accum <= -40f) {
                                        onMoveUp(song.id)
                                        accum = 0f
                                    } else if (accum >= 40f) {
                                        onMoveDown(song.id)
                                        accum = 0f
                                    }
                                }
                            )
                        }
                ) {
                    ListItem(
                        headlineContent = { Text(text = song.title) },
                        supportingContent = { Text(text = song.artist) },
                        trailingContent = {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = formatDuration(song.durationMs))
                                if (state.id.startsWith("user:")) {
                                    IconButton(onClick = { onMoveUp(song.id) }) { Icon(imageVector = Icons.Outlined.ArrowUpward, contentDescription = null) }
                                    IconButton(onClick = { onMoveDown(song.id) }) { Icon(imageVector = Icons.Outlined.ArrowDownward, contentDescription = null) }
                                    IconButton(onClick = { onRemove(song.id) }) { Icon(imageVector = Icons.Outlined.Delete, contentDescription = null) }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = stringResource(titleRes), style = MaterialTheme.typography.titleLarge)
            Text(text = stringResource(messageRes), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ScanImportScreen(
    folders: List<String>,
    onAddFolder: () -> Unit,
    onRemoveFolder: (String) -> Unit,
    onRescanLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(R.string.drawer_scan_import), style = MaterialTheme.typography.titleLarge)
        Text(text = stringResource(R.string.placeholder_scan_import), style = MaterialTheme.typography.bodyMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onAddFolder) { Text(text = stringResource(R.string.folders_add_button)) }
            OutlinedButton(onClick = onRescanLibrary) { Text(text = stringResource(R.string.songs_rescan_button)) }
        }

        HorizontalDivider()
        Text(text = stringResource(R.string.folders_saved_header), style = MaterialTheme.typography.titleMedium)

        if (folders.isEmpty()) {
            Text(text = stringResource(R.string.folders_empty_state), style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(folders) { uriString ->
                    ListItem(
                        headlineContent = { Text(text = uriString, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                        trailingContent = {
                            AssistChip(
                                onClick = { onRemoveFolder(uriString) },
                                label = { Text(text = stringResource(R.string.folders_remove_button)) }
                            )
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

// ---- Playback slider & speed selector -----------------------------------------------------------

@Composable
private fun PlaybackSlider(
    state: PlayerUiState,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val fraction =
            if (state.durationMs == 0L) 0f
            else (state.positionMs.toFloat() / state.durationMs.toFloat()).coerceIn(0f, 1f)
        // Glow track behind the slider for a premium feel
        val colorScheme = MaterialTheme.colorScheme
        val primary = colorScheme.primary
        val onSurface = colorScheme.onSurface
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .drawBehind {
                val w = this.size.width * fraction
                val h = 6.dp.toPx()
                val y = this.size.height / 2f - h / 2f
                val glowBrush = Brush.horizontalGradient(listOf(primary.copy(alpha = 0f), primary.copy(alpha = 0.35f), primary.copy(alpha = 0f)))
                // base track
                drawRoundRect(color = onSurface.copy(alpha = 0.12f), topLeft = androidx.compose.ui.geometry.Offset(0f, y), size = androidx.compose.ui.geometry.Size(this.size.width, h), cornerRadius = androidx.compose.ui.geometry.CornerRadius(h/2, h/2))
                // active track
                drawRoundRect(color = primary, topLeft = androidx.compose.ui.geometry.Offset(0f, y), size = androidx.compose.ui.geometry.Size(w, h), cornerRadius = androidx.compose.ui.geometry.CornerRadius(h/2, h/2))
                // glow overlay
                drawRect(brush = glowBrush, topLeft = androidx.compose.ui.geometry.Offset(0f, y - 4.dp.toPx()), size = androidx.compose.ui.geometry.Size(w, h + 8.dp.toPx()))
            }
        ) {
            // Overlay the interactive slider with transparent track to preserve gestures
        }
        Slider(
            value = fraction,
            onValueChange = { value ->
                if (state.durationMs > 0) {
                    onSeekTo((state.durationMs * value).toLong())
                }
            },
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = primary,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent
            )
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = state.positionText, style = MaterialTheme.typography.labelSmall)
            Text(text = state.durationText, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun PlaybackSpeedSelector(
    currentSpeed: Float,
    speeds: List<Float>,
    onSpeedSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLabel = currentSpeed.toPlaybackSpeedLabel()
    val speedContentDescription = stringResource(R.string.playback_speed_content_description, currentLabel)
    Box(modifier = modifier) {
        AssistChip(
            onClick = { expanded = true },
            label = { Text(text = stringResource(R.string.playback_speed_label, currentLabel)) },
            leadingIcon = { Icon(imageVector = Icons.Outlined.Timer, contentDescription = null) },
            modifier = Modifier.semantics { contentDescription = speedContentDescription }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Text(
                text = stringResource(R.string.playback_speed_menu_title),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            speeds.forEach { speed ->
                val isSelected = abs(speed - currentSpeed) < PLAYBACK_SPEED_TOLERANCE
                val optionLabel = stringResource(R.string.playback_speed_option, speed.toPlaybackSpeedLabel())
                DropdownMenuItem(
                    text = { Text(text = optionLabel) },
                    onClick = {
                        expanded = false
                        onSpeedSelected(speed)
                    },
                    leadingIcon = if (isSelected) {
                        { Icon(imageVector = Icons.Outlined.Check, contentDescription = null) }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

// ---- Equalizer ---------------------------------------------------------------------------------

@Composable
private fun EqualizerScreen(
    state: EqualizerUiState,
    onEqualizerEnabledChange: (Boolean) -> Unit,
    onBandLevelChange: (Int, Int) -> Unit,
    onPresetSelected: (Int) -> Unit,
    onBassBoostChange: (Int) -> Unit,
    onVirtualizerChange: (Int) -> Unit,
    onReverbSelected: (Short) -> Unit,
    onResetBands: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = stringResource(R.string.equalizer_tab), style = MaterialTheme.typography.titleLarge)
        if (!state.isAvailable) {
            Text(text = stringResource(R.string.equalizer_not_available))
            return
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.equalizer_enable_label),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium
            )
            androidx.compose.material3.Switch(
                checked = state.isEnabled,
                onCheckedChange = onEqualizerEnabledChange,
                modifier = Modifier.testTag(EqualizerTestTags.ENABLE_SWITCH)
            )
        }
        AnimatedVisibility(visible = !state.isEnabled, enter = fadeIn(), exit = fadeOut()) {
            Text(
                text = stringResource(R.string.equalizer_disabled_message),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        val canReset = state.bands.any { it.levelMillibels != 0 } || state.selectedPresetIndex != -1
        if (state.presets.isEmpty()) {
            Text(text = stringResource(R.string.equalizer_no_presets))
        } else {
            Text(text = stringResource(R.string.equalizer_preset_label), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.presets.forEachIndexed { index, preset ->
                    FilterChip(
                        selected = state.selectedPresetIndex == index,
                        onClick = { onPresetSelected(index) },
                        enabled = state.isEnabled,
                        label = { Text(text = preset) },
                        modifier = Modifier.testTag("equalizerPreset_$index")
                    )
                }
            }
        }
        state.bands.forEach { band ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = band.label, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = stringResource(R.string.equalizer_gain_value, band.levelMillibels / 100f),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Slider(
                    value = band.levelMillibels.toFloat(),
                    onValueChange = { onBandLevelChange(band.index, it.toInt()) },
                    valueRange = band.minLevelMillibels.toFloat()..band.maxLevelMillibels.toFloat(),
                    enabled = state.isEnabled,
                    modifier = Modifier.testTag("equalizerBand_${band.index}")
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.equalizer_gain_value, band.minLevelMillibels / 100f),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = stringResource(R.string.equalizer_gain_value, band.maxLevelMillibels / 100f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        TextButton(onClick = onResetBands, enabled = canReset) {
            Text(text = stringResource(R.string.equalizer_reset_button))
        }
        if (state.isBassBoostSupported) {
            Text(text = stringResource(R.string.equalizer_bass_boost_label), style = MaterialTheme.typography.titleMedium)
            Slider(
                value = state.bassBoostStrength.toFloat(),
                onValueChange = { onBassBoostChange(it.toInt()) },
                valueRange = 0f..1000f,
                enabled = state.isEnabled
            )
        }
        if (state.isVirtualizerAvailable) {
            Text(text = stringResource(R.string.equalizer_virtualizer_label), style = MaterialTheme.typography.titleMedium)
            if (state.isVirtualizerSupported) {
                Slider(
                    value = state.virtualizerStrength.toFloat(),
                    onValueChange = { onVirtualizerChange(it.toInt()) },
                    valueRange = 0f..1000f,
                    enabled = state.isEnabled,
                    modifier = Modifier.testTag(EqualizerTestTags.VIRTUALIZER_SLIDER)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.isVirtualizationEnabled) stringResource(R.string.dark_mode_on) else stringResource(R.string.dark_mode_off),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    androidx.compose.material3.Switch(
                        checked = state.isVirtualizationEnabled,
                        onCheckedChange = { checked -> onVirtualizerChange(if (checked) 1000 else 0) },
                        enabled = state.isEnabled,
                        modifier = Modifier.testTag(EqualizerTestTags.VIRTUALIZER_SWITCH)
                    )
                }
                Text(
                    text = stringResource(R.string.equalizer_virtualizer_spatializer_hint),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Text(text = stringResource(R.string.equalizer_reverb_label), style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.reverbOptions.forEach { option ->
                FilterChip(
                    selected = state.selectedReverbPreset == option.preset,
                    onClick = { onReverbSelected(option.preset) },
                    enabled = state.isEnabled,
                    label = { Text(text = option.label) }
                )
            }
        }
    }
}

object EqualizerTestTags {
    const val ENABLE_SWITCH = "equalizerEnableSwitch"
    const val VIRTUALIZER_SWITCH = "equalizerVirtualizerSwitch"
    const val VIRTUALIZER_SLIDER = "equalizerVirtualizerSlider"
}

// ---- Lyrics ------------------------------------------------------------------------------------

@Composable
private fun LyricsScreen(
    state: LyricsUiState,
    onDraftChange: (String) -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit,
    onLoadSample: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val characterCount = state.draftLyrics.length
    val lineCount = if (state.draftLyrics.isBlank()) 0 else state.draftLyrics.lines().size
    Column(
        modifier = modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(R.string.lyrics_tab), style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(R.string.lyrics_editor_helper),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = state.draftLyrics,
            onValueChange = onDraftChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            label = { Text(text = stringResource(R.string.lyrics_input_label)) },
            placeholder = { Text(text = stringResource(R.string.lyrics_input_hint)) },
            supportingText = {
                Text(text = stringResource(R.string.lyrics_character_count, characterCount, lineCount))
            }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onSave, enabled = state.isSaveEnabled) {
                Text(text = stringResource(R.string.lyrics_save_button))
            }
            TextButton(onClick = onClear) {
                Text(text = stringResource(R.string.lyrics_clear_button))
            }
            AssistChip(onClick = onLoadSample, label = { Text(text = stringResource(R.string.lyrics_load_sample_button)) })
        }
        AnimatedVisibility(visible = state.statusMessage != null, enter = fadeIn(), exit = fadeOut()) {
            state.statusMessage?.let {
                AssistChip(onClick = {}, label = { Text(text = it) })
            }
        }
        HorizontalDivider()
        Text(text = stringResource(R.string.lyrics_saved_header), style = MaterialTheme.typography.titleMedium)
        if (state.currentLyrics.isBlank()) {
            Text(text = stringResource(R.string.lyrics_empty_state))
        } else {
            Surface(tonalElevation = 2.dp, shape = RoundedCornerShape(12.dp)) {
                Text(
                    text = state.currentLyrics,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

// ---- Theme studio ------------------------------------------------------------------------------

@Composable
private fun ThemeScreen(
    state: ThemeUiState,
    onSelectOption: (Int) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onToggleDynamicColor: (Boolean) -> Unit,
    onToggleAmoledBlack: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = stringResource(R.string.themes_tab), style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(R.string.theme_preview_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        state.options.forEachIndexed { index, option ->
            val isSelected = state.selectedOptionIndex == index
            val previewScheme = option.colorScheme(state.useDarkTheme)
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelectOption(index) },
                colors = CardDefaults.elevatedCardColors(containerColor = previewScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(option.labelRes), style = MaterialTheme.typography.titleMedium)
                        androidx.compose.material3.RadioButton(
                            selected = isSelected,
                            onClick = { onSelectOption(index) }
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ThemeSwatch(color = previewScheme.primary)
                        ThemeSwatch(color = previewScheme.secondary)
                        ThemeSwatch(color = previewScheme.surface)
                        ThemeSwatch(color = previewScheme.onSurface)
                    }
                    Text(
                        text = stringResource(
                            R.string.theme_selected_label,
                            if (isSelected) stringResource(R.string.theme_selected_active) else stringResource(R.string.theme_selected_inactive)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = stringResource(R.string.theme_dark_mode_label), modifier = Modifier.weight(1f))
            androidx.compose.material3.Switch(checked = state.useDarkTheme, onCheckedChange = onToggleDarkTheme)
        }
        Text(
            text = stringResource(R.string.theme_dark_mode_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = stringResource(R.string.theme_dynamic_color_label), modifier = Modifier.weight(1f))
            androidx.compose.material3.Switch(checked = state.useDynamicColor, onCheckedChange = onToggleDynamicColor)
        }
        Text(
            text = stringResource(R.string.theme_dynamic_color_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = stringResource(R.string.theme_amoled_black_label), modifier = Modifier.weight(1f))
            androidx.compose.material3.Switch(checked = state.useAmoledBlack, onCheckedChange = onToggleAmoledBlack)
        }
        Text(
            text = stringResource(R.string.theme_amoled_black_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ThemeSwatch(color: Color) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(color, RoundedCornerShape(12.dp))
    )
}

// ---- Tag editor --------------------------------------------------------------------------------

@Composable
private fun TagEditorScreen(
    state: TagEditorUiState,
    onTitleChange: (String) -> Unit,
    onArtistChange: (String) -> Unit,
    onAlbumChange: (String) -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(R.string.tags_tab), style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(R.string.tag_editor_helper_text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = state.titleInput,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.tag_editor_title_label)) },
            supportingText = {
                Text(text = stringResource(R.string.tag_editor_character_count, state.titleInput.length))
            }
        )
        OutlinedTextField(
            value = state.artistInput,
            onValueChange = onArtistChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.tag_editor_artist_label)) },
            supportingText = {
                Text(text = stringResource(R.string.tag_editor_character_count, state.artistInput.length))
            }
        )
        OutlinedTextField(
            value = state.albumInput,
            onValueChange = onAlbumChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.tag_editor_album_label)) },
            supportingText = {
                Text(text = stringResource(R.string.tag_editor_character_count, state.albumInput.length))
            }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onSave, enabled = state.canSave) {
                Text(text = stringResource(R.string.tag_editor_save_button))
            }
            TextButton(onClick = onReset, enabled = state.isDirty) {
                Text(text = stringResource(R.string.tag_editor_reset_button))
            }
        }
        AnimatedVisibility(visible = state.statusMessage != null, enter = fadeIn(), exit = fadeOut()) {
            state.statusMessage?.let {
                AssistChip(onClick = {}, label = { Text(text = it) })
            }
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Types below are EXPECTED to exist elsewhere in the project. They are referenced here to ensure
// this file compiles against the real implementations. No stubs are provided on purpose.
// - R (resources), HomeUiState, PlayerUiState, EqualizerUiState, LyricsUiState, TagEditorUiState,
//   OnboardingUiState, SleepTimerUiState, DrawerDestination, DrawerDestinationId, HomeTab,
//   SongSortState, SongSortField, SortDirection, QueueUiState,
//   SongSummary, PlaylistSummary, FolderSummary, AlbumSummary, ArtistSummary, GenreSummary,
//   LongformItem, VideoSummary, LongformFilter, LongformCategory,
//   SearchBucket, SearchCorpus, SearchResults, SearchUiState, searchLibrary,
//   OnboardingOverlay, SleepTimerScreen.
// ------------------------------------------------------------------------------------------------
enum class RepeatUi { Off, All, One }
