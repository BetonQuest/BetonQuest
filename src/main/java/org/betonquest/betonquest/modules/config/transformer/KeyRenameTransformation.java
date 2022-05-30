package org.betonquest.betonquest.modules.config.transformer;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Renames a key while preserving the value.
 */
public class KeyRenameTransformation implements PatchTransformation {

    /**
     * Default constructor
     */
    public KeyRenameTransformation() {
    }

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String oldKey = options.get("oldKey");
        final String newKey = options.get("newKey");

        final String value = config.getString(oldKey);
        config.set(oldKey, null);
        config.set(newKey, value);
    }
}
