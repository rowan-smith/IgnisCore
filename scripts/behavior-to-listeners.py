#!/usr/bin/env python3
"""Rename Behavior classes to Listeners and add event handler wrappers."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

HELPERS = [
    ("onBlockPlace", "OnBlockPlaceListener", "BlockPlaceEvent", "onBlockPlace"),
    ("onBlockBreak", "OnBlockBreakListener", "BlockBreakEvent", "onBlockBreak"),
    ("onBlockInteract", "OnBlockInteractListener", "BlockInteractEvent", "onBlockInteract"),
    ("onBlockClick", "OnBlockClickListener", "BlockClickEvent", "onBlockClick"),
    ("onBlockActivate", "OnBlockActivateListener", "BlockActivateEvent", "onBlockActivate"),
    ("onBlockTick", "OnBlockTickListener", "BlockTickEvent", "onBlockTick"),
    ("onBlockTrigger", "OnBlockTriggerListener", "BlockTriggerEvent", "onBlockTrigger"),
    ("onItemClick", "OnItemClickListener", "ItemClickEvent", "onItemClick"),
]


def extract_call(strategy: str, helper: str) -> list[tuple[str, str]]:
    results = []
    token = f"{helper}(event -> behavior."
    start = 0
    while True:
        idx = strategy.find(token, start)
        if idx == -1:
            break
        i = idx + len(token)
        method_end = strategy.find("(", i)
        method = strategy[i:method_end]
        i = method_end + 1
        depth = 1
        args_start = i
        while i < len(strategy) and depth:
            if strategy[i] == "(":
                depth += 1
            elif strategy[i] == ")":
                depth -= 1
            i += 1
        args = strategy[args_start : i - 1].strip()
        results.append((method, args))
        start = i
    return results


def parse_bindings(strategy: str) -> list[dict]:
    bindings = []
    for helper, listener, event, method in HELPERS:
        for behavior_method, args in extract_call(strategy, helper):
            bindings.append(
                {
                    "helper": helper,
                    "listener": listener,
                    "event": event,
                    "method": method,
                    "call": f"{behavior_method}({args})",
                }
            )
        block = re.search(rf"{helper}\(event -> \{{(.*?)\n        \}}\);", strategy, re.S)
        if block:
            body = block.group(1).strip()
            bindings.append(
                {
                    "helper": helper,
                    "listener": listener,
                    "event": event,
                    "method": method,
                    "inline": re.sub(r"\bbehavior\.", "", body),
                }
            )
    return bindings


def to_listener_call(call: str) -> str:
    updated = call
    updated = updated.replace("event.block()", "event.block().location()")
    updated = updated.replace("event.definition()", "event.block().definition()")
    updated = updated.replace("event.instance().getDefinition()", "event.definition()")
    return updated


def generate_listeners(behavior_path: Path, behavior: str, bindings: list[dict]) -> str:
    listeners_name = behavior_path.stem.replace("Behavior", "Listeners")
    old_class = behavior_path.stem
    interfaces = ", ".join(b["listener"] for b in bindings)

    content = behavior
    content = content.replace(f"final class {old_class}", f"final class {listeners_name} implements {interfaces}")
    content = content.replace(f"{old_class}(", f"{listeners_name}(")

    imports = set(re.findall(r"^import (.+);", content, re.M))
    for b in bindings:
        imports.add(f"dev.rono.igniscore.api.event.{b['listener']}")
        imports.add(f"dev.rono.igniscore.api.event.{b['event']}")

    wrappers = []
    for b in bindings:
        if "inline" in b:
            wrappers.append(
                f"\n    @Override\n    public void {b['method']}({b['event']} event) {{\n        {b['inline']}\n    }}\n"
            )
        else:
            wrappers.append(
                f"\n    @Override\n    public void {b['method']}({b['event']} event) {{\n        {to_listener_call(b['call'])};\n    }}\n"
            )

    content = re.sub(r"^import .+;\n", "", content, flags=re.M)
    pkg = ".".join(behavior_path.parts[behavior_path.parts.index("java") + 1 : -1])
    import_lines = "\n".join(f"import {imp};" for imp in sorted(imports))
    content = re.sub(r"^package .+;\n", "", content).strip()
    if content.endswith("}"):
        content = content[:-1]
    return f"package {pkg};\n\n{import_lines}\n\n{content}{''.join(wrappers)}}}\n"


def update_strategy(strategy_path: Path, listeners_name: str, bindings: list[dict]) -> None:
    text = strategy_path.read_text()
    text = re.sub(r"\n    private final \w+Behavior behavior;\n", "\n", text)
    lines = [f"        var listeners = new {listeners_name}(context);"]
    for b in bindings:
        lines.append(f"        {b['helper']}(listeners);")
    ctor = "    public Strategy(IgnisStrategyContext context) {\n        super(context);\n" + "\n".join(lines) + "\n    }"
    text = re.sub(r"    public Strategy\(IgnisStrategyContext context\) \{.*?\n    \}", ctor, text, count=1, flags=re.S)
    strategy_path.write_text(text)


def process(behavior_path: Path) -> bool:
    if "auto-sieve" in behavior_path.as_posix():
        return False
    strategy_path = behavior_path.parent / "Strategy.java"
    strategy = strategy_path.read_text()
    if "Behavior" not in strategy:
        return False
    bindings = parse_bindings(strategy)
    if not bindings:
        return False
    behavior = behavior_path.read_text()
    listeners_name = behavior_path.stem.replace("Behavior", "Listeners")
    source = generate_listeners(behavior_path, behavior, bindings)
    (behavior_path.parent / f"{listeners_name}.java").write_text(source)
    update_strategy(strategy_path, listeners_name, bindings)
    behavior_path.unlink()
    return True


def main() -> None:
    count = sum(process(path) for path in sorted(ROOT.glob("extensions/**/*Behavior.java")))
    print(f"Converted {count}")


if __name__ == "__main__":
    main()
