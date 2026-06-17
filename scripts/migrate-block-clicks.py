#!/usr/bin/env python3
"""Wire PlacedClickListener into block strategies and strip YAML click routing."""

from __future__ import annotations

import re
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]

ACTION_MAP = {
    "none": "CustomBlockAction.NONE",
    "break": "CustomBlockAction.BREAK",
    "ignite": "CustomBlockAction.IGNITE",
    "open": "CustomBlockAction.OPEN",
    "handled": "CustomBlockAction.HANDLED",
}


def read_click_pattern(config_path: Path) -> tuple[str | None, str | None]:
    if not config_path.exists():
        return None, None
    data = yaml.safe_load(config_path.read_text()) or {}
    behavior = data.get("behavior") or {}
    return behavior.get("left_click_block"), behavior.get("right_click_block")


def strip_click_keys(config_path: Path) -> bool:
    text = config_path.read_text()
    original = text
    text = re.sub(r"^  left_click_block:.*\n", "", text, flags=re.M)
    text = re.sub(r"^  right_click_block:.*\n", "", text, flags=re.M)
    text = re.sub(r"^  left_click_air:.*\n", "", text, flags=re.M)
    text = re.sub(r"^  right_click_air:.*\n", "", text, flags=re.M)
    # drop empty behavior section
    text = re.sub(r"\nbehavior:\n  sounds:\n", "\nbehavior:\n  sounds:\n", text)
    if text != original:
        config_path.write_text(text)
        return True
    return False


def ensure_imports(text: str) -> str:
    imports = [
        "dev.rono.extensions.shared.strategy.PlacedClickListener",
        "dev.rono.igniscore.api.CustomBlockAction",
    ]
    for imp in imports:
        line = f"import {imp};"
        if line not in text:
            text = text.replace("import dev.rono.igniscore.api.strategy.", f"import {imp};\nimport dev.rono.igniscore.api.strategy.", 1)
    if "import dev.rono.extensions.shared.strategy.PlacedClickListener;" not in text:
        pkg_end = text.index("\n", text.index("package "))
        text = text[: pkg_end + 1] + "\nimport dev.rono.extensions.shared.strategy.PlacedClickListener;\nimport dev.rono.igniscore.api.CustomBlockAction;\n" + text[pkg_end + 1 :]
    return text


def add_click_subscription(text: str, left: str | None, right: str | None) -> str:
    if "PlacedClickListener" in text:
        return text
    left = left or "break"
    right = right or "none"
    if right == "ignite":
        sub = "        context.eventBus().subscribe(PlacedClickListener.forStrategy(this));"
    else:
        sub = (
            f"        context.eventBus().subscribe(PlacedClickListener.fixed("
            f"{ACTION_MAP[left]}, {ACTION_MAP[right]}));"
        )
    return re.sub(
        r"(public Strategy\(IgnisStrategyContext context\) \{\n        super\(context\);\n)",
        r"\1" + sub + "\n",
        text,
        count=1,
    )


def update_combustible_profile(text: str) -> str:
    if "PlacedClickListener.forStrategy" not in text:
        return text
    if "StrategyProfile.combustible" in text:
        return text
    if "profile(BlockDefinition definition)" not in text:
        # fuse-only TNT: add profile with combustible from custom_data
        insert = """
    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.combustible(
                dev.rono.igniscore.api.strategy.StrategySupport.customInt(definition, "fuse", 80),
                dev.rono.igniscore.api.strategy.StrategySupport.customDouble(definition, "radius", 4.0));
    }
"""
        if "StrategyProfile profile" in text:
            return text
        return text.replace("\n}\n", insert + "\n}\n")
    # replace builder-only fuse profile
    text = re.sub(
        r"return StrategyProfile\.builder\(\)\s*\n\s*\.defaultFuse\([^)]+\)\s*\n(?:\s*\.defaultRadius\([^)]+\)\s*\n)?\s*\.build\(\);",
        "return StrategyProfile.combustible(\n"
        "                dev.rono.igniscore.api.strategy.StrategySupport.customInt(definition, \"fuse\", 80),\n"
        "                dev.rono.igniscore.api.strategy.StrategySupport.customDouble(definition, \"radius\", 4.0));",
        text,
    )
    return text


def migrate_block(block_dir: Path) -> bool:
    strategy_path = next(block_dir.glob("src/main/java/**/Strategy.java"), None)
    config_path = block_dir / "src/main/resources/config.yml"
    if not strategy_path or not config_path.exists():
        return False
    left, right = read_click_pattern(config_path)
    if left is None and right is None:
        left, right = "break", "none"
    text = strategy_path.read_text()
    original = text
    text = add_click_subscription(text, left, right)
    text = ensure_imports(text)
    if right == "ignite":
        text = update_combustible_profile(text)
    if text != original:
        strategy_path.write_text(text)
    strip_click_keys(config_path)
    return text != original


def main() -> None:
    count = 0
    for block_dir in sorted((ROOT / "extensions/blocks").iterdir()):
        if block_dir.is_dir() and migrate_block(block_dir):
            count += 1
            print(block_dir.name)
    print(f"Updated {count} block strategies")


if __name__ == "__main__":
    main()
