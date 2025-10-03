# EXECUTION BLUEPRINT (Cursor Playbook) — Ember: Your Audio Player
Version: 2025-10-01

**Precedence:**  
1) If anything conflicts, naming/brand/tabs/visuals = **docs/EMBER_GOLDEN_BLUEPRINT.md**.  
2) Durations/curves/impl details = this file, unless Golden provides exact numbers.  
3) If both specify numbers → **Golden wins**; update this file accordingly.

---

## Global Rules (applies to every PR/task)
- **Tokens only:** use `core/design/Tokens.kt` for color/gradients/glass/radii/motion.  
- **Motion tokens:** Fast 120ms · Standard 220ms · Gentle 320ms · Emphasis 420ms, easing `cubic(0.2,0,0,1)`.  
- **A11y:** AA contrast; 48dp targets; TalkBack; RTL; Large Text.  
- **Media3:** no file-level `@OptIn`. Use `core/media/adapters/*`.  
- **Performance:** p90 frame ≤16.6ms (scroll, open sheet, scrub, reorder).

---

## Where to look (in this order)
1) `docs/STATE.yml` (phase, checkpoints)  
2) `docs/NEXT.md` (top 3 tasks; always current)  
3) `docs/EMBER_GOLDEN_BLUEPRINT.md` (product intent)  
4) The sections below for exact files & animations

---

## PHASE 0 — Stabilize & Tokens (CURRENT)
**Goal:** Build clean; token system live; motion helpers ready; no raw hex in UI code.

**Do**
1. Add/migrate to `core/design/Tokens.kt` (colors/gradients/radii/glass/motion).  
2. Add `EmberTheme` and wrap `setContent` with it (do not break existing screens).  
3. Create `docs/CHANGELOG/` and write `YYYY-MM-DD_phase0_01.md` with the local change-log template.  
4. Update `docs/STATE.yml` → `phase: 0`, check off `tokens: true` when done.  
5. Update `docs/NEXT.md` with Phase 0 next steps.

**Pass**
- Build green. No raw `Color(0xFF…)` in modified files.
- Splash/app bar can reference tokens.

---

## PHASE 1 — Brand & Splash
**Files:** `feature/onboarding/ui/Splash.kt`, adaptive icon layers in `res/mipmap-*`

**Animation**
- Ignite reveal: 900–1100ms sequence made of 3 parts (Gentle 320ms segments):  
  1) Flame glyph scale 0.96→1.00  
  2) Ember gradient sweep (18°) with 8–12% bloom  
  3) Crossfade to Home (Standard 220ms)
- Reduced Motion → single 220ms crossfade.

**Pass**
- Zero dropped frames; icon silhouette exact.

---

## PHASE 2 — Onboarding (Hello → Permissions → Scan → Theme → Finish)
**Long audio**: in **Scan** step, offer **Audiobooks / Podcasts / Keep in Music** for ≥20m.

**Motion**
- Panels: slide/fade (Gentle 320ms).
- Sheen on primary CTAs (Fast 120ms).

**Persist**
- `scanFolders`, `includeTypes`, `longAudioRouting`, `themeId`, `trueBlack`, `blurLevel`, `dynamicColor`, `highContrast`, `reducedMotion`, `colorSafe`.

---

## PHASE 3 — Library Tabs
Tabs (scrollable): **Songs · Playlists · Folders · Albums · Artists · Genres · Audiobooks · Podcasts · Videos**

**Micro-interactions**
- Tab underline slide (Fast 120ms) + content translateY 6dp.
- Row press ripple + 1dp lift.
- Shuffle/Play pills: glow pulse on state; sheen 120ms.
- Multi-select bar spring-in (Standard 220ms).

**Overflow sheet actions (order fixed):**
Play next · Add to queue · Add to playlist · Go to album · Go to artist · Edit tags · Change cover · **Share** · **Set as ringtone** · Hide song · Delete from device · Info.

---

## PHASE 4 — Now Playing + Queue
**Modes:** Song | Lyrics | Video  
**Chips:** Favorite • Add • **EQ (scope badge)** • Sleep • Queue  
**Transport:** ±10s chips; ember-trail scrubber; tooltip time pill

**Animations**
- Play/Pause morph + glow pulse (≤24 sparks, 180ms).  
- Artwork parallax on prev/next; backdrop palette crossfade (Standard 220ms).  
- Lyrics karaoke glow; seek previews active line.  
- Video flip-in with shutter; PiP on collapse.

---

## PHASE 5 — Equalizer & Per-Song Profiles
**Engine:** 10-band parametric (or Android AudioEffect fallback).  
**UI:** 5-band **Simple** (60/230/910/4k/14k) mapped onto engine; 10-band **Advanced** tab.

**Per-song EQ**
- From Now Playing EQ chip: tabs Song/Album/Artist/Genre/Route/Global; A/B audition (press-to-preview).
- Apply within <100ms; no pops (10–20ms ramp).

---

## PHASE 6 — Share / Import / Tag / Ringtone
**Share**: original/copy/clip/playlist/card; transcode; normalize; secure Content URIs.  
**Receive**: Play/Add/Save/Clip.  
**Ringtone Studio**: waveform, pinch zoom, fades, normalize.

---

## PHASE 7 — Widgets (Glance primary; RV fallback)
Templates: Flame Minimal (4×1), Flame Card (4×2), Vinyl (4×2), Circular (2×2), Full Art (4×4), Standard (4×2), Mini (2×2), Stacked (4×2), Icon (1×1).  
Designer: per-widget theme/accent/corners/glass/controls.

---

## PHASE 8 — Engine Excellence
Gapless + smart crossfade; ReplayGain; Silence Skip; Output profiles; Hi-Res where supported.

---

## PHASE 9 — Settings & Privacy
All toggles live, with clear summaries; battery-optimization helper workflow.

---

## PHASE 10 — A11y & Performance QA
TalkBack scripts, large-text/RTL screenshots; macrobenchmarks for splash/tab/now-playing.

---

## Always update
- `docs/STATE.yml` — phase, checklist  
- `docs/NEXT.md` — top 3 tasks  
- `docs/CHANGELOG/…` — what changed & why (template)


