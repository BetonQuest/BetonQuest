package org.betonquest.betonquest.modules.config.transformer;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renames an entry in a list.
 */
public class ListEntryRenameTransformation implements PatchTransformation {

    /**
     * Default constructor
     */
    public ListEntryRenameTransformation() {
    }

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String key = options.get("key");

        final var list = config.getStringList(key);
        if (list.isEmpty()) {
            return;
        }

        final String regex = options.get("oldEntryRegex");
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        int index = 0;
        for (final String entry : list) {
            final Matcher matcher = pattern.matcher(entry);
            if (matcher.matches()) {
                break;
            }
            index++;
            if (index == list.size()) {
                return;
            }

        }

        final String newEntry = options.get("newEntry");
        list.set(index, newEntry);
        config.set(key, list);
    }
}
