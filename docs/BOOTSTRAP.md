# BOOTSTRAP — How Codex must start every session
1) Read: AGENTS.md, docs/MASTER_PROMPT.md, docs/STATE.yml, docs/NEXT.md
2) Echo back the current_workstream and planned file edits (full paths).
3) Only then, apply changes. Always emit **full file contents**.
4) Run: `./gradlew :core-ui:compileDebugKotlin :app:assembleDebug`
5) Update docs/STATE.yml (commit SHA, files changed, next_actions).
6) Open a PR using the PR template. Do NOT squash multiple concerns.
