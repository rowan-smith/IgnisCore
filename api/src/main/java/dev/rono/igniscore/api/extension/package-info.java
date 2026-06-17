/**
 * Extension manifest metadata, optional integration requirements, and
 * behavior profile declarations.
 *
 * <p>Every extension JAR ships a {@link ExtensionManifest} parsed from
 * {@code block-extension.yml} or {@code item-extension.yml}. Use
 * {@link ExtensionIntegration} and {@link ExtensionProfile} tokens in YAML
 * to document and validate capabilities at load time.</p>
 */
package dev.rono.igniscore.api.extension;
