import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';
import siteVars from './site-vars.json';
import siteVarsPlugin from './src/remark/siteVarsPlugin';

const JAVADOC_VERSIONS = [
  {label: `${siteVars.version} (current)`, href: `pathname:///apidocs/${siteVars.version}/index.html`},
];

const config: Config = {
  title: 'IgnisCore',
  tagline: 'Multi-platform framework for custom blocks, items, and server extensions.',
  favicon: 'img/favicon.ico',

  url: siteVars.siteUrl,
  baseUrl: siteVars.baseUrl,

  organizationName: 'rowan-smith',
  projectName: 'igniscore',

  customFields: {
    ...siteVars,
  },

  onBrokenLinks: 'throw',
  trailingSlash: false,

  markdown: {
    hooks: {
      onBrokenMarkdownLinks: 'warn',
    },
  },

  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      {
        docs: {
          routeBasePath: '/',
          sidebarPath: './sidebars.ts',
          editUrl: `https://github.com/${siteVars.repo}/edit/main/website/`,
          remarkPlugins: [siteVarsPlugin],
        },
        blog: false,
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  themes: [
    [
      require.resolve('@easyops-cn/docusaurus-search-local'),
      {
        hashed: true,
        language: ['en'],
        indexDocs: true,
        docsRouteBasePath: '/',
        highlightSearchTermsOnTargetPage: true,
        searchResultLimits: 12,
        searchBarShortcutHint: true,
      },
    ],
  ],

  themeConfig: {
    metadata: [
      {
        name: 'description',
        content:
          'IgnisCore — custom blocks, items, and extensions for Spigot, Paper, and Sponge. Runtime extension loading, layered plugin API, and auto-built resource packs.',
      },
    ],
    navbar: {
      title: 'IgnisCore',
      logo: {
        alt: 'IgnisCore',
        src: 'img/ignis-logo.svg',
      },
      hideOnScroll: true,
      items: [
        {
          type: 'dropdown',
          label: 'Getting Started',
          position: 'left',
          items: [
            {type: 'doc', docId: 'index', label: 'Overview'},
            {type: 'doc', docId: 'requirements', label: 'Requirements'},
            {type: 'doc', docId: 'concepts/extensions', label: 'Extensions'},
            {type: 'doc', docId: 'concepts/blocks', label: 'Block Lifecycle'},
            {type: 'doc', docId: 'concepts/items', label: 'Item Lifecycle'},
            {type: 'doc', docId: 'concepts/resource-pack', label: 'Resource Packs'},
          ],
        },
        {type: 'doc', docId: 'commands/index', label: 'Commands', position: 'left'},
        {
          type: 'dropdown',
          label: 'Configuration',
          position: 'left',
          items: [
            {type: 'doc', docId: 'configuration', label: 'config.yml'},
            {type: 'doc', docId: 'storage', label: 'Storage & Persistence'},
          ],
        },
        {
          type: 'dropdown',
          label: 'Guides',
          position: 'left',
          items: [
            {type: 'doc', docId: 'guides/recipes', label: 'Common Setups'},
            {type: 'doc', docId: 'guides/troubleshooting', label: 'Troubleshooting'},
            {type: 'doc', docId: 'faq/api-version', label: 'API Versioning'},
            {type: 'doc', docId: 'changelog', label: 'Changelog'},
          ],
        },
        {
          type: 'dropdown',
          label: 'API',
          position: 'left',
          items: [
            {type: 'doc', docId: 'developers/index', label: 'Developer Overview'},
            {type: 'doc', docId: 'developers/cookbook', label: 'Extension Cookbook'},
            {type: 'doc', docId: 'developers/api/index', label: 'API Reference'},
            {type: 'doc', docId: 'developers/architecture', label: 'Architecture'},
            {type: 'doc', docId: 'developers/reference', label: 'Javadoc Hub'},
            {type: 'doc', docId: 'developers/contributing', label: 'Contributing'},
            {type: 'html', value: '<hr/>'},
            ...JAVADOC_VERSIONS.map((v) => ({
              label: `Javadoc ${v.label}`,
              href: v.href,
            })),
            {type: 'html', value: '<hr/>'},
            {
              href: `https://github.com/${siteVars.repo}/releases`,
              label: 'Release notes (GitHub)',
            },
            {type: 'doc', docId: 'changelog', label: 'Changelog'},
          ],
        },
        {
          href: `https://github.com/${siteVars.repo}/releases`,
          label: 'Download',
          position: 'right',
          className: 'navbar-download-btn',
        },
        {
          type: 'dropdown',
          label: siteVars.version,
          position: 'right',
          className: 'navbar-version-badge',
          items: [
            {
              href: `https://github.com/${siteVars.repo}/releases/tag/v${siteVars.version}`,
              label: `Release v${siteVars.version}`,
            },
            {
              href: `pathname:///apidocs/${siteVars.version}/index.html`,
              label: 'Current Javadoc',
            },
            {type: 'doc', docId: 'developers/reference', label: 'Javadoc hub'},
            {type: 'doc', docId: 'changelog', label: 'Changelog'},
            {
              href: `https://github.com/${siteVars.repo}/releases`,
              label: 'Older releases',
            },
          ],
        },
        {
          href: `https://github.com/${siteVars.repo}`,
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Getting Started',
          items: [
            {label: 'Overview', to: '/'},
            {label: 'Requirements', to: '/requirements'},
            {label: 'Extensions', to: '/concepts/extensions'},
          ],
        },
        {
          title: 'Reference',
          items: [
            {label: 'Commands', to: '/commands'},
            {label: 'Configuration', to: '/configuration'},
            {label: 'Extension Cookbook', to: '/developers/cookbook'},
            {label: 'API Reference', to: '/developers/api'},
            {label: 'Architecture', to: '/developers/architecture'},
            {label: 'Javadoc', to: '/developers/reference'},
          ],
        },
        {
          title: 'Project',
          items: [
            {label: 'Changelog', to: '/changelog'},
            {label: 'GitHub', href: `https://github.com/${siteVars.repo}`},
            {label: 'Releases', href: `https://github.com/${siteVars.repo}/releases`},
            {label: 'Issues', href: `https://github.com/${siteVars.repo}/issues`},
            {
              label: 'License',
              href: `https://github.com/${siteVars.repo}/blob/main/LICENSE`,
            },
          ],
        },
      ],
      copyright: `© ${new Date().getFullYear()} IgnisCore v${siteVars.version}`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
      additionalLanguages: ['bash', 'yaml', 'java'],
    },
    colorMode: {
      defaultMode: 'light',
      respectPrefersColorScheme: true,
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
