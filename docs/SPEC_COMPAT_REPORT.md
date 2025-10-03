# SPEC COMPATIBILITY REPORT — Ember

Version: {{today}}

## A. Canonical Decisions (single source of truth)

- **App name & tagline:** Ember — “Your Audio Player.” (Golden §0, §1)  
- **Tabs:** Songs · Playlists · Folders · Albums · Artists · Genres · Audiobooks · Podcasts · Videos (Golden §5)  
- **Colors:** Use Golden tokens exactly (Golden §1).  
- **Motion:** Fast 120ms · Standard 220ms · Gentle 320ms · Emphasis 420ms; cubic(0.2,0,0,1) (Golden §3).  
- **Onboarding order:** Hello → Permissions → Scan Options → Theme → Finish (Golden §4).  
- **Long-audio routing:** In Scan Options; Audiobooks/Podcasts/Keep in Music (Golden §4).  
- **EQ:** 10-band parametric engine; UI offers 5-band Simple + 10-band Advanced (Execution detail; Golden allows 10-band).  
- **Widgets:** Glance primary; RV fallback (Golden §9).

## B. Conflicts found

1) **EQ bands:** Golden mandates 10-band capability; previous Execution drafts mentioned “5-band default.”  
   **Resolution:** Keep 10-band engine; offer 5-band **UI** mapped onto 10-band. Execution updated.

2) **Widgets framework:** Golden specifies Glance; earlier notes mentioned RemoteViews.  
   **Resolution:** Glance **primary**, RV fallback. Execution updated.

3) **Motion numbers:** Execution previously listed alternative durations.  
   **Resolution:** Adopt Golden timings/curves. Execution updated.

## C. Edits applied

- Added “Precedence” block to both specs.  
- Synced motion/color tokens in Execution to match Golden.  
- Clarified EQ UI mapping.  
- Affirmed Glance-first widget approach.

## D. Next steps

- Land this report with the Execution doc.
- Enable CI `spec_lint.sh` to prevent future drift.
