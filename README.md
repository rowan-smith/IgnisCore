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
mvn clean package
```

Deploy `bootstrap/target/IgnisCore-<version>.jar` (version matches `pom.xml`).

### Bundling extension JARs

Extension modules are built separately in [IgnisCore-Extensions](https://github.com/rowan-smith/IgnisCore-Extensions). Copy built JARs into this repo before packaging:

```
bootstrap/bundled/blocks/*.jar
bootstrap/bundled/items/*.jar
bootstrap/bundled/lib/shared.jar   # optional — shades ExtensionShared into the plugin
```

Then run `mvn package` as usual.

## Extension authoring

See the [Extension Cookbook](https://igniscore.rono.dev/developers/cookbook) on the documentation site. Extension projects consume the IgnisCore API via [JitPack](https://jitpack.io/#rowan-smith/IgnisCore).

## Documentation site (local)

```bash
cd website
npm install
npm start
```
