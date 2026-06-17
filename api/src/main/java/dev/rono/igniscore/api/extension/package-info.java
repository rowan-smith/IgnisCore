/**
 * Extension JAR metadata, resource access, and load-time requirements.
 *
 * <p>Each extension ships {@code block-extension.yml} or {@code item-extension.yml} parsed by
 * {@link ExtensionManifest}. Optional {@code requires-integrations} and {@code profiles} entries
 * describe platform dependencies and expected strategy callbacks. {@link ExtensionResources}
 * opens classpath assets from the extension class loader.</p>
 */
package dev.rono.igniscore.api.extension;
