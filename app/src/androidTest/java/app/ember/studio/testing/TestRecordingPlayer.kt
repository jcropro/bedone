package app.ember.studio.testing

import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.media3.common.*
import androidx.media3.common.text.CueGroup

open class TestRecordingPlayer : Player {
    private val listeners = mutableListOf<Player.Listener>()

    var stopCount: Int = 0
        private set

    private var internalVolume: Float = 1f
    private var internalPlayWhenReady: Boolean = false
    private var internalPlaybackParameters: PlaybackParameters = PlaybackParameters.DEFAULT
    private var internalPlaybackState: Int = Player.STATE_READY
    private var internalRepeatMode: Int = Player.REPEAT_MODE_ALL
    private var internalCurrentPosition: Long = 0L
    private var internalDuration: Long = 0L
    private var internalMediaMetadata: MediaMetadata = MediaMetadata.EMPTY

    fun emitPlaybackEnded() {
        internalPlaybackState = Player.STATE_ENDED
        listeners.forEach { it.onPlaybackStateChanged(Player.STATE_ENDED) }
    }

    // Listener
    override fun addListener(listener: Player.Listener) { listeners += listener }
    override fun removeListener(listener: Player.Listener) { listeners -= listener }

    // Basic playback state
    override fun getPlaybackState(): Int = internalPlaybackState
    override fun getPlayWhenReady(): Boolean = internalPlayWhenReady
    override fun setPlayWhenReady(playWhenReady: Boolean) { internalPlayWhenReady = playWhenReady }
    override fun getPlaybackParameters(): PlaybackParameters = internalPlaybackParameters
    override fun setPlaybackParameters(playbackParameters: PlaybackParameters) { internalPlaybackParameters = playbackParameters }
    override fun isPlaying(): Boolean = internalPlayWhenReady && internalPlaybackState == Player.STATE_READY
    override fun getRepeatMode(): Int = internalRepeatMode
    override fun setRepeatMode(repeatMode: Int) { internalRepeatMode = repeatMode }

    // Timeline / position
    override fun getCurrentPosition(): Long = internalCurrentPosition
    override fun getDuration(): Long = internalDuration
    override fun seekTo(positionMs: Long) { internalCurrentPosition = positionMs }
    override fun seekTo(windowIndex: Int, positionMs: Long) { internalCurrentPosition = positionMs }
    override fun seekToDefaultPosition() { internalCurrentPosition = 0L }
    override fun seekToDefaultPosition(windowIndex: Int) { internalCurrentPosition = 0L }
    override fun seekBack() {}
    override fun seekForward() {}

    // Media & preparation
    override fun getMediaMetadata(): MediaMetadata = internalMediaMetadata
    override fun setMediaItem(mediaItem: MediaItem) { internalMediaMetadata = mediaItem.mediaMetadata }
    override fun setMediaItem(mediaItem: MediaItem, startPositionMs: Long) { internalMediaMetadata = mediaItem.mediaMetadata; internalCurrentPosition = startPositionMs }
    override fun setMediaItem(mediaItem: MediaItem, resetPosition: Boolean) { internalMediaMetadata = mediaItem.mediaMetadata; if (resetPosition) internalCurrentPosition = 0L }
    override fun setMediaItems(mediaItems: MutableList<MediaItem>) {}
    override fun prepare() { /* no-op */ }
    override fun play() { internalPlayWhenReady = true }
    override fun pause() { internalPlayWhenReady = false }
    override fun stop() { stopCount += 1; internalPlayWhenReady = false }

    // Volume
    override fun getVolume(): Float = internalVolume
    override fun setVolume(volume: Float) { internalVolume = volume }

    // Unused in tests — return defaults or no-ops
    override fun getApplicationLooper(): Looper = Looper.getMainLooper()
    override fun getAvailableCommands(): Player.Commands = Player.Commands.Builder().build()
    override fun isCommandAvailable(command: Int): Boolean = true
    override fun canAdvertiseSession(): Boolean = false
    override fun getPlaybackSuppressionReason(): Int = Player.PLAYBACK_SUPPRESSION_REASON_NONE
    override fun getPlayerError(): PlaybackException? = null
    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {}
    override fun getShuffleModeEnabled(): Boolean = false
    override fun isLoading(): Boolean = false
    override fun getSeekBackIncrement(): Long = 0L
    override fun getSeekForwardIncrement(): Long = 0L
    override fun getMaxSeekToPreviousPosition(): Long = 0L
    override fun release() {}
    override fun getCurrentTracks(): Tracks = Tracks.EMPTY
    override fun getTrackSelectionParameters(): TrackSelectionParameters = TrackSelectionParameters.DEFAULT_WITHOUT_CONTEXT
    override fun setTrackSelectionParameters(parameters: TrackSelectionParameters) {}
    override fun getPlaylistMetadata(): MediaMetadata = MediaMetadata.EMPTY
    override fun setPlaylistMetadata(mediaMetadata: MediaMetadata) {}
    override fun getCurrentTimeline(): Timeline = Timeline.EMPTY
    override fun getCurrentPeriodIndex(): Int = 0
    override fun getCurrentMediaItemIndex(): Int = 0
    override fun getCurrentWindowIndex(): Int = 0
    override fun getNextWindowIndex(): Int = C.INDEX_UNSET
    override fun getNextMediaItemIndex(): Int = C.INDEX_UNSET
    override fun getPreviousWindowIndex(): Int = C.INDEX_UNSET
    override fun getPreviousMediaItemIndex(): Int = C.INDEX_UNSET
    override fun getCurrentMediaItem(): MediaItem? = null
    override fun getMediaItemCount(): Int = 0
    override fun getMediaItemAt(index: Int): MediaItem = MediaItem.EMPTY
    override fun getBufferedPosition(): Long = internalCurrentPosition
    override fun getTotalBufferedDuration(): Long = 0L
    override fun getBufferedPercentage(): Int = 0
    override fun isPlayingAd(): Boolean = false
    override fun getCurrentAdGroupIndex(): Int = C.INDEX_UNSET
    override fun getCurrentAdIndexInAdGroup(): Int = C.INDEX_UNSET
    override fun getContentPosition(): Long = internalCurrentPosition
    override fun getContentBufferedPosition(): Long = internalCurrentPosition
    override fun isCurrentWindowDynamic(): Boolean = false
    override fun isCurrentMediaItemDynamic(): Boolean = false
    override fun isCurrentWindowLive(): Boolean = false
    override fun isCurrentMediaItemLive(): Boolean = false
    override fun getCurrentLiveOffset(): Long = C.TIME_UNSET
    override fun isCurrentWindowSeekable(): Boolean = true
    override fun isCurrentMediaItemSeekable(): Boolean = true
    override fun getContentDuration(): Long = internalDuration
    override fun getCurrentManifest(): Any? = null
    override fun clearVideoSurface() {}
    override fun clearVideoSurface(surface: Surface?) {}
    override fun setVideoSurface(surface: Surface?) {}
    override fun setVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {}
    override fun clearVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {}
    override fun setVideoSurfaceView(surfaceView: SurfaceView?) {}
    override fun clearVideoSurfaceView(surfaceView: SurfaceView?) {}
    override fun setVideoTextureView(textureView: TextureView?) {}
    override fun clearVideoTextureView(textureView: TextureView?) {}
    override fun getVideoSize(): VideoSize = VideoSize.UNKNOWN
    override fun getSurfaceSize(): androidx.media3.common.util.Size = androidx.media3.common.util.Size(0, 0)
    override fun getCurrentCues(): CueGroup = CueGroup.EMPTY_TIME_ZERO
    override fun getDeviceInfo(): DeviceInfo = DeviceInfo(0, 0, 0)
    override fun getDeviceVolume(): Int = 0
    override fun isDeviceMuted(): Boolean = false
    override fun setDeviceVolume(volume: Int) {}
    override fun setDeviceVolume(volume: Int, flags: Int) {}
    override fun increaseDeviceVolume() {}
    override fun increaseDeviceVolume(flags: Int) {}
    override fun decreaseDeviceVolume() {}
    override fun decreaseDeviceVolume(flags: Int) {}
    override fun setDeviceMuted(muted: Boolean) {}
    override fun setDeviceMuted(muted: Boolean, flags: Int) {}
    override fun setAudioAttributes(audioAttributes: AudioAttributes, handleAudioFocus: Boolean) {}
    override fun getAudioAttributes(): AudioAttributes = AudioAttributes.DEFAULT
    override fun setMediaItems(mediaItems: MutableList<MediaItem>, resetPosition: Boolean) {}
    override fun setMediaItems(mediaItems: MutableList<MediaItem>, startIndex: Int, startPositionMs: Long) {}
    override fun addMediaItems(index: Int, mediaItems: MutableList<MediaItem>) {}
    override fun moveMediaItems(fromIndex: Int, toIndex: Int, newIndex: Int) {}
    override fun replaceMediaItems(fromIndex: Int, toIndex: Int, mediaItems: MutableList<MediaItem>) {}
    override fun removeMediaItems(fromIndex: Int, toIndex: Int) {}
    override fun addMediaItems(mediaItems: MutableList<MediaItem>) {}
    override fun addMediaItem(mediaItem: MediaItem) {}
    override fun addMediaItem(index: Int, mediaItem: MediaItem) {}
    override fun moveMediaItem(fromIndex: Int, toIndex: Int) {}
    override fun replaceMediaItem(index: Int, mediaItem: MediaItem) {}
    override fun removeMediaItem(index: Int) {}
    override fun clearMediaItems() {}
    override fun hasPreviousMediaItem(): Boolean = false
    override fun seekToPreviousMediaItem() {}
    override fun seekToPrevious() {}
    override fun hasNextMediaItem(): Boolean = false
    override fun seekToNextMediaItem() {}
    override fun seekToNext() {}
    override fun setPlaybackSpeed(speed: Float) { internalPlaybackParameters = PlaybackParameters(speed) }
}
