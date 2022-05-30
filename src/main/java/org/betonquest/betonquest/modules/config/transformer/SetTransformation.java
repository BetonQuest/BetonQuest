package org.betonquest.betonquest.modules.config.transformer;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Sets the given value on the given key.
 */
public class SetTransformation implements PatchTransformation {

    /**
     * Default constructor
     */
    public SetTransformation() {
    }

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String key = options.get("key");
        final String value = options.get("value");

        config.set(key, value);
    }
}
