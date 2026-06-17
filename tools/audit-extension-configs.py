#!/usr/bin/env python3
"""Audit and repair extension config.yml custom_data against behavior templates.

Compares each utility extension's config.yml to the keys its Behavior template reads,
then merges missing keys using code defaults. Run after changing behaviors:

    python3 tools/audit-extension-configs.py
    python3 tools/audit-extension-configs.py --check   # exit 1 if any missing
"""

from __future__ import annotations

import argparse
import ast
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
GEN = ROOT / "tools" / "generate-utility-extensions.py"

CONSUMABLE_KEYS: dict[str, dict] = {
    "miners-lunch": {"cooldownTicks": 12000},
    "farmers-tea": {"cropRadius": 5},
    "divers-salt": {},
    "cartographers-espresso": {},
    "ghost-peppermint": {},
    "heavy-coat-tonic": {},
    "honey-throat-coat": {},
    "chorus-bite": {},
    "glow-berry-shot": {},
    "bricklayers-broth": {},
    "luck-dust": {},
    "antidote-swab": {},
    "unlabeled-potion": {},
}

FUSE_POWER: dict[str, float] = {
    "splitter": 4.0,
    "ricochet": 3.0,
    "cascade": 3.5,
    "powder_trail": 4.0,
    "bridge_builder": 2.5,
    "scaffold": 3.0,
    "pause_fuse": 4.5,
    "accelerating_fuse": 5.0,
    "echo_fuse": 4.0,
    "blink": 3.5,
    "swap": 2.5,
    "phase": 4.0,
    "rift": 4.5,
    "mirror": 4.0,
}


def load_registry() -> tuple[list[tuple[str, str, dict]], list[tuple[str, str, dict]]]:
    text = GEN.read_text(encoding="utf-8")
    blocks_part, items_part = text.split("ITEMS =", 1)
    blocks = []
    for m in re.finditer(
        r'\{"id": "([^"]+)", "name": "[^"]+", "kind": "([^"]+)"[^}]*\}',
        blocks_part,
    ):
        entry = m.group(0)
        fuse = re.search(r'"fuse": (\d+)', entry)
        combustible = '"combustible": False' in entry or '"combustible": false' in entry
        meta = {}
        if fuse:
            meta["fuse"] = int(fuse.group(1))
        if combustible:
            meta["combustible"] = False
        blocks.append((m.group(1), m.group(2), meta))
    items = []
    for m in re.finditer(
        r'\{"id": "([^"]+)", "name": "[^"]+", "kind": "([^"]+)"[^}]*\}',
        items_part.split("def pkg")[0],
    ):
        entry = m.group(0)
        custom = {}
        cd = re.search(r'"custom_data": (\{[^}]+\})', entry)
        if cd:
            custom = ast.literal_eval(cd.group(1).replace("true", "True").replace("false", "False"))
        items.append((m.group(1), m.group(2), custom))
    return blocks, items


def parse_literal(raw: str):
    raw = raw.strip()
    if raw.startswith('"') and raw.endswith('"'):
        return raw[1:-1]
    if raw in ("true", "false"):
        return raw == "true"
    if raw == "loc.y()":
        return 64.0  # documentable default; runtime uses placement Y
    try:
        if "." in raw:
            return float(raw)
        return int(raw)
    except ValueError:
        return raw


def keys_from_template(kind: str) -> dict:
    path = ROOT / "tools" / "utility-behaviors" / f"{kind}.java.template"
    if not path.exists():
        return {}
    text = path.read_text(encoding="utf-8")
    keys: dict[str, object] = {}
    for m in re.finditer(
        r'StrategySupport\.custom(Int|Double|Boolean|String)\([^,]+,\s*"([^"]+)"\s*,\s*([^)]+)\)',
        text,
    ):
        key = m.group(2)
        if key == "pauseAtElapsed":
            continue
        keys[key] = parse_literal(m.group(3))
    if kind in FUSE_POWER:
        keys.setdefault("power", FUSE_POWER[kind])
    if "ExplosionSupport.createExplosion(world, loc, def, " in text and "power" not in keys:
        m = re.search(r"createExplosion\(world, loc, def,\s*([\d.]+)", text)
        if m:
            keys["power"] = float(m.group(1))
    return keys


def expected_custom(ext_id: str, kind: str, meta: dict) -> dict:
    if kind == "consumable_item":
        base = {"cooldownTicks": 0}
        base.update(CONSUMABLE_KEYS.get(ext_id, {}))
        return base
    keys = keys_from_template(kind)
    if "fuse" in meta:
        keys["fuse"] = meta["fuse"]
    for k, v in meta.items():
        if k != "combustible":
            keys[k] = v
    return keys


def config_path(ext_id: str, is_block: bool) -> Path:
    base = ROOT / "extensions" / ("blocks" if is_block else "items") / ext_id
    return base / "src/main/resources/config.yml"


def parse_simple_yaml(path: Path) -> tuple[str, dict | None]:
    text = path.read_text(encoding="utf-8")
    custom: dict | None = None
    if "\ncustom_data:\n" in text:
        head, tail = text.split("\ncustom_data:\n", 1)
        custom = {}
        for line in tail.splitlines():
            if not line.strip() or not line.startswith("  "):
                break
            line = line.strip()
            if ": " not in line:
                continue
            k, v = line.split(": ", 1)
            if v.startswith('"') and v.endswith('"'):
                custom[k] = v[1:-1]
            elif v in ("true", "false"):
                custom[k] = v == "true"
            else:
                try:
                    custom[k] = int(v) if "." not in v else float(v)
                except ValueError:
                    custom[k] = v
        text = head.rstrip() + "\n"
    return text, custom


def format_custom(custom: dict) -> str:
    if not custom:
        return ""
    lines = ["", "custom_data:"]
    for key in sorted(custom.keys()):
        value = custom[key]
        if isinstance(value, str):
            lines.append(f'  {key}: "{value}"')
        elif isinstance(value, bool):
            lines.append(f"  {key}: {'true' if value else 'false'}")
        else:
            lines.append(f"  {key}: {value}")
    return "\n".join(lines) + "\n"


def repair(check_only: bool) -> int:
    blocks, items = load_registry()
    issues = 0
    for ext_id, kind, meta in blocks:
        path = config_path(ext_id, True)
        if not path.exists():
            print(f"MISSING FILE block {ext_id}")
            issues += 1
            continue
        expected = expected_custom(ext_id, kind, meta)
        head, actual = parse_simple_yaml(path)
        actual = actual or {}
        missing = {k: v for k, v in expected.items() if k not in actual}
        if missing:
            issues += len(missing)
            print(f"{ext_id}: missing {missing}")
            if not check_only:
                merged = {**expected, **actual}
                path.write_text(head.rstrip() + format_custom(merged), encoding="utf-8")
    for ext_id, kind, meta in items:
        path = config_path(ext_id, False)
        if not path.exists():
            print(f"MISSING FILE item {ext_id}")
            issues += 1
            continue
        expected = expected_custom(ext_id, kind, meta)
        head, actual = parse_simple_yaml(path)
        actual = actual or {}
        missing = {k: v for k, v in expected.items() if k not in actual}
        if missing:
            issues += len(missing)
            print(f"{ext_id}: missing {missing}")
            if not check_only:
                merged = {**expected, **actual}
                path.write_text(head.rstrip() + format_custom(merged), encoding="utf-8")
    if check_only and issues:
        print(f"\n{issues} missing custom_data key(s)")
        return 1
    if not check_only:
        print(f"Repaired configs; {issues} key(s) were missing before repair.")
    return 0


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    sys.exit(repair(args.check))


if __name__ == "__main__":
    main()
