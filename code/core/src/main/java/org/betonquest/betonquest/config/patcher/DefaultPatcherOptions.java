package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatcherOptions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Options for the patcher.
 * This class is used to store options for the patcher, which can be used by transformers.
 */
public class DefaultPatcherOptions implements PatcherOptions {

    /**
     * The options for the patcher.
     */
    private final Map<String, Object> options;

    /**
     * Creates a new PatcherOptions instance with the given options.
     *
     * @param options the options to set
     */
    public DefaultPatcherOptions(final Map<?, ?> options) {
        this.options = options.entrySet().stream()
                .map(entry -> Map.entry(String.valueOf(entry.getKey()), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Object get(final String key) throws PatchException {
        final Object object = options.get(key);
        if (object == null) {
            throw new PatchException("Key '" + key + "' does not exist in options.");
        }
        return object;
    }

    @Contract("_, !null -> !null")
    @Override
    @Nullable
    public Object get(final String key, @Nullable final Object defaultValue) {
        final Object object = options.get(key);
        if (object == null) {
            return defaultValue;
        }
        return object;
    }

    @Override
    public String getString(final String key) throws PatchException {
        final Object object = get(key);
        return String.valueOf(object);
    }

    @Contract("_, !null -> !null")
    @Override
    @Nullable
    public String getString(final String key, @Nullable final String defaultValue) {
        final Object object = get(key, defaultValue);
        if (object == null) {
            return null;
        }
        return String.valueOf(object);
    }
}
