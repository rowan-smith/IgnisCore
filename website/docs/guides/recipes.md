---
title: Common Setups
description: Install IgnisCore, deploy extensions, and configure resource packs.
slug: /guides/recipes
---

## Fresh server install

1. Install **Java 25** and your server software (Spigot, Paper, or SpongeVanilla)
2. On Bukkit-family servers, install **NBTAPI**
3. Download `%%site.jarName%%` from [Releases](https://github.com/%%site.repo%%/releases)
4. Place the jar in `plugins/` and start the server
5. Verify with `/ignis` — bundled extensions extract automatically

## Deploy a custom extension

1. Build your extension JAR (`mvn package`)
2. Copy to `plugins/IgnisCore/blocks/` or `items/`
3. Run `/ignis reload blocks` or `/ignis reload items`
4. Give yourself the type: `/ignis give <player> block <id>`

## Production resource pack URL

Set `public-url` to an address clients can reach:

```yaml
resource-pack:
  host: "0.0.0.0"
  port: 8080
  public-url: "https://play.example.com/resourcepack.zip"
```

Use a reverse proxy for HTTPS. After changing config:

```text
/ignis reload server
/ignis reload all
```

## Example server config {#example-server-config}

See [examples/server-config.yml](https://github.com/%%site.repo%%/blob/main/examples/server-config.yml) in the repository.

## Extension development workflow

1. Copy an existing module from [IgnisCore-Extensions](https://github.com/%%site.extensionsRepo%%) under `blocks/` or `items/`
2. Rename packages and IDs
3. Run `mvn package` from the repo root
4. Test with MockBukkit unit tests, then deploy to a dev server

See the [Extension Cookbook](/developers/cookbook) for code recipes.

## Related

- [Requirements](/requirements) — Java and dependencies
- [Configuration](/configuration) — all config keys
- [Extensions](/concepts/extensions) — manifest format
