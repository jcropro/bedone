# Build Proof — 2025-09-28

Environment:
- ANDROID_SDK_ROOT=$HOME/android-sdk
- PATH+= $HOME/android-sdk/cmdline-tools/latest/bin:$HOME/android-sdk/platform-tools
- Gradle 8.13, AGP 8.12.0, JDK 17, Kotlin 2.1.20, Compose BOM 2025.06.00

Commands run:
- tools/ensure-android-sdk.sh (with ANDROID_SDK_ROOT/ANDROID_HOME=$HOME/android-sdk)
- tools/codex-preflight.sh
- ./gradlew --version && ./gradlew -q help
- ./gradlew -S :core-ui:compileDebugKotlin :app:assembleDebug
- ./gradlew -S :app:compileReleaseKotlin
- ./gradlew -S :app:testDebugUnitTest
- ./gradlew -S :core-ui:testDebugUnitTest

Results:
- :core-ui:compileDebugKotlin — UP-TO-DATE
- :app:assembleDebug — BUILD SUCCESSFUL
- :app:compileReleaseKotlin — BUILD SUCCESSFUL
- :app:testDebugUnitTest — BUILD SUCCESSFUL
- :core-ui:testDebugUnitTest — BUILD SUCCESSFUL

Invariants:
- local.properties not committed; generated to point at $HOME/android-sdk
- gradle.properties includes jvmargs, AndroidX, Jetifier per contract
- Pins: AGP 8.12.0, Gradle 8.13, JDK 17, Kotlin 2.1.20, Compose BOM 2025.06.00, Media3 1.8.0

