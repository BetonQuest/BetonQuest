package org.betonquest.betonquest.modules.config.transformers;

import org.betonquest.betonquest.modules.config.PatchTransformation;
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
        config.set(options.get("key"), null);
    }
}
