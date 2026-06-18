# IgnisCore

Multi-platform framework for custom Minecraft blocks, items, and runtime extensions — fuse explosives, utility blocks, link tools, consumables, throwables, and more.

**Documentation:** https://igniscore.rono.dev

## Quick start

1. Download the latest `IgnisCore-<version>.jar` from [GitHub Releases](https://github.com/rowan-smith/IgnisCore/releases)
2. Install [NBTAPI](https://www.spigotmc.org/resources/nbt-api.7939/) on Spigot/Paper
3. Place the jar in `plugins/` and restart
4. Run `/ignis` in-game

## Build

```bash
git submodule update --init --recursive
./scripts/build-all.sh
```

This builds bundled extensions from the `extensions/` submodule ([IgnisCore-Extensions](https://github.com/rowan-smith/IgnisCore-Extensions)), installs the API, and packages the plugin.

Deploy `bootstrap/target/IgnisCore-<version>.jar` (version matches `pom.xml`).

## Extension authoring

See the [Extension Cookbook](https://igniscore.rono.dev/developers/cookbook) on the documentation site.

## Documentation site (local)

```bash
cd website
npm install
npm start
```
