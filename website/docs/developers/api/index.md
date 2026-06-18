---
title: API Overview
description: IgnisCore public API surfaces and which library to use.
slug: /developers/api
---

## API surfaces

| Surface | Maven artifact | Package | Status |
|---------|----------------|---------|--------|
| **Core API** | `dev.rono:api` | `dev.rono.igniscore.api.*` | Stable, fully Javadoc'd |
| **Extension shared** | `dev.rono.extensions:shared` | `dev.rono.extensions.shared.*` | Optional — `ExtensionShared` facade |

## Start here

| Doc | Content |
|-----|---------|
| [API layers](/developers/api/layers) | L1–L4 model — extension vs integrator entry points |
| [Core API](/developers/api/core-api) | Package map, `IgnisStrategies`, `IgnisStrategyContext` |
| [Extension shared](/developers/api/extension-shared) | Optional helpers via `ExtensionShared.*()` |
| [Extension profiles](/developers/extension-profiles) | Manifest `profiles` and `requires-integrations` |
| [Extension config](/developers/extension-config) | `config.yml` sections and keys |
| [Javadoc](/developers/reference) | Every public class and method |

## Which API should I use?

| Task | Use |
|------|-----|
| Build a block or item extension | **L2–L3** — strategies + `IgnisStrategyContext` |
| Block profiles, click routing, YAML reads | **Core API** — `IgnisStrategies.blocks()`, `.items()`, `.data()` |
| Read extension tuning from YAML | **Core API** — `IgnisStrategies.data()` or `StrategySupport` |
| Explosions, throwables, link items, GUIs | **Optional** — `ExtensionShared.*()` ([details](/developers/api/extension-shared)) |
| Access runtime from another plugin | **L4** — `IgnisCoreAPI` facade |

## Sample extensions

| Module | GitHub |
|--------|--------|
| nuke (block) | [extensions/blocks/nuke](https://github.com/%%site.extensionsRepo%%/tree/main/blocks/nuke) |
| grenade (item) | [extensions/items/grenade](https://github.com/%%site.extensionsRepo%%/tree/main/items/grenade) |
| quarry-cache (block) | [extensions/blocks/quarry-cache](https://github.com/%%site.extensionsRepo%%/tree/main/blocks/quarry-cache) |
| lamp-dimmer (item) | [extensions/items/lamp-dimmer](https://github.com/%%site.extensionsRepo%%/tree/main/items/lamp-dimmer) |

## Related docs

- [Extension Cookbook](/developers/cookbook) — practical recipes
- [Architecture](/developers/architecture) — module boundaries
