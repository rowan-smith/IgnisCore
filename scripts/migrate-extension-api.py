#!/usr/bin/env python3
"""Migrate IgnisCore extensions to event-driven constructor subscriptions."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

REPLACEMENTS = [
    (r"\.getExtensionSupport\(\)", ".extensions()"),
    (r"\.getScheduler\(\)", ".scheduler()"),
    (r"\.getNbtService\(\)", ".nbt()"),
    (r"\.getEffectService\(\)", ".effects()"),
    (r"\.getProtocolService\(\)", ".protocol()"),
    (r"\.getEventBus\(\)", ".eventBus()"),
    (r"event\.location\(\)", "event.block()"),
    (r"onBlockPlace\(", "context.eventBus().subscribe("),
    (r"onBlockBreak\(", "context.eventBus().subscribe("),
    (r"onBlockInteract\(", "context.eventBus().subscribe("),
    (r"onBlockClick\(", "context.eventBus().subscribe("),
    (r"onBlockActivate\(", "context.eventBus().subscribe("),
    (r"onBlockTick\(", "context.eventBus().subscribe("),
    (r"onBlockTrigger\(", "context.eventBus().subscribe("),
    (r"onItemClick\(", "context.eventBus().subscribe("),
]

REGISTER_EVENTS_PATTERN = re.compile(
    r"\n    @Override\n    public void registerEvents\(\) \{\n(?P<body>(?:.*\n)*?)    \}\n",
    re.MULTILINE,
)

CONSTRUCTOR_PATTERN = re.compile(
    r"(public Strategy\(IgnisStrategyContext context\) \{\n"
    r"        super\(context\);\n"
    r"        this\.behavior = new [^\n]+\n)"
    r"    \}",
    re.MULTILINE,
)


def transform_java(content: str) -> str:
    for pattern, replacement in REPLACEMENTS:
        content = re.sub(pattern, replacement, content)
    return content


def migrate_strategy(content: str) -> str | None:
    match = REGISTER_EVENTS_PATTERN.search(content)
    if not match:
        return None

    body = match.group("body")
    for pattern, replacement in REPLACEMENTS:
        body = re.sub(pattern, replacement, body)

    # Re-indent body from 8 spaces to 8 spaces (same level in constructor)
    body_lines = [line for line in body.splitlines() if line.strip()]
    subscription_block = "\n".join(body_lines) + "\n"

    content = REGISTER_EVENTS_PATTERN.sub("\n", content)

    constructor_match = CONSTRUCTOR_PATTERN.search(content)
    if constructor_match:
        prefix = constructor_match.group(1)
        content = content.replace(
            constructor_match.group(0),
            prefix + subscription_block + "    }",
            1,
        )
        return content

    # Fallback: insert before last closing brace of class
    insert_at = content.rfind("\n}")
    if insert_at == -1:
        return None
    return content[:insert_at] + "\n" + subscription_block + content[insert_at:]


def process_file(path: Path) -> bool:
    original = path.read_text()
    updated = transform_java(original)

    if path.name == "Strategy.java" and "registerEvents" in original:
        migrated = migrate_strategy(updated)
        if migrated is not None:
            updated = migrated

    if updated != original:
        path.write_text(updated)
        return True
    return False


def main() -> None:
    changed = 0
    for path in ROOT.rglob("*.java"):
        if "target" in path.parts:
            continue
        if process_file(path):
            changed += 1
            print(path.relative_to(ROOT))
    print(f"Updated {changed} files")


if __name__ == "__main__":
    main()
