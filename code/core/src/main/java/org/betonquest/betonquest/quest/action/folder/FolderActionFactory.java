package org.betonquest.betonquest.quest.action.folder;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;
import org.bukkit.plugin.Plugin;
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
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The plugin manager to register the quit listener.
     */
    private final PluginManager pluginManager;

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Create a new folder action factory.
     *
     * @param plugin           the plugin instance
     * @param loggerFactory    the logger factory to create a logger for the actions
     * @param pluginManager    the plugin manager to register the quit listener
     * @param actionManager    the action manager
     * @param conditionManager the condition manager
     */
    public FolderActionFactory(final Plugin plugin, final BetonQuestLoggerFactory loggerFactory,
                               final PluginManager pluginManager, final ActionManager actionManager, final ConditionManager conditionManager) {
        this.plugin = plugin;
        this.loggerFactory = loggerFactory;
        this.pluginManager = pluginManager;
        this.actionManager = actionManager;
        this.conditionManager = conditionManager;
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
        return new NullableActionAdapter(new FolderAction(plugin, loggerFactory.create(FolderAction.class), pluginManager,
                actionManager, conditionManager, actions, new Random(), delay, period, random, timeUnit, cancelOnLogout, cancelConditions));
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
