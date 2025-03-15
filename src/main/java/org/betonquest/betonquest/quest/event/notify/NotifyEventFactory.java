package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.message.ParsedMessage;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory for {@link NotifyEvent}.
 */
public class NotifyEventFactory implements EventFactory {
    /**
     * A pattern for the notation of notifyIO options.
     */
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(?<key>[a-zA-Z]+?):(?<value>\\S+)");

    /**
     * A pattern for the notation of multiple translations in a single event.
     */
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("\\{(?<lang>[a-z-]{2,5})}\\s(?<message>.*?)(?=\\s+\\{[a-z-]{2,5}}\\s|$)");

    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * The {@link MessageParser} to use for parsing messages.
     */
    private final MessageParser messageParser;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Creates a new factory for {@link NotifyEvent}.
     *
     * @param loggerFactory     the logger factory to use for creating the event logger
     * @param data              the data for primary server thread access
     * @param messageParser     the message parser to use for parsing messages
     * @param playerDataStorage the storage providing player data
     */
    public NotifyEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data,
                              final MessageParser messageParser, final PlayerDataStorage playerDataStorage) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.messageParser = messageParser;
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final String rawInstruction = String.join(" ", instruction.getValueParts());
        final Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(rawInstruction);

        final Message message = getMessage(instruction, keyValueMatcher, rawInstruction);
        final NotifyIO notifyIO = processInstruction(instruction, keyValueMatcher);

        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new NotifyEvent(notifyIO, message),
                loggerFactory.create(NotifyEvent.class),
                instruction.getPackage()
        ), data);
    }

    private Message getMessage(final Instruction instruction, final Matcher keyValueMatcher, final String rawInstruction) throws QuestException {
        final int indexEnd = keyValueMatcher.find() ? keyValueMatcher.start() : rawInstruction.length();
        keyValueMatcher.reset();
        final String langMessages = rawInstruction.substring(0, indexEnd);
        return getLanguages(instruction, langMessages);
    }

    private NotifyIO processInstruction(final Instruction instruction, final Matcher keyValueMatcher) {
        final Map<String, String> data = getData(keyValueMatcher);
        final String category = data.remove("category");
        return Notify.get(instruction.getPackage(), category, data);
    }

    private Message getLanguages(final Instruction instruction, final String messages) throws QuestException {
        final Map<String, VariableString> translations = new HashMap<>();
        final Matcher languageMatcher = LANGUAGE_PATTERN.matcher(messages);

        while (languageMatcher.find()) {
            final String lang = languageMatcher.group("lang");
            final String message = languageMatcher.group("message")
                    .replace("\\{", "{")
                    .replace("\\:", ":");
            translations.put(lang, instruction.get(message, VariableString::new));
        }

        final String defaultLanguageKey = Config.getLanguage();
        if (translations.isEmpty()) {
            final String message = messages
                    .replace("\\{", "{")
                    .replace("\\:", ":");
            translations.put(defaultLanguageKey, instruction.get(message, VariableString::new));
        }
        if (!translations.containsKey(defaultLanguageKey)) {
            throw new QuestException("No message defined for default language '" + defaultLanguageKey + "'!");
        }
        return new ParsedMessage(messageParser, translations, playerDataStorage);
    }

    private Map<String, String> getData(final Matcher keyValueMatcher) {
        final Map<String, String> data = new HashMap<>();

        while (keyValueMatcher.find()) {
            final String key = keyValueMatcher.group("key");
            final String value = keyValueMatcher.group("value");
            data.put(key, value);
        }

        data.remove("events");
        data.remove("conditions");

        return data;
    }
}
