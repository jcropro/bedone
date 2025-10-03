package app.ember.studio.library

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Smart Playlist Engine - Advanced playlist generation based on criteria
 * 
 * Features:
 * - Dynamic playlist generation based on user criteria
 * - Auto-updating playlists that refresh when library changes
 * - Complex filtering with multiple conditions
 * - Premium visual design with flame accents
 * - Smooth animations and micro-interactions
 */
data class SmartPlaylistCriteria(
    val name: String,
    val conditions: List<SmartCondition>,
    val sortBy: SmartSortOption = SmartSortOption.RecentlyAdded,
    val sortDirection: SmartSortDirection = SmartSortDirection.Descending,
    val limit: Int? = null, // null = no limit
    val autoUpdate: Boolean = true
)

data class SmartCondition(
    val field: SmartField,
    val operator: SmartOperator,
    val value: String,
    val isAnd: Boolean = true // true = AND, false = OR
)

enum class SmartField {
    Title, Artist, Album, Genre, Year, Duration, DateAdded, PlayCount, Rating, Bitrate, FileSize
}

enum class SmartOperator {
    Contains, Equals, StartsWith, EndsWith, GreaterThan, LessThan, Between, IsEmpty, IsNotEmpty
}

enum class SmartSortOption {
    Title, Artist, Album, Duration, DateAdded, RecentlyAdded, PlayCount, Rating, Random
}

enum class SmartSortDirection {
    Ascending, Descending
}

// SmartPlaylist data class is defined in LibraryModels.kt

class SmartPlaylistEngine(private val context: Context) {
    
    /**
     * Generate a smart playlist based on criteria
     */
    suspend fun generatePlaylist(
        libraryIndex: LibraryIndex,
        criteria: SmartPlaylistCriteria
    ): List<SongItem> = withContext(Dispatchers.Default) {
        
        var filteredSongs = libraryIndex.songs
        
        // Apply all conditions
        criteria.conditions.forEach { condition ->
            filteredSongs = when (condition.isAnd) {
                true -> filteredSongs.filter { song -> matchesCondition(song, condition) }
                false -> {
                    val currentFiltered = filteredSongs
                    val newFiltered = libraryIndex.songs.filter { song -> matchesCondition(song, condition) }
                    (currentFiltered + newFiltered).distinctBy { it.id }
                }
            }
        }
        
        // Apply sorting
        filteredSongs = when (criteria.sortBy) {
            SmartSortOption.Title -> filteredSongs.sortedBy { it.title.lowercase() }
            SmartSortOption.Artist -> filteredSongs.sortedBy { it.artist.lowercase() }
            SmartSortOption.Album -> filteredSongs.sortedBy { it.album.lowercase() }
            SmartSortOption.Duration -> filteredSongs.sortedBy { it.durationMs }
            SmartSortOption.DateAdded -> filteredSongs.sortedBy { it.dateAddedSec ?: 0L }
            SmartSortOption.RecentlyAdded -> filteredSongs.sortedBy { it.dateAddedSec ?: 0L }
            SmartSortOption.PlayCount -> filteredSongs.sortedBy { 0 } // Placeholder - would need play count data
            SmartSortOption.Rating -> filteredSongs.sortedBy { 0 } // Placeholder - would need rating data
            SmartSortOption.Random -> filteredSongs.shuffled()
        }
        
        // Apply sort direction
        if (criteria.sortDirection == SmartSortDirection.Descending) {
            filteredSongs = filteredSongs.reversed()
        }
        
        // Apply limit
        criteria.limit?.let { limit ->
            filteredSongs = filteredSongs.take(limit)
        }
        
        filteredSongs
    }
    
    private fun matchesCondition(song: SongItem, condition: SmartCondition): Boolean {
        val fieldValue = when (condition.field) {
            SmartField.Title -> song.title
            SmartField.Artist -> song.artist
            SmartField.Album -> song.album
            SmartField.Genre -> "" // Would need genre data
            SmartField.Year -> "" // Would need year data
            SmartField.Duration -> song.durationMs.toString()
            SmartField.DateAdded -> song.dateAddedSec?.toString() ?: ""
            SmartField.PlayCount -> "0" // Placeholder
            SmartField.Rating -> "0" // Placeholder
            SmartField.Bitrate -> "0" // Placeholder
            SmartField.FileSize -> "0" // Placeholder
        }
        
        return when (condition.operator) {
            SmartOperator.Contains -> fieldValue.lowercase().contains(condition.value.lowercase())
            SmartOperator.Equals -> fieldValue.equals(condition.value, ignoreCase = true)
            SmartOperator.StartsWith -> fieldValue.lowercase().startsWith(condition.value.lowercase())
            SmartOperator.EndsWith -> fieldValue.lowercase().endsWith(condition.value.lowercase())
            SmartOperator.GreaterThan -> {
                val numericValue = fieldValue.toLongOrNull() ?: 0L
                val conditionValue = condition.value.toLongOrNull() ?: 0L
                numericValue > conditionValue
            }
            SmartOperator.LessThan -> {
                val numericValue = fieldValue.toLongOrNull() ?: 0L
                val conditionValue = condition.value.toLongOrNull() ?: 0L
                numericValue < conditionValue
            }
            SmartOperator.Between -> {
                val parts = condition.value.split("-")
                if (parts.size == 2) {
                    val numericValue = fieldValue.toLongOrNull() ?: 0L
                    val minValue = parts[0].trim().toLongOrNull() ?: 0L
                    val maxValue = parts[1].trim().toLongOrNull() ?: Long.MAX_VALUE
                    numericValue in minValue..maxValue
                } else false
            }
            SmartOperator.IsEmpty -> fieldValue.isEmpty()
            SmartOperator.IsNotEmpty -> fieldValue.isNotEmpty()
        }
    }
    
    /**
     * Get predefined smart playlist templates
     */
    fun getPredefinedTemplates(): List<SmartPlaylistCriteria> = listOf(
        // Recently Added
        SmartPlaylistCriteria(
            name = "Recently Added",
            conditions = listOf(
                SmartCondition(SmartField.DateAdded, SmartOperator.GreaterThan, "0")
            ),
            sortBy = SmartSortOption.DateAdded,
            sortDirection = SmartSortDirection.Descending,
            limit = 50
        ),
        
        // Long Tracks (> 5 minutes)
        SmartPlaylistCriteria(
            name = "Long Tracks",
            conditions = listOf(
                SmartCondition(SmartField.Duration, SmartOperator.GreaterThan, TimeUnit.MINUTES.toMillis(5).toString())
            ),
            sortBy = SmartSortOption.Duration,
            sortDirection = SmartSortDirection.Descending
        ),
        
        // Short Tracks (< 3 minutes)
        SmartPlaylistCriteria(
            name = "Short Tracks",
            conditions = listOf(
                SmartCondition(SmartField.Duration, SmartOperator.LessThan, TimeUnit.MINUTES.toMillis(3).toString())
            ),
            sortBy = SmartSortOption.Duration,
            sortDirection = SmartSortDirection.Ascending
        ),
        
        // Unknown Artists
        SmartPlaylistCriteria(
            name = "Unknown Artists",
            conditions = listOf(
                SmartCondition(SmartField.Artist, SmartOperator.IsEmpty, ""),
                SmartCondition(SmartField.Artist, SmartOperator.Equals, "Unknown Artist", false)
            ),
            sortBy = SmartSortOption.Title,
            sortDirection = SmartSortDirection.Ascending
        ),
        
        // High Energy (based on duration and title keywords)
        SmartPlaylistCriteria(
            name = "High Energy",
            conditions = listOf(
                SmartCondition(SmartField.Duration, SmartOperator.Between, "${TimeUnit.MINUTES.toMillis(2)}-${TimeUnit.MINUTES.toMillis(4)}"),
                SmartCondition(SmartField.Title, SmartOperator.Contains, "energy", false),
                SmartCondition(SmartField.Title, SmartOperator.Contains, "fast", false),
                SmartCondition(SmartField.Title, SmartOperator.Contains, "upbeat", false)
            ),
            sortBy = SmartSortOption.Random,
            sortDirection = SmartSortDirection.Ascending
        ),
        
        // Chill Out (longer tracks, slower tempo keywords)
        SmartPlaylistCriteria(
            name = "Chill Out",
            conditions = listOf(
                SmartCondition(SmartField.Duration, SmartOperator.GreaterThan, TimeUnit.MINUTES.toMillis(4).toString()),
                SmartCondition(SmartField.Title, SmartOperator.Contains, "chill", false),
                SmartCondition(SmartField.Title, SmartOperator.Contains, "slow", false),
                SmartCondition(SmartField.Title, SmartOperator.Contains, "ambient", false)
            ),
            sortBy = SmartSortOption.Duration,
            sortDirection = SmartSortDirection.Descending
        )
    )
}
