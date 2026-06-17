---
title: API Overview
description: IgnisCore public API surfaces and which library to use.
slug: /developers/api
---

## API surfaces

| Surface | Maven artifact | Package | Status |
|---------|----------------|---------|--------|
| **Core API** | `dev.rono:api` | `dev.rono.igniscore.api.*` | Stable |

## Which API should I use?

| Task | Use |
|------|-----|
| Build a block or item extension | **Core API** — strategies, models, ports, `StrategySupport` |
| Read extension tuning from YAML | **Core API** — `definition.getCustomData()` + `StrategySupport` |
| Access runtime from another plugin | **Core API** — `IgnisCoreAPI` facade |

## Sub-references

- [Core API](/developers/api/core-api) — strategies, ports, models, config
- [Extension config](/developers/extension-config) — `config.yml` sections and keys

## Sample extensions

| Module | GitHub |
|--------|--------|
| nuke (block) | [extensions/blocks/nuke](https://github.com/%%site.repo%%/tree/main/extensions/blocks/nuke) |
| grenade (item) | [extensions/items/grenade](https://github.com/%%site.repo%%/tree/main/extensions/items/grenade) |
| quarry-cache (block) | [extensions/blocks/quarry-cache](https://github.com/%%site.repo%%/tree/main/extensions/blocks/quarry-cache) |
| lamp-dimmer (item) | [extensions/items/lamp-dimmer](https://github.com/%%site.repo%%/tree/main/extensions/items/lamp-dimmer) |

## Related docs

- [Extension Cookbook](/developers/cookbook) — practical recipes
- [Javadoc](/developers/reference) — full class reference
- [Architecture](/developers/architecture) — module boundaries
