# EMBER — The Definitive Product Blueprint (Ultra-Detailed, Design-First)

This document describes **exactly** what Ember is and how it looks, feels, and behaves—down to micro-interactions, motion curves, surface finishes, and accessibility states. It is intentionally exhaustive so any contributor can audit work against it and know when something is “**done and beautiful**.”

No code here—pure product, UX, and visual design specification.

---

## 0) North Star & Personality

**Ember: Your Audio Player.**
A premium, offline-first Android player that feels like lighting a candle—calm, warm, and a little magical. Every touch should “throw a spark” (subtle delight), never shouty. Fast, responsive, and trustworthy. Zero jank.

**Design ethic:** *elegant minimalism with molten warmth*. Clean typography, matte lists, glassy modals, soft ember glows used sparingly. Dark-first and OLED-friendly.

**Craft principles**

1. **Beautiful by default** (sane, aesthetic defaults; users can personalize further).
2. **Motion that serves meaning** (micro delight, macro clarity; never gimmicks).
3. **Never lose context** (state persists; you can leave and return anywhere).
4. **Accessible and respectful** (AA/AAA contrast, talkback, reduce motion).
5. **Offline-first privacy** (no cloud assumptions; clear when anything leaves device).

---

## 1) Brand System

### 1.1 Primary Mark & App Icon

* **Glyph:** exact flame/treble shape you provided (no deviation). Always oriented upright; never mirrored or skewed.
* **Adaptive icon:**

  * **Foreground:** the flame rendered at 1024 px source; crisp vector or high-res PNG with baked soft inner glow.
  * **Background:** true black (`#000`) or graphite (`#0A0A0A`) depending on theme; subtle vignette ≤ 4% opacity.
  * **Safe zone:** keep glyph within 66% circle to avoid launcher crops. Edges shouldn’t touch mask.
  * **Monochrome icon:** single-color flame for Android 13+; tint inherits from system; ensure inner negative space reads at small sizes.
* **Usage:** Splash hero at 40–48% of shortest edge; watermark on empty states at 6–8% opacity; never placed under dense text.

**Focus ring:** gold rim (`#FFD27A` @ 26% opacity), 2px soft edge when the icon is the focusable ele­ment (e.g., TV or keyboard focus).

### 1.2 Color, Surfaces & Finishes

* **Primary (Ember)**: `#FF7A1A`
* **Warm ramp:** `#FF9C4A → #FF7A1A → #D11F1F`
* **Neutrals (Dark):** Surface `#0B0C0E`, Elevation `#121418`, Outline `#2B2F36`
* **Glass**: blur 8–12px; inner shadow `black@14%` inset; border `white@6%` for edge definition.
* **Glow**: additive orange/amber blur 12–20px; **cap opacity at 12%** on any control; 6% typical.
* **Shadows**: ambient only—no harsh drops. Y=6 to 10, blur 24–30.
* **Grain/noise**: optional 0.5–1% on large panels to avoid banding.

**Contrast guardrails**

* Body text ≥ **4.5:1** contrast; icons ≥ **3:1**; disabled content opacity ~42–50%.

### 1.3 Type & Layout Grid

* **Type scale** (Material 3 tuned):

  * Display: 32/38/44 (weight 700) for major headers (e.g., “Songs”).
  * Title: 20–22 (600), supportive subtitle 14–16 (500) at 70% opacity.
  * Body: 14–16 (500); Micro labels 12–13 (500) for meta info.
* **Line heights**: generous; do not compress (e.g., 1.3–1.4 for body).
* **Grid**: 8dp baseline; 4dp only for micro alignments (never compounding odd sizes).
* **Touch targets**: **≥48dp** min; list items 64–72dp height.

### 1.4 Motion System

* **Global durations**: 120–240ms for taps/transitions; 280–380ms for complex reveals (e.g., queue).
* **Curves**

  * Standard: `cubic-bezier(0.2, 0, 0, 1)` (fast in, soft settle)
  * Decel: `cubic-bezier(0, 0, 0, 1)` (for sheet landings)
  * Spring (playful taps): damping 0.78, stiffness 350
* **Beat-reactive accents**: track RMS drives a **very** subtle (≤6% opacity) pulse on the play button halo and progress bar.
* **Motion accessibility**: “Reduce Motion” swaps springs/particles for fades; durations trimmed by 15%.

---

## 2) Theme Library (Dark-first, OLED-tuned)

Ember ships **11** expertly curated themes (you listed 10 + Cyber Ember; we include all). Each includes palette, gradients, and finish notes. All themes must feel **premium**, **matte**, and **quiet**.

> **Global rules for all themes**
>
> * One visible gradient per component max; never stack loud glows.
> * Warm (Ember) accents are for **primary actions** only; cool accents for secondary affordances (sliders, active tab underline).
> * Text never uses accent color, only neutrals.
> * True-black mode is available for OLED battery savings (Obsidian Ember & Everyday Dark sub-variants).

### 2.1 Everyday Dark (Default)

* **Mood:** smoldering, minimal, brand-true.
* **Palette:** Surface **#0B0C0E**, Elevation **#121418**, Text **#E9EBEF**, Muted **#B6BBC6**, Outline **#2B2F36**, Primary **#FF7A1A**, AccentCool **#7AE1FF**.
* **Gradients:** AppBar **#0F1013 → #191B20** (4%) ; Play radial **#FF7A1A → transparent** (very low).
* **Finish:** satin CTAs; tiny warm rim-light around focused controls; glow ≤12%.
* **Components:** lists matte; modals use glass; chips use smoky backgrounds `white@6%`.

### 2.2 Inferno Core

* **Mood:** warm strength without glare.
* **Palette:** Surface **#0D0E12**, Elevation **#13151A**, Text **#E8EAEE**, Primary **#FF7A1A**, Soft Amber **#FF9C4A**, Neutral **#2C2F36**.
* **Gradients:** Panel **#0F1013 → #1A1C21** (5%); CTA **#FF9C4A → #FF7A1A** (18% overlay).
* **Finish:** inner-shadow on play; stately chips; no pure yellow.

### 2.3 Obsidian Ember (Pro OLED)

* **Mood:** surgical, audiophile, glass-lite.
* **Palette:** Surface **#05070A**, CardGlass **rgba(255,255,255,0.06)**, Text **#F2F5FA**, AccentCool **#6FD7FF**, Primary **#FF7A1A**.
* **Gradients:** Background **#070A0F → #0E1117** (3%).
* **Finish:** glass only for modals; lists matte; neon underline (active tab) at 12% opacity; **true black** variant togglable.

### 2.4 Aurora Flame

* **Mood:** cool clarity with warm cues.
* **Palette:** Surface **#0A0F12**, Text **#E8EEF2**, Teal **#15B8A9**, Ember **#FF7A1A**, Muted **#263139**.
* **Gradients:** **#0E222B → #102E38** (6%).
* **Finish:** frosted tabs (blur 20); teal for sliders/switches; warm for CTAs only.

### 2.5 Velvet Magma

* **Mood:** premium, intimate, lyrical.
* **Palette:** Surface **#120F17**, Elevation **#191425**, Text **#ECE6F3**, Plum **#8E41C7**, Ember **#FF7A1A**, Soft Lilac **#E8D3F9**.
* **Gradients:** **#1B1226 → #2A1840** (8%); CTA **#D6A7FF → #8E41C7** (15–20%).
* **Finish:** satin knobs, warm gold speculars on focus rings; EQ looks gorgeous in this theme.

### 2.6 Solar Flare

* **Mood:** understated luxury.
* **Palette:** Surface **#101113**, Elevation **#15171B**, Text **#F3EEE6**, Amber **#EFA64A**, Ember **#FF7A1A**, Metal **#FFD27A**.
* **Gradients:** **#0F0F12 → #18191C** (4%); faint gold dust ≤1%.
* **Finish:** 0.5px soft gold borders on cards; CTAs amber→ember with satin sheen.

### 2.7 Midnight Oil

* **Mood:** focused night drive.
* **Palette:** Surface **#0A0E18**, Elevation **#0F1424**, Text **#E6EAF6**, Indigo **#335AE1**, Ember **#FF7A1A**.
* **Gradients:** **#0B1021 → #111732** (7%).
* **Finish:** indigo micro-glow on progress thumb; warm accents reserved for play/primary.

### 2.8 Glacier Ember

* **Mood:** crisp, airy, technical.
* **Palette:** Surface **#0C1115**, Elevation **#141A20**, Text **#E9F3FA**, Ice **#6FBFEA**, Ember **#FF7A1A**.
* **Gradients:** **#0F1B22 → #152531** (6%).
* **Finish:** cold accents on selection & EQ bands; warm accents on CTAs only.

### 2.9 Forest Ember

* **Mood:** grounded, calm, highly readable.
* **Palette:** Surface **#0D1413**, Elevation **#121A19**, Text **#E8F1ED**, Emerald **#13AB7C**, Ember **#FF7A1A**, Copper **#C78B4A**.
* **Gradients:** **#0F1716 → #14201D** (5%).
* **Finish:** matte surfaces; 1px copper outline on active list rows & chips.

### 2.10 Rose Quartz x Gold

* **Mood:** elegant, subscription-ready.
* **Palette:** Surface **#121014**, Elevation **#18151B**, Text **#F9F1F4**, Rose **#DC7AA1**, Ember **#FF7A1A**, Petal **#FFE1EB**.
* **Gradients:** **#171319 → #201722** (6%); CTA **#F4BFD3 → #DC7AA1** (15%).
* **Finish:** modal headers use soft bokeh; gold rim focus `#FFD27A@24–28%`.

### 2.11 Cyber Ember

* **Mood:** synthy yet grounded.
* **Palette:** Surface **#0A0A12**, Elevation **#121527**, Text **#E8FBFE**, Aqua **#00CFE0**, Ember **#FF7A1A**.
* **Gradients:** **#0A0A12 → #121527** (6%); CTA **#8EF1FF → #00CFE0** (bloom ≤10%).
* **Finish:** subtle grid vignette (4%); neon limited to active tab and play.

---

## 3) App Architecture & Navigation

### 3.1 Primary Navigation

* **Top app bar + tabs** (never bottom tabs):

  * Tabs: **Songs, Playlists, Folders, Albums, Artists, Genres, Audiobooks, Podcasts, Videos**.
  * **Underline** is 2–3px with animated slide; color is warm or theme accent.
  * **Title** on left (bold); **Actions** on right: **Themes**, **Search**, **Settings**.
  * **Hamburger** on left opens drawer (Library, Settings, Equalizer, Sleep Timer, Widgets, Troubleshooting, About).

### 3.2 Mini-Player (Persistent)

* Docked at bottom; 64–72dp height.
* Left: circular art; center: title/artist (single line each, truncation fade); right: play/pause and queue icon.
* **Progress bar** runs along top edge (1–2dp); on press shows a thicker scrub bar.
* **Gestures**: tap expands to Now Playing; swipe up opens Queue; swipe left/right skip tracks (with haptic tick).

**States**

* **Playing**: subtle pulse on halo; progress animates.
* **Paused**: halo off; static art.
* **Casting**: cast badge next to controls; tapping opens route picker.
* **Reduced motion**: no pulse; progress animates linearly.

### 3.3 Drawer

* **Library** (returns home), **Settings**, **Equalizer** (shows current preset name), **Sleep Timer**, **Skin Theme**, **Widgets**. If any playback issues detected, **Troubleshoot background play** appears with orange dot.
* Responsive to theme; icons 24dp; row height 56–64dp; active item highlighted with glow underline.

---

## 4) Onboarding & First-Run Flow

### 4.1 Splash → Warm Ignition

* **Sequence (≤1200ms):** icon appears at 80% scale → slow warm-up (color from graphite to ember) → soft bloom → logo moves slightly upward as a **radial ember reveal** uncovers app background.
* **Audio:** no sounds.
* **Reduced motion:** static fade from icon to home.

### 4.2 Permissions

* Dialog with **left-aligned illustration** (flame faint), precise copy, and bullet list of benefits.
* If user denies, show **non-nagging** rationale card in the library (“Grant media access to see your music”).

### 4.3 Scan Setup

* **Include/exclude** folders UI: chips for common roots; toggles for Downloads, Music, SD card; file types (music, podcasts, audiobooks, videos).
* **Long audio routing** page:

  * “How should Ember treat long audio?” — options: **Audiobooks**, **Podcasts**, **Music**, **Ask each time** (default recommended: Ask).
  * Definition of “long” defaults to **≥20 minutes** with slider (10–60).
* **Scan progress:** giant ring progress with ember sparks orbiting; cancel/resume; ETA text; stats (files found, indexed, deduped).

### 4.4 Theme Selection

* Carousel of theme cards with **Live preview**: mini list, mini player, and play button that glows per theme.
* **Advanced** collapsible: gradient angle/intensity, blur level, true black, icon variant (standard/flat/monochrome).
* Confirm with “**Light it up**” CTA.

---

## 5) Library Screens (Lists)

### 5.1 Common Elements

* **Header**: Section title (“Songs”) in 32–44pt bold; active tab underlined; “X Songs” subtitle.
* **Actions**: Sort (icon with current sort chip), Multi-select (icon), right side; **Shuffle** & **Play** pills beneath header.
* **Row design**:

  * 64–72dp height; 16dp art placeholder with brand glyph; title 16 (700), subtitle 13–14 (500 @70% opacity).
  * Right column: subtle metadata (date, duration) 12–13; overflow `⋮`.
  * **States**: normal, pressed (ink ripple + 1–2dp lift), playing (accent left bar or tiny eq bars), selected (check knob animated in).
* **Empty state**: brand illustration (faint flame), copy + CTA (“Import music” / “Scan folders”), never a dead end.
* **Loading**: shimmer skeletons for image & text lines.

### 5.2 Songs

* **Default sort**: by title (A→Z).
* **Quick filter** chips (optional): Downloaded, High bitrate, Longer than 10m, Has video.

### 5.3 Playlists

* Top: curated smart lists (My favourite, Recently added, Recently played, My top tracks) with gradient icons.
* **Primary CTAs**: “Create playlist” row; **Restore playlist** & **Import playlist** pill buttons with brand glow.

### 5.4 Folders

* **Breadcrumb chips** at top; **folder cover** can be set; filters: music/podcasts/audiobooks/videos.
* Long-press a folder → **Play folder**, **Add to Queue**, **Set as Source** (priority for scanning).

### 5.5 Albums/Artists/Genres

* Grid or list toggle; artwork placeholders use brand glyph in embossed style.
* Overflow per item reflects context (Go to artist/album, Add to playlist, Share album, Edit tags).

### 5.6 Audiobooks & Podcasts

* **Audiobooks**: progress badges (% listened), resume position under title, **speed** per book remembered.
* **Podcasts**: show episode date; skip silence toggle; “Play next unplayed” CTA.

### 5.7 Multi-select

* Enter via icon or long-press row. App bar morphs to “**N selected**.”
* **Select All / Deselect All** chip under the search bar; **range selection**: long-press start, tap end.
* Bottom bar actions: **Play**, **Add to playlist**, **Delete** (destructive last), **Hide**.

### 5.8 Sort Sheet

* Sticky header “Sort by”; radio groups (Field & Direction); chips show last 3 presets.
* **Preview** at top (small list fragment reflects choice in place).
* “OK” confirms; “Cancel” restores.

### 5.9 Per-item Overflow

* **Header card** with art, title, meta `duration | bitrate`, two icons: Info (metadata sheet), Share/Refresh as configured.
* Actions grouped: Queue, Library, Navigate, Edit, System, Visibility, Destructive.
* **Sheet motion**: spring up from 0% → 100% with 12dp over-shoot then settle.

---

## 6) Now Playing (The Showcase)

### 6.1 Composition

* **Top bar:** collapse chevron; segmented control **Song | Lyrics** (animated underline); Theme icon; Overflow.
* **Hero area**:

  * **Artwork tile** with 16–24dp corner radius and soft shadow.
  * **Video canvas** (if track has linked video): seamlessly replaces artwork; frame-safe controls.
* **Title block**: Track title (22–24, 700), artist (14–16, 500 @70%).
* **Context chip row** *(all animated)*:

  * **Favorite** (heart)
  * **Add to playlist** (+ card)
  * **Equalizer** (sliders; with scope badge: Global/Song/Album/Artist/Genre)
  * **Sleep timer** (clock with tiny ring when active)
  * **Queue** (stack with count badge)
* **Transport strip**:

  * Left **−10s** chip (arc sweep animation) and right **+10s**.
  * Seek bar with floating tooltip; A-B loop markers if set.
  * **Controls**: Shuffle, Prev, Play/Pause (large), Next, Output (speaker/cast).
  * Repeat cycle: Off → All → One (morph with rotate).

### 6.2 Motion & Micro-interactions

* **Play/Pause**: triangle ↔ pause bars morph with scale bounce; soft glow pulse (≤10% opacity) behind; thin ambient ring rotates slowly while playing.
* **Seek**: thumb leaves faint ember trail; releasing snaps with elastic ease; tooltip counts hh:mm:ss.
* **Prev/Next**: artwork slides 32dp with parallax; background palette crossfade; medium haptic.
* **Favorite**: outline → filled using gradient fill; **3–5 sparks** rise then fade out in 200–250ms; unfavorite reverses.
* **Add to playlist**: plus rotates 90° as a mini **glowing chip** drops into selected playlist tray (visual metaphor only; not literal drag).
* **Equalizer**: on tap the sliders icon staggers briefly (300ms preview) before the sheet opens.
* **Sleep timer**: clock hand sweeps 45°; ring shows countdown.
* **Queue**: lines slide up on icon; sheet rises with a drag handle and **3D depth**.

### 6.3 Lyrics Mode

* Centered karaoke view; **active line** scales to 110% with warm glow; previous/next lines dim and blur slightly.
* Tap on a line → smooth seek.
* Manual **Offset** control (±5s) with slider and preview.
* Font options: Sans (default), Serif (lyrical), Mono (accessibility), with 3 sizes.
* **No lyrics**: empty card with “Add lyrics” explanation.

### 6.4 Video Mode (MP4 Everywhere)

* Artwork flips to **PlayerView**; background darkens, controls lift for contrast.
* **PiP** on Home or App Switch; re-enter returns to exact position.
* Dragging seek shows thumbnail filmstrip if available; else time markers only.

---

## 7) Equalizer & Audio Tools

### 7.1 Equalizer Screen (Molten Lab)

**Scene**: dark canvas with faint **breathing flame watermark** behind sliders (scale 0.95–1.05 @ 6–8% opacity, synced to RMS). Sparse **ember particles** drift upward (4–6 per second), throttled when battery saver on.

**Header**

* Title “Equalizer”; master toggle On/Off (pill with subtle rim light when On); **A/B** compare switch; icon for **Presets** drawer.

**Presets row** (scrollable chips)

* Custom (active), Normal, Rock, Pop, Hip-Hop, Jazz, Classical, Acoustic, Electronic, Vocal Boost, Treble Boost, Bass Boost, Piano, Lounge, Deep, Loud, Flat.
* **Interactions:** tap to apply; **press-and-hold** to audition while pressed (reverts on release); tiny sparkle on selection.
* “+ Save as preset” chip opens naming sheet.

**Sliders**

* Default **5-band**: 60Hz, 230Hz, 910Hz, 4kHz, 14kHz.
* **Advanced toggle** reveals **10-band** (31, 62, 125, 250, 500, 1k, 2k, 4k, 8k, 16k) via spring reveal.
* Track: vertical glass with tick marks every 3dB.
* Thumb: **molten orb** (subtle refraction & heat shimmer when dragged).
* Label bubble near thumb shows dB (+12 → −12).
* Double-tap resets band to 0 dB; long-press opens numeric input.
* **Two-finger drag** bends adjacent bands (Q-style curve)—pro feature.

**Gain & Safety**

* **Output Gain** slider (−12 to +12 dB); **Auto Gain** toggle (soft limiter, gentle knee). When clipping risk detected, the meter pulses red and a short label appears: “Loudness protected—Auto Gain adjusted.”

**Effects Card**

* **Reverb** (None, Small, Medium, Large, Hall, Plate)—rotary knob with inertia.
* **Bass Boost** & **Virtualizer**—rotary knobs; level labels; reset on double-tap.

**Profiles by Route**

* Profile pills: **Device, Headphones, Bluetooth, Car**. Auto-apply on route change; small toast: “Bluetooth profile active”.

**Footer**

* Reset All (ash → pristine animation), Save Preset, Share (exports JSON).

**Accessibility**

* TalkBack describes sliders: “60 Hertz, plus three decibels, adjustable.”

### 7.2 Per-Song/Album/Artist/Genre EQ

* **EQ badge** in Now Playing displays the scope (e.g., “Song”).
* Tap → **EQ sheet** with tabs: **Song** | Album | Artist | Genre | Global (read-only).
* Same UI as Equalizer screen, but scoped.
* **Precedence**: Song → Album → Artist → Genre → Output route → Global.
* Tiny flame dot next to track title when any scoped profile applies.
* **A/B** works inside the sheet; “Revert” returns to inherited profile.

### 7.3 Ringtone & Clip Studio

* **Waveform** fills the top; pinch-to-zoom to frames; two handles set start/end (with magnified loupe).
* **Loop preview** between handles; fade in/out toggles with 0–1000ms sliders; **normalize** checkbox.
* **Length** indicator shows “28.4s” live.
* **Category chips**: Ringtone / Notification / Alarm; option to **Assign to contact**.
* **Set** action: gradient pill morphs to check; Snackbar “Ringtone set • Undo”.
* If WRITE_SETTINGS denied: “Saved trimmed file. Here’s how to set manually” with link.

---

## 8) Widgets & External Surfaces

### 8.1 Widget Gallery (in-app)

* Large preview cards for each widget; size chips; “ADD” button (pin flow).
* **Families** (all resize-aware):

  1. **Flame Minimal (4×1)** — glass pill, art thumb, title/artist, play/next/favorite.
  2. **Flame Card (4×2)** — big art, progress line, prev/play/next, queue toggle.
  3. **Vinyl (4×2)** — rotating disc, pause stops rotation; flame at center.
  4. **Circular (2×2)** — ring progress around play; tap center toggles.
  5. **Full Art (4×4)** — blurred art background, large controls.
  6. **Standard (4×2)** — compatibility layout (no glass).
  7. **Mini (2×2)** — icon + play; no text.
  8. **Stacked (4×2)** — two rows: marquee title, controls.
  9. **Icon (1×1)** — flame; long-press actions (Search, Shuffle All).

### 8.2 Widget Customization

* **Theme**: Follow app theme (default) or per-widget palette (album-blur, transparent, solid).
* **Accent**: Ember, cool accent, or auto from album art.
* **Controls**: show/hide prev/next/favorite/queue, **read-only progress** or ±10s tap zones.
* **Corners & Glass**: radius 10–28dp; glass intensity slider.
* **Text**: size S/M/L, bold titles, two-line option, marquee on overflow.
* **Behaviors**: vinyl spin; pulse on beat (battery-aware); launch target (Now Playing vs Library).
* **Live preview** updates as you tweak; **Reset** returns to defaults.

### 8.3 Lock Screen, AOD, Notification, Quick Settings

* **Lock screen/AOD**: full-bleed art; ember glow control cluster; ring progress; reduced motion obeyed.
* **Notification**: dynamic color; compact/expanded; large art; mini waveform progress (if supported).
* **Quick Settings tile**: play/pause toggle; long-press opens Now Playing; secondary action cycles audio route.

---

## 9) Search & Commanding

* **Global search screen**: rounded field, back arrow, keyboard focus.
* **Scoped tabs**: All / Songs / Albums / Artists / Playlists / Folders / Podcasts / Audiobooks / Videos.
* In **All**, group results with headers; show 3–5 each with “View all →”.
* **Filters**: chips for Downloaded, Duration, Has video, Bitrate, Added recently.
* **Type-ahead** suggestions with highlighting; **long-press** an item for quick actions (Play next, Add to queue, Go to album/artist).
* **Command palette** (optional power feature): from top bar or shortcut—run commands like “shuffle all,” “open EQ,” “scan downloads.”

---

## 10) Share Audio (Critical)

* **Share from Ember**

  * Single track: share the audio file (when permitted) or a share card with title/artist/cover and a “Play in Ember” deep link.
  * Playlist: export `.m3u8` + cover collage; or share selection UI.
  * **Microcopy:** “Shared via **Ember** 🔥”.
* **Share to Ember**

  * Accept **audio/video** via Android share sheet; prompt: “Add to queue, Play now, or Save to…”.
  * If playing, overlay toast: “Added ‘Track’ to Queue.”

---

## 11) Settings (Detailed)

### 11.1 General

* Scan music (manual), Hidden music manager, Backup & restore (timestamp), Language, Keep screen on, Widgets configuration.

### 11.2 Playback

* Gapless (on by default), Crossfade slider (0–12s; disabled for podcasts/audiobooks unless opted in), Volume leveling (EBU R128 style) with description, Skip silence (for podcasts/audiobooks), Replay gain use (track/album).

### 11.3 Appearance

* Theme chooser, icon variant, blur intensity, glass level, true black toggle, accent choices, Reduce Motion, High Contrast.

### 11.4 Behavior

* Headset controls mapping (double-press next, triple previous; long-press toggles video), Resume on Bluetooth connect, Battery optimization helper (with OEM-specific guides).

### 11.5 Help & About

* FAQ, Feedback, Rate us, Troubleshoot background play, Privacy policy, Terms, Version & build.

### 11.6 Advanced

* Export/import data (library, EQ, themes, presets), Developer diagnostics (logs), Reset to defaults.

**Toggle behavior**

* Switches animate with **quiet spring**; labels update immediately; provide context hint on disabled actions.

---

## 12) Accessibility & Inclusivity

* **TalkBack**: complete labels and states. Example: “Favorite, on”; “Equalizer, song profile active, +3 dB at 60 hertz.”
* **Focus order**: left→right, top→bottom logical traversal.
* **Large text**: reflow; avoid clipping; dynamic height lists.
* **Color-blind safe options**: alternate accents; never encode status purely by color.
* **Reduce Motion**: turns off ember particles, live visualizers, beat pulses; replaces morphs with fades; keeps responsiveness.
* **Haptic profiles**: Off / Soft / Full; consistent: light tick (tap confirm), medium tick (skip, add), long buzz (destructive).

---

## 13) Performance & Resilience

* **Frame budget**: ≤16.6ms per frame; seek & queue drag never jank.
* **Startup**: pre-warm fonts & icons; lazy-init heavy processors; splash ≤1200ms.
* **Foreground service**: proper audio focus handling; call interruptions safe; network independent.
* **Persistence**: theme, view state per tab, sort, selection, queue position, speed/EQ remembered; survive process death.
* **Battery awareness**: throttle visuals in low power/thermal; pause heavy animations when app backgrounded.
* **Scanning**: resumable; progress visible; cancellation safe.

---

## 14) Privacy & Trust

* **Offline by default**; explicit consent for any online lookups (e.g., cover art).
* **Encrypted backups** using user-provided key; selective restore.
* **Private Vault** (optional): biometric/passcode gate; content excluded from search, widgets, notifications.

---

## 15) Delight & Rituals

* **Ignition**: at launch, just once per cold start, never when returning from recents.
* **Milestones**: first playlist, 1k minutes listened—tiny ember confetti (opt-out).
* **Listening Insights**: time listened; top artists/albums/tracks; **Year in Sound** montage (on-device video export).
* **Easter egg**: long-press logo on Now Playing triggers ambient flame screensaver (tap to exit).

---

## 16) Copy & Tone (selected microcopy)

* Theme chooser: “**Choose your look** — all themes are easy on the eyes.”
* EQ on: “**Ignited. Equalizer is on.**”
* Save preset: “Preset **saved** to your library.”
* Ringtone set: “**Set!** • Undo”
* Battery helper: “Keep music alive in the background—allow Ember to run without restrictions.”
* Long audio routing: “We noticed long tracks. Treat these as: Audiobooks / Podcasts / Music / Ask me.”

---

## 17) Visual QA Checklists (Definition of Done)

**App Icon & Splash**

* [ ] Adaptive icon glyph matches reference exactly; monochrome variant clean at small sizes.
* [ ] Splash ignition animation duration ≤1200ms; reduced-motion path verified.

**Themes**

* [ ] All 11 themes match palette and gradient specs; text contrast AA/AAA; glow ≤12%; one gradient per component max.

**Navigation & Lists**

* [ ] Top tabs with underline animation; right-side actions visible; no bottom tabs.
* [ ] Row spacing, typography, and metadata placement match spec; loading skeletons present; empty states tasteful.

**Multi-select & Sort**

* [ ] Selection knob animation present; bottom action bar slides in with depth.
* [ ] Sort sheet grouping & preview; presets remembered per tab.

**Now Playing**

* [ ] Context chip animations; play/pause morph; seek ember trail; prev/next parallax.
* [ ] Lyrics synced; offset control; tap-to-seek; video flip & PiP stable.

**Queue**

* [ ] Springy sheet; drag reorder with haptic ticks; swipe to remove; undo snackbar.

**Equalizer**

* [ ] 5-band & 10-band; molten orb thumbs; A/B compare; Auto Gain limiter; per-route profiles.
* [ ] Per-song/album/artist/genre scoping with precedence and badge; revert path.

**Ringtone/Clip Studio**

* [ ] Pinch-zoom waveform; A/B handles with loupe; fade/normalize; category chips; set/undo; permission fallback message.

**Widgets**

* [ ] Gallery previews; pin works; resize behavior correct; live updates <250ms; survive reboot.
* [ ] Customization options present; follows app theme or overrides cleanly.

**Search**

* [ ] Grouped results; filters; quick actions via long-press; scoped tabs.
* [ ] Command palette (if included) shows sensible actions.

**Accessibility**

* [ ] TalkBack scripts pass; large text safe; Reduce Motion swaps to fades; high-contrast theme available.

**Performance**

* [ ] Seek & queue drag at 60fps; lists smooth on 10k items; fast cold start.
* [ ] Visualizers/particles throttle on low battery/thermal.

**Share Audio**

* [ ] Share from Ember includes file/playlist and pretty share text; share to Ember enqueues or plays now with clear UI.

---

## 18) “Looks & Feels” — Paint-with-Words for Key Screens

### 18.1 Home / Songs

The background is a low-noise charcoal gradient that feels like velvet. The section title “**Songs**” sits confidently, its underline a thin ember stroke that slides like liquid when switching tabs. The Shuffle and Play pills have a satin finish; when pressed, their glow blooms just enough to feel alive, then retreats. Each row lines up like a rhythm—art on the left (embossed glyph if missing), title crisp, artist/subtitle soft. The right edge whispers metadata and three dots. Nothing screams; everything breathes.

### 18.2 Overflow Sheet

A glassy panel rises with a gentle overshoot. A compact track card in the header shows the art with a faint inner glow. Actions are grouped with thin dividers; icons are quiet but pristine. On press, a ripple and 1dp lift suggest tactility. Close sits at the bottom, a rounded bar that feels like polished stone.

### 18.3 Now Playing

The art floats like a piece of glass above the surface, haloed only by ambient light. The play button is the heart—a molten droplet whose pulse tracks the music. Scrubbing draws a faint ember trail that curls away as your finger lifts. The lyric view is candle-lit karaoke; the active line warms as it passes, then cools. Pulling up the queue feels like grabbing a physical card from the table; it has weight, it springs, it yields.

### 18.4 Equalizer

The lab glows faintly from within. Sliders stand like glass pillars with molten orbs for thumbs. Move one and a heat shimmer trails behind for a split second. Flip A/B and the sound—*and* the scene—crossfades almost imperceptibly, like a room’s mood changing. The reverb knob turns with tiny resistance, a tactile illusion that makes you smile.

### 18.5 Ringtone Studio

A crisp waveform sits on a smoked-glass bed. Handles grip with a satisfying snap. Looping the selection feels like winding a music box—soft bumps at the loop points, gentle bounces as you tweak length. Tapping “Set as ringtone” sends a narrow flame lick across the button before it locks in with a check.

### 18.6 Widgets

On the home screen, the Flame Card widget looks like a real piece of glass laid on the wallpaper—light catches its edges. The play button glows when music plays; the vinyl widget rotates with just enough inertia that it feels physical.

---

## 19) Guardrails & Anti-Patterns

* No bottom navigation bar.
* No loud neon floods; glow is seasoning, not sauce.
* No bright accent on large text blocks.
* No long animations that delay task completion.
* No gesture that lacks a visible counterpart (all gestures have visible buttons too).
* Never lose user state (sort, filters, queue, position).

---

## 20) Roadmap Hints (What “Great” Looks Like Over Time)

* **Car Mode**: simplified giant-touch UI with auto dark contrast and voice actions.
* **Wear OS**: mini controller with art and basic controls.
* **Android TV**: leanback browse + Now Playing with visualizer.
* **Listening Insights “Year in Sound”**: on-device montage that people want to share.

---

This blueprint is the **canonical definition** of Ember’s product experience. When auditing work, judge it against:

* **Brand fidelity** (icon, colors, surfaces),
* **Motion quality** (curves, timing, no jank),
* **Interaction polish** (haptics, feedback),
* **Accessibility** (labels, contrast, motion),
* **Performance** (responsiveness),
* **Delight** (tasteful sparks that make you smile).

If any element feels merely “functional,” push it to **functional *and* gorgeous**—quiet glows, impeccable spacing, precise timing. That’s what sets Ember apart.
