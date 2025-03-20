package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.config.patcher.migration.Migration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Handles the npc variable rename to quester migration.
 */
public class NpcRename implements Migration {

    /**
     * The old variable value.
     */
    private static final String NPC = "%npc%";

    /**
     * The new variable value.
     */
    private static final String QUESTER = "%quester%";

    /**
     * The config producer.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new npc to quester variable migrator.
     *
     * @param provider The config provider
     */
    public NpcRename(final FileConfigurationProvider provider) {
        this.producer = provider;
    }

    @Override
    public void migrate() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
        renameVariables(configs);
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    private void renameVariables(final Map<File, YamlConfiguration> configs) throws IOException {
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();
            boolean needSave = false;
            for (final String key : config.getKeys(true)) {
                if (config.isConfigurationSection(key)) {
                    continue;
                }
                if (config.isList(key)) {
                    final List<String> stringList = config.getStringList(key);
                    boolean listChanged = false;
                    for (int i = 0; i < stringList.size(); i++) {
                        final String value = stringList.get(i);
                        if (value.contains(NPC)) {
                            stringList.set(i, value.replaceAll(NPC, QUESTER));
                            listChanged = true;
                        }
                    }
                    if (listChanged) {
                        config.set(key, stringList);
                        needSave = true;
                    }
                    continue;
                }
                final String value = config.getString(key);
                if (value != null && value.contains(NPC)) {
                    config.set(key, value.replaceAll(NPC, QUESTER));
                    needSave = true;
                }
            }
            if (needSave) {
                config.save(file);
            }
        }
    }
}
