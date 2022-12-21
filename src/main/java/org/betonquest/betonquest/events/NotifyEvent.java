package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.notify.NotifyIO;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sends a notification to the profile's player using BetonQuest's notification system.
 */
public class NotifyEvent extends QuestEvent {

    /**
     * A pattern for the notation of notifyIO options.
     */
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(?<key>[a-zA-Z]+?):(?<value>\\S+)");
    /**
     * A pattern for the notation of multiple translations in a single event.
     */
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("\\{(?<lang>[a-z-]{2,5})} (?<message>.*?)(?= \\{[a-z-]{2,5}} |$)");
    /**
     * This map contains language codes as keys and messages as values. Since the messages may include variables, they
     * are stored as VariableString objects.
     */
    private final Map<String, VariableString> translations = new HashMap<>();
    /**
     * The name of the notifyIO to use.
     */
    private final NotifyIO notifyIO;


    /**
     * Creates a new notify event.
     *
     * @param instruction the instructions to follow for this event
     * @throws InstructionParseException if the instruction is malformed
     */
    public NotifyEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        final HashMap<String, String> data = new HashMap<>();

        final String rawInstruction = instruction.getInstruction();
        final Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(rawInstruction);

        final int indexStart = rawInstruction.indexOf(' ') + 1;
        if (indexStart != 0) {
            final int indexEnd = keyValueMatcher.find() ? keyValueMatcher.start() : rawInstruction.length();
            keyValueMatcher.reset();

            final String langMessages = rawInstruction.substring(indexStart, indexEnd);

            final Matcher languageMatcher = LANGUAGE_PATTERN.matcher(langMessages);
            while (languageMatcher.find()) {
                final String lang = languageMatcher.group("lang");
                final String message = languageMatcher.group("message")
                        .replace("\\{", "{")
                        .replace("\\:", ":");
                translations.put(lang, new VariableString(instruction.getPackage(), message));
            }
            final String defaultLanguageKey = Config.getLanguage();
            if (translations.isEmpty()) {
                final String message = langMessages
                        .replace("\\{", "{")
                        .replace("\\:", ":");
                translations.put(defaultLanguageKey, new VariableString(instruction.getPackage(), message));
            }
            if (!translations.containsKey(defaultLanguageKey)) {
                throw new InstructionParseException("No message defined for default language '" + defaultLanguageKey + "'!");
            }

            while (keyValueMatcher.find()) {
                final String key = keyValueMatcher.group("key");
                final String value = keyValueMatcher.group("value");
                data.put(key, value);
            }
            data.remove("events");
            data.remove("conditions");
        }
        final String category = data.remove("category");
        notifyIO = Notify.get(instruction.getPackage(), category, data);
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final String playerLanguageKey = BetonQuest.getInstance().getPlayerData(profile).getLanguage();
        final String defaultLanguageKey = Config.getLanguage();

        final VariableString message = translations.containsKey(playerLanguageKey)
                ? translations.get(playerLanguageKey)
                : translations.get(defaultLanguageKey);
        notifyIO.sendNotify(message.getString(profile), profile.getOnlineProfile().get());
        return null;
    }
}
