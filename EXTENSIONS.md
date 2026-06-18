# IgnisCore Extension Authoring

Extension authoring documentation has moved to the documentation site:

- **[Developer overview](https://igniscore.rono.dev/developers)** — quick start and API layers
- **[Extension Cookbook](https://igniscore.rono.dev/developers/cookbook)** — copy-paste recipes (core API)
- **[Extension config](https://igniscore.rono.dev/developers/extension-config)** — `config.yml` reference
- **[API Reference](https://igniscore.rono.dev/developers/api)** — core API
- **[Concepts: Extensions](https://igniscore.rono.dev/concepts/extensions)** — manifest, deploy paths, identifiers

Bundled sample extensions are maintained in the separate [IgnisCore-Extensions](https://github.com/rowan-smith/IgnisCore-Extensions) repository. That project consumes this API via [JitPack](https://jitpack.io/#rowan-smith/IgnisCore).

To ship bundled extensions inside the plugin JAR, copy built extension JARs into `bootstrap/bundled/blocks/` and `bootstrap/bundled/items/` before running `mvn package`.
