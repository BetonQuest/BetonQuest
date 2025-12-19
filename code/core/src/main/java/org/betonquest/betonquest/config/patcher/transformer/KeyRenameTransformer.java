package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.betonquest.betonquest.api.config.patcher.PatcherOptions;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Renames a key while preserving the value.
 */
public class KeyRenameTransformer implements PatchTransformer {

    /**
     * Default constructor.
     */
    public KeyRenameTransformer() {
    }

    @Override
    public void transform(final PatcherOptions options, final ConfigurationSection config) throws PatchException {
        final String oldKey = options.getString("oldKey");
        final String newKey = options.getString("newKey");

        final Object value = config.get(oldKey);
        if (value == null) {
            throw new PatchException("Key '" + oldKey + "' was not set, skipping transformation to '" + newKey + "'.");
        }
        config.set(oldKey, null);
        if (value instanceof final ConfigurationSection section) {
            final Map<String, Object> values = section.getValues(true).entrySet().stream()
                    .filter(entry -> !(entry.getValue() instanceof ConfigurationSection))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            config.createSection(newKey, values);
        } else {
            config.set(newKey, value);
        }
    }
}
