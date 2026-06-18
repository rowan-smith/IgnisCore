---
title: About IgnisCore
description: What IgnisCore is and who maintains it.
slug: /about
---

IgnisCore is a multi-platform framework for custom blocks, items, and runtime extensions, maintained by [rowan-smith](https://github.com/rowan-smith). The bootstrap plugin provides extension loading, block and item registries, resource-pack generation, and a layered plugin API for integrators.

Reference extension modules (blocks, items, shared helpers) live in the separate [IgnisCore-Extensions](https://github.com/%%site.extensionsRepo%%) repository. Build JARs from that project and deploy them to your server's `plugins/IgnisCore/blocks/` and `plugins/IgnisCore/items/` folders.

## Platforms

IgnisCore supports **Spigot**, **Paper**, and **SpongeVanilla** from a single bootstrap JAR. Bukkit-family servers require [NBTAPI](https://www.spigotmc.org/resources/nbt-api.7939/); Sponge uses native data containers.

## Documentation

- [Getting started](/)
- [Extension Cookbook](/developers/cookbook)
- [GitHub repository](https://github.com/%%site.repo%%)
