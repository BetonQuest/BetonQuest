package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.text.ParsedText;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.betonquest.betonquest.config.PluginMessage.LANG_KEY;

/**
 * Factory for {@link NotifyEvent}.
 */
public class NotifyEventFactory implements PlayerEventFactory {

    /**
     * A pattern for the notation of notifyIO options.
     */
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(?<key>[a-zA-Z]+?):(?<value>\\S+)");

    /**
     * A pattern for the notation of multiple translations in a single event.
     */
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("\\{(?<lang>" + LANG_KEY + ")}\\s(?<text>.*?)(?=\\s+\\{" + LANG_KEY + "}\\s|$)");

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * The {@link TextParser} to use for parsing text.
     */
    private final TextParser textParser;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The language provider to get the default language.
     */
    private final LanguageProvider languageProvider;

    /**
     * Creates a new factory for {@link NotifyEvent}.
     *
     * @param loggerFactory     the logger factory to create a logger for the events
     * @param data              the data for primary server thread access
     * @param textParser        the text parser to use for parsing text
     * @param playerDataStorage the storage providing player data
     * @param languageProvider  the language provider to get the default language
     */
    public NotifyEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data,
                              final TextParser textParser, final PlayerDataStorage playerDataStorage,
                              final LanguageProvider languageProvider) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.textParser = textParser;
        this.playerDataStorage = playerDataStorage;
        this.languageProvider = languageProvider;
    }

    @Override
    public PlayerEvent parsePlayer(final DefaultInstruction instruction) throws QuestException {
        final String rawInstruction = String.join(" ", instruction.getValueParts());
        final Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(rawInstruction);

        final Text text = getText(instruction, keyValueMatcher, rawInstruction);
        final NotifyIO notifyIO = processInstruction(instruction, keyValueMatcher);

        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new NotifyEvent(notifyIO, text),
                loggerFactory.create(NotifyEvent.class),
                instruction.getPackage()
        ), data);
    }

    private Text getText(final DefaultInstruction instruction, final Matcher keyValueMatcher, final String rawInstruction) throws QuestException {
        final int indexEnd = keyValueMatcher.find() ? keyValueMatcher.start() : rawInstruction.length();
        keyValueMatcher.reset();
        final String langTexts = rawInstruction.substring(0, indexEnd);
        return getLanguages(instruction, langTexts);
    }

    private NotifyIO processInstruction(final DefaultInstruction instruction, final Matcher keyValueMatcher) throws QuestException {
        final Map<String, String> data = getData(keyValueMatcher);
        final String category = data.remove("category");
        return Notify.get(instruction.getPackage(), category, data);
    }

    private Text getLanguages(final DefaultInstruction instruction, final String texts) throws QuestException {
        final Map<String, Variable<String>> translations = new HashMap<>();
        final Matcher languageMatcher = LANGUAGE_PATTERN.matcher(texts);

        while (languageMatcher.find()) {
            final String lang = languageMatcher.group("lang");
            final String text = languageMatcher.group("text")
                    .replace("\\{", "{")
                    .replace("\\:", ":");
            translations.put(lang, instruction.get(text, Argument.STRING));
        }

        final String defaultLanguageKey = languageProvider.getDefaultLanguage();
        if (translations.isEmpty()) {
            final String text = texts
                    .replace("\\{", "{")
                    .replace("\\:", ":");
            translations.put(defaultLanguageKey, instruction.get(text, Argument.STRING));
        }
        if (!translations.containsKey(defaultLanguageKey)) {
            throw new QuestException("No text defined for default language '" + defaultLanguageKey + "'!");
        }
        return new ParsedText(textParser, translations, playerDataStorage, languageProvider);
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
