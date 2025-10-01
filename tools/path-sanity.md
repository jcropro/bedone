Path Sanity — Relative Paths Only

- Policy: All scripts, docs, and configs must use repo-root relative paths. Do not hardcode absolute paths.
- Verified patterns:
  - No references to "/workspace/" found in repo.
  - No references to "C:\\Users\\jcron" found in repo.
  - Observed absolute paths under build artifacts (non-source): entries in build caches/logs include "/mnt/c/Users/jcron/..." and the repo-root itself; these are expected and not committed.

Proposed fixes for future changes:
- Use environment variables and relative paths (e.g., "$PWD", "./app", "./tools").
- For Android SDK, prefer local.properties with a portable path or "$HOME/android-sdk" (see tools/sdk-wsl.sh and tools/sdk-win.bat).
- Avoid embedding user-specific or container-specific paths in source or Gradle files.

Optional WSL compatibility symlink (do not run unless you need legacy paths):
- Command: ln -s "/mnt/c/Users/jcron/AndroidStudioProjects/EmberAudioPlayer3" "/workspace/EmberAudioPlayer3"

