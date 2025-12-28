package org.betonquest.betonquest.quest.objective.block;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
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
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<BlockSelector> selector = instruction.blockSelector().get();
        final FlagArgument<Boolean> exactMatch = instruction.bool().getFlag("exactMatch", false);
        final Argument<Number> targetAmount = instruction.number().get();
        final FlagArgument<Boolean> noSafety = instruction.bool().getFlag("noSafety", false);
        final Argument<Location> location = instruction.location().get("loc").orElse(null);
        final Argument<Location> region = instruction.location().get("region").orElse(null);
        final FlagArgument<Boolean> ignoreCancel = instruction.bool().getFlag("ignorecancel", false);
        final BetonQuestLogger log = loggerFactory.create(BlockObjective.class);
        final IngameNotificationSender blockBreakSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(),
                instruction.getID().getFull(), NotificationLevel.INFO, "blocks_to_break");
        final IngameNotificationSender blockPlaceSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(),
                instruction.getID().getFull(), NotificationLevel.INFO, "blocks_to_place");
        return new BlockObjective(instruction, targetAmount, selector, exactMatch, noSafety, location, region, ignoreCancel,
                blockBreakSender, blockPlaceSender);
    }
}
