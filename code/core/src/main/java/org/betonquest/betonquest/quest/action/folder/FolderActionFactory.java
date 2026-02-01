package org.betonquest.betonquest.quest.action.folder;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.bukkit.plugin.PluginManager;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Factory to create {@link FolderAction} instances.
 */
public class FolderActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Logger factory to create a logger for the actions.
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
     * Create a new folder action factory.
     *
     * @param betonQuest    the BetonQuest instance
     * @param loggerFactory the logger factory to create a logger for the actions
     * @param pluginManager the plugin manager to register the quit listener
     * @param questTypeApi  the Quest Type API
     */
    public FolderActionFactory(final BetonQuest betonQuest, final BetonQuestLoggerFactory loggerFactory,
                               final PluginManager pluginManager, final QuestTypeApi questTypeApi) {
        this.betonQuest = betonQuest;
        this.loggerFactory = loggerFactory;
        this.pluginManager = pluginManager;
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createFolderAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createFolderAction(instruction);
    }

    private NullableActionAdapter createFolderAction(final Instruction instruction) throws QuestException {
        final Argument<List<ActionIdentifier>> actions = instruction.identifier(ActionIdentifier.class).list().get();
        final Argument<Number> delay = instruction.number().get("delay").orElse(null);
        final Argument<Number> period = instruction.number().get("period").orElse(null);
        final Argument<Number> random = instruction.number().get("random").orElse(null);
        final Argument<TimeUnit> timeUnit = instruction.parse(this::getTimeUnit).get("unit", TimeUnit.SECONDS);
        final FlagArgument<Boolean> cancelOnLogout = instruction.bool().getFlag("cancelOnLogout", true);
        final Argument<List<ConditionIdentifier>> cancelConditions = instruction.identifier(ConditionIdentifier.class)
                .list().get("cancelConditions", Collections.emptyList());
        return new NullableActionAdapter(new FolderAction(betonQuest, loggerFactory.create(FolderAction.class), pluginManager,
                actions,
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
