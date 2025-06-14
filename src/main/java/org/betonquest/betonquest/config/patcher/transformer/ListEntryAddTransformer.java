package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.betonquest.betonquest.config.patcher.PatcherOptions;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Locale;

/**
 * Adds an entry to the given list at the given position.
 */
public class ListEntryAddTransformer implements PatchTransformer {

    /**
     * Default constructor.
     */
    public ListEntryAddTransformer() {
    }

    @Override
    public void transform(final PatcherOptions options, final ConfigurationSection config) throws PatchException {
        final String key = options.getString("key");
        final String entry = options.getString("entry");
        final String position = options.getString("position", "LAST");

        final List<String> list = config.getStringList(key);
        final boolean listExists = config.isList(key);

        final int index = switch (position.toUpperCase(Locale.ROOT)) {
            case "FIRST" -> 0;
            case "LAST" -> list.size();
            default -> list.size();
        };

        list.add(index, entry);
        config.set(key, list);

        if (!listExists) {
            throw new PatchException("List '" + key + "' did not exist, so it was created.");
        }
    }
}
