# IgnisCore

Multi-platform framework for custom Minecraft blocks, items, and runtime extensions — fuse explosives, utility blocks, link tools, consumables, throwables, and more.

**Documentation:** https://igniscore.rono.dev

## Quick start

1. Download the latest `IgnisCore-<version>.jar` from [GitHub Releases](https://github.com/rowan-smith/IgnisCore/releases)
2. On Spigot/Paper, install [NBTAPI](https://www.spigotmc.org/resources/nbt-api.7939/)
3. Place the jar in `plugins/` and restart
4. Deploy extension JARs to `plugins/IgnisCore/blocks/` and `plugins/IgnisCore/items/`
5. Run `/ignis reload all` in-game

## Build

```bash
mvn clean package
```

Deploy `universal/target/IgnisCore-<version>.jar` for all platforms, or use a platform-specific build:

- Spigot: `spigot/target/IgnisCore-Spigot-<version>.jar`
- Paper: `paper/target/IgnisCore-Paper-<version>.jar`
- Sponge 8.5: `sponge-v8.5.0/target/IgnisCore-Sponge-v8-<version>.jar`

## Extension authoring

Sample and reference extensions live in [IgnisCore-Extensions](https://github.com/rowan-smith/IgnisCore-Extensions). See the [Extension Cookbook](https://igniscore.rono.dev/developers/cookbook) on the documentation site. Extension projects consume the IgnisCore API via [JitPack](https://jitpack.io/#rowan-smith/IgnisCore).

## Documentation site (local)

```bash
cd website
npm install
npm start
```
