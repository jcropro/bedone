package app.ember.studio.testing

import app.ember.studio.MainActivity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice

fun seedMultiItemQueue(activity: MainActivity) {
    try {
        val vmField = MainActivity::class.java.getDeclaredField("playerViewModel")
        vmField.isAccessible = true
        val vm = vmField.get(activity) as app.ember.studio.PlayerViewModel
        vm.playAllSongs()
    } catch (_: Throwable) {
        // best-effort
    }
}

fun takeScreenshot(name: String) {
    try {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val file = java.io.File(ctx.filesDir, "$name.png")
        device.takeScreenshot(file)
    } catch (_: Throwable) { }
}
