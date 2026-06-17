---
title: API Overview
description: IgnisCore public API surfaces and which library to use.
slug: /developers/api
---

## API surfaces

| Surface | Maven artifact | Package | Status |
|---------|----------------|---------|--------|
| **Core API** | `dev.rono:api` | `dev.rono.igniscore.api.*` | Stable, fully Javadoc'd |
| **Extension shared** | `dev.rono.extensions:shared` | `dev.rono.extensions.shared.*` | Optional helpers for bundled extensions |

## Start here

| Doc | Content |
|-----|---------|
| [API layers](/developers/api/layers) | L1–L4 model — extension vs integrator entry points |
| [Core API](/developers/api/core-api) | Package map and `IgnisStrategyContext` |
| [Extension profiles](/developers/extension-profiles) | Manifest `profiles` and `requires-integrations` |
| [Javadoc](/developers/reference) | Every public class and method |

## Which API should I use?

| Task | Use |
|------|-----|
| Build a block or item extension | **L2–L3** — strategies + `IgnisStrategyContext` |
| Parse explosion / throwable config | **Extension shared** (optional) or `ExtensionConfig` in core API |
| Access runtime from another plugin | **L4** — `IgnisCoreAPI` facade |

## Sample extensions

| Module | GitHub |
|--------|--------|
| nuke (block) | [extensions/blocks/nuke](https://github.com/%%site.repo%%/tree/main/extensions/blocks/nuke) |
| grenade (item) | [extensions/items/grenade](https://github.com/%%site.repo%%/tree/main/extensions/items/grenade) |
| quarry-cache (block) | [extensions/blocks/quarry-cache](https://github.com/%%site.repo%%/tree/main/extensions/blocks/quarry-cache) |

## Related docs

- [Extension Cookbook](/developers/cookbook) — practical recipes
- [Architecture](/developers/architecture) — module boundaries
