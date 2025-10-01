package app.ember.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme

/**
 * Enhanced tag editor with undo functionality and validation
 */
@Composable
fun TagEditor(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    album: String,
    onTitleChange: (String) -> Unit,
    onArtistChange: (String) -> Unit,
    onAlbumChange: (String) -> Unit,
    onSave: () -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    isDirty: Boolean = false,
    canSave: Boolean = true,
    statusMessage: String? = null
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Edit Tags",
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
                    text = "💡 Tag editing tips:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Changes are saved to the audio file\n" +
                            "• Use undo to revert changes\n" +
                            "• Empty fields will be cleared\n" +
                            "• Special characters are supported",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Form fields
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title") },
                placeholder = { Text("Enter song title") },
                supportingText = {
                    Text(
                        text = "${title.length} characters",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
            
            OutlinedTextField(
                value = artist,
                onValueChange = onArtistChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Artist") },
                placeholder = { Text("Enter artist name") },
                supportingText = {
                    Text(
                        text = "${artist.length} characters",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
            
            OutlinedTextField(
                value = album,
                onValueChange = onAlbumChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Album") },
                placeholder = { Text("Enter album name") },
                supportingText = {
                    Text(
                        text = "${album.length} characters",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onSave,
                enabled = canSave && isDirty,
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Changes")
            }
            
            TextButton(
                onClick = onUndo,
                enabled = isDirty
            ) {
                Text("Undo")
            }
        }
        
        // Reset button
        TextButton(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset to Original")
        }
        
        // Status message
        if (statusMessage != null) {
            AssistChip(
                onClick = { },
                label = { Text(statusMessage) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Preview section
        HorizontalDivider()
        Text(
            text = "Preview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        TagPreview(
            title = title,
            artist = artist,
            album = album
        )
    }
}

@Composable
private fun TagPreview(
    title: String,
    artist: String,
    album: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Song Information",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            TagPreviewItem(
                label = "Title",
                value = title.ifBlank { "Not set" }
            )
            
            TagPreviewItem(
                label = "Artist",
                value = artist.ifBlank { "Not set" }
            )
            
            TagPreviewItem(
                label = "Album",
                value = album.ifBlank { "Not set" }
            )
        }
    }
}

@Composable
private fun TagPreviewItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (value == "Not set") {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TagEditorPreview() {
    EmberTheme {
        TagEditor(
            title = "Sample Song",
            artist = "Sample Artist",
            album = "Sample Album",
            onTitleChange = {},
            onArtistChange = {},
            onAlbumChange = {},
            onSave = {},
            onUndo = {},
            onReset = {},
            isDirty = true,
            statusMessage = "Changes saved successfully"
        )
    }
}
