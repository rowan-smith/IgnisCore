#!/usr/bin/env python3
"""Re-add PlacedClickListener subscriptions removed during listener split."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

IGNITE_BLOCKS = {
    "accelerating-fuse-tnt", "blink-tnt", "bridge-builder", "cascade-mine", "echo-fuse-tnt",
    "erupting-tnt", "last-stand-charge", "mimic-tnt", "mirror-world-tnt", "nuke", "pause-tnt",
    "phantom-tnt", "phase-tnt", "powder-trail", "ricochet-tnt", "rift-generator",
    "scaffold-charge", "signal-charge", "spider-storm-tnt", "splitter-charge", "swap-charge",
    "tunneling-tnt", "wormhole-tnt",
}


def patch_strategy(path: Path, block_id: str) -> bool:
    text = path.read_text()
    if "PlacedClickListener" in text or "AutoSieveOnBlockClickListener" in text:
        return False
    if block_id == "auto-sieve":
        return False

    if block_id in IGNITE_BLOCKS or "OnBlockTrigger" in text:
        sub = "        context.eventBus().subscribe(PlacedClickListener.forStrategy(this));"
    elif "OnBlockInteract" in text:
        sub = "        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.OPEN));"
    else:
        sub = "        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.NONE));"

    if "import dev.rono.extensions.shared.strategy.PlacedClickListener;" not in text:
        text = text.replace(
            "import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;",
            "import dev.rono.extensions.shared.strategy.PlacedClickListener;\n"
            "import dev.rono.igniscore.api.CustomBlockAction;\n"
            "import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;",
            1,
        )

    new_text = re.sub(
        r"(public Strategy\(IgnisStrategyContext context\) \{\n        super\(context\);\n)",
        r"\1" + sub + "\n",
        text,
        count=1,
    )
    if new_text == text:
        return False
    text = new_text

    if block_id in IGNITE_BLOCKS and "StrategyProfile.combustible" not in text:
        text = re.sub(
            r"return StrategyProfile\.builder\(\)\s*\n\s*\.defaultFuse\([^)]+\)\s*\n\s*\.defaultRadius\([^)]+\)\s*\n\s*\.build\(\);",
            "return StrategyProfile.combustible(\n"
            "                dev.rono.igniscore.api.strategy.StrategySupport.customInt(definition, \"fuse\", 80),\n"
            "                dev.rono.igniscore.api.strategy.StrategySupport.customDouble(definition, \"radius\", 4.0));",
            text,
        )

    path.write_text(text)
    return True


def main() -> None:
    count = 0
    for path in sorted(ROOT.glob("extensions/blocks/**/Strategy.java")):
        block_id = path.parts[path.parts.index("blocks") + 1]
        if patch_strategy(path, block_id):
            count += 1
            print(block_id)
    print(f"Patched {count}")


if __name__ == "__main__":
    main()
