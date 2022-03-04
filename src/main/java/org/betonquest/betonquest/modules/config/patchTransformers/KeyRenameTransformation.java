package org.betonquest.betonquest.modules.config.patchTransformers;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class KeyRenameTransformation implements PatchTransformation {

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String oldKey = options.get("oldKey");
        final String newKey = options.get("newKey");

        final String value = config.getString(oldKey);
        config.set(oldKey, null);
        config.set(newKey, value);
    }
}
