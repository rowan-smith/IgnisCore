#!/usr/bin/env python3
"""Migrate Strategy.java files from override methods to event bus subscriptions."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

METHOD_PATTERNS = [
    (
        re.compile(
            r"@Override\s+public void onPlaced\(BlockDefinition definition, IgnisLocation location, IgnisItem placedFrom\) \{\s*(.*?)\s*\}",
            re.DOTALL,
        ),
        lambda body: f"onBlockPlace(event -> {rewrite_placed_from(body)})",
    ),
    (
        re.compile(
            r"@Override\s+public void onPlaced\(BlockDefinition definition, IgnisLocation location\) \{\s*(.*?)\s*\}",
            re.DOTALL,
        ),
        lambda body: f"onBlockPlace(event -> {rewrite_placed(body)})",
    ),
    (
        re.compile(
            r"@Override\s+public void onPlacedBreak\(BlockDefinition definition, IgnisLocation location, IgnisItem droppedItem\) \{\s*(.*?)\s*\}",
            re.DOTALL,
        ),
        lambda body: f"onBlockBreak(event -> {rewrite_break_dropped(body)})",
    ),
    (
        re.compile(
            r"@Override\s+public void onPlacedBreak\(BlockDefinition definition, IgnisLocation location\) \{\s*(.*?)\s*\}",
            re.DOTALL,
        ),
        lambda body: f"onBlockBreak(event -> {rewrite_break(body)})",
    ),
    (
        re.compile(
            r"@Override\s+public void onPlacedInteract\(BlockDefinition definition,\s*IgnisLocation location,\s*IgnisPlayer player,\s*[^\)]+\) \{\s*(.*?)\s*\}",
            re.DOTALL,
        ),
        lambda body: f"onBlockInteract(event -> {rewrite_interact(body)})",
    ),
    (
        re.compile(
            r"@Override\s+public void onPlace\(RuntimeBlockInstance instance\) \{\s*(.*?)\s*\}",
            re.DOTALL,
        ),
        lambda body: f"onBlockActivate(event -> {rewrite_instance(body)})",
    ),
    (
        re.compile(
            r"@Override\s+public void onTick\(RuntimeBlockInstance instance\) \{\s*(.*?)\s*\}",
            re.DOTALL,
        ),
        lambda body: f"onBlockTick(event -> {rewrite_instance(body)})",
    ),
    (
        re.compile(
            r"@Override\s+public void onTrigger\(RuntimeBlockInstance instance, Object \w+\) \{\s*(.*?)\s*\}",
            re.DOTALL,
        ),
        lambda body: f"onBlockTrigger(event -> {rewrite_trigger(body)})",
    ),
    (
        re.compile(
            r"@Override\s+public void onItemAction\(IgnisPlayer player, ItemDefinition definition, IgnisItem item,\s*IgnisInteraction action, IgnisBlock clickedBlock, String actionToken\) \{\s*(.*?)\s*\}",
            re.DOTALL,
        ),
        lambda body: f"onItemClick(event -> {{ {rewrite_item_action(body)} }})",
    ),
    (
        re.compile(
            r"@Override\s+public void onItemUse\(IgnisPlayer player, ItemDefinition definition, IgnisItem item,\s*IgnisInteraction action, IgnisBlock clickedBlock\) \{\s*(.*?)\s*\}",
            re.DOTALL,
        ),
        lambda body: f"onItemClick(event -> {{ {rewrite_item_use(body)} }})",
    ),
]


def rewrite_placed(body: str) -> str:
    body = body.strip().rstrip(";")
    body = body.replace("definition", "event.definition()")
    body = body.replace("location", "event.location()")
    return body


def rewrite_placed_from(body: str) -> str:
    body = body.strip().rstrip(";")
    body = body.replace("definition", "event.definition()")
    body = body.replace("location", "event.location()")
    body = body.replace("placedFrom", "event.placedFrom()")
    return body


def rewrite_break(body: str) -> str:
    body = body.strip().rstrip(";")
    body = body.replace("definition", "event.definition()")
    body = body.replace("location", "event.location()")
    return body


def rewrite_break_dropped(body: str) -> str:
    body = body.strip().rstrip(";")
    body = body.replace("definition", "event.definition()")
    body = body.replace("location", "event.location()")
    body = body.replace("droppedItem", "event.droppedItem()")
    return body


def rewrite_interact(body: str) -> str:
    body = body.strip().rstrip(";")
    replacements = {
        "definition": "event.definition()",
        "location": "event.location()",
        "player": "event.player()",
        "interaction": "event.interaction()",
        "heldItem": "event.heldItem()",
        "action": "event.action()",
    }
    for old, new in replacements.items():
        body = re.sub(rf"\b{old}\b", new, body)
    return body


def rewrite_instance(body: str) -> str:
    body = body.strip().rstrip(";")
    return body.replace("instance", "event.instance()")


def rewrite_trigger(body: str) -> str:
    body = body.strip().rstrip(";")
    body = body.replace("instance", "event.instance()")
    body = re.sub(r"\bcontext\b", "event.triggerContext()", body)
    body = re.sub(r"\btriggerContext\b", "event.triggerContext()", body)
    body = body.replace("event.instance().getDefinition()", "event.instance().getDefinition()")
    return body


def rewrite_item_action(body: str) -> str:
    body = body.strip()
    body = body.replace('"throw".equals(actionToken)', '"throw".equals(event.actionToken())')
    body = body.replace('"use".equals(actionToken)', '"use".equals(event.actionToken())')
    body = body.replace("actionToken", "event.actionToken()")
    body = re.sub(r"\bplayer\b", "event.player()", body)
    body = re.sub(r"\bdefinition\b", "event.definition()", body)
    body = re.sub(r"\bitem\b", "event.item()", body)
    body = re.sub(r"\bclickedBlock\b", "event.clickedBlock()", body)
    return body


def rewrite_item_use(body: str) -> str:
    if "ItemBehaviorConfig" in body and "actionFor" in body:
        inner = re.search(r"ifPresent\(token -> \{(.*)\}\);?", body, re.DOTALL)
        if inner:
            token_body = inner.group(1).strip()
            token_body = token_body.replace('"use".equals(token)', '"use".equals(event.actionToken())')
            token_body = token_body.replace('"assign".equals(token)', '"assign".equals(event.actionToken())')
            token_body = token_body.replace('"detonate".equals(token)', '"detonate".equals(event.actionToken())')
            token_body = re.sub(r"\bplayer\b", "event.player()", token_body)
            token_body = re.sub(r"\bdefinition\b", "event.definition()", token_body)
            token_body = re.sub(r"\bitem\b", "event.item()", token_body)
            token_body = re.sub(r"\bclickedBlock\b", "event.clickedBlock()", token_body)
            if "switch (token)" in token_body or "switch (event.actionToken())" in token_body:
                token_body = token_body.replace("switch (token)", "switch (event.actionToken())")
                token_body = token_body.replace('case "assign"', 'case "assign"')
            return f"if (event.actionToken() != null) {{ {token_body} }}"
    body = body.strip().rstrip(";")
    body = re.sub(r"\bplayer\b", "event.player()", body)
    body = re.sub(r"\bdefinition\b", "event.definition()", body)
    body = re.sub(r"\bitem\b", "event.item()", body)
    body = re.sub(r"\bclickedBlock\b", "event.clickedBlock()", body)
    body = re.sub(r"\baction\b", "event.interaction()", body)
    return body


def strip_unused_imports(content: str) -> str:
    for imp in [
        "import dev.rono.igniscore.api.config.ItemBehaviorConfig;\n",
        "import dev.rono.igniscore.api.model.RuntimeBlockInstance;\n",
        "import dev.rono.igniscore.api.port.IgnisBlock;\n",
        "import dev.rono.igniscore.api.port.IgnisInteraction;\n",
        "import dev.rono.igniscore.api.port.IgnisItem;\n",
        "import dev.rono.igniscore.api.port.IgnisLocation;\n",
        "import dev.rono.igniscore.api.port.IgnisPlayer;\n",
        "import dev.rono.igniscore.api.model.ItemDefinition;\n",
    ]:
        if imp.split()[1] not in content.replace(imp, ""):
            content = content.replace(imp, "")
    return content


def migrate_file(path: Path) -> bool:
    original = path.read_text()
    content = original
    subscriptions: list[str] = []

    for pattern, builder in METHOD_PATTERNS:
        while True:
            match = pattern.search(content)
            if not match:
                break
            subscriptions.append(builder(match.group(1)))
            content = pattern.sub("", content, count=1)

    if not subscriptions:
        return False

    content = re.sub(r"\n{3,}", "\n\n", content)

    register_block = "\n".join(f"        {line};" for line in subscriptions)
    if "registerEvents()" in content:
        content = re.sub(
            r"@Override\s+public void registerEvents\(\) \{\s*.*?\s*\}",
            f"@Override\n    public void registerEvents() {{\n{register_block}\n    }}",
            content,
            count=1,
            flags=re.DOTALL,
        )
    elif "public StrategyProfile profile" in content:
        content = re.sub(
            r"(public StrategyProfile profile\(BlockDefinition definition\) \{.*?\n    \})",
            rf"\1\n\n    @Override\n    public void registerEvents() {{\n{register_block}\n    }}",
            content,
            count=1,
            flags=re.DOTALL,
        )
    else:
        content = re.sub(
            r"(public Strategy\(IgnisStrategyContext context\) \{.*?\n    \})",
            rf"\1\n\n    @Override\n    public void registerEvents() {{\n{register_block}\n    }}",
            content,
            count=1,
            flags=re.DOTALL,
        )

    content = strip_unused_imports(content)
    if content != original:
        path.write_text(content)
        return True
    return False


def main() -> None:
    changed = 0
    for path in sorted(ROOT.glob("extensions/**/Strategy.java")):
        if migrate_file(path):
            changed += 1
            print(f"migrated {path.relative_to(ROOT)}")
    print(f"done: {changed} files")


if __name__ == "__main__":
    main()
