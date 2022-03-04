package org.betonquest.betonquest.modules.config.patchTransformers;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class SetTransformation implements PatchTransformation {

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String key = options.get("key");
        final String value = options.get("value");

        config.set(key, value);
    }
}
