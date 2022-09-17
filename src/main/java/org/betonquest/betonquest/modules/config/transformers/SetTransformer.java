package org.betonquest.betonquest.modules.config.transformers;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Sets the given value on the given key.
 */
public class SetTransformer implements PatchTransformer {

    /**
     * Default constructor
     */
    public SetTransformer() {
    }

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) throws PatchException {
        final String key = options.get("key");
        final String value = options.get("value");
        final boolean doOverride = Boolean.parseBoolean(options.get("override"));

        if (!config.isSet(key) || doOverride) {
            config.set(key, value);
        } else {
            throw new PatchException("The key '%s' is already set and won't be overridden with '%s' since override is disabled."
                    .formatted(key, value));
        }
    }
}
