package org.betonquest.betonquest.quest.event.drop;

import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Event that drops items at a location.
 */
public class DropEvent implements ComposedEvent {
    /**
     * Items to be dropped.
     */
    private final Item[] items;

    /**
     * Selector for the drop location.
     */
    private final Selector<Location> locationSelector;

    /**
     * Creates an event that drops the given items at a location selected by the given selector.
     *
     * @param items            items to be dropped
     * @param locationSelector selector for the drop location
     */
    public DropEvent(final Item[] items, final Selector<Location> locationSelector) {
        this.items = items.clone();
        this.locationSelector = locationSelector;
    }

    private static void dropAsStacks(final Location location, final ItemStack item, final int amount) {
        int remaining = amount;
        while (remaining > 0) {
            final int stackSize = Math.min(remaining, item.getMaxStackSize());
            dropStack(location, item, stackSize);
            remaining -= stackSize;
        }
    }

    private static void dropStack(final Location location, final ItemStack item, final int stackSize) {
        final ItemStack drop = item.clone();
        drop.setAmount(stackSize);
        location.getWorld().dropItem(location, drop);
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        final Location location = locationSelector.selectFor(profile);
        for (final Item itemDefinition : items) {
            final ItemStack item = itemDefinition.getItem().generate(1, profile);
            final int amount = itemDefinition.getAmount().getInt(profile);

            dropAsStacks(location, item, amount);
        }
    }
}
