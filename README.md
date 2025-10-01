**Golden Standard:** See [docs/EMBER_GOLDEN_BLUEPRINT.md](docs/EMBER_GOLDEN_BLUEPRINT.md) for the complete product spec, visual system, motion, QA gates, and "definition of done".

# Ember Audio Player

**Continue developing the application:** Powerful Equalizer • Lyrics • Themes • Tag Editor • Longform (Podcasts/Audiobooks) • Videos

Ember Audio Player is the best music player for Android. With a gorgeous equalizer, all formats supported, and a stylish, themeable UI, Ember provides the best musical experience. Browse all songs on your device, listen without Wi-Fi—this is a perfect offline music player.

---

## Key Features

- 🎵 **All formats**: MP3, MIDI, WAV, FLAC, AAC, APE, etc. in high quality.
- 🎵 **Offline**: songs player, audio player, mp3 player—no network required.
- 🎵 **Powerful equalizer**: bass boost, reverb effects, virtualizer; 5-band EQ.
- 🎵 **Playback modes**: shuffle, order, loop.
- 🎵 **Library**: scan all audio; view by songs, artists, albums, folders, playlists.
- 🎵 **Playlists**: favorites and custom lists; Add to Queue flow.
- 🎵 **Search**: fast keyword search.
- 🎵 **System integration**: lock-screen controls, notification controls, set as ringtone.
- 🎵 **Quality of life**: useful for workout; Sleep Timer; widgets; stylish layout & themes.
- **Lyrics**: quickly import and display.
- **Themes**: colorful and custom themes via Theme Studio.
- **Tag editor**: safe metadata edits with undo.
- **Longform**: podcasts/audiobooks support with bookmarks and speed control.
- **Videos**: optional MP4/WEBM tab; inline Now Playing video.
- **Mp4 support**: add videos to tracks (video above art area when present).

---

## Product Blueprint — Top Tabs + Drawer (Full)

### A. Brand identity (fixed)
- **Name**: Ember
- **Tagline**: Your audio player.
- **App icon**: Helix-Note Ember (flame + negative-space note).
- **Accent ramp**: Ember Orange `#FF7A1A` → Amber `#FF9A3D`.
- **Surfaces**: Base `#0E1014`, Surface `#14171C`, Stroke `#262A33`.
- **Text**: Primary `#E8EAEE`, Secondary `#B9C0CC`, Muted `#8F97A3`.
- **A11y**: Contrast ≥ 4.5:1; 48dp touch targets.
- **Motion**: Short, precise; no ornamental loops.
- **Signature live indicator**: tiny animated ember (8–10dp) in the top bar; breathes while playing.

### B. First-launch onboarding (one pass)
**Scene 1 — Brand hello (2.5–3s, skippable)**
- Centered column; animated Helix Ember (96dp); title “Ember”; subtitle “Your audio player”.
- Animation: scale 0.96→1.00; glow pulses ×2 (120ms in / 140ms out). No looping beyond two pulses.
- “Continue” appears at 800ms.

**Scene 2 — Permission with empathy**
- Title: “Play your local audio”
- Body: “Ember scans your device to show your music. Nothing leaves your phone.”
- Primary: “Allow access” → READ_MEDIA_AUDIO (Android 13+) and legacy; Secondary: “Choose folders instead” (SAF).
- On grant: initial scan with inline “Scanning… 0 of N”.
- On deny: rationale toast + “Try again”.
- **Auto-detection policy**: on every launch, delta scan of MediaStore and watched folders; non-blocking; brief “Library updated” snackbar when changes found.

**Scene 3 — Theme pick**
- Options: Ember Classic / Night Copper / Mono Graphite.
- Copy: “More customization anytime in Theme Studio.” (spark glyph)
- Persist in DataStore.

### C. App shell (always visible)
- **Top app bar**: Drawer • Sliding tabs • [Live indicator] • Search • Theme Studio • Settings
- **Tabs** (reorder/hide in Settings ▸ Interface): Songs · Playlist · Folders · Albums · Artists · Genres · **Longform** · **Videos**
- **Mini-player** bottom bar: 32dp art; title/artist; Play/Pause, Next, Queue; tap → Now Playing; swipe up → Queue; swipe down to collapse.

### D. Tabs (visuals, controls, menus)

**Conventions for lists**: Row 72/64/56dp; thumb 48dp; single-line title; subtitle “Artist — Album/Folder”; trailing date or duration.

**1) Songs**
- Header pills: **Shuffle** (left), **Play** (right).
- Toolbar: Sort (⇅), Select (checklist).
- Count: “XX Songs”.
- Current playing row: title in Ember color; 2dp ember strip at left; subtle equalizer glyph animates at 120ms while playing.
- Overflow (order): Play next · Add to queue · Add to playlist · Go to album · Go to artist · Edit tags · Change cover · Share · Set as ringtone · Hide song · Delete from device · Info.
- Sort options: Song name · Artist · Album · Folder · Added time · Play count · Year · Duration · File size; with A→Z/Z→A or New→Old/Old→New secondary.
- Multi-select: Play, Add to playlist, Delete.

**2) Playlist**
- Pinned: Favourites, Recently added, Recently played, Top tracks.
- “Create playlist” card; Restore playlist; Import M3U/PLS.
- Detail: cover mosaic (first 4 arts), pills Shuffle/Play, Add songs, Reorder, Sort, Select; overflow Rename, Change cover, Share (M3U + cover), Export (M3U), Delete.

**3) Folders**
- Only folders with audio; row shows name, path crumb, count badge.
- Overflow: Include/Exclude · Pin to top · Open in Files.
- Detail behaves like Songs; “Parent folder” chip.

**4) Albums**
- Grid 2+ cols; card art 1:1, 8dp radius; labels: Title / Artist; small “N songs” chip.
- Sort: A→Z, Artist, Year, Added, Most played.
- Overflow: Play next · Add to queue · Add all to playlist · Edit album tags · Change cover · Share.

**5) Artists**
- List; colored initial tile if no image; “X albums | Y songs”.
- Unknown artist bucket labeled exactly `""` as first when present.
- Detail: header, Albums grid, All songs list, pills Shuffle/Play.

**6) Genres**
- List with count; detail like playlist (Shuffle/Play, sort, select).
- No catch-all if missing genre.

**7) Longform (Podcasts & Audiobooks)**
- Auto bucket items ≥ **20m** (threshold adjustable 10–60m).
- Chips: All · Podcasts · Audiobooks.
- Heuristics for suggestions:
  - **Audiobooks**: tokens `book|audiobook|chapter|ch|part|vol` or ≥ **2h**; ch## patterns.
  - **Podcasts**: tokens `episode|ep|podcast`, show-ep naming, or embedded podcast tags.
- Row extras: **Bookmarks** badge; Now Playing shows **Speed** chip (0.5×–2.0×).
- Overflow additions: Add bookmark; Sleep at end of chapter; Mark finished/unplayed.

**8) Videos**
- Explicit import only (no auto video scan).
- Grid thumbnails with duration; default sort “Added”.
- Now Playing shows video instead of artwork; tap to reveal controls; screen-off → audio continues.
- Overflow: Play next · Add to queue · Add to playlist · Share · Set as video ringtone (if supported) · Info · Delete.

### E. Now Playing & Queue
**Now Playing**
- Artwork 1:1 (or video), rounded 20dp; dynamic gradient background (8–12% from dominant colors).
- Scrubber: 4dp track; ember droplet thumb; tap-seek; elapsed/remaining labels.
- Transport: Prev · Play/Pause (56dp) · Next.
- Toggles: Shuffle, Repeat (off/one/all).
- Secondary bar: Speed (for longform), Sleep, Output/Route, Cast (future), Lyrics.
- Visualizer (optional): Bars/Wave/Helix; throttled on battery saver.
- Overflow: playlist/queue, Share, Set as ringtone, Edit tags, Go to album/artist, Info.
- Animated flame indicator lives in the top bar; Play/Pause pulses scale 1.00→1.03→1.00.

**Queue**
- Pull-up sheet; draggable; current item thin ember border.
- Bulk: Clear after current; Save as playlist.
- Swipe left delete; undo snackbar.

### F. File management & storage
- **Linked** (default): points to original file; delete via SAF removes original.
- **Managed** (explicit imports/videos): single copy in app-private; **Recycle Bin** (30 days) on delete; restore/delete permanently.
- **Hide song**: hides from views without moving files.
- Duplicate protection: MD5 dedupe on import; offer to remove duplicate originals.

### G. Artwork & metadata enrichment
- Local covers: `cover.jpg/png`, `folder.jpg/png` lookup.
- Optional online suggestions (opt-in); confidence threshold auto-apply with Undo.
- Tag edits: safe write-back with single-level undo per session.
- Auto grouping heuristic for albums; otherwise ungrouped.

### H. Search (global)
- Top icon; buckets: All, Songs, Albums, Artists, Playlists, Longform, Videos.
- Debounce 75–100ms; streaming results; long-press row preview; filters chips when applicable.

### I. Drawer
- **Library**; **Equalizer** (presets: Normal, Rock, Dance, Electronic, Flat, Vocal, Bass Boost, Custom; 5 bands 60/230/910/4k/14k; Reverb; Bass Boost & Virtualizer; Headphone profiles; A/B).
- **Sleep Timer** (HH:MM:SS wheels + quick 15/30/45/60m; options: fade last 20s, stop after track/queue).
- **Theme Studio (Skins)**: Background (Charcoal/Gradient/Custom image with blur/dim/vignette), Accent color (preset + custom w/ auto-contrast), Fonts (Inter/Manrope/DM Sans/Work Sans/JetBrains Mono), Icon style (Line/Solid/Ember Gradient), List density (Comfort/Cozy/Compact), Visualizer style, Apply scope (All / Now Playing), Save/Rename/Reset/Import/Export Theme (JSON).
- **Widgets**: Classic 4×1, Lite 4×1, Vinyl 4×2, Simple 2×1, Circular, Helix.
- **Scan & Import**: scan, pick folders (SAF), import files (Managed), ZIP import, Hidden music.
- **Backup & Restore**: playlists + settings to user SAF.
- **Remove ads** (if applicable); About/Rate/Privacy/Terms.

### J. Settings
- **General**: language; backup/restore; hidden music; remove ads.
- **Playback**: crossfade (0–12s, equal-power), gapless, keep screen on, lock screen, pause on detach, duck on notifications.
- **Library**: watched folders; online artwork & metadata (toggle); longform threshold; only folders with audio; include videos.
- **Interface**: tab order/visibility; Theme Studio shortcut; list density; default visualizer; mini-player style; peeking vinyl toggle.
- **Help**: FAQ, Feedback, Rate, background play tips, Version.
- All settings write immediately; destructive actions confirm.

### K. Accessibility & i18n
- Full TalkBack labels; focus order mirrors visuals; RTL mirrored; plurals/dates localized; large text support (2-line safe).

### L. Performance budgets & quality bars
- Cold start P50 ≤ 900ms (mid-tier), warm ≤ 500ms.
- Scroll jank (90th) < 1 frame.
- Seek latency P50 ≤ 120ms.
- Screen-off drain ≤ 1.5%/hr (audio only).
- Crash-free sessions ≥ 99.8%; ANR < 0.3%.

### M. Telemetry (opt-in, no PII)
- Lifecycle: app_open, scan_start/complete.
- Library: tab_view, sort_set, select_count.
- Playback: play_start, play_pause, seek, queue_add, queue_jump.
- Playlists: create/add/export/import/restore.
- Equalizer: toggle/preset/bass/reverb.
- Sleep: timer_set.
- Theme: theme_apply/theme_customize.
- Artwork: suggestion_shown, auto_applied (confidence), undo.
- Errors as toasts with Retry/Details; never block UI except permission/SAF.

### N. Definition of Done (milestones)
- **M1**: onboarding, shell, tabs (Songs/Playlists/Folders/Albums/Artists/Genres), Now Playing + Queue, Search, EQ, Sleep, Settings, Theme Studio v1, a11y pass.
- **M2**: Theme Studio v2 (fonts/icons/density), Longform w/ bookmarks, Managed import + Recycle Bin, artwork suggestions, Widgets, Videos tab.
- **M3**: Backup/Restore, Hidden music manager, Album/Artist polish, Ads (if used), performance & release QA.

### O. Micro-animation catalog
- Helix hello (scale + glow), Play pulse, List press glow, Tab underline (180ms cubic), Queue drag (spring), Flame indicator breathe (900ms loop when playing).

### P. Copy library (verbatim)
- Onboarding S2 title: “Play your local audio”
- Onboarding S2 body: “Ember scans your device to show your music. Nothing leaves your phone.”
- Onboarding S3 title: “Pick a look”
- Theme hint: “More customization anytime in Theme Studio.”
- Empty library: “No audio yet. Pick folders or import files.”
- Delete confirm (managed): “Move to Recycle Bin for 30 days?” / [Move] [Cancel]
- Art applied snackbar: “Artwork set.” [Undo]
- Denied permission: “Without access, Ember can’t find your music.” [Try again]

### Q. Assets & sizes
- Helix Ember logo: vector + Lottie (96dp onboarding; 24dp spark top bar).
- Icons 24/32dp (Material-compatible; spark/flame custom).
- Artwork: 48dp list; 160–200dp Now Playing; 128–160dp album grid.
- Widget previews: 16:9 @ 1080w.

### R. Engineering notes
- **UI**: Jetpack Compose; one Scaffold; tabs use LazyColumn/LazyVerticalGrid.
- **State**: ViewModels per tab; immutable state; flows for playback/scan.
- **Playback**: AndroidX Media3/ExoPlayer; MediaSessionService; MediaStyle notification.
- **Storage**: Room for entities; DataStore (Proto) for settings; app-private files for Managed + Recycle Bin.
- **Scan**: MediaStore + optional FileObserver; WorkManager for background rescans.
- **Art cache**: disk LRU; non-blocking decode; prefetch on scroll.
- **Permissions**: request only when needed; SAF for delete/import.
- **i18n**: externalized strings; plurals; RTL tested.

---

## Conditional Onboarding — Scene 2b: Long audio found (first scan)

**Trigger**: After permissions when initial scan finds ≥ 1 item with duration ≥ **20:00** (threshold adjustable later).

**Purpose**: Quickly sort long recordings into Audiobooks/Podcasts or skip.

**Screen**: “Long audio found”
- Subtitle: “We can organize longer recordings for you. You can change this anytime.”
- Pill: “N items ≥ 20 min”
- Buttons: **Import all as Audiobooks** · **Import all as Podcasts** · **Choose individually** · **Skip for now**
- Footer: “Change later in Longform or Settings ▸ Library.”
- Back = Skip.

**Choose individually**:
- Header: “Sort long audio (N)”
- Chips: All · Suggested Audiobooks · Suggested Podcasts · By folder (top 5; “More…” to pick)
- Row: Title/filename, duration, folder crumb; right segmented control: Music / Podcast / Audiobook
- Long-press multi-select; bottom actions; “Select all in folder”
- Apply → persist types; Toast “Sorting applied.” [Undo]; then Theme pick.

**Heuristics**:
- Audiobook suggestion: tokens `book|audiobook|chapter|ch|part|vol`, or ≥ 2h; or chapter patterns.
- Podcast suggestion: tokens `episode|ep|podcast|show`, ep number/date patterns, embedded tags.
- Else default to Music (unclassified).

**Persistence**:
- Track entity adds `duration_ms`, `longform_detected` (derived), `longform_type` {NONE, PODCAST, AUDIOBOOK}.
- Settings ▸ Library: “Consider longform if ≥ [20] minutes”; “Re-run longform suggestions”.

**Surfacing later**:
- Longform chips: All, Podcasts, Audiobooks, Unassigned.
- Row overflow additions: Change type; Add bookmark.
- Items still appear in Songs; classification affects Longform grouping and defaults (e.g., speed chip).

**Telemetry (opt-in)**:
- longform_onboarding_shown {count}
- longform_onboarding_choice {mode}
- longform_apply {podcasts, audiobooks, music}
- longform_skip

**Edge cases & DoD**:
- No candidates → skip Scene 2b.
- >1000 items: pagination; “Select all in folder” encouraged; jank ≤ 1 frame.
- Undo reverts last bulk classification.
- A11y: segmented control accessible with clear labels.

---

## Build & Run

### Codex Cloud (container) — **do this first**
> Codex must provision the Android SDK and generate `local.properties` (untracked).

```bash
tools/ensure-android-sdk.sh || true
# Writes: local.properties with sdk.dir=${ANDROID_SDK_ROOT:-/opt/android-sdk}

./gradlew --version && ./gradlew -q help
./gradlew -S :core-ui:testDebugUnitTest
./gradlew -S :core-ui:compileDebugKotlin :app:assembleDebug
````

**Requires**: Android **platforms;android-35**, **build-tools;35.0.0**, **platform-tools**. If missing:

```bash
sdkmanager --licenses <<< "y"
sdkmanager "platform-tools" "platforms;android-35" "build-tools;35.0.0"
```

### Android Studio (local)

1. JDK 17, Android SDK 35, build-tools 35.0.0.
2. Ensure `local.properties`:

```
sdk.dir=/path/to/Android/Sdk
```

3. Sync & run `app`.

> **Never commit** `local.properties` (machine-specific; generated, ignored).

---

## Gradle / Toolchain

* AGP **8.12.0**, Gradle **8.13**, Kotlin **2.1.20**, JDK **17**
* Compose via BOM 2025.06.00 (do **not** set `kotlinCompilerExtensionVersion`)
* `gradle.properties`:

  * `org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m -Dfile.encoding=UTF-8 -Dkotlin.daemon.jvm.options=-Xmx2048m`
  * `org.gradle.parallel=true`
  * `org.gradle.configuration-cache=true`
  * `kotlin.code.style=official`, `kotlin.incremental=true`
  * `android.useAndroidX=true`, `android.nonTransitiveRClass=true`
  * `android.enableJetifier=false` (turn **true** only if legacy support libs require it)

---

## CI

Workflow: `.github/workflows/android-ci.yml`

* JDK 17, Android SDK setup, license acceptance
* Installs required SDK packages
* `:app:assembleDebug`, detekt & ktlint (non-blocking), uploads APK artifact.

---

## Policies

* **Never commit** `local.properties`; generate when missing.
* **No conflict markers** (`<<<<<<<`, `=======`, `>>>>>>>`) in repo files.
* Emit **final files**, not partial patches; keep package/namespace alignment.
* Do not set `kotlinCompilerExtensionVersion` (use Compose BOM/Kotlin plugin).

---

## Definition of Done (per PR)

* `:core-ui:testDebugUnitTest` **passes**
* `:app:assembleDebug` **passes**
* Attach last \~20 lines of build output
* Update `docs/STATE.yml` & `docs/NEXT.md`

---

## Privacy

* Library scans and usage stay **on device** by default.
* Optional online artwork/metadata is **opt-in**; no PII.
* Backups export to user-chosen SAF location.

---

## Roadmap

* **M1**: onboarding, shell, core tabs, Now Playing + Queue, EQ, Sleep, Settings, Search, Theme Studio v1, a11y.
* **M2**: Theme Studio v2 (fonts/icons/density), Longform bookmarks, Managed import + Recycle Bin, artwork suggestions, Widgets, Videos.
* **M3**: Backup/Restore, Hidden music manager, album/artist polish, ads (if used), perf & release QA.

---

```
