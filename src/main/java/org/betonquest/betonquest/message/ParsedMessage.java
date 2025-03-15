package org.betonquest.betonquest.message;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

/**
 * A message that is parsed using a message parser.
 */
public class ParsedMessage implements Message {
    /**
     * The message parser to use for parsing messages.
     */
    private final MessageParser parser;

    /**
     * The messages to use for each language.
     */
    private final Map<String, VariableString> messages;

    /**
     * The data storage to use for getting the player's language.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Constructs a new parsed message with messages in multiple languages.
     *
     * @param parser      the message parser to use
     * @param messages    the messages to use for each language
     * @param dataStorage the data storage to use for getting the player's language
     * @throws QuestException if an error occurs while constructing the message
     */
    public ParsedMessage(final MessageParser parser, final Map<String, VariableString> messages, final PlayerDataStorage dataStorage) throws QuestException {
        this.parser = parser;
        this.messages = messages;
        this.dataStorage = dataStorage;
        if (!messages.containsKey(Config.getLanguage())) {
            throw new QuestException("No message in default language defined.");
        }
    }

    /**
     * Constructs a new parsed message with a single message in the default language.
     *
     * @param parser      the message parser to use
     * @param message     the message to parse
     * @param dataStorage the data storage to use for getting the player's language
     * @throws QuestException if an error occurs while constructing the message
     */
    public ParsedMessage(final MessageParser parser, final VariableString message, final PlayerDataStorage dataStorage) throws QuestException {
        this(parser, Collections.singletonMap(Config.getLanguage(), message), dataStorage);
    }

    @Override
    public Component asComponent(@Nullable final Profile profile) throws QuestException {
        String language = null;
        VariableString message = null;
        if (profile != null) {
            language = dataStorage.get(profile).getLanguage();
            message = messages.get(language);
        }
        if (message == null) {
            language = Config.getLanguage();
            message = messages.get(language);
        }
        if (message == null) {
            throw new QuestException("No message in language " + language + " defined.");
        }
        return parser.parse(message.getValue(profile));
    }
}
