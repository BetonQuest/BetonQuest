package org.betonquest.betonquest.menu.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.MenuItemID;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;

/**
 * Called whenever an item in a menu is clicked by a profile's player.
 */
@SuppressWarnings("PMD.DataClass")
public class MenuClickEvent extends MenuEvent implements Cancellable {
    /**
     * A list of all handlers for this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Clicked slot.
     */
    private final int slot;

    /**
     * Clicked Menu Item.
     */
    private final MenuItemID itemId;

    /**
     * Click Type.
     */
    private final ClickType clickType;

    /**
     * Whether the event is cancelled.
     */
    private boolean cancelled;

    /**
     * Create a new Menu Event.
     *
     * @param who       the profile which interacted with the menu
     * @param menu      the id of the menu which was interacted with
     * @param slot      the clicked slot
     * @param itemId    the clicked menu item
     * @param clickType the click type on the menu
     */
    public MenuClickEvent(final Profile who, final MenuID menu, final int slot, final MenuItemID itemId, final ClickType clickType) {
        super(who, menu);
        this.clickType = clickType;
        this.itemId = itemId;
        this.slot = slot;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList.
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Get the clicked slot.
     *
     * @return the clicked slot
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Get the clicked Item's id.
     *
     * @return the internal id of the item in the clicked slot
     */
    public MenuItemID getItemId() {
        return itemId;
    }

    /**
     * Get the click type.
     *
     * @return the type of the click preformed (Possible values are: RIGHT, SHIFT_RIGHT, LEFT, SHIFT_LEFT)
     */
    public ClickType getClickType() {
        return clickType;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
