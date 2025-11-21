package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Event to notify crafting objectives when an item is crafting without the
 * {@link org.bukkit.event.inventory.CraftItemEvent}.
 */
public class ItemStackCraftedEvent extends ProfileEvent {
    /**
     * Static HandlerList to register listeners on the event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Crafted item.
     */
    private final ItemStack stack;

    /**
     * Amount of items crafted.
     */
    private final int amount;

    /**
     * Creates an item stack crafted event.
     *
     * @param who    who the profile
     * @param stack  the crafted item
     * @param amount the amount of items crafted
     */
    public ItemStackCraftedEvent(final OnlineProfile who, final ItemStack stack, final int amount) {
        super(who);
        this.stack = stack;
        this.amount = amount;
    }

    /**
     * The static getter as required by the Event specification.
     *
     * @return the handler list to register new listener
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Get the crafted item stack.
     *
     * @return the crafted item
     */
    public ItemStack getStack() {
        return stack;
    }

    /**
     * Get the amount of crafted item stacks.
     *
     * @return the crafted amount
     */
    public int getAmount() {
        return amount;
    }
}
