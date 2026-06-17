---
title: Extensions
description: How IgnisCore loads block and item extension JARs at runtime.
slug: /concepts/extensions
---

Extensions are platform-agnostic JARs loaded at runtime from the plugin data folder. Each extension registers a **strategy** class and ships YAML config plus texture assets.

## Deploy paths

| Kind | Folder | Example |
|------|--------|---------|
| Block | `blocks/` | `blocks/my-tnt-1.0.0.jar` |
| Item | `items/` | `items/my-grenade-1.0.0.jar` |

Bundled extensions ship inside the bootstrap JAR and extract on first boot.

## Two identifiers

| Field | Source | Purpose |
|-------|--------|---------|
| **Manifest `id`** | `*-extension.yml` | Registers the strategy in `IgnisStrategyRegistry` |
| **Config `id`** | `config.yml` | In-game type id (`/ignis give`, NBT type key) |

These are usually the same (e.g. `nuke`). Hyphens in ids become package segments without hyphens (`quarry-cache` → `quarrycache`).

## Manifest

Block extensions use `block-extension.yml`; items use `item-extension.yml`:

```yaml
id: my-tnt
name: My TNT
version: 1.0.0
api-version: 1.0.0
author: YourName
strategy: dev.rono.igniscore.block.mytnt.Strategy
```

## Project layout

```
my-extension/
├── pom.xml
├── src/main/java/.../Strategy.java
├── src/main/resources/
│   ├── block-extension.yml    # or item-extension.yml
│   ├── config.yml
│   └── textures/
└── src/test/java/...
```

## API version

`api-version` declares the **Ignis API** your extension was built against — see [API versioning](/faq/api-version).

## Reference extensions

| Block | Pattern |
|-------|---------|
| [nuke](https://github.com/%%site.repo%%/tree/main/extensions/blocks/nuke) | Explosive + fuse + `ExplosionConfig` |
| [quarry-cache](https://github.com/%%site.repo%%/tree/main/extensions/blocks/quarry-cache) | Non-combustible GUI block |
| [tunneling-tnt](https://github.com/%%site.repo%%/tree/main/extensions/blocks/tunneling-tnt) | Minimal `onTrigger`-only strategy |

| Item | Pattern |
|------|---------|
| [grenade](https://github.com/%%site.repo%%/tree/main/extensions/items/grenade) | Throwable + `ThrowableItemConfig` |
| [detonator](https://github.com/%%site.repo%%/tree/main/extensions/items/detonator) | Multi-click item |

## Related

- [Strategies](/concepts/strategies) — Java strategy contract
- [Extension Cookbook](/developers/cookbook) — build your own
- [Storage](/storage) — deploy paths and reload
