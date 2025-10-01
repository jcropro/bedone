package app.ember.studio.lyrics

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import app.ember.studio.MainActivity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import androidx.media3.common.Player

/**
 * Comprehensive tests for lyrics synchronization functionality
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class LyricsSynchronizationTest {

    @Test
    fun lyricsSynchronizer_parsesTimestampsCorrectly() {
        val synchronizer = LyricsSynchronizer()
        val sampleLyrics = """
[00:00.000] Verse 1
[00:15.500] This is a sample song
[00:20.000] With synchronized lyrics
[00:25.500] That will highlight as we play
[00:30.000] 
[00:35.000] Chorus
[00:40.000] Sing along with the music
""".trimIndent()
        
        val parsedLines = synchronizer.parseLyrics(sampleLyrics)
        
        assertEquals("Should parse 7 lines", 7, parsedLines.size)
        assertEquals("First line timestamp", 0L, parsedLines[0].timestampMs)
        assertEquals("Second line timestamp", 15500L, parsedLines[1].timestampMs)
        assertEquals("Third line timestamp", 20000L, parsedLines[2].timestampMs)
        assertEquals("First line text", "Verse 1", parsedLines[0].text)
        assertEquals("Second line text", "This is a sample song", parsedLines[1].text)
    }

    @Test
    fun lyricsSynchronizer_handlesLinesWithoutTimestamps() {
        val synchronizer = LyricsSynchronizer()
        val lyricsWithoutTimestamps = """
This is a line without timestamp
Another line without timestamp
And one more line
""".trimIndent()
        
        val parsedLines = synchronizer.parseLyrics(lyricsWithoutTimestamps)
        
        assertEquals("Should parse 3 lines", 3, parsedLines.size)
        assertEquals("First line timestamp", 0L, parsedLines[0].timestampMs)
        assertEquals("Second line timestamp", 1000L, parsedLines[1].timestampMs)
        assertEquals("Third line timestamp", 2000L, parsedLines[2].timestampMs)
    }

    @Test
    fun lyricsSynchronizer_findsCurrentLineIndex() {
        val synchronizer = LyricsSynchronizer()
        val sampleLyrics = """
[00:00.000] Line 1
[00:10.000] Line 2
[00:20.000] Line 3
[00:30.000] Line 4
""".trimIndent()
        
        synchronizer.parseLyrics(sampleLyrics)
        
        // Test various positions
        assertEquals("Before first line", -1, findCurrentLineIndex(synchronizer.getAllLines(), 5000L))
        assertEquals("At first line", 0, findCurrentLineIndex(synchronizer.getAllLines(), 10000L))
        assertEquals("At second line", 1, findCurrentLineIndex(synchronizer.getAllLines(), 20000L))
        assertEquals("At third line", 2, findCurrentLineIndex(synchronizer.getAllLines(), 30000L))
        assertEquals("After last line", 3, findCurrentLineIndex(synchronizer.getAllLines(), 40000L))
    }

    @Test
    fun lyricsSynchronizer_handlesEmptyLyrics() {
        val synchronizer = LyricsSynchronizer()
        
        val emptyResult = synchronizer.parseLyrics("")
        assertTrue("Empty lyrics should return empty list", emptyResult.isEmpty())
        
        val blankResult = synchronizer.parseLyrics("   \n  \n  ")
        assertTrue("Blank lyrics should return empty list", blankResult.isEmpty())
    }

    @Test
    fun lyricsSynchronizer_contextLines() {
        val synchronizer = LyricsSynchronizer()
        val sampleLyrics = """
[00:00.000] Line 1
[00:10.000] Line 2
[00:20.000] Line 3
[00:30.000] Line 4
[00:40.000] Line 5
[00:50.000] Line 6
""".trimIndent()
        
        synchronizer.parseLyrics(sampleLyrics)
        
        // Test context around line 3 (index 2)
        val contextLines = synchronizer.getContextLines(contextSize = 2)
        assertEquals("Should return 5 context lines", 5, contextLines.size)
        assertEquals("First context line", "Line 1", contextLines[0].text)
        assertEquals("Last context line", "Line 5", contextLines[4].text)
    }

    @Test
    fun lyricsSynchronizer_integrationWithPlayer() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val field = MainActivity::class.java.getDeclaredField("playerViewModel")
            field.isAccessible = true
            val vm = field.get(activity) as app.ember.studio.PlayerViewModel
            val exo = vm.exoPlayerOrNull()!!
            
            val synchronizer = LyricsSynchronizer()
            val sampleLyrics = """
[00:00.000] Test lyrics
[00:05.000] For integration test
[00:10.000] With player
""".trimIndent()
            
            synchronizer.parseLyrics(sampleLyrics)
            synchronizer.startSynchronization(exo)
            
            // Start playback
            vm.playAllSongs()
            
            // Wait for player to be ready
            waitUntil(timeoutMs = 10000) {
                exo.playbackState == Player.STATE_READY && exo.duration > 0
            }
            
            // Test synchronization
            synchronizer.updateCurrentLine(exo.currentPosition)
            val currentLine = synchronizer.getCurrentLine()
            
            assertNotNull("Should have current line", currentLine)
            assertTrue("Should be synchronized", synchronizer.isSynchronized.value)
            
            // Clean up
            synchronizer.stopSynchronization()
        }
        scenario.close()
    }

    @Test
    fun lyricsDisplay_handlesMalformedTimestamps() {
        val synchronizer = LyricsSynchronizer()
        val malformedLyrics = """
[invalid] This line has invalid timestamp
[00:60.000] This line has invalid seconds
[25:00.000] This line has invalid minutes
[00:00] This line has no milliseconds
[00:00.000] This line is valid
""".trimIndent()
        
        val parsedLines = synchronizer.parseLyrics(malformedLyrics)
        
        // Should handle malformed timestamps gracefully
        assertTrue("Should parse some lines", parsedLines.isNotEmpty())
        
        // Valid line should be parsed correctly
        val validLine = parsedLines.find { it.text == "This line is valid" }
        assertNotNull("Should find valid line", validLine)
        assertEquals("Valid line timestamp", 0L, validLine?.timestampMs)
    }

    @Test
    fun lyricsDisplay_performanceWithLargeLyrics() {
        val synchronizer = LyricsSynchronizer()
        
        // Create large lyrics file
        val largeLyrics = buildString {
            for (i in 0..1000) {
                val minutes = i / 60
                val seconds = i % 60
                appendLine("[$minutes:${seconds.toString().padStart(2, '0')}.000] Line $i")
            }
        }
        
        val startTime = System.currentTimeMillis()
        val parsedLines = synchronizer.parseLyrics(largeLyrics)
        val endTime = System.currentTimeMillis()
        
        assertEquals("Should parse all 1001 lines", 1001, parsedLines.size)
        assertTrue("Should parse quickly (< 1 second)", (endTime - startTime) < 1000)
    }

    // Helper function to replicate the logic from LyricsDisplay
    private fun findCurrentLineIndex(lyricsLines: List<LyricsLine>, positionMs: Long): Int {
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

    private fun waitUntil(timeoutMs: Long, check: () -> Boolean) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (check()) return
            try { Thread.sleep(50) } catch (_: InterruptedException) { break }
        }
    }
}
