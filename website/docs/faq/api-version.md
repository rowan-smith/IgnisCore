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

## Manifest example

```yaml
api-version: 1.0.0
```

## Maven dependency

```xml
<dependency>
  <groupId>dev.rono</groupId>
  <artifactId>api</artifactId>
  <version>%%site.version%%</version>
  <scope>provided</scope>
</dependency>
```

## Related

- [Extensions](/concepts/extensions) — manifest fields
- [Developer overview](/developers) — quick start
- [Extension Cookbook](/developers/cookbook) — build and test extensions
