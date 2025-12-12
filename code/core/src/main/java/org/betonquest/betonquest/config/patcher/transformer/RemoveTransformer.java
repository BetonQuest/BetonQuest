package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.betonquest.betonquest.api.config.patcher.PatcherOptions;
import org.bukkit.configuration.ConfigurationSection;

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
    public void transform(final PatcherOptions options, final ConfigurationSection config) throws PatchException {
        final String key = options.getString("key");
        if (config.isSet(key)) {
            config.set(key, null);
        } else {
            throw new PatchException("Key '" + key + "' did not exist, so it was not deleted.");
        }
    }
}
