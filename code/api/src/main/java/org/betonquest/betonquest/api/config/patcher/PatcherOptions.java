package org.betonquest.betonquest.api.config.patcher;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Options for the patcher.
 * This class is used to store options for the patcher, which can be used by transformers.
 *
 * @since 3.0.0
 */
public interface PatcherOptions {

    /**
     * Gets the value of the given key.
     *
     * @param key the key to get
     * @return the value of the key
     * @throws PatchException if the key does not exist in the options
     * @since 3.0.0
     */
    Object get(String key) throws PatchException;

    /**
     * Gets the value of the given key.
     *
     * @param key          the key to get
     * @param defaultValue the default value to return if the key does not exist
     * @return the value of the key
     * @since 3.0.0
     */
    @Contract("_, !null -> !null")
    @Nullable
    Object get(String key, @Nullable Object defaultValue);

    /**
     * Gets the value of the given key.
     *
     * @param key the key to get
     * @return the value of the key
     * @throws PatchException if the key does not exist in the options
     * @since 3.0.0
     */
    String getString(String key) throws PatchException;

    /**
     * Gets the value of the given key.
     *
     * @param key          the key to get
     * @param defaultValue the default value to return if the key does not exist
     * @return the value of the key
     * @since 3.0.0
     */
    @Contract("_, !null -> !null")
    @Nullable
    String getString(String key, @Nullable String defaultValue);
}
