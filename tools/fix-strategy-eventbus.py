#!/usr/bin/env python3
"""Repair Strategy.java registerEvents blocks using behavior method signatures."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def behavior_info(strategy_path: Path) -> tuple[str, Path | None, dict[str, str]]:
    text = strategy_path.read_text()
    match = re.search(r"new (\w+Behavior|\w+Registry)\(", text)
    if not match:
        return "behavior", None, {}
    name = match.group(1)
    var = "registry" if name.endswith("Registry") else "behavior"
    behavior_path = strategy_path.parent / f"{name}.java"
    signatures: dict[str, str] = {}
    if behavior_path.exists():
        body = behavior_path.read_text()
        for method, args in re.findall(r"void (on[A-Za-z]+)\(([^)]*)\)", body):
            signatures[method] = args.strip()
    return var, behavior_path, signatures


def call(var: str, method: str, args: str) -> str:
    mapping = {
        "BlockDefinition definition": "event.definition()",
        "ItemDefinition definition": "event.definition()",
        "IgnisLocation location": "event.location()",
        "IgnisItem placedFrom": "event.placedFrom()",
        "IgnisItem droppedItem": "event.droppedItem()",
        "RuntimeBlockInstance instance": "event.instance()",
        "Object context": "event.triggerContext()",
        "Object triggerContext": "event.triggerContext()",
        "IgnisPlayer player": "event.player()",
        "IgnisInteraction interaction": "event.interaction()",
        "IgnisItem heldItem": "event.heldItem()",
        "CustomBlockAction action": "event.action()",
        "IgnisItem item": "event.item()",
        "IgnisBlock clickedBlock": "event.clickedBlock()",
    }
    if not args:
        return f"{var}.{method}()"
    parts = [part.strip() for part in args.split(",")]
    resolved = []
    for part in parts:
        if part in mapping:
            resolved.append(mapping[part])
        elif part == "BlockDefinition definition" and method == "onTrigger":
            resolved.append("event.instance().getDefinition()")
        else:
            resolved.append(part.split()[-1])
    return f"{var}.{method}({', '.join(resolved)})"


def build_register_events(strategy_path: Path) -> str | None:
    text = strategy_path.read_text()
    is_block = "AbstractIgnisBlockStrategy" in text
    is_item = "AbstractIgnisItemStrategy" in text
    var, behavior_path, sigs = behavior_info(strategy_path)

    lines: list[str] = ["    @Override", "    public void registerEvents() {"]

    if "quarry-cache" in str(strategy_path) or (behavior_path and behavior_path.name == "QuarryCacheRegistry.java"):
        lines.extend([
            "        onBlockPlace(event -> registry.register(event.location(), event.definition(), event.placedFrom()));",
            "        onBlockBreak(event -> registry.handleBreak(event.location(), event.droppedItem()));",
            "        onBlockInteract(event -> {",
            "            if (event.action() == CustomBlockAction.OPEN) {",
            "                registry.openGui(event.player(), event.location());",
            "            }",
            "        });",
        ])
    elif is_block:
        if "onPlaced" in sigs:
            lines.append(f"        onBlockPlace(event -> {call(var, 'onPlaced', sigs['onPlaced'])});")
        if "onPlacedBreak" in sigs:
            lines.append(f"        onBlockBreak(event -> {call(var, 'onPlacedBreak', sigs['onPlacedBreak'])});")
        if "onPlacedInteract" in sigs:
            lines.append(f"        onBlockInteract(event -> {call(var, 'onPlacedInteract', sigs['onPlacedInteract'])});")
        if "onPlace" in sigs:
            lines.append(f"        onBlockActivate(event -> {call(var, 'onPlace', sigs['onPlace'])});")
        if "onTick" in sigs:
            lines.append(f"        onBlockTick(event -> {call(var, 'onTick', sigs['onTick'])});")
        if "onTrigger" in sigs:
            lines.append(f"        onBlockTrigger(event -> {call(var, 'onTrigger', sigs['onTrigger'])});")

    if is_item:
        if "detonator" in str(strategy_path):
            lines.extend([
                "        onItemClick(event -> {",
                "            switch (event.actionToken()) {",
                "                case \"assign\" -> behavior.assignBomb(event.player(), event.definition(), event.item(), event.clickedBlock());",
                "                case \"detonate\" -> behavior.detonateLinkedBombs(event.player(), event.definition(), event.item());",
                "                default -> { }",
                "            }",
                "        });",
            ])
        elif "grenade" in str(strategy_path):
            lines.extend([
                "        onItemClick(event -> {",
                "            if (\"throw\".equals(event.actionToken())) {",
                "                behavior.onItemUse(event.player(), event.definition(), event.item());",
                "            }",
                "        });",
            ])
        elif "onItemUse" in sigs:
            lines.extend([
                "        onItemClick(event -> {",
                "            if (\"use\".equals(event.actionToken())) {",
                f"                {call(var, 'onItemUse', sigs['onItemUse'])};",
                "            }",
                "        });",
            ])

    if len(lines) <= 2:
        return None

    lines.append("    }")
    return "\n".join(lines)


def repair_file(path: Path) -> bool:
    register = build_register_events(path)
    if not register:
        return False
    original = path.read_text()
    content = re.sub(
        r"\s*@Override\s+public void registerEvents\(\) \{.*?\n    \}",
        "\n\n" + register,
        original,
        count=1,
        flags=re.DOTALL,
    )
    if content != original:
        path.write_text(content)
        return True
    return False


def main() -> None:
    changed = 0
    for path in sorted(ROOT.glob("extensions/**/Strategy.java")):
        if repair_file(path):
            changed += 1
    print(f"repaired {changed} files")


if __name__ == "__main__":
    main()
