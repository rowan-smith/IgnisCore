---
title: Resource Packs
description: How IgnisCore builds and serves client resource packs.
slug: /concepts/resource-pack
---

IgnisCore auto-generates a resource pack from loaded extension textures and custom model data assignments.

## How it works

1. Extensions ship PNG textures in their JARs
2. On load or reload, IgnisCore merges textures into a pack
3. An embedded HTTP server serves the pack at the configured [public URL](/configuration#resource-pack)
4. Clients receive the pack URL on join (or via `/ignis pack`)

## Rebuild and apply

Players can trigger a rebuild in-game:

```text
/ignis pack
```

Admins can reload everything (extensions + pack):

```text
/ignis reload all
```

## Debug

```text
/ignis debug pack
```

Shows latest pack hash, registered block model mappings, and the configured public URL.

## Configuration

| Key | Purpose |
|-----|---------|
| `resource-pack.host` | Bind address for pack server (default `0.0.0.0` — all interfaces) |
| `resource-pack.port` | TCP port |
| `resource-pack.public-url` | URL sent to clients |

Ensure `public-url` is reachable from player machines — use your public IP or reverse proxy. The default `public-url` uses `0.0.0.0` as a placeholder; replace it with an address clients can reach.

## Related

- [Configuration](/configuration) — server config reference
- [Troubleshooting](/guides/troubleshooting) — pack not updating
- [Commands](/commands/general) — `/ignis pack` and `/ignis debug pack`
