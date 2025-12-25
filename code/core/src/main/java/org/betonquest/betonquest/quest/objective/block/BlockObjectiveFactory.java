package org.betonquest.betonquest.quest.objective.block;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.bukkit.Location;

/**
 * Factory for creating {@link BlockObjective} instances from {@link Instruction}s.
 */
public class BlockObjectiveFactory implements ObjectiveFactory {

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Creates a new instance of the BlockObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public BlockObjectiveFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<BlockSelector> selector = instruction.blockSelector().get();
        final boolean exactMatch = instruction.hasArgument("exactMatch");
        final Argument<Number> targetAmount = instruction.number().get();
        final boolean noSafety = instruction.hasArgument("noSafety");
        final Argument<Location> location = instruction.location().get("loc").orElse(null);
        final Argument<Location> region = instruction.location().get("region").orElse(null);
        final boolean ignoreCancel = instruction.hasArgument("ignorecancel");
        final BetonQuestLogger log = loggerFactory.create(BlockObjective.class);
        final IngameNotificationSender blockBreakSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(),
                instruction.getID().getFull(), NotificationLevel.INFO, "blocks_to_break");
        final IngameNotificationSender blockPlaceSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(),
                instruction.getID().getFull(), NotificationLevel.INFO, "blocks_to_place");
        return new BlockObjective(instruction, targetAmount, selector, exactMatch, noSafety, location, region, ignoreCancel,
                blockBreakSender, blockPlaceSender);
    }
}
