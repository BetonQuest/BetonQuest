package org.betonquest.betonquest.config.patcher.migration.migrators.from2to3;

import org.betonquest.betonquest.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.config.patcher.migration.Migration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the aura_skills rename migration.
 */
public class LanguageRename implements Migration {
    /**
     * The config producer.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new aura_skills migrator.
     *
     * @param provider The config provider
     */
    public LanguageRename(final FileConfigurationProvider provider) {
        this.producer = provider;
    }

    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    @Override
    public void migrate() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();
            final Map<String, String> languages = new HashMap<>();
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

            boolean replaced = false;
            for (final Map.Entry<String, String> language : languages.entrySet()) {
                replaced |= replaceValueInSection(config, "events", "notify", "{" + language.getKey() + "}", "{" + language.getValue() + "}");
                replaced |= replaceValueInSection(config, "events", "notifyAll", "{" + language.getKey() + "}", "{" + language.getValue() + "}");
            }

            replaced |= renameLanguageKeys(config, languages, "conversations", "*", "quester");
            replaced |= renameLanguageKeys(config, languages, "conversations", "*", "NPC_options", "*", "text");
            replaced |= renameLanguageKeys(config, languages, "conversations", "*", "player_options", "*", "text");
            replaced |= renameLanguageKeys(config, languages, "compass", "*", "name");
            replaced |= renameLanguageKeys(config, languages, "cancel", "*", "name");
            replaced |= renameLanguageKeys(config, languages, "journal", "*");
            replaced |= renameLanguageKeys(config, languages, "journal_main_page", "*", "text");
            replaced |= renameLanguageKeys(config, languages, "menus", "*", "items", "*", "text");

            if (replaced) {
                config.save(file);
            }
        }
    }

    private boolean renameLanguageKeys(final YamlConfiguration config, final Map<String, String> languages, final String... selection) {
        boolean replaced = false;
        for (final String key : config.getKeys(true)) {
            final String[] split = key.split("\\.");
            if (split.length - 1 != selection.length || !keyMatchesSelection(split, selection)) {
                continue;
            }
            final Object value = config.get(key);
            if (value == null) {
                continue;
            }
            final String newLanguage = languages.get(split[split.length - 1]);
            if (newLanguage == null) {
                continue;
            }
            split[split.length - 1] = newLanguage;
            final String newKey = String.join(".", split);
            config.set(newKey, value);
            config.set(key, null);
            replaced = true;
        }
        return replaced;
    }

    @SuppressWarnings("PMD.UseVarargs")
    private boolean keyMatchesSelection(final String[] split, final String[] selection) {
        for (int i = 0; i < selection.length - 1; i++) {
            final String select = selection[i];
            if (!("*".equals(select) || split[i].equals(select))) {
                return false;
            }
        }
        return true;
    }
}
