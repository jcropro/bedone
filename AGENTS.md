# AGENTS.md — EmberAudioPlayer3 (Agent Operating Contract)

## Pins (must match build files)

* AGP **8.12.0**, Gradle **8.13**, JDK **17**
* Kotlin **2.1.20** + `org.jetbrains.kotlin.plugin.compose` **2.1.20**
* Compose BOM **2025.06.00**
* Media3 **1.8.0**
* min / target / compile SDK: **24 / 35 / 35**

## Green Checks Playbook (Android SDK + Gradle) — NO EXCEPTIONS

1. Ensure Android SDK is available and wired:

   * `ANDROID_SDK_ROOT=/opt/android-sdk` (or the container’s path)
   * `ANDROID_HOME=/opt/android-sdk`
   * `PATH += $ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools`
   * Install if missing: `platform-tools`, `platforms;android-35`, `build-tools;35.0.0`
2. Generate `local.properties` (untracked) so Gradle finds the SDK:

   ```properties
   sdk.dir=${ANDROID_SDK_ROOT:-/opt/android-sdk}
   ```
3. `gradle.properties` must include:

   ```
   org.gradle.jvmargs=-Xmx2g -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8
   android.useAndroidX=true
   android.enableJetifier=true
   ```
4. Prove toolchain before edits:

   ```
   tools/ensure-android-sdk.sh
   ./gradlew --version && ./gradlew -q help
   ```

## Session Ritual (MUST DO, IN ORDER)

1. Read-only context: `docs/README.md` (or `docs/PROJECT_BRIEF.md` if present), `docs/MASTER_PROMPT.md` (do not edit).
2. Read/write state: `docs/STATE.yml`, `docs/NEXT.md`.
3. Run `tools/ensure-android-sdk.sh`, then `tools/codex-preflight.sh` (must succeed before any Gradle task).

   * `./gradlew -S :core-ui:compileDebugKotlin :app:assembleDebug`   # REQUIRED gate
   * `./gradlew -S :app:compileReleaseKotlin`                         # ensure release variant keeps compiling
   * `./gradlew -S :app:testDebugUnitTest || echo "Unit tests failed — proceed and open a follow-up Task issue"`  # non-blocking
4. If `assembleDebug` is green, implement the next task from `docs/NEXT.md` (emit full files), then update `STATE.yml/NEXT.md` and open PR with Build Proof.

## Android SDK provisioning & `local.properties`

* `local.properties` is **machine/container-specific** and **MUST NOT** be committed.
* Agents **MUST** create/overwrite it during Setup/Maintenance/CI if missing, to point Gradle at the SDK.
* Ensure `.gitignore` contains `local.properties`.

**Required behavior**

1. If Gradle reports a missing SDK, generate in repo root:

   ```properties
   sdk.dir=${ANDROID_SDK_ROOT:-/opt/android-sdk}
   ```
2. Do **not** add other keys.
3. **Never** include `local.properties` in patches/commits/PRs.
4. Install any additional SDK packages Gradle requests.

## Hard Rules (progress-friendly)

* `local.properties`: never committed; always generated when missing.
* Never set `kotlinCompilerExtensionVersion` in build files (use Compose BOM).
* Emit **complete files**, not diffs; keep packages/namespaces aligned.
* **No conflict markers** (`<<<<<<<`, `=======`, `>>>>>>>`) in repo files.
* Compose UI files must import `Modifier`/`Alignment` from `androidx.compose.ui`, `Composable` from `androidx.compose.runtime`, and `KeyboardOptions` from `androidx.compose.ui.text.input` — never alias foundation variants that cause ambiguous references.
* Use Material3 `HorizontalDivider`/`VerticalDivider` instead of the deprecated `Divider` composable.
* Prefer `Icons.AutoMirrored.Outlined.*` (or equivalent) for directional glyphs; avoid the deprecated `Icons.Outlined` variants that break RTL mirroring and surface build warnings.
* `app/src/main/java/androidx/compose/ui/text/input/KeyboardOptionsAlias.kt` bridges the `KeyboardOptions` symbol into the expected package until Compose exposes it directly; keep this alias or update the contract when upgrading Compose.
* When referencing `SleepTimerUiState` outside the `app.ember.studio.sleep` package, import it with `as SleepTimerState` (or an equivalent alias) and delete duplicate imports so Android Studio never surfaces ambiguous symbol errors again.
* Keep the AndroidTest coverage under `app/src/androidTest/java/app/ember/studio/sleep/` intact; extend it when timer behavior changes rather than disabling tests, since these guard against regressions that break release builds.
* Before invoking `:core-ui:compileDebugKotlin`, `:app:assembleDebug`, `:app:compileReleaseKotlin`, or any test task, rerun `tools/codex-preflight.sh`; fix the environment if it fails.

## Definition of Done

* `:core-ui:testDebugUnitTest` and `:app:assembleDebug` **pass** on Codex Cloud.
* `STATE.yml` / `NEXT.md` updated and committed.
* PR includes Build Proof log and invariants checklist ticked.
