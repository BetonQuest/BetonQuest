package org.betonquest.betonquest.quest.event.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
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
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the item durability event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param data          the data for primary server thread access
     */
    public ItemDurabilityEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<EquipmentSlot> slot = instruction.get(Argument.ENUM(EquipmentSlot.class));
        final Variable<PointType> operation = instruction.get(Argument.ENUM(PointType.class));
        final Variable<Number> amount = instruction.get(Argument.NUMBER);
        final boolean ignoreUnbreakable = instruction.hasArgument("ignoreUnbreakable");
        final boolean ignoreEvents = instruction.hasArgument("ignoreEvents");
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new ItemDurabilityEvent(slot, operation, amount, ignoreUnbreakable, ignoreEvents, new Random()),
                loggerFactory.create(ItemDurabilityEvent.class),
                instruction.getPackage()
        ), data);
    }
}
