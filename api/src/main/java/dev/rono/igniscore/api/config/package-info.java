/**
 * YAML parsing and typed configuration views for extension definitions.
 *
 * <p>{@link dev.rono.igniscore.api.config.YamlDefinitions} and
 * {@link dev.rono.igniscore.api.config.DefinitionParser} load {@code config.yml} maps into
 * {@link dev.rono.igniscore.api.model.BlockDefinition} and
 * {@link dev.rono.igniscore.api.model.ItemDefinition}. {@link ExtensionConfig} exposes nested
 * sections; {@link BlockBehaviorConfig} and {@link ItemBehaviorConfig} interpret standard
 * {@code behavior} keys.</p>
 */
package dev.rono.igniscore.api.config;
