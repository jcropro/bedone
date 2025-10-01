package app.ember.studio

import java.util.Locale
import kotlin.math.abs

internal const val PLAYBACK_SPEED_TOLERANCE = 0.001f

fun Float.toPlaybackSpeedLabel(): String {
    val value = this
    val integerPortion = value.toInt()
    val numeric = if (abs(value - integerPortion) < PLAYBACK_SPEED_TOLERANCE) {
        integerPortion.toString()
    } else {
        String.format(Locale.US, "%.2f", value).trimEnd('0').trimEnd('.')
    }
    return "$numeric" + "x"
}
