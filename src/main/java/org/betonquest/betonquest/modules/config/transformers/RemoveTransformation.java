package org.betonquest.betonquest.modules.config.transformers;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformation;
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
    public void transform(final Map<String, String> options, final ConfigurationSection config) throws PatchException {
        final String key = options.get("key");
        if (config.isSet(key)) {
            config.set(key, null);
        } else {
            throw new PatchException("Key '" + key + "' did not exist, so it was not deleted.");
        }
    }
}
