#!/usr/bin/env bash
set -euo pipefail

fail() { echo "SANITY FAIL: $*" >&2; exit 1; }
ok()   { echo "SANITY OK: $*"; }

REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
cd "$REPO_ROOT"

# --- 0) Ensure Gradle sees the Android SDK (prevents Codex Cloud loops) ---
# Creates local.properties if missing, pointing to $ANDROID_SDK_ROOT (or /opt/android-sdk)
if [ -x tools/ensure-android-sdk.sh ]; then
  tools/ensure-android-sdk.sh
else
  SDK="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-/opt/android-sdk}}"
  [ -f local.properties ] || printf 'sdk.dir=%s\n' "$SDK" > local.properties
fi

# --- 1) No conflict markers anywhere (classic merge-loop killer) ---
if git grep -nE '^(<<<<<<<|=======|>>>>>>>)' -- ':!tools/sanity.sh' >/dev/null; then
  git grep -nE '^(<<<<<<<|=======|>>>>>>>)' || true
  fail "Conflict markers present in repo files; resolve and commit clean copies."
fi

# --- 2) local.properties must NOT be tracked ---
git ls-files --error-unmatch local.properties >/dev/null 2>&1 && \
  fail "local.properties is tracked by git; run: git rm --cached local.properties"

# --- 3) gradle.properties exists and has memory flags ---
[ -f gradle.properties ] || fail "gradle.properties missing"
grep -q 'org.gradle.jvmargs' gradle.properties || \
  fail "gradle.properties missing org.gradle.jvmargs"

# --- 4) Do not hard-code kotlinCompilerExtensionVersion anywhere ---
if grep -R --line-number --fixed-strings --include='*.gradle' --include='*.gradle.kts' --include='*.kts' --include='*.kt' \
  'kotlinCompilerExtensionVersion' . >/dev/null; then
  grep -R --line-number --fixed-strings --include='*.gradle' --include='*.gradle.kts' --include='*.kts' --include='*.kt' \
    'kotlinCompilerExtensionVersion' . || true
  fail "Do not set kotlinCompilerExtensionVersion; rely on Kotlin Compose plugin / BOM."
fi

# --- 5) Theme/typography quick corruption tripwire (matches your original intent) ---
if grep -R --line-number -E '\b(Brightness|palette|codex|cvrj48)\b' \
  -- core-ui/src/main/java/app/ember/core/ui/theme >/dev/null 2>&1; then
  grep -R --line-number -E '\b(Brightness|palette|codex|cvrj48)\b' \
    -- core-ui/src/main/java/app/ember/core/ui/theme || true
  fail "Theme files contain unexpected tokens; likely merge debris or corruption."
fi

# --- 6) Light SDK path sanity (non-fatal guidance unless completely bogus) ---
SDK_DIR="$(awk -F= '/^sdk.dir=/{print $2}' local.properties || true)"
if [ -z "${SDK_DIR:-}" ]; then
  echo "NOTE: sdk.dir not found in local.properties (file was created, but is empty?)."
elif [ ! -d "$SDK_DIR" ]; then
  echo "NOTE: sdk.dir points to a non-existent path: $SDK_DIR"
  echo "      Ensure Android SDK is installed in the container or update ANDROID_SDK_ROOT."
fi

ok "Repo sanity checks passed."
