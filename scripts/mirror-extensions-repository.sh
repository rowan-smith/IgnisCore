#!/usr/bin/env bash
set -euo pipefail

# Mirror the extensions-export branch into IgnisCore-Extensions.
# Requires push access to https://github.com/rowan-smith/IgnisCore-Extensions
#
# Usage:
#   ./scripts/mirror-extensions-repository.sh
#   ./scripts/mirror-extensions-repository.sh --batch
#
# Environment:
#   IGNIS_CORE_REPO
#   IGNIS_EXTENSIONS_FALLBACK_BRANCH
#   IGNIS_EXTENSIONS_REPO
#   IGNIS_EXTENSIONS_TARGET_BRANCH
#   GIT_TERMINAL_PROMPT=0            (set automatically with --batch)

SOURCE_REPO="${IGNIS_CORE_REPO:-https://github.com/rowan-smith/IgnisCore.git}"
SOURCE_BRANCH="${IGNIS_EXTENSIONS_FALLBACK_BRANCH:-extensions-export}"
TARGET_REPO="${IGNIS_EXTENSIONS_REPO:-https://github.com/rowan-smith/IgnisCore-Extensions.git}"
TARGET_BRANCH="${IGNIS_EXTENSIONS_TARGET_BRANCH:-main}"

BATCH=0
for arg in "$@"; do
  case "$arg" in
    --batch|-batch|/batch)
      BATCH=1
      ;;
    -h|--help)
      sed -n '1,20p' "$0"
      exit 0
      ;;
    *)
      echo "Unknown argument: $arg" >&2
      echo "Usage: $0 [--batch]" >&2
      exit 2
      ;;
  esac
done

if [[ "$BATCH" -eq 1 ]]; then
  export GIT_TERMINAL_PROMPT=0
fi

tmpdir="$(mktemp -d)"
cleanup() { rm -rf "$tmpdir"; }
trap cleanup EXIT

echo "Cloning $SOURCE_REPO ($SOURCE_BRANCH)..."
git clone --branch "$SOURCE_BRANCH" --depth 1 "$SOURCE_REPO" "$tmpdir/export"
cd "$tmpdir/export"

git remote add target "$TARGET_REPO"
echo "Pushing to $TARGET_REPO ($TARGET_BRANCH)..."
git push target "HEAD:$TARGET_BRANCH"

echo "Mirrored $SOURCE_BRANCH from IgnisCore to $TARGET_REPO ($TARGET_BRANCH)"
