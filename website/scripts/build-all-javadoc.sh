#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
WEBSITE_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
REPO_ROOT="$(cd "$WEBSITE_ROOT/.." && pwd)"
APIDOCS_DIR="${WEBSITE_ROOT}/static/apidocs"

VERSION="$(node -p "JSON.parse(require('fs').readFileSync('${WEBSITE_ROOT}/site-vars.json','utf8')).version")"

echo "Building Javadoc artifacts under ${APIDOCS_DIR}"

mkdir -p "$APIDOCS_DIR"

CURRENT_OUT="${APIDOCS_DIR}/${VERSION}"
API_DIR="$REPO_ROOT/api/target/reports/apidocs"

mvn -f "$REPO_ROOT/pom.xml" \
  -pl api \
  -am compile javadoc:javadoc \
  -Ddoclint=none \
  -DskipTests \
  -q

rm -rf "$CURRENT_OUT"
mkdir -p "$CURRENT_OUT"

if [ -d "$API_DIR" ]; then
  cp -a "$API_DIR/." "$CURRENT_OUT/"
fi

test -f "${CURRENT_OUT}/index.html"
echo "OK: ${VERSION} -> ${CURRENT_OUT}"
