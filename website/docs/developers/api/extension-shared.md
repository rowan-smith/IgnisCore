---
title: Extension Shared (internal)
description: Optional helpers used by bundled extensions — not part of the public authoring contract.
slug: /developers/api/extension-shared
---

Bundled extensions in this repository may depend on `dev.rono.extensions:shared` for shared behavior helpers (explosion utilities, GUI registries, link-item support, and similar). **Extension authors should use the core API only** — see the [Extension Cookbook](/developers/cookbook) for raw patterns.

## For extension authors

| Task | Use instead |
|------|-------------|
| Read YAML tuning | `StrategySupport` + `definition.getCustomData()` |
| Explosion on detonate | `IgnisWorld.createExplosion(...)` |
| Item NBT | `IgnisNbtService` via `IgnisStrategyContext` |
| Repeating placed-block ticks | `IgnisScheduler` via `IgnisStrategyContext` |

See [Extension config](/developers/extension-config) for which `config.yml` keys to declare.

## Bundled module

The `extensions/shared` Maven module exists for code reuse across first-party extensions shipped in the bootstrap JAR. It is not a supported public SDK surface for third-party extensions.

## Related

- [Extension Cookbook](/developers/cookbook) — core API recipes
- [Core API](/developers/api/core-api) — required dependency
- [Architecture](/developers/architecture) — module layout
