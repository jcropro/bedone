# Ember Audio Player — M0 QA Checklist

## Build
- From project root (Windows): `gradlew.bat clean assembleDebug`
- Install the APK on an emulator/device via Android Studio or `adb install -r app\build\outputs\apk\debug\app-debug.apk`.

## First Run & Permissions
- Launch the app; grant media and notifications permissions when prompted.
- If no audio appears on the Songs tab, go to Folders and add a folder via the system picker. Ensure the app requests and persists URI permissions.
  - Also test adding an Excluded folder; verify items under it no longer appear after a rescan.

## Library Scan
- After launch, verify a toast and accessibility announcement: “Scanned N items” when new items are found.
- Relaunch app; if no new media, announcement may be omitted (no duplicates).

## Playback & Controls
- From Songs, tap an item to start playback. Verify:
  - MiniPlayer shows the title; Play/Pause toggles work.
  - Now Playing sheet: position slider scrubs; prev/next/shuffle/repeat operate.
  - Now Playing: verify shuffle and repeat icons reflect current state (highlighted when active).
  - Use ±10s seek buttons; confirm time labels update correctly.
  - Adjust Speed slider (0.5x–2.0x); confirm playback rate and persistence across relaunch.
  - TalkBack: Play/Pause announces “Playing/Paused”.
- Crossfade & Skip Silence:
  - Open Now Playing; toggle Skip silence.
  - Enable Crossfade and adjust slider; verify audio ramps between tracks.
- MiniPlayer: verify album art thumbnail shows beside the title.

## Settings
- Tap the gear icon; verify Settings screen loads.
- Toggle Skip silence and Crossfade; adjust Fade slider; confirm behavior during playback.
- Adjust Long-form threshold slider; ensure value persists across relaunch.
- Tap Manage folders to open Folders screen.
- Tap Rescan library and confirm media scan runs (toast and a11y announcement on MainActivity).
  - After adding/removing included/excluded folders, rescan to validate changes reflect in Songs/Albums/Artists.

## Search
- Tap the search icon in the top app bar to open Search.
- Type to see live results; verify:
  - Songs: title + artist/album in subtitle.
  - Albums: title + artist subtitle.
  - Artists: name only.
- Use filter chips (Songs/Albums/Artists) to switch result types.
- Tap a Song to play immediately; tap an Album/Artist to navigate to its detail screen.
- Verify query highlighting appears in titles/subtitles for matches.
- Thumbnails: verify album art shows for Song and Album results; placeholder when missing.
- Songs filter: open overflow menu or long‑press a row; test Play next / Add to queue / Add to playlist behaviors.

## Queue
- From song row menu, Add to queue and Play next, then open Queue:
  - Reorder, play at index, and remove items; verify changes take effect.
  - Close app, relaunch; verify queue snapshot persists (order, current index, position).
- Swipe an item left or right to remove; confirm removal and no unintended deletions when scrolling.

## Albums & Artists
- Albums/Artists tabs list items; tap through to details:
  - Empty detail screens show a simple “No tracks yet” state.
  - Album details: header shows album art (if available) and Play all / Shuffle all.
  - Artist details: header shows artist name with Play all / Shuffle all.
  - Rows use 64dp height and 16dp horizontal padding with thumbnails.
  - Sorting:
    - Albums: use Sort to switch between Title A–Z and Z–A.
    - Artists: use Sort to switch between Name A–Z and Z–A.
  - Album art loads when available; dominant color subtly tints MiniPlayer.

## Accessibility (TalkBack)
- Lists are announced as lists; row labels read as “Song/Album/Artist/Playlist: <name>”.
- Overflow/menu icon reads “More options”.
- MiniPlayer is a button labeled “Now playing”; tapping Play/Pause announces changes.
- Now Playing position slider labeled “Playback position”.

## Performance
- Scroll large lists; ensure smoothness.
- Observe memory in Android Studio Profiler while navigating:
  - Confirm album art bitmaps are reused (ArtCache) and dominant color extraction feels snappy (PaletteCache).

## Visual Polish
- Verify spacing/margins align visually: 16dp list padding, 64dp rows, tab indicator animates ≈180ms.

## Regression Sweep
- Playlist creation and adding tracks via deep-link parameter (from Songs → Playlists) works.
- Folder management add/remove behaves and persists.
- App resumes last playback or queue without auto-playing on cold start.
- Songs list: long-press a row opens the same actions as the overflow menu.
- Playlists: long-press or overflow menu on a playlist row opens Rename/Delete. Verify rename persists; verify delete removes playlist (and entries) without crashing.
- Share: from Songs/Search/Playlist item, use Share; confirm intent chooser appears and audio URI is shared.
- Last tab: navigate to a different top tab, exit app, relaunch; verify the app restores to the last selected tab.
- Smart playlists:
  - In Playlists, use Recently added / Most played / Recently played shortcuts.
  - Verify lists load and play; Most/Recent rely on play history tracked on transitions.

---
If any issues appear, note screen, steps, expected vs. actual, and logs (logcat) for follow-up.
- Songs: try Sort (Title, Date added, Duration); verify sorting updates and persists across relaunch.
