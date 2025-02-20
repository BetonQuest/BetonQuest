package org.betonquest.betonquest.message;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Messages loaded from a Configuration Section identified by languages.
 */
public class ParsedSectionMessage {
    /**
     * The default language.
     */
    private final String defaultLanguage = Config.getLanguage();

    /**
     * Maps the language to the message.
     */
    private final Map<String, VariableString> messages;

    /**
     * Loads value(s) from a key in a section, potentially identified by a language key.
     * When there is no section the value will be identified by the default language.
     *
     * @param variableProcessor the variable processor to create new variables
     * @param pack              the pack to resolve variables
     * @param section           the section to load from
     * @param path              where the value(s) are stored in the section
     * @throws QuestException if there is no value, the default language is missing or the section format is invalid
     */
    public ParsedSectionMessage(final VariableProcessor variableProcessor, final QuestPackage pack,
                                final ConfigurationSection section, final String path) throws QuestException {
        if (section.isConfigurationSection(path)) {
            messages = parseSection(variableProcessor, pack, section, path);
            if (!messages.containsKey(defaultLanguage)) {
                throw new QuestException("No message for default language!");
            }
        } else if (section.isString(path)) {
            final String raw = GlobalVariableResolver.resolve(pack, section.getString(path));
            if (raw == null) {
                throw new QuestException("No string value for '" + path + "'!");
            }
            messages = Map.of(defaultLanguage, new VariableString(variableProcessor, pack, raw));
        } else {
            throw new QuestException("The '" + path + "' is missing!");
        }
    }

    private Map<String, VariableString> parseSection(final VariableProcessor variableProcessor, final QuestPackage pack,
                                                     final ConfigurationSection section, final String path) throws QuestException {
        final ConfigurationSection subSection = section.getConfigurationSection(path);
        if (subSection == null) {
            throw new QuestException("No configuration section for '" + path + "'!");
        }
        final Map<String, VariableString> messages = new HashMap<>();
        for (final String key : subSection.getKeys(false)) {
            final String raw = GlobalVariableResolver.resolve(pack, subSection.getString(key));
            if (raw == null) {
                throw new QuestException("No string value for key '" + key + "'!");
            }
            messages.put(key, new VariableString(variableProcessor, pack, raw));
        }
        if (messages.isEmpty()) {
            throw new QuestException("No values defined!");
        }
        return messages;
    }

    /**
     * Resolves the message in the language or default language.
     *
     * @param language the preferred language to get the message
     * @param profile  the profile to resolve the variable
     * @return the resolved message for the language
     * @throws QuestException when there is no message for the requested language and default language
     *                        or the variable can't be resolved
     */
    public String getResolved(final String language, @Nullable final Profile profile) throws QuestException {
        return get(language).getValue(profile);
    }

    /**
     * Returns the message in the language or default language.
     *
     * @param language the preferred language to get the message
     * @return the message for the language
     * @throws QuestException when there is no message for the requested language and default language
     */
    public VariableString get(final String language) throws QuestException {
        VariableString name = messages.get(language);
        if (name != null) {
            return name;
        }
        name = messages.get(defaultLanguage);
        if (name != null) {
            return name;
        }
        throw new QuestException("No text for language '" + language + "' or default!");
    }
}
