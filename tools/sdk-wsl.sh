#!/usr/bin/env bash
set -euo pipefail

# Writes local.properties with a WSL-friendly SDK path
script_dir="$(cd "$(dirname "$0")" && pwd -P)"
repo_root="$(cd "$script_dir/.." && pwd -P)"
printf 'sdk.dir=%s\n' "$HOME/android-sdk" > "$repo_root/local.properties"
echo "Wrote $repo_root/local.properties with sdk.dir=$HOME/android-sdk"

