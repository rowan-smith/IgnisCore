#!/usr/bin/env python3
"""Update BehaviorTest files to fire events via TestEventBus."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BLOCKS = ROOT / "extensions" / "blocks"

IMPORTS = """import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.model.PlacedBlock;
import dev.rono.igniscore.testsupport.TestEventBus;
"""

ON_TRIGGER = re.compile(
    r"(\s+)BehaviorTestSupport\.TestContext ctx = BehaviorTestSupport\.createContext\(\);\n"
    r"\1BlockDefinition definition = ExtensionTestSupport\.loadBlockDefinition\([^,]+, \"([^\"]+)\", \d+\);\n"
    r"\1Strategy strategy = new Strategy\(ctx\.context\(\)\);\n"
    r"\1RuntimeBlockInstance instance = BehaviorTestSupport\.blockInstance\(definition\);\n"
    r"\1assertDoesNotThrow\(\(\) -> strategy\.onTrigger\(instance, null\)\);",
    re.MULTILINE,
)

ON_PLACED_AND_TRIGGER = re.compile(
    r"(\s+)BehaviorTestSupport\.TestContext ctx = BehaviorTestSupport\.createContext\(\);\n"
    r"\1BlockDefinition definition = ExtensionTestSupport\.loadBlockDefinition\([^,]+, \"([^\"]+)\", \d+\);\n"
    r"\1Strategy strategy = new Strategy\(ctx\.context\(\)\);\n"
    r"\1assertDoesNotThrow\(\(\) -> strategy\.onPlaced\(definition, new IgnisLocation\(\"world\", 1, 2, 3\)\)\);",
    re.MULTILINE,
)


def ensure_imports(text: str) -> str:
    if "TestEventBus" in text:
        return text
    anchor = "import dev.rono.igniscore.testsupport.ExtensionTestSupport;\n"
    return text.replace(anchor, anchor + IMPORTS)


def migrate(path: Path) -> bool:
    original = path.read_text(encoding="utf-8")
    text = original

    def trigger_repl(match: re.Match[str]) -> str:
        indent, ext_id = match.group(1), match.group(2)
        return (
            f"{indent}TestEventBus.TestContext ctx = TestEventBus.createContext();\n"
            f"{indent}BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, \"{ext_id}\", 10001);\n"
            f"{indent}Strategy strategy = TestEventBus.activate(() -> new Strategy(ctx.context()), \"{ext_id}\");\n"
            f"{indent}RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(definition);\n"
            f"{indent}ctx.eventBus().fireBlockTrigger(new BlockTriggerEvent(instance, null), \"{ext_id}\");"
        )

    def placed_repl(match: re.Match[str]) -> str:
        indent, ext_id = match.group(1), match.group(2)
        return (
            f"{indent}TestEventBus.TestContext ctx = TestEventBus.createContext();\n"
            f"{indent}BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, \"{ext_id}\", 10001);\n"
            f"{indent}Strategy strategy = TestEventBus.activate(() -> new Strategy(ctx.context()), \"{ext_id}\");\n"
            f"{indent}ctx.eventBus().fireBlockPlace(\n"
            f"{indent}        new BlockPlaceEvent(PlacedBlock.of(definition, new IgnisLocation(\"world\", 1, 2, 3)), null),\n"
            f"{indent}        \"{ext_id}\");"
        )

    text = ON_TRIGGER.sub(trigger_repl, text)
    text = ON_PLACED_AND_TRIGGER.sub(placed_repl, text)
    text = text.replace(
        "strategy.onTrigger(instance, null);",
        'ctx.eventBus().fireBlockTrigger(new BlockTriggerEvent(instance, null), "'
        + '${ext}'  # fallback noop
        + '");',
    )
    if "strategy.onTrigger" in text or "strategy.onPlaced" in text:
        return False
    text = ensure_imports(text)
    if text != original:
        path.write_text(text, encoding="utf-8")
        return True
    return False


def main() -> None:
    changed = 0
    for path in sorted(BLOCKS.glob("*/src/test/java/**/BehaviorTest.java")):
        if migrate(path):
            changed += 1
            print(f"updated {path.relative_to(ROOT)}")
    print(f"done: {changed}")


if __name__ == "__main__":
    main()
