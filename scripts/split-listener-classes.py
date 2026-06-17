#!/usr/bin/env python3
"""Split monolithic *Listeners classes into per-event listener files."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

EVENT_TYPES = [
    ("onBlockPlace", "BlockPlaceEvent", "OnBlockPlaceListener", "OnBlockPlace"),
    ("onBlockBreak", "BlockBreakEvent", "OnBlockBreakListener", "OnBlockBreak"),
    ("onBlockInteract", "BlockInteractEvent", "OnBlockInteractListener", "OnBlockInteract"),
    ("onBlockClick", "BlockClickEvent", "OnBlockClickListener", "OnBlockClick"),
    ("onBlockActivate", "BlockActivateEvent", "OnBlockActivateListener", "OnBlockActivate"),
    ("onBlockTick", "BlockTickEvent", "OnBlockTickListener", "OnBlockTick"),
    ("onBlockTrigger", "BlockTriggerEvent", "OnBlockTriggerListener", "OnBlockTrigger"),
    ("onItemClick", "ItemClickEvent", "OnItemClickListener", "OnItemClick"),
]


def find_matching_brace(text: str, open_idx: int) -> int:
    depth = 0
    i = open_idx
    in_string = False
    escape = False
    while i < len(text):
        ch = text[i]
        if in_string:
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == '"':
                in_string = False
            i += 1
            continue
        if ch == '"':
            in_string = True
        elif ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            if depth == 0:
                return i
        i += 1
    raise ValueError("unbalanced")


def extract_override(text: str, method: str) -> str | None:
    pattern = rf"@Override\s+public void {method}\([^)]+\)\s*\{{"
    match = re.search(pattern, text)
    if not match:
        return None
    brace = text.index("{", match.end() - 1)
    end = find_matching_brace(text, brace)
    return text[match.start() : end + 1]


def parse_class(text: str) -> dict:
    pkg = re.search(r"^package (.+);", text, re.M).group(1)
    imports = re.findall(r"^import .+;$", text, re.M)
    class_match = re.search(r"final class (\w+)", text)
    if not class_match:
        return {}
    class_name = class_match.group(1)
    impl_match = re.search(r"implements (.+) \{", text, re.S)
    interfaces = [i.strip() for i in impl_match.group(1).split(",")] if impl_match else []
    body_start = text.index("{", impl_match.end() - 1)
    body_end = text.rfind("}")
    body = text[body_start + 1 : body_end]
    # remove @Override blocks from body
    core_body = body
    for method, _, _, _ in EVENT_TYPES:
        block = extract_override(body, method)
        if block:
            core_body = core_body.replace(block, "")
    return {
        "pkg": pkg,
        "imports": imports,
        "class_name": class_name,
        "interfaces": interfaces,
        "core_body": core_body.strip(),
        "text": text,
    }


def needs_context(fields_body: str) -> bool:
    return "context" in fields_body or "IgnisStrategyContext" in fields_body


def split_file(path: Path) -> bool:
    if "auto-sieve" in path.as_posix():
        return False
    text = path.read_text()
    if "*Listeners" not in path.name:
        return False
    info = parse_class(text)
    if not info:
        return False
    overrides = []
    for method, event, listener, suffix in EVENT_TYPES:
        block = extract_override(info["text"], method)
        if block:
            overrides.append((method, event, listener, suffix, block))
    if len(overrides) <= 1:
        return False

    prefix = info["class_name"].replace("Listeners", "")
    pkg = info["pkg"]
    dir_path = path.parent
    support_name = f"{prefix}Support"
    support_path = dir_path / f"{support_name}.java"

    # Build support from private methods + shared fields
    fields = []
    for line in info["core_body"].splitlines():
        if re.match(r"\s+private (?:static )?(?:final )?", line) or re.match(r"\s+private static final", line):
            fields.append(line)
    helpers = []
    in_method = False
    depth = 0
    buf: list[str] = []
    for line in info["core_body"].splitlines():
        if re.match(r"\s+private ", line) and "(" in line and not line.strip().startswith("private static final"):
            in_method = True
            depth = 0
            buf = [line]
            continue
        if in_method:
            buf.append(line)
            depth += line.count("{") - line.count("}")
            if depth <= 0 and "{" in "".join(buf):
                helpers.append("\n".join(buf))
                in_method = False
    ctor_params = []
    ctor_assign = []
    if any("IgnisStrategyContext context" in f for f in fields):
        ctor_params.append("IgnisStrategyContext context")
        ctor_assign.append("        this.context = context;")
    for f in fields:
        if "Registry" in f or "Storage" in f:
            m = re.search(r"private final (\w+) (\w+);", f)
            if m:
                ctor_params.append(f"{m.group(1)} {m.group(2)}")
                ctor_assign.append(f"        this.{m.group(2)} = {m.group(2)};")

    # write per-event listeners
    created = []
    for method, event, listener, suffix, block in overrides:
        listener_class = f"{prefix}{suffix}Listener"
        uses_context = "context" in block or "registry" in block or "linkStorage" in block
        listener_imports = set(info["imports"])
        listener_imports.add(f"dev.rono.igniscore.api.event.{event}")
        listener_imports.add(f"dev.rono.igniscore.api.event.{listener}")
        if uses_context and "IgnisStrategyContext" in info["text"]:
            listener_imports.add("dev.rono.igniscore.api.strategy.IgnisStrategyContext")
        fields_decl = ""
        ctor = ""
        if uses_context and any("IgnisStrategyContext context" in f for f in fields):
            fields_decl = "    private final IgnisStrategyContext context;\n\n"
            ctor = (
                f"    {listener_class}(IgnisStrategyContext context) {{\n"
                f"        this.context = context;\n"
                f"    }}\n\n"
            )
        body_method = re.sub(r"@Override\s+", "", block)
        content = (
            f"package {pkg};\n\n"
            + "\n".join(sorted(listener_imports))
            + f"\n\nfinal class {listener_class} implements {listener} {{\n"
            + fields_decl
            + ctor
            + f"    @{body_method}\n"
            + "}\n"
        )
        out = dir_path / f"{listener_class}.java"
        # delegate shared private methods via support - keep monolithic if helpers exist
        if helpers:
            return False
        out.write_text(content)
        created.append((listener_class, uses_context, listener))

    if not created:
        return False

    # update strategy
    strategy_path = dir_path / "Strategy.java"
    if not strategy_path.exists():
        return False
    st = strategy_path.read_text()
    old_var = f"{info['class_name']} listeners = new {info['class_name']}(context);"
    subs = []
    for listener_class, uses_context, listener_iface in created:
        if uses_context:
            subs.append(f"        context.eventBus().subscribe(new {listener_class}(context));")
        else:
            subs.append(f"        context.eventBus().subscribe(new {listener_class}());")
    st = st.replace(old_var, "\n".join(subs) + "\n")
    for _, _, listener_iface in created:
        st = re.sub(rf"\s*context\.eventBus\(\)\.subscribe\(\({listener_iface}\) listeners\);\n", "", st)
    strategy_path.write_text(st)
    path.unlink()
    return True


def main() -> None:
    count = 0
    for path in sorted(ROOT.glob("extensions/**/*Listeners.java")):
        if split_file(path):
            count += 1
            print(path.relative_to(ROOT))
    print(f"Split {count}")


if __name__ == "__main__":
    main()
