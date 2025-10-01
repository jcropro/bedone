package app.ember.studio.util

fun formatDuration(valueMs: Long): String {
    if (valueMs <= 0L) return "0:00"
    val totalSeconds = valueMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%d:%02d".format(minutes, seconds)
    }
}
