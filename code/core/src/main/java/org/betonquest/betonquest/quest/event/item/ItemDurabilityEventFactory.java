package org.betonquest.betonquest.quest.event.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.quest.event.point.PointType;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Random;

/**
 * Factory for the item durability event.
 */
public class ItemDurabilityEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the item durability event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public ItemDurabilityEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<EquipmentSlot> slot = instruction.enumeration(EquipmentSlot.class).get();
        final Variable<PointType> operation = instruction.enumeration(PointType.class).get();
        final Variable<Number> amount = instruction.number().get();
        final boolean ignoreUnbreakable = instruction.hasArgument("ignoreUnbreakable");
        final boolean ignoreEvents = instruction.hasArgument("ignoreEvents");
        return new OnlineEventAdapter(new ItemDurabilityEvent(slot, operation, amount, ignoreUnbreakable, ignoreEvents, new Random()),
                loggerFactory.create(ItemDurabilityEvent.class), instruction.getPackage());
    }
}
