# NEXT — top 3 actions only (do not grow this list)

1) Now Playing background: add smoke instrumentation test to assert palette‑based background color changes when switching tracks (generous timing; tolerate missing artwork).
2) Share flow tests: instrument Now Playing and row actions to verify content URIs + persisted grants are attached, and text‑only fallback when no stream exists.
3) Crossfade polish: guard with explicit capability checks, add instrumentation hooks to validate fade‑out/in around transitions, and document fallback behavior.

> Rule: Keep stabilization closed unless a regression is found. Focus on functional coverage and reliability of core flows.
