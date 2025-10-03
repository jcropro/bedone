package app.ember.studio

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.ember.core.ui.theme.EmberAudioPlayerTheme
import app.ember.core.ui.theme.ThemeUiState
import app.ember.studio.feature.songs.SongsScreen
import app.ember.studio.sleep.SleepTimerUiState
import app.ember.studio.sleep.SleepTimerEndAction
import app.ember.studio.player.MiniPlayerState
import androidx.media3.exoplayer.ExoPlayer

/**
 * Minimal working version of EmberAudioPlayerApp using only existing components
 */
@Composable
fun EmberAudioPlayerAppMinimal(
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
    miniPlayerState: MiniPlayerState = MiniPlayerState(),
    modifier: Modifier = Modifier
) {
    EmberAudioPlayerTheme(themeState = themeState) {
        Box(modifier = modifier.fillMaxSize()) {
            // Show onboarding if visible
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
                    onSelectThemeOption = onSelectThemeOption,
                    onToggleDarkTheme = onToggleDarkTheme,
                    onComplete = onCompleteOnboarding,
                    onUndo = onUndoLongformChange,
                    onDismissMessage = onConsumeOnboardingMessage
                )
            } else {
                // Show main content when onboarding is not visible
                app.ember.studio.navigation.MainNavigation(
                    modifier = Modifier.fillMaxSize(),
                    selectedDrawerDestination = homeState.selectedDrawerDestination,
                    settingsState = settingsState,
                    themeState = themeState,
                    miniPlayerState = miniPlayerState,
                    onToggleRearmOnBootEnabled = onToggleRearmOnBootEnabled,
                    onSelectRearmMinMinutes = onSelectRearmMinMinutes,
                    onToggleSkipSilenceEnabled = onToggleSkipSilenceEnabled,
                    onSelectCrossfadeMs = onSelectCrossfadeMs,
                    onSelectLongformThresholdMinutes = onSelectLongformThresholdMinutes,
                    onToggleUseHaptics = onToggleUseHaptics,
                    onRescanLibrary = onRescanLibrary,
                    onOpenScanImport = { /* TODO: Implement scan import */ },
                    onSelectThemeOption = onSelectThemeOption,
                    onToggleDarkTheme = onToggleDarkTheme,
                    onToggleDynamicColor = onToggleDynamicColor,
                    onToggleAmoledBlack = onToggleAmoledBlack,
                    onDrawerDestinationSelected = onDrawerDestinationSelected,
                    onPlayPauseClick = onTogglePlayPause,
                    onSkipNextClick = onPlayNext,
                    onSkipPreviousClick = onPlayPrevious
                )
            }
        }
    }
}
