package app.ember.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme

/**
 * Enhanced lyrics editor with timestamp support and formatting
 */
@Composable
fun LyricsEditor(
    modifier: Modifier = Modifier,
    lyricsText: String,
    onLyricsChange: (String) -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit,
    onLoadSample: () -> Unit,
    isSaveEnabled: Boolean = true,
    statusMessage: String? = null
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(lyricsText)) }
    
    // Update text field when lyrics change externally
    androidx.compose.runtime.LaunchedEffect(lyricsText) {
        textFieldValue = TextFieldValue(lyricsText)
    }
    
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Lyrics Editor",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Helper text
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "💡 Tips for synchronized lyrics:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Use [mm:ss] or [mm:ss.xxx] format for timestamps\n" +
                            "• Example: [01:30] This is a lyric line\n" +
                            "• Lines without timestamps will auto-sync\n" +
                            "• Empty lines create pauses",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Text editor
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                onLyricsChange(newValue.text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            label = { Text("Lyrics with timestamps") },
            placeholder = { 
                Text(
                    text = "[00:00] Enter your lyrics here...\n[00:15] With timestamps for sync\n[00:30] Or without for auto-sync",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            supportingText = {
                val lineCount = textFieldValue.text.split("\n").size
                val charCount = textFieldValue.text.length
                Text(
                    text = "$charCount characters, $lineCount lines",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        )
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onSave,
                enabled = isSaveEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Lyrics")
            }
            
            TextButton(onClick = onClear) {
                Text("Clear")
            }
        }
        
        // Sample and status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AssistChip(
                onClick = onLoadSample,
                label = { Text("Load Sample") }
            )
            
            if (statusMessage != null) {
                AssistChip(
                    onClick = { },
                    label = { Text(statusMessage) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Preview section
        HorizontalDivider()
        Text(
            text = "Preview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        if (textFieldValue.text.isBlank()) {
            EmptyPreviewState()
        } else {
            LyricsPreview(
                lyricsText = textFieldValue.text,
                modifier = Modifier.height(200.dp)
            )
        }
    }
}

@Composable
private fun LyricsPreview(
    lyricsText: String,
    modifier: Modifier = Modifier
) {
    val parsedLines = remember(lyricsText) {
        parseLyricsForPreview(lyricsText)
    }
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            parsedLines.forEach { line ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (line.timestampMs > 0) {
                        Text(
                            text = line.timestampText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(60.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Spacer(modifier = Modifier.width(68.dp))
                    }
                    
                    Text(
                        text = line.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyPreviewState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Preview will appear here as you type",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// Helper function for preview parsing
private fun parseLyricsForPreview(lyricsText: String): List<PreviewLyricsLine> {
    if (lyricsText.isBlank()) return emptyList()
    
    val lines = lyricsText.split("\n")
    val parsedLines = mutableListOf<PreviewLyricsLine>()
    
    for (line in lines) {
        val trimmedLine = line.trim()
        if (trimmedLine.isBlank()) {
            parsedLines.add(PreviewLyricsLine(0L, ""))
            continue
        }
        
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
            parsedLines.add(PreviewLyricsLine(timestampMs, text))
        } else {
            // Line without timestamp
            parsedLines.add(PreviewLyricsLine(0L, trimmedLine))
        }
    }
    
    return parsedLines
}

data class PreviewLyricsLine(
    val timestampMs: Long,
    val text: String
) {
    val timestampText: String
        get() = if (timestampMs > 0) {
            formatTimestamp(timestampMs)
        } else {
            ""
        }
    
    private fun formatTimestamp(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

@Preview(showBackground = true)
@Composable
private fun LyricsEditorPreview() {
    EmberTheme {
        LyricsEditor(
            lyricsText = "[00:00] Sample lyrics\n[00:15] With timestamps\n[00:30] For synchronization",
            onLyricsChange = {},
            onSave = {},
            onClear = {},
            onLoadSample = {},
            statusMessage = "Lyrics saved successfully"
        )
    }
}
