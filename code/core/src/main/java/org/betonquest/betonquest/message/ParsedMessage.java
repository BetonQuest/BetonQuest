package org.betonquest.betonquest.message;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

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
    private final Map<String, Variable<String>> messages;

    /**
     * The data storage to use for getting the player's language.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The language provider to get the default language.
     */
    private final LanguageProvider languageProvider;

    /**
     * Constructs a new parsed message with messages in multiple languages.
     *
     * @param parser           the message parser to use
     * @param messages         the messages to use for each language
     * @param dataStorage      the data storage to use for getting the player's language
     * @param languageProvider the language provider to get the default language
     * @throws QuestException if an error occurs while constructing the message
     */
    public ParsedMessage(final MessageParser parser, final Map<String, Variable<String>> messages,
                         final PlayerDataStorage dataStorage, final LanguageProvider languageProvider) throws QuestException {
        this.parser = parser;
        this.messages = messages;
        this.dataStorage = dataStorage;
        this.languageProvider = languageProvider;
        if (!messages.containsKey(languageProvider.getDefaultLanguage())) {
            throw new QuestException("No message in default language defined.");
        }
    }

    @Override
    public Component asComponent(@Nullable final Profile profile) throws QuestException {
        String language = null;
        Variable<String> message = null;
        if (profile != null) {
            language = dataStorage.get(profile).getLanguage().orElseGet(languageProvider::getDefaultLanguage);
            message = messages.get(language);
        }
        if (message == null) {
            language = languageProvider.getDefaultLanguage();
            message = messages.get(language);
        }
        if (message == null) {
            throw new QuestException("No message in language " + language + " defined.");
        }
        return parser.parse(message.getValue(profile));
    }
}
