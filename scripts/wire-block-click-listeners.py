#!/usr/bin/env python3
"""Wire generated OnBlockClickListener classes into Strategy.java files."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BLOCKS = ROOT / "extensions" / "blocks"


def listener_prefix(java_dir: Path) -> str:
    click = java_dir / next(
        (p.name for p in java_dir.glob("*OnBlockClickListener.java")),
        None,
    )
    if click:
        name = click.stem
        return name[: name.find("OnBlockClick")]
    raise RuntimeError(f"no click listener in {java_dir}")


def fix_strategy(strategy_path: Path) -> bool:
    text = strategy_path.read_text(encoding="utf-8")
    if "PlacedClickListener" not in text:
        return False

    prefix = listener_prefix(strategy_path.parent)
    pattern = re.compile(r"\s*context\.eventBus\(\)\.subscribe\(PlacedClickListener\.[^;]+\);\n")
    text, count = pattern.subn(
        f"        context.eventBus().subscribe(new {prefix}OnBlockClickListener());\n",
        text,
        count=1,
    )
    if count == 0:
        return False

    text = text.replace("import dev.rono.extensions.shared.strategy.PlacedClickListener;\n", "")
    text = text.replace("import dev.rono.igniscore.api.CustomBlockAction;\n", "")
    strategy_path.write_text(text, encoding="utf-8")
    return True


def main() -> None:
    changed = 0
    for strategy in sorted(BLOCKS.glob("*/src/main/java/**/Strategy.java")):
        if fix_strategy(strategy):
            changed += 1
    print(f"fixed {changed} strategies")


if __name__ == "__main__":
    main()
