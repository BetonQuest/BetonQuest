package org.betonquest.betonquest.message;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * Messages loaded from a Configuration Section identified by languages.
 */
public class ParsedSectionMessage extends ParsedMessage {

    /**
     * Loads value(s) from a key in a section, potentially identified by a language key.
     * When there is no section, the value will be identified by the default language.
     *
     * @param variableProcessor the variable processor to create new variables
     * @param messageParser     the message parser to parse the message
     * @param playerDataStorage the player data storage to get the player's language
     * @param pack              the pack to resolve variables
     * @param section           the section to load from
     * @param path              where the value(s) are stored in the section
     * @param languageProvider  the language provider to get the default language
     * @throws QuestException if there is no value, the default language is missing or the section format is invalid
     */
    public ParsedSectionMessage(final VariableProcessor variableProcessor, final MessageParser messageParser,
                                final PlayerDataStorage playerDataStorage, final QuestPackage pack,
                                final ConfigurationSection section, final String path,
                                final LanguageProvider languageProvider) throws QuestException {
        super(messageParser, parse(variableProcessor, pack, section, path, languageProvider), playerDataStorage, languageProvider);
    }

    private static Map<String, Variable<String>> parse(final VariableProcessor variableProcessor, final QuestPackage pack,
                                                       final ConfigurationSection section, final String path,
                                                       final LanguageProvider languageProvider) throws QuestException {
        if (section.isConfigurationSection(path)) {
            return parseSection(variableProcessor, pack, section, path);
        } else if (section.isList(path)) {
            return Map.of(languageProvider.getDefaultLanguage(), new Variable<>(variableProcessor, pack,
                    String.join("\n", section.getStringList(path)), Argument.STRING));
        } else if (section.isString(path)) {
            final String raw = section.getString(path);
            if (raw == null) {
                throw new QuestException("No string value for '" + path + "'!");
            }
            return Map.of(languageProvider.getDefaultLanguage(), new Variable<>(variableProcessor, pack, raw, Argument.STRING));
        } else {
            throw new QuestException("The '" + path + "' is missing!");
        }
    }

    private static Map<String, Variable<String>> parseSection(final VariableProcessor variableProcessor, final QuestPackage pack,
                                                              final ConfigurationSection messageSection, final String path) throws QuestException {
        final ConfigurationSection subSection = messageSection.getConfigurationSection(path);
        if (subSection == null) {
            throw new QuestException("No configuration section for '" + path + "'!");
        }
        final Map<String, Variable<String>> messages = new HashMap<>();
        for (final String key : subSection.getKeys(false)) {
            if (subSection.isList(key)) {
                messages.put(key, new Variable<>(variableProcessor, pack,
                        String.join("\n", subSection.getStringList(key)), Argument.STRING));
                continue;
            }
            final String raw = subSection.getString(key);
            if (raw == null) {
                throw new QuestException("No string value for key '" + key + "'!");
            }
            messages.put(key, new Variable<>(variableProcessor, pack, raw, Argument.STRING));
        }
        if (messages.isEmpty()) {
            throw new QuestException("No values defined!");
        }
        return messages;
    }
}
