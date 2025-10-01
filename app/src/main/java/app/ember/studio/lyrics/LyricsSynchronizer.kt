package app.ember.studio.lyrics

import androidx.media3.common.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Synchronizes lyrics display with audio playback position
 */
class LyricsSynchronizer {
    
    private val _currentLineIndex = MutableStateFlow(-1)
    val currentLineIndex: StateFlow<Int> = _currentLineIndex.asStateFlow()
    
    private val _isSynchronized = MutableStateFlow(false)
    val isSynchronized: StateFlow<Boolean> = _isSynchronized.asStateFlow()
    
    private var lyricsLines: List<LyricsLine> = emptyList()
    private var player: Player? = null
    
    /**
     * Parse lyrics text into synchronized lines
     * Expected format: [mm:ss.xxx] Line text
     */
    fun parseLyrics(lyricsText: String): List<LyricsLine> {
        if (lyricsText.isBlank()) {
            lyricsLines = emptyList()
            return emptyList()
        }
        
        val lines = lyricsText.split("\n")
        val parsedLines = mutableListOf<LyricsLine>()
        
        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isBlank()) continue
            
            // Try to parse timestamp format [mm:ss.xxx] or [mm:ss]
            val timestampRegex = Regex("""\[(\d{1,2}):(\d{2})(?:\.(\d{1,3}))?\]\s*(.*)""")
            val match = timestampRegex.find(trimmedLine)
            
            if (match != null) {
                val minutes = match.groupValues[1].toInt()
                val seconds = match.groupValues[2].toInt()
                val milliseconds = match.groupValues[3].let { 
                    if (it.isNotEmpty()) it.padEnd(3, '0').toInt() else 0 
                }
                val text = match.groupValues[4].trim()
                
                val timestampMs = (minutes * 60 + seconds) * 1000L + milliseconds
                parsedLines.add(LyricsLine(timestampMs, text))
            } else {
                // Line without timestamp - add with previous timestamp or 0
                val timestamp = if (parsedLines.isNotEmpty()) {
                    parsedLines.last().timestampMs + 1000L // 1 second after previous
                } else {
                    0L
                }
                parsedLines.add(LyricsLine(timestamp, trimmedLine))
            }
        }
        
        lyricsLines = parsedLines.sortedBy { it.timestampMs }
        return lyricsLines
    }
    
    /**
     * Start synchronization with player
     */
    fun startSynchronization(player: Player) {
        this.player = player
        _isSynchronized.value = lyricsLines.isNotEmpty()
        
        if (_isSynchronized.value) {
            updateCurrentLine(player.currentPosition)
        }
    }
    
    /**
     * Stop synchronization
     */
    fun stopSynchronization() {
        player = null
        _isSynchronized.value = false
        _currentLineIndex.value = -1
    }
    
    /**
     * Update current line based on playback position
     */
    fun updateCurrentLine(positionMs: Long) {
        if (!_isSynchronized.value || lyricsLines.isEmpty()) return
        
        val newIndex = findCurrentLineIndex(positionMs)
        if (newIndex != _currentLineIndex.value) {
            _currentLineIndex.value = newIndex
        }
    }
    
    /**
     * Find the current line index based on position
     */
    private fun findCurrentLineIndex(positionMs: Long): Int {
        if (lyricsLines.isEmpty()) return -1
        
        // Find the last line that hasn't started yet
        for (i in lyricsLines.indices) {
            if (lyricsLines[i].timestampMs > positionMs) {
                return (i - 1).coerceAtLeast(0)
            }
        }
        
        // If we're past all lyrics, return the last line
        return lyricsLines.lastIndex
    }
    
    /**
     * Get the current lyrics line
     */
    fun getCurrentLine(): LyricsLine? {
        val index = _currentLineIndex.value
        return if (index >= 0 && index < lyricsLines.size) {
            lyricsLines[index]
        } else {
            null
        }
    }
    
    /**
     * Get all lyrics lines
     */
    fun getAllLines(): List<LyricsLine> = lyricsLines
    
    /**
     * Get lines around current position for context
     */
    fun getContextLines(contextSize: Int = 3): List<LyricsLine> {
        val currentIndex = _currentLineIndex.value
        if (currentIndex < 0 || lyricsLines.isEmpty()) return emptyList()
        
        val startIndex = (currentIndex - contextSize).coerceAtLeast(0)
        val endIndex = (currentIndex + contextSize).coerceAtMost(lyricsLines.lastIndex)
        
        return lyricsLines.subList(startIndex, endIndex + 1)
    }
    
    /**
     * Seek to a specific lyrics line
     */
    fun seekToLine(lineIndex: Int) {
        if (lineIndex < 0 || lineIndex >= lyricsLines.size) return
        
        val timestampMs = lyricsLines[lineIndex].timestampMs
        player?.seekTo(timestampMs)
    }
}

/**
 * Represents a single line of lyrics with timestamp
 */
data class LyricsLine(
    val timestampMs: Long,
    val text: String
) {
    val timestampText: String
        get() = formatTimestamp(timestampMs)
    
    private fun formatTimestamp(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

/**
 * Sample lyrics for testing
 */
object SampleLyrics {
    const val SAMPLE_LYRICS = """
[00:00.000] Verse 1
[00:15.500] This is a sample song
[00:20.000] With synchronized lyrics
[00:25.500] That will highlight as we play
[00:30.000] 
[00:35.000] Chorus
[00:40.000] Sing along with the music
[00:45.000] Follow the highlighted words
[00:50.000] Enjoy the synchronized experience
[00:55.000] 
[01:00.000] Verse 2
[01:05.000] The lyrics will change color
[01:10.000] As the song progresses
[01:15.000] Creating an immersive experience
[01:20.000] 
[01:25.000] Bridge
[01:30.000] This is where the magic happens
[01:35.000] When words and music align
[01:40.000] Creating perfect harmony
[01:45.000] 
[01:50.000] Final Chorus
[01:55.000] Sing along with the music
[02:00.000] Follow the highlighted words
[02:05.000] Enjoy the synchronized experience
[02:10.000] Until the very end
"""
}
