package org.betonquest.betonquest.modules.config.patchTransformers;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

public class ListEntryRemoveTransformation implements PatchTransformation {

    @Override
    public void transform(final Map<String, String> options, final ConfigurationSection config) {
        final String targetList = options.get("key");
        final String deleteEntry = options.get("entry");

        final List<?> list = config.getList(targetList);
        if (list == null) {
            //TODO: Exception?
            return;
        }
        list.remove(deleteEntry);
    }
}
