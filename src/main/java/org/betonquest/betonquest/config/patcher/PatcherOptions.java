package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Options for the patcher.
 * This class is used to store options for the patcher, which can be used by transformers.
 */
public class PatcherOptions {
    /**
     * The options for the patcher.
     */
    private final Map<String, Object> options;

    /**
     * Creates a new PatcherOptions instance with the given options.
     *
     * @param options the options to set
     */
    public PatcherOptions(final Map<?, ?> options) {
        this.options = options.entrySet().stream()
                .map(entry -> Map.entry(String.valueOf(entry.getKey()), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Gets the value of the given key.
     *
     * @param key the key to get
     * @return the value of the key
     * @throws PatchException if the key does not exist in the options
     */
    public Object get(final String key) throws PatchException {
        final Object object = options.get(key);
        if (object == null) {
            throw new PatchException("Key '" + key + "' does not exist in options.");
        }
        return object;
    }

    /**
     * Gets the value of the given key.
     *
     * @param key          the key to get
     * @param defaultValue the default value to return if the key does not exist
     * @return the value of the key
     * @throws PatchException if the key does not exist in the options
     */
    @Contract("_, !null -> !null")
    @Nullable
    public Object get(final String key, @Nullable final Object defaultValue) throws PatchException {
        final Object object = options.get(key);
        if (object == null) {
            return defaultValue;
        }
        return object;
    }

    /**
     * Gets the value of the given key as a boolean.
     *
     * @param key the key to get
     * @return the value of the key as a boolean
     * @throws PatchException if the key does not exist or cannot be converted to a boolean
     */
    public String getString(final String key) throws PatchException {
        final Object object = get(key);
        return String.valueOf(object);
    }

    /**
     * Gets the value of the given key as a boolean.
     *
     * @param key          the key to get
     * @param defaultValue the default value to return if the key does not exist
     * @return the value of the key as a boolean
     * @throws PatchException if the key does not exist or cannot be converted to a boolean
     */
    @Contract("_, !null -> !null")
    @Nullable
    public String getString(final String key, @Nullable final String defaultValue) throws PatchException {
        final Object object = get(key, defaultValue);
        if (object == null) {
            return null;
        }
        return String.valueOf(object);
    }
}
