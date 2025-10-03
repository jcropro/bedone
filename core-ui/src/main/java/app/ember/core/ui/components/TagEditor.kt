package app.ember.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.res.painterResource
import app.ember.core.ui.R
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.ember.core.ui.design.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Premium Tag Editor with metadata editing capabilities
 */
@Composable
fun TagEditor(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    songMetadata: SongMetadata,
    onSave: (SongMetadata) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 300, easing = EasingStandard)
        ) + fadeIn(animationSpec = tween(durationMillis = 200, easing = EasingStandard)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 300, easing = EasingStandard)
        ) + fadeOut(animationSpec = tween(durationMillis = 200, easing = EasingStandard))
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = EmberInk.copy(alpha = 0.8f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Tag editor content
                TagEditorContent(
                    songMetadata = songMetadata,
                    onSave = onSave,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun TagEditorContent(
    songMetadata: SongMetadata,
    onSave: (SongMetadata) -> Unit,
    onDismiss: () -> Unit
) {
    var editedMetadata by remember { mutableStateOf(songMetadata) }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .padding(Spacing16),
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = EmberCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            TagEditorHeader(
                songTitle = songMetadata.title,
                onSave = { onSave(editedMetadata) },
                onDismiss = onDismiss
            )
            
            HorizontalDivider(
                color = TextMuted.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            // Form content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing16),
                verticalArrangement = Arrangement.spacedBy(Spacing16)
            ) {
                // Basic info
                TagEditorSection(
                    title = "Basic Information",
                    icon = painterResource(R.drawable.ic_audio_file)
                ) {
                    TagEditorField(
                        label = "Title",
                        value = editedMetadata.title,
                        onValueChange = { editedMetadata = editedMetadata.copy(title = it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    TagEditorField(
                        label = "Artist",
                        value = editedMetadata.artist,
                        onValueChange = { editedMetadata = editedMetadata.copy(artist = it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    TagEditorField(
                        label = "Album",
                        value = editedMetadata.album,
                        onValueChange = { editedMetadata = editedMetadata.copy(album = it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Additional info
                TagEditorSection(
                    title = "Additional Information",
                    icon = painterResource(R.drawable.ic_content_copy)
                ) {
                    TagEditorField(
                        label = "Genre",
                        value = editedMetadata.genre,
                        onValueChange = { editedMetadata = editedMetadata.copy(genre = it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    TagEditorField(
                        label = "Year",
                        value = editedMetadata.year,
                        onValueChange = { editedMetadata = editedMetadata.copy(year = it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    TagEditorField(
                        label = "Track Number",
                        value = editedMetadata.trackNumber,
                        onValueChange = { editedMetadata = editedMetadata.copy(trackNumber = it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Bottom padding
                Spacer(modifier = Modifier.height(Spacing24))
            }
        }
    }
}

@Composable
private fun TagEditorHeader(
    songTitle: String,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Edit Tags",
                style = MaterialTheme.typography.headlineSmall,
                color = TextStrong,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = songTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing8)
        ) {
            // Save button
            IconButton(
                onClick = onSave,
                modifier = Modifier
                    .size(40.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Save changes"
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(40.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Close tag editor"
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TagEditorSection(
    title: String,
    icon: Painter,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmberElev1.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing16)
        ) {
            // Section header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = EmberFlame,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(Spacing8))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextStrong,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing12))
            
            // Section content
            content()
        }
    }
}

@Composable
private fun TagEditorField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = AnimationStandard,
        label = "glowAlpha"
    )
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .focusRequester(focusRequester)
                .semantics {
                    role = Role.Button
                    contentDescription = "Edit $label"
                },
        colors = TextFieldDefaults.colors(
            focusedTextColor = TextStrong,
            unfocusedTextColor = TextStrong,
            focusedLabelColor = EmberFlame,
            unfocusedLabelColor = TextMuted,
            focusedContainerColor = EmberCard.copy(alpha = 0.5f),
            unfocusedContainerColor = EmberCard.copy(alpha = 0.3f),
            focusedIndicatorColor = EmberFlame,
            unfocusedIndicatorColor = TextMuted.copy(alpha = 0.5f)
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusRequester.requestFocus() }
        ),
        singleLine = true
    )
    
    // Glow effect
    if (isPressed) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawFieldGlow(
                alpha = glowAlpha
            )
        }
    }
}

private fun DrawScope.drawFieldGlow(
    alpha: Float
) {
    val glowColor = EmberFlame.copy(alpha = alpha * 0.1f)
    
    // Draw subtle glow around the field
    drawRect(
        color = glowColor,
        topLeft = Offset.Zero,
        size = size
    )
    
    // Draw accent line on the left
    drawRect(
        color = EmberFlame.copy(alpha = alpha * 0.3f),
        topLeft = Offset.Zero,
        size = androidx.compose.ui.geometry.Size(
            width = 3.dp.toPx(),
            height = size.height
        )
    )
}

data class SongMetadata(
    val title: String,
    val artist: String,
    val album: String,
    val genre: String,
    val year: String,
    val trackNumber: String
)