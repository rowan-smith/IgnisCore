#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DEST="$ROOT/extensions"
EXTENSIONS_REPO="${IGNIS_EXTENSIONS_REPO:-https://github.com/rowan-smith/IgnisCore-Extensions.git}"
FALLBACK_BRANCH="${IGNIS_EXTENSIONS_FALLBACK_BRANCH:-extensions-export}"
CORE_REPO="${IGNIS_CORE_REPO:-https://github.com/rowan-smith/IgnisCore.git}"

if [[ -f "$DEST/pom.xml" && -d "$DEST/blocks" && -d "$DEST/items" ]]; then
  echo "extensions/ already present at $DEST"
  exit 0
fi

tmpdir="$(mktemp -d)"
cleanup() { rm -rf "$tmpdir"; }
trap cleanup EXIT

src=""
if clone_dir="$(mktemp -d "$tmpdir/clone.XXXXXX")" \
    && git clone --depth 1 "$EXTENSIONS_REPO" "$clone_dir" 2>/dev/null \
    && [[ -f "$clone_dir/pom.xml" && -d "$clone_dir/blocks" ]]; then
  src="$clone_dir"
  echo "Using bundled extensions from $EXTENSIONS_REPO"
else
  rm -rf "$tmpdir"/clone.*
  fallback_dir="$(mktemp -d "$tmpdir/fallback.XXXXXX")"
  echo "Primary extensions repository unavailable; using $CORE_REPO branch $FALLBACK_BRANCH"
  git clone --depth 1 --branch "$FALLBACK_BRANCH" "$CORE_REPO" "$fallback_dir"
  src="$fallback_dir"
fi

mkdir -p "$DEST"
cp -a "$src/." "$DEST/"
echo "Bootstrapped extensions into $DEST"
