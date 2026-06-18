#!/usr/bin/env bash
set -euo pipefail

# One-time helper: mirror the extensions-export branch into IgnisCore-Extensions.
# Requires push access to https://github.com/rowan-smith/IgnisCore-Extensions

set -euo pipefail

SOURCE_REPO="${IGNIS_CORE_REPO:-https://github.com/rowan-smith/IgnisCore.git}"
SOURCE_BRANCH="${IGNIS_EXTENSIONS_FALLBACK_BRANCH:-extensions-export}"
TARGET_REPO="${IGNIS_EXTENSIONS_REPO:-https://github.com/rowan-smith/IgnisCore-Extensions.git}"
TARGET_BRANCH="${IGNIS_EXTENSIONS_TARGET_BRANCH:-main}"

tmpdir="$(mktemp -d)"
cleanup() { rm -rf "$tmpdir"; }
trap cleanup EXIT

git clone --branch "$SOURCE_BRANCH" --depth 1 "$SOURCE_REPO" "$tmpdir/export"
cd "$tmpdir/export"
git remote add target "$TARGET_REPO"
git push target "HEAD:$TARGET_BRANCH"
echo "Mirrored $SOURCE_BRANCH from IgnisCore to $TARGET_REPO ($TARGET_BRANCH)"
