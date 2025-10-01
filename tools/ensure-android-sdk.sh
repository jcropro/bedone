#!/usr/bin/env bash
set -euo pipefail
REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
SDK="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-/opt/android-sdk}}"
mkdir -p "$REPO_ROOT"
mkdir -p "$SDK"
[ -f "$REPO_ROOT/local.properties" ] || printf 'sdk.dir=%s\n' "$SDK" > "$REPO_ROOT/local.properties"
