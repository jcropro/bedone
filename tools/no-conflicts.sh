#!/usr/bin/env bash
set -euo pipefail
# Fail if any conflict markers exist
if git grep -nE '^(<<<<<<<|=======|>>>>>>>)' -- ':!tools/no-conflicts.sh' ':!tools/sanity.sh' >/dev/null; then
  echo "ERROR: Conflict markers found. Resolve and commit clean files."
  git grep -nE '^(<<<<<<<|=======|>>>>>>>)' || true
  exit 1
fi
# Fail if local.properties is tracked
if git ls-files --error-unmatch local.properties >/dev/null 2>&1; then
  echo "ERROR: local.properties is tracked. Run: git rm --cached local.properties"
  exit 1
fi
echo "✅ No conflict markers; local.properties not tracked."
