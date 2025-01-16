package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renames an entry in a list.
 */
public class ListEntryRenameTransformer implements PatchTransformer {

    /**
     * Default constructor.
     */
    public ListEntryRenameTransformer() {
    }

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) throws PatchException {
        final String key = options.get("key");
        final String regex = options.get("oldEntryRegex");
        final String newEntry = options.get("newEntry");

        final List<String> list = config.getStringList(key);
        final boolean listExists = config.isList(key);

        final AtomicBoolean match = new AtomicBoolean(false);
        final Pattern pattern = Pattern.compile(regex);
        list.replaceAll(entry -> {
            final Matcher matcher = pattern.matcher(entry);
            if (matcher.matches()) {
                match.set(true);
                return newEntry;
            }
            return entry;
        });

        config.set(key, list);

        if (!listExists) {
            throw new PatchException("List '" + key + "' did not exist, so an empty list was created.");
        }
        if (!match.get()) {
            throw new PatchException("Tried to rename '%s' with '%s' but there was no such element in the list '%s'."
                    .formatted(regex, newEntry, key));
        }
    }
}
