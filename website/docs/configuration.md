---
title: Configuration
description: IgnisCore server config.yml — resource pack hosting and performance settings.
slug: /configuration
---

IgnisCore reads server configuration from the plugin data folder:

- **Spigot / Sponge:** `config.yml`
- **Paper:** `paper-config.yml`

Reload server config without reloading extensions:

```text
/ignis reload server
```

## Default config

```yaml
resource-pack:
  host: "0.0.0.0"
  port: 8080
  public-url: "http://0.0.0.0:8080/resourcepack.zip"

performance:
  chunk-restore-blocks-per-tick: 16
  visual-refresh-blocks-per-tick: 32
  resource-pack-retain-count: 3
```

## `resource-pack`

| Key | Purpose |
|-----|---------|
| `host` | Bind address for the embedded HTTP server that serves the pack (`0.0.0.0` listens on all interfaces) |
| `port` | TCP port for the pack server |
| `public-url` | URL sent to clients — must be reachable from player machines |

Set `public-url` to your server's public address (or a reverse proxy) so clients can download the pack. The embedded server binds to `host`; `public-url` is what players receive and may point at a reverse proxy or public IP. Use `/ignis pack` in-game to rebuild and apply after extension changes.

## `performance`

| Key | Purpose |
|-----|---------|
| `chunk-restore-blocks-per-tick` | Rate limit for restoring barrier blocks after explosions |
| `visual-refresh-blocks-per-tick` | Rate limit for block visual updates |
| `resource-pack-retain-count` | Number of historical pack builds to keep on disk |

## Extension config

Block and item extensions ship their own `config.yml` inside each extension JAR. These are separate from the server configuration files above.

See [Extension config](/developers/extension-config) for which sections and keys belong in extension configs, and the [Extension Cookbook](/developers/cookbook) for how strategies read them.

## Related

- [Resource packs](/concepts/resource-pack) — how packs are built and applied
- [Storage](/storage) — placed-block persistence
- [Example config](/guides/recipes#example-server-config) — production-oriented sample
