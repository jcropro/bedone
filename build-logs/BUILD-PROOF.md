# Build Proof — 2025-09-26

Environment:
- ANDROID_SDK_ROOT=$HOME/android-sdk
- Gradle 8.13, AGP 8.12.0, JDK 17, Kotlin 2.1.20, Compose BOM 2025.06.00

Commands run:
- tools/codex-preflight.sh
- ./gradlew -S :core-ui:compileDebugKotlin :app:assembleDebug
- ./gradlew -S :app:compileReleaseKotlin
- ./gradlew -S :app:testDebugUnitTest

Results:
- :core-ui:compileDebugKotlin — UP-TO-DATE
- :app:assembleDebug — BUILD SUCCESSFUL
- :app:compileReleaseKotlin — BUILD SUCCESSFUL
- :app:testDebugUnitTest — BUILD SUCCESSFUL

Notes:
- Fixed onboarding crash by declaring READ_MEDIA_AUDIO and legacy READ_EXTERNAL_STORAGE (maxSdkVersion 32) in AndroidManifest.
