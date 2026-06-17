#!/usr/bin/env python3
"""Remove StrategyProfile.profile() overrides and migrate TNT click listeners."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BLOCKS = ROOT / "extensions" / "blocks"

PROFILE_OVERRIDE = re.compile(
    r"\n\s*@Override\s*\n\s*public StrategyProfile profile\(BlockDefinition definition\) \{[^}]+\}\n?",
    re.MULTILINE | re.DOTALL,
)

IMPORTS_TO_DROP = {
    "import dev.rono.igniscore.api.model.BlockDefinition;\n",
    "import dev.rono.igniscore.api.strategy.StrategyProfile;\n",
    "import dev.rono.igniscore.api.strategy.StrategySupport;\n",
}


def cleanup_imports(text: str) -> str:
    for imp in IMPORTS_TO_DROP:
        if "profile(" not in text and imp in text:
            text = text.replace(imp, "")
    if "BlockDefinition" not in text:
        text = text.replace("import dev.rono.igniscore.api.model.BlockDefinition;\n", "")
    if "StrategyProfile" not in text:
        text = text.replace("import dev.rono.igniscore.api.strategy.StrategyProfile;\n", "")
    if "StrategySupport" not in text:
        text = text.replace("import dev.rono.igniscore.api.strategy.StrategySupport;\n", "")
    return text


def migrate_strategy(path: Path) -> bool:
    original = path.read_text(encoding="utf-8")
    text = original

    text = PROFILE_OVERRIDE.sub("\n", text)
    text = text.replace(
        "PlacedClickListener.forStrategy(this)",
        "PlacedClickListener.combustible()",
    )
    text = cleanup_imports(text)

    if text != original:
        path.write_text(text, encoding="utf-8")
        return True
    return False


def main() -> None:
    changed = 0
    for strategy in sorted(BLOCKS.glob("*/src/main/java/**/Strategy.java")):
        if migrate_strategy(strategy):
            changed += 1
            print(f"updated {strategy.relative_to(ROOT)}")
    print(f"done: {changed} strategies")


if __name__ == "__main__":
    main()
