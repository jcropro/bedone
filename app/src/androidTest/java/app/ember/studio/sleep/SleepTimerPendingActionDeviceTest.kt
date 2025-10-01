package app.ember.studio.sleep

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import app.ember.studio.PlayerViewModel
import app.ember.studio.di.PlayerViewModelProvider
import app.ember.studio.testing.TestPlayer
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class SleepTimerPendingActionDeviceTest {

    // Activity is launched explicitly within the test to ensure DI is set first.

    private lateinit var testPlayer: TestPlayer
    private lateinit var sleepRepo: SleepTimerPreferencesRepository

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        sleepRepo = SleepTimerPreferencesRepository(app.sleepTimerPreferencesDataStore)
        // Seed expired stop-after-track timer to schedule pending action on restore
        val now = System.currentTimeMillis()
        runBlocking {
            sleepRepo.updatePreferences(
                SleepTimerPreferences(
                    activeEndTimestampMillis = now - 1_000L,
                    activeFadeEnabled = true,
                    activeEndAction = SleepTimerEndAction.StopAfterTrack,
                    activeOriginalVolume = 0.8f,
                    endAction = SleepTimerEndAction.StopAfterTrack
                )
            )
        }

        PlayerViewModelProvider.dependenciesFactory = { application ->
            testPlayer = TestPlayer()
            PlayerViewModel.Dependencies(
                playerFactory = { _ -> testPlayer }
            )
        }
    }

    @After
    fun tearDown() {
        PlayerViewModelProvider.clear()
    }

    @Test
    fun expiredStopAfterTrack_isAppliedAndClearedAfterPlaybackEnds() {
        ActivityScenario.launch(MainActivity::class.java).use {

            // Pending action should be scheduled; simulate track end
            testPlayer.emitPlaybackEnded()

            // Verify stop invoked and pending action cleared
            assertEquals(1, testPlayer.stopCount)

            val prefs = runBlocking { sleepRepo.preferences.first() }
            assertEquals(SleepTimerPendingActionType.None, prefs.pendingAction.type)
        }
    }
}
