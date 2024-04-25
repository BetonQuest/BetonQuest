package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Factory for {@link NotifyEvent}.
 */
public class NotifyEventFactory implements EventFactory {
    /**
     * A pattern for the notation of multiple translations in a single event.
     */
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("\\{(?<lang>[a-z-]{2,5})} (?<message>.*?)(?= \\{[a-z-]{2,5}} |$)");

    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

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
     * @param loggerFactory Logger factory to use for creating the event logger.
     * @param server        Server to use for syncing to the primary server thread.
     * @param scheduler     Scheduler to use for syncing to the primary server thread.
     * @param plugin        Plugin to use for syncing to the primary server thread.
     */
    public NotifyEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final Map<String, VariableString> translations = new HashMap<>();
        final NotifyIO notifyIO = processInstruction(instruction, translations);
        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        loggerFactory.create(NotifyEvent.class), new NotifyEvent(notifyIO, translations),
                        instruction.getPackage()),
                server, scheduler, plugin
        );
    }

    /**
     * Processes the instruction and extracts the translations and returns the notifyIO to use.
     *
     * @param instruction  the instruction to process
     * @param translations the map to put the translations into
     * @return the notifyIO to use
     * @throws InstructionParseException if the instruction is invalid
     */
    protected NotifyIO processInstruction(final Instruction instruction, final Map<String, VariableString> translations) throws InstructionParseException {
        final String langMessage = Arrays.stream(instruction.getRequiredArguments())
                .skip(1).collect(Collectors.joining(" "));
        translations.putAll(getLanguages(instruction.getPackage(), langMessage));
        final Map<String, String> data = instruction.getOptionalArguments();
        data.remove("events");
        data.remove("conditions");
        return Notify.get(instruction.getPackage(), data.remove("category"), data);
    }

    private Map<String, VariableString> getLanguages(final QuestPackage pack, final String messages) throws InstructionParseException {
        final Map<String, VariableString> translations = new HashMap<>();
        final Matcher languageMatcher = LANGUAGE_PATTERN.matcher(messages);

        while (languageMatcher.find()) {
            final String lang = languageMatcher.group("lang");
            final String message = languageMatcher.group("message")
                    .replace("\\{", "{")
                    .replace("\\:", ":");
            translations.put(lang, new VariableString(pack, message));
        }

        final String defaultLanguageKey = Config.getLanguage();
        if (translations.isEmpty()) {
            final String message = messages
                    .replace("\\{", "{")
                    .replace("\\:", ":");
            translations.put(defaultLanguageKey, new VariableString(pack, message));
        }
        if (!translations.containsKey(defaultLanguageKey)) {
            throw new InstructionParseException("No message defined for default language '" + defaultLanguageKey + "'!");
        }
        return translations;
    }

}
