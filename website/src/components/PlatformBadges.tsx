import type {CSSProperties} from 'react';
import styles from './PlatformBadges.module.css';

const PLATFORMS = [
  {name: 'Spigot', color: '#f59e0b', abbr: 'S'},
  {name: 'Paper', color: '#ef4444', abbr: 'P'},
  {name: 'Sponge', color: '#eab308', abbr: 'Sp'},
];

export default function PlatformBadges(): JSX.Element {
  return (
    <div className={styles.row}>
      <span className={styles.label}>One jar · runs on</span>
      <ul className={styles.list}>
        {PLATFORMS.map((platform) => (
          <li key={platform.name} className={styles.badge}>
            <span
              className={styles.icon}
              style={{'--platform-color': platform.color} as CSSProperties}>
              {platform.abbr}
            </span>
            {platform.name}
          </li>
        ))}
      </ul>
    </div>
  );
}
