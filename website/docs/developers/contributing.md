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

- `bukkit/target/%%site.jarName%%` — Spigot and Sponge
- `paper/target/IgnisCore-Paper-%%site.version%%.jar` — Paper

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
