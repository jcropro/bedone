# MASTER_PROMPT.md — Runbook (Codex Cloud)

You run inside a Codex Cloud container. Your first duty is to **make the build pass**.
- Set `ANDROID_SDK_ROOT`/`ANDROID_HOME` to the container SDK path (default `/opt/android-sdk`).
- PATH must include `$ANDROID_SDK_ROOT/cmdline-tools/latest/bin` and `$ANDROID_SDK_ROOT/platform-tools`.
- If packages are missing, install with `sdkmanager`: `platform-tools`, `platforms;android-35`, `build-tools;35.0.0`.
- Generate `local.properties` in the repo root (untracked):
  ```
  sdk.dir=${ANDROID_SDK_ROOT:-/opt/android-sdk}
  ```

Build sequence:
1) `tools/ensure-android-sdk.sh`
2) `./gradlew --version && ./gradlew -q help`
3) `./gradlew -S :core-ui:testDebugUnitTest`
4) `./gradlew -S :core-ui:compileDebugKotlin :app:assembleDebug`

Policy:
- `local.properties` must exist for builds and must **not** be committed.
- Never output git conflict markers. Emit **final** file contents only.
