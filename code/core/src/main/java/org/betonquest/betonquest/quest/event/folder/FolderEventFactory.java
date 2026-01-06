package org.betonquest.betonquest.quest.event.folder;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.bukkit.plugin.PluginManager;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Factory to create {@link FolderEvent} instances.
 */
public class FolderEventFactory implements PlayerActionFactory, PlayerlessActionFactory {

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
    private final QuestTypeApi questTypeApi;

    /**
     * Create a new folder event factory.
     *
     * @param betonQuest    the BetonQuest instance
     * @param loggerFactory the logger factory to create a logger for the events
     * @param pluginManager the plugin manager to register the quit listener
     * @param questTypeApi  the Quest Type API
     */
    public FolderEventFactory(final BetonQuest betonQuest, final BetonQuestLoggerFactory loggerFactory,
                              final PluginManager pluginManager, final QuestTypeApi questTypeApi) {
        this.betonQuest = betonQuest;
        this.loggerFactory = loggerFactory;
        this.pluginManager = pluginManager;
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createFolderEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createFolderEvent(instruction);
    }

    private NullableActionAdapter createFolderEvent(final Instruction instruction) throws QuestException {
        final Argument<List<ActionID>> events = instruction.parse(ActionID::new).list().get();
        final Argument<Number> delay = instruction.number().get("delay").orElse(null);
        final Argument<Number> period = instruction.number().get("period").orElse(null);
        final Argument<Number> random = instruction.number().get("random").orElse(null);
        final Argument<TimeUnit> timeUnit = instruction.parse(this::getTimeUnit).get("unit", TimeUnit.SECONDS);
        final FlagArgument<Boolean> cancelOnLogout = instruction.bool().getFlag("cancelOnLogout", true);
        final Argument<List<ConditionID>> cancelConditions = instruction.parse(ConditionID::new)
                .list().get("cancelConditions", Collections.emptyList());
        return new NullableActionAdapter(new FolderEvent(betonQuest, loggerFactory.create(FolderEvent.class), pluginManager,
                events,
                questTypeApi, new Random(), delay, period, random, timeUnit, cancelOnLogout, cancelConditions));
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
