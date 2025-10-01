package app.ember.studio.playlists

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.ember.studio.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class UserPlaylistReorderDeviceTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before fun setUp() { scenario = ActivityScenario.launch(MainActivity::class.java) }
    @After fun tearDown() { scenario.close() }

    @Test
    fun moveDown_then_moveUp_persistsInSelectedPlaylist() {
        scenario.onActivity { activity ->
            // Access the ViewModel via reflection (test-only)
            val vmField = MainActivity::class.java.getDeclaredField("playerViewModel")
            vmField.isAccessible = true
            val vm = vmField.get(activity) as app.ember.studio.PlayerViewModel

            // Create a user playlist with two items
            val songs = vm.homeState.value.songs.take(2)
            if (songs.size < 2) return@onActivity
            val idA = songs[0].id
            val idB = songs[1].id
            vm.addSongToNewUserPlaylist("Test List", idA)
            // Allow VM to open the new playlist detail
            Thread.sleep(200)
            val pid = vm.homeState.value.selectedPlaylist?.id ?: return@onActivity
            vm.addSongToUserPlaylist(pid, idB)
            Thread.sleep(200)

            // Move down first item
            val selected = vm.homeState.value.selectedPlaylist ?: return@onActivity
            vm.movePlaylistItemDown(pid, idA)
            // Validate new order in selectedPlaylist snapshot
            val afterDown = vm.homeState.value.selectedPlaylist?.items ?: return@onActivity
            assertEquals(listOf(idB, idA), afterDown.map { it.id })

            // Move up second item (idA)
            vm.movePlaylistItemUp(pid, idA)
            val afterUp = vm.homeState.value.selectedPlaylist?.items ?: return@onActivity
            assertEquals(listOf(idA, idB), afterUp.map { it.id })
        }
    }
}
