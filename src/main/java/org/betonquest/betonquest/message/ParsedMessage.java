package org.betonquest.betonquest.message;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.variable.VariableString;

import java.util.Collections;
import java.util.Map;

public class ParsedMessage implements Message {

    private final MessageParser parser;

    private final Map<String, VariableString> messages;

    private final PlayerDataStorage dataStorage;

    public ParsedMessage(final MessageParser parser, final Map<String, VariableString> messages, final PlayerDataStorage dataStorage) throws QuestException {
        this.parser = parser;
        this.messages = messages;
        this.dataStorage = dataStorage;
        if (!messages.containsKey(Config.getLanguage())) {
            throw new QuestException("No message in default language defined.");
        }
    }

    public ParsedMessage(final MessageParser parser, final VariableString message, final PlayerDataStorage dataStorage) throws QuestException {
        this(parser, Collections.singletonMap(Config.getLanguage(), message), dataStorage);
    }

    @Override
    public Component asComponent(final Profile profile) throws QuestException {
        final String language = dataStorage.get(profile).getLanguage();
        final VariableString message;
        if (messages.containsKey(language)) {
            message = messages.get(language);
        } else {
            message = messages.get(Config.getLanguage());
        }
        if (message == null) {
            throw new QuestException("No message in language " + language + " defined.");
        }
        return parser.parse(message.getValue(profile));
    }
}
