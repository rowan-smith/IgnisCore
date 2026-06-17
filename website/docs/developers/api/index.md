---
title: API Overview
description: IgnisCore public API surfaces and which library to use.
slug: /developers/api
---

## API surfaces

| Surface | Maven artifact | Package | Status |
|---------|----------------|---------|--------|
| **Core API** | `dev.rono:api` | `dev.rono.igniscore.api.*` | Stable, fully Javadoc'd |
| **Extension shared** | `dev.rono.extensions:shared` | `dev.rono.extensions.shared.*` | Internal — bundled extensions only |

## Start here

| Doc | Content |
|-----|---------|
| [API layers](/developers/api/layers) | L1–L4 model — extension vs integrator entry points |
| [Core API](/developers/api/core-api) | Package map and `IgnisStrategyContext` |
| [Extension profiles](/developers/extension-profiles) | Manifest `profiles` and `requires-integrations` |
| [Extension config](/developers/extension-config) | `config.yml` sections and keys |
| [Javadoc](/developers/reference) | Every public class and method |

## Which API should I use?

| Task | Use |
|------|-----|
| Build a block or item extension | **L2–L3** — strategies + `IgnisStrategyContext` |
| Read extension tuning from YAML | **Core API** — `definition.getCustomData()` + `StrategySupport` or `ExtensionConfig` |
| Access runtime from another plugin | **L4** — `IgnisCoreAPI` facade |

Third-party extension authors should not depend on `extensions/shared`. See [Extension shared (internal)](/developers/api/extension-shared).

## Sample extensions

| Module | GitHub |
|--------|--------|
| nuke (block) | [extensions/blocks/nuke](https://github.com/%%site.repo%%/tree/main/extensions/blocks/nuke) |
| grenade (item) | [extensions/items/grenade](https://github.com/%%site.repo%%/tree/main/extensions/items/grenade) |
| quarry-cache (block) | [extensions/blocks/quarry-cache](https://github.com/%%site.repo%%/tree/main/extensions/blocks/quarry-cache) |
| lamp-dimmer (item) | [extensions/items/lamp-dimmer](https://github.com/%%site.repo%%/tree/main/extensions/items/lamp-dimmer) |

## Related docs

- [Extension Cookbook](/developers/cookbook) — practical recipes
- [Architecture](/developers/architecture) — module boundaries
