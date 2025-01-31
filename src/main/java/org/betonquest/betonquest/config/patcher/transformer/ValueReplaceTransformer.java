package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

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
    public void transform(final Map<String, String> options, final ConfigurationSection config) throws PatchException {
        final String key = options.get("key");

        final Object value = config.get(key);
        if (value == null) {
            throw new PatchException("The key '" + key + "' did not exist, skipping transformation.");
        }

        final String oldValue = options.get("oldValue");
        final String newValue = options.get("newValue");

        final String replaced = value.toString().replace(oldValue, newValue);

        if (replaced.equals(value.toString())) {
            throw new PatchException("Value does not contain the old value '" + oldValue + "', skipping transformation.");
        } else {
            config.set(key, replaced);
        }
    }
}
