package org.betonquest.betonquest.menu.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.menu.MenuID;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;

/**
 * Called whenever an item in a menu is clicked by a profile's player.
 */
@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class MenuClickEvent extends MenuEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final int slot;

    private final String itemId;

    private final ClickType clickType;

    private boolean cancelled;

    public MenuClickEvent(final Profile who, final MenuID menu, final int slot, final String itemId, final ClickType clickType) {
        super(who, menu);
        this.clickType = clickType;
        this.itemId = itemId;
        this.slot = slot;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * @return the clicked slot
     */
    public int getSlot() {
        return slot;
    }

    /**
     * @return the internal id of the item in the clicked slot
     */
    public String getItemId() {
        return itemId;
    }

    /**
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
