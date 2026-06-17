import type {CommandEntry} from '@site/src/data/commands';
import commands from '@site/src/data/commands.json';
import Link from '@docusaurus/Link';
import styles from './CommandIndex.module.css';

const CATEGORIES = [
  {
    slug: '/commands/general',
    label: 'General',
    description: 'Give items, reload extensions, resource packs, debug, and listing loaded JARs.',
  },
] as const;

function countForCategory(label: string): number {
  return (commands as CommandEntry[]).filter((entry) => entry.category === label).length;
}

export default function CommandIndex(): JSX.Element {
  return (
    <div className={styles.grid}>
      {CATEGORIES.map((category) => {
        const count = countForCategory(category.label);
        return (
          <Link key={category.slug} className={styles.card} to={category.slug}>
            <div className={styles.cardHeader}>
              <span className={styles.label}>{category.label}</span>
              <span className={styles.count}>
                {count} command{count === 1 ? '' : 's'}
              </span>
            </div>
            <p className={styles.description}>{category.description}</p>
          </Link>
        );
      })}
    </div>
  );
}
