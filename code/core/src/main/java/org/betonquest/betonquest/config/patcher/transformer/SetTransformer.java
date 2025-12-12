package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.betonquest.betonquest.api.config.patcher.PatcherOptions;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Sets the given value on the given key.
 */
public class SetTransformer implements PatchTransformer {

    /**
     * Default constructor.
     */
    public SetTransformer() {
    }

    @Override
    public void transform(final PatcherOptions options, final ConfigurationSection config) throws PatchException {
        final String key = options.getString("key");
        final Object value = options.get("value");

        config.set(key, value);
    }
}
