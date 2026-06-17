#!/usr/bin/env python3
"""Split *Listeners.java into per-event listener classes + Support/Runtime helpers."""

from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

EVENTS = [
    ("onBlockPlace", "BlockPlaceEvent", "OnBlockPlaceListener", "OnBlockPlace"),
    ("onBlockBreak", "BlockBreakEvent", "OnBlockBreakListener", "OnBlockBreak"),
    ("onBlockInteract", "BlockInteractEvent", "OnBlockInteractListener", "OnBlockInteract"),
    ("onBlockClick", "BlockClickEvent", "OnBlockClickListener", "OnBlockClick"),
    ("onBlockActivate", "BlockActivateEvent", "OnBlockActivateListener", "OnBlockActivate"),
    ("onBlockTick", "BlockTickEvent", "OnBlockTickListener", "OnBlockTick"),
    ("onBlockTrigger", "BlockTriggerEvent", "OnBlockTriggerListener", "OnBlockTrigger"),
]

SKIP_PATH_PARTS = ("auto-sieve", "xp-vacuum", "items/")


def brace_end(text: str, open_idx: int) -> int:
    depth = 0
    i = open_idx
    while i < len(text):
        if text[i] == "{":
            depth += 1
        elif text[i] == "}":
            depth -= 1
            if depth == 0:
                return i
        i += 1
    raise ValueError("unbalanced brace")


def extract_override(text: str, method: str) -> str | None:
    m = re.search(rf"\n\s+@Override\n\s+public void {method}\([^)]*\)\s*\{{", text)
    if not m:
        return None
    body_open = text.index("{", m.end() - 1)
    body_close = brace_end(text, body_open)
    return text[body_open + 1 : body_close]


def extract_constructor_body(text: str, class_name: str) -> str:
    m = re.search(rf"\n\s+{re.escape(class_name)}\(IgnisStrategyContext context\)\s*\{{", text)
    if not m:
        return ""
    body_open = text.index("{", m.end() - 1)
    body_close = brace_end(text, body_open)
    return text[body_open + 1 : body_close].strip()


def extract_private_methods(text: str) -> list[str]:
    methods: list[str] = []
    for m in re.finditer(
        r"\n\s+private (?!static)(?:[\w<>,\s\[\]]+) \w+\([^)]*\)\s*\{",
        text,
    ):
        start = m.start() + 1
        body_open = text.index("{", m.end() - 1)
        body_close = brace_end(text, body_open)
        chunk = text[start : body_close + 1]
        if "@Override" in chunk:
            continue
        methods.append(chunk)
    return methods


def extract_static_constants(text: str) -> list[str]:
    return [line.strip() for line in re.findall(r"^\s+private static final .+;$", text, re.M)]


def extract_instance_fields(text: str) -> list[tuple[str, str]]:
    return [(m.group(1), m.group(2)) for m in re.finditer(r"^\s+private final (\S+) (\w+);", text, re.M)]


def method_signature(chunk: str) -> re.Match[str] | None:
    return re.search(r"private ((?:[\w<>,\s\[\]]+)) (\w+)\(([^)]*)\)", chunk)


def normalize_indent(body: str, indent: str = "        ") -> str:
    lines = [ln.rstrip() for ln in body.splitlines()]
    while lines and not lines[0].strip():
        lines.pop(0)
    while lines and not lines[-1].strip():
        lines.pop()
    if not lines:
        return ""
    min_indent = min(len(ln) - len(ln.lstrip()) for ln in lines if ln.strip())
    out: list[str] = []
    for ln in lines:
        out.append(indent + ln[min_indent:] if ln.strip() else "")
    return "\n".join(out)


def uses_identifier(code: str, name: str) -> bool:
    return bool(re.search(rf"\b{re.escape(name)}\b", code))


def rewrite_fields(code: str, mapping: dict[str, str]) -> str:
    for field, replacement in mapping.items():
        code = re.sub(rf"\b{re.escape(field)}\.", f"{replacement}.", code)
        code = re.sub(rf"\b{re.escape(field)}\b(?!\s*\.)", replacement, code)
    return code


def convert_private_to_static(
    chunk: str,
    *,
    runtime_name: str | None,
    support_method_names: list[str],
) -> str:
    sig = method_signature(chunk)
    if not sig:
        return chunk
    ret, name, params = sig.group(1).strip(), sig.group(2), sig.group(3).strip()
    body_open = chunk.index("{")
    body = chunk[body_open + 1 : -1]

    if runtime_name:
        body = rewrite_fields(body, {"context": "runtime.context", "registry": "runtime.registry", "nbtService": "runtime.nbtService"})
        prefix = f"static {ret} {name}({runtime_name} runtime"
    else:
        body = body.replace("context.", "ctx.")
        prefix = f"static {ret} {name}(IgnisStrategyContext ctx"

    if params:
        converted = f"{prefix}, {params}) {{\n{body}\n    }}"
    else:
        converted = f"{prefix}) {{\n{body}\n    }}"

    # fix internal helper calls
    for helper in support_method_names:
        if helper == name:
            continue
        if runtime_name:
            converted = re.sub(
                rf"\b{re.escape(helper)}\(",
                f"{helper}(runtime, ",
                converted,
            )
        else:
            converted = re.sub(
                rf"\b{re.escape(helper)}\(",
                f"{helper}(ctx, ",
                converted,
            )
    return converted


def filter_imports(
    pkg: str,
    content: str,
    source_imports: list[str],
    required: list[str],
) -> list[str]:
    required_imports: set[str] = set()
    for item in required:
        required_imports.add(item if item.startswith("import ") else f"import {item};")

    needed: set[str] = set(required_imports)
    simple = set(re.findall(r"\b([A-Z][A-Za-z0-9_]*)\b", content))
    for imp in source_imports:
        cls = imp.rsplit(".", 1)[-1].rstrip(";")
        if cls in simple:
            needed.add(imp)
    skip_suffixes = (
        "OnBlockPlaceListener;",
        "OnBlockBreakListener;",
        "OnBlockInteractListener;",
        "OnBlockClickListener;",
        "OnBlockActivateListener;",
        "OnBlockTickListener;",
        "OnBlockTriggerListener;",
    )
    local_types = {f"import {pkg}.{t};" for t in simple}
    return sorted(
        imp
        for imp in needed
        if imp not in local_types
        and (imp in required_imports or not any(imp.endswith(suffix) for suffix in skip_suffixes))
    )


def write_java(path: Path, pkg: str, imports: list[str], body: str) -> None:
    path.write_text(f"package {pkg};\n\n" + "\n".join(imports) + f"\n\n{body}\n")


def split_listeners(path: Path) -> bool:
    if any(part in path.as_posix() for part in SKIP_PATH_PARTS):
        return False
    if not path.name.endswith("Listeners.java"):
        return False

    text = path.read_text()
    class_m = re.search(r"final class (\w+)", text)
    if not class_m:
        return False
    class_name = class_m.group(1)
    prefix = class_name.replace("Listeners", "")

    overrides: list[tuple[str, str, str, str, str]] = []
    for method, event, iface, suffix in EVENTS:
        body = extract_override(text, method)
        if body is not None:
            overrides.append((method, event, iface, suffix, body))
    if not overrides:
        return False

    pkg_m = re.search(r"^package (.+);", text, re.M)
    if not pkg_m:
        return False
    pkg = pkg_m.group(1)
    source_imports = list(dict.fromkeys(re.findall(r"^import .+;$", text, re.M)))
    dir_path = path.parent

    static_constants = extract_static_constants(text)
    instance_fields = extract_instance_fields(text)
    private_methods = extract_private_methods(text)
    private_method_names = [sig.group(2) for pm in private_methods if (sig := method_signature(pm))]
    ctor_body = extract_constructor_body(text, class_name)

    field_names = [name for _, name in instance_fields]
    has_context_field = "context" in field_names
    needs_runtime = len(instance_fields) > 1 or (len(instance_fields) == 1 and not has_context_field)

    runtime_name = f"{prefix}Runtime"
    support_name = f"{prefix}Support"

    field_mapping: dict[str, str] = {}
    if needs_runtime:
        for _, name in instance_fields:
            field_mapping[name] = f"runtime.{name}"
    elif has_context_field:
        field_mapping["context"] = "context"

    support_path = dir_path / f"{support_name}.java"
    if private_methods or static_constants:
        chunks: list[str] = []
        if static_constants:
            chunks.append("\n".join(f"    {c.replace('private static final', 'static final')}" for c in static_constants))
        for pm in private_methods:
            chunks.append(
                "    "
                + convert_private_to_static(
                    pm,
                    runtime_name=runtime_name if needs_runtime else None,
                    support_method_names=private_method_names,
                )
            )
        support_content = "\n\n".join(chunks)
        support_imports = filter_imports(
            pkg,
            support_content,
            source_imports,
            (["dev.rono.igniscore.api.strategy.IgnisStrategyContext"] if has_context_field and not needs_runtime else [])
            + ([f"{pkg}.{runtime_name}"] if needs_runtime else []),
        )
        write_java(
            support_path,
            pkg,
            support_imports,
            f"final class {support_name} {{\n    private {support_name}() {{\n    }}\n\n{support_content}\n}}\n",
        )

    runtime_path = dir_path / f"{runtime_name}.java"
    if needs_runtime:
        field_decls = "\n".join(f"    final {typ} {name};" for typ, name in instance_fields)
        init_lines = "\n".join(f"        {line}" for line in ctor_body.splitlines() if line.strip())
        runtime_imports = filter_imports(
            pkg,
            field_decls + init_lines,
            source_imports,
            ["dev.rono.igniscore.api.strategy.IgnisStrategyContext"],
        )
        write_java(
            runtime_path,
            pkg,
            runtime_imports,
            f"final class {runtime_name} {{\n{field_decls}\n\n"
            f"    {runtime_name}(IgnisStrategyContext context) {{\n{init_lines}\n    }}\n}}\n",
        )

    created: list[tuple[str, str]] = []
    for method, event, iface, suffix, raw_body in overrides:
        listener_name = f"{prefix}{suffix}Listener"
        method_body = normalize_indent(raw_body)
        method_body = rewrite_fields(method_body, field_mapping)

        if private_method_names and support_path.exists():
            call_prefix = "runtime, " if needs_runtime else ("context, " if has_context_field else "")
            for fn in private_method_names:
                method_body = re.sub(rf"\b{re.escape(fn)}\(", f"{support_name}.{fn}({call_prefix}", method_body)

        if static_constants and support_path.exists():
            for const_line in static_constants:
                cm = re.search(r"(\w+)\s*=", const_line)
                if cm:
                    method_body = re.sub(rf"\b{cm.group(1)}\b", f"{support_name}.{cm.group(1)}", method_body)

        listener_content = method_body
        ctor_arg = ""
        fields_decl = ""
        ctor_decl = ""

        if needs_runtime:
            fields_decl = f"    private final {runtime_name} runtime;\n\n"
            ctor_decl = f"    {listener_name}({runtime_name} runtime) {{\n        this.runtime = runtime;\n    }}\n\n"
            ctor_arg = "runtime"
        elif has_context_field and uses_identifier(listener_content, "context"):
            fields_decl = "    private final IgnisStrategyContext context;\n\n"
            ctor_decl = f"    {listener_name}(IgnisStrategyContext context) {{\n        this.context = context;\n    }}\n\n"
            ctor_arg = "context"

        required_imports = [
            f"dev.rono.igniscore.api.event.{event}",
            f"dev.rono.igniscore.api.event.{iface}",
        ]
        if ctor_arg == "context":
            required_imports.append("dev.rono.igniscore.api.strategy.IgnisStrategyContext")
        if ctor_arg == "runtime":
            required_imports.append(f"{pkg}.{runtime_name}")
        if private_method_names or static_constants:
            if f"{support_name}." in listener_content or any(
                f"{support_name}.{c.split('=')[0].split()[-1].strip()}" in listener_content
                for c in static_constants
                if "=" in c
            ):
                required_imports.append(f"{pkg}.{support_name}")

        listener_imports = filter_imports(pkg, fields_decl + listener_content, source_imports, required_imports)
        write_java(
            dir_path / f"{listener_name}.java",
            pkg,
            listener_imports,
            f"final class {listener_name} implements {iface} {{\n"
            f"{fields_decl}{ctor_decl}"
            f"    @Override\n"
            f"    public void {method}({event} event) {{\n"
            f"{listener_content}\n"
            f"    }}\n}}\n",
        )
        created.append((listener_name, ctor_arg))

    strategy_path = dir_path / "Strategy.java"
    if not strategy_path.exists():
        raise FileNotFoundError(f"Strategy.java not found for {path}")

    st = strategy_path.read_text()
    st = re.sub(
        rf"^        {re.escape(class_name)} listeners = new {re.escape(class_name)}\(context\);\n",
        "",
        st,
        flags=re.M,
    )
    st = re.sub(
        rf"^        context\.eventBus\(\)\.subscribe\(new {re.escape(class_name)}\(context\)\);\n",
        "",
        st,
        flags=re.M,
    )
    st = re.sub(
        r"^        context\.eventBus\(\)\.subscribe\(\(\w+\) listeners\);\n",
        "",
        st,
        flags=re.M,
    )
    for _, _, iface, _ in EVENTS:
        st = re.sub(rf"^import dev\.rono\.igniscore\.api\.event\.{iface};\n", "", st, flags=re.M)

    subs_lines: list[str] = []
    if needs_runtime:
        subs_lines.append(f"        {runtime_name} runtime = new {runtime_name}(context);")
    for listener_name, ctor_arg in created:
        if ctor_arg == "runtime":
            subs_lines.append(f"        context.eventBus().subscribe(new {listener_name}(runtime));")
        elif ctor_arg == "context":
            subs_lines.append(f"        context.eventBus().subscribe(new {listener_name}(context));")
        else:
            subs_lines.append(f"        context.eventBus().subscribe(new {listener_name}());")
    subs = "\n".join(subs_lines) + "\n"

    inserted = False
    m = re.search(r"(context\.eventBus\(\)\.subscribe\(PlacedClickListener\.[^)]+\)\);\n)", st)
    if m:
        st = st[: m.end()] + subs + st[m.end() :]
        inserted = True
    if not inserted:
        m = re.search(r"(public Strategy\(IgnisStrategyContext context\) \{\n        super\(context\);\n)", st)
        if m:
            st = st[: m.end()] + subs + st[m.end() :]
            inserted = True
    if not inserted:
        raise ValueError("Could not find insertion point in Strategy.java")

    strategy_path.write_text(st)
    path.unlink()
    return True


def main() -> int:
    count = 0
    failures: list[str] = []
    for path in sorted(ROOT.glob("extensions/blocks/**/*Listeners.java")):
        try:
            if split_listeners(path):
                count += 1
                print(f"OK {path.relative_to(ROOT)}")
        except Exception as ex:
            failures.append(f"{path.relative_to(ROOT)}: {ex}")
            print(f"FAIL {path.relative_to(ROOT)}: {ex}", file=sys.stderr)
    print(f"\nSplit {count} extensions")
    if failures:
        print(f"Failures ({len(failures)}):", file=sys.stderr)
        for failure in failures:
            print(f"  {failure}", file=sys.stderr)
    return 1 if failures else 0


if __name__ == "__main__":
    raise SystemExit(main())
