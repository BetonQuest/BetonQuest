package org.betonquest.betonquest.message;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Loads value(s) from a key in a section, potentially identified by a language key.
 * When there is no section, the value will be identified by the default language.
 */
public class ParsedSectionMessageCreator {

    /**
     * The Message parser.
     */
    protected final MessageParser messageParser;

    /**
     * Storage for Player Data to retrieve the language from a profile.
     */
    protected final PlayerDataStorage playerDataStorage;

    /**
     * Language Provider to retrieve the default language.
     */
    protected final LanguageProvider languageProvider;

    /**
     * The Variable Processor to create and resolve variables.
     */
    protected final VariableProcessor variableProcessor;

    /**
     * Creates a new Message Factory.
     *
     * @param variableProcessor the variable processor to create new variables
     * @param messageParser     the message parser to parse the message
     * @param playerDataStorage the player data storage to get the player's language
     * @param languageProvider  the language provider to get the default language
     */
    public ParsedSectionMessageCreator(final MessageParser messageParser, final PlayerDataStorage playerDataStorage,
                                       final LanguageProvider languageProvider, final VariableProcessor variableProcessor) {
        this.messageParser = messageParser;
        this.playerDataStorage = playerDataStorage;
        this.languageProvider = languageProvider;
        this.variableProcessor = variableProcessor;
    }

    /**
     * Loads value(s) from a key in a section, potentially identified by a language key.
     * When there is no section, the value will be identified by the default language.
     *
     * @param pack    the pack to resolve variables
     * @param section the section to load from
     * @param path    where the value(s) are stored in the section
     * @return the newly created message
     * @throws QuestException if there is no value, the default language is missing or the section format is invalid
     */
    public Message parseFromSection(final QuestPackage pack, final ConfigurationSection section, final String path)
            throws QuestException {
        return new ParsedSectionMessage(variableProcessor, messageParser, playerDataStorage,
                pack, section, path, languageProvider);
    }
}
