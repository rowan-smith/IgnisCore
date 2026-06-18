---
title: Contributing
description: Build IgnisCore from source and run tests.
slug: /developers/contributing
---

## Prerequisites

- Java 25
- Maven 3.9+
- Node 18+ (for documentation site)

## Build

```bash
mvn clean package
```

Output:

- `bootstrap/universal/target/%%site.jarName%%` — all platforms
- `spigot/target/IgnisCore-Spigot-%%site.version%%.jar` — Spigot only
- `paper/target/IgnisCore-Paper-%%site.version%%.jar` — Paper only
- `sponge-v8.5.0/target/IgnisCore-Sponge-v8-%%site.version%%.jar` — Sponge 8.5
- `sponge-v12.0.0/target/IgnisCore-Sponge-v12-%%site.version%%.jar` — Sponge 12
- `sponge-v19.0.0/target/IgnisCore-Sponge-v19-%%site.version%%.jar` — Sponge 19

## Run tests

```bash
mvn test
```

Extension modules include MockBukkit strategy and behavior tests.

## Documentation site

```bash
cd website
npm install
npm start          # http://localhost:3000
```

Production build (includes Javadoc when run locally):

```bash
cd website
npm run build:javadoc   # optional locally; CI does this
npm run build
```

## Adding a doc page

1. Create `website/docs/{section}/{page-name}.md` with frontmatter (`title`, `description`, `slug`)
2. Add doc ID to `website/sidebars.ts`
3. Optionally add to navbar in `website/docusaurus.config.ts`
4. Cross-link from related pages
5. Use `%%site.version%%`, `%%site.jarName%%`, and `%%site.repo%%` for versioned content
6. Run `npm run build` — broken links fail the build

## Pull requests

- Keep changes focused
- Update docs when behavior or developer workflow changes
- Ensure `mvn test` passes

## Related

- [Architecture](/developers/architecture) — module layout
- [Extension Cookbook](/developers/cookbook) — extension patterns
