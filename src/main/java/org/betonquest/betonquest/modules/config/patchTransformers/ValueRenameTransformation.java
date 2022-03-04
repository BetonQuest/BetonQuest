package org.betonquest.betonquest.modules.config.patchTransformers;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueRenameTransformation implements PatchTransformation {

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String key = options.get("key");
        final String regex = options.get("oldValueRegex");
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final String newEntry = options.get("newValue");

        final String value = config.getString(key);
        if (value == null) {
            //TODO: Exception?
            return;
        }
        final Matcher m = pattern.matcher(value);
        if (m.matches()) {
            config.set(key, newEntry);
        }
    }
}
