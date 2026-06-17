#!/usr/bin/env python3
"""Inline package-private listener helpers into @Override event handlers."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

HELPER_NAMES = (
    "onPlaced",
    "onPlacedBreak",
    "onPlacedInteract",
    "onTick",
    "onTrigger",
    "onPlace",
    "onItemUse",
    "assignBomb",
    "detonateLinkedBombs",
)


def find_matching_paren(text: str, open_idx: int) -> int:
    depth = 0
    i = open_idx
    while i < len(text):
        ch = text[i]
        if ch == "(":
            depth += 1
        elif ch == ")":
            depth -= 1
            if depth == 0:
                return i
        i += 1
    raise ValueError("unbalanced parens")


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
    raise ValueError("unbalanced braces")


def split_args(args: str) -> list[str]:
    parts: list[str] = []
    current: list[str] = []
    depth = 0
    for ch in args:
        if ch in "([{":
            depth += 1
        elif ch in ")]}":
            depth -= 1
        if ch == "," and depth == 0:
            parts.append("".join(current).strip())
            current = []
        else:
            current.append(ch)
    tail = "".join(current).strip()
    if tail:
        parts.append(tail)
    return parts


def extract_methods(text: str) -> dict[str, tuple[list[str], str]]:
    methods: dict[str, tuple[list[str], str]] = {}
    for name in HELPER_NAMES:
        pattern = re.compile(rf"\n\s+(?:void|private void)\s+{name}\s*\(")
        match = pattern.search(text)
        if not match:
            continue
        paren_start = text.index("(", match.start())
        paren_end = find_matching_paren(text, paren_start)
        params_raw = text[paren_start + 1 : paren_end]
        param_names = []
        for part in split_args(params_raw):
            if not part:
                continue
            param_names.append(part.rsplit(" ", 1)[-1].strip())
        brace_start = text.index("{", paren_end)
        brace_end = find_matching_brace(text, brace_start)
        body = text[brace_start + 1 : brace_end]
        methods[name] = (param_names, body)
    return methods


def substitute_params(body: str, param_names: list[str], arg_exprs: list[str]) -> str:
    if len(param_names) != len(arg_exprs):
        raise ValueError(f"param/arg mismatch: {param_names} vs {arg_exprs}")
    result = body
    for name, expr in sorted(zip(param_names, arg_exprs), key=lambda x: -len(x[0])):
        result = re.sub(rf"\b{re.escape(name)}\b", expr, result)
    return result


def indent_block(body: str, spaces: str = "        ") -> str:
    lines = body.splitlines()
    stripped = [line.rstrip() for line in lines]
    while stripped and not stripped[0].strip():
        stripped.pop(0)
    while stripped and not stripped[-1].strip():
        stripped.pop()
    if not stripped:
        return ""
    return "\n".join(spaces + line if line.strip() else "" for line in stripped)


def inline_override(text: str, override_start: int, override_end: int, methods: dict) -> tuple[str, bool]:
    block = text[override_start:override_end]
    changed = False

    # switch with helper calls (detonator)
    switch = re.search(
        r"switch\s*\(\s*event\.actionToken\(\)\s*\)\s*\{(.*)\}\s*$",
        block,
        re.S,
    )
    if switch:
        switch_body = switch.group(1)
        new_switch_body = switch_body
        for name in ("assignBomb", "detonateLinkedBombs"):
            if name not in methods:
                continue
            param_names, body = methods[name]
            call_re = re.compile(
                rf"case\s+\"[^\"]+\"\s*->\s*{name}\(([^)]*)\);",
            )
            for call in call_re.finditer(switch_body):
                args = split_args(call.group(1))
                inlined = indent_block(substitute_params(body, param_names, args))
                replacement = call.group(0).split("->", 1)[0] + "-> {\n" + inlined + "\n            }"
                new_switch_body = new_switch_body.replace(call.group(0), replacement)
                changed = True
        if changed:
            block = block[: switch.start(1)] + new_switch_body + block[switch.end(1) :]
            text = text[:override_start] + block + text[override_end:]
            return text, True

    call_match = re.search(r"\n\s+([a-zA-Z]+)\(([^;]*)\);\s*\n", block)
    if not call_match:
        return text, False
    helper = call_match.group(1)
    if helper not in methods:
        return text, False
    param_names, body = methods[helper]
    args = split_args(call_match.group(2))
    if len(args) != len(param_names):
        return text, False
    inlined = indent_block(substitute_params(body, param_names, args))
    new_block = re.sub(
        r"\n\s+[a-zA-Z]+\([^;]*\);\s*\n",
        "\n" + inlined + "\n",
        block,
        count=1,
    )
    if new_block == block:
        return text, False
    text = text[:override_start] + new_block + text[override_end:]
    return text, True


def remove_method(text: str, name: str) -> str:
    pattern = re.compile(rf"\n\s+(?:void|private void)\s+{name}\s*\(")
    match = pattern.search(text)
    if not match:
        return text
    paren_start = text.index("(", match.start())
    paren_end = find_matching_paren(text, paren_start)
    brace_start = text.index("{", paren_end)
    brace_end = find_matching_brace(text, brace_start)
    return text[: match.start()] + text[brace_end + 1 :]


def migrate_file(path: Path) -> bool:
    text = path.read_text()
    methods = extract_methods(text)
    if not methods:
        return False

    original = text
    removed: set[str] = set()

    while True:
        overrides = list(re.finditer(r"\n\s+@Override\n\s+public void ", text))
        if not overrides:
            break
        progress = False
        for match in reversed(overrides):
            brace_start = text.index("{", match.end())
            brace_end = find_matching_brace(text, brace_start)
            override_start = match.start()
            override_end = brace_end + 1
            text, changed = inline_override(text, override_start, override_end, methods)
            if changed:
                progress = True
                call_match = re.search(
                    r"\n\s+([a-zA-Z]+)\([^;]*\);\s*\n",
                    original[override_start:override_end],
                )
                if call_match and call_match.group(1) in methods:
                    removed.add(call_match.group(1))
        if not progress:
            break

    # Re-detect helpers still referenced (e.g. from switch inlining)
    methods = extract_methods(text)
    for name in list(methods):
        if name in removed or not re.search(rf"\b{name}\s*\(", text):
            text = remove_method(text, name)
            removed.add(name)

    # Remove helpers that are no longer called
    for name in HELPER_NAMES:
        if name in methods and name not in removed:
            if not re.search(rf"(?<!void )\b{name}\s*\(", text):
                text = remove_method(text, name)

    text = re.sub(r"\n{3,}", "\n\n", text)
    if text != original:
        path.write_text(text)
        return True
    return False


def main() -> None:
    count = 0
    for path in sorted(ROOT.glob("extensions/**/*Listeners.java")):
        if migrate_file(path):
            count += 1
            print(path.relative_to(ROOT))
    print(f"Inlined {count}")


if __name__ == "__main__":
    main()
