package app.ember.studio.feature.songs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme
import androidx.compose.foundation.layout.Column

@Composable
fun FilterChipRow(
    modifier: Modifier = Modifier,
    activeFilters: Set<FilterOption> = emptySet(),
    onFilterChange: (Set<FilterOption>) -> Unit = {},
    onShowAllFilters: () -> Unit = {}
) {
    if (activeFilters.isEmpty()) {
        // Show "Add filters" button when no filters are active
        AddFiltersButton(
            modifier = modifier,
            onClick = onShowAllFilters
        )
    } else {
        // Show active filter chips
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp)
        ) {
            items(activeFilters.toList()) { filter ->
                FilterChip(
                    filter = filter,
                    onRemove = {
                        onFilterChange(activeFilters - filter)
                    }
                )
            }
            
            // Add more filters button
            item {
                AddMoreFiltersButton(
                    onClick = onShowAllFilters
                )
            }
        }
    }
}

@Composable
private fun AddFiltersButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(32.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add filters",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            
            Text(
                text = "Add filters",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun FilterChip(
    modifier: Modifier = Modifier,
    filter: FilterOption,
    onRemove: () -> Unit
) {
    Card(
        modifier = modifier.height(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = getFilterLabel(filter),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Remove filter",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(16.dp)
                    .clickable(onClick = onRemove)
            )
        }
    }
}

@Composable
private fun AddMoreFiltersButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(32.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add more filters",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            
            Text(
                text = "More",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun getFilterLabel(filter: FilterOption): String {
    return when (filter) {
        FilterOption.RecentlyAdded -> "Recently added"
        FilterOption.Downloads -> "Downloads"
        FilterOption.LongAudio -> "Long audio"
        FilterOption.HasLyrics -> "Has lyrics"
        FilterOption.HighBitrate -> "High bitrate"
        FilterOption.Mp3 -> "MP3"
        FilterOption.Flac -> "FLAC"
        FilterOption.Aac -> "AAC"
        FilterOption.Favorites -> "Favorites"
    }
}

@Preview(showBackground = true)
@Composable
fun FilterChipRowPreview() {
    EmberTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterChipRow()
            FilterChipRow(
                activeFilters = setOf(FilterOption.RecentlyAdded, FilterOption.HasLyrics)
            )
        }
    }
}
