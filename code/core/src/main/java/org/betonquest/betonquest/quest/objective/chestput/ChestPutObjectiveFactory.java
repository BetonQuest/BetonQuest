package org.betonquest.betonquest.quest.objective.chestput;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.quest.condition.chest.ChestItemCondition;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.chest.ChestTakeEvent;
import org.bukkit.Location;

import java.util.List;

/**
 * Factory for creating {@link ChestPutObjective} instances from {@link Instruction}s.
 */
public class ChestPutObjectiveFactory implements ObjectiveFactory {

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Creates a new instance of the ChestPutObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public ChestPutObjectiveFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<List<ItemWrapper>> items = instruction.item().getList();
        final boolean multipleAccess = instruction.bool().get("multipleaccess", false).getValue(null);
        final ChestItemCondition chestItemCondition = new ChestItemCondition(loc, items);
        final ChestTakeEvent chestTakeEvent = instruction.hasArgument("items-stay") ? null : new ChestTakeEvent(loc, items);
        final BetonQuestLogger log = loggerFactory.create(ChestPutObjective.class);
        final IngameNotificationSender occupiedSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(),
                instruction.getID().getFull(), NotificationLevel.INFO, "chest_occupied");
        return new ChestPutObjective(instruction, chestItemCondition, chestTakeEvent, loc, occupiedSender,
                multipleAccess);
    }
}
