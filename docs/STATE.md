# D3-B — Now Playing Surface Polish

Phase B1 complete (palette backdrop + inner-shadow artwork).

Summary
- Added `PaletteBackdrop` and `InnerShadowCard` composables.
- Updated `NowPlayingV2` to use palette-driven backdrop with graceful fallback.
- Added subtle play/pause pulse scaling on artwork.
- Theme: introduced gentle alpha tokens for backdrops.

Notes
- No heavy blur; gradients and multiply edge darkening only.
- Backdrop cross-fades on track change; falls back to ember gradient when art/palette missing.

Next
- B2: Status chips (EQ On + Sleep countdown) and a11y strings.

Branch/PR
- Branch: `codex/dev`
- Commit title: `refactor(ui/nowplaying): palette-driven backdrop + inner-shadow artwork card with subtle play pulse`

Phase B2 complete (status chips + a11y).

Phase B3 complete (mini-player palette stripe + breathing ember dot refinement).

Build fix: set Compose BOM to 2024.12.01 (2024.12.00 does not exist).

Fix: cleaned duplicate package blocks in InnerShadowCard.kt and MiniPlayer.kt by rewriting files to final single-definition versions.

Queue Q1: Added undo snackbar for removals and animateItemPlacement for reorder feedback in QueueSheet; exposed queueEntries and insert/remove snapshot helpers in PlaybackViewModel.

Batch 1: Now Playing chips — crossfade, tick haptics, success on start, and live a11y announcements; added chip_eq_on/chip_sleep_countdown/cd_chip_updates strings.

Queue Q2: Drag-reorder via long-press drag handle with animateItemPlacement and TalkBack announcements; undo snackbar retained.

Search Q1: Added All tab with section headers and View all actions; 80ms debounce, IME action=Search, clear button, match highlighting preserved.

D3-B polish: art pulse tuned (90/120ms); minor UI glowPress feedback on controls.

Theme Studio v1: Added proto fields and screen; dynamic accent + background (charcoal/ember gradient/custom image with blur+dim); persisted via DataStore; applied via EmberTheme.

M1 Continuation — September 14
- Now Playing chips: placed with updated strings (eq_on_chip, sleep_chip_prefix), tick haptics on cancel, live announcements.
- Queue: drag handle uses long-press gesture; announces moved up/down; undo still available.
- Search: ~90ms debounce; sticky section headers; IME Search hides keyboard; empty states.
- Theme: extended tokens (accent preset/custom, gradient intensity/vignette, font, icon style); typography applied via Google Fonts provider.
- Polish: PaletteBackdrop crossfades at 220ms.
