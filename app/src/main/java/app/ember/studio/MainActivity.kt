package app.ember.studio

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.annotation.OptIn as AndroidOptIn
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaController
import app.ember.studio.media3adapters.MediaControllerAdapters
import app.ember.studio.media3adapters.MediaSessionAdapters
import app.ember.studio.RepeatUi
import app.ember.studio.di.PlayerViewModelProvider
import app.ember.studio.notifications.PlayerNotificationController
import app.ember.studio.playback.PlaybackEngine
import app.ember.studio.playback.PlaybackService
import androidx.media3.common.Player as ExoPlayerConst

/**
 * Main entry activity hosting Compose content and wiring user intents to [PlayerViewModel].
 *
 * NOTE: This file assumes the following types exist elsewhere in the project:
 * - PlayerViewModel, SongShareMessage, and the various UiState classes used below.
 */
class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels {
        val provider = PlayerViewModelProvider
        val factoryDeps = provider.dependenciesFactory
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PlayerViewModel(
                    application,
                    factoryDeps?.invoke(application) ?: PlayerViewModel.Dependencies()
                ) as T
            }
        }
    }
    private var mediaSession: MediaSession? = null
    private var mediaController: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialPermission = determineAudioPermission()
        if (initialPermission == null ||
            ContextCompat.checkSelfPermission(this, initialPermission) == PackageManager.PERMISSION_GRANTED
        ) {
            playerViewModel.onInitialPermissionGranted()
        }

        // Onboarding state will be properly managed by PlayerViewModel
        // TODO: Remove temporary bypass once onboarding state management is verified

        setContent {
            var showIgnite by rememberSaveable { mutableStateOf(true) }
            
            if (showIgnite) {
                app.ember.studio.ui.IgniteOverlay(onFinished = { showIgnite = false })
            }
            
            val homeState by playerViewModel.homeState.collectAsStateWithLifecycle()
            val playerState by playerViewModel.uiState.collectAsStateWithLifecycle()
            val equalizerState by playerViewModel.equalizerState.collectAsStateWithLifecycle()
            val lyricsState by playerViewModel.lyricsState.collectAsStateWithLifecycle()
            val themeState by playerViewModel.themeState.collectAsStateWithLifecycle()
            val tagEditorState by playerViewModel.tagEditorState.collectAsStateWithLifecycle()
            val onboardingState by playerViewModel.onboardingState.collectAsStateWithLifecycle()
            val sleepTimerState by playerViewModel.sleepTimerState.collectAsStateWithLifecycle()
            val settingsState by playerViewModel.settingsState.collectAsStateWithLifecycle()
            val globalMessage by playerViewModel.globalStatusMessage.collectAsStateWithLifecycle()
            val safFolders by playerViewModel.safFolders.collectAsStateWithLifecycle()

            val audioPermission = determineAudioPermission()
            val videoPermission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_EXTERNAL_STORAGE
            var isVideoPermissionGranted = ContextCompat.checkSelfPermission(this, videoPermission) == PackageManager.PERMISSION_GRANTED

            val permissionLauncher = rememberLauncherForPermission { granted ->
                playerViewModel.handlePermissionResult(granted)
            }

            val videoPermissionLauncher = rememberLauncherForPermission { granted ->
                isVideoPermissionGranted = granted
            }

            val folderLauncher = rememberLauncherForFolder { uri ->
                if (uri != null) {
                    persistFolderAccess(uri)
                    playerViewModel.onFolderSelected(uri)
                } else {
                    playerViewModel.onFolderSelectionCancelled()
                }
            }

            val importLauncher = rememberLauncherForPlaylist { uri ->
                if (uri != null) playerViewModel.importPlaylistFromUri(uri)
            }

            val pendingExportId = rememberSaveable { androidx.compose.runtime.mutableStateOf<String?>(null) }
            val exportLauncher = rememberLauncherForCreateM3U { uri ->
                val pid = pendingExportId.value
                if (uri != null && pid != null) playerViewModel.exportUserPlaylistToM3U(pid, uri)
                pendingExportId.value = null
            }

            val shuffleEnabled = mediaController?.shuffleModeEnabled == true
            val repeatUi = when (mediaController?.repeatMode) {
                ExoPlayerConst.REPEAT_MODE_ONE -> RepeatUi.One
                ExoPlayerConst.REPEAT_MODE_ALL -> RepeatUi.All
                else -> RepeatUi.Off
            }
            EmberAudioPlayerAppMinimal(
                homeState = homeState,
                playerState = playerState,
                equalizerState = equalizerState,
                lyricsState = lyricsState,
                themeState = themeState,
                settingsState = settingsState,
                tagEditorState = tagEditorState,
                onboardingState = onboardingState,
                sleepTimerState = sleepTimerState,
                onSelectSleepTimerQuickDuration = playerViewModel::selectSleepTimerQuickDuration,
                onSleepTimerHoursChanged = playerViewModel::updateSleepTimerHoursInput,
                onSleepTimerMinutesChanged = playerViewModel::updateSleepTimerMinutesInput,
                onSleepTimerFadeToggle = playerViewModel::setSleepTimerFade,
                onSleepTimerEndActionSelected = playerViewModel::selectSleepTimerEndAction,
                onStartSleepTimer = playerViewModel::startSleepTimer,
                onCancelSleepTimer = playerViewModel::cancelSleepTimer,
                onDismissSleepTimerMessage = playerViewModel::dismissSleepTimerStatusMessage,
                onTogglePlayPause = {
                    mediaController?.let { c -> if (c.playWhenReady) c.pause() else c.play() }
                        ?: playerViewModel.togglePlayPause()
                },
                onToggleShuffle = {
                    mediaController?.let { c -> MediaControllerAdapters.setShuffleModeEnabled(c, !c.shuffleModeEnabled) }
                        ?: run { /* ViewModel fallback not strictly needed */ }
                },
                onCycleRepeat = {
                    val c = mediaController
                    if (c != null) {
                        val next = when (c.repeatMode) {
                            androidx.media3.common.Player.REPEAT_MODE_OFF -> androidx.media3.common.Player.REPEAT_MODE_ALL
                            androidx.media3.common.Player.REPEAT_MODE_ALL -> androidx.media3.common.Player.REPEAT_MODE_ONE
                            else -> androidx.media3.common.Player.REPEAT_MODE_OFF
                        }
                        MediaControllerAdapters.setRepeatMode(c, next)
                    }
                },
                onSeekTo = { pos ->
                    mediaController?.seekTo(pos) ?: playerViewModel.seekTo(pos)
                },
                onPlaybackSpeedSelected = { speed ->
                    mediaController?.let { c -> MediaControllerAdapters.setPlaybackSpeed(c, speed) } ?: playerViewModel.setPlaybackSpeed(speed)
                },
                onEqualizerEnabledChange = playerViewModel::setEqualizerEnabled,
                onBandLevelChange = playerViewModel::setBandLevel,
                onPresetSelected = playerViewModel::selectPreset,
                onBassBoostChange = playerViewModel::setBassBoostStrength,
                onVirtualizerChange = playerViewModel::setVirtualizerStrength,
                onReverbSelected = playerViewModel::setReverbPreset,
                onResetEqualizer = playerViewModel::resetEqualizer,
                onLyricsDraftChange = playerViewModel::updateLyricsDraft,
                onLyricsSave = playerViewModel::saveLyrics,
                onLyricsClear = playerViewModel::clearLyrics,
                onSampleLyrics = playerViewModel::loadSampleLyrics,
                onSelectThemeOption = playerViewModel::selectThemeOption,
                onToggleDarkTheme = playerViewModel::setDarkTheme,
                onToggleDynamicColor = playerViewModel::setDynamicColor,
                onToggleAmoledBlack = playerViewModel::setAmoledBlack,
                onUpdateTagTitle = playerViewModel::updateTagTitle,
                onUpdateTagArtist = playerViewModel::updateTagArtist,
                onUpdateTagAlbum = playerViewModel::updateTagAlbum,
                onSaveTags = playerViewModel::saveTags,
                onResetTags = playerViewModel::resetTagEditor,
                onShareCurrentSong = ::shareCurrentSong,
                onShareSong = ::shareSpecificSong,
                onAddToFavorites = playerViewModel::addToFavorites,
                onAddSongToPlaylist = playerViewModel::addSongToUserPlaylist,
                onCreatePlaylistAndAdd = playerViewModel::addSongToNewUserPlaylist,
                onTabSelected = playerViewModel::selectTab,
                onDrawerDestinationSelected = playerViewModel::selectDrawerDestination,
                onSongSelected = playerViewModel::playSong,
                onPlayAllSongs = playerViewModel::playAllSongs,
                onPlayAllVideos = playerViewModel::playAllVideos,
                onRequestVideoPermission = {
                    val granted = ContextCompat.checkSelfPermission(this@MainActivity, videoPermission) == PackageManager.PERMISSION_GRANTED
                    if (!granted) videoPermissionLauncher.launch(videoPermission)
                },
                isVideoPermissionGranted = isVideoPermissionGranted,
                onPlayPlaylist = playerViewModel::playPlaylist,
                onPlayVideo = playerViewModel::playVideo,
                onShuffleAllSongs = playerViewModel::shuffleAllSongs,
                onRescanLibrary = playerViewModel::rescanLibrary,
                songSort = homeState.songSort,
                albumSortDirection = homeState.albumSortDirection,
                artistSortDirection = homeState.artistSortDirection,
                onSongSortFieldSelected = playerViewModel::setSongSortField,
                onSongSortDirectionToggle = playerViewModel::toggleSongSortDirection,
                onAlbumSortToggle = playerViewModel::toggleAlbumSortDirection,
                onArtistSortToggle = playerViewModel::toggleArtistSortDirection,
                onToggleQueue = playerViewModel::toggleQueueVisibility,
                onDismissQueue = playerViewModel::dismissQueue,
                onRemoveFromQueue = playerViewModel::removeFromQueue,
                onPlayNext = {
                    val c = mediaController
                    if (c != null && c.mediaItemCount > 1 && MediaControllerAdapters.hasNextMediaItem(c)) MediaControllerAdapters.seekToNextMediaItem(c)
                    else playerViewModel.playNext()
                },
                onPlayPrevious = {
                    val c = mediaController
                    if (c != null && c.mediaItemCount > 1 && MediaControllerAdapters.hasPreviousMediaItem(c)) MediaControllerAdapters.seekToPreviousMediaItem(c)
                    else playerViewModel.playPrevious()
                },
                onAddBookmark = playerViewModel::addLongformBookmarkHere,
                onResumeBookmark = playerViewModel::resumeFromLongformBookmark,
                onClearBookmark = playerViewModel::clearLongformBookmark,
                onSelectLongformFilter = playerViewModel::selectLongformFilter,
                onClearLongformBookmarkFor = playerViewModel::clearLongformBookmarkFor,
                onOnboardingWelcomeContinue = playerViewModel::continueFromWelcome,
                onRequestAudioPermission = {
                    playerViewModel.onPermissionRequestLaunched()
                    val permission = audioPermission
                    if (permission == null) {
                        playerViewModel.handlePermissionResult(granted = true)
                    } else {
                        permissionLauncher.launch(permission)
                    }
                },
                onChooseFolders = {
                    playerViewModel.onPermissionRequestLaunched()
                    folderLauncher.launch(null)
                },
                safFolders = safFolders,
                onRemoveSafFolder = playerViewModel::removeSafFolder,
                onAssignAllLongform = playerViewModel::assignAllLongform,
                onChooseLongformIndividually = playerViewModel::chooseLongformIndividually,
                onLongformSelectionChange = playerViewModel::updateLongformSelection,
                onApplyLongformSelection = playerViewModel::applyLongformSelections,
                onSkipLongform = playerViewModel::skipLongformClassification,
                onUndoLongformChange = playerViewModel::undoLongformClassification,
                onConsumeOnboardingMessage = playerViewModel::consumeOnboardingStatusMessage,
                onCompleteOnboarding = playerViewModel::completeOnboarding,
                exoPlayer = playerViewModel.exoPlayerOrNull(),
                isShuffleEnabled = shuffleEnabled,
                repeatMode = repeatUi,
                globalStatusMessage = globalMessage,
                onConsumeGlobalStatusMessage = playerViewModel::consumeGlobalStatusMessage,
                onOpenPlaylistDetail = playerViewModel::openPlaylistDetail,
                onClosePlaylistDetail = playerViewModel::closePlaylistDetail,
                onImportPlaylist = { importLauncher.launch(arrayOf("audio/x-mpegurl","application/vnd.apple.mpegurl","text/plain","*/*")) },
                onMovePlaylistItemUp = playerViewModel::movePlaylistItemUp,
                onMovePlaylistItemDown = playerViewModel::movePlaylistItemDown,
                onRemoveFromUserPlaylist = playerViewModel::removeFromUserPlaylist,
                onExportUserPlaylist = { id ->
                    pendingExportId.value = id
                    exportLauncher.launch("playlist.m3u")
                },
                onVideoSortToggle = playerViewModel::toggleVideoSortDirection,
                onVideoSortFieldSelected = playerViewModel::setVideoSortField,
                onToggleRearmOnBootEnabled = playerViewModel::setRearmOnBootEnabled,
                onSelectRearmMinMinutes = playerViewModel::setRearmMinMinutes,
                onToggleSkipSilenceEnabled = playerViewModel::setSkipSilenceEnabled,
                onSelectCrossfadeMs = playerViewModel::setCrossfadeMs,
                onToggleUseHaptics = playerViewModel::setUseHaptics,
                onSelectLongformThresholdMinutes = playerViewModel::setLongformThresholdMinutes
            )
        }

        // Start foreground playback service; its MediaSession becomes available shortly.
        PlaybackService.start(this)
        mediaSession = PlaybackEngine.session
        buildControllerWhenSessionReady()
    }

    // ---------- ActivityResult helpers -----------------------------------------------------------

    @Composable
    private fun rememberLauncherForPermission(
        onResult: (Boolean) -> Unit
    ): ActivityResultLauncher<String> =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = onResult
        )

    @Composable
    private fun rememberLauncherForFolder(
        onResult: (Uri?) -> Unit
    ): ActivityResultLauncher<Uri?> =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree(),
            onResult = onResult
        )

    @Composable
    private fun rememberLauncherForPlaylist(
        onResult: (Uri?) -> Unit
    ): ActivityResultLauncher<Array<String>> =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
            onResult = onResult
        )

    @Composable
    private fun rememberLauncherForCreateM3U(
        onResult: (Uri?) -> Unit
    ): ActivityResultLauncher<String> =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/x-mpegURL"),
            onResult = onResult
        )

    // ---------- Share helpers --------------------------------------------------------------------

    private fun shareCurrentSong() {
        val message = playerViewModel.getCurrentSongShareMessage()
        if (message != null) {
            val stream = playerViewModel.getCurrentSongShareUri()
            shareSongMessage(message, stream)
        } else {
            showShareUnavailable()
        }
    }

    private fun shareSpecificSong(songId: String) {
        val message = playerViewModel.getShareMessageForSong(songId)
        if (message != null) {
            val stream = playerViewModel.getShareUriForSong(songId)
            shareSongMessage(message, stream)
        } else {
            showShareUnavailable()
        }
    }

    private fun shareSongMessage(message: SongShareMessage, stream: Uri?) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            if (stream != null) {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, stream)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                clipData = ClipData.newRawUri("audio", stream)
                try {
                    val resInfo = packageManager.queryIntentActivities(this, PackageManager.MATCH_DEFAULT_ONLY)
                    resInfo.forEach { ri ->
                        grantUriPermission(ri.activityInfo.packageName, stream, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                } catch (_: Throwable) { }
            } else {
                type = "text/plain"
            }
            putExtra(Intent.EXTRA_SUBJECT, message.subject)
            putExtra(Intent.EXTRA_TEXT, message.text)
        }
        val chooser = Intent.createChooser(intent, getString(R.string.share_song_chooser_title))
        try {
            startActivity(chooser)
        } catch (_: ActivityNotFoundException) {
            showShareUnavailable()
        }
    }

    override fun onDestroy() {
        mediaController?.release()
        mediaController = null
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

    private fun buildControllerWhenSessionReady() {
        val existing = PlaybackEngine.session
        if (existing != null) {
            try { mediaController = MediaSessionAdapters.buildAsync(MediaController.Builder(this, existing.token)).get() } catch (_: Throwable) {}
            return
        }
        Thread {
            val deadline = System.currentTimeMillis() + 5000L
            while (System.currentTimeMillis() < deadline) {
                val session = PlaybackEngine.session
                if (session != null) {
                    try {
                        val c = MediaSessionAdapters.buildAsync(MediaController.Builder(this, session.token)).get()
                        runOnUiThread { mediaController = c }
                    } catch (_: Throwable) {}
                    break
                }
                try { Thread.sleep(100) } catch (_: InterruptedException) { break }
            }
        }.start()
    }

    private fun showShareUnavailable() {
        Toast.makeText(this, getString(R.string.share_song_error), Toast.LENGTH_SHORT).show()
    }

    // ---------- Permissions & SAF ----------------------------------------------------------------

    private fun determineAudioPermission(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            @Suppress("DEPRECATION")
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    private fun persistFolderAccess(uri: Uri) {
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            contentResolver.takePersistableUriPermission(uri, flags)
        } catch (_: SecurityException) {
            // Some providers may not allow persistable grants — ignore gracefully.
        }
    }
}
