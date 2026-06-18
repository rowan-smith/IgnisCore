---
title: API Versioning
description: How extension api-version semver interacts with the IgnisCore runtime.
slug: /faq/api-version
---

Extension manifests declare an `api-version` field — the **Ignis API** version the extension was built against, not the extension's own release version.

## Rules

| Scenario | Result |
|----------|--------|
| Runtime **1.2.0**, extension targets **1.0.0** (same major) | Loads |
| Runtime **1.2.0**, extension targets **1.3.0** | Fails — extension needs newer API |
| Runtime **1.x**, extension targets **2.x** | Fails — major mismatch |

Current API version: **`1.0.0`** (`IgnisApiVersion.CURRENT`).

## Breaking changes in 1.0.0

Strategy lifecycle hooks moved to the **event bus**. Extensions must implement `registerEvents()` and subscribe with helpers such as `onBlockPlace`, `onBlockTrigger`, and `onItemClick`. Legacy override methods (`onPlaced`, `onTrigger`, `onItemUse`, `onPlacedClick`, …) are not supported — there is no runtime compatibility shim.

See [Strategies](/concepts/strategies) and [Core API — Event bus](/developers/api/core-api#event-bus).

## Manifest example

```yaml
api-version: 1.0.0
```

## Maven dependency (JitPack)

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.rowan-smith.IgnisCore</groupId>
  <artifactId>api</artifactId>
  <version>%%site.version%%</version>
  <scope>provided</scope>
</dependency>
```

## Related

- [Extensions](/concepts/extensions) — manifest fields
- [Developer overview](/developers) — quick start
- [Extension Cookbook](/developers/cookbook) — build and test extensions
