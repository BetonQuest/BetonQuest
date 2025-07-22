package org.betonquest.betonquest.quest.event.drop;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Event that drops items at a location.
 */
public class DropEvent implements NullableEvent {
    /**
     * Items to be dropped.
     */
    private final Variable<List<Item>> items;

    /**
     * Location to drop the items at.
     */
    private final Variable<Location> location;

    /**
     * Creates an event that drops the given items at a location selected by the given selector.
     *
     * @param items    items to be dropped
     * @param location the location to drop the items at
     */
    public DropEvent(final Variable<List<Item>> items, final Variable<Location> location) {
        this.items = items;
        this.location = location;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Location location = this.location.getValue(profile);
        for (final Item itemDefinition : items.getValue(profile)) {
            final ItemStack item = itemDefinition.generate(profile);
            final int amount = itemDefinition.getAmount().getValue(profile).intValue();

            dropAsStacks(location, item, amount);
        }
    }

    private void dropAsStacks(final Location location, final ItemStack item, final int amount) {
        int remaining = amount;
        while (remaining > 0) {
            final int stackSize = Math.min(remaining, item.getMaxStackSize());
            dropStack(location, item, stackSize);
            remaining -= stackSize;
        }
    }

    private void dropStack(final Location location, final ItemStack item, final int stackSize) {
        final ItemStack drop = item.clone();
        drop.setAmount(stackSize);
        location.getWorld().dropItem(location, drop);
    }
}
