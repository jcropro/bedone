package app.ember.studio.library

import android.net.Uri

/**
 * Data models for advanced library functionality
 * 
 * These models support:
 * - Library scanning with progress tracking
 * - Smart playlist creation with dynamic rules
 * - Metadata editing with batch operations
 * - Comprehensive library statistics
 * - Folder management with SAF integration
 */

// ============================================================================
// LIBRARY SCANNING MODELS
// ============================================================================

/**
 * Represents the current state of library scanning
 */
data class ScanProgress(
    val isScanning: Boolean = false,
    val currentPhase: ScanPhase = ScanPhase.Idle,
    val processedFiles: Int = 0,
    val totalFiles: Int = 0,
    val currentFile: String = "",
    val estimatedTimeRemaining: Long = 0, // milliseconds
    val folders: List<ScanFolder> = emptyList(),
    val errors: List<ScanError> = emptyList()
)

/**
 * Represents the different phases of library scanning
 */
enum class ScanPhase {
    Idle,
    Preparing,
    ScanningAudio,
    ScanningVideo,
    ProcessingMetadata,
    BuildingIndex,
    Completing
}

/**
 * Represents a folder being scanned
 */
data class ScanFolder(
    val uri: Uri,
    val displayName: String,
    val fileCount: Int = 0,
    val isIncluded: Boolean = true
)

/**
 * Represents a scan error
 */
data class ScanError(
    val filePath: String,
    val errorMessage: String,
    val errorType: ScanErrorType
)

/**
 * Types of scan errors
 */
enum class ScanErrorType {
    FileNotFound,
    PermissionDenied,
    CorruptedFile,
    UnsupportedFormat,
    MetadataError,
    NetworkError
}

// ============================================================================
// SMART PLAYLIST MODELS
// ============================================================================

/**
 * Represents a smart playlist with dynamic rules
 */
data class SmartPlaylist(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val rules: List<SmartPlaylistRule> = emptyList(),
    val matchingTracks: List<TrackPreview> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Represents a rule in a smart playlist
 */
data class SmartPlaylistRule(
    val field: SmartPlaylistField = SmartPlaylistField.Title,
    val operator: SmartPlaylistOperator = SmartPlaylistOperator.Contains,
    val value: String = ""
)

/**
 * Fields that can be used in smart playlist rules
 */
enum class SmartPlaylistField {
    Title,
    Artist,
    Album,
    Genre,
    Year,
    Rating,
    PlayCount,
    DateAdded,
    Duration,
    Bitrate
}

/**
 * Operators for smart playlist rules
 */
enum class SmartPlaylistOperator {
    Contains,
    Equals,
    StartsWith,
    EndsWith,
    GreaterThan,
    LessThan,
    IsEmpty,
    IsNotEmpty
}

/**
 * Preview of a track for smart playlist matching
 */
data class TrackPreview(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val trackNumber: Int,
    val duration: String
)

// ============================================================================
// METADATA EDITING MODELS
// ============================================================================

/**
 * Represents track metadata for editing
 */
data class TrackMetadata(
    val id: String,
    val filePath: String,
    val title: String,
    val artist: String,
    val album: String,
    val genre: String,
    val year: Int,
    val trackNumber: Int,
    val discNumber: Int = 1,
    val isSelected: Boolean = false,
    val hasChanges: Boolean = false,
    val originalMetadata: TrackMetadata? = null
)

/**
 * Represents metadata editing session
 */
data class MetadataEditSession(
    val tracks: List<TrackMetadata>,
    val hasUnsavedChanges: Boolean = false,
    val lastSaved: Long = 0
)

// ============================================================================
// LIBRARY STATISTICS MODELS
// ============================================================================

/**
 * Comprehensive library statistics
 */
data class LibraryStatistics(
    val totalTracks: Int = 0,
    val totalAlbums: Int = 0,
    val uniqueArtists: Int = 0,
    val totalDuration: String = "0:00",
    val totalSizeBytes: Long = 0,
    val totalFolders: Int = 0,
    val genreDistribution: List<GenreStatistic> = emptyList(),
    val yearDistribution: List<YearStatistic> = emptyList(),
    val formatDistribution: List<FormatStatistic> = emptyList(),
    val topArtists: List<ArtistStatistic> = emptyList(),
    val topAlbums: List<AlbumStatistic> = emptyList(),
    val storageUsage: StorageUsage = StorageUsage(),
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Genre distribution statistic
 */
data class GenreStatistic(
    val name: String,
    val trackCount: Int,
    val totalTracks: Int,
    val color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified
)

/**
 * Year distribution statistic
 */
data class YearStatistic(
    val year: Int,
    val trackCount: Int,
    val totalTracks: Int
)

/**
 * File format distribution statistic
 */
data class FormatStatistic(
    val extension: String,
    val trackCount: Int,
    val sizeBytes: Long,
    val sizeFormatted: String
)

/**
 * Top artist statistic
 */
data class ArtistStatistic(
    val name: String,
    val trackCount: Int,
    val albumCount: Int
)

/**
 * Top album statistic
 */
data class AlbumStatistic(
    val name: String,
    val artist: String,
    val trackCount: Int
)

/**
 * Storage usage breakdown
 */
data class StorageUsage(
    val totalSizeBytes: Long = 0,
    val audioSizeBytes: Long = 0,
    val videoSizeBytes: Long = 0,
    val otherSizeBytes: Long = 0
)

// ============================================================================
// LIBRARY MANAGEMENT MODELS
// ============================================================================

/**
 * Represents library management settings
 */
data class LibrarySettings(
    val autoScanOnStartup: Boolean = true,
    val scanHiddenFiles: Boolean = false,
    val includeVideoFiles: Boolean = true,
    val maxScanDepth: Int = 10,
    val scanIntervalHours: Int = 24,
    val enableSmartPlaylists: Boolean = true,
    val enableMetadataEditing: Boolean = true,
    val backupMetadata: Boolean = true
)

/**
 * Represents a library backup
 */
data class LibraryBackup(
    val id: String,
    val name: String,
    val createdAt: Long,
    val sizeBytes: Long,
    val trackCount: Int,
    val includesMetadata: Boolean,
    val includesPlaylists: Boolean
)

/**
 * Represents library import/export options
 */
data class LibraryImportExport(
    val format: ImportExportFormat,
    val includeMetadata: Boolean = true,
    val includePlaylists: Boolean = true,
    val includeStatistics: Boolean = false,
    val compressionLevel: Int = 6
)

/**
 * Supported import/export formats
 */
enum class ImportExportFormat {
    JSON,
    XML,
    CSV,
    M3U,
    PLS
}

// ============================================================================
// UTILITY FUNCTIONS
// ============================================================================

/**
 * Extension functions for library models
 */

fun TrackMetadata.hasFieldChanged(field: String, newValue: String): Boolean {
    return when (field) {
        "title" -> title != newValue
        "artist" -> artist != newValue
        "album" -> album != newValue
        "genre" -> genre != newValue
        "year" -> year.toString() != newValue
        "trackNumber" -> trackNumber.toString() != newValue
        else -> false
    }
}

fun SmartPlaylistRule.isValid(): Boolean {
    return when (operator) {
        SmartPlaylistOperator.IsEmpty, SmartPlaylistOperator.IsNotEmpty -> true
        else -> value.isNotBlank()
    }
}

fun ScanProgress.getProgressPercentage(): Float {
    return if (totalFiles > 0) {
        processedFiles.toFloat() / totalFiles.toFloat()
    } else 0f
}

fun LibraryStatistics.getAverageTrackDuration(): String {
    return if (totalTracks > 0) {
        // TODO: Calculate average duration from total duration
        "3:45"
    } else "0:00"
}

fun StorageUsage.getAudioPercentage(): Float {
    return if (totalSizeBytes > 0) {
        audioSizeBytes.toFloat() / totalSizeBytes.toFloat()
    } else 0f
}

fun StorageUsage.getVideoPercentage(): Float {
    return if (totalSizeBytes > 0) {
        videoSizeBytes.toFloat() / totalSizeBytes.toFloat()
    } else 0f
}

fun StorageUsage.getOtherPercentage(): Float {
    return if (totalSizeBytes > 0) {
        otherSizeBytes.toFloat() / totalSizeBytes.toFloat()
    } else 0f
}
