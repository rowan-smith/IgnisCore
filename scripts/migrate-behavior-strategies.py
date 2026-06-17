#!/usr/bin/env python3
"""Migrate legacy Strategy+Behavior blocks to event-bus listeners."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BLOCKS = ROOT / "extensions" / "blocks"

COMBUSTIBLE_CLICK = """\
package {package};

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.event.BlockClickEvent;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;

final class {prefix}OnBlockClickListener implements OnBlockClickListener {{
    @Override
    public void onBlockClick(BlockClickEvent event) {{
        if (event.interaction() == IgnisInteraction.LEFT_CLICK_BLOCK) {{
            event.setResult(CustomBlockAction.BREAK);
            return;
        }}
        if (event.interaction() != IgnisInteraction.RIGHT_CLICK_BLOCK) {{
            return;
        }}
        BlockBehaviorConfig behavior = BlockBehaviorConfig.from(event.block().definition().getBehaviorConfig());
        if (!behavior.combustible()) {{
            return;
        }}
        String material = materialKey(event.heldItem());
        for (String ignition : behavior.ignitionMaterials()) {{
            if (material.equalsIgnoreCase(ignition)) {{
                event.setResult(CustomBlockAction.IGNITE);
                return;
            }}
        }}
    }}

    private static String materialKey(IgnisItem heldItem) {{
        if (heldItem == null || heldItem.isAir()) {{
            return "AIR";
        }}
        String materialKey = heldItem.getMaterialKey();
        return materialKey == null || materialKey.isBlank() ? "AIR" : materialKey;
    }}
}}
"""

BREAK_CLICK = """\
package {package};

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockClickEvent;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.port.IgnisInteraction;

final class {prefix}OnBlockClickListener implements OnBlockClickListener {{
    @Override
    public void onBlockClick(BlockClickEvent event) {{
        if (event.interaction() == IgnisInteraction.LEFT_CLICK_BLOCK) {{
            event.setResult(CustomBlockAction.BREAK);
        }}
    }}
}}
"""

RUNTIME_TEMPLATE = """\
package {package};

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class {prefix}Runtime {{
    final IgnisStrategyContext context;
    final {behavior_class} behavior;

    {prefix}Runtime(IgnisStrategyContext context) {{
        this.context = context;
        this.behavior = new {behavior_class}(context);
    }}
}}
"""

EVENT_MAP = {
    "onPlaced": ("Place", "BlockPlaceEvent", "OnBlockPlaceListener", "onBlockPlace",
                 "        runtime.behavior.onPlaced(event.block().definition(), event.block().location());"),
    "onPlacedBreak": ("Break", "BlockBreakEvent", "OnBlockBreakListener", "onBlockBreak",
                      "        runtime.behavior.onPlacedBreak(event.block().location());"),
    "onPlacedInteract": ("Interact", "BlockInteractEvent", "OnBlockInteractListener", "onBlockInteract",
                         """        runtime.behavior.onPlacedInteract(
                event.block().definition(),
                event.block().location(),
                event.player(),
                event.interaction(),
                event.heldItem(),
                event.action());"""),
    "onActivate": ("Activate", "BlockActivateEvent", "OnBlockActivateListener", "onBlockActivate",
                   "        runtime.behavior.onActivate(event.instance());"),
    "onTick": ("Tick", "BlockTickEvent", "OnBlockTickListener", "onBlockTick",
               "        runtime.behavior.onTick(event.instance());"),
}


def behavior_prefix(behavior_class: str) -> str:
    if behavior_class.endswith("Behavior"):
        return behavior_class[: -len("Behavior")]
    return behavior_class


def trigger_body(behavior_text: str) -> str:
    if re.search(r"void onTrigger\(\s*RuntimeBlockInstance\s+\w+\s*,\s*Object", behavior_text):
        return "        runtime.behavior.onTrigger(event.instance(), event.triggerContext());"
    return "        runtime.behavior.onTrigger(event.instance());"


def combustible_from_profile(strategy_text: str) -> bool:
    return not re.search(r"\.combustible\(\s*false\s*\)", strategy_text)


def overridden_methods(strategy_text: str) -> list[str]:
    methods = [name for name in EVENT_MAP if re.search(rf"void {name}\(", strategy_text)]
    if re.search(r"void onTrigger\(", strategy_text):
        methods.append("onTrigger")
    return methods


def listener_source(package: str, prefix: str, suffix: str, event_type: str, iface: str,
                    method: str, body: str) -> str:
    return f"""package {package};

import dev.rono.igniscore.api.event.{event_type};
import dev.rono.igniscore.api.event.{iface};

final class {prefix}OnBlock{suffix}Listener implements {iface} {{
    private final {prefix}Runtime runtime;

    {prefix}OnBlock{suffix}Listener({prefix}Runtime runtime) {{
        this.runtime = runtime;
    }}

    @Override
    public void {method}({event_type} event) {{
{body}
    }}
}}
"""


def migrate_strategy(path: Path) -> bool:
    text = path.read_text(encoding="utf-8")
    if "StrategyProfile" not in text:
        return False

    package_match = re.search(r"^package (.+);", text, re.M)
    behavior_match = re.search(r"private final (\w+) behavior;", text)
    if not package_match or not behavior_match:
        print(f"skip {path}: missing package/behavior")
        return False

    package = package_match.group(1)
    behavior_class = behavior_match.group(1)
    prefix = behavior_prefix(behavior_class)
    module_dir = path.parent

    behavior_path = module_dir / f"{behavior_class}.java"
    behavior_text = behavior_path.read_text(encoding="utf-8") if behavior_path.exists() else ""

    methods = overridden_methods(text)
    combustible = combustible_from_profile(text)

    click_template = COMBUSTIBLE_CLICK if combustible else BREAK_CLICK
    (module_dir / f"{prefix}OnBlockClickListener.java").write_text(
        click_template.format(package=package, prefix=prefix),
        encoding="utf-8",
    )

    (module_dir / f"{prefix}Runtime.java").write_text(
        RUNTIME_TEMPLATE.format(package=package, prefix=prefix, behavior_class=behavior_class),
        encoding="utf-8",
    )

    subscriptions = [f"        context.eventBus().subscribe(new {prefix}OnBlockClickListener());"]
    subscriptions.append(f"        {prefix}Runtime runtime = new {prefix}Runtime(context);")

    for method in methods:
        if method == "onTrigger":
            body = trigger_body(behavior_text)
            content = listener_source(
                package, prefix, "Trigger", "BlockTriggerEvent", "OnBlockTriggerListener",
                "onBlockTrigger", body,
            )
            (module_dir / f"{prefix}OnBlockTriggerListener.java").write_text(content, encoding="utf-8")
            subscriptions.append(
                f"        context.eventBus().subscribe(new {prefix}OnBlockTriggerListener(runtime));"
            )
            continue

        suffix, event_type, iface, handler, body = EVENT_MAP[method]
        content = listener_source(package, prefix, suffix, event_type, iface, handler, body)
        (module_dir / f"{prefix}OnBlock{suffix}Listener.java").write_text(content, encoding="utf-8")
        subscriptions.append(
            f"        context.eventBus().subscribe(new {prefix}OnBlock{suffix}Listener(runtime));"
        )

    # runtime must be created before listener subscriptions that use it
    subs_without_runtime = [s for s in subscriptions if "Runtime runtime" not in s]
    runtime_line = next(s for s in subscriptions if "Runtime runtime" in s)
    ordered = [runtime_line] + subs_without_runtime

    strategy = f"""package {package};

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {{

    public Strategy(IgnisStrategyContext context) {{
        super(context);
{chr(10).join(ordered)}
    }}

}}
"""
    path.write_text(strategy, encoding="utf-8")
    print(f"migrated {path.parent.name}")
    return True


def main() -> None:
    count = 0
    for strategy in sorted(BLOCKS.glob("*/src/main/java/**/Strategy.java")):
        if migrate_strategy(strategy):
            count += 1
    print(f"done: {count} strategies migrated")


if __name__ == "__main__":
    main()
