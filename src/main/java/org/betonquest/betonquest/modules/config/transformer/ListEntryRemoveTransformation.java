package org.betonquest.betonquest.modules.config.transformer;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

/**
 * Removes an entry from a list.
 */
public class ListEntryRemoveTransformation implements PatchTransformation {

    /**
     * Default constructor
     */
    public ListEntryRemoveTransformation() {
    }

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String targetList = options.get("key");

        final List<?> list = config.getList(targetList);
        if (list == null) {
            return;
        }

        final String deleteEntry = options.get("entry");
        list.remove(deleteEntry);
    }
}
