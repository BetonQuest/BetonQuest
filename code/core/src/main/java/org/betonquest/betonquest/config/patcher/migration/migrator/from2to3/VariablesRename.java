package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            constants.setComments(key, variables.getComments(key));
            constants.setInlineComments(key, variables.getInlineComments(key));
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

    @VisibleForTesting
    String replaceGlobalVariables(final String input) {
        final String result1 = replaceRegex(input, "%math\\.(.*?)%",
                math -> "%math." + replaceRegex(math, "(?<!\\\\)\\$(.*?)(?<!\\\\)\\$",
                        variable -> "{" + migrateVariable(variable) + "}") + "%");
        final String result2 = replaceRegex(result1, "(?<!\\\\)\\$(.*?)(?<!\\\\)\\$",
                variable -> "%" + migrateVariable(variable) + "%");

        return result2.replaceAll("\\\\\\$", "\\$");
    }

    private String replaceRegex(final String input, final String regex, final Function<String, String> transformation) {
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(input);

        final StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            final String variable = matcher.group(1);
            final String replacement = transformation.apply(variable);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String migrateVariable(final String variable) {
        if (!variable.contains(".")) {
            return "constant." + variable;
        }

        final int index = variable.indexOf('.');
        final String pkg = variable.substring(0, index);
        final String name = variable.substring(index + 1);
        return pkg + ".constant." + name;
    }
}
