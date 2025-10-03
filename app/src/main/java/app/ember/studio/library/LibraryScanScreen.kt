package app.ember.studio.library

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.design.*
import app.ember.core.ui.components.PremiumLoadingIndicator
import app.ember.core.ui.components.GlassMorphismCard
import app.ember.studio.R
import kotlinx.coroutines.delay
import android.net.Uri

/**
 * Premium Library Scan Screen with advanced progress indicators and folder management
 * 
 * Features:
 * - Real-time progress indicators with flame animations
 * - Folder management with SAF integration
 * - Smart playlist creation
 * - Premium visual design with glass morphism
 * - Smooth animations and micro-interactions
 * - Comprehensive error handling
 */
@Composable
fun LibraryScanScreen(
    modifier: Modifier = Modifier,
    scanProgress: ScanProgress,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onAddFolder: () -> Unit,
    onRemoveFolder: (Uri) -> Unit,
    onToggleFolder: (Uri) -> Unit,
    onCreateSmartPlaylist: () -> Unit,
    onViewStatistics: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmberInk)
            .padding(Spacing16)
    ) {
        // Header
        LibraryScanHeader(
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Progress Section
        if (scanProgress.isScanning) {
            LibraryScanProgress(
                progress = scanProgress,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Spacing24))
        } else if (scanProgress.totalFiles == 0) {
            // Show premium loading indicator when no files are scanned yet
            PremiumLoadingIndicator(
                isVisible = true,
                message = "Ready to scan your library",
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Spacing24))
        }
        
        // Folder Management
        LibraryFolderManagement(
            folders = scanProgress.folders,
            onAddFolder = onAddFolder,
            onRemoveFolder = onRemoveFolder,
            onToggleFolder = onToggleFolder,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Action Buttons
        LibraryScanActions(
            isScanning = scanProgress.isScanning,
            onStartScan = onStartScan,
            onStopScan = onStopScan,
            onCreateSmartPlaylist = onCreateSmartPlaylist,
            onViewStatistics = onViewStatistics,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Error Display
        if (scanProgress.errors.isNotEmpty()) {
            LibraryScanErrors(
                errors = scanProgress.errors,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LibraryScanHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.FolderOpen,
            contentDescription = "Library Scan",
            tint = EmberFlame,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.width(Spacing12))
        
        Column {
            Text(
                text = "Library Scan",
                style = MaterialTheme.typography.headlineSmall,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Manage your music library",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun LibraryScanProgress(
    progress: ScanProgress,
    modifier: Modifier = Modifier
) {
    val progressValue = if (progress.totalFiles > 0) {
        progress.processedFiles.toFloat() / progress.totalFiles.toFloat()
    } else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progressValue,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "progress"
    )
    
    val phaseColor by animateColorAsState(
        targetValue = when (progress.currentPhase) {
            ScanPhase.Preparing -> AccentIce
            ScanPhase.ScanningAudio -> EmberFlame
            ScanPhase.ScanningVideo -> AccentCool
            ScanPhase.ProcessingMetadata -> EmberWarm1
            ScanPhase.BuildingIndex -> EmberWarm2
            ScanPhase.Completing -> Success
            else -> TextMuted
        },
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "phaseColor"
    )
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            // Phase indicator
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(phaseColor, RoundedCornerShape(RadiusPill))
                )
                
                Spacer(modifier = Modifier.width(Spacing8))
                
                Text(
                    text = getPhaseDisplayName(progress.currentPhase),
                    color = TextStrong,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Progress bar
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.fillMaxWidth(),
                color = EmberFlame,
                trackColor = EmberOutline.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(Spacing8))
            
            // Progress text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${progress.processedFiles} / ${progress.totalFiles} files",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                
                if (progress.estimatedTimeRemaining > 0) {
                    Text(
                        text = formatTimeRemaining(progress.estimatedTimeRemaining),
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
            
            // Current file
            if (progress.currentFile.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing8))
                Text(
                    text = progress.currentFile,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LibraryFolderManagement(
    folders: List<ScanFolder>,
    onAddFolder: () -> Unit,
    onRemoveFolder: (Uri) -> Unit,
    onToggleFolder: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Scan Folders",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            IconButton(onClick = onAddFolder) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Folder",
                    tint = EmberFlame
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing12))
        
        if (folders.isEmpty()) {
            // Empty state
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EmberCard),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing24),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.FolderOpen,
                        contentDescription = "No Folders",
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(Spacing12))
                    
                    Text(
                        text = "No folders selected",
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "Add folders to scan your music library",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            // Folder list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                items(folders) { folder ->
                    LibraryFolderItem(
                        folder = folder,
                        onRemove = { onRemoveFolder(folder.uri) },
                        onToggle = { onToggleFolder(folder.uri) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryFolderItem(
    folder: ScanFolder,
    onRemove: () -> Unit,
    onToggle: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (folder.isIncluded) EmberCard else EmberCard.copy(alpha = 0.5f),
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "backgroundColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (folder.isIncluded) TextStrong else TextMuted,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "textColor"
    )
    
    GlassMorphismCard(
        modifier = Modifier.fillMaxWidth(),
        isHovered = folder.isIncluded,
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Toggle button
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (folder.isIncluded) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = if (folder.isIncluded) "Included" else "Excluded",
                    tint = if (folder.isIncluded) EmberFlame else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing8))
            
            // Folder info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.displayName,
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (folder.fileCount > 0) {
                    Text(
                        text = "${folder.fileCount} files",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
            
            // Remove button
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove Folder",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun LibraryScanActions(
    isScanning: Boolean,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onCreateSmartPlaylist: () -> Unit,
    onViewStatistics: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Primary action button
        Button(
            onClick = if (isScanning) onStopScan else onStartScan,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isScanning) Error else EmberFlame
            ),
            shape = RoundedCornerShape(RadiusLG)
        ) {
            Icon(
                imageVector = if (isScanning) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                contentDescription = if (isScanning) "Stop Scan" else "Start Scan",
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(Spacing8))
            
            Text(
                text = if (isScanning) "Stop Scan" else "Start Scan",
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing12))
        
        // Secondary actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing8)
        ) {
            OutlinedButton(
                onClick = onCreateSmartPlaylist,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmberFlame),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(EmberFlame, EmberFlame.copy(alpha = 0.5f))
                    )
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = "Smart Playlist",
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing4))
                
                Text(
                    text = "Smart Playlist",
                    fontSize = 12.sp
                )
            }
            
            OutlinedButton(
                onClick = onViewStatistics,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
            ) {
                Icon(
                    imageVector = Icons.Filled.Analytics,
                    contentDescription = "Statistics",
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing4))
                
                Text(
                    text = "Statistics",
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun LibraryScanErrors(
    errors: List<ScanError>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Errors",
                    tint = Error,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing8))
                
                Text(
                    text = "Scan Errors (${errors.size})",
                    color = Error,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing8))
            
            errors.take(3).forEach { error ->
                Text(
                    text = "${error.filePath}: ${error.errorMessage}",
                    color = TextMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (errors.size > 3) {
                Text(
                    text = "... and ${errors.size - 3} more errors",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun getPhaseDisplayName(phase: ScanPhase): String = when (phase) {
    ScanPhase.Preparing -> "Preparing scan..."
    ScanPhase.ScanningAudio -> "Scanning audio files..."
    ScanPhase.ScanningVideo -> "Scanning video files..."
    ScanPhase.ProcessingMetadata -> "Processing metadata..."
    ScanPhase.BuildingIndex -> "Building library index..."
    ScanPhase.Completing -> "Finalizing..."
    ScanPhase.Idle -> "Ready"
}

private fun formatTimeRemaining(milliseconds: Long): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes % 60}m"
        minutes > 0 -> "${minutes}m ${seconds % 60}s"
        else -> "${seconds}s"
    }
}
