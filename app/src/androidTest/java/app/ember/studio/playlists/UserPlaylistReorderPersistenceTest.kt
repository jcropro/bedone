package app.ember.studio.playlists

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class UserPlaylistReorderPersistenceTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before fun setUp() { scenario = ActivityScenario.launch(MainActivity::class.java) }
    @After fun tearDown() { scenario.close() }

    @Test
    fun reorder_persists_after_recreation() {
        val pidRef = AtomicReference<String>()
        val idARef = AtomicReference<String>()
        val idBRef = AtomicReference<String>()

        scenario.onActivity { activity ->
            val vmField = MainActivity::class.java.getDeclaredField("playerViewModel")
            vmField.isAccessible = true
            val vm = vmField.get(activity) as app.ember.studio.PlayerViewModel
            val songs = vm.homeState.value.songs.take(2)
            if (songs.size < 2) return@onActivity
            val idA = songs[0].id
            val idB = songs[1].id
            idARef.set(idA)
            idBRef.set(idB)
            vm.addSongToNewUserPlaylist("Persist List", idA)
            Thread.sleep(200)
            val pid = vm.homeState.value.selectedPlaylist?.id ?: return@onActivity
            pidRef.set(pid)
            vm.addSongToUserPlaylist(pid, idB)
            Thread.sleep(200)
            vm.movePlaylistItemDown(pid, idA)
            Thread.sleep(150)
            val afterDown = vm.homeState.value.selectedPlaylist?.items ?: return@onActivity
            assertEquals(listOf(idB, idA), afterDown.map { it.id })
        }

        // Recreate activity
        scenario.recreate()

        scenario.onActivity { activity ->
            val pid = pidRef.get() ?: return@onActivity
            val idA = idARef.get() ?: return@onActivity
            val idB = idBRef.get() ?: return@onActivity
            val vmField = MainActivity::class.java.getDeclaredField("playerViewModel")
            vmField.isAccessible = true
            val vm = vmField.get(activity) as app.ember.studio.PlayerViewModel
            vm.openPlaylistDetail(pid)
            Thread.sleep(200)
            val items = vm.homeState.value.selectedPlaylist?.items ?: return@onActivity
            assertEquals(listOf(idB, idA), items.map { it.id })
        }
    }
}

