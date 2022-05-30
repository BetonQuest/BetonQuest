package org.betonquest.betonquest.modules.config.transformer;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renames a value for a given key if the given regex matches.
 */
public class ValueRenameTransformation implements PatchTransformation {

    /**
     * Default constructor
     */
    public ValueRenameTransformation() {
    }

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String key = options.get("key");

        final String value = config.getString(key);
        if (value == null) {
            return;
        }

        final String regex = options.get("oldValueRegex");
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final String newEntry = options.get("newValue");

        final Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            config.set(key, newEntry);
        }
    }
}
