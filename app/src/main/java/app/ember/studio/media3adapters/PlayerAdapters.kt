package app.ember.studio.media3adapters

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

/**
 * Adapter functions for Player unstable API usage.
 * All unstable Media3 Player calls are centralized here to maintain lint compliance.
 */
object PlayerAdapters {

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setShuffleModeEnabled(player: Player, enabled: Boolean) {
        player.shuffleModeEnabled = enabled
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setRepeatMode(player: Player, mode: Int) {
        player.repeatMode = mode
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setPlaybackSpeed(player: Player, speed: Float) {
        val currentParameters = player.playbackParameters
        player.playbackParameters = PlaybackParameters(speed, currentParameters.pitch)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setPlayWhenReady(player: Player, playWhenReady: Boolean) {
        player.playWhenReady = playWhenReady
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun seekTo(player: Player, positionMs: Long) {
        player.seekTo(positionMs)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun pause(player: Player) {
        player.pause()
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun stop(player: Player) {
        player.stop()
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun prepare(player: Player) {
        player.prepare()
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setMediaItems(player: Player, mediaItems: List<MediaItem>, startIndex: Int, startPositionMs: Long) {
        player.setMediaItems(mediaItems, startIndex, startPositionMs)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setVolume(player: Player, volume: Float) {
        player.volume = volume
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getVolume(player: Player): Float {
        return player.volume
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getShuffleModeEnabled(player: Player): Boolean {
        return player.shuffleModeEnabled
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getRepeatMode(player: Player): Int {
        return player.repeatMode
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getPlayWhenReady(player: Player): Boolean {
        return player.playWhenReady
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getPlaybackState(player: Player): Int {
        return player.playbackState
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getCurrentPosition(player: Player): Long {
        return player.currentPosition
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getDuration(player: Player): Long {
        return player.duration
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getCurrentMediaItem(player: Player): MediaItem? {
        return player.currentMediaItem
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getCurrentMediaItemIndex(player: Player): Int {
        return player.currentMediaItemIndex
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getPlaybackParameters(player: Player): PlaybackParameters {
        return player.playbackParameters
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getAudioSessionId(exoPlayer: ExoPlayer): Int {
        return exoPlayer.audioSessionId
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setSkipSilenceEnabled(exoPlayer: ExoPlayer, enabled: Boolean) {
        exoPlayer.skipSilenceEnabled = enabled
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getSkipSilenceEnabled(exoPlayer: ExoPlayer): Boolean {
        return exoPlayer.skipSilenceEnabled
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun isPlaying(player: Player): Boolean {
        return player.isPlaying
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun addListener(player: Player, listener: Player.Listener) {
        player.addListener(listener)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun removeListener(player: Player, listener: Player.Listener) {
        player.removeListener(listener)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun release(player: Player) {
        player.release()
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun setMediaItem(player: Player, mediaItem: MediaItem) {
        player.setMediaItem(mediaItem)
    }

    @OptIn(UnstableApi::class)
    @Suppress("UnsafeOptInUsageError")
    fun getMediaMetadata(player: Player): MediaMetadata {
        return player.mediaMetadata
    }
}
