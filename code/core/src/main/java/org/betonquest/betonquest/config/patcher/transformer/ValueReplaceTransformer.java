package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.betonquest.betonquest.config.patcher.PatcherOptions;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Replace a value part for a given key if the given regex matches.
 */
public class ValueReplaceTransformer implements PatchTransformer {

    /**
     * Default constructor.
     */
    public ValueReplaceTransformer() {
    }

    @Override
    public void transform(final PatcherOptions options, final ConfigurationSection config) throws PatchException {
        final String key = options.getString("key");
        final String oldValue = options.getString("oldValue");
        final String newValue = options.getString("newValue");

        final String value = config.getString(key);
        if (value == null) {
            throw new PatchException("The key '" + key + "' did not exist, skipping transformation.");
        }

        final String replaced = value.replace(oldValue, newValue);
        if (replaced.equals(value)) {
            throw new PatchException("Value does not contain the old value '" + oldValue + "', skipping transformation.");
        } else {
            config.set(key, replaced);
        }
    }
}
