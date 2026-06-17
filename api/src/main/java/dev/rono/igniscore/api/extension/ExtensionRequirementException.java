package dev.rono.igniscore.api.extension;

/**
 * Thrown when an extension manifest declares requirements that the current
 * runtime cannot satisfy.
 */
public class ExtensionRequirementException extends RuntimeException {

    public ExtensionRequirementException(String message) {
        super(message);
    }
}
