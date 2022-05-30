package org.betonquest.betonquest.modules.config.transformer;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Deletes the given key.
 */
public class RemoveTransformation implements PatchTransformation {

    /**
     * Default constructor
     */
    public RemoveTransformation() {
    }

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String key = options.get("key");

        config.set(key, null);
    }
}
