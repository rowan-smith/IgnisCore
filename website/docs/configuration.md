---
title: Configuration
description: IgnisCore server config.yml — resource pack hosting and performance settings.
slug: /configuration
---

IgnisCore reads `config.yml` from the plugin data folder. Reload server config without reloading extensions:

```text
/ignis reload server
```

## Default config

```yaml
resource-pack:
  host: "localhost"
  port: 8080
  public-url: "http://localhost:8080/resourcepack.zip"

performance:
  chunk-restore-blocks-per-tick: 16
  visual-refresh-blocks-per-tick: 32
  resource-pack-retain-count: 3
```

## `resource-pack`

| Key | Purpose |
|-----|---------|
| `host` | Bind address for the embedded HTTP server that serves the pack |
| `port` | TCP port for the pack server |
| `public-url` | URL sent to clients — must be reachable from player machines |

Set `public-url` to your server's public address (or a reverse proxy) so clients can download the pack. Use `/ignis pack` in-game to rebuild and apply after extension changes.

## `performance`

| Key | Purpose |
|-----|---------|
| `chunk-restore-blocks-per-tick` | Rate limit for restoring barrier blocks after explosions |
| `visual-refresh-blocks-per-tick` | Rate limit for block visual updates |
| `resource-pack-retain-count` | Number of historical pack builds to keep on disk |

## Extension config

Block and item extensions ship their own `config.yml` inside each extension JAR. These are separate from the server `config.yml`.

See [Extension config](/developers/extension-config) for which sections and keys belong in extension configs, and the [Extension Cookbook](/developers/cookbook) for how strategies read them.

## Related

- [Resource packs](/concepts/resource-pack) — how packs are built and applied
- [Storage](/storage) — placed-block persistence
- [Example config](/guides/recipes#example-server-config) — production-oriented sample
