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
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.bukkit.Location;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<BlockSelector> selector = instruction.blockSelector().get();
        final FlagArgument<Boolean> exactMatch = instruction.bool().getFlag("exactMatch", true);
        final Argument<Number> targetAmount = instruction.number().get();
        final FlagArgument<Boolean> noSafety = instruction.bool().getFlag("noSafety", true);
        final Argument<Location> location = instruction.location().get("loc").orElse(null);
        final Argument<Location> region = instruction.location().get("region").orElse(null);
        final FlagArgument<Boolean> ignoreCancel = instruction.bool().getFlag("ignorecancel", true);
        final BetonQuestLogger log = loggerFactory.create(BlockObjective.class);
        final IngameNotificationSender blockBreakSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(),
                instruction.getID().getFull(), NotificationLevel.INFO, "blocks_to_break");
        final IngameNotificationSender blockPlaceSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(),
                instruction.getID().getFull(), NotificationLevel.INFO, "blocks_to_place");
        final BlockObjective objective = new BlockObjective(service, targetAmount, selector, exactMatch, noSafety,
                location, region, ignoreCancel, blockBreakSender, blockPlaceSender);
        service.request(BlockPlaceEvent.class).priority(EventPriority.HIGHEST).onlineHandler(objective::onBlockPlace)
                .player(BlockPlaceEvent::getPlayer).subscribe(false);
        service.request(BlockBreakEvent.class).priority(EventPriority.HIGHEST).onlineHandler(objective::onBlockBreak)
                .player(BlockBreakEvent::getPlayer).subscribe(false);
        return objective;
    }
}
