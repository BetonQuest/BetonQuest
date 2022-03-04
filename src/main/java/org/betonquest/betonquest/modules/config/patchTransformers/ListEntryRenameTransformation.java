package org.betonquest.betonquest.modules.config.patchTransformers;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renames an entry in a list.
 */
public class ListEntryRenameTransformation implements PatchTransformation {

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String key = options.get("key");
        final String regex = options.get("oldEntryRegex");
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final String newEntry = options.get("newEntry");

        final var list = config.getStringList(key);
        if (list == null) {
            //TODO: Exception?
            return;
        }

        int index = 0;
        for (final String entry : list) {
            final Matcher m = pattern.matcher(entry);
            if (m.matches()) {
                break;
            }
            index++;
            if (index == list.size()) {
                //TODO: No match -> exception
                return;
            }

        }
        list.set(index, newEntry);
        config.set(key, list);
    }
}
