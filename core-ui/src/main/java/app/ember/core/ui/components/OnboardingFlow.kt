package app.ember.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.ember.core.ui.theme.EmberTheme
import kotlinx.coroutines.delay

/**
 * Enhanced onboarding flow with premium visual design
 */
@Composable
fun OnboardingFlow(
    modifier: Modifier = Modifier,
    currentStep: OnboardingStep,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Premium gradient background
        OnboardingBackground()
        
        // Content overlay
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Progress indicator
                OnboardingProgress(
                    currentStep = currentStep,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
                
                // Main content
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    content()
                }
                
                // Navigation controls
                OnboardingNavigation(
                    currentStep = currentStep,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    onComplete = onComplete,
                    onSkip = onSkip,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
}

@Composable
private fun OnboardingBackground() {
    val gradient = Brush.radialGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
            Color.Transparent
        ),
        radius = 800f
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    )
}

@Composable
private fun OnboardingProgress(
    currentStep: OnboardingStep,
    modifier: Modifier = Modifier
) {
    val steps = OnboardingStep.values().filter { it != OnboardingStep.Complete }
    val currentIndex = steps.indexOf(currentStep)
    val progress = if (currentIndex >= 0) (currentIndex + 1).toFloat() / steps.size else 0f
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Step ${currentIndex + 1} of ${steps.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = currentStep.displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun OnboardingNavigation(
    currentStep: OnboardingStep,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = OnboardingStep.values().filter { it != OnboardingStep.Complete }
    val currentIndex = steps.indexOf(currentStep)
    val isFirstStep = currentIndex == 0
    val isLastStep = currentIndex == steps.lastIndex
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous button
        if (!isFirstStep) {
            TextButton(onClick = onPrevious) {
                Text("Previous")
            }
        } else {
            Spacer(modifier = Modifier.width(80.dp))
        }
        
        // Skip button (not on last step)
        if (!isLastStep) {
            TextButton(onClick = onSkip) {
                Text("Skip")
            }
        } else {
            Spacer(modifier = Modifier.width(80.dp))
        }
        
        // Next/Complete button
        Button(
            onClick = if (isLastStep) onComplete else onNext,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(if (isLastStep) "Get Started" else "Next")
        }
    }
}

/**
 * Welcome step with animated logo and premium design
 */
@Composable
fun OnboardingWelcomeStep(
    modifier: Modifier = Modifier,
    onContinue: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    val logoScale = remember { Animatable(0.8f) }
    val logoGlow = remember { Animatable(0.1f) }
    
    LaunchedEffect(Unit) {
        delay(500)
        showContent = true
        
        // Animate logo
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        logoGlow.animateTo(
            targetValue = 0.3f,
            animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing)
        )
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Animated logo
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = logoGlow.value)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔥",
                            style = MaterialTheme.typography.displayLarge
                        )
                    }
                }
                
                // Welcome text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Welcome to Ember",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Your premium music experience awaits",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Discover your music library with beautiful visuals, intelligent features, and seamless playback.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                
                // Feature highlights
                OnboardingFeatureHighlights()
            }
        }
    }
}

@Composable
private fun OnboardingFeatureHighlights() {
    val features = listOf(
        "🎵" to "All audio formats supported",
        "🎨" to "Dynamic visual themes",
        "🎧" to "Professional equalizer",
        "⏰" to "Smart sleep timer",
        "📱" to "Seamless integration"
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        features.forEach { (icon, text) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Theme selection step with enhanced visual design
 */
@Composable
fun OnboardingThemeStep(
    modifier: Modifier = Modifier,
    selectedThemeIndex: Int,
    isDarkTheme: Boolean,
    onThemeSelected: (Int) -> Unit,
    onDarkThemeToggled: (Boolean) -> Unit,
    onContinue: () -> Unit
) {
    val themes = listOf(
        "Ember Orange" to "Warm and energetic",
        "Ocean Blue" to "Calm and peaceful", 
        "Forest Green" to "Natural and fresh",
        "Sunset Purple" to "Creative and inspiring"
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Choose Your Theme",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Personalize your music experience with beautiful themes",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        
        // Theme options
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            themes.forEachIndexed { index, (name, description) ->
                ThemeOptionCard(
                    name = name,
                    description = description,
                    isSelected = selectedThemeIndex == index,
                    onClick = { onThemeSelected(index) }
                )
            }
        }
        
        // Dark theme toggle
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Dark Mode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Switch between light and dark themes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                RadioButton(
                    selected = isDarkTheme,
                    onClick = { onDarkThemeToggled(!isDarkTheme) }
                )
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    name: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

enum class OnboardingStep(val displayName: String) {
    Welcome("Welcome"),
    Permission("Permissions"),
    Theme("Theme"),
    Complete("Complete")
}

@Preview(showBackground = true)
@Composable
private fun OnboardingFlowPreview() {
    EmberTheme {
        OnboardingFlow(
            currentStep = OnboardingStep.Welcome,
            onNext = {},
            onPrevious = {},
            onComplete = {},
            onSkip = {}
        ) {
            OnboardingWelcomeStep(onContinue = {})
        }
    }
}
