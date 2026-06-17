package dev.rono.igniscore.api.extension;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates extension manifest metadata against runtime capabilities.
 */
public final class ExtensionRequirements {

    private ExtensionRequirements() {
    }

    /**
     * Ensures every {@link ExtensionManifest#getRequiredIntegrations() required integration}
     * is available. Logs warnings for missing optional integrations when
     * {@code warnOnly} is true; otherwise throws {@link ExtensionRequirementException}.
     *
     * @param manifest extension manifest from the JAR
     * @param capabilities current server integration availability
     * @param warnOnly when true, missing integrations produce warnings instead of failing load
     * @return human-readable warnings (empty when fully satisfied or failing fast)
     * @throws ExtensionRequirementException when {@code warnOnly} is false and a requirement is missing
     */
    public static List<String> validate(ExtensionManifest manifest,
                                        ExtensionRuntimeCapabilities capabilities,
                                        boolean warnOnly) {
        List<String> warnings = new ArrayList<>();
        for (ExtensionIntegration integration : manifest.getRequiredIntegrations()) {
            if (capabilities.isEnabled(integration)) {
                continue;
            }
            String message = "Extension '" + manifest.getId() + "' requires integration '"
                    + integration.manifestKey() + "' but it is not available on this platform";
            if (warnOnly) {
                warnings.add(message);
            } else {
                throw new ExtensionRequirementException(message);
            }
        }
        return List.copyOf(warnings);
    }
}
