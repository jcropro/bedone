package app.ember.studio.playback

import android.app.Application
import androidx.media3.common.util.UnstableApi
import androidx.media3.test.utils.FakePlayer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class PlaybackSpeedPersistenceTest {

    @OptIn(UnstableApi::class)
    @Test
    fun speedPersistsAcrossViewModelRecreation() = runTest {
        val application: Application = ApplicationProvider.getApplicationContext()
        val repo = PlaybackPreferencesRepository(application.playbackPreferencesDataStore)

        val deps1 = PlayerViewModel.Dependencies(
            playerFactory = { FakePlayer() },
            playbackPreferencesRepository = repo,
            ioDispatcher = Dispatchers.Main
        )
        val vm1 = PlayerViewModel(application, deps1)
        try {
            vm1.setPlaybackSpeed(1.5f)
            assertEquals(1.5f, vm1.uiState.value.playbackSpeed)
        } finally {
            vm1.onCleared()
        }

        // Recreate with same repository (same DataStore)
        val deps2 = PlayerViewModel.Dependencies(
            playerFactory = { FakePlayer() },
            playbackPreferencesRepository = repo,
            ioDispatcher = Dispatchers.Main
        )
        val vm2 = PlayerViewModel(application, deps2)
        try {
            // Allow observer to update state
            assertEquals(1.5f, vm2.uiState.value.playbackSpeed)
        } finally {
            vm2.onCleared()
        }
    }
}

