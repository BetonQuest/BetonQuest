package org.betonquest.betonquest.modules.config.transformer;

import lombok.CustomLog;
import org.betonquest.betonquest.modules.config.PatchException;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Adds an entry to the given list at the given position.
 */
@CustomLog
@SuppressWarnings({"PMD.AvoidLiteralsInIfCondition"})
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
        String position = options.get("position");
        if (position == null) {
            position = "LAST";
        }
        position = position.toUpperCase(Locale.ROOT);

        final List<String> list = config.getStringList(key);
        final boolean listIsEmpty = list.isEmpty();

        if ("LAST".equals(position) || listIsEmpty) {
            list.add(entry);
            config.set(key, list);
        } else if ("FIRST".equals(position)) {
            final List<String> newList = new ArrayList<>();
            newList.add(entry);
            newList.addAll(list);
            config.set(key, newList);
        }

        if (listIsEmpty) {
            throw new PatchException("List '" + key + "' did not exist, so it was created.");
        }
    }
}
