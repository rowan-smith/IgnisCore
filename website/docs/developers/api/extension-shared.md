---
title: Extension Shared API
description: Optional ExtensionShared helpers from IgnisCore-Extensions.
slug: /developers/api/extension-shared
---

The `shared` module in [IgnisCore-Extensions](https://github.com/%%site.extensionsRepo%%) ships optional helpers for extension authors. Use the **`ExtensionShared`** facade for grouped, API-style access — do not import `*Support` classes directly.

## Dependency

Build `shared` from [IgnisCore-Extensions](https://github.com/%%site.extensionsRepo%%) and depend on it with `provided` scope in your extension project:

```xml
<dependency>
  <groupId>dev.rono.extensions</groupId>
  <artifactId>shared</artifactId>
  <version>${igniscore.version}</version>
  <scope>provided</scope>
</dependency>
```

Install the built `shared` JAR on your dev server classpath when testing extensions that use it. Do **not** shade `shared` into extension JARs — each extension should declare it as `provided`.

## Usage

```java
ExtensionShared.blocks().fuse(context, definition);
ExtensionShared.items().throwable(context, definition);
```

See Javadoc on `ExtensionShared` in the extensions repository.

## Related

- [Extension Cookbook](/developers/cookbook)
- [API layers](/developers/api/layers)
