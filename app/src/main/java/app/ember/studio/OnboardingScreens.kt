package app.ember.studio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import app.ember.core.ui.design.EasingStandard
import app.ember.core.ui.design.EasingDecel
import app.ember.core.ui.design.AnimationTransition
import app.ember.core.ui.design.AnimationReveal
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.ember.core.ui.design.*
import app.ember.core.ui.theme.ThemeUiState
import app.ember.core.ui.theme.ThemeOption
import app.ember.studio.util.formatDuration
import kotlinx.coroutines.delay

object OnboardingTestTags {
    const val OVERLAY = "onboardingOverlay"
    const val THEME_OPTION_PREFIX = "onboardingThemeOption-"
    const val DARK_THEME_SWITCH = "onboardingDarkThemeSwitch"
}

@Composable
fun OnboardingOverlay(
    state: OnboardingUiState,
    themeState: ThemeUiState,
    onWelcomeContinue: () -> Unit,
    onRequestPermission: () -> Unit,
    onChooseFolders: () -> Unit,
    onAssignAllLongform: (LongformCategory) -> Unit,
    onChooseIndividually: () -> Unit,
    onSelectionChange: (String, LongformCategory) -> Unit,
    onApplySelection: () -> Unit,
    onSkip: () -> Unit,
    onUndo: () -> Unit,
    onDismissMessage: () -> Unit,
    onComplete: () -> Unit,
    onSelectThemeOption: (Int) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val actionLabel = if (state.canUndoLongformChange) {
        appString(R.string.onboarding_undo)
    } else {
        null
    }

    val statusMessage = state.statusMessage
    LaunchedEffect(statusMessage, actionLabel) {
        if (statusMessage != null) {
            val result = snackbarHostState.showSnackbar(
                message = statusMessage,
                actionLabel = actionLabel
            )
            if (result == SnackbarResult.ActionPerformed && state.canUndoLongformChange) {
                onUndo()
            }
            onDismissMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize().testTag(OnboardingTestTags.OVERLAY)) {
        AnimatedOnboardingBackground()
        // Subtle scrim to ensure readability on top of gradient
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)))

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (state.step) {
                    OnboardingStep.Welcome -> WelcomeStep(onContinue = onWelcomeContinue)
                    OnboardingStep.Permission -> PermissionStep(
                        state = state.permissionState,
                        onRequestPermission = onRequestPermission,
                        onChooseFolders = onChooseFolders
                    )
                    OnboardingStep.LongAudio -> LongAudioStep(
                        state = state.longformState,
                        onAssignAllLongform = onAssignAllLongform,
                        onChooseIndividually = onChooseIndividually,
                        onSelectionChange = onSelectionChange,
                        onApplySelection = onApplySelection,
                        onSkip = onSkip
                    )
                    OnboardingStep.Theme -> ThemeStep(
                        themeState = themeState,
                        onSelectThemeOption = onSelectThemeOption,
                        onToggleDarkTheme = onToggleDarkTheme,
                        onFinish = onComplete
                    )
                    OnboardingStep.Complete -> {}
                }
            }
        }
    }
}

@Composable
private fun AnimatedOnboardingBackground() {
    // Using canonical tokens for premium gradient
    val colors = listOf(
        EmberFlame.copy(alpha = 0.55f),
        EmberFlameGlow.copy(alpha = 0.45f),
        AccentIce.copy(alpha = 0.40f),
        EmberFlame.copy(alpha = 0.55f)
    )
    val transition = rememberInfiniteTransition(label = "onboardingGradient")
    val shift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = EasingStandard),
            repeatMode = RepeatMode.Reverse
        ),
        label = "onboardingGradientShift"
    )
    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, shift.value / 3f),
        end = Offset(shift.value, shift.value / 2f)
    )
    Box(modifier = Modifier.fillMaxSize().background(brush))
}

@Composable
private fun WelcomeStep(onContinue: () -> Unit) {
    var hasAdvanced by remember { mutableStateOf(false) }
    var showContinue by remember { mutableStateOf(false) }
    val scale = remember { Animatable(0.96f) }
    val glow = remember { Animatable(0.18f) }
    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(Unit) {
        delay(800)
        showContinue = true
    }

    LaunchedEffect(hasAdvanced) {
        if (!hasAdvanced) {
            delay(3000)
            if (!hasAdvanced) {
                hasAdvanced = true
                onContinue()
            }
        }
    }

    LaunchedEffect(Unit) {
        repeat(2) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = AnimationTransition
            )
            glow.animateTo(
                targetValue = 0.35f,
                animationSpec = AnimationTransition
            )
            scale.animateTo(
                targetValue = 0.96f,
                animationSpec = AnimationReveal
            )
            glow.animateTo(
                targetValue = 0.18f,
                animationSpec = AnimationReveal
            )
        }
        scale.animateTo(
            targetValue = 1f,
            animationSpec = AnimationTransition
        )
        glow.animateTo(
            targetValue = 0.22f,
            animationSpec = AnimationReveal
        )
    }

    fun advance() {
        if (!hasAdvanced) {
            hasAdvanced = true
            onContinue()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .drawBehind {
                        drawCircle(
                            color = primaryColor.copy(alpha = glow.value),
                            radius = size.minDimension / 2f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale.value
                            scaleY = scale.value
                        }
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LibraryMusic,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = appString(R.string.onboarding_brand_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = appString(R.string.onboarding_brand_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        AnimatedVisibility(
            visible = showContinue,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Button(onClick = ::advance, modifier = Modifier.fillMaxWidth()) {
                Text(text = appString(R.string.onboarding_continue))
            }
        }
    }
}

@Composable
private fun PermissionStep(
    state: PermissionStepState,
    onRequestPermission: () -> Unit,
    onChooseFolders: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = appString(R.string.onboarding_permission_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = appString(R.string.onboarding_permission_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            val labelRes = if (state.errorMessage != null) {
                R.string.onboarding_permission_retry
            } else {
                R.string.onboarding_permission_allow
            }
            Text(text = appString(labelRes))
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onChooseFolders,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = appString(R.string.onboarding_permission_choose_folders))
        }
        if (state.selectedFolderCount > 0) {
            val folderCountText = if (state.selectedFolderCount == 1) {
                appString(R.string.onboarding_permission_selected_folder_single)
            } else {
                appString(
                    R.string.onboarding_permission_selected_folder_plural,
                    state.selectedFolderCount
                )
            }
            Text(
                text = folderCountText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (state.isScanning) {
            Spacer(modifier = Modifier.height(24.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            val total = state.totalItemCount.coerceAtLeast(1)
            val scanned = state.scannedItemCount.coerceAtMost(total)
            Text(
                text = appString(
                    R.string.onboarding_scanning_progress,
                    scanned,
                    total
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun LongAudioStep(
    state: LongformStepState,
    onAssignAllLongform: (LongformCategory) -> Unit,
    onChooseIndividually: () -> Unit,
    onSelectionChange: (String, LongformCategory) -> Unit,
    onApplySelection: () -> Unit,
    onSkip: () -> Unit
) {
    when (state.mode) {
        LongformMode.Overview -> LongAudioOverview(
            state = state,
            onAssignAllLongform = onAssignAllLongform,
            onChooseIndividually = onChooseIndividually,
            onSkip = onSkip
        )
        LongformMode.Chooser -> LongAudioChooser(
            state = state,
            onSelectionChange = onSelectionChange,
            onApplySelection = onApplySelection,
            onSkip = onSkip
        )
    }
}

@Composable
private fun LongAudioOverview(
    state: LongformStepState,
    onAssignAllLongform: (LongformCategory) -> Unit,
    onChooseIndividually: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = appString(R.string.onboarding_long_audio_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = appString(R.string.onboarding_long_audio_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = appString(R.string.onboarding_long_audio_count, state.itemCount),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        FilledTonalButton(
            onClick = { onAssignAllLongform(LongformCategory.Audiobook) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = appString(R.string.onboarding_long_audio_import_audiobooks))
        }
        Spacer(modifier = Modifier.height(12.dp))
        FilledTonalButton(
            onClick = { onAssignAllLongform(LongformCategory.Podcast) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = appString(R.string.onboarding_long_audio_import_podcasts))
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onChooseIndividually,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = appString(R.string.onboarding_long_audio_choose_individually))
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = appString(R.string.onboarding_long_audio_skip))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = appString(R.string.onboarding_long_audio_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LongAudioChooser(
    state: LongformStepState,
    onSelectionChange: (String, LongformCategory) -> Unit,
    onApplySelection: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = appString(R.string.onboarding_long_audio_sort_header, state.itemCount),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )
        Text(
            text = appString(R.string.onboarding_long_audio_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(state.candidates) { candidate ->
                LongformCandidateRow(candidate = candidate, onSelectionChange = onSelectionChange)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        Button(onClick = onApplySelection, modifier = Modifier.fillMaxWidth()) {
            Text(text = appString(R.string.onboarding_long_audio_apply))
        }
        OutlinedButton(
            onClick = onSkip,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 24.dp)
        ) {
            Text(text = appString(R.string.onboarding_long_audio_skip))
        }
    }
}

@Composable
private fun LongformCandidateRow(
    candidate: LongformCandidate,
    onSelectionChange: (String, LongformCategory) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = candidate.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = candidate.source,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = formatDuration(candidate.durationMs),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryChip(
                    label = appString(R.string.onboarding_long_audio_music),
                    selected = candidate.selectedCategory == LongformCategory.Unassigned,
                    onClick = { onSelectionChange(candidate.id, LongformCategory.Unassigned) }
                )
                CategoryChip(
                    label = appString(R.string.onboarding_long_audio_podcast),
                    selected = candidate.selectedCategory == LongformCategory.Podcast,
                    onClick = { onSelectionChange(candidate.id, LongformCategory.Podcast) }
                )
                CategoryChip(
                    label = appString(R.string.onboarding_long_audio_audiobook),
                    selected = candidate.selectedCategory == LongformCategory.Audiobook,
                    onClick = { onSelectionChange(candidate.id, LongformCategory.Audiobook) }
                )
            }
            if (candidate.suggestedCategory != candidate.selectedCategory) {
                Text(
                    text = appString(
                        R.string.onboarding_long_audio_suggested,
                        suggestionLabel(candidate.suggestedCategory)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            null
        }
    )
}

@Composable
private fun ThemeStep(
    themeState: ThemeUiState,
    onSelectThemeOption: (Int) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = appString(R.string.onboarding_theme_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = appString(R.string.onboarding_theme_hint),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        themeState.options.forEachIndexed { index, option ->
            ThemeOptionCard(
                modifier = Modifier.testTag("${OnboardingTestTags.THEME_OPTION_PREFIX}$index"),
                label = appString(option.labelRes),
                option = option,
                selected = index == themeState.selectedOptionIndex,
                useDarkTheme = themeState.useDarkTheme,
                onSelect = { onSelectThemeOption(index) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = appString(R.string.onboarding_theme_dark_toggle),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                modifier = Modifier.testTag(OnboardingTestTags.DARK_THEME_SWITCH),
                checked = themeState.useDarkTheme,
                onCheckedChange = onToggleDarkTheme,
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
            )
        }
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
            Text(text = appString(R.string.onboarding_finish))
        }
    }
}

@Composable
private fun ThemeOptionCard(
    modifier: Modifier = Modifier,
    label: String,
    option: ThemeOption,
    selected: Boolean,
    useDarkTheme: Boolean,
    onSelect: () -> Unit
) {
    val previewScheme = option.colorScheme(useDarkTheme)
    val cardColors = if (selected) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
        )
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                this.selected = selected
                role = Role.RadioButton
            },
        onClick = onSelect,
        colors = cardColors,
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(previewScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = previewScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    val modeLabel = if (useDarkTheme) {
                        appString(R.string.onboarding_theme_preview_dark)
                    } else {
                        appString(R.string.onboarding_theme_preview_light)
                    }
                    Text(
                        text = modeLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                RadioButton(selected = selected, onClick = onSelect)
            }
            Spacer(modifier = Modifier.height(16.dp))
            ThemeOptionPreview(colorScheme = previewScheme)
        }
    }
}

@Composable
private fun ThemeOptionPreview(colorScheme: ColorScheme) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colorScheme.surface)
            .border(
                width = 1.dp,
                color = colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(colorScheme.primary)
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Box(
                modifier = Modifier
                    .height(10.dp)
                    .fillMaxWidth(0.45f)
                    .clip(RoundedCornerShape(5.dp))
                    .background(colorScheme.primary)
            )
            Spacer(modifier = Modifier.height(12.dp))
            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorScheme.onSurface.copy(alpha = 0.12f))
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                ThemeColorSwatch(color = colorScheme.primary, borderColor = colorScheme.onSurface)
                ThemeColorSwatch(color = colorScheme.secondary, borderColor = colorScheme.onSurface)
                ThemeColorSwatch(color = colorScheme.tertiary, borderColor = colorScheme.onSurface)
                ThemeColorSwatch(color = colorScheme.surfaceVariant, borderColor = colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun ThemeColorSwatch(color: Color, borderColor: Color) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(color)
            .border(width = 1.dp, color = borderColor.copy(alpha = 0.18f), shape = CircleShape)
    )
}

@Composable
private fun appString(resId: Int, vararg formatArgs: Any): String {
    return androidx.compose.ui.res.stringResource(id = resId, formatArgs = formatArgs)
}

@Composable
private fun suggestionLabel(category: LongformCategory): String = when (category) {
    LongformCategory.Podcast -> appString(R.string.onboarding_long_audio_podcast)
    LongformCategory.Audiobook -> appString(R.string.onboarding_long_audio_audiobook)
    LongformCategory.Unassigned -> appString(R.string.onboarding_long_audio_music)
}

/**
 * OnboardingOverlay - Main onboarding screen that manages the entire onboarding flow
 */
@Composable
fun OnboardingOverlay(
    state: OnboardingUiState,
    themeState: app.ember.core.ui.theme.ThemeUiState,
    onWelcomeContinue: () -> Unit,
    onRequestPermission: () -> Unit,
    onChooseFolders: () -> Unit,
    onAssignAllLongform: (LongformCategory) -> Unit,
    onChooseIndividually: () -> Unit,
    onSelectionChange: (String, LongformCategory) -> Unit,
    onApplySelection: () -> Unit,
    onSkip: () -> Unit,
    onSelectThemeOption: (Int) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onComplete: () -> Unit,
    onUndo: () -> Unit,
    onDismissMessage: () -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    // Simplified onboarding overlay - just show a placeholder for now
    // Full implementation should be restored from the original OnboardingScreens.kt
    androidx.compose.material3.Surface(
        modifier = modifier.fillMaxSize(),
        color = app.ember.core.ui.design.EmberInk
    ) {
        Box(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "Welcome to Ember",
                    style = MaterialTheme.typography.headlineMedium,
                    color = app.ember.core.ui.design.TextStrong,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                
                androidx.compose.material3.Button(
                    onClick = onComplete
                ) {
                    androidx.compose.material3.Text("Get Started")
                }
            }
        }
    }
}
