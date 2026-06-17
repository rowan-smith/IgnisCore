import type {SidebarsConfig} from '@docusaurus/plugin-content-docs';

const sidebars: SidebarsConfig = {
  docsSidebar: [
    {
      type: 'category',
      label: 'Getting Started',
      collapsed: false,
      items: [
        'index',
        'requirements',
        'concepts/extensions',
        'concepts/blocks',
        'concepts/items',
        'concepts/resource-pack',
        'concepts/strategies',
      ],
    },
    {
      type: 'category',
      label: 'Commands',
      collapsed: false,
      link: {type: 'doc', id: 'commands/index'},
      items: ['commands/general'],
    },
    {
      type: 'category',
      label: 'Configuration',
      collapsed: false,
      items: ['configuration', 'storage'],
    },
    {
      type: 'category',
      label: 'Guides',
      collapsed: false,
      items: [
        'guides/recipes',
        'guides/troubleshooting',
        'faq/api-version',
        'changelog',
      ],
    },
    {
      type: 'category',
      label: 'Developer Docs',
      collapsed: false,
      items: [
        'developers/index',
        'developers/cookbook',
        'developers/extension-profiles',
        'developers/extension-config',
        'developers/reference',
        {
          type: 'category',
          label: 'API Reference',
          collapsed: false,
          items: [
            'developers/api/index',
            'developers/api/layers',
            'developers/api/core-api',
            'developers/api/extension-shared',
          ],
        },
        'developers/architecture',
        'developers/contributing',
      ],
    },
    {
      type: 'category',
      label: 'Project',
      collapsed: true,
      items: ['about'],
    },
  ],
};

export default sidebars;
