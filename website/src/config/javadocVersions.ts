import siteVars from '@site/site-vars.json';

export type JavadocVersion = {
  version: string;
  label: string;
  href: string;
  description: string;
  current?: boolean;
};

export function getJavadocVersions(): JavadocVersion[] {
  return [
    {
      version: siteVars.version,
      label: `${siteVars.version} (current)`,
      href: `pathname:///apidocs/${siteVars.version}/index.html`,
      description: 'IgnisCore public API',
      current: true,
    },
  ];
}
