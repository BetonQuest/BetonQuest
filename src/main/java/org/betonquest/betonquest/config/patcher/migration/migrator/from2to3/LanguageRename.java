package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the migration of language keys.
 */
public class LanguageRename implements QuestMigration {
    /**
     * Language keys to rename.
     */
    private final Map<String, String> languages;

    /**
     * Creates a new language rename migration.
     */
    public LanguageRename() {
        languages = new HashMap<>();
        languages.put("en", "en-US");
        languages.put("de", "de-DE");
        languages.put("es", "es-ES");
        languages.put("fr", "fr-FR");
        languages.put("hu", "hu-HU");
        languages.put("it", "it-IT");
        languages.put("nl", "nl-NL");
        languages.put("pl", "pl-PL");
        languages.put("pt-br", "pt-BR");
        languages.put("pt-pt", "pt-PT");
        languages.put("ru", "ru-RU");
        languages.put("vi", "vi-VN");
        languages.put("cn", "zh-CN");
    }

    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final MultiConfiguration config = quest.getQuestConfig();
        for (final Map.Entry<String, String> language : languages.entrySet()) {
            replaceValueInSection(config, "events", "notify", "{" + language.getKey() + "}", "{" + language.getValue() + "}");
            replaceValueInSection(config, "events", "notifyAll", "{" + language.getKey() + "}", "{" + language.getValue() + "}");
        }

        renameLanguageKeys(config, "conversations", "*", "quester");
        renameLanguageKeys(config, "conversations", "*", "NPC_options", "*", "text");
        renameLanguageKeys(config, "conversations", "*", "player_options", "*", "text");
        renameLanguageKeys(config, "compass", "*", "name");
        renameLanguageKeys(config, "cancel", "*", "name");
        renameLanguageKeys(config, "journal", "*");
        renameLanguageKeys(config, "journal_main_page", "*", "text");
        renameLanguageKeys(config, "menus", "*", "items", "*", "text");
    }

    private void renameLanguageKeys(final MultiConfiguration config, final String... selection) throws InvalidConfigurationException {
        for (final String key : config.getKeys(true)) {
            final String[] split = key.split("\\.");
            if (!keyMatchesSelection(split, selection)) {
                continue;
            }
            final Object value = config.get(key);
            if (value == null) {
                continue;
            }
            final String oldLanguage = split[split.length - 1];
            final String newLanguage = languages.get(oldLanguage);
            if (newLanguage == null) {
                continue;
            }
            split[split.length - 1] = newLanguage;
            final String path = key.substring(0, key.lastIndexOf('.'));
            final String newKey = String.join(".", split);
            final ConfigurationSection source = config.getSourceConfigurationSection(path);
            if (source == null) {
                throw new InvalidConfigurationException(path + " is not a valid source");
            }
            config.set(newKey, value);
            config.set(key, null);
            config.associateWith(source);
        }
    }

    @SuppressWarnings("PMD.UseVarargs")
    private boolean keyMatchesSelection(final String[] split, final String[] selection) {
        if (split.length - 1 != selection.length) {
            return false;
        }
        for (int i = 0; i < selection.length - 1; i++) {
            final String select = selection[i];
            if (!("*".equals(select) || split[i].equals(select))) {
                return false;
            }
        }
        return true;
    }
}
