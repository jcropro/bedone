package app.ember.studio.onboarding

import android.app.Application
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.waitUntil
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import app.ember.studio.OnboardingTestTags
import app.ember.studio.R
import app.ember.studio.theme.ThemePreferencesRepository
import app.ember.studio.theme.themePreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@RunWith(AndroidJUnit4::class)
class OnboardingThemeStepTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var onboardingRepository: OnboardingPreferencesRepository
    private lateinit var themeRepository: ThemePreferencesRepository

    @Before
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        onboardingRepository = OnboardingPreferencesRepository(application.onboardingPreferencesDataStore)
        themeRepository = ThemePreferencesRepository(application.themePreferencesDataStore)
        runBlocking {
            onboardingRepository.setOnboardingComplete(false)
            themeRepository.setSelectedThemeOption(0)
            themeRepository.setDarkTheme(true)
        }
        composeRule.waitForIdle()
    }

    @After
    fun tearDown() {
        runBlocking {
            onboardingRepository.setOnboardingComplete(false)
            themeRepository.setSelectedThemeOption(0)
            themeRepository.setDarkTheme(true)
        }
    }

    @Test
    fun themeSelectionsPersistAndDarkToggleStayInSync() {
        composeRule.waitForNode(hasTestTag(OnboardingTestTags.OVERLAY))

        val continueLabel = composeRule.activity.getString(R.string.onboarding_continue)
        composeRule.waitForNode(hasText(continueLabel))
        composeRule.onNodeWithText(continueLabel, useUnmergedTree = true).performClick()

        val allowLabel = composeRule.activity.getString(R.string.onboarding_permission_allow)
        composeRule.waitForNode(hasText(allowLabel))
        composeRule.onNodeWithText(allowLabel, useUnmergedTree = true).performClick()

        val skipLabel = composeRule.activity.getString(R.string.onboarding_long_audio_skip)
        composeRule.waitForNode(hasText(skipLabel), timeoutMillis = 30_000)
        composeRule.onNodeWithText(skipLabel, useUnmergedTree = true).performClick()

        composeRule.waitForNode(hasTestTag(themeOptionTag(0)))

        val darkPreviewLabel = composeRule.activity.getString(R.string.onboarding_theme_preview_dark)
        composeRule.onNodeWithTag(OnboardingTestTags.DARK_THEME_SWITCH).assertIsOn()
        composeRule.onNodeWithText(darkPreviewLabel, useUnmergedTree = true).assertExists()

        val secondOptionTag = themeOptionTag(1)
        composeRule.onNodeWithTag(secondOptionTag).performClick()
        composeRule.onNodeWithTag(secondOptionTag).assertIsSelected()

        composeRule.onNodeWithTag(OnboardingTestTags.DARK_THEME_SWITCH).performClick()
        composeRule.onNodeWithTag(OnboardingTestTags.DARK_THEME_SWITCH).assertIsOff()

        val lightPreviewLabel = composeRule.activity.getString(R.string.onboarding_theme_preview_light)
        composeRule.onNodeWithText(lightPreviewLabel, useUnmergedTree = true).assertExists()

        val finishLabel = composeRule.activity.getString(R.string.onboarding_finish)
        composeRule.onNodeWithText(finishLabel, useUnmergedTree = true).performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(hasTestTag(OnboardingTestTags.OVERLAY)).fetchSemanticsNodes().isEmpty()
        }

        val preferences = runBlocking { themeRepository.themePreferences.first() }
        assertEquals(1, preferences.selectedOptionIndex)
        assertFalse(preferences.useDarkTheme)
    }
}

private fun ComposeTestRule.waitForNode(
    matcher: SemanticsMatcher,
    timeoutMillis: Long = 10_000L
) {
    waitUntil(timeoutMillis) {
        onAllNodes(matcher, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
    }
}

private fun themeOptionTag(index: Int): String = "${OnboardingTestTags.THEME_OPTION_PREFIX}$index"

