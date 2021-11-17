package org.betonquest.betonquest.api.bukkit.config;

import java.util.Objects;

/**
 * Wraps an {@link KeyConflictConfigurationException} with an unchecked exception.
 */
public class UncheckedKeyConflictConfigurationException extends RuntimeException {
    private static final long serialVersionUID = -8134305061645241065L;

    /**
     * Constructs an instance of this class.
     *
     * @param message the detail message, can be null
     * @param cause   the {@code KeyConflictConfigurationException}
     * @throws NullPointerException if the cause is {@code null}
     */
    public UncheckedKeyConflictConfigurationException(final String message, final KeyConflictConfigurationException cause) {
        super(message, Objects.requireNonNull(cause));
    }

    /**
     * Constructs an instance of this class.
     *
     * @param cause the {@code KeyConflictConfigurationException}
     * @throws NullPointerException if the cause is {@code null}
     */
    public UncheckedKeyConflictConfigurationException(final KeyConflictConfigurationException cause) {
        super(Objects.requireNonNull(cause));
    }

    /**
     * Returns the cause of this exception.
     *
     * @return the {@code KeyConflictConfigurationException} which is the cause of this exception.
     */
    @Override
    public KeyConflictConfigurationException getCause() {
        return (KeyConflictConfigurationException) super.getCause();
    }
}
