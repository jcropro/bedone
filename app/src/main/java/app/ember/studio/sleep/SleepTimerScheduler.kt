package app.ember.studio.sleep

interface SleepTimerScheduler {
    fun schedule(endTimestampMillis: Long, fadeEnabled: Boolean, endAction: SleepTimerEndAction, originalVolume: Float?)
    fun cancel()
}

