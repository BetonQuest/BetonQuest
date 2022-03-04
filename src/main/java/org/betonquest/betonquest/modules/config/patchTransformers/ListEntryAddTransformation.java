package org.betonquest.betonquest.modules.config.patchTransformers;

import lombok.CustomLog;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@CustomLog
public class ListEntryAddTransformation implements PatchTransformation {

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String key = options.get("key");
        final String entry = options.get("entry");
        String position = options.get("position");
        if (position == null) {
            position = "LAST";
        }
        position = position.toUpperCase(Locale.ROOT);

        final List<String> list = config.getStringList(key);

        switch (position) {
            case "LAST" -> {
                list.add(entry);
                config.set(key, list);
            }
            case "FIRST" -> {
                final List<String> newList = new ArrayList<>();
                newList.add(entry);
                newList.addAll(list);
                config.set(key, newList);
            }
            default -> LOG.warn("Invalid position");
        }
    }
}
