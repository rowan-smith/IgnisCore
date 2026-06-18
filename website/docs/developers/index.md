---
title: Overview
description: Build IgnisCore block and item extensions.
slug: /developers
---

IgnisCore extensions are platform-agnostic JARs loaded at runtime. This section covers building, testing, and deploying them.

## Quick start

1. Clone [IgnisCore-Extensions](https://github.com/%%site.extensionsRepo%%) or copy an existing module under `blocks/` or `items/`
2. Add Maven dependency on `api` (`provided` scope)
3. Implement a strategy class extending `AbstractIgnisBlockStrategy` or `AbstractIgnisItemStrategy`, subscribe to lifecycle events in the constructor via `context.eventBus().subscribe(...)`, and declare behavior in `config.yml`
4. Ship `block-extension.yml` / `item-extension.yml`, `config.yml`, and textures
5. Build with `mvn package` and deploy to the plugin data folder

```xml
<dependency>
  <groupId>dev.rono</groupId>
  <artifactId>api</artifactId>
  <version>%%site.version%%</version>
  <scope>provided</scope>
</dependency>
```

## Documentation layers

| Layer | What it covers |
|-------|----------------|
| **[API layers](/developers/api/layers)** | L1–L4 model — extension vs integrator surfaces |
| **[Extension profiles](/developers/extension-profiles)** | Manifest profiles and integration requirements |
| **[Extension config](/developers/extension-config/)** | What belongs in `config.yml` |
| **[Extension Cookbook](/developers/cookbook/)** | Practical recipes — explosions, throwables, link items, consumables |
| **[API Reference](/developers/api/)** | Core API package map and entry points |
| **[Architecture](/developers/architecture/)** | Module stack, boot flow, dependency rules |
| **[Javadoc](/developers/reference/)** | Full class and method reference (100% public API coverage) |

## Libraries

| Library | Maven artifact | When |
|---------|----------------|------|
| **Core API** | `dev.rono:api` | Always — strategies, ports, models, `IgnisStrategies` |
| **Extension shared** | `dev.rono.extensions:shared` | Optional — explosions, throwables, link items via `ExtensionShared` |

The bootstrap plugin supplies the API at runtime (`provided` scope). See [Extension shared](/developers/api/extension-shared) for optional helpers.

## Next steps

- [Extension Cookbook](/developers/cookbook/) — copy-paste recipes (core API)
- [Extension config](/developers/extension-config/) — `config.yml` reference
- [API Reference](/developers/api/) — surface overview
- [Architecture](/developers/architecture/) — module layout
- [Javadoc](/developers/reference/) — browse all classes
- [Contributing](/developers/contributing/) — build from source
