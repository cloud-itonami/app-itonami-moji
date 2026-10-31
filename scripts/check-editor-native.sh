#!/bin/sh
# Runs src/moji/editor.kotoba's own checks on the native Amu CLI.
#
# The native public entry admits one exported `main [] :i64`, and its checker
# requires every export to return :i64, so the app exports (init/view/step,
# which return :document) cannot be compiled there as they stand. This copies
# the source with its `(:export [...])` list narrowed to `[main]` -- every
# definition byte is unchanged -- and runs it. `main` returns how many checks
# pass; the expected count is the number of `test-*` definitions.
#
# Usage: scripts/check-editor-native.sh [path/to/amu]   (default: amu on PATH)
set -eu
here=$(cd "$(dirname "$0")/.." && pwd)
amu=${1:-amu}
src="$here/src/moji/editor.kotoba"
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT
python3 - "$src" "$work/editor.kotoba" <<'PY'
import re, sys
s = open(sys.argv[1], encoding="utf-8").read()
t, n = re.subn(r"\(:export \[[^\]]*\]\)", "(:export [main])", s, count=1)
if n != 1:
    sys.exit("no (:export [...]) form found")
open(sys.argv[2], "w", encoding="utf-8").write(t)
PY
expected=$(grep -c '^(defn test-' "$src")
got=$(cd "$work" && "$amu" run editor.kotoba | tail -n 1)
if [ "$got" = "$expected" ]; then
  echo "ok: $got/$expected checks pass"
else
  echo "FAIL: $got/$expected checks pass" >&2
  exit 1
fi
