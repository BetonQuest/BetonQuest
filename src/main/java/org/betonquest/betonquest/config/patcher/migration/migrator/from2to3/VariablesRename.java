package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;

/**
 * Handles the variables rename migration.
 */
public class VariablesRename implements QuestMigration {

    /**
     * Creates a new variables rename migrator.
     */
    public VariablesRename() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final MultiConfiguration config = quest.getQuestConfig();
        renameSection(config);
        renameVariables(config);
    }

    private void renameSection(final MultiConfiguration config) throws InvalidConfigurationException {
        final ConfigurationSection variables = config.getConfigurationSection("variables");
        if (variables == null) {
            return;
        }
        final ConfigurationSection constants = config.createSection("constants");
        for (final String key : variables.getKeys(false)) {
            final ConfigurationSection source = config.getSourceConfigurationSection("variables." + key);
            if (source == null) {
                throw new InvalidConfigurationException("Cannot migrate variables to constants for key: " + key);
            }
            constants.set(key, variables.get(key));
            config.associateWith("constants." + key, source);
        }
        config.set("variables", null);
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private void renameVariables(final MultiConfiguration config) {
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
                }
                continue;
            }
            final String value = config.getString(key);
            if (value != null && value.contains("$")) {
                config.set(key, replaceGlobalVariables(value));
            }
        }
    }

    private String replaceGlobalVariables(final String input) {
        final String notEscapedDollar = "(?<!\\\\)\\$";
        final String regexWithPackage = notEscapedDollar + "(.*)\\.(.*)" + notEscapedDollar;
        final String regexWithoutPackage = notEscapedDollar + "(.*)" + notEscapedDollar;
        final String replacementWithPackage = "%$1.constant.$2%";
        final String replacementWithoutPackage = "%constant.$1%";
        final String result = input.replaceAll(regexWithPackage, replacementWithPackage).replaceAll(regexWithoutPackage, replacementWithoutPackage);
        return result.replaceAll("\\\\\\$", "\\$");
    }
}
