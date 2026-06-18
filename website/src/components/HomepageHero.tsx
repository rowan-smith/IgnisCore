import Link from '@docusaurus/Link';
import PlatformBadges from '@site/src/components/PlatformBadges';
import siteVars from '@site/site-vars.json';
import styles from './HomepageHero.module.css';

export default function HomepageHero(): JSX.Element {
  return (
    <section className={styles.hero}>
      <div className={styles.grid} aria-hidden="true" />
      <div className={styles.inner}>
        <span className={styles.badge}>
          v{siteVars.version} · Spigot · Paper · Sponge
        </span>
        <h1 className={styles.title}>
          Custom blocks and items, <em>one framework</em>
        </h1>
        <p className={styles.subtitle}>
          IgnisCore is a multi-platform framework for custom blocks, items, and runtime
          extensions — fuse explosives, utility blocks, link tools, consumables, throwables,
          and more. Platform-neutral core, layered plugin API, runtime JAR loading, and
          auto-built resource packs in one bootstrap jar for Spigot, Paper, and Sponge.
        </p>
        <div className={styles.actions}>
          <Link
            className={styles.primary}
            href={`https://github.com/${siteVars.repo}/releases`}>
            Download {siteVars.jarName}
          </Link>
          <Link className={styles.secondary} to="/commands">
            Command reference
          </Link>
          <Link className={styles.ghost} to="/developers/cookbook">
            Extension cookbook
          </Link>
        </div>
        <PlatformBadges />
      </div>
    </section>
  );
}
