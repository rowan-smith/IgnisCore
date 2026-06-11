# IgnisCore

IgnisCore is a Spigot-compatible plugin for custom explosive blocks and throwable items. It targets Spigot 1.21+ and automatically enables Paper-specific hooks when running on Paper. Behavior is driven by YAML configuration and Java strategy classes shipped as extension JARs.

## Module layout

```
igniscore-parent/
├── api/              Public contracts, models, config parsing, strategy API
├── blocks/           Block extension modules (one JAR per block type)
├── items/            Item extension modules (one JAR per item type)
├── platform/         Server platform hooks (Spigot default, Paper enhancements)
├── plugin/           Core runtime (loaders, listeners, services)
└── dist/             Assembles the deployable plugin JAR
```

| Module | Purpose |
|--------|---------|
| `api` | Stable API for extension authors: `IgnisCoreAPI`, models, `DefinitionParser`, `IgnisBlockStrategy` / `IgnisItemStrategy`, protocol/effect services |
| `blocks/*` | Individual block extensions depending only on `api` |
| `items/*` | Individual item extensions depending only on `api` |
| `platform` | Hookable platform layer: Spigot implementations with optional Paper enhancements |
| `plugin` | Runtime that loads extensions, renders blocks, handles events |
| `dist` | Unpacks the plugin, bundles extension JARs, produces `igniscore.jar` |

## Build output

From the repository root:

```bash
mvn clean package
```

Deploy this file to your server:

```
dist/target/igniscore.jar
```

On first run, bundled extensions are extracted to:

```
plugins/IgnisCore/blocks/*.jar
plugins/IgnisCore/items/*.jar
```

Drop additional extension JARs into those folders and run `/ignis reload blocks`, `/ignis reload items`, or `/ignis reload all`.

## Public API

Third-party plugins integrate through the `api` module:

```java
IgnisCoreAPI.init(facade); // done by IgnisCore on enable

Map<String, BlockDefinition> blocks = IgnisCoreAPI.getBlockTypes();
ItemStack grenade = IgnisCoreAPI.createItem("grenade");
IgnisCoreAPI.getProtocolService().sendFakeExplosion(location, 4.0f, players);
IgnisCoreAPI.getEffectService().playSound(location, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
```

Strategy classes receive an `IgnisStrategyContext` with plugin, NBT, protocol, and effect services.

### Package map (`api`)

| Package | Contents |
|---------|----------|
| `dev.rono.igniscore.api` | `IgnisCoreAPI`, `IgnisCoreFacade`, `IgnisApiVersion` |
| `dev.rono.igniscore.api.model` | `BlockDefinition`, `ItemDefinition`, `RuntimeBlockInstance` |
| `dev.rono.igniscore.api.config` | `DefinitionParser` |
| `dev.rono.igniscore.api.strategy` | `IgnisBlockStrategy`, `IgnisItemStrategy`, registry, profiles |
| `dev.rono.igniscore.api.service` | `IgnisNbtService`, `IgnisProtocolService`, `IgnisEffectService` |
| `dev.rono.igniscore.api.extension` | `ExtensionManifest`, `ExtensionResources` |

## Extension authoring

Extensions are self-contained JARs with a manifest, config, strategy class, and optional textures.

### Block extension

**JAR contents:**

```
block-extension.yml    Extension manifest
config.yml             Block definition
icon.png               Optional resource-pack texture
dev/rono/blocks/.../Strategy.class
```

**`block-extension.yml`:**

```yaml
id: my-block
name: My Block
version: 1.0.0
api-version: 1.0.0
author: YourName
strategy: dev.rono.blocks.myblock.Strategy
```

**`config.yml` (excerpt):**

```yaml
id: myblock

display:
  title: "&aMy Block"

block:
  placeable: true
  breakable: true
  base_material: paper

behavior:
  strategy: myblock
  strategy_name: My Block Behavior   # optional
  strategy_version: 1.0.0            # optional
  strategy_author: YourName          # optional
  fuse: 80
  radius: 6.0
```

**Strategy class:**

```java
public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        // block detonation logic
    }
}
```

Implement `IgnisBlockStrategy` (or extend `AbstractIgnisBlockStrategy`) for block lifecycle hooks: `onStaticPlace`, `onPlace`, `onTick`, `onTrigger`, and `profile`.

### Item extension

**JAR contents:**

```
item-extension.yml
config.yml
icon.png
dev/rono/items/.../Strategy.class
```

**`item-extension.yml`:**

```yaml
id: my-item
name: My Item
version: 1.0.0
api-version: 1.0.0
author: YourName
strategy: dev.rono.items.myitem.Strategy
```

**`config.yml` (excerpt):**

```yaml
id: myitem

display:
  title: "&cMy Item"

item:
  base_material: snowball

behavior:
  strategy: myitem
  custom_data:
    power: 4.0
```

**Strategy class:**

```java
public class Strategy extends AbstractIgnisItemStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public void onItemUse(Player player, ItemDefinition definition, ItemStack item, Action action) {
        // right-click behavior
    }
}
```

Implement `IgnisItemStrategy` (or extend `AbstractIgnisItemStrategy`) for item-only hooks.

### Strategy metadata

Strategy identity is declared in `config.yml` under `behavior`:

| Key | Required | Description |
|-----|----------|-------------|
| `strategy` | yes | Unique strategy id used at runtime |
| `strategy_name` | no | Display name (defaults to extension manifest name) |
| `strategy_version` | no | Defaults to `1.0.0` |
| `strategy_author` | no | Defaults to `IgnisCore` |

The loader validates that block extensions register an `IgnisBlockStrategy` and item extensions register an `IgnisItemStrategy`.

### Maven dependency

Extension modules should depend only on the `api` artifact:

```xml
<dependency>
  <groupId>dev.rono</groupId>
  <artifactId>api</artifactId>
  <version>1.0.0</version>
  <scope>provided</scope>
</dependency>
```

### Custom model data

Block icons use custom model data starting at `10001`; items start at `20001`. The dist build assigns sequential values when extensions load.

## Commands

| Command | Description |
|---------|-------------|
| `/ignis reload blocks` | Reload block extensions |
| `/ignis reload items` | Reload item extensions |
| `/ignis reload all` | Reload everything and rebuild the resource pack |

## Platform support

IgnisCore compiles against **Spigot API** and runs on Spigot 1.21+ servers. When started on Paper, it automatically loads Paper-specific hooks for:

- Custom model data via the Paper Data Component API
- Adventure-based item metadata and messaging
- Resource pack prompts
- Block replaceability checks
- Registry sound key lookups

The hook layer lives in the `platform` module:

| Module | Role |
|--------|------|
| `platform-api` | `PlatformHooks` interface and runtime loader |
| `platform-spigot` | Default Spigot implementations (bundled Adventure via `adventure-platform-bukkit`, legacy item meta) |
| `platform-paper` | Paper enhancements (data components, Adventure APIs) |

At startup, `PlatformHookLoader` detects Paper via `io.papermc.paper.datacomponent.DataComponentTypes` and selects the appropriate implementation. Both hook JARs are bundled into the final plugin.

## Requirements

- Spigot or Paper 1.21+
- Java 25
- ProtocolLib (optional, enables fake explosion packets and advanced visuals)
