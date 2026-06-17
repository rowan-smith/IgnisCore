#!/usr/bin/env python3
"""Update extension StrategyTest files after StrategyProfile removal."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BLOCKS = ROOT / "extensions" / "blocks"

PROFILE_TEST = re.compile(
    r"\n\s*@Test\s*\n\s*void strategyExposesProfile(?:ForConfig)?\(\) \{[^}]+\}\n?",
    re.MULTILINE | re.DOTALL,
)

REPLACEMENT = """
    @Test
    void strategyConstructs() {
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());
        assertNotNull(strategy);
    }
"""


def migrate_test(path: Path) -> bool:
    original = path.read_text(encoding="utf-8")
    text = original
    text = PROFILE_TEST.sub(REPLACEMENT, text)
    text = text.replace("import dev.rono.igniscore.api.model.BlockDefinition;\n", "")
    text = text.replace("import dev.rono.igniscore.api.strategy.StrategyProfile;\n", "")
    if text != original:
        path.write_text(text, encoding="utf-8")
        return True
    return False


def main() -> None:
    changed = 0
    for test in sorted(BLOCKS.glob("*/src/test/java/**/StrategyTest.java")):
        if migrate_test(test):
            changed += 1
            print(f"updated {test.relative_to(ROOT)}")
    print(f"done: {changed} tests")


if __name__ == "__main__":
    main()
