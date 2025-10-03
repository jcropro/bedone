package app.ember.studio.feature.songs

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
fun FilterSheet(
    modifier: Modifier = Modifier,
    activeFilters: Set<FilterOption> = emptySet(),
    onFilterChange: (Set<FilterOption>) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedFilters by remember { mutableStateOf(activeFilters) }

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
                text = "Filters",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Time-based filters
            FilterCategory(
                title = "Time & Status",
                filters = listOf(
                    FilterOption.RecentlyAdded,
                    FilterOption.Downloads,
                    FilterOption.Favorites
                ),
                selectedFilters = selectedFilters,
                onFilterToggle = { filter ->
                    selectedFilters = if (selectedFilters.contains(filter)) {
                        selectedFilters - filter
                    } else {
                        selectedFilters + filter
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content filters
            FilterCategory(
                title = "Content",
                filters = listOf(
                    FilterOption.LongAudio,
                    FilterOption.HasLyrics
                ),
                selectedFilters = selectedFilters,
                onFilterToggle = { filter ->
                    selectedFilters = if (selectedFilters.contains(filter)) {
                        selectedFilters - filter
                    } else {
                        selectedFilters + filter
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quality filters
            FilterCategory(
                title = "Quality",
                filters = listOf(
                    FilterOption.HighBitrate
                ),
                selectedFilters = selectedFilters,
                onFilterToggle = { filter ->
                    selectedFilters = if (selectedFilters.contains(filter)) {
                        selectedFilters - filter
                    } else {
                        selectedFilters + filter
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // File type filters
            FilterCategory(
                title = "File Type",
                filters = listOf(
                    FilterOption.Mp3,
                    FilterOption.Flac,
                    FilterOption.Aac
                ),
                selectedFilters = selectedFilters,
                onFilterToggle = { filter ->
                    selectedFilters = if (selectedFilters.contains(filter)) {
                        selectedFilters - filter
                    } else {
                        selectedFilters + filter
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = {
                        selectedFilters = emptySet()
                        onFilterChange(emptySet())
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear all")
                }
                
                Button(
                    onClick = {
                        onFilterChange(selectedFilters)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Apply")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FilterCategory(
    modifier: Modifier = Modifier,
    title: String,
    filters: List<FilterOption>,
    selectedFilters: Set<FilterOption>,
    onFilterToggle: (FilterOption) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
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
                filters.forEach { filter ->
                    FilterOptionRow(
                        filter = filter,
                        isSelected = selectedFilters.contains(filter),
                        onToggle = { onFilterToggle(filter) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterOptionRow(
    modifier: Modifier = Modifier,
    filter: FilterOption,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(
                selected = isSelected,
                onClick = onToggle,
                role = Role.Checkbox
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = getFilterLabel(filter),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Checkbox(
            checked = isSelected,
            onCheckedChange = null // null recommended for accessibility
        )
    }
}

private fun getFilterLabel(filter: FilterOption): String {
    return when (filter) {
        FilterOption.RecentlyAdded -> "Recently added"
        FilterOption.Downloads -> "Downloads"
        FilterOption.LongAudio -> "Long audio (≥20 min)"
        FilterOption.HasLyrics -> "Has lyrics"
        FilterOption.HighBitrate -> "High bitrate (≥320 kbps)"
        FilterOption.Mp3 -> "MP3"
        FilterOption.Flac -> "FLAC"
        FilterOption.Aac -> "AAC"
        FilterOption.Favorites -> "Favorites"
    }
}

@Preview(showBackground = true)
@Composable
fun FilterSheetPreview() {
    EmberTheme {
        FilterSheet(
            activeFilters = setOf(FilterOption.RecentlyAdded, FilterOption.HasLyrics)
        )
    }
}
