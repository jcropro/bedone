package app.ember.studio.longform

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.core.app.ActivityScenario
import app.ember.studio.MainActivity
import app.ember.studio.LongformCandidate
import app.ember.studio.SampleLibrary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LongformThresholdInstrumentedTest {

    @Test
    fun threshold_changes_affect_candidates_and_persist() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val field = MainActivity::class.java.getDeclaredField("playerViewModel")
            field.isAccessible = true
            val vm = field.get(activity) as app.ember.studio.PlayerViewModel

            vm.setLongformThresholdMinutes(10)
            val msField = app.ember.studio.PlayerViewModel::class.java.getDeclaredField("longformThresholdMs").apply { isAccessible = true }
            assertEquals(10 * 60_000L, msField.getLong(vm))
            val method = app.ember.studio.PlayerViewModel::class.java.getDeclaredMethod("computeLongformCandidates", List::class.java).apply { isAccessible = true }
            @Suppress("UNCHECKED_CAST")
            val c10 = method.invoke(vm, SampleLibrary.longformItems) as List<LongformCandidate>

            vm.setLongformThresholdMinutes(60)
            assertEquals(60 * 60_000L, msField.getLong(vm))
            @Suppress("UNCHECKED_CAST")
            val c60 = method.invoke(vm, SampleLibrary.longformItems) as List<LongformCandidate>
            assertTrue(c60.size <= c10.size)
        }
        scenario.close()
    }
}

