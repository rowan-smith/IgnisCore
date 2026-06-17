#!/usr/bin/env python3
"""Replace onBlock*/onItem* helpers with context.eventBus().subscribe()."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

MAPPINGS = [
    ("onBlockPlace", "OnBlockPlaceListener"),
    ("onBlockBreak", "OnBlockBreakListener"),
    ("onBlockInteract", "OnBlockInteractListener"),
    ("onBlockClick", "OnBlockClickListener"),
    ("onBlockActivate", "OnBlockActivateListener"),
    ("onBlockTick", "OnBlockTickListener"),
    ("onBlockTrigger", "OnBlockTriggerListener"),
    ("onItemClick", "OnItemClickListener"),
]


def migrate_strategy(path: Path) -> bool:
    text = path.read_text()
    if "auto-sieve" in path.as_posix():
        return False
    if not re.search(r"\bon(Block|Item)", text):
        return False

    original = text
    # var listeners = new XxxListeners(context);
    var_match = re.search(r"var listeners = new (\w+)\(context\);\n", text)
    listeners_class = var_match.group(1) if var_match else None

    subs = []
    for helper, listener in MAPPINGS:
        if re.search(rf"\s+{helper}\(listeners\);\n", text):
            subs.append((helper, listener))

    if not subs:
        return False

    if len(subs) == 1 and listeners_class:
        helper, listener = subs[0]
        text = re.sub(r"        var listeners = new \w+\(context\);\n", "", text)
        text = re.sub(
            rf"        {helper}\(listeners\);\n",
            f"        context.eventBus().subscribe(new {listeners_class}(context));\n",
            text,
        )
    else:
        if listeners_class:
            text = re.sub(
                r"        var listeners = new (\w+)\(context\);\n",
                r"        \1 listeners = new \1(context);\n",
                text,
            )
            listeners_class = var_match.group(1)
            for helper, listener in subs:
                text = re.sub(
                    rf"        {helper}\(listeners\);\n",
                    f"        context.eventBus().subscribe(({listener}) listeners);\n",
                    text,
                )

    if text != original:
        path.write_text(text)
        return True
    return False


def main() -> None:
    count = 0
    for path in sorted(ROOT.glob("extensions/**/Strategy.java")):
        if migrate_strategy(path):
            count += 1
            print(path.relative_to(ROOT))
    print(f"Updated {count}")


if __name__ == "__main__":
    main()
