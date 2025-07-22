package org.betonquest.betonquest.quest.event.folder;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.plugin.PluginManager;

import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Factory to create {@link FolderEvent} instances.
 */
public class FolderEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The plugin manager to register the quit listener.
     */
    private final PluginManager pluginManager;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Create a new folder event factory.
     *
     * @param betonQuest    the BetonQuest instance
     * @param loggerFactory the logger factory to create a logger for the events
     * @param pluginManager the plugin manager to register the quit listener
     * @param questTypeAPI  the Quest Type API
     */
    public FolderEventFactory(final BetonQuest betonQuest, final BetonQuestLoggerFactory loggerFactory,
                              final PluginManager pluginManager, final QuestTypeAPI questTypeAPI) {
        this.betonQuest = betonQuest;
        this.loggerFactory = loggerFactory;
        this.pluginManager = pluginManager;
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createFolderEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createFolderEvent(instruction);
    }

    private NullableEventAdapter createFolderEvent(final Instruction instruction) throws QuestException {
        final Variable<List<EventID>> events = instruction.getList(EventID::new);
        final Variable<Number> delay = instruction.getValue("delay", Argument.NUMBER);
        final Variable<Number> period = instruction.getValue("period", Argument.NUMBER);
        final Variable<Number> random = instruction.getValue("random", Argument.NUMBER);
        final Variable<TimeUnit> timeUnit = instruction.getValue("unit", this::getTimeUnit, TimeUnit.SECONDS);
        final boolean cancelOnLogout = instruction.hasArgument("cancelOnLogout");
        final Variable<List<ConditionID>> cancelConditions = instruction.getValueList("cancelConditions", ConditionID::new);
        return new NullableEventAdapter(new FolderEvent(betonQuest, loggerFactory.create(FolderEvent.class), pluginManager,
                events,
                questTypeAPI, new Random(), delay, period, random, timeUnit, cancelOnLogout, cancelConditions));
    }

    private TimeUnit getTimeUnit(final String input) throws QuestException {
        return switch (input.toLowerCase(Locale.ROOT)) {
            case "ticks" -> TimeUnit.TICKS;
            case "seconds" -> TimeUnit.SECONDS;
            case "minutes" -> TimeUnit.MINUTES;
            default ->
                    throw new QuestException("Invalid time unit: " + input + ". Valid units are: ticks, seconds, minutes.");
        };
    }
}
