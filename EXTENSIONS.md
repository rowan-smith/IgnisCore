# IgnisCore Extension Authoring Guide

This guide explains how to create **block** and **item** extensions for IgnisCore. Extensions are platform-agnostic JARs loaded at runtime from the server data folder.

## Quick start with Maven archetypes

After building IgnisCore (`mvn clean install`), generate a new extension module:

### Block extension

```bash
mvn -pl archetypes/block-extension archetype:generate \
  -DarchetypeGroupId=dev.rono.archetypes \
  -DarchetypeArtifactId=igniscore-block-extension-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.example.ignis \
  -DartifactId=my-tnt \
  -Dversion=1.0.0 \
  -Dpackage=dev.rono.igniscore.block.mytnt \
  -DextensionId=my-tnt
```

### Item extension

```bash
mvn -pl archetypes/item-extension archetype:generate \
  -DarchetypeGroupId=dev.rono.archetypes \
  -DarchetypeArtifactId=igniscore-item-extension-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.example.ignis \
  -DartifactId=my-grenade \
  -Dversion=1.0.0 \
  -Dpackage=dev.rono.igniscore.item.mygrenade \
  -DextensionId=my-grenade
```

Build the generated project (`mvn package`) and copy the JAR to:

| Kind | Deploy path (under plugin data folder) |
|------|----------------------------------------|
| Block | `blocks/` |
| Item | `items/` |

Bundled extensions ship inside the bootstrap JAR and extract on first boot; drop-in JARs in these folders override or add types.

## Project layout

```
my-extension/
├── pom.xml                         # api dependency (provided scope)
├── src/main/java/.../Strategy.java # required strategy class
├── src/main/resources/
│   ├── block-extension.yml         # or item-extension.yml
│   ├── config.yml                  # visuals + tuning
│   └── textures/                   # PNG assets
└── src/test/java/...               # StrategyTest + BehaviorTest
```

## Two identifiers

| Field | Source | Purpose |
|-------|--------|---------|
| **Manifest `id`** | `*-extension.yml` | Registers the strategy in `IgnisStrategyRegistry` |
| **Config `id`** | `config.yml` | In-game type id (`/ignis give`, NBT type key) |

These are usually the same (e.g. `nuke`). Hyphens in ids become package segments without hyphens (`quarry-cache` → `quarrycache`).

## Manifest (`block-extension.yml` / `item-extension.yml`)

```yaml
id: my-tnt
name: My TNT
version: 1.0.0
api-version: 1.0.0        # Ignis API semver (see below)
author: YourName
strategy: dev.rono.igniscore.block.mytnt.Strategy
```

### API version (semver)

`api-version` declares the **Ignis API** your extension was built against, not your extension's release version.

- Runtime **1.2.0** loads extensions targeting **1.0.0** on the same major line.
- Extensions targeting **1.3.0** fail on runtime **1.2.0**.
- Major versions must match (**2.x** extensions do not load on **1.x**).

Current API version: **`1.0.0`** (`IgnisApiVersion.CURRENT`).

## Strategy class contract

Every extension JAR must provide a public class listed in the manifest:

```java
public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
    }
}
```

Items extend `AbstractIgnisItemStrategy` instead.

`IgnisStrategyContext` exposes scheduler, NBT, protocol, effects, and `ExtensionSupport` (inventories, drop collectors, world bridge).

## Block lifecycle

Blocks have **placed** and **active** phases:

| Phase | When | Callbacks |
|-------|------|-----------|
| **Placed** | Barrier block exists in world | `onPlaced`, `onPlacedInteract`, `onPlacedBreak` |
| **Active** | After ignition / fuse | `onPlace`, `onTick`, `onTrigger` |

Override `profile(BlockDefinition)` for defaults (combustible, click actions, fuse/radius). YAML `interactions` and `breaking` override profile at runtime.

### Typed block config

```java
ExplosionConfig explosion = ExtensionConfigs.explosion(definition);
int fuse = explosion.fuse();
float power = explosion.resolvedPower();
```

Raw maps remain available via `definition.getCustomConfig()`.

## Item lifecycle

Items use a single hook:

```java
@Override
public void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                       IgnisInteraction action, IgnisBlock clickedBlock) {
    switch (definition.interactionAction(action)) {
        case "throw" -> throwItem(player, definition, item);
        default -> { }
    }
}
```

### Typed item config

```java
ThrowableItemConfig throwable = ExtensionConfigs.throwable(definition);
double speed = throwable.throwVelocity();
int fuseTicks = throwable.fuseTicks();
```

Declare actions under `interactions` in `config.yml`:

```yaml
interactions:
  right_click:
    action: throw
```

## Config.yml overview (blocks)

```yaml
id: my-tnt
display:
  title: "&cMy TNT"
  description: ["&7Boom"]
block:
  placeable: true
  breakable: true
  breaking:
    ticks: 0
textures:
  top: top.png
  side: side.png
  bottom: bottom.png
custom_data:
  fuse: 80
  radius: 4.0
  power: 4.0
  fire: false
interactions:
  right_click:
    action: ignite
    materials: [FLINT_AND_STEEL]
```

## Config.yml overview (items)

```yaml
id: my-grenade
display:
  title: "&cGrenade"
item:
  base_material: snowball
textures:
  icon: icon.png
custom_data:
  throw_velocity: 1.2
  fuse_ticks: 40
  power: 4.0
interactions:
  right_click:
    action: throw
```

## Testing

Extension modules depend on `api` test-jar for `ExtensionTestSupport` and `BehaviorTestSupport`:

```xml
<dependency>
  <groupId>dev.rono</groupId>
  <artifactId>api</artifactId>
  <type>test-jar</type>
  <scope>test</scope>
</dependency>
```

Minimum tests:

- **StrategyTest** — manifest id, strategy class loads, profile smoke
- **BehaviorTest** — exercise `onPlaced` / `onTrigger` / `onItemUse` with `BehaviorTestSupport`

## Reference extensions

| Block | Pattern |
|-------|---------|
| `extensions/blocks/nuke` | Explosive + fuse ticks + `ExplosionConfig` |
| `extensions/blocks/quarry-cache` | Non-combustible GUI block + `ExtensionSupport` |
| `extensions/blocks/tunneling-tnt` | Minimal `onTrigger`-only strategy |

| Item | Pattern |
|------|---------|
| `extensions/items/grenade` | Throwable + `ThrowableItemConfig` + `interactionAction` |
| `extensions/items/detonator` | Multi-action item via YAML interactions |

## Breaking changes (1.x)

- Block placed callbacks renamed from `onStaticPlace` / `onStaticInteract` / `onStaticBreak` to **`onPlaced`** / **`onPlacedInteract`** / **`onPlacedBreak`**.
