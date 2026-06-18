---
title: Storage & Persistence
description: Extension deploy paths, placed-block index, and data folder layout.
slug: /storage
---

## Plugin data folder

After first boot, IgnisCore creates a data folder (typically `plugins/IgnisCore/` on Bukkit, platform-specific on Sponge).

| Path | Purpose |
|------|---------|
| `blocks/` | Drop-in block extension JARs |
| `items/` | Drop-in item extension JARs |
| `placed-blocks.json` | Index of placed custom blocks in the world |
| `resourcepack/` | Generated resource pack builds |
| `config.yml` | Server configuration — see [Configuration](/configuration) |

## Extension JARs

| Kind | Deploy path | Reload |
|------|-------------|--------|
| Block | `blocks/` | `/ignis reload blocks` or `all` |
| Item | `items/` | `/ignis reload items` or `all` |

Bundled extensions are not shipped with the bootstrap JAR. Copy built extension JARs from [IgnisCore-Extensions](https://github.com/%%site.extensionsRepo%%) into these folders, then reload.

## Placed blocks

Placed custom blocks are tracked in **`placed-blocks.json`**.

- Legacy `placed-blocks.yml` files are migrated automatically on first load
- After migration, the YAML file is renamed to `placed-blocks.yml.migrated`

Breaking or replacing a placed block removes its entry from the index.

## Resource pack artifacts

Generated packs and build history live under `resourcepack/`. The number of retained builds is controlled by `performance.resource-pack-retain-count` in [config.yml](/configuration).

## Related

- [Extensions](/concepts/extensions) — manifest and config layout
- [Block lifecycle](/concepts/blocks) — placed vs active phases
- [Troubleshooting](/guides/troubleshooting) — common data-folder issues
