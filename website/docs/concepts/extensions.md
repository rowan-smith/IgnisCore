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
profiles:
  - fuse
requires-integrations:
  - protocol   # optional — only if you need ProtocolLib
```

See [Extension profiles](/developers/extension-profiles) for all profile and integration tokens.

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
| [nuke](https://github.com/%%site.repo%%/tree/main/extensions/blocks/nuke) | Explosive fuse + documented `custom_data` |
| [quarry-cache](https://github.com/%%site.repo%%/tree/main/extensions/blocks/quarry-cache) | Non-combustible GUI block |
| [auto-sieve](https://github.com/%%site.repo%%/tree/main/extensions/blocks/auto-sieve) | Placed tick block, no `custom_data` |
| [socket-lamp](https://github.com/%%site.repo%%/tree/main/extensions/blocks/socket-lamp) | Linked remote-activation block |
| [picnic-basket](https://github.com/%%site.repo%%/tree/main/extensions/blocks/picnic-basket) | Interact / storage GUI |

| Item | Pattern |
|------|---------|
| [grenade](https://github.com/%%site.repo%%/tree/main/extensions/items/grenade) | Throwable explosive |
| [lamp-dimmer](https://github.com/%%site.repo%%/tree/main/extensions/items/lamp-dimmer) | Link item with NBT |
| [miners-lunch](https://github.com/%%site.repo%%/tree/main/extensions/items/miners-lunch) | Consumable + cooldown NBT |
| [detonator](https://github.com/%%site.repo%%/tree/main/extensions/items/detonator) | Multi-click item |

## Related

- [Strategies](/concepts/strategies) — Java strategy contract
- [Extension profiles](/developers/extension-profiles) — manifest profiles and integrations
- [Extension config](/developers/extension-config) — `config.yml` sections and keys
- [API layers](/developers/api/layers) — extension vs integrator surfaces
- [Extension Cookbook](/developers/cookbook) — build your own
- [Storage](/storage) — deploy paths and reload
