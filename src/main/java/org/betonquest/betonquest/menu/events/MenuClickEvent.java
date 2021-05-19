package org.betonquest.betonquest.menu.events;

import org.betonquest.betonquest.menu.MenuID;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;

/**
 * Called whenever a item in a menu is clicked by a player
 * <p>
 * Created on 16.03.2018.
 *
 * @author Jonas Blocher
 */
public class MenuClickEvent extends MenuEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final int slot;
    private final String itemId;
    private final ClickType clickType;
    private boolean cancelled = false;

    public MenuClickEvent(final Player who, final MenuID menu, final int slot, final String itemId, final ClickType clickType) {
        super(who, menu);
        this.clickType = clickType;
        this.itemId = itemId;
        this.slot = slot;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean b) {
        cancelled = b;
    }
}
