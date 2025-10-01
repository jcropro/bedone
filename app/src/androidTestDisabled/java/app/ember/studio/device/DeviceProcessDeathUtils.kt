package app.ember.studio.device

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice

object DeviceProcessDeathUtils {
    private const val PACKAGE = "app.ember.studio"

    fun forceStopApp() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.executeShellCommand("am force-stop $PACKAGE")
    }

    fun relaunchMainActivity() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE)
            ?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        if (intent != null) {
            context.startActivity(intent)
        }
    }
}

