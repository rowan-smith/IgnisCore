# IgnisCore Extension Authoring

Extension authoring documentation has moved to the documentation site:

- **[Developer overview](https://igniscore.rono.dev/developers)** — quick start and API layers
- **[Extension Cookbook](https://igniscore.rono.dev/developers/cookbook)** — copy-paste recipes (core API)
- **[Extension config](https://igniscore.rono.dev/developers/extension-config)** — `config.yml` reference
- **[API Reference](https://igniscore.rono.dev/developers/api)** — core API
- **[Concepts: Extensions](https://igniscore.rono.dev/concepts/extensions)** — manifest, deploy paths, identifiers

Bundled sample extensions live in the [IgnisCore-Extensions](https://github.com/rowan-smith/IgnisCore-Extensions) repository (`blocks/` and `items/`), included here as the `extensions/` git submodule.

When building IgnisCore from source, initialize submodules first:

```bash
git submodule update --init --recursive
./scripts/build-all.sh
```
