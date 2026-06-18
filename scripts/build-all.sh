#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

if [[ ! -f extensions/pom.xml ]]; then
  echo "Initializing extensions submodule..."
  git submodule update --init --recursive extensions
fi

echo "Installing IgnisCore API..."
mvn install -pl api -am "$@"

echo "Building bundled extensions..."
mvn -f extensions/pom.xml install "$@"

echo "Packaging IgnisCore bootstrap..."
mvn package "$@"
