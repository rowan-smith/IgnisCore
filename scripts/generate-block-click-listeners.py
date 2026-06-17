#!/usr/bin/env python3
"""Generate per-module OnBlockClickListener classes and wire them in Strategy.java."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BLOCKS = ROOT / "extensions" / "blocks"

COMBUSTIBLE = re.compile(r"PlacedClickListener\.combustible\(\)")
BREAK_NONE = re.compile(
    r"PlacedClickListener\.fixed\(CustomBlockAction\.BREAK,\s*CustomBlockAction\.NONE\)"
)
BREAK_OPEN = re.compile(
    r"PlacedClickListener\.fixed\(CustomBlockAction\.BREAK,\s*CustomBlockAction\.OPEN\)"
)

COMBUSTIBLE_TEMPLATE = """\
package {package};

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.event.BlockClickEvent;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;

final class {prefix}OnBlockClickListener implements OnBlockClickListener {{
    @Override
    public void onBlockClick(BlockClickEvent event) {{
        if (event.interaction() == IgnisInteraction.LEFT_CLICK_BLOCK) {{
            event.setResult(CustomBlockAction.BREAK);
            return;
        }}
        if (event.interaction() != IgnisInteraction.RIGHT_CLICK_BLOCK) {{
            return;
        }}
        BlockBehaviorConfig behavior = BlockBehaviorConfig.from(event.block().definition().getBehaviorConfig());
        if (!behavior.combustible()) {{
            return;
        }}
        String material = materialKey(event.heldItem());
        for (String ignition : behavior.ignitionMaterials()) {{
            if (material.equalsIgnoreCase(ignition)) {{
                event.setResult(CustomBlockAction.IGNITE);
                return;
            }}
        }}
    }}

    private static String materialKey(IgnisItem heldItem) {{
        if (heldItem == null || heldItem.isAir()) {{
            return "AIR";
        }}
        String materialKey = heldItem.getMaterialKey();
        return materialKey == null || materialKey.isBlank() ? "AIR" : materialKey;
    }}
}}
"""

BREAK_NONE_TEMPLATE = """\
package {package};

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockClickEvent;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.port.IgnisInteraction;

final class {prefix}OnBlockClickListener implements OnBlockClickListener {{
    @Override
    public void onBlockClick(BlockClickEvent event) {{
        if (event.interaction() == IgnisInteraction.LEFT_CLICK_BLOCK) {{
            event.setResult(CustomBlockAction.BREAK);
        }}
    }}
}}
"""

BREAK_OPEN_TEMPLATE = """\
package {package};

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockClickEvent;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.port.IgnisInteraction;

final class {prefix}OnBlockClickListener implements OnBlockClickListener {{
    @Override
    public void onBlockClick(BlockClickEvent event) {{
        if (event.interaction() == IgnisInteraction.LEFT_CLICK_BLOCK) {{
            event.setResult(CustomBlockAction.BREAK);
        }} else if (event.interaction() == IgnisInteraction.RIGHT_CLICK_BLOCK) {{
            event.setResult(CustomBlockAction.OPEN);
        }}
    }}
}}
"""


def listener_prefix(java_dir: Path) -> str:
    for path in sorted(java_dir.glob("*OnBlock*Listener.java")):
        name = path.stem
        marker = "OnBlock"
        idx = name.find(marker)
        if idx > 0:
            return name[:idx]
    package = java_dir.name
    parts = re.split(r"[-_]", package.replace("tnt", " tnt"))
    return "".join(part[:1].upper() + part[1:] for part in package.split("-") if part)


def classify(strategy_text: str) -> str | None:
    if COMBUSTIBLE.search(strategy_text):
        return "combustible"
    if BREAK_OPEN.search(strategy_text):
        return "open"
    if BREAK_NONE.search(strategy_text):
        return "none"
    return None


def migrate_module(strategy_path: Path) -> bool:
    text = strategy_path.read_text(encoding="utf-8")
    kind = classify(text)
    if kind is None:
        return False

    java_dir = strategy_path.parent
    package = ".".join(java_dir.parts[java_dir.parts.index("java") + 1 :])
    prefix = listener_prefix(java_dir)
    listener_path = java_dir / f"{prefix}OnBlockClickListener.java"

    template = {
        "combustible": COMBUSTIBLE_TEMPLATE,
        "none": BREAK_NONE_TEMPLATE,
        "open": BREAK_OPEN_TEMPLATE,
    }[kind]
    listener_path.write_text(
        template.format(package=package, prefix=prefix),
        encoding="utf-8",
    )

    subscribe_pattern = re.compile(
        r"\s*context\.eventBus\(\)\.subscribe\(PlacedClickListener\.[^;]+\);\n"
    )
    replacement = f"        context.eventBus().subscribe(new {prefix}OnBlockClickListener());\n"
    text, count = subscribe_pattern.subn(replacement, text, count=1)
    if count == 0:
        return False

    text = text.replace("import dev.rono.extensions.shared.strategy.PlacedClickListener;\n", "")
    text = text.replace("import dev.rono.igniscore.api.CustomBlockAction;\n", "")

    strategy_path.write_text(text, encoding="utf-8")
    return True


def main() -> None:
    changed = 0
    for strategy in sorted(BLOCKS.glob("*/src/main/java/**/Strategy.java")):
        if migrate_module(strategy):
            changed += 1
            print(f"updated {strategy.relative_to(ROOT)}")
    print(f"done: {changed} modules")


if __name__ == "__main__":
    main()
