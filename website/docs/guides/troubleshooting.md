---
title: Troubleshooting
description: Common IgnisCore issues and fixes.
slug: /guides/troubleshooting
---

## Extension not loading

| Symptom | Check |
|---------|-------|
| JAR not listed in `/ignis blocks` or `/ignis items` | File is in correct folder (`blocks/` or `items/`) |
| Load error in console | `api-version` matches runtime — see [API versioning](/faq/api-version) |
| Strategy class not found | Manifest `strategy` path matches actual class |

Run `/ignis reload all` after adding JARs.

## Resource pack not updating

1. Run `/ignis pack` or `/ignis reload all`
2. Check `public-url` in config — must be reachable from clients, not `localhost`
3. Run `/ignis debug pack` to verify hash and block mappings
4. Players may need to reconnect or re-accept the pack

## Unknown block/item type on give

The config `id` in `config.yml` must match the id used in `/ignis give`. Manifest `id` and config `id` are usually the same but serve different purposes — see [Extensions](/concepts/extensions).

## NBTAPI errors (Bukkit)

IgnisCore requires NBTAPI on Spigot and Paper. Install it as a separate plugin before IgnisCore.

## Placed blocks missing after restart

Placed blocks are stored in `placed-blocks.json`. If the file is corrupted, check server logs on startup. Legacy YAML files migrate automatically — see [Storage](/storage).

## Debug logging

```text
/ignis debug on
```

Disable when finished:

```text
/ignis debug off
```

## Get help

- [GitHub Issues](https://github.com/%%site.repo%%/issues)
- Include server software, Java version, and relevant console output
