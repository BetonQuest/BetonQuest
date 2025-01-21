package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renames a value for a given key if the given regex matches.
 */
public class ValueRenameTransformer implements PatchTransformer {

    /**
     * Default constructor.
     */
    public ValueRenameTransformer() {
    }

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) throws PatchException {
        final String key = options.get("key");

        final Object value = config.get(key);
        if (value == null) {
            throw new PatchException("The key '" + key + "' did not exist, skipping transformation.");
        }

        final String regex = options.get("oldValueRegex");
        final Pattern pattern = Pattern.compile(regex);
        final String newEntry = options.get("newValue");

        final Matcher matcher = pattern.matcher(value.toString());
        if (matcher.matches()) {
            config.set(key, newEntry);
        } else {
            throw new PatchException("Value does not match the given regex, skipping transformation.");
        }
    }
}
