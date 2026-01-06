package org.betonquest.betonquest.quest.objective.chestput;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.betonquest.betonquest.quest.action.chest.ChestTakeAction;
import org.betonquest.betonquest.quest.condition.chest.ChestItemCondition;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

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
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<List<ItemWrapper>> items = instruction.item().list().get();
        final boolean multipleAccess = instruction.bool().get("multipleaccess", false).getValue(null);
        final ChestItemCondition chestItemCondition = new ChestItemCondition(loc, items);
        final boolean itemsStay = instruction.bool().getFlag("items-stay", true)
                .getValue(null).orElse(false);
        final ChestTakeAction chestTakeAction = itemsStay ? null : new ChestTakeAction(loc, items);
        final BetonQuestLogger log = loggerFactory.create(ChestPutObjective.class);
        final IngameNotificationSender occupiedSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(),
                instruction.getID().getFull(), NotificationLevel.INFO, "chest_occupied");
        final ChestPutObjective objective = new ChestPutObjective(service, chestItemCondition, chestTakeAction,
                loc, occupiedSender, multipleAccess);
        service.request(InventoryOpenEvent.class).onlineHandler(objective::onChestOpen)
                .entity(InventoryOpenEvent::getPlayer).subscribe(false);
        service.request(InventoryCloseEvent.class).onlineHandler(objective::onChestClose)
                .entity(InventoryCloseEvent::getPlayer).subscribe(true);
        return objective;
    }
}
