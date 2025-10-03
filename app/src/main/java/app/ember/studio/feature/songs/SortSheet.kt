package app.ember.studio.feature.songs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortSheet(
    modifier: Modifier = Modifier,
    currentSort: SortOption = SortOption.SongName,
    currentDirection: SortDirection = SortDirection.Ascending,
    onSortChange: (SortOption, SortDirection) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedSort by remember { mutableStateOf(currentSort) }
    var selectedDirection by remember { mutableStateOf(currentDirection) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "Sort by",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Sort options
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                        .padding(8.dp)
                ) {
                    SortOption.values().forEach { option ->
                        SortOptionRow(
                            option = option,
                            isSelected = selectedSort == option,
                            onSelect = { selectedSort = option }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Direction options
            Text(
                text = "Direction",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                        .padding(8.dp)
                ) {
                    val directionOptions = getDirectionOptions(selectedSort)
                    directionOptions.forEach { (direction, label) ->
                        DirectionOptionRow(
                            label = label,
                            isSelected = selectedDirection == direction,
                            onSelect = { selectedDirection = direction }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                
                Button(
                    onClick = {
                        onSortChange(selectedSort, selectedDirection)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("OK")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SortOptionRow(
    modifier: Modifier = Modifier,
    option: SortOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = getSortOptionLabel(option),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
            label = "SelectionIndicator"
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DirectionOptionRow(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        RadioButton(
            selected = isSelected,
            onClick = null // null recommended for accessibility
        )
    }
}

private fun getSortOptionLabel(option: SortOption): String {
    return when (option) {
        SortOption.SongName -> "Song name"
        SortOption.ArtistName -> "Artist name"
        SortOption.AlbumName -> "Album name"
        SortOption.FolderName -> "Folder name"
        SortOption.AddedTime -> "Added time"
        SortOption.PlayCount -> "Play count"
        SortOption.Year -> "Year"
        SortOption.Duration -> "Duration"
        SortOption.Size -> "Size"
        SortOption.Random -> "Random"
    }
}

private fun getDirectionOptions(sortOption: SortOption): List<Pair<SortDirection, String>> {
    return when (sortOption) {
        SortOption.AddedTime, SortOption.PlayCount, SortOption.Year, SortOption.Duration, SortOption.Size -> {
            listOf(
                SortDirection.Descending to "New→Old",
                SortDirection.Ascending to "Old→New"
            )
        }
        else -> {
            listOf(
                SortDirection.Ascending to "A→Z",
                SortDirection.Descending to "Z→A"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SortSheetPreview() {
    EmberTheme {
        SortSheet(
            currentSort = SortOption.ArtistName,
            currentDirection = SortDirection.Ascending
        )
    }
}
