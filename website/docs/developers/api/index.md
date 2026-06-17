---
title: API Overview
description: IgnisCore public API surfaces and which library to use.
slug: /developers/api
---

## API surfaces

| Surface | Maven artifact | Package | Status |
|---------|----------------|---------|--------|
| **Core API** | `dev.rono:api` | `dev.rono.igniscore.api.*` | Stable |
| **Extension shared** | `dev.rono.extensions:shared` | `dev.rono.extensions.shared.*` | Stable helpers |

## Which API should I use?

| Task | Use |
|------|-----|
| Build a block or item extension | **Core API** — strategies, models, ports |
| Parse explosion / throwable config | **Extension shared** — `ExtensionConfigs` helpers |
| Access runtime from another plugin | **Core API** — `IgnisCoreAPI` facade |

## Sub-references

- [Core API](/developers/api/core-api) — strategies, ports, models, config
- [Extension shared](/developers/api/extension-shared) — typed config helpers

## Sample extensions

| Module | GitHub |
|--------|--------|
| nuke (block) | [extensions/blocks/nuke](https://github.com/%%site.repo%%/tree/main/extensions/blocks/nuke) |
| grenade (item) | [extensions/items/grenade](https://github.com/%%site.repo%%/tree/main/extensions/items/grenade) |
| quarry-cache (block) | [extensions/blocks/quarry-cache](https://github.com/%%site.repo%%/tree/main/extensions/blocks/quarry-cache) |

## Related docs

- [Extension Cookbook](/developers/cookbook) — practical recipes
- [Javadoc](/developers/reference) — full class reference
- [Architecture](/developers/architecture) — module boundaries
