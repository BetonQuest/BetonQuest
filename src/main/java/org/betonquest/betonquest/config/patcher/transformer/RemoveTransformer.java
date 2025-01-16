package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Deletes the given key.
 */
public class RemoveTransformer implements PatchTransformer {

    /**
     * Default constructor.
     */
    public RemoveTransformer() {
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
