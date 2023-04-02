package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

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
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("\\{(?<lang>[a-z-]{2,5})} (?<message>.*?)(?= \\{[a-z-]{2,5}} |$)");

    /**
     * Server to use for syncing to the primary server thread.
     */
    private final Server server;
    /**
     * Scheduler to use for syncing to the primary server thread.
     */
    private final BukkitScheduler scheduler;
    /**
     * Plugin to use for syncing to the primary server thread.
     */
    private final Plugin plugin;

    /**
     * Creates a new factory for {@link NotifyEvent}.
     *
     * @param server    Server to use for syncing to the primary server thread.
     * @param scheduler Scheduler to use for syncing to the primary server thread.
     * @param plugin    Plugin to use for syncing to the primary server thread.
     */
    public NotifyEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final HashMap<String, String> data = new HashMap<>();
        final Map<String, VariableString> translations = new HashMap<>();

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
        final NotifyIO notifyIO = Notify.get(instruction.getPackage(), category, data);
        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        new NotifyEvent(notifyIO, translations),
                        instruction.getPackage()),
                server, scheduler, plugin
        );
    }
}
