#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

"$ROOT/scripts/bootstrap-extensions.sh"

echo "Installing IgnisCore API..."
mvn install -pl api -am "$@"

echo "Building bundled extensions..."
mvn -f extensions/pom.xml install "$@"

echo "Packaging IgnisCore bootstrap..."
mvn package "$@"
