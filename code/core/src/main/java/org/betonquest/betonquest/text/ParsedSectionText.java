package org.betonquest.betonquest.text;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.parser.StringParser;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * Text loaded from a Configuration Section identified by languages.
 */
public class ParsedSectionText extends ParsedText {

    /**
     * Loads value(s) from a key in a section, potentially identified by a language key.
     * When there is no section, the value will be identified by the default language.
     *
     * @param placeholders      the {@link Placeholders} to create and resolve placeholders
     * @param textParser        the text parser to parse the text
     * @param playerDataStorage the player data storage to get the player's language
     * @param pack              the pack to resolve placeholders
     * @param section           the section to load from
     * @param path              where the value(s) are stored in the section
     * @param languageProvider  the language provider to get the default language
     * @throws QuestException if there is no value, the default language is missing or the section format is invalid
     */
    public ParsedSectionText(final Placeholders placeholders, final TextParser textParser,
                             final PlayerDataStorage playerDataStorage, final QuestPackage pack,
                             final ConfigurationSection section, final String path,
                             final LanguageProvider languageProvider) throws QuestException {
        super(textParser, parse(placeholders, pack, section, path, languageProvider), playerDataStorage, languageProvider);
    }

    private static Map<String, Argument<String>> parse(final Placeholders placeholders, final QuestPackage pack,
                                                       final ConfigurationSection section, final String path,
                                                       final LanguageProvider languageProvider) throws QuestException {
        final StringParser stringParser = new StringParser();
        if (section.isConfigurationSection(path)) {
            return parseSection(placeholders, pack, section, path, stringParser);
        }
        if (section.isList(path)) {
            return Map.of(languageProvider.getDefaultLanguage(), new DefaultArgument<>(placeholders, pack,
                    String.join("\n", section.getStringList(path)), stringParser));
        }
        if (section.isString(path)) {
            final String raw = section.getString(path);
            if (raw == null) {
                throw new QuestException("No string value for '" + path + "'!");
            }
            return Map.of(languageProvider.getDefaultLanguage(), new DefaultArgument<>(placeholders, pack, raw, stringParser));
        }
        throw new QuestException("The '" + path + "' is missing!");
    }

    private static Map<String, Argument<String>> parseSection(final Placeholders placeholders, final QuestPackage pack,
                                                              final ConfigurationSection textSection, final String path,
                                                              final StringParser stringParser) throws QuestException {
        final ConfigurationSection subSection = textSection.getConfigurationSection(path);
        if (subSection == null) {
            throw new QuestException("No configuration section for '" + path + "'!");
        }
        final Map<String, Argument<String>> texts = new HashMap<>();
        for (final String key : subSection.getKeys(false)) {
            if (subSection.isList(key)) {
                texts.put(key, new DefaultArgument<>(placeholders, pack,
                        String.join("\n", subSection.getStringList(key)), stringParser));
                continue;
            }
            final String raw = subSection.getString(key);
            if (raw == null) {
                throw new QuestException("No string value for key '" + key + "'!");
            }
            texts.put(key, new DefaultArgument<>(placeholders, pack, raw, stringParser));
        }
        if (texts.isEmpty()) {
            throw new QuestException("No values defined!");
        }
        return texts;
    }
}
