package app.ember.studio

import android.app.Application
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import app.ember.studio.media3adapters.PlayerAdapters
import app.ember.core.ui.theme.ThemeUiState
import app.ember.studio.onboarding.OnboardingPreferencesRepository
import app.ember.studio.onboarding.onboardingPreferencesDataStore
import app.ember.studio.theme.ThemePreferencesRepository
import app.ember.studio.theme.themePreferencesDataStore
import app.ember.studio.sleep.SleepTimerCallbacks
import app.ember.studio.sleep.SleepTimerController
import app.ember.studio.sleep.SleepTimerControllerHandle
import app.ember.studio.sleep.SleepTimerEndAction
import app.ember.studio.sleep.SleepTimerDefaults
import app.ember.studio.sleep.SleepTimerPendingActionPreferences
import app.ember.studio.sleep.SleepTimerPendingActionType
import app.ember.studio.sleep.SleepTimerPreferences
import app.ember.studio.sleep.SleepTimerPreferencesRepository
import app.ember.studio.sleep.SleepTimerScheduler
import app.ember.studio.sleep.SleepTimerAlarmScheduler
import app.ember.studio.audio.VirtualizerCompat
import app.ember.studio.audio.VirtualizerHandle
import app.ember.studio.equalizer.EqualizerPreferencesRepository
import app.ember.studio.equalizer.EqualizerPreferences
import app.ember.studio.equalizer.equalizerPreferencesDataStore
import app.ember.studio.library.LibraryRepository
import app.ember.studio.library.MediaStoreLibraryRepository
import app.ember.studio.library.LibraryCacheRepository
import app.ember.studio.library.VideoLibraryRepository
import app.ember.studio.library.PlaylistLibraryRepository
import app.ember.studio.library.UserPlaylistsRepository
import app.ember.studio.library.userPlaylistsDataStore
import app.ember.studio.imports.FolderImportRepository
import app.ember.studio.imports.safFolderDataStore
import app.ember.studio.playback.PlaybackPreferencesRepository
import app.ember.studio.playback.playbackPreferencesDataStore
import app.ember.studio.playback.PlaybackQueueRepository
import app.ember.studio.playback.playbackQueuePreferencesDataStore
import app.ember.studio.tag.TagOverlay
import app.ember.studio.tag.TagOverlayRepository
import app.ember.studio.tag.tagOverlayDataStore
import app.ember.studio.sleep.SleepTimerUiState as SleepTimerState
import app.ember.studio.sleep.sleepTimerPreferencesDataStore
import app.ember.studio.util.formatDuration
import app.ember.studio.util.formatFrequencyLabel
import kotlin.math.max
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.LinkedHashSet
import kotlin.math.abs
import app.ember.studio.longform.LongformPreferencesRepository
import app.ember.studio.longform.LongformPref
import app.ember.studio.longform.longformPreferencesDataStore
class PlayerViewModel(
    application: Application,
    private val dependencies: Dependencies = Dependencies()
) : AndroidViewModel(application) {

    data class Dependencies(
        val playerFactory: ((Application) -> Player)? = null,
        val themePreferencesRepository: ThemePreferencesRepository? = null,
        val onboardingPreferencesRepository: OnboardingPreferencesRepository? = null,
        val sleepTimerPreferencesRepository: SleepTimerPreferencesRepository? = null,
        val equalizerPreferencesRepository: EqualizerPreferencesRepository? = null,
        val playbackPreferencesRepository: PlaybackPreferencesRepository? = null,
        val sleepTimerControllerFactory: ((CoroutineScope, SleepTimerCallbacks) -> SleepTimerControllerHandle)? = null,
        val sleepTimerSchedulerFactory: ((Application) -> SleepTimerScheduler)? = null,
        val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        val clock: () -> Long = System::currentTimeMillis
    )

    private val appContext = application
    private val ioDispatcher: CoroutineDispatcher = dependencies.ioDispatcher
    private val now: () -> Long = dependencies.clock

    private val player: Player =
        dependencies.playerFactory?.invoke(application) ?: ExoPlayer.Builder(application).build()

    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_AUDIO_SESSION_ID)) {
                ensureAudioEffects()
            }
            if (
                events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) ||
                events.contains(Player.EVENT_POSITION_DISCONTINUITY)
            ) {
                val mediaId = PlayerAdapters.getCurrentMediaItem(player)?.mediaId
                val idx = if (mediaId != null) queueOrder.indexOf(mediaId) else PlayerAdapters.getCurrentMediaItemIndex(player)
                if (idx >= 0) {
                    currentSongIndex = idx.coerceIn(0, (queueOrder.size - 1).coerceAtLeast(0))
                    recomputeQueueState(currentSongIndex)
                }
            }
            if (
                events.contains(Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED) ||
                events.contains(Player.EVENT_REPEAT_MODE_CHANGED)
            ) {
                val shuffle = PlayerAdapters.getShuffleModeEnabled(player)
                val repeat = PlayerAdapters.getRepeatMode(player)
                viewModelScope.launch(ioDispatcher) {
                    playbackPreferencesRepository.setShuffleEnabled(shuffle)
                    // Map to integer directly (0=OFF, 1=ONE, 2=ALL) matches ExoPlayer
                    playbackPreferencesRepository.setRepeatMode(repeat)
                }
            }
            if (
                events.contains(Player.EVENT_MEDIA_METADATA_CHANGED) ||
                events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED) ||
                events.contains(Player.EVENT_IS_PLAYING_CHANGED) ||
                events.contains(Player.EVENT_POSITION_DISCONTINUITY) ||
                events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)
            ) {
                updateState()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                handleSleepTimerPlaybackCompleted()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            // Trigger fade-in on automatic transition when crossfade is enabled
            if (crossfadeDurationMs > 0 && reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                crossfadeFadeInEndTimestampMs = now() + crossfadeDurationMs
                try { PlayerAdapters.setVolume(player, 0f) } catch (_: Throwable) {}
            }
        }
    }

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: VirtualizerHandle? = null
    private var presetReverb: PresetReverb? = null
    private var currentAudioSessionId: Int = C.AUDIO_SESSION_ID_UNSET
    private var lastEqualizerPreferences: EqualizerPreferences? = null
    private var hasRestoredQueueFromPrefs: Boolean = false
    private var tagOverlays: Map<String, TagOverlay> = emptyMap()
    // Crossfade state (engine-managed ramp around item transitions)
    @Volatile private var crossfadeDurationMs: Long = 0L
    @Volatile private var crossfadeFadeInEndTimestampMs: Long = 0L

    private var allSongs: List<SongSummary> = SampleLibrary.songs
    private var allAlbums: List<AlbumSummary> = SampleLibrary.albums
    private var allArtists: List<ArtistSummary> = SampleLibrary.artists
    private var allGenresList: List<GenreSummary> = SampleLibrary.genres
    private var allFolders: List<FolderSummary> = SampleLibrary.folders
    // Videos now load from repository; start empty until scan completes
    private var allVideos: List<VideoSummary> = emptyList()
    private var allPlaylists: List<PlaylistSummary> = emptyList()
    private var longformLibrary: List<LongformItem> = SampleLibrary.longformItems
    private var currentSongIndex: Int = allSongs.indices.firstOrNull() ?: 0
    private var currentSongId: String? = allSongs.firstOrNull()?.id

    private val _homeState = MutableStateFlow(
        HomeUiState(
            selectedTab = HomeTab.Songs,
            songs = sortSongs(allSongs, SongSortState()),
            // Start with computed playlists; real playlists will be appended after scan
            playlists = computePlaylists(sortSongs(allSongs, SongSortState())),
            folders = SampleLibrary.folders,
            albums = sortAlbums(allAlbums, SortDirection.Ascending),
            artists = sortArtists(allArtists, SortDirection.Ascending),
            genres = SampleLibrary.genres,
            audiobooks = longformLibrary.filter { it.category == LongformCategory.Audiobook },
            podcasts = longformLibrary.filter { it.category == LongformCategory.Podcast },
            longformFilter = LongformFilter.All,
            // Videos populate after repository scan; start empty
            videos = emptyList(),
            drawerDestinations = defaultDrawerDestinations(),
            songSort = SongSortState(),
            albumSortDirection = SortDirection.Ascending,
            artistSortDirection = SortDirection.Ascending
        )
    )
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    private var queueOrder: MutableList<String> = _homeState.value.songs.map { it.id }.toMutableList()

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val _equalizerState = MutableStateFlow(
        EqualizerUiState(reverbOptions = buildReverbOptions())
    )
    val equalizerState: StateFlow<EqualizerUiState> = _equalizerState.asStateFlow()

    private val _lyricsState = MutableStateFlow(LyricsUiState())
    val lyricsState: StateFlow<LyricsUiState> = _lyricsState.asStateFlow()

    private val themePreferencesRepository =
        dependencies.themePreferencesRepository ?: ThemePreferencesRepository(appContext.themePreferencesDataStore)

    private val sleepTimerPreferencesRepository =
        dependencies.sleepTimerPreferencesRepository ?: SleepTimerPreferencesRepository(appContext.sleepTimerPreferencesDataStore)

    private val onboardingPreferencesRepository =
        dependencies.onboardingPreferencesRepository ?: OnboardingPreferencesRepository(appContext.onboardingPreferencesDataStore)

    private val equalizerPreferencesRepository =
        dependencies.equalizerPreferencesRepository ?: EqualizerPreferencesRepository(appContext.equalizerPreferencesDataStore)

    private val playbackPreferencesRepository =
        dependencies.playbackPreferencesRepository ?: PlaybackPreferencesRepository(appContext.playbackPreferencesDataStore)

    private val playbackQueueRepository =
        PlaybackQueueRepository(appContext.playbackQueuePreferencesDataStore)
    private val tagOverlayRepository =
        TagOverlayRepository(appContext.tagOverlayDataStore)
    private val longformPreferencesRepository =
        LongformPreferencesRepository(appContext.longformPreferencesDataStore)

    private val _themeState = MutableStateFlow(ThemeUiState())
    val themeState: StateFlow<ThemeUiState> = _themeState.asStateFlow()
    // SAF folders (persisted tree URIs managed by the user)
    private val _safFolders = MutableStateFlow<List<String>>(emptyList())
    val safFolders: StateFlow<List<String>> = _safFolders.asStateFlow()
    private var longformPrefs: Map<String, LongformPref> = emptyMap()
    private val sleepTimerCallbacks = object : SleepTimerCallbacks {
        override val currentVolume: Float
            get() = PlayerAdapters.getVolume(player)

        override fun setVolume(volume: Float) {
            PlayerAdapters.setVolume(player, volume.coerceIn(0f, 1f))
        }

        override fun pausePlayback() {
            clearPendingSleepTimerAction()
            PlayerAdapters.pause(player)
            showSleepTimerMessage(R.string.sleep_timer_message_completed_pause)
            persistSleepTimerPreferences()
        }

        override fun stopPlayback() {
            clearPendingSleepTimerAction()
            PlayerAdapters.stop(player)
            showSleepTimerMessage(R.string.sleep_timer_message_completed_stop)
            persistSleepTimerPreferences()
        }

        override fun stopAfterCurrentTrack() {
            showSleepTimerMessage(R.string.sleep_timer_message_completed_stop_after_track)
            requestStopAfterTrack()
            persistSleepTimerPreferences()
        }

        override fun stopAfterQueue() {
            showSleepTimerMessage(R.string.sleep_timer_message_completed_stop_after_queue)
            requestStopAfterQueue()
            persistSleepTimerPreferences()
        }
    }

    private val sleepTimerController: SleepTimerControllerHandle =
        dependencies.sleepTimerControllerFactory?.invoke(viewModelScope, sleepTimerCallbacks)
            ?: SleepTimerController(
                scope = viewModelScope,
                callbacks = sleepTimerCallbacks,
                dispatcher = Dispatchers.Main.immediate
            )
    private val sleepTimerScheduler: SleepTimerScheduler =
        dependencies.sleepTimerSchedulerFactory?.invoke(application) ?: SleepTimerAlarmScheduler(application)
    val sleepTimerState: StateFlow<SleepTimerState> = sleepTimerController.state

    // Settings (sleep timer resiliency toggle)
    data class SettingsUiState(
        val rearmOnBootEnabled: Boolean = false,
        val rearmMinMinutes: Int = 15,
        val rearmMinOptions: List<Int> = listOf(5, 10, 15, 30),
        val skipSilenceEnabled: Boolean = false,
        val crossfadeMs: Int = 0,
        val crossfadeOptions: List<Int> = listOf(0, 3000, 6000, 12000),
        val longformThresholdMinutes: Int = 20,
        val longformThresholdOptions: List<Int> = listOf(10, 15, 20, 30, 45, 60),
        val useHaptics: Boolean = true
    )
    private val _settingsState = MutableStateFlow(SettingsUiState())
    val settingsState: StateFlow<SettingsUiState> = _settingsState.asStateFlow()

    private sealed interface PendingSleepTimerAction {
        data class StopAfterTrack(val trackId: String) : PendingSleepTimerAction
        data class StopAfterQueue(
            val queueSnapshot: List<String>,
            val currentIndex: Int
        ) : PendingSleepTimerAction
    }

    private var pendingSleepTimerAction: PendingSleepTimerAction? = null
    private var lastSleepTimerPreferences: SleepTimerPreferences? = null
    @Volatile private var longformThresholdMs: Long = LONGFORM_THRESHOLD_MS

    private val _tagEditorState = MutableStateFlow(TagEditorUiState())
    val tagEditorState: StateFlow<TagEditorUiState> = _tagEditorState.asStateFlow()

    // Global, app-wide status message (shown via Snackbar in the app shell)
    private val _globalStatusMessage = MutableStateFlow<String?>(null)
    val globalStatusMessage: StateFlow<String?> = _globalStatusMessage.asStateFlow()

    private val _onboardingState = MutableStateFlow(createInitialOnboardingState())
    val onboardingState: StateFlow<OnboardingUiState> = _onboardingState.asStateFlow()

    private var hasCompletedInitialScan = false
    private val libraryRepository: LibraryRepository = MediaStoreLibraryRepository()
    private val libraryCacheRepository = LibraryCacheRepository(appContext)
    private val videoRepository = VideoLibraryRepository()
    private val playlistRepository = PlaylistLibraryRepository()
    private val folderImportRepository = FolderImportRepository(appContext.safFolderDataStore)
    private val userPlaylistsRepository = UserPlaylistsRepository(appContext.userPlaylistsDataStore)
    private var previousLongformLibrary: List<LongformItem>? = null

    private var userTitleOverride: String? = null
    private var userArtistOverride: String? = null
    private var userAlbumOverride: String? = null

    init {
        // Expose the player to the foreground service so it can host the MediaSession
        try {
            app.ember.studio.playback.PlaybackEngine.setPlayer(player)
        } catch (_: Throwable) {
            // Ignore if service classes unavailable in certain test environments
        }
        PlayerAdapters.addListener(player, listener)
        PlayerAdapters.setRepeatMode(player, Player.REPEAT_MODE_ALL)
        val firstSong = allSongs.firstOrNull()
        if (firstSong != null) {
            val idx = queueOrder.indexOf(firstSong.id).takeIf { it >= 0 } ?: 0
            currentSongIndex = idx
            syncPlayerTimelineFromQueue(startIndex = idx, play = false)
        }
        ensureAudioEffects()
        updateState()
        syncTagEditorFromPlayer()
        recomputeQueueState()
        observePositionChanges()
        observeOnboardingPreferences()
        observeThemePreferences()
        observeSleepTimerPreferences()
        observeEqualizerPreferences()
        observePlaybackPreferences()
        observePlaybackQueuePreferences()
        observeTagOverlays()
        observeUserPlaylists()
        observeLongformPreferences()
        observeSafFolders()
        // Try cache, then scan
        loadLibraryFromCache()
        scanLibraryAsync()
    }

    override fun onCleared() {
        clearPendingSleepTimerAction()
        sleepTimerController.cancelTimer()
        PlayerAdapters.removeListener(player, listener)
        PlayerAdapters.release(player)
        releaseAudioEffects()
        super.onCleared()
    }

    fun exoPlayerOrNull(): ExoPlayer? = player as? ExoPlayer

    fun selectTab(tab: HomeTab) {
        _homeState.update { it.copy(selectedTab = tab) }
    }

    fun selectDrawerDestination(destinationId: DrawerDestinationId) {
        _homeState.update { it.copy(selectedDrawerDestination = destinationId) }
    }

    fun toggleQueueVisibility() {
        _homeState.update { it.copy(isQueueVisible = !it.isQueueVisible) }
    }

    fun dismissQueue() {
        _homeState.update { it.copy(isQueueVisible = false) }
    }

    fun selectLongformFilter(filter: LongformFilter) {
        _homeState.update { it.copy(longformFilter = filter) }
    }

    fun selectSleepTimerQuickDuration(minutes: Int) {
        sleepTimerController.selectQuickDuration(minutes)
        persistSleepTimerPreferences()
    }

    fun updateSleepTimerHoursInput(hours: String) {
        sleepTimerController.updateCustomHours(hours)
        persistSleepTimerPreferences()
    }

    fun updateSleepTimerMinutesInput(minutes: String) {
        sleepTimerController.updateCustomMinutes(minutes)
        persistSleepTimerPreferences()
    }

    fun setSleepTimerFade(enabled: Boolean) {
        sleepTimerController.setFadeEnabled(enabled)
        persistSleepTimerPreferences()
    }

    fun selectSleepTimerEndAction(action: SleepTimerEndAction) {
        sleepTimerController.selectEndAction(action)
        persistSleepTimerPreferences()
    }

    fun startSleepTimer() {
        clearPendingSleepTimerAction()
        sleepTimerController.clearStatusMessage()
        sleepTimerController.startTimer()
        val configuredMillis = sleepTimerController.state.value.configuredDurationMillis
        if (configuredMillis > 0L) {
            val formatted = formatDuration(configuredMillis)
            sleepTimerController.showStatusMessage(
                appContext.getString(R.string.sleep_timer_message_started, formatted)
            )
        }
        // Schedule an OS alarm so long timers survive background/kill
        val end = sleepTimerController.state.value.scheduledEndTimestampMillis
        if (end != null) {
            sleepTimerScheduler.schedule(
                endTimestampMillis = end,
                fadeEnabled = sleepTimerController.state.value.fadeEnabled,
                endAction = sleepTimerController.state.value.endAction,
                originalVolume = sleepTimerController.state.value.originalVolume
            )
        }
        persistSleepTimerPreferences()
    }

    fun cancelSleepTimer() {
        clearPendingSleepTimerAction()
        sleepTimerController.cancelTimer()
        showSleepTimerMessage(R.string.sleep_timer_message_cancelled)
        sleepTimerScheduler.cancel()
        persistSleepTimerPreferences()
    }

    fun dismissSleepTimerStatusMessage() {
        sleepTimerController.clearStatusMessage()
        persistSleepTimerPreferences()
    }

    private fun requestStopAfterTrack() {
        clearPendingSleepTimerAction()
        val trackId = currentSongId
        if (trackId == null) {
            PlayerAdapters.stop(player)
            persistSleepTimerPreferences()
            return
        }
        pendingSleepTimerAction = PendingSleepTimerAction.StopAfterTrack(trackId)
        if (PlayerAdapters.getPlaybackState(player) == Player.STATE_ENDED) {
            handleSleepTimerPlaybackCompleted()
        }
        persistSleepTimerPreferences()
    }

    private fun requestStopAfterQueue() {
        clearPendingSleepTimerAction()
        val snapshot = queueOrder.toList()
        if (snapshot.isEmpty()) {
            PlayerAdapters.stop(player)
            persistSleepTimerPreferences()
            return
        }
        val trackId = currentSongId
        if (trackId == null) {
            PlayerAdapters.stop(player)
            persistSleepTimerPreferences()
            return
        }
        val index = snapshot.indexOf(trackId)
        if (index == -1) {
            PlayerAdapters.stop(player)
            persistSleepTimerPreferences()
            return
        }
        pendingSleepTimerAction = PendingSleepTimerAction.StopAfterQueue(
            queueSnapshot = snapshot,
            currentIndex = index
        )
        if (PlayerAdapters.getPlaybackState(player) == Player.STATE_ENDED) {
            handleSleepTimerPlaybackCompleted()
        }
        persistSleepTimerPreferences()
    }

    private fun handleSleepTimerPlaybackCompleted() {
        when (val pending = pendingSleepTimerAction) {
            is PendingSleepTimerAction.StopAfterTrack -> {
                clearPendingSleepTimerAction()
                PlayerAdapters.stop(player)
            }
            is PendingSleepTimerAction.StopAfterQueue -> {
                val snapshot = pending.queueSnapshot
                val nextIndex = pending.currentIndex + 1
                if (nextIndex >= snapshot.size) {
                    clearPendingSleepTimerAction()
                    PlayerAdapters.stop(player)
                } else {
                    val nextId = snapshot[nextIndex]
                    val nextSong = findSongById(nextId)
                    if (nextSong != null) {
                        pendingSleepTimerAction = PendingSleepTimerAction.StopAfterQueue(
                            queueSnapshot = snapshot,
                            currentIndex = nextIndex
                        )
                        val queueIndex = queueOrder.indexOf(nextId)
                        if (queueIndex >= 0) {
                            currentSongIndex = queueIndex
                        }
                        syncPlayerTimelineFromQueue(startIndex = currentSongIndex, play = true)
                    } else {
                        clearPendingSleepTimerAction()
                        PlayerAdapters.stop(player)
                    }
                }
            }
            null -> Unit
        }
        persistSleepTimerPreferences()
    }

    private fun clearPendingSleepTimerAction() {
        pendingSleepTimerAction = null
    }

    fun setSongSortField(field: SongSortField) {
        val current = homeState.value.songSort
        if (current.field == field) return
        val updated = current.copy(field = field, direction = field.defaultDirection)
        applySongSort(updated)
    }

    fun toggleSongSortDirection() {
        val current = homeState.value.songSort
        val updated = current.copy(direction = current.direction.toggled())
        applySongSort(updated)
    }

    fun toggleAlbumSortDirection() {
        val next = homeState.value.albumSortDirection.toggled()
        applyAlbumSort(next)
    }

    fun toggleArtistSortDirection() {
        val next = homeState.value.artistSortDirection.toggled()
        applyArtistSort(next)
    }

    fun playSong(songId: String) {
        val songs = homeState.value.songs
        val song = songs.firstOrNull { it.id == songId } ?: return
        ensureSongInQueue(songId, songs)
        val index = queueOrder.indexOf(songId).takeIf { it >= 0 } ?: return
        currentSongIndex = index
        syncPlayerTimelineFromQueue(startIndex = index, play = true)
    }

    fun playAllSongs() {
        val songs = homeState.value.songs
        if (songs.isEmpty()) return
        queueOrder = songs.map { it.id }.toMutableList()
        currentSongIndex = 0
        syncPlayerTimelineFromQueue(startIndex = 0, play = true)
    }

    fun shuffleAllSongs() {
        val songs = homeState.value.songs
        if (songs.isEmpty()) return
        val shuffled = songs.shuffled()
        queueOrder = shuffled.map { it.id }.toMutableList()
        currentSongIndex = 0
        syncPlayerTimelineFromQueue(startIndex = 0, play = true)
    }

    fun playPlaylist(playlistId: String) {
        when (playlistId) {
            "playlist_all_songs" -> {
                val songs = homeState.value.songs
                if (songs.isEmpty()) return
                playPlaylistInternal(songs)
            }
            "playlist_recent" -> {
                val songs = homeState.value.songs
                    .sortedByDescending { it.addedTimestampMs ?: 0L }
                    .take(25)
                if (songs.isEmpty()) return
                playPlaylistInternal(songs)
            }
            else -> {
                if (playlistId.startsWith("mspl:")) {
                    val idNum = playlistId.substringAfter(":").toLongOrNull() ?: return
                    viewModelScope.launch(ioDispatcher) {
                        val audioIds = try { playlistRepository.members(appContext, idNum) } catch (_: Throwable) { emptyList() }
                        val lookup = allSongs.associateBy { it.id }
                        val songs = audioIds.mapNotNull { aid -> lookup["media:$aid"] }
                        if (songs.isNotEmpty()) {
                            launch(Dispatchers.Main) { playPlaylistInternal(songs) }
                        }
                    }
                }
            }
        }
    }

    private fun playPlaylistInternal(songs: List<SongSummary>) {
        queueOrder = songs.map { it.id }.toMutableList()
        currentSongIndex = 0
        syncPlayerTimelineFromQueue(startIndex = 0, play = true)
    }

    fun rescanLibrary() {
        scanLibraryAsync()
    }

    fun openPlaylistDetail(playlistId: String) {
        viewModelScope.launch(ioDispatcher) {
            val (title, items) = when {
                playlistId == "playlist_all_songs" -> appContext.getString(R.string.songs_tab) to homeState.value.songs
                playlistId == "playlist_recent" -> appContext.getString(R.string.playlists_recently_added) to homeState.value.songs
                    .sortedByDescending { it.addedTimestampMs ?: 0L }
                    .take(25)
                playlistId.startsWith("mspl:") -> {
                    val idNum = playlistId.substringAfter(":").toLongOrNull()
                    val ids = if (idNum != null) try { playlistRepository.members(appContext, idNum) } catch (_: Throwable) { emptyList() } else emptyList()
                    val lookup = allSongs.associateBy { it.id }
                    val songs = ids.mapNotNull { aid -> lookup["media:$aid"] }
                    val name = allPlaylists.firstOrNull { it.id == playlistId }?.title ?: appContext.getString(R.string.playlists_tab)
                    name to songs
                }
                playlistId.startsWith("user:") -> {
                    val pl = userPlaylistsRepository.playlists.firstOrNull()?.get(playlistId)
                    val lookup = allSongs.associateBy { it.id }
                    val songs = pl?.items?.mapNotNull { id -> lookup[id] } ?: emptyList()
                    (pl?.title ?: appContext.getString(R.string.playlists_tab)) to songs
                }
                else -> appContext.getString(R.string.playlists_tab) to emptyList()
            }
            val duration = items.sumOf { max(0L, it.durationMs) }
            launch(Dispatchers.Main) {
                _homeState.update { it.copy(selectedPlaylist = PlaylistDetailUiState(playlistId, title, items, duration)) }
            }
        }
    }

    fun closePlaylistDetail() {
        _homeState.update { it.copy(selectedPlaylist = null) }
    }

    fun importPlaylistFromUri(uri: Uri) {
        viewModelScope.launch(ioDispatcher) {
            val parsed = UserPlaylistsRepository.parseM3U(appContext, uri)
            if (parsed == null) {
                launch(Dispatchers.Main) { _globalStatusMessage.value = appContext.getString(R.string.playlists_import_failed) }
                return@launch
            }

            // Build robust lookup maps
            fun normalize(s: String): String = s.trim().lowercase().replace("\\s+".toRegex(), " ")
            fun baseName(pathOrUri: String): String {
                val seg = pathOrUri.substringAfterLast('/')
                val noQuery = seg.substringBefore('?')
                return noQuery
            }
            fun baseNameNoExt(pathOrUri: String): String {
                val b = baseName(pathOrUri)
                val dot = b.lastIndexOf('.')
                return if (dot > 0) b.substring(0, dot) else b
            }

            val byId = allSongs.associateBy { it.id }
            val byUriString = allSongs.mapNotNull { it.uri?.let { u -> u.lowercase() to it } }.toMap()
            val byFileName = allSongs.mapNotNull { s -> s.uri?.let { baseName(it).lowercase() to s } }.toMap()
            val byFileStem = allSongs.mapNotNull { s -> s.uri?.let { baseNameNoExt(it).lowercase() to s } }.toMap()
            val byTitleNorm = allSongs.associateBy { normalize(it.title) }

            val matchedIds = mutableListOf<String>()
            parsed.items.forEach { rawLine ->
                val raw = rawLine.trim()
                if (raw.isEmpty()) return@forEach
                // 1) Exact id
                byId[raw]?.let { s -> matchedIds.add(s.id); return@forEach }
                val rawLower = raw.lowercase()
                // 2) Exact uri string
                byUriString[rawLower]?.let { s -> matchedIds.add(s.id); return@forEach }
                // 3) If raw is a uri/path, try file name/stem
                val name = baseName(rawLower)
                byFileName[name]?.let { s -> matchedIds.add(s.id); return@forEach }
                val stem = baseNameNoExt(rawLower)
                byFileStem[stem]?.let { s -> matchedIds.add(s.id); return@forEach }
                // 4) Fallback to title normalization (no extension)
                byTitleNorm[normalize(stem)]?.let { s -> matchedIds.add(s.id); return@forEach }
            }

            if (matchedIds.isEmpty()) {
                launch(Dispatchers.Main) { _globalStatusMessage.value = appContext.getString(R.string.playlists_import_none_matched) }
                return@launch
            }

            // Persist and open the imported playlist
            userPlaylistsRepository.addOrReplace(parsed.copy(items = matchedIds))
            val importedCount = matchedIds.size
            val totalCount = parsed.items.size
            launch(Dispatchers.Main) {
                _globalStatusMessage.value = appContext.getString(R.string.playlists_import_result, importedCount, totalCount)
                openPlaylistDetail(parsed.id)
            }
        }
    }

    fun consumeGlobalStatusMessage() {
        _globalStatusMessage.value = null
    }

    fun addToFavorites(songId: String) {
        viewModelScope.launch(ioDispatcher) {
            val favoritesId = userPlaylistsRepository.ensurePlaylist(appContext.getString(R.string.playlist_title_favorites), stableId = "user:favorites")
            userPlaylistsRepository.addItem(favoritesId, songId)
            // Refresh detail if open
            if (_homeState.value.selectedPlaylist?.id == favoritesId) {
                openPlaylistDetail(favoritesId)
            }
        }
    }

    fun addSongToUserPlaylist(playlistId: String, songId: String) {
        viewModelScope.launch(ioDispatcher) {
            userPlaylistsRepository.addItem(playlistId, songId)
            if (_homeState.value.selectedPlaylist?.id == playlistId) {
                openPlaylistDetail(playlistId)
            }
        }
    }

    fun addSongToNewUserPlaylist(title: String, songId: String) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch(ioDispatcher) {
            val id = userPlaylistsRepository.ensurePlaylist(trimmed)
            userPlaylistsRepository.addItem(id, songId)
            // Optionally open the new playlist
            openPlaylistDetail(id)
        }
    }

    fun movePlaylistItemUp(playlistId: String, itemId: String) {
        if (!playlistId.startsWith("user:")) return
        val current = _homeState.value.selectedPlaylist ?: return
        if (current.id != playlistId) return
        val idx = current.items.indexOfFirst { it.id == itemId }
        if (idx <= 0) return
        val swapped = current.items.toMutableList().apply { add(idx - 1, removeAt(idx)) }
        _homeState.update { it.copy(selectedPlaylist = current.copy(items = swapped)) }
        viewModelScope.launch(ioDispatcher) {
            userPlaylistsRepository.updateItems(playlistId, swapped.map { it.id })
        }
    }

    fun movePlaylistItemDown(playlistId: String, itemId: String) {
        if (!playlistId.startsWith("user:")) return
        val current = _homeState.value.selectedPlaylist ?: return
        if (current.id != playlistId) return
        val idx = current.items.indexOfFirst { it.id == itemId }
        if (idx < 0 || idx >= current.items.lastIndex) return
        val swapped = current.items.toMutableList().apply { add(idx + 1, removeAt(idx)) }
        _homeState.update { it.copy(selectedPlaylist = current.copy(items = swapped)) }
        viewModelScope.launch(ioDispatcher) {
            userPlaylistsRepository.updateItems(playlistId, swapped.map { it.id })
        }
    }

    fun removeFromUserPlaylist(playlistId: String, itemId: String) {
        if (!playlistId.startsWith("user:")) return
        val current = _homeState.value.selectedPlaylist ?: return
        if (current.id != playlistId) return
        val filtered = current.items.filterNot { it.id == itemId }
        _homeState.update { it.copy(selectedPlaylist = current.copy(items = filtered)) }
        viewModelScope.launch(ioDispatcher) {
            userPlaylistsRepository.removeItem(playlistId, itemId)
        }
    }

    fun exportUserPlaylistToM3U(playlistId: String, target: Uri) {
        if (!playlistId.startsWith("user:")) return
        viewModelScope.launch(ioDispatcher) {
            val pl = userPlaylistsRepository.playlists.firstOrNull()?.get(playlistId) ?: return@launch
            val map = allSongs.associateBy { it.id }
            val lines = buildList {
                add("#EXTM3U")
                pl.items.forEach { id ->
                    val s = map[id]
                    if (s != null) {
                        val title = s.title
                        val duration = s.durationMs / 1000
                        add("#EXTINF:$duration,$title")
                        add(s.uri ?: s.id)
                    }
                }
            }
            try {
                appContext.contentResolver.openOutputStream(target, "w")?.use { out ->
                    out.writer().use { w ->
                        lines.forEach { line ->
                            w.appendLine(line)
                        }
                    }
                }
            } catch (_: Throwable) {
                // Ignore export errors for now
            }
        }
    }

    fun playNext() {
        if (queueOrder.isEmpty()) return
        val nextIndex = (currentSongIndex + 1).mod(queueOrder.size)
        currentSongIndex = nextIndex
        syncPlayerTimelineFromQueue(startIndex = nextIndex, play = true)
    }

    fun playPrevious() {
        if (queueOrder.isEmpty()) return
        val prevIndex = if (currentSongIndex - 1 < 0) queueOrder.lastIndex else currentSongIndex - 1
        currentSongIndex = prevIndex
        syncPlayerTimelineFromQueue(startIndex = prevIndex, play = true)
    }

    fun removeFromQueue(songId: String) {
        if (!queueOrder.contains(songId)) return
        val removingIndex = queueOrder.indexOf(songId)
        if (queueOrder.size == 1) {
            queueOrder.clear()
            currentSongIndex = 0
            PlayerAdapters.stop(player)
            _homeState.update { it.copy(queue = QueueUiState()) }
            return
        }

        queueOrder.removeAt(removingIndex)
        if (removingIndex <= currentSongIndex) {
            currentSongIndex = (currentSongIndex - 1).coerceAtLeast(0).coerceAtMost(queueOrder.lastIndex)
        }
        syncPlayerTimelineFromQueue(startIndex = currentSongIndex, play = PlayerAdapters.getPlayWhenReady(player))
    }

    fun togglePlayPause() {
        PlayerAdapters.setPlayWhenReady(player, !PlayerAdapters.getPlayWhenReady(player))
        persistPlaybackQueue()
    }

    fun setPlaybackSpeed(speed: Float) {
        val speeds = _uiState.value.availablePlaybackSpeeds
        val target = speeds.firstOrNull { abs(it - speed) < PLAYBACK_SPEED_TOLERANCE } ?: return
        val currentParameters = PlayerAdapters.getPlaybackParameters(player)
        PlayerAdapters.setPlaybackSpeed(player, target)
        _uiState.update { it.copy(playbackSpeed = target) }
        viewModelScope.launch(ioDispatcher) {
            playbackPreferencesRepository.setPlaybackSpeed(target)
            // Persist default speed for longform tracks
            val id = currentSongId
            val song = id?.let { sid -> allSongs.firstOrNull { it.id == sid } }
            if (song != null && song.durationMs >= longformThresholdMs) {
                longformPreferencesRepository.setDefaultSpeed(song.id, target)
            }
        }
    }

    fun cyclePlaybackSpeed() {
        val speeds = _uiState.value.availablePlaybackSpeeds
        if (speeds.isEmpty()) return
        val current = _uiState.value.playbackSpeed
        val currentIndex = speeds.indexOfFirst { abs(it - current) < PLAYBACK_SPEED_TOLERANCE }
        val nextIndex = if (currentIndex == -1) 0 else (currentIndex + 1) % speeds.size
        setPlaybackSpeed(speeds[nextIndex])
    }

    fun seekTo(positionMs: Long) {
        val duration = PlayerAdapters.getDuration(player).takeIf { it > 0 } ?: 0L
        PlayerAdapters.seekTo(player, positionMs.coerceIn(0L, duration))
        persistPlaybackQueue()
    }

    fun addLongformBookmarkHere() {
        val id = currentSongId ?: return
        val duration = PlayerAdapters.getDuration(player).takeIf { it > 0 } ?: 0L
        if (duration <= 0L) return
        val pos = PlayerAdapters.getCurrentPosition(player).coerceIn(0L, duration)
        viewModelScope.launch(ioDispatcher) { longformPreferencesRepository.setBookmark(id, pos) }
        updateState()
    }

    fun resumeFromLongformBookmark() {
        val id = currentSongId ?: return
        val bookmark = longformPrefs[id]?.bookmarkMs ?: return
        seekTo(bookmark)
        if (!PlayerAdapters.getPlayWhenReady(player)) togglePlayPause()
    }

    fun clearLongformBookmark() {
        val id = currentSongId ?: return
        viewModelScope.launch(ioDispatcher) { longformPreferencesRepository.setBookmark(id, null) }
        updateState()
    }

    fun clearLongformBookmarkFor(songId: String) {
        viewModelScope.launch(ioDispatcher) { longformPreferencesRepository.setBookmark(songId, null) }
        updateState()
    }

    private fun persistPlaybackQueue() {
        if (queueOrder.isEmpty()) return
        val index = currentSongIndex.coerceIn(0, queueOrder.lastIndex)
        val snapshot = app.ember.studio.playback.PlaybackQueuePreferences(
            queueOrder = queueOrder.toList(),
            currentIndex = index,
            nowPlayingId = queueOrder.getOrNull(index),
            positionMs = PlayerAdapters.getCurrentPosition(player).coerceAtLeast(0L),
            playWhenReady = PlayerAdapters.getPlayWhenReady(player)
        )
        viewModelScope.launch(ioDispatcher) {
            playbackQueueRepository.update(snapshot)
        }
    }

    private fun observePlaybackQueuePreferences() {
        viewModelScope.launch(ioDispatcher) {
            playbackQueueRepository.preferences.collect { prefs ->
                if (hasRestoredQueueFromPrefs) return@collect
                if (prefs.queueOrder.isEmpty() || prefs.nowPlayingId.isNullOrEmpty()) return@collect
                hasRestoredQueueFromPrefs = true
                launch(Dispatchers.Main.immediate) {
                    val songsById = homeState.value.songs.associateBy { it.id }
                    val order = prefs.queueOrder.filter { songsById.containsKey(it) }
                    if (order.isEmpty()) return@launch
                    queueOrder = order.toMutableList()
                    val targetIndex = prefs.currentIndex.coerceIn(0, queueOrder.lastIndex)
                    currentSongIndex = targetIndex
                    val targetId = queueOrder[targetIndex]
                    syncPlayerTimelineFromQueue(startIndex = targetIndex, play = prefs.playWhenReady)
                    val duration = PlayerAdapters.getDuration(player).takeIf { it > 0 } ?: 0L
                    if (duration > 0L) {
                        PlayerAdapters.seekTo(player, prefs.positionMs.coerceIn(0L, duration))
                    }
                }
            }
        }
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        ensureAudioEffects()
        val eq = equalizer ?: return
        eq.enabled = enabled
        _equalizerState.update { it.copy(isEnabled = enabled, isAvailable = true) }
        viewModelScope.launch(ioDispatcher) {
            equalizerPreferencesRepository.setEqualizerEnabled(enabled)
        }
    }

    fun setBandLevel(bandIndex: Int, levelMillibels: Int) {
        ensureAudioEffects()
        val eq = equalizer ?: return
        val range = eq.bandLevelRange
        val clamped = levelMillibels.coerceIn(range[0].toInt(), range[1].toInt())
        eq.setBandLevel(bandIndex.toShort(), clamped.toShort())
        refreshEqualizerState(selectedPresetOverride = -1) { state ->
            state.copy(
                bands = state.bands.updateBandLevel(bandIndex, clamped),
                selectedPresetIndex = -1
            )
        }
        val levels = _equalizerState.value.bands.map { it.levelMillibels }
        viewModelScope.launch(ioDispatcher) {
            equalizerPreferencesRepository.setBandLevels(levels)
            equalizerPreferencesRepository.setPresetIndex(-1)
        }
    }

    fun selectPreset(index: Int) {
        ensureAudioEffects()
        val eq = equalizer ?: return
        if (index !in 0 until eq.numberOfPresets) return
        try {
            eq.usePreset(index.toShort())
            eq.enabled = true
            refreshEqualizerState(selectedPresetOverride = index)
            val levels = _equalizerState.value.bands.map { it.levelMillibels }
            viewModelScope.launch(ioDispatcher) {
                equalizerPreferencesRepository.setPresetIndex(index)
                equalizerPreferencesRepository.setBandLevels(levels)
            }
        } catch (_: IllegalArgumentException) {
            // Ignore invalid preset errors.
        }
    }

    fun setBassBoostStrength(strength: Int) {
        ensureAudioEffects()
        val boost = bassBoost ?: return
        val clamped = strength.coerceIn(0, MAX_EFFECT_STRENGTH)
        boost.enabled = clamped > 0
        if (boost.strengthSupported) {
            boost.setStrength(clamped.toShort())
        }
        _equalizerState.update {
            it.copy(
                isBassBoostSupported = boost.strengthSupported,
                bassBoostStrength = clamped
            )
        }
        viewModelScope.launch(ioDispatcher) {
            equalizerPreferencesRepository.setBassBoostStrength(clamped)
        }
    }

    fun setVirtualizerStrength(strength: Int) {
        ensureAudioEffects()
        val effect = virtualizer ?: return
        val clamped = strength.coerceIn(0, MAX_EFFECT_STRENGTH)
        effect.isEnabled = clamped > 0
        effect.setStrength(clamped)
        _equalizerState.update {
            it.copy(
                isVirtualizerAvailable = true,
                isVirtualizerSupported = effect.strengthSupported,
                virtualizerStrength = clamped,
                isVirtualizationEnabled = clamped > 0
            )
        }
        viewModelScope.launch(ioDispatcher) {
            equalizerPreferencesRepository.setVirtualizerStrength(clamped)
        }
    }

    fun setReverbPreset(preset: Short) {
        ensureAudioEffects()
        val reverb = presetReverb ?: return
        reverb.preset = preset
        reverb.enabled = preset != PresetReverb.PRESET_NONE
        _equalizerState.update { it.copy(selectedReverbPreset = preset) }
        viewModelScope.launch(ioDispatcher) {
            equalizerPreferencesRepository.setReverbPreset(preset.toInt())
        }
    }

    fun resetEqualizer() {
        ensureAudioEffects()
        val eq = equalizer ?: return
        val bands = 0 until eq.numberOfBands
        bands.forEach { index ->
            eq.setBandLevel(index.toShort(), 0)
        }
        refreshEqualizerState(selectedPresetOverride = -1) { state ->
            state.copy(
                bands = state.bands.map { band -> band.copy(levelMillibels = 0) },
                selectedPresetIndex = -1
            )
        }
        viewModelScope.launch(ioDispatcher) {
            equalizerPreferencesRepository.setPresetIndex(-1)
            equalizerPreferencesRepository.setBandLevels(_equalizerState.value.bands.map { it.levelMillibels })
            equalizerPreferencesRepository.setBassBoostStrength(0)
            equalizerPreferencesRepository.setReverbPreset(PresetReverb.PRESET_NONE.toInt())
        }
    }

    fun updateLyricsDraft(text: String) {
        _lyricsState.update { it.copy(draftLyrics = text, statusMessage = null) }
    }

    fun saveLyrics() {
        _lyricsState.update {
            it.copy(
                currentLyrics = it.draftLyrics,
                statusMessage = appContext.getString(R.string.lyrics_saved_message)
            )
        }
    }

    fun clearLyrics() {
        _lyricsState.update {
            it.copy(
                currentLyrics = "",
                draftLyrics = "",
                statusMessage = appContext.getString(R.string.lyrics_cleared_message)
            )
        }
    }

    fun loadSampleLyrics() {
        val sample = appContext.getString(R.string.sample_lyrics_body)
        _lyricsState.update {
            it.copy(
                draftLyrics = sample,
                statusMessage = appContext.getString(R.string.lyrics_sample_loaded_message)
            )
        }
    }

    fun selectThemeOption(index: Int) {
        var persistedSelection: Int? = null
        _themeState.update { state ->
            val updated = state.withSelectedOption(index)
            if (updated !== state) {
                persistedSelection = updated.selectedOptionIndex
            }
            updated
        }
        persistedSelection?.let { selection ->
            viewModelScope.launch(ioDispatcher) {
                themePreferencesRepository.setSelectedThemeOption(selection)
            }
        }
    }

    fun setDarkTheme(useDarkTheme: Boolean) {
        var shouldPersist = false
        _themeState.update { state ->
            val updated = state.withDarkTheme(useDarkTheme)
            if (updated !== state) {
                shouldPersist = true
            }
            updated
        }
        if (shouldPersist) {
            viewModelScope.launch(ioDispatcher) {
                themePreferencesRepository.setDarkTheme(useDarkTheme)
            }
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        var shouldPersist = false
        _themeState.update { state ->
            val updated = state.withDynamicColor(enabled)
            if (updated !== state) shouldPersist = true
            updated
        }
        if (shouldPersist) {
            viewModelScope.launch(ioDispatcher) {
                themePreferencesRepository.setDynamicColor(enabled)
            }
        }
    }

    fun setAmoledBlack(enabled: Boolean) {
        var shouldPersist = false
        _themeState.update { state ->
            val updated = state.withAmoledBlack(enabled)
            if (updated !== state) shouldPersist = true
            updated
        }
        if (shouldPersist) {
            viewModelScope.launch(ioDispatcher) {
                themePreferencesRepository.setAmoledBlack(enabled)
            }
        }
    }

    private fun observeOnboardingPreferences() {
        viewModelScope.launch(ioDispatcher) {
            onboardingPreferencesRepository.preferences.collect { preferences ->
                _onboardingState.update { state ->
                    if (preferences.isComplete) {
                        state.copy(isVisible = false, step = OnboardingStep.Complete, statusMessage = null)
                    } else {
                        // Show onboarding if not complete
                        val step = if (state.step == OnboardingStep.Complete) {
                            OnboardingStep.Welcome
                        } else {
                            state.step
                        }
                        state.copy(isVisible = true, step = step)
                    }
                }
            }
        }
    }

    private fun observeThemePreferences() {
        viewModelScope.launch(ioDispatcher) {
            themePreferencesRepository.themePreferences.collect { preferences ->
                _themeState.update { state ->
                    state
                        .withSelectedOption(preferences.selectedOptionIndex)
                        .withDarkTheme(preferences.useDarkTheme)
                        .withDynamicColor(preferences.useDynamicColor)
                        .withAmoledBlack(preferences.useAmoledBlack)
                }
            }
        }
    }

    private fun observeSleepTimerPreferences() {
        viewModelScope.launch(ioDispatcher) {
            sleepTimerPreferencesRepository.preferences.collect { preferences ->
                lastSleepTimerPreferences = preferences
                sleepTimerController.restoreFromPreferences(preferences)
                restoreSleepTimerStatus(preferences.statusMessage)
                restorePendingSleepTimerAction(preferences.pendingAction)
                restoreActiveSleepTimer(preferences)
                _settingsState.update {
                    it.copy(
                        rearmOnBootEnabled = preferences.rearmOnBootEnabled,
                        rearmMinMinutes = preferences.rearmMinMinutes
                    )
                }
            }
        }
    }

    private fun observeEqualizerPreferences() {
        viewModelScope.launch(ioDispatcher) {
            equalizerPreferencesRepository.preferences.collect { prefs ->
                lastEqualizerPreferences = prefs
                launch(Dispatchers.Main.immediate) {
                    ensureAudioEffects()
                    applyEqualizerPreferencesToEffects(prefs)
                    refreshEqualizerState(
                        selectedPresetOverride = prefs.selectedPresetIndex.takeIf { it >= 0 }
                    )
                }
            }
        }
    }

    private fun observePlaybackPreferences() {
        viewModelScope.launch(ioDispatcher) {
            playbackPreferencesRepository.preferences.collect { prefs ->
                launch(Dispatchers.Main.immediate) {
                    val speeds = _uiState.value.availablePlaybackSpeeds
                    val target = speeds.firstOrNull { abs(it - prefs.playbackSpeed) < PLAYBACK_SPEED_TOLERANCE }
                        ?: 1.0f
                    val currentParameters = PlayerAdapters.getPlaybackParameters(player)
                    if (abs(currentParameters.speed - target) >= PLAYBACK_SPEED_TOLERANCE) {
                        PlayerAdapters.setPlaybackSpeed(player, target)
                    }
                    _uiState.update { it.copy(playbackSpeed = target) }
                    // Restore shuffle and repeat
                    if (PlayerAdapters.getShuffleModeEnabled(player) != prefs.shuffleEnabled) {
                        PlayerAdapters.setShuffleModeEnabled(player, prefs.shuffleEnabled)
                    }
                    val desiredRepeat = when (prefs.repeatMode) {
                        Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ONE
                        Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ALL
                        else -> Player.REPEAT_MODE_OFF
                    }
                    if (PlayerAdapters.getRepeatMode(player) != desiredRepeat) {
                        PlayerAdapters.setRepeatMode(player, desiredRepeat)
                    }
                    // Apply skip silence (ExoPlayer-specific)
                    (player as? ExoPlayer)?.let { exo ->
                        if (PlayerAdapters.getSkipSilenceEnabled(exo) != prefs.skipSilenceEnabled) {
                            PlayerAdapters.setSkipSilenceEnabled(exo, prefs.skipSilenceEnabled)
                        }
                    }
                    longformThresholdMs = (prefs.longformThresholdMinutes.coerceIn(5, 120) * 60_000L)
                    _settingsState.update { current ->
                        current.copy(
                            skipSilenceEnabled = prefs.skipSilenceEnabled,
                            crossfadeMs = prefs.crossfadeMs,
                            longformThresholdMinutes = prefs.longformThresholdMinutes,
                            useHaptics = prefs.useHapticsEnabled
                        )
                    }
                    // Update engine crossfade duration; restore volume if disabled
                    crossfadeDurationMs = prefs.crossfadeMs.coerceIn(0, 12_000).toLong()
                    if (crossfadeDurationMs <= 0L && !isSleepTimerFadeActive()) {
                        try { PlayerAdapters.setVolume(player, 1f) } catch (_: Throwable) {}
                    }
                }
            }
        }
    }

    private fun persistSleepTimerPreferences() {
        val snapshot = sleepTimerController.state.value
        val pendingPreferences = when (val pending = pendingSleepTimerAction) {
            is PendingSleepTimerAction.StopAfterTrack -> SleepTimerPendingActionPreferences(
                type = SleepTimerPendingActionType.StopAfterTrack,
                trackId = pending.trackId
            )
            is PendingSleepTimerAction.StopAfterQueue -> SleepTimerPendingActionPreferences(
                type = SleepTimerPendingActionType.StopAfterQueue,
                queueSnapshot = pending.queueSnapshot,
                queueIndex = pending.currentIndex
            )
            null -> SleepTimerPendingActionPreferences()
        }
        val preferences = SleepTimerPreferences(
            selectedQuickDurationMinutes = snapshot.selectedQuickDurationMinutes,
            customHoursInput = snapshot.customHoursInput,
            customMinutesInput = snapshot.customMinutesInput,
            fadeEnabled = snapshot.fadeEnabled,
            endAction = snapshot.endAction,
            activeEndTimestampMillis = snapshot.scheduledEndTimestampMillis?.takeIf { snapshot.isRunning },
            activeFadeEnabled = if (snapshot.isRunning) snapshot.fadeEnabled else false,
            activeEndAction = snapshot.endAction.takeIf { snapshot.isRunning },
            activeOriginalVolume = snapshot.originalVolume?.takeIf { snapshot.isRunning },
            pendingAction = pendingPreferences,
            statusMessage = snapshot.statusMessage
        )
        viewModelScope.launch(ioDispatcher) {
            sleepTimerPreferencesRepository.updatePreferences(preferences)
        }
    }

    private fun showSleepTimerMessage(@StringRes messageRes: Int) {
        sleepTimerController.showStatusMessage(appContext.getString(messageRes))
    }

    private fun restoreSleepTimerStatus(message: String?) {
        if (message != null) {
            sleepTimerController.showStatusMessage(message)
        } else {
            sleepTimerController.clearStatusMessage()
        }
    }

    private fun restorePendingSleepTimerAction(preferences: SleepTimerPendingActionPreferences) {
        pendingSleepTimerAction = when (preferences.type) {
            SleepTimerPendingActionType.None -> null
            SleepTimerPendingActionType.StopAfterTrack ->
                preferences.trackId?.let { PendingSleepTimerAction.StopAfterTrack(it) }
            SleepTimerPendingActionType.StopAfterQueue ->
                if (preferences.queueSnapshot.isNotEmpty()) {
                    val clampedIndex = preferences.queueIndex.coerceIn(
                        0,
                        preferences.queueSnapshot.lastIndex.coerceAtLeast(0)
                    )
                    PendingSleepTimerAction.StopAfterQueue(
                        queueSnapshot = preferences.queueSnapshot,
                        currentIndex = clampedIndex
                    )
                } else {
                    null
                }
        }
    }

    private fun restoreActiveSleepTimer(preferences: SleepTimerPreferences) {
        val activeEnd = preferences.activeEndTimestampMillis
        val activeAction = preferences.activeEndAction
        if (activeEnd == null || activeAction == null) {
            return
        }
        val controllerState = sleepTimerController.state.value
        if (controllerState.isRunning && controllerState.scheduledEndTimestampMillis == activeEnd) {
            return
        }
        val remaining = activeEnd - now()
        val originalVolume = preferences.activeOriginalVolume?.coerceIn(0f, 1f)
        if (remaining > 0L) {
            sleepTimerController.resumeTimer(
                endTimestampMillis = activeEnd,
                fadeEnabled = preferences.activeFadeEnabled,
                endAction = activeAction,
                originalVolume = originalVolume
            )
            sleepTimerScheduler.schedule(
                endTimestampMillis = activeEnd,
                fadeEnabled = preferences.activeFadeEnabled,
                endAction = activeAction,
                originalVolume = originalVolume
            )
            persistSleepTimerPreferences()
        } else {
            restorePlayerVolume(originalVolume)
            handleExpiredSleepTimer(activeAction)
            sleepTimerScheduler.cancel()
            persistSleepTimerPreferences()
        }
    }

    private fun restorePlayerVolume(volume: Float?) {
        if (volume != null) {
            PlayerAdapters.setVolume(player, volume.coerceIn(0f, 1f))
        }
    }

    private fun handleExpiredSleepTimer(action: SleepTimerEndAction) {
        when (action) {
            SleepTimerEndAction.PausePlayback -> {
                clearPendingSleepTimerAction()
                PlayerAdapters.pause(player)
                showSleepTimerMessage(R.string.sleep_timer_message_completed_pause)
            }
            SleepTimerEndAction.StopPlayback -> {
                clearPendingSleepTimerAction()
                PlayerAdapters.stop(player)
                showSleepTimerMessage(R.string.sleep_timer_message_completed_stop)
            }
            SleepTimerEndAction.StopAfterTrack -> {
                showSleepTimerMessage(R.string.sleep_timer_message_completed_stop_after_track)
                requestStopAfterTrack()
            }
            SleepTimerEndAction.StopAfterQueue -> {
                showSleepTimerMessage(R.string.sleep_timer_message_completed_stop_after_queue)
                requestStopAfterQueue()
            }
        }
    }

    fun setRearmOnBootEnabled(enabled: Boolean) {
        val base = lastSleepTimerPreferences ?: SleepTimerPreferences()
        val updated = base.copy(rearmOnBootEnabled = enabled)
        viewModelScope.launch(ioDispatcher) {
            sleepTimerPreferencesRepository.updatePreferences(updated)
        }
        _settingsState.update { it.copy(rearmOnBootEnabled = enabled) }
    }

    fun setRearmMinMinutes(minutes: Int) {
        val clamped = minutes.coerceIn(1, 240)
        val base = lastSleepTimerPreferences ?: SleepTimerPreferences()
        val updated = base.copy(rearmMinMinutes = clamped)
        viewModelScope.launch(ioDispatcher) {
            sleepTimerPreferencesRepository.updatePreferences(updated)
        }
        _settingsState.update { it.copy(rearmMinMinutes = clamped) }
    }

    fun setSkipSilenceEnabled(enabled: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            playbackPreferencesRepository.setSkipSilenceEnabled(enabled)
        }
        // Player will be updated by observer; we also update UI immediately
        _settingsState.update { it.copy(skipSilenceEnabled = enabled) }
    }

    fun setUseHaptics(enabled: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            try { playbackPreferencesRepository.setUseHapticsEnabled(enabled) } catch (_: Throwable) {}
        }
        _settingsState.update { it.copy(useHaptics = enabled) }
    }

    fun setCrossfadeMs(ms: Int) {
        val clamped = ms.coerceIn(0, 12_000)
        viewModelScope.launch(ioDispatcher) {
            playbackPreferencesRepository.setCrossfadeMs(clamped)
        }
        _settingsState.update { it.copy(crossfadeMs = clamped) }
    }

    fun setLongformThresholdMinutes(minutes: Int) {
        val clamped = minutes.coerceIn(5, 120)
        viewModelScope.launch(ioDispatcher) {
            playbackPreferencesRepository.setLongformThresholdMinutes(clamped)
        }
        longformThresholdMs = clamped * 60_000L
        _settingsState.update { it.copy(longformThresholdMinutes = clamped) }
    }

    fun updateTagTitle(value: String) {
        _tagEditorState.update { state ->
            val trimmed = value
            state.copy(
                titleInput = trimmed,
                isDirty = isTagDirty(trimmed, state.artistInput, state.albumInput),
                statusMessage = null
            )
        }
    }

    fun updateTagArtist(value: String) {
        _tagEditorState.update { state ->
            val trimmed = value
            state.copy(
                artistInput = trimmed,
                isDirty = isTagDirty(state.titleInput, trimmed, state.albumInput),
                statusMessage = null
            )
        }
    }

    fun updateTagAlbum(value: String) {
        _tagEditorState.update { state ->
            val trimmed = value
            state.copy(
                albumInput = trimmed,
                isDirty = isTagDirty(state.titleInput, state.artistInput, trimmed),
                statusMessage = null
            )
        }
    }

    fun saveTags() {
        val currentEditor = _tagEditorState.value
        val title = currentEditor.titleInput.ifBlank { appContext.getString(R.string.sample_tone_title) }
        val artist = currentEditor.artistInput.ifBlank { appContext.getString(R.string.sample_tone_artist) }
        val album = currentEditor.albumInput.ifBlank { appContext.getString(R.string.sample_album_title) }
        // Persist overlay for current song id
        currentSongId?.let { id ->
            viewModelScope.launch(ioDispatcher) {
                tagOverlayRepository.setOverlay(id, app.ember.studio.tag.TagOverlay(title = title, artist = artist, album = album))
            }
        }
        userTitleOverride = null
        userArtistOverride = null
        userAlbumOverride = null
        _uiState.update {
            it.copy(
                title = title,
                artist = artist,
                album = album
            )
        }
        _tagEditorState.update {
            it.copy(
                titleInput = title,
                artistInput = artist,
                albumInput = album,
                isDirty = false,
                statusMessage = appContext.getString(R.string.tag_editor_saved_message)
            )
        }
    }

    fun resetTagEditor() {
        val current = _uiState.value
        _tagEditorState.update {
            it.copy(
                titleInput = current.title,
                artistInput = current.artist,
                albumInput = current.album,
                isDirty = false,
                statusMessage = null
            )
        }
    }

    fun getShareMessageForSong(songId: String): SongShareMessage? {
        val song = allSongs.find { it.id == songId } ?: return null
        return buildShareMessage(
            title = song.title,
            artist = song.artist.ifBlank { appContext.getString(R.string.sample_tone_artist) }
        )
    }

    fun getCurrentSongShareMessage(): SongShareMessage? {
        val currentId = _uiState.value.currentSongId ?: return null
        val stateTitle = _uiState.value.title
        val stateArtist = _uiState.value.artist
        val fallback = allSongs.find { it.id == currentId }
        val title = stateTitle.ifBlank { fallback?.title ?: return null }
        val artist = stateArtist.ifBlank { fallback?.artist ?: appContext.getString(R.string.sample_tone_artist) }
        return buildShareMessage(title = title, artist = artist)
    }

    fun getShareUriForSong(songId: String): Uri? {
        val song = allSongs.find { it.id == songId } ?: return null
        // Prefer explicit content URI if present on the model
        song.uri?.let { s -> return try { Uri.parse(s) } catch (_: Throwable) { null } }
        // Fallback to MediaStore content URI derived from id when using MediaStore-backed ids
        return contentUriFromIdString(song.id)?.let { Uri.parse(it) }
    }

    fun getCurrentSongShareUri(): Uri? {
        val currentId = _uiState.value.currentSongId ?: return null
        return getShareUriForSong(currentId)
    }

    private fun buildShareMessage(title: String, artist: String): SongShareMessage {
        val subject = appContext.getString(R.string.share_song_subject, title)
        val text = appContext.getString(R.string.share_song_text, title, artist)
        return SongShareMessage(subject = subject, text = text)
    }

    fun continueFromWelcome() {
        _onboardingState.update { state ->
            if (!state.isVisible || state.step != OnboardingStep.Welcome) {
                state
            } else {
                state.copy(step = OnboardingStep.Permission)
            }
        }
    }

    fun onPermissionRequestLaunched() {
        _onboardingState.update { state ->
            state.copy(
                permissionState = state.permissionState.copy(errorMessage = null)
            )
        }
    }

    fun onFolderPickerOpened() {
        _onboardingState.update { state ->
            state.copy(
                permissionState = state.permissionState.copy(shouldOpenFolderPicker = false)
            )
        }
    }

    fun onInitialPermissionGranted() {
        triggerInitialScan()
    }

    fun handlePermissionResult(granted: Boolean) {
        if (granted) {
            triggerInitialScan()
        } else {
            _onboardingState.update { state ->
                state.copy(
                    permissionState = state.permissionState.copy(
                        errorMessage = appContext.getString(R.string.onboarding_permission_error),
                        isPermissionGranted = false,
                        shouldOpenFolderPicker = true // Auto-open folder picker when permission denied
                    )
                )
            }
        }
    }

    fun onFolderSelected(uri: Uri) {
        // Persist selection and trigger scan
        viewModelScope.launch(ioDispatcher) {
            runCatching { folderImportRepository.addFolder(uri) }
        }
        _onboardingState.update { state ->
            state.copy(
                permissionState = state.permissionState.copy(
                    isPermissionGranted = true,
                    selectedFolderCount = state.permissionState.selectedFolderCount + 1,
                    errorMessage = null,
                    shouldOpenFolderPicker = false // Clear the flag when folder is selected
                )
            )
        }
        triggerInitialScan()
    }

    fun onFolderSelectionCancelled() {
        if (!_onboardingState.value.permissionState.isPermissionGranted) {
            _onboardingState.update { state ->
                state.copy(
                    permissionState = state.permissionState.copy(
                        errorMessage = appContext.getString(R.string.onboarding_permission_error)
                    )
                )
            }
        }
    }

    fun assignAllLongform(category: LongformCategory) {
        val candidates = _onboardingState.value.longformState.candidates
        if (candidates.isEmpty()) {
            proceedToThemeStep()
            return
        }
        val selection = candidates.associate { it.id to category }
        val messageRes = when (category) {
            LongformCategory.Podcast -> R.string.onboarding_long_audio_sorted_podcasts
            LongformCategory.Audiobook -> R.string.onboarding_long_audio_sorted_audiobooks
            LongformCategory.Unassigned -> R.string.onboarding_long_audio_sorted_music
        }
        applyLongformSelection(selection, messageRes)
    }

    fun chooseLongformIndividually() {
        _onboardingState.update { state ->
            if (state.longformState.candidates.isEmpty()) {
                state
            } else {
                state.copy(longformState = state.longformState.copy(mode = LongformMode.Chooser))
            }
        }
    }

    fun updateLongformSelection(itemId: String, category: LongformCategory) {
        _onboardingState.update { state ->
            val updatedCandidates = state.longformState.candidates.map { candidate ->
                if (candidate.id == itemId) {
                    candidate.copy(selectedCategory = category)
                } else {
                    candidate
                }
            }
            state.copy(longformState = state.longformState.copy(candidates = updatedCandidates))
        }
    }

    fun applyLongformSelections() {
        val state = _onboardingState.value
        val selection = state.longformState.candidates.associate { it.id to it.selectedCategory }
        applyLongformSelection(selection, R.string.onboarding_long_audio_applied)
    }

    fun skipLongformClassification() {
        proceedToThemeStep()
    }

    fun undoLongformClassification() {
        val snapshot = previousLongformLibrary ?: return
        longformLibrary = snapshot
        previousLongformLibrary = null
        _homeState.update { state ->
            state.copy(
                audiobooks = longformLibrary.filter { it.category == LongformCategory.Audiobook },
                podcasts = longformLibrary.filter { it.category == LongformCategory.Podcast }
            )
        }
        _onboardingState.update { state ->
            state.copy(
                statusMessage = appContext.getString(R.string.onboarding_long_audio_undo_message),
                canUndoLongformChange = false
            )
        }
    }

    fun consumeOnboardingStatusMessage() {
        if (_onboardingState.value.statusMessage != null) {
            _onboardingState.update { state -> state.copy(statusMessage = null) }
        }
    }

    fun completeOnboarding() {
        _onboardingState.update { state ->
            state.copy(isVisible = false, step = OnboardingStep.Complete, statusMessage = null)
        }
        viewModelScope.launch(ioDispatcher) {
            onboardingPreferencesRepository.setOnboardingComplete(true)
        }
    }

    fun resetOnboarding() {
        _onboardingState.update { state ->
            state.copy(isVisible = true, step = OnboardingStep.Welcome, statusMessage = null)
        }
        viewModelScope.launch(ioDispatcher) {
            onboardingPreferencesRepository.setOnboardingComplete(false)
        }
    }
    
    // Debug function to test onboarding state
    fun debugOnboardingState() {
        viewModelScope.launch(ioDispatcher) {
            val currentPrefs = onboardingPreferencesRepository.preferences.firstOrNull()
            println("DEBUG: Onboarding preferences - isComplete: ${currentPrefs?.isComplete}")
            println("DEBUG: Onboarding state - isVisible: ${_onboardingState.value.isVisible}, step: ${_onboardingState.value.step}")
        }
    }
    
    fun forceShowOnboarding() {
        _onboardingState.update { state ->
            state.copy(isVisible = true, step = OnboardingStep.Welcome, statusMessage = null)
        }
    }
    
    fun forceHideOnboarding() {
        _onboardingState.update { state ->
            state.copy(isVisible = false, step = OnboardingStep.Complete, statusMessage = null)
        }
    }

    private fun prepareSong(song: SongSummary, autoPlay: Boolean) {
        val mediaItem = song.toMediaItem(appContext)
        PlayerAdapters.setMediaItem(player, mediaItem)
        PlayerAdapters.prepare(player)
        PlayerAdapters.setPlayWhenReady(player, autoPlay)
        currentSongId = song.id
        userTitleOverride = song.title
        userArtistOverride = song.artist
        userAlbumOverride = song.album
        longformPrefs[song.id]?.defaultSpeed?.let { speed ->
            val currentParameters = PlayerAdapters.getPlaybackParameters(player)
            if (kotlin.math.abs(currentParameters.speed - speed) >= PLAYBACK_SPEED_TOLERANCE) {
                PlayerAdapters.setPlaybackSpeed(player, speed)
            }
        }
        updateState()
        syncTagEditorFromPlayer()
        persistPlaybackQueue()
    }

    private fun buildMediaItemsForQueue(): List<MediaItem> {
        val byId = homeState.value.songs.associateBy { it.id }
        val items = mutableListOf<MediaItem>()
        val filtered = mutableListOf<String>()
        queueOrder.forEach { id ->
            val s = byId[id]
            if (s != null) {
                items += s.toMediaItem(appContext)
                filtered += id
            }
        }
        if (filtered.size != queueOrder.size) {
            queueOrder = filtered
        }
        return items
    }

    private fun syncPlayerTimelineFromQueue(startIndex: Int = currentSongIndex, play: Boolean) {
        val items = buildMediaItemsForQueue()
        if (items.isEmpty()) {
            PlayerAdapters.stop(player)
            return
        }
        val idx = startIndex.coerceIn(0, items.lastIndex)
        PlayerAdapters.setMediaItems(player, items, idx, C.TIME_UNSET)
        PlayerAdapters.prepare(player)
        PlayerAdapters.setPlayWhenReady(player, play)
        currentSongId = queueOrder.getOrNull(idx)
        recomputeQueueState(idx)
        persistPlaybackQueue()
    }

    private fun triggerInitialScan() {
        if (hasCompletedInitialScan) {
            advanceBeyondPermission()
            return
        }
        hasCompletedInitialScan = true
        viewModelScope.launch {
            _onboardingState.update { state ->
                state.copy(
                    permissionState = state.permissionState.copy(
                        isPermissionGranted = true,
                        errorMessage = null,
                        isScanning = true,
                        totalItemCount = allSongs.size,
                        scannedItemCount = 0
                    )
                )
            }
            allSongs.forEachIndexed { index, _ ->
                delay(SCAN_PROGRESS_DELAY_MS)
                _onboardingState.update { state ->
                    state.copy(
                        permissionState = state.permissionState.copy(
                            scannedItemCount = index + 1
                        )
                    )
                }
            }
            _homeState.update { state ->
                state.copy(
                    // Replace SampleLibrary playlists/videos with repository-backed data
                    playlists = computePlaylists(sortSongs(allSongs, state.songSort)),
                    folders = SampleLibrary.folders,
                    genres = SampleLibrary.genres,
                    audiobooks = longformLibrary.filter { it.category == LongformCategory.Audiobook },
                    podcasts = longformLibrary.filter { it.category == LongformCategory.Podcast },
                    // Keep current videos (initially empty); a later scan populates from repository
                    videos = sortVideos(allVideos, state.videoSortDirection)
                )
            }
            applySongSort(homeState.value.songSort)
            applyAlbumSort(homeState.value.albumSortDirection)
            applyArtistSort(homeState.value.artistSortDirection)
            _onboardingState.update { state ->
                state.copy(
                    permissionState = state.permissionState.copy(
                        isScanning = false,
                        scannedItemCount = allSongs.size
                    )
                )
            }
            advanceBeyondPermission()
        }
    }

    private fun advanceBeyondPermission() {
        val candidates = computeLongformCandidates(longformLibrary)
        _onboardingState.update { state ->
            val updatedLongform = state.longformState.copy(
                candidates = candidates,
                mode = LongformMode.Overview
            )
            val nextStep = if (candidates.isEmpty()) OnboardingStep.Theme else OnboardingStep.LongAudio
            state.copy(
                permissionState = state.permissionState.copy(
                    isPermissionGranted = true,
                    isScanning = false,
                    errorMessage = null,
                    totalItemCount = allSongs.size,
                    scannedItemCount = allSongs.size
                ),
                longformState = updatedLongform,
                step = nextStep
            )
        }
    }

    private fun applyLongformSelection(
        selection: Map<String, LongformCategory>,
        @StringRes messageRes: Int
    ) {
        if (selection.isEmpty()) {
            proceedToThemeStep()
            return
        }
        previousLongformLibrary = longformLibrary
        val updatedLibrary = longformLibrary.map { item ->
            val newCategory = selection[item.id] ?: item.category
            if (newCategory == item.category) {
                item
            } else {
                item.copy(category = newCategory)
            }
        }
        longformLibrary = updatedLibrary
        _homeState.update { state ->
            state.copy(
                audiobooks = updatedLibrary.filter { it.category == LongformCategory.Audiobook },
                podcasts = updatedLibrary.filter { it.category == LongformCategory.Podcast }
            )
        }
        _onboardingState.update { state ->
            state.copy(
                longformState = state.longformState.copy(
                    candidates = state.longformState.candidates.map { candidate ->
                        val updatedCategory = selection[candidate.id] ?: candidate.selectedCategory
                        candidate.copy(selectedCategory = updatedCategory)
                    },
                    mode = LongformMode.Overview
                ),
                statusMessage = appContext.getString(messageRes),
                canUndoLongformChange = true,
                step = OnboardingStep.Theme
            )
        }
    }

    private fun proceedToThemeStep() {
        previousLongformLibrary = null
        _onboardingState.update { state ->
            state.copy(
                longformState = state.longformState.copy(mode = LongformMode.Overview),
                statusMessage = null,
                canUndoLongformChange = false,
                step = OnboardingStep.Theme
            )
        }
    }

    private fun applySongSort(sortState: SongSortState) {
        val sortedSongs = sortSongs(allSongs, sortState)
        rebuildQueueFromSongs(sortedSongs)
        _homeState.update {
            it.copy(
                songs = sortedSongs,
                songSort = sortState
            )
        }
        recomputeQueueState(currentSongIndex)
    }

    private fun applyAlbumSort(direction: SortDirection) {
        val sorted = sortAlbums(allAlbums, direction)
        _homeState.update {
            it.copy(
                albums = sorted,
                albumSortDirection = direction
            )
        }
    }

    private fun applyArtistSort(direction: SortDirection) {
        val sorted = sortArtists(allArtists, direction)
        _homeState.update {
            it.copy(
                artists = sorted,
                artistSortDirection = direction
            )
        }
    }

    private fun recomputeQueueState(indexOverride: Int? = null) {
        _homeState.update { state ->
            if (queueOrder.isEmpty()) {
                currentSongId = null
                currentSongIndex = 0
                state.copy(queue = QueueUiState())
            } else {
                val songsById = state.songs.associateBy { it.id }
                val index = indexOverride ?: currentSongIndex.coerceIn(0, queueOrder.lastIndex)
                currentSongIndex = index
                val nowPlayingId = queueOrder.getOrNull(index)
                val nowPlaying = nowPlayingId?.let { songsById[it] }
                currentSongId = nowPlaying?.id
                val history = queueOrder.take(index).mapNotNull { songsById[it] }
                val upNext = queueOrder.drop(index + 1).mapNotNull { songsById[it] }
                if (nowPlaying == null) {
                    state.copy(queue = QueueUiState())
                } else {
                    state.copy(
                        queue = QueueUiState(
                            history = history,
                            nowPlaying = nowPlaying,
                            upNext = upNext
                        )
                    )
                }
            }
        }
        persistPlaybackQueue()
    }

    private fun rebuildQueueFromSongs(songs: List<SongSummary>) {
        val validIds = songs.map { it.id }
        val validIdSet = validIds.toSet()
        val orderedIds = LinkedHashSet<String>()
        queueOrder.forEach { id ->
            if (id in validIdSet) {
                orderedIds += id
            }
        }
        validIds.forEach { id -> orderedIds += id }
        val newOrder = orderedIds.toMutableList()
        if (newOrder.isEmpty()) {
            newOrder.addAll(validIds)
        }
        queueOrder = newOrder
        val currentId = currentSongId
        currentSongIndex = when {
            currentId != null && queueOrder.contains(currentId) -> queueOrder.indexOf(currentId)
            queueOrder.isNotEmpty() -> currentSongIndex.coerceIn(0, queueOrder.lastIndex)
            else -> 0
        }
        if (queueOrder.isNotEmpty()) {
            currentSongId = queueOrder[currentSongIndex]
        } else {
            currentSongId = null
        }
        persistPlaybackQueue()
    }

    private fun ensureSongInQueue(songId: String, songs: List<SongSummary>) {
        if (queueOrder.contains(songId)) return
        val precedingIds = songs.takeWhile { it.id != songId }.map { it.id }
        val insertAfter = precedingIds.asReversed().firstOrNull { queueOrder.contains(it) }
        val insertionIndex = if (insertAfter != null) {
            queueOrder.indexOf(insertAfter) + 1
        } else {
            0
        }
        queueOrder.add(insertionIndex, songId)
    }

    private fun findSongById(songId: String): SongSummary? {
        return homeState.value.songs.firstOrNull { it.id == songId }
    }

    private fun observePositionChanges() {
        viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                updateState()
                applyCrossfadeIfNeeded()
                delay(if (PlayerAdapters.getPlayWhenReady(player) && PlayerAdapters.isPlaying(player)) POSITION_UPDATE_INTERVAL_PLAYING_MS else POSITION_UPDATE_INTERVAL_PAUSED_MS)
            }
        }
    }

    private fun updateState() {
        _uiState.update { current ->
            val metadata = PlayerAdapters.getMediaMetadata(player)
            val baseTitle = metadata.title?.toString() ?: appContext.getString(R.string.sample_tone_title)
            val baseArtist = metadata.artist?.toString() ?: appContext.getString(R.string.sample_tone_artist)
            val baseAlbum = metadata.albumTitle?.toString() ?: appContext.getString(R.string.sample_album_title)
            val overlay = currentSongId?.let { id -> tagOverlays[id] }
            val title = overlay?.title ?: userTitleOverride ?: baseTitle
            val artist = overlay?.artist ?: userArtistOverride ?: baseArtist
            val album = overlay?.album ?: userAlbumOverride ?: baseAlbum
            val bookmark = currentSongId?.let { id -> longformPrefs[id]?.bookmarkMs }
            current.copy(
                currentSongId = currentSongId,
                title = title,
                artist = artist,
                album = album,
                isPlaying = PlayerAdapters.getPlayWhenReady(player) && PlayerAdapters.getPlaybackState(player) == Player.STATE_READY,
                durationMs = PlayerAdapters.getDuration(player).takeIf { it > 0 } ?: 0L,
                positionMs = PlayerAdapters.getCurrentPosition(player).coerceIn(0L, PlayerAdapters.getDuration(player).takeIf { it > 0 } ?: 0L),
                playbackSpeed = PlayerAdapters.getPlaybackParameters(player).speed,
                bookmarkPositionMs = bookmark
            )
        }
        syncTagEditorFromPlayer()
    }

    private fun isSleepTimerFadeActive(): Boolean {
        val st = sleepTimerController.state.value
        return st.isRunning && st.fadeEnabled && st.remainingMillis in 1..SleepTimerDefaults.fadeDurationMillis
    }

    private fun applyCrossfadeIfNeeded() {
        val ms = crossfadeDurationMs
        if (ms <= 0L) return
        if (isSleepTimerFadeActive()) return

        val nowMs = now()
        // Fade-in after automatic transition
        val fadeInEnd = crossfadeFadeInEndTimestampMs
        if (fadeInEnd > nowMs) {
            val remaining = (fadeInEnd - nowMs).coerceAtMost(ms)
            val progress = 1f - (remaining.toFloat() / ms)
            val vol = progress.coerceIn(0f, 1f)
            try { PlayerAdapters.setVolume(player, vol) } catch (_: Throwable) {}
            return
        }

        // Fade-out near end of the current item
        val duration = PlayerAdapters.getDuration(player)
        val position = PlayerAdapters.getCurrentPosition(player)
        if (duration > 0 && position >= 0) {
            val remaining = (duration - position).coerceAtLeast(0L)
            if (remaining in 0..ms) {
                val vol = (remaining.toFloat() / ms).coerceIn(0f, 1f)
                try { PlayerAdapters.setVolume(player, vol) } catch (_: Throwable) {}
            } else if (PlayerAdapters.getVolume(player) < 1f) {
                try { PlayerAdapters.setVolume(player, 1f) } catch (_: Throwable) {}
            }
        }
    }

    private fun observeTagOverlays() {
        viewModelScope.launch(ioDispatcher) {
            tagOverlayRepository.overlays.collect { map ->
                tagOverlays = map
                launch(Dispatchers.Main.immediate) { updateState() }
            }
        }
    }

    private fun observeUserPlaylists() {
        viewModelScope.launch(ioDispatcher) {
            userPlaylistsRepository.playlists.collect { lists ->
                val userSummaries = lists.values.map { pl ->
                    PlaylistSummary(
                        id = pl.id,
                        title = pl.title,
                        trackCount = pl.items.size,
                        totalDurationMs = pl.items.mapNotNull { id -> allSongs.firstOrNull { it.id == id }?.durationMs }.sum()
                    )
                }
                _homeState.update { state ->
                    val computed = computePlaylists(state.songs)
                    state.copy(playlists = computed + allPlaylists + userSummaries)
                }
            }
        }
    }

    private fun observeLongformPreferences() {
        viewModelScope.launch(ioDispatcher) {
            longformPreferencesRepository.preferences.collect { prefs ->
                longformPrefs = prefs
                launch(Dispatchers.Main.immediate) {
                    updateState()
                    val bookmarks = prefs.mapNotNull { (id, p) ->
                        val b = p.bookmarkMs
                        if (b != null && b > 0L) id to b else null
                    }.toMap()
                    _homeState.update { it.copy(longformBookmarks = bookmarks) }
                }
            }
        }
    }

    private fun observeSafFolders() {
        viewModelScope.launch(ioDispatcher) {
            folderImportRepository.folders.collect { uris ->
                val list = uris.map { it.toString() }
                launch(Dispatchers.Main.immediate) { _safFolders.value = list }
            }
        }
    }

    fun removeSafFolder(uriString: String) {
        viewModelScope.launch(ioDispatcher) {
            runCatching { folderImportRepository.removeFolder(android.net.Uri.parse(uriString)) }
            // Trigger a rescan after removal so lists stay in sync
            scanLibraryAsync()
        }
    }

    private fun ensureAudioEffects() {
        val sessionId = (player as? ExoPlayer)?.let { PlayerAdapters.getAudioSessionId(it) } ?: C.AUDIO_SESSION_ID_UNSET
        if (sessionId == C.AUDIO_SESSION_ID_UNSET) {
            return
        }
        if (sessionId == currentAudioSessionId && equalizer != null) {
            refreshEqualizerState()
            return
        }
        releaseAudioEffects()
        currentAudioSessionId = sessionId
        try {
            equalizer = Equalizer(0, sessionId).apply { enabled = true }
            bassBoost = BassBoost(0, sessionId).apply { enabled = false }
            virtualizer = VirtualizerCompat.create(appContext, sessionId)
            presetReverb = PresetReverb(0, sessionId).apply { enabled = false }
            lastEqualizerPreferences?.let { prefs -> applyEqualizerPreferencesToEffects(prefs) }
            refreshEqualizerState()
        } catch (_: RuntimeException) {
            releaseAudioEffects()
            _equalizerState.value = EqualizerUiState(
                isAvailable = false,
                reverbOptions = buildReverbOptions()
            )
        } catch (_: IllegalArgumentException) {
            releaseAudioEffects()
            _equalizerState.value = EqualizerUiState(
                isAvailable = false,
                reverbOptions = buildReverbOptions()
            )
        }
    }

    private fun applyEqualizerPreferencesToEffects(prefs: EqualizerPreferences) {
        val eq = equalizer
        if (eq != null) {
            eq.enabled = prefs.isEnabled
            if (prefs.selectedPresetIndex in 0 until eq.numberOfPresets) {
                try { eq.usePreset(prefs.selectedPresetIndex.toShort()) } catch (_: IllegalArgumentException) {}
            } else if (prefs.bandLevels.isNotEmpty()) {
                val count = minOf(eq.numberOfBands.toInt(), prefs.bandLevels.size)
                for (i in 0 until count) {
                    val level = prefs.bandLevels[i]
                    eq.setBandLevel(i.toShort(), level.toShort())
                }
            }
        }
        bassBoost?.let { boost ->
            val clamped = prefs.bassBoostStrength.coerceIn(0, MAX_EFFECT_STRENGTH)
            boost.enabled = clamped > 0
            if (boost.strengthSupported) boost.setStrength(clamped.toShort())
        }
        virtualizer?.let { virt ->
            val clamped = prefs.virtualizerStrength.coerceIn(0, MAX_EFFECT_STRENGTH)
            virt.isEnabled = clamped > 0
            virt.setStrength(clamped)
        }
        presetReverb?.let { rev ->
            val preset = prefs.reverbPreset.toShort()
            rev.preset = preset
            rev.enabled = preset != PresetReverb.PRESET_NONE
        }
    }

    private fun refreshEqualizerState(
        selectedPresetOverride: Int? = null,
        transform: ((EqualizerUiState) -> EqualizerUiState)? = null
    ) {
        val eq = equalizer ?: run {
            _equalizerState.value = EqualizerUiState(
                isAvailable = false,
                reverbOptions = buildReverbOptions()
            )
            return
        }
        val presets = (0 until eq.numberOfPresets).map { index ->
            eq.getPresetName(index.toShort())
        }
        val selectedPresetIndex = selectedPresetOverride
            ?: _equalizerState.value.selectedPresetIndex.takeIf { it in presets.indices } ?: -1
        val bandRange = eq.bandLevelRange
        val bands = (0 until eq.numberOfBands).map { index ->
            val centerFrequency = eq.getCenterFreq(index.toShort()).toInt()
            EqualizerBandState(
                index = index,
                label = formatFrequencyLabel(centerFrequency),
                levelMillibels = eq.getBandLevel(index.toShort()).toInt(),
                minLevelMillibels = bandRange[0].toInt(),
                maxLevelMillibels = bandRange[1].toInt()
            )
        }
        val bass = bassBoost
        val virt = virtualizer
        val reverb = presetReverb
        var newState = EqualizerUiState(
            isAvailable = true,
            isEnabled = eq.enabled,
            bands = bands,
            presets = presets,
            selectedPresetIndex = selectedPresetIndex,
            isBassBoostSupported = bass?.strengthSupported == true,
            bassBoostStrength = bass?.roundedStrength?.toInt() ?: 0,
            isVirtualizerAvailable = virt != null,
            isVirtualizerSupported = virt?.strengthSupported == true,
            virtualizerStrength = virt?.roundedStrength ?: 0,
            isVirtualizationEnabled = virt?.isEnabled == true,
            reverbOptions = buildReverbOptions(),
            selectedReverbPreset = reverb?.preset ?: PresetReverb.PRESET_NONE
        )
        if (transform != null) {
            newState = transform(newState)
        }
        _equalizerState.value = newState
    }

    private fun releaseAudioEffects() {
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
        presetReverb?.release()
        equalizer = null
        bassBoost = null
        virtualizer = null
        presetReverb = null
        currentAudioSessionId = C.AUDIO_SESSION_ID_UNSET
        _equalizerState.value = EqualizerUiState(
            isAvailable = false,
            isEnabled = false,
            isVirtualizerAvailable = false,
            isVirtualizerSupported = false,
            isVirtualizationEnabled = false,
            reverbOptions = buildReverbOptions()
        )
    }

    private fun syncTagEditorFromPlayer() {
        val current = _uiState.value
        _tagEditorState.update { state ->
            if (state.isDirty) {
                state
            } else {
                state.copy(
                    titleInput = current.title,
                    artistInput = current.artist,
                    albumInput = current.album
                )
            }
        }
    }

    private fun isTagDirty(title: String, artist: String, album: String): Boolean {
        val current = _uiState.value
        return current.title != title || current.artist != artist || current.album != album
    }

    private fun buildReverbOptions(): List<ReverbOption> = listOf(
        ReverbOption(PresetReverb.PRESET_NONE, appContext.getString(R.string.reverb_none)),
        ReverbOption(PresetReverb.PRESET_SMALLROOM, appContext.getString(R.string.reverb_small_room)),
        ReverbOption(PresetReverb.PRESET_MEDIUMROOM, appContext.getString(R.string.reverb_medium_room)),
        ReverbOption(PresetReverb.PRESET_LARGEROOM, appContext.getString(R.string.reverb_large_room)),
        ReverbOption(PresetReverb.PRESET_MEDIUMHALL, appContext.getString(R.string.reverb_medium_hall)),
        ReverbOption(PresetReverb.PRESET_LARGEHALL, appContext.getString(R.string.reverb_large_hall)),
        ReverbOption(PresetReverb.PRESET_PLATE, appContext.getString(R.string.reverb_plate))
    )

    private fun SongSummary.toMediaItem(application: Application): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setAlbumTitle(album)
            .build()
        val builder = MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(metadata)
        val resolvedUri: Uri? = when {
            rawResId != null -> Uri.parse("rawresource://${application.packageName}/$rawResId")
            uri != null -> Uri.parse(uri)
            else -> null
        }
        resolvedUri?.let { builder.setUri(it) }
        return builder.build()
    }

    private fun createInitialOnboardingState(): OnboardingUiState {
        val candidates = computeLongformCandidates(longformLibrary)
        return OnboardingUiState(
            isVisible = false, // Start hidden, will be shown if onboarding is not complete
            step = OnboardingStep.Welcome,
            permissionState = PermissionStepState(totalItemCount = allSongs.size),
            longformState = LongformStepState(candidates = candidates)
        )
    }

    private fun computeLongformCandidates(items: List<LongformItem>): List<LongformCandidate> {
        val threshold = longformThresholdMs
        return items.filter { it.durationMs >= threshold }
            .map { item ->
                val suggested = suggestLongformCategory(
                    title = item.title,
                    source = item.source,
                    existingCategory = item.category,
                    durationMs = item.durationMs
                )
                val initial = when {
                    item.category != LongformCategory.Unassigned -> item.category
                    suggested != LongformCategory.Unassigned -> suggested
                    else -> LongformCategory.Unassigned
                }
                LongformCandidate(
                    id = item.id,
                    title = item.title,
                    durationMs = item.durationMs,
                    source = item.source,
                    suggestedCategory = suggested,
                    selectedCategory = initial
                )
            }
    }

    private fun loadLibraryFromCache() {
        val cached = libraryCacheRepository.load() ?: return
        if (cached.songs.isEmpty()) return
        val songs = cached.songs.map { item ->
            SongSummary(
                id = item.id,
                title = item.title,
                artist = item.artist,
                album = item.album,
                durationMs = item.durationMs,
                rawResId = null,
                uri = cachedItemToContentUri(item.id)
            )
        }
        allSongs = songs
        allAlbums = songs
            .map { it.album to it.artist }
            .distinct()
            .mapIndexed { idx, (title, artist) -> AlbumSummary(id = "album_$idx", title = title, artist = artist, trackCount = songs.count { s -> s.album == title }) }
        allArtists = songs
            .map { it.artist }
            .distinct()
            .mapIndexed { idx, name -> ArtistSummary(id = "artist_$idx", name = name, albumCount = allAlbums.count { it.artist == name }, trackCount = songs.count { it.artist == name }) }
        _homeState.update { state ->
            val sortedSongs = sortSongs(allSongs, state.songSort)
            state.copy(
                songs = sortedSongs,
                albums = sortAlbums(allAlbums, state.albumSortDirection),
                artists = sortArtists(allArtists, state.artistSortDirection),
                folders = allFolders,
                genres = allGenresList,
                playlists = computePlaylists(sortedSongs),
                        videos = sortVideos(allVideos, state.videoSortDirection),
                librarySongCount = songs.size
            )
        }
        rebuildQueueFromSongs(_homeState.value.songs)
        recomputeQueueState(currentSongIndex)
    }

    private fun scanLibraryAsync() {
        viewModelScope.launch(ioDispatcher) {
            _homeState.update { it.copy(isScanning = true, isPermissionDenied = !hasAudioPermission()) }
            val index = try { libraryRepository.scan(appContext) } catch (_: SecurityException) { null }
            if (index == null || index.songs.isEmpty()) {
                _homeState.update { it.copy(isScanning = false, isPermissionDenied = !hasAudioPermission()) }
                return@launch
            }
            libraryCacheRepository.save(index)
            val scannedSongs = index.songs.map { item ->
                SongSummary(
                    id = item.id,
                    title = item.title.ifBlank { appContext.getString(R.string.sample_tone_title) },
                    artist = item.artist.ifBlank { appContext.getString(R.string.sample_tone_artist) },
                    album = item.album.ifBlank { appContext.getString(R.string.sample_album_title) },
                    durationMs = item.durationMs.coerceAtLeast(0L),
                    rawResId = null,
                    uri = contentUriFromIdString(item.id),
                    addedTimestampMs = (item.dateAddedSec ?: 0L) * 1000L
                )
            }
            // Merge SAF-imported audio from user-selected folders (best-effort)
            val safSongs = try { folderImportRepository.scanAudio(appContext) } catch (_: Throwable) { emptyList() }
            // Deduplicate by URI if available
            val merged = buildMap {
                scannedSongs.forEach { s -> put(s.uri ?: s.id, s) }
                safSongs.forEach { s -> put(s.uri ?: s.id, s) }
            }.values.toList()
            allSongs = merged
            allAlbums = scannedSongs
                .map { it.album to it.artist }
                .distinct()
                .mapIndexed { idx, (title, artist) -> AlbumSummary(id = "album_$idx", title = title, artist = artist, trackCount = merged.count { s -> s.album == title }) }
            allArtists = merged
                .map { it.artist }
                .distinct()
                .mapIndexed { idx, name -> ArtistSummary(id = "artist_$idx", name = name, albumCount = allAlbums.count { it.artist == name }, trackCount = merged.count { it.artist == name }) }
            // Genres (best-effort): group by album name as placeholder when there is no genre column
            val genreNames = merged
                .map { it.album }
                .filter { it.isNotBlank() }
                .distinct()
            allGenresList = genreNames.mapIndexed { idx, name ->
                GenreSummary(id = "genre_$idx", name = name, trackCount = merged.count { it.album == name })
            }
            // Folders derived from bucket name or relative path
            val folderPairs = index.songs.map { item ->
                val name = item.bucketDisplayName
                    ?: item.relativePath?.trimEnd('/')?.substringAfterLast('/')
                    ?: appContext.getString(R.string.folders_tab)
                name
            }
            val folderNames = folderPairs.distinct()
            allFolders = folderNames.mapIndexed { idx, name ->
                FolderSummary(id = "folder_$idx", name = name, path = name, trackCount = merged.count { it.album == name || it.uri?.contains(name, ignoreCase = true) == true })
            }

            // Refresh repository-backed videos and playlists (best-effort)
            try { allVideos = videoRepository.scan(appContext) } catch (_: Throwable) { allVideos = emptyList() }
            try { allPlaylists = playlistRepository.scan(appContext) } catch (_: Throwable) { allPlaylists = emptyList() }

            launch(Dispatchers.Main) {
                // Best-effort videos list already fetched; compute sorted for UI
                val videos = sortVideos(allVideos, homeState.value.videoSortDirection)
                _homeState.update { state ->
                    val sortedSongs = sortSongs(allSongs, state.songSort)
                    state.copy(
                        songs = sortedSongs,
                        albums = sortAlbums(allAlbums, state.albumSortDirection),
                        artists = sortArtists(allArtists, state.artistSortDirection),
                        folders = allFolders,
                        genres = allGenresList,
                        playlists = computePlaylists(sortedSongs) + allPlaylists,
                        videos = videos,
                        isScanning = false,
                        lastScanTimestampMs = System.currentTimeMillis(),
                        librarySongCount = allSongs.size,
                        isPermissionDenied = false
                    )
                }
                rebuildQueueFromSongs(_homeState.value.songs)
                recomputeQueueState(currentSongIndex)
            }
        }
    }

    private fun contentUriFromIdString(idString: String): String? {
        // idString expected format: "media:<numericId>"
        val parts = idString.split(":")
        val idNum = parts.getOrNull(1)?.toLongOrNull() ?: return null
        val base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val uri = android.content.ContentUris.withAppendedId(base, idNum)
        return uri.toString()
    }

    private fun cachedItemToContentUri(idString: String): String? = contentUriFromIdString(idString)

    private fun videoContentUriFromIdString(idString: String): String? {
        val parts = idString.split(":")
        val idNum = parts.getOrNull(1)?.toLongOrNull() ?: return null
        val base = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val uri = android.content.ContentUris.withAppendedId(base, idNum)
        return uri.toString()
    }

    private fun hasAudioPermission(): Boolean {
        val perm = if (android.os.Build.VERSION.SDK_INT >= 33) {
            android.Manifest.permission.READ_MEDIA_AUDIO
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return androidx.core.content.ContextCompat.checkSelfPermission(appContext, perm) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun computePlaylists(songs: List<SongSummary>): List<PlaylistSummary> {
        if (songs.isEmpty()) return emptyList()
        val totalDuration = songs.sumOf { max(0L, it.durationMs) }
        val all = PlaylistSummary(
            id = "playlist_all_songs",
            title = appContext.getString(R.string.songs_tab),
            trackCount = songs.size,
            totalDurationMs = totalDuration
        )
        val sortedByAdded = songs.sortedByDescending { it.addedTimestampMs ?: 0L }
        val recent = sortedByAdded.take(25)
        val recentDuration = recent.sumOf { max(0L, it.durationMs) }
        val recentlyAdded = PlaylistSummary(
            id = "playlist_recent",
            title = appContext.getString(R.string.playlists_recently_added),
            trackCount = recent.size,
            totalDurationMs = recentDuration
        )
        return listOf(all, recentlyAdded)
    }

    private fun sortVideos(items: List<VideoSummary>, direction: SortDirection): List<VideoSummary> =
        if (direction == SortDirection.Ascending) items.sortedBy { it.durationMs } else items.sortedByDescending { it.durationMs }

    fun toggleVideoSortDirection() {
        val next = homeState.value.videoSortDirection.toggled()
        _homeState.update { it.copy(videos = sortVideos(allVideos, next), videoSortDirection = next) }
    }

    fun setVideoSortField(field: VideoSortField) {
        val direction = homeState.value.videoSortDirection
        val sorted = if (field == VideoSortField.Duration) sortVideos(allVideos, direction) else allVideos.sortedWith(
            if (direction == SortDirection.Ascending) compareBy({ it.title.lowercase() }, { it.durationMs })
            else compareByDescending<VideoSummary> { it.title.lowercase() }.thenByDescending { it.durationMs }
        )
        _homeState.update { it.copy(videos = sorted, videoSortField = field) }
    }

    fun playAllVideos() {
        val items = allVideos.mapNotNull { it.toVideoMediaItemOrNull() }
        if (items.isEmpty()) return
        syncVideoTimeline(items = items, startIndex = 0, play = true)
    }

    fun playVideo(videoId: String) {
        val index = allVideos.indexOfFirst { it.id == videoId }.takeIf { it >= 0 } ?: return
        val items = allVideos.mapNotNull { it.toVideoMediaItemOrNull() }
        if (items.isEmpty()) return
        syncVideoTimeline(items = items, startIndex = index, play = true)
    }

    private fun VideoSummary.toVideoMediaItemOrNull(): MediaItem? {
        val uri = videoContentUriFromIdString(this.id) ?: return null
        val md = MediaMetadata.Builder().setTitle(this.title).build()
        return MediaItem.Builder().setMediaId(this.id).setUri(uri).setMediaMetadata(md).build()
    }

    private fun syncVideoTimeline(items: List<MediaItem>, startIndex: Int = 0, play: Boolean) {
        if (items.isEmpty()) return
        val idx = startIndex.coerceIn(0, items.lastIndex)
        PlayerAdapters.setMediaItems(player, items, idx, C.TIME_UNSET)
        PlayerAdapters.prepare(player)
        PlayerAdapters.setPlayWhenReady(player, play)
        // Video flows do not integrate with song queue UI; clear queue view
        _homeState.update { it.copy(queue = QueueUiState()) }
    }

    companion object {
        private const val POSITION_UPDATE_INTERVAL_PLAYING_MS = 500L
        private const val POSITION_UPDATE_INTERVAL_PAUSED_MS = 1000L
        private const val MAX_EFFECT_STRENGTH = 1000
        private const val SCAN_PROGRESS_DELAY_MS = 120L
    }
}

enum class VideoSortField { Duration, Title }

internal val DEFAULT_PLAYBACK_SPEEDS = listOf(0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)

enum class HomeTab(@StringRes val titleRes: Int) {
    Songs(R.string.songs_tab),
    Playlists(R.string.playlists_tab),
    Folders(R.string.folders_tab),
    Albums(R.string.albums_tab),
    Artists(R.string.artists_tab),
    Genres(R.string.genres_tab),
    Audiobooks(R.string.audiobooks_tab),
    Podcasts(R.string.podcasts_tab),
    Videos(R.string.videos_tab)
}

data class HomeUiState(
    val selectedTab: HomeTab = HomeTab.Songs,
    val songs: List<SongSummary> = emptyList(),
    val playlists: List<PlaylistSummary> = emptyList(),
    val folders: List<FolderSummary> = emptyList(),
    val albums: List<AlbumSummary> = emptyList(),
    val artists: List<ArtistSummary> = emptyList(),
    val genres: List<GenreSummary> = emptyList(),
    val audiobooks: List<LongformItem> = emptyList(),
    val podcasts: List<LongformItem> = emptyList(),
    val longformFilter: LongformFilter = LongformFilter.All,
    val videos: List<VideoSummary> = emptyList(),
    val videoSortDirection: SortDirection = SortDirection.Ascending,
    val videoSortField: VideoSortField = VideoSortField.Duration,
    val drawerDestinations: List<DrawerDestination> = emptyList(),
    val selectedDrawerDestination: DrawerDestinationId = DrawerDestinationId.Library,
    val songSort: SongSortState = SongSortState(),
    val albumSortDirection: SortDirection = SortDirection.Ascending,
    val artistSortDirection: SortDirection = SortDirection.Ascending,
    val selectedPlaylist: PlaylistDetailUiState? = null,
    val queue: QueueUiState = QueueUiState(),
    val isQueueVisible: Boolean = false,
    val isScanning: Boolean = false,
    val lastScanTimestampMs: Long? = null,
    val librarySongCount: Int = 0,
    val isPermissionDenied: Boolean = false,
    val longformBookmarks: Map<String, Long> = emptyMap()
) {
    val filteredLongformItems: List<LongformItem>
        get() = when (longformFilter) {
            LongformFilter.All -> audiobooks + podcasts
            LongformFilter.Podcasts -> podcasts
            LongformFilter.Audiobooks -> audiobooks
            LongformFilter.Unassigned -> emptyList() // No longer needed with separate tabs
        }
}

data class PlaylistDetailUiState(
    val id: String,
    val title: String,
    val items: List<SongSummary>,
    val totalDurationMs: Long
)

data class QueueUiState(
    val history: List<SongSummary> = emptyList(),
    val nowPlaying: SongSummary? = null,
    val upNext: List<SongSummary> = emptyList()
)

data class PlayerUiState(
    val currentSongId: String? = null,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1f,
    val bookmarkPositionMs: Long? = null,
    val availablePlaybackSpeeds: List<Float> = DEFAULT_PLAYBACK_SPEEDS
) {
    val positionText: String get() = formatDuration(positionMs)
    val durationText: String get() = formatDuration(durationMs)
    val playbackSpeedLabel: String get() = playbackSpeed.toPlaybackSpeedLabel()
}

data class EqualizerUiState(
    val isAvailable: Boolean = false,
    val isEnabled: Boolean = false,
    val bands: List<EqualizerBandState> = emptyList(),
    val presets: List<String> = emptyList(),
    val selectedPresetIndex: Int = -1,
    val isBassBoostSupported: Boolean = false,
    val bassBoostStrength: Int = 0,
    val isVirtualizerAvailable: Boolean = false,
    val isVirtualizerSupported: Boolean = false,
    val virtualizerStrength: Int = 0,
    val isVirtualizationEnabled: Boolean = false,
    val reverbOptions: List<ReverbOption> = emptyList(),
    val selectedReverbPreset: Short = PresetReverb.PRESET_NONE
)

data class EqualizerBandState(
    val index: Int,
    val label: String,
    val levelMillibels: Int,
    val minLevelMillibels: Int,
    val maxLevelMillibels: Int
)

data class ReverbOption(
    val preset: Short,
    val label: String
)

data class LyricsUiState(
    val currentLyrics: String = "",
    val draftLyrics: String = "",
    val statusMessage: String? = null
) {
    val isSaveEnabled: Boolean get() = draftLyrics.isNotBlank()
}

data class TagEditorUiState(
    val titleInput: String = "",
    val artistInput: String = "",
    val albumInput: String = "",
    val isDirty: Boolean = false,
    val statusMessage: String? = null
) {
    val canSave: Boolean get() = isDirty
}

data class SongShareMessage(
    val subject: String,
    val text: String
)

enum class OnboardingStep {
    Welcome,
    Permission,
    LongAudio,
    Theme,
    Complete
}

data class OnboardingUiState(
    val isVisible: Boolean = true,
    val step: OnboardingStep = OnboardingStep.Welcome,
    val permissionState: PermissionStepState = PermissionStepState(),
    val longformState: LongformStepState = LongformStepState(),
    val statusMessage: String? = null,
    val canUndoLongformChange: Boolean = false
)

data class PermissionStepState(
    val isPermissionGranted: Boolean = false,
    val selectedFolderCount: Int = 0,
    val isScanning: Boolean = false,
    val scannedItemCount: Int = 0,
    val totalItemCount: Int = 0,
    val errorMessage: String? = null,
    val shouldOpenFolderPicker: Boolean = false
)

data class LongformStepState(
    val candidates: List<LongformCandidate> = emptyList(),
    val mode: LongformMode = LongformMode.Overview
) {
    val itemCount: Int get() = candidates.size
    val hasCandidates: Boolean get() = candidates.isNotEmpty()
}

enum class LongformMode {
    Overview,
    Chooser
}

data class LongformCandidate(
    val id: String,
    val title: String,
    val durationMs: Long,
    val source: String,
    val suggestedCategory: LongformCategory,
    val selectedCategory: LongformCategory
)

private fun List<EqualizerBandState>.updateBandLevel(index: Int, levelMillibels: Int): List<EqualizerBandState> =
    map { band ->
        if (band.index == index) {
            band.copy(levelMillibels = levelMillibels)
        } else {
            band
        }
    }
