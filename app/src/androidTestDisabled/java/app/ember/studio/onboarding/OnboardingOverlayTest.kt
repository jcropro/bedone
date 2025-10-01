package app.ember.studio.onboarding

import android.app.Application
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNode
import androidx.compose.ui.test.waitUntil
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import app.ember.studio.OnboardingTestTags
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingOverlayTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var preferencesRepository: OnboardingPreferencesRepository

    @Before
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        preferencesRepository = OnboardingPreferencesRepository(application.onboardingPreferencesDataStore)
        runBlocking { preferencesRepository.setOnboardingComplete(false) }
        composeRule.waitForIdle()
    }

    @After
    fun tearDown() {
        runBlocking { preferencesRepository.setOnboardingComplete(false) }
    }

    @Test
    fun overlayStaysHiddenOnceCompletionPreferenceIsSet() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(hasTestTag(OnboardingTestTags.OVERLAY)).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNode(hasTestTag(OnboardingTestTags.OVERLAY)).assertExists()

        runBlocking { preferencesRepository.setOnboardingComplete(true) }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(hasTestTag(OnboardingTestTags.OVERLAY)).fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNode(hasTestTag(OnboardingTestTags.OVERLAY)).assertDoesNotExist()

        composeRule.activityRule.scenario.recreate()
        composeRule.waitForIdle()

        composeRule.onNode(hasTestTag(OnboardingTestTags.OVERLAY)).assertDoesNotExist()
    }
}

