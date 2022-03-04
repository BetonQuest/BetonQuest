package org.betonquest.betonquest.modules.config.patchTransformers;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class RemoveTransformation implements PatchTransformation {

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String key = options.get("key");

        config.set(key, null);
    }
}
