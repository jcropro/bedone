```markdown
# Ember Audio Player — Local Dev (Cursor Autopilot)

**Product Bible:** [`docs/EMBER_GOLDEN_BLUEPRINT.md`](docs/EMBER_GOLDEN_BLUEPRINT.md)  
**Execution Playbook (Cursor):** [`docs/EXECUTION_BLUEPRINT_CURSOR.md`](docs/EXECUTION_BLUEPRINT_CURSOR.md)

Ember is a world-class, offline-first audio player with AAA-grade visuals, animations, EQ, longform media (podcasts/audiobooks), and video.  
Your local working copy lives at:

```

C:\Users\jcron\AndroidStudioProjects\EmberAudioPlayer3

````

---

## How you drive this project (Zero babysitting)

- You type **“proceed”** here after any adjustment.  
- Cursor reads:
  - `docs/STATE.yml` — current phase & checkpoints
  - `docs/NEXT.md` — the next 3 tasks
  - `docs/EXECUTION_BLUEPRINT_CURSOR.md` — exact steps, file names, motion specs
  - `docs/EMBER_GOLDEN_BLUEPRINT.md` — product intent & brand rules (source of truth)

Cursor performs the tasks, updates `docs/STATE.yml` and `docs/NEXT.md`, and logs to `docs/CHANGELOG/`.

---

## ✅ Run Green After Every “Proceed” (must pass)

**One-liner (Windows PowerShell):**
```powershell
powershell -ExecutionPolicy Bypass -File tools/run_green_checks.ps1
````

**What it does (non-interactive):**

1. `gradlew :app:assembleDebug` (builds APK)
2. `gradlew :app:lintDebug` (lint)
3. `gradlew :app:testDebugUnitTest` (unit tests)
4. (If a device/emulator is connected) `gradlew :app:connectedDebugAndroidTest` (instrumented tests)
5. Verifies **no raw hex** was introduced in UI files (tokens-only check)
6. Verifies `docs/STATE.yml` & `docs/NEXT.md` are consistent (phase/task sync)
7. Prints APK path and a short summary

> If any step fails, the script exits **non-zero** and prints a clear reason so Cursor can fix and re-run.

---

## Local build (Android Studio)

1. Open the folder above in Android Studio (JDK 17).
2. Ensure **Android SDK 35** and **Build Tools 35.0.0** are installed.
3. Make sure `local.properties` exists:

```
sdk.dir=C:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
```

4. Build: **Run ▶** or `.\gradlew :app:assembleDebug`.

---

## Visual Quality Bar (non-negotiable)

* **Tokens only** (no raw hex codes) for colors/gradients/glass/motion.
* **60 fps** target on mid-tier device; zero jank on scroll, sheet open, scrubbing, queue reorder.
* Every control has a **premium press animation** + correct **haptic**.
* All screens pass **WCAG AA**; **Reduced Motion** supported.

---

## Key docs

* **Product Bible:** complete product spec, visuals, motion, QA, “definition of done”.
  [`docs/EMBER_GOLDEN_BLUEPRINT.md`](docs/EMBER_GOLDEN_BLUEPRINT.md)
* **Execution Playbook (Cursor):** step-by-step file work with motion timings, acceptance tests.
  [`docs/EXECUTION_BLUEPRINT_CURSOR.md`](docs/EXECUTION_BLUEPRINT_CURSOR.md)
* **Project State:** where we left off (phase, checklist).
  [`docs/STATE.yml`](docs/STATE.yml)
* **Next 3 Tasks:** Cursor’s immediate to-do.
  [`docs/NEXT.md`](docs/NEXT.md)
* **Change Log entries:** auto-written per “proceed”.
  `docs/CHANGELOG/`

---

## Policies

* Do **not** commit `local.properties`.
* Do **not** hardcode `kotlinCompilerExtensionVersion` (use Compose BOM).
* No **file-level** Media3 opt-ins; use adapters under `core/media/adapters`.

Enjoy. Type **proceed** to continue.

````

---

## 2) Add this file: `tools/run_green_checks.ps1`

```powershell
Param()

$ErrorActionPreference = "Stop"

function ExecGradle($args) {
  Write-Host "→ gradlew $args" -ForegroundColor Cyan
  & .\gradlew $args --console=plain | Tee-Object -Variable lastOutput
  if ($LASTEXITCODE -ne 0) {
    Write-Error "Gradle failed: $args"
  }
}

function CheckTokensOnly() {
  Write-Host "→ Verifying tokens-only usage in UI (no raw hex)" -ForegroundColor Cyan
  $uiFiles = Get-ChildItem -Recurse -Include *.kt | Where-Object { $_.FullName -match "\\ui\\|\\compose\\|\\feature\\" }
  $bad = @()
  foreach ($f in $uiFiles) {
    $text = Get-Content $f.FullName -Raw
    if ($text -match "Color\(\s*0x[0-9A-Fa-f]{8}\s*\)") {
      $bad += $f.FullName
    }
  }
  if ($bad.Count -gt 0) {
    Write-Host "Found raw hex colors (disallowed):" -ForegroundColor Red
    $bad | ForEach-Object { Write-Host "  $_" -ForegroundColor Red }
    throw "Raw hex colors detected in UI files. Use Tokens.kt."
  }
  Write-Host "✓ Tokens-only check passed"
}

function CheckStateSync() {
  Write-Host "→ Checking STATE.yml / NEXT.md coherence" -ForegroundColor Cyan
  $state = Get-Content "docs/STATE.yml" -Raw
  $next  = Get-Content "docs/NEXT.md" -Raw

  if (-not $state) { throw "docs/STATE.yml missing or empty" }
  if (-not $next)  { throw "docs/NEXT.md missing or empty" }

  # simple sanity: phase present and next has at least one task header
  if ($state -notmatch "phase:\s*\d+") { throw "STATE.yml missing 'phase:' field" }
  if ($next  -notmatch "^##\s*1\)" -and $next -notmatch "^# NEXT 3 TASKS") { throw "NEXT.md missing tasks" }

  Write-Host "✓ State/Next check passed"
}

# 1) Build
ExecGradle ":app:assembleDebug"

# 2) Lint
ExecGradle ":app:lintDebug"

# 3) Unit tests
ExecGradle ":app:testDebugUnitTest"

# 4) Connected tests (optional; ignore failure if no device)
try {
  ExecGradle ":app:connectedDebugAndroidTest"
} catch {
  Write-Warning "connectedDebugAndroidTest skipped (no device/emulator?)."
}

# 5) Tokens-only check (no raw hex in UI)
CheckTokensOnly

# 6) STATE/NEXT coherence
CheckStateSync

# 7) APK path summary
$apk = Get-ChildItem -Recurse -Include *.apk | Where-Object { $_.FullName -match "\\app\\build\\outputs\\apk\\debug" } | Sort-Object LastWriteTime -Descending | Select-Object -First 1
if ($apk) {
  Write-Host "✓ Build OK — APK:" -ForegroundColor Green
  Write-Host "  $($apk.FullName)"
} else {
  Write-Warning "APK not found (build may use app variants)."
}

Write-Host "`nALL GREEN ✔" -ForegroundColor Green
exit 0
````

This script is safe to run repeatedly; it fails fast with clear reasons (so Cursor can fix and rerun automatically).

---

## 3) Slight tweak to `docs/NEXT.md` (append this to the bottom)

```markdown
---

## Always run green checks after each “proceed”
Run:
```

powershell -ExecutionPolicy Bypass -File tools/run_green_checks.ps1

```
All steps must pass (build, lint, tests, tokens-only, state/next coherence). If anything fails, fix and re-run before the next “proceed”.
```

---

