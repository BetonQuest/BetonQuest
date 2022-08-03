package org.betonquest.betonquest.modules.config.transformers;

import lombok.CustomLog;
import org.betonquest.betonquest.modules.config.PatchException;
import org.betonquest.betonquest.modules.config.PatchTransformation;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

/**
 * Adds an entry to the given list at the given position.
 */
@CustomLog
public class ListEntryAddTransformation implements PatchTransformation {

    /**
     * Default constructor
     */
    public ListEntryAddTransformation() {
    }

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) throws PatchException {
        final String key = options.get("key");
        final String entry = options.get("entry");
        final String position = options.getOrDefault("position", "LAST");

        final List<String> list = config.getStringList(key);
        final boolean listExists = config.isList(key);

        final int index = "LAST".equalsIgnoreCase(position) ? list.size() : 0;
        list.add(index, entry);

        config.set(key, list);

        if (!listExists) {
            throw new PatchException("List '" + key + "' did not exist, so it was created.");
        }
    }
}
