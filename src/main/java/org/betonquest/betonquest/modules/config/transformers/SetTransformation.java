package org.betonquest.betonquest.modules.config.transformers;

import org.betonquest.betonquest.modules.config.PatchTransformation;
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
        final String doOverride = options.get("override");

        config.set(key, value);
        //TODO: Overrides
    }
}
