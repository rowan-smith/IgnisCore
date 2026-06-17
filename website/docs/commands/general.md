---
title: General Commands
description: /ignis admin commands for giving items, reloading, and debugging.
slug: /commands/general
---

All commands require `igniscore.admin`.

---

## `/ignis`

**Syntax:** `/ignis`

Shows the help menu listing available subcommands.

---

## `/ignis give <player> <block|item> <id>`

**Syntax:** `/ignis give <player> <block|item> <id>`

Gives a custom block item or throwable item to the target player.

```text
/ignis give Steve block nuke
/ignis give Alex item grenade
```

---

## `/ignis pack`

**Syntax:** `/ignis pack`

Rebuilds the resource pack and applies it to the executing player. Player-only.

---

## `/ignis reload <all|blocks|items|server>`

**Syntax:** `/ignis reload <all|blocks|items|server>`

Reloads extensions or server configuration.

| Target | Effect |
|--------|--------|
| `all` | Reload block + item extensions, then rebuild resource pack |
| `blocks` | Reload block extensions only |
| `items` | Reload item extensions only |
| `server` | Reload `config.yml` only (no extension reload) |

---

## `/ignis blocks`

**Syntax:** `/ignis blocks`

Lists all loaded block extension JARs with manifest name, version, and config id.

---

## `/ignis items`

**Syntax:** `/ignis items`

Lists all loaded item extension JARs with manifest name, version, and config id.

---

## `/ignis debug <on|off|pack>`

**Syntax:** `/ignis debug <on|off|pack>`

Toggle debug logging or show resource pack diagnostics.

| Subcommand | Effect |
|------------|--------|
| `on` | Enable debug logging |
| `off` | Disable debug logging |
| `pack` | Show resource pack debug info (hash, block mappings, public URL) |

---
