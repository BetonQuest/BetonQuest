package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.modules.config.patcher.migration.Migration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Handles the aura_skills rename migration.
 */
public class VariablesRename implements Migration {
    /**
     * The config producer.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new aura_skills migrator.
     *
     * @param provider The config provider
     */
    public VariablesRename(final FileConfigurationProvider provider) {
        this.producer = provider;
    }

    @Override
    public void migrate() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
        if (renameSection(configs)) {
            renameVariables(configs);
        }
    }

    private boolean renameSection(final Map<File, YamlConfiguration> configs) throws IOException {
        boolean found = false;
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();
            final ConfigurationSection variables = config.getConfigurationSection("variables");
            if (variables != null) {
                config.set("constants", variables);
                config.set("variables", null);
                config.save(file);
                found = true;
            }
        }
        return found;
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
                        if (value.contains("$")) {
                            stringList.set(i, replaceGlobalVariables(value));
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
                if (value != null && value.contains("$")) {
                    config.set(key, replaceGlobalVariables(value));
                    needSave = true;
                }
            }
            if (needSave) {
                config.save(file);
            }
        }
    }

    private String replaceGlobalVariables(final String input) {
        final String regex = "(?<!\\\\|^)\\$(.*?)(?<!\\\\|^)\\$";
        final String replacement = "%constant.$1%";
        return input.replaceAll(regex, replacement);
    }
}
