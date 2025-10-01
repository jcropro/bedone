#!/usr/bin/env bash
set -euo pipefail

# Resolve repo root relative to this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$REPO_ROOT"

SDK_ROOT="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$HOME/android-sdk}}"
export ANDROID_SDK_ROOT="$SDK_ROOT"
export ANDROID_HOME="$SDK_ROOT"
mkdir -p "$SDK_ROOT"

printf 'sdk.dir=%s\n' "$SDK_ROOT" > local.properties

SDKM="$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"
if [[ ! -x "$SDKM" ]]; then
  echo "No sdkmanager under $SDK_ROOT/cmdline-tools/latest/bin" >&2
  exit 1
fi

yes | "$SDKM" --sdk_root="$SDK_ROOT" --licenses >/dev/null || true
"$SDKM" --sdk_root="$SDK_ROOT" --install \
  "platform-tools" \
  "platforms;android-35" \
  "build-tools;35.0.0" \
  >/dev/null

./gradlew -q help >/dev/null
