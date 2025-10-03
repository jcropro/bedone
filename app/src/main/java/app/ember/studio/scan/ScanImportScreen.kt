package app.ember.studio.scan

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
import app.ember.studio.R

/**
 * Premium Scan/Import Screen
 * 
 * Features:
 * - File system browser with SAF integration
 * - Import progress tracking
 * - File format support indicators
 * - Duplicate detection
 * - Metadata extraction preview
 * - Premium visual design with glass morphism
 * - Smooth animations and micro-interactions
 */
@Composable
fun ScanImportScreen(
    modifier: Modifier = Modifier,
    scanImportState: ScanImportState,
    onSelectFolder: () -> Unit,
    onSelectFiles: () -> Unit,
    onStartImport: () -> Unit,
    onCancelImport: () -> Unit,
    onRemoveItem: (ImportItem) -> Unit,
    onToggleItem: (ImportItem) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onDismissMessage: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmberInk)
            .padding(Spacing16)
    ) {
        // Header
        ScanImportHeader(
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Import Progress
        if (scanImportState.isImporting) {
            ImportProgress(
                progress = scanImportState.importProgress,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Spacing24))
        }
        
        // Source Selection
        SourceSelection(
            onSelectFolder = onSelectFolder,
            onSelectFiles = onSelectFiles,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Spacing24))
        
        // Import Items List
        ImportItemsList(
            items = scanImportState.importItems,
            onRemoveItem = onRemoveItem,
            onToggleItem = onToggleItem,
            onSelectAll = onSelectAll,
            onDeselectAll = onDeselectAll,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.height(Spacing16))
        
        // Action Buttons
        ScanImportActions(
            isImporting = scanImportState.isImporting,
            hasItems = scanImportState.importItems.isNotEmpty(),
            selectedCount = scanImportState.importItems.count { it.isSelected },
            onStartImport = onStartImport,
            onCancelImport = onCancelImport,
            modifier = Modifier.fillMaxWidth()
        )
    }
    
    // Status Message
    if (scanImportState.statusMessage != null) {
        ScanImportStatusMessage(
            message = scanImportState.statusMessage,
            onDismiss = onDismissMessage
        )
    }
}

@Composable
private fun ScanImportHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.FolderOpen,
            contentDescription = "Scan Import",
            tint = EmberFlame,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.width(Spacing12))
        
        Column {
            Text(
                text = "Scan & Import",
                style = MaterialTheme.typography.headlineSmall,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Import music from your device",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun ImportProgress(
    progress: ImportProgress,
    modifier: Modifier = Modifier
) {
    val progressValue = if (progress.totalFiles > 0) {
        progress.processedFiles.toFloat() / progress.totalFiles.toFloat()
    } else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progressValue,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "progress"
    )
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "Importing",
                    tint = EmberFlame,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing8))
                
                Text(
                    text = "Importing Files",
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
                
                Text(
                    text = "${(progressValue * 100).toInt()}%",
                    color = TextMuted,
                    fontSize = 12.sp
                )
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
private fun SourceSelection(
    onSelectFolder: () -> Unit,
    onSelectFiles: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EmberCard),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing16)
        ) {
            Text(
                text = "Select Source",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing16))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                // Select Folder button
                Button(
                    onClick = onSelectFolder,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = EmberFlame),
                    shape = RoundedCornerShape(RadiusMD)
                ) {
                    Icon(
                        imageVector = Icons.Filled.FolderOpen,
                        contentDescription = "Select Folder",
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(Spacing8))
                    
                    Text(
                        text = "Select Folder",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                // Select Files button
                OutlinedButton(
                    onClick = onSelectFiles,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentIce)
                ) {
                    Icon(
                        imageVector = Icons.Filled.FileOpen,
                        contentDescription = "Select Files",
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(Spacing8))
                    
                    Text(
                        text = "Select Files",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportItemsList(
    items: List<ImportItem>,
    onRemoveItem: (ImportItem) -> Unit,
    onToggleItem: (ImportItem) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Header with actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Import Items (${items.size})",
                style = MaterialTheme.typography.titleMedium,
                color = TextStrong,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                OutlinedButton(
                    onClick = onSelectAll,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmberFlame)
                ) {
                    Text("Select All", fontSize = 12.sp)
                }
                
                OutlinedButton(
                    onClick = onDeselectAll,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
                ) {
                    Text("Deselect All", fontSize = 12.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing12))
        
        if (items.isEmpty()) {
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
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = "No Items",
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(Spacing12))
                    
                    Text(
                        text = "No files selected",
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "Select a folder or files to import",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            // Items list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing8)
            ) {
                items(items) { item ->
                    ImportItemCard(
                        item = item,
                        onRemove = { onRemoveItem(item) },
                        onToggle = { onToggleItem(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportItemCard(
    item: ImportItem,
    onRemove: () -> Unit,
    onToggle: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (item.isSelected) EmberCard.copy(alpha = 0.8f) else EmberCard,
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "backgroundColor"
    )
    
    val borderColor by animateColorAsState(
        targetValue = when {
            item.isDuplicate -> Warning
            item.hasError -> Error
            item.isSelected -> EmberFlame
            else -> EmberOutline
        },
        animationSpec = tween(MotionTransition, easing = EasingStandard), label = "borderColor"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation1),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection checkbox
            Checkbox(
                checked = item.isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = EmberFlame,
                    uncheckedColor = TextMuted
                )
            )
            
            Spacer(modifier = Modifier.width(Spacing8))
            
            // File icon
            Icon(
                imageVector = getFileIcon(item.fileType),
                contentDescription = "File Type",
                tint = getFileIconColor(item.fileType),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(Spacing8))
            
            // File info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.fileName,
                    color = TextStrong,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${item.fileSize} • ${item.filePath}",
                    color = TextMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Status indicators
                if (item.isDuplicate) {
                    Text(
                        text = "Duplicate",
                        color = Warning,
                        fontSize = 11.sp
                    )
                } else if (item.hasError) {
                    Text(
                        text = item.errorMessage ?: "Error",
                        color = Error,
                        fontSize = 11.sp
                    )
                }
            }
            
            // Remove button
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ScanImportActions(
    isImporting: Boolean,
    hasItems: Boolean,
    selectedCount: Int,
    onStartImport: () -> Unit,
    onCancelImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing8)
    ) {
        if (isImporting) {
            Button(
                onClick = onCancelImport,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Error),
                shape = RoundedCornerShape(RadiusLG)
            ) {
                Icon(
                    imageVector = Icons.Filled.Stop,
                    contentDescription = "Cancel",
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing8))
                
                Text(
                    text = "Cancel Import",
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Button(
                onClick = onStartImport,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = EmberFlame),
                shape = RoundedCornerShape(RadiusLG),
                enabled = hasItems && selectedCount > 0
            ) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "Import",
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing8))
                
                Text(
                    text = "Import $selectedCount Items",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ScanImportStatusMessage(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing16),
        colors = CardDefaults.cardColors(containerColor = EmberFlame.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation2)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Info",
                tint = EmberFlame,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(Spacing8))
            
            Text(
                text = message,
                color = TextStrong,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Dismiss",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun getFileIcon(fileType: FileType): ImageVector = when (fileType) {
    FileType.Audio -> Icons.Filled.MusicNote
    FileType.Video -> Icons.Filled.VideoFile
    FileType.Playlist -> Icons.Filled.PlaylistPlay
    FileType.Other -> Icons.Filled.InsertDriveFile
}

private fun getFileIconColor(fileType: FileType): Color = when (fileType) {
    FileType.Audio -> EmberFlame
    FileType.Video -> AccentIce
    FileType.Playlist -> AccentCool
    FileType.Other -> TextMuted
}

// Data models
data class ScanImportState(
    val isImporting: Boolean = false,
    val importProgress: ImportProgress = ImportProgress(),
    val importItems: List<ImportItem> = emptyList(),
    val statusMessage: String? = null
)

data class ImportProgress(
    val processedFiles: Int = 0,
    val totalFiles: Int = 0,
    val currentFile: String = ""
)

data class ImportItem(
    val fileName: String,
    val filePath: String,
    val fileSize: String,
    val fileType: FileType,
    val isSelected: Boolean = true,
    val isDuplicate: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
)

enum class FileType {
    Audio,
    Video,
    Playlist,
    Other
}
