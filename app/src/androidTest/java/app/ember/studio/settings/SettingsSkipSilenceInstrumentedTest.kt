package app.ember.studio.settings

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.core.app.ActivityScenario
import app.ember.studio.MainActivity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SettingsSkipSilenceInstrumentedTest {

    @Test
    fun togglingSkipSilence_updatesExoPlayerFlag() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val field = MainActivity::class.java.getDeclaredField("playerViewModel")
            field.isAccessible = true
            val vm = field.get(activity) as app.ember.studio.PlayerViewModel
            val exo = vm.exoPlayerOrNull()
            // ExoPlayer should be available in activity context
            requireNotNull(exo)
            assertFalse(exo.skipSilenceEnabled)
            vm.setSkipSilenceEnabled(true)
            assertTrue(exo.skipSilenceEnabled)
            vm.setSkipSilenceEnabled(false)
            assertFalse(exo.skipSilenceEnabled)
        }
        scenario.close()
    }
}

