EMBER — GOLDEN BLUEPRINT (Final)

> This document is the single source of truth. Cursor must read and comply with this file before making changes. Visual polish, animation quality, stability, and Media3 correctness are non-negotiable. Anything rudimentary or off-brand is rejected.


0) Product Vision (North Star)

Ember: Your Audio Player — a stunning, buttery-smooth, privacy-respecting player for music, podcasts, audiobooks, and videos (MP4 linking). The experience must match or exceed the visual + motion standards of Spotify/Apple Music: flawless micro-interactions, consistent brand identity, meaningful animations, and zero jank.

Non-negotiables

World-class visual design and micro-interactions everywhere.

Stable background audio, resume-perfect video playback, seamless switching.

Zero crashes, zero lint errors, Media3 opt-in usage isolated (no file-level opt-in).

Privacy-first: on-device scanning, no tracking by default.

Accessibility first-class: TalkBack, contrast, dynamic type, RTL.


---

1) Brand System (canonical tokens)

Name & Tagline: Ember — Your Audio Player
Logo: Ember flame monogram + wordmark. Launcher icon must match the provided reference (or better), with adaptive icon and monochrome variant.

Color tokens (dark-first)

--amber-900 #FF7A1A (primary flame)

--amber-700 #FF9E3D (flame glow)

--ember-ink #0B0B0C (app bg)

--ember-ink-2 #121316 (alt bg)

--ember-card #16181C (cards/surfaces)

--ember-elev-1 #1B1E23 (elev surfaces)

--accent-ice #7AD7F0 (contrast accent, video contexts)

--success #2BD17E, --warning #FFC83D, --error #FF5A5A

Text: --text-strong #F6F7FA, --text-muted #BAC1CC, --text-disabled #7A828E


Light theme auto-derived via HCT/LAB from these seeds; ensure AA contrast.

Type

Display: Inter/Outfit/SF (fallbacks) — Display 32/36 Semibold

Title 22/26 Semibold · Body 16/24 Medium · Caption 13/18 Medium

Mono: JetBrains Mono (timestamps/diagnostics)


Radii & elevation

Radius: xl 24dp, lg 16dp, md 12dp, sm 8dp, pill 999dp

Elevation shadows (Compose): 3dp/8%, 8dp/10%, 16dp/14%

Hairline dividers: 1dp, #FFFFFF14 on dark


Spacing scale: 4, 8, 12, 16, 20, 24, 32, 40

Gradients

Flame: 35° --amber-900 → --amber-700 with 12% bloom glow

Glow overlays: radial highlights on active controls


Iconography

24dp grid, 1.75dp stroke, rounded joins; AutoMirrored where needed.


Haptics

Tap: light (10–15ms)

Affirm (like/add): light+medium compound

Error: short double


---

2) Launcher Icon & In-App Logo (exact)

Files

app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml (adaptive)

app/src/main/res/values/ic_launcher_bg.xml background #0B0B0C

Foreground vector: app/src/main/res/drawable/ic_ember_logo.xml (flame)

Include monochrome in adaptive icon.


Rule: The home screen icon must look exactly like the provided reference (or a superior iteration under the same brand). No pixel drift.


---

3) Motion System & Micro-Interactions

Global timings

Fast 120ms · Standard 220ms · Gentle 320ms · Emphasis 420ms

Easing: cubic(0.2, 0, 0, 1); overshoot cubic(0.18, 0.9, 0.2, 1.2)

60fps, no frame > 24ms during critical interactions.


Screen transitions

Onboarding: parallax panels, flame gradient sweep (Gentle)

Home ↔ Now Playing: shared element album art (0.92→1.00, blur→sharp)

Library tabs: underline slide + content fade/translate 6dp (Fast)


Now Playing interactions

Play/Pause: flame burst particles (≤24, 180ms), ripple ring

Like: ember fill (gradient sweep bottom→top 260ms)

Shuffle/Repeat: icon morph + ±8° tilt + accent pulse

Seek: waveform scrub with FFT glow; time tooltip springs in/out

EQ sliders: spring to rest (stiffness 250, damping 22), active glow


Loading/empty

Ember heat shimmer skeleton loaders; never bare spinners.


Video/PIP

Enter/Exit PIP: card detach/reattach; poster crossfade; no jank.


---

4) First-Run Onboarding (flow & UX)

Flow order

1. Hello (brand reveal + mission)


2. Permissions: storage/media; clear rationale and privacy stance


3. Scan Options

choose sources (folders)

toggle include MP4

toggle include long audio (>20m) and auto-route: Audiobooks / Podcasts / Keep in Music

Skip for now (CTA clearly available)



4. Theme Picker (Dark / Light / Auto; brand accents preview)


5. Finish (start scan if chosen, background-friendly, clear progress UI)


Rules

Scanning is chunked; UI remains smooth.

If skipped, a Scan entry is present in Settings + Audiobooks/Podcasts tabs.

Privacy: scanning is local only; no upload/analytics.


---

5) Home & Navigation

Top-level tabs: Songs, Playlists, Folders, Albums, Artists, Genres, Audiobooks, Podcasts, Videos

Consistent cards, art masks, hover/highlight states.

Sort & filters per tab; index fast-scroll for long lists.

Search omnibox with type filters (e.g., type:podcast "term").

Pinned/Recent surfaces; smart empty states with CTA.


---

6) Now Playing (music/podcast/audiobook/video)

Layout: Large art/video pane, flame-accented scrubber, primary controls row, secondary actions tray (like, queue, timer, speed, EQ, share, link MP4).
Video linking: Any song/podcast/audiobook can link a specific MP4.

Background: audio continues with app minimized.

Resume: returning to the app restores the video frame exactly.

PIP on supported Android versions.
Bookmarks: per-item (esp. longform), timecode + label.
Speed: granularity 0.05x steps (persist by content type).
Crossfade (music), Silence skip (optional), ReplayGain (optional).
Lyrics/Chapters (later optional): local LRC/chapters if present.


---

7) Equalizer & Effects (global + per-song)

Visual design: Flame EQ — molten gradient bars, ember glow caps, smooth springs, dark glass cards.
DSP

10-band parametric (or platform EQ fallback)

Presets: Ember, Acoustic, Bass, Vocal, Classical, Dance, Flat, Loudness Night

Per-song override stored in DataStore; toggle "Use song preset."

Optional: Bass boost, spatial, reverb (device capability-aware)


Rules

Changes preview instantly; undo/redo; reset to default.

Per-song EQ accessible from Now Playing "fx" icon.

Persist & sync with backups (local export/import).


---

8) Ringtone / Clip Studio

Waveform UI with in/out markers, pinch zoom, fade presets (in/out curves), level meter.

Export to ringtone/notification/alarm with proper MediaStore + Settings.System usage and explicit user consent.

Save as copy to user folder via SAF.

Visual: warm ember glow on selected region; haptics on snap to beat grid.


---

9) Widgets (beautiful defaults + customization)

Compose Glance widgets: Small (1×2), Medium (2×2), Large (4×2)

Art + title/artist, play/pause, next; optional EQ shortcut.

Theme aware (System, Ember Dark, Ember Light) and accent toggle.

Settings: rounded style, compact style, glass style.

Previews in a dedicated Widgets gallery screen.


---

10) Settings (essentials)

Theme (Dark/Light/Auto + accent choice)

Library scanning (folders, MP4 linking rules, >20m routing)

Playback (crossfade, skip silence, speed defaults, replaygain)

Video/PIP behavior

EQ global presets; per-song overrides list

Ringtone/Clip Studio settings

Accessibility (font scale tips, haptic toggle)

Privacy (crash logs opt-in only), Export/Import settings


---

11) Accessibility & Internationalization

Contrast AA+

Dynamic type to 1.3x; reflow thoughtfully

TalkBack: contentDesc for all controls; focus order logical

RTL full pass; album art and progress mirrored appropriately

Haptics as cues (never the only signal)

Localization scaffolding from day one; avoid baked strings


---

12) Performance & Budgets

Cold start (Pixel 6, release): < 700ms to first render

Open Now Playing: < 250ms

Tab switches: < 120ms perceived (prefetch + caching)

Library scan (50k tracks): chunked; never block main; visible progress

Memory steady state: < 200MB

Jank frames < 1.5%


Engineering tactics

Baseline Profiles & R8 rules for Compose/Media3/Lottie/DataStore

StrictMode in debug; macrobenchmark samples for critical flows

Image caching tuned (art, video thumbs) with preheating


---

13) Media3 Compliance (Lint-clean by design)

Rule: Never use file-level @OptIn for Media3's UnstableApi.

Wrap unstable calls (e.g., PlayerView.resizeMode, AspectRatioFrameLayout) in adapter functions annotated with @UnstableApi, or switch to stable APIs if available.

Keep all such adapters in app/…/media3adapters/ so lint is centralized.

If lint requires, add purpose-scoped @Suppress("UnsafeOptInUsageError") only on the annotated adapter—not on callers.


---

14) Data & Persistence

DataStore for settings (theme, scan prefs, speed, toggles).

Room (or lightweight DB) for library index, bookmarks, per-song EQ.

Background coroutine scope for scans; WorkManager for long jobs.

Backups: export/import JSON (settings + per-song EQ + bookmarks).


---

15) QA & "Definition of Done"

Every PR must include:

Lint: 0 errors (esp. Media3 opt-in), warnings triaged or suppressed locally with justification.

Unit tests for logic & adapters; instrumentation smoke on:

Onboarding (all branches: scan/skip/themes)

Now Playing (music/podcast/audiobook/video link, background + resume)

EQ (global/per-song), Ringtone export path

Widgets preview & add


Accessibility spot-check (TalkBack through Now Playing & Onboarding)

3-device matrix: modern (Android 14/15), mid (12/13), small screen

Frame-profile capture for Now Playing interactions (no spikes > 24ms)

Screenshots/video clips in PR description (before/after if UI)


Exit criteria for major features (examples)

Onboarding: all permission paths & "scan later" verified; theme applied app-wide.

Video link: link→background→return flow perfect; PIP works.

EQ: presets save; per-song override reflects on replay; reset works.

Ringtone Studio: can crop, fade, save via SAF; set as ringtone with consent.

Widgets: add/remove/edit without ANRs; theme responsive.


---

16) Security & Privacy

All scanning is local only.

No analytics by default; optional Crashlytics with explicit consent.

Clear privacy notice in onboarding + settings.


---

17) Release & Store Readiness

Assets: launcher icon (all densities), promo images, screenshots (light and dark).

Signed release; versioning SemVer (versionName), monotonic versionCode.

Proguard/R8 safe rules for Media3/Compose/Lottie.

Play Console listing consistent with Ember brand.


---

18) Repository & Cursor Runbook

Paths & environment

Use relative repo paths; do not assume Windows drive letters.

No hardcoded local file system paths.

Keep all brand assets in app/branding/ (SVG + exports), vectors in res/drawable/.


Branching

main protected.

Features feat/<area>-<shortdesc>; fixups fix/<scope>-<desc>.


Commit style

Conventional Commits (e.g., feat(now-playing): add flame burst micro-interaction).


PR template must include:

What/Why, Screenshots or videos, Accessibility checklist, Perf notes, Test plan, Lint status.


Cursor step order (repeatable)

1. Read this blueprint top-to-bottom.


2. Brand pass: tokens, theme, launcher, logo vector.


3. Motion framework: shared elements, loaders, micro-interactions.


4. Onboarding (entire flow).


5. Library tabs (all 9), lists, filters, empties, fast-scroll.


6. Now Playing + Video link/PIP.


7. EQ global & per-song.


8. Ringtone/Clip Studio.


9. Widgets (Glance).


10. Settings consolidation.


11. Media3 adapter isolation; lint clean.


12. Perf & A11y sweep.


13. QA exit criteria & visual polish pass.


14. Update screenshots; prep release notes.


---

19) Risk & Mitigation

Media3 API churn → All unstable calls live in adapters; minimal blast radius.

Huge libraries → Paged cursors, backpressure, cancellation, visible progress.

Animation overdraw → drawBehind, limit blur radii, cap particle count.

Low-end devices → offer "Performance mode" switch to reduce heavy effects.


---

20) Inspiration Guardrails (quality bar)

If an animated element looks anything like a basic spinner, cheap shape spam, or mismatched typography/spacing—reject and rework. We only ship interactions that feel intentional, alive, and brand-true.


---

Appendix A — Adaptive Icon XML (reference)

<!-- app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml -->
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_bg"/>
    <foreground android:drawable="@drawable/ic_ember_logo" />
    <monochrome android:drawable="@drawable/ic_ember_logo" />
</adaptive-icon>

<!-- app/src/main/res/values/ic_launcher_bg.xml -->
<resources>
    <color name="ic_launcher_bg">#0B0B0C</color>
</resources>

(Foreground vector ic_ember_logo.xml must match the provided reference flame.)


---

Appendix B — Media3 Adapter Pattern (lint-safe)

> Do not place @OptIn at file level. Contain unstable usage here.



// app/.../media3adapters/VideoResizeAdapters.kt
package app.ember.studio.media3adapters

import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@UnstableApi
@Suppress("UnsafeOptInUsageError")
fun PlayerView.applyAspectFillOrFit(aspectFill: Boolean) {
    this.resizeMode = if (aspectFill) {
        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
    } else {
        AspectRatioFrameLayout.RESIZE_MODE_FIT
    }
}

Callers (UI code) must never import AspectRatioFrameLayout directly; they call playerView.applyAspectFillOrFit(...). Lint stays clean.


---

Appendix C — Performance Budgets (summary)

Cold start < 700ms · Now Playing open < 250ms · Tab switch <120ms

Jank < 1.5% · Memory < 200MB · No main-thread content resolver work


---

Appendix D — PR Checklist (paste into .github/pull_request_template.md)

[ ] Lint clean; no Media3 opt-in errors

[ ] Screenshots/videos included; dark & light

[ ] A11y pass (TalkBack flows)

[ ] Perf note (frame profile if UI changed)

[ ] Tests added/updated (unit/instrumentation)

[ ] Visuals match brand tokens & motion spec


---

How to install this blueprint

1. Create file docs/EMBER_GOLDEN_BLUEPRINT.md and paste all of the above.


2. Add link at the very top of README.md exactly as shown earlier.


3. In Cursor, run a Read-This-First task:

"Open docs/EMBER_GOLDEN_BLUEPRINT.md and follow it strictly. Start with the Brand pass (tokens, theme, icon), then Motion framework, then Onboarding. Block merges unless PRs meet the PR checklist and QA gates."




This is the gold standard. If anything conflicts elsewhere in the repo, this document wins.
