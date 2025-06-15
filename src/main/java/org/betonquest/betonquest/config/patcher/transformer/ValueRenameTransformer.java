package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.betonquest.betonquest.config.patcher.PatcherOptions;
import org.bukkit.configuration.ConfigurationSection;

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
    public void transform(final PatcherOptions options, final ConfigurationSection config) throws PatchException {
        final String key = options.getString("key");
        final String regex = options.getString("oldValueRegex");
        final Object newEntry = options.get("newValue");

        final String value = config.getString(key);
        if (value == null) {
            throw new PatchException("The key '" + key + "' did not exist, skipping transformation.");
        }

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            config.set(key, newEntry);
        } else {
            throw new PatchException("Value does not match the given regex '" + regex + "', skipping transformation.");
        }
    }
}
