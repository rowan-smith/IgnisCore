package dev.rono.igniscore.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Reports the Ignis API version compiled into this artifact and validates extension compatibility.
 *
 * <p>Values are loaded once from {@code ignis-api-version.properties} on the classpath. Extensions
 * may target an older API on the same major line; they may not require a newer API than the runtime
 * provides.</p>
 *
 * @see SemVersion
 */
public final class IgnisApiVersion {
    private static final Pattern MAJOR_ONLY = Pattern.compile("^(\\d+)$");
    private static final Pattern UNFILTERED_MAVEN_PLACEHOLDER = Pattern.compile("^@.+@$");

    /** Raw version string from {@code ignis-api-version.properties}, for example {@code 1.2.0}. */
    public static final String CURRENT;
    /** Parsed {@link SemVersion} of {@link #CURRENT}. */
    public static final SemVersion CURRENT_SEMVER;

    static {
        try (InputStream input = IgnisApiVersion.class.getResourceAsStream("/ignis-api-version.properties")) {
            if (input == null) {
                throw new IllegalStateException("Missing ignis-api-version.properties");
            }
            Properties properties = new Properties();
            properties.load(input);
            String version = properties.getProperty("version");
            if (version == null || version.isBlank()) {
                throw new IllegalStateException("ignis-api-version.properties is missing version");
            }
            CURRENT = version;
            CURRENT_SEMVER = SemVersion.parse(version);
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private IgnisApiVersion() {
    }

    /**
     * Ensures an extension's declared {@code api-version} is supported by this runtime.
     *
     * <p>Extensions may target an older API on the same major line; they may not require a newer API.</p>
     *
     * @param extensionApiVersion semver string from the extension manifest
     * @param extensionId extension identifier used in error messages
     * @throws IllegalStateException when the runtime API is older than the extension requires
     */
    public static void requireCompatible(String extensionApiVersion, String extensionId) {
        SemVersion required = parseExtensionRequirement(extensionApiVersion);
        if (SemVersion.isRuntimeCompatibleWith(CURRENT_SEMVER, required)) {
            return;
        }
        throw new IllegalStateException("Extension '" + extensionId + "' requires Ignis API "
                + required + " but runtime provides " + CURRENT_SEMVER
                + " (same major and runtime >= required)");
    }

    /**
     * Parses an extension manifest {@code api-version}, accepting shorthand and development labels
     * produced by standalone JitPack builds (for example {@code main}).
     *
     * @param extensionApiVersion raw manifest value
     * @return semver used for compatibility checks
     */
    public static SemVersion parseExtensionRequirement(String extensionApiVersion) {
        if (extensionApiVersion == null || extensionApiVersion.isBlank()) {
            return CURRENT_SEMVER;
        }

        String raw = extensionApiVersion.trim();
        try {
            return SemVersion.parse(raw);
        } catch (IllegalArgumentException ignored) {
            // Fall through to shorthand and development labels.
        }

        var majorOnly = MAJOR_ONLY.matcher(raw);
        if (majorOnly.matches()) {
            return new SemVersion(Integer.parseInt(majorOnly.group(1)), 0, 0, null);
        }

        if (isDevelopmentLabel(raw) || UNFILTERED_MAVEN_PLACEHOLDER.matcher(raw).matches()) {
            return CURRENT_SEMVER;
        }

        throw new IllegalArgumentException("Invalid semver: " + extensionApiVersion);
    }

    private static boolean isDevelopmentLabel(String raw) {
        String normalized = raw.toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "main", "master", "dev", "develop", "development", "snapshot" -> true;
            default -> normalized.endsWith("-snapshot");
        };
    }
}
