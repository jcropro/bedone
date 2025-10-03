package app.ember.studio.feature.songs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun LongAudioRoutingDialog(
    modifier: Modifier = Modifier,
    songTitle: String = "",
    onRouteSelected: (LongAudioRoute) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var selectedRoute by remember { mutableStateOf<LongAudioRoute?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Text(
                text = "Route long audio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "How would you like to treat \"$songTitle\"?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
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
                        LongAudioRoute.values().forEach { route ->
                            RouteOptionRow(
                                route = route,
                                isSelected = selectedRoute == route,
                                onSelect = { selectedRoute = route }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedRoute?.let { onRouteSelected(it) }
                    onDismiss()
                },
                enabled = selectedRoute != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Route")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun RouteOptionRow(
    modifier: Modifier = Modifier,
    route: LongAudioRoute,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = getRouteIcon(route),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = getRouteTitle(route),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = getRouteDescription(route),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        RadioButton(
            selected = isSelected,
            onClick = null // null recommended for accessibility
        )
    }
}

enum class LongAudioRoute {
    Music,
    Podcast,
    Audiobook,
    AskPerFile
}

private fun getRouteIcon(route: LongAudioRoute): androidx.compose.ui.graphics.vector.ImageVector {
    return when (route) {
        LongAudioRoute.Music -> Icons.Filled.MusicNote
        LongAudioRoute.Podcast -> Icons.Filled.MusicNote
        LongAudioRoute.Audiobook -> Icons.Filled.MusicNote
        LongAudioRoute.AskPerFile -> Icons.Filled.QuestionMark
    }
}

private fun getRouteTitle(route: LongAudioRoute): String {
    return when (route) {
        LongAudioRoute.Music -> "Music"
        LongAudioRoute.Podcast -> "Podcast"
        LongAudioRoute.Audiobook -> "Audiobook"
        LongAudioRoute.AskPerFile -> "Ask per file"
    }
}

private fun getRouteDescription(route: LongAudioRoute): String {
    return when (route) {
        LongAudioRoute.Music -> "Treat as regular music track"
        LongAudioRoute.Podcast -> "Enable podcast features (chapters, speed)"
        LongAudioRoute.Audiobook -> "Enable audiobook features (bookmarks, chapters)"
        LongAudioRoute.AskPerFile -> "Ask for each long audio file"
    }
}

@Preview(showBackground = true)
@Composable
fun LongAudioRoutingDialogPreview() {
    EmberTheme {
        LongAudioRoutingDialog(
            songTitle = "Sample Long Audio Track"
        )
    }
}
