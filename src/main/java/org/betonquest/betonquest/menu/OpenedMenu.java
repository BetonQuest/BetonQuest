package org.betonquest.betonquest.menu;


import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.menu.events.MenuClickEvent;
import org.betonquest.betonquest.menu.events.MenuCloseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class representing a menu which is currently displayed to a player
 */
@SuppressWarnings("PMD.CommentRequired")
public class OpenedMenu implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(OpenedMenu.class);

    /**
     * Hashmap containing all currently opened menus
     */
    private static final Map<UUID, OpenedMenu> OPENED_MENUS = new HashMap<>();
    private final OnlineProfile onlineProfile;
    private final Menu data;
    private MenuItem[] items;
    private boolean closed;

    public OpenedMenu(final OnlineProfile onlineProfile, final Menu menu) {
        // If player already has an open menu we close it first
        final OpenedMenu current = getMenu(onlineProfile);
        if (current != null) {
            current.close();
        }

        this.data = menu;
        this.onlineProfile = onlineProfile;
        final Inventory inventory = Bukkit.createInventory(null, data.getSize(), data.getTitle(onlineProfile));
        this.update(onlineProfile, inventory);
        onlineProfile.getPlayer().openInventory(inventory);
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        OPENED_MENUS.put(onlineProfile.getProfileUUID(), this);
        this.data.runOpenEvents(onlineProfile);
    }

    /**
     * Returns the menu a player has opened
     *
     * @param onlineProfile the player of the {@link OnlineProfile} to check for
     * @return the menu the player has opened or null if he has no open menus
     */
    public static OpenedMenu getMenu(final OnlineProfile onlineProfile) {
        return OPENED_MENUS.get(onlineProfile.getProfileUUID());
    }

    /**
     * Closes the players menu from the {@link OnlineProfile} if he has one open
     */
    protected static void closeMenu(final OnlineProfile onlineProfile) {
        final OpenedMenu menu = OPENED_MENUS.get(onlineProfile.getProfileUUID());
        if (menu == null) {
            return;
        }
        menu.close();
    }

    /**
     * Closes all currently opened menus
     * <p>
     * Called when the plugin unloads to prevent glitching menus
     */
    public static void closeAll() {
        for (final OpenedMenu openedMenu : OPENED_MENUS.values()) {
            openedMenu.close();
        }
    }

    /**
     * @return true if menu was closed
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * @return the id of this menu
     */
    public final MenuID getId() {
        return this.data.getMenuID();
    }

    /**
     * @return the menu object containing all data
     */
    public Menu getData() {
        return this.data;
    }

    /**
     * @return the player the menu is displayed to
     */
    public OnlineProfile getProfile() {
        return onlineProfile;
    }

    /**
     * @return the inventory which shows the menu
     */
    public Inventory getInventory() {
        return this.getProfile().getPlayer().getOpenInventory().getTopInventory();
    }

    /**
     * Closes the menu
     */
    public void close() {
        getProfile().getPlayer().closeInventory();
        closed = true;
    }

    /**
     * (Re-)adds all items to the inventory
     *
     * @param onlineProfile the player the menu is displayed to
     * @param inventory     the inventory showing the menu
     */
    public final void update(final OnlineProfile onlineProfile, final Inventory inventory) {
        this.items = data.getItems(onlineProfile);
        final ItemStack[] content = new ItemStack[items.length];
        //add the items if display conditions are matched
        for (int i = 0; i < items.length; i++) {
            content[i] = (items[i] == null) ? new ItemStack(Material.AIR) : items[i].generateItem(onlineProfile);
        }
        LOG.debug(getId().getPackage(), "updated contents of menu " + getId() + " for " + onlineProfile);
        inventory.setContents(content);
    }

    /**
     * Readds all items to the inventory
     */
    public void update() {
        this.update(getProfile(), getInventory());
    }

    @EventHandler
    @SuppressWarnings({"PMD.NPathComplexity", "PMD.CyclomaticComplexity", "PMD.PrematureDeclaration", "PMD.CognitiveComplexity"})
    public void onClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof final Player player)) {
            return;
        }
        if (!player.equals(onlineProfile.getPlayer())) {
            return;
        }
        event.setCancelled(true);
        final Inventory inventory = event.getClickedInventory();
        //only continue if menu is clicked
        if (inventory == null || inventory instanceof PlayerInventory) {
            return;
        }
        final MenuItem item = this.items[event.getSlot()];
        //only continue if a displayed item was clicked
        if (item == null) {
            return;
        }
        //only continue if click type is valid
        switch (event.getClick()) {
            case SHIFT_RIGHT:
            case RIGHT:
            case SHIFT_LEFT:
            case LEFT:
            case MIDDLE:
                break;
            default:
                return;
        }
        //call event
        Bukkit.getServer().getScheduler().runTask(BetonQuest.getInstance(), () -> {
            final MenuClickEvent clickEvent = new MenuClickEvent(onlineProfile, getId(), event.getSlot(), item.getId(), event.getClick());
            Bukkit.getPluginManager().callEvent(clickEvent);
            LOG.debug(getId().getPackage(), onlineProfile + " clicked on slot " + event.getSlot() + " with item " + item.getId() + " in menu " + getId());
            if (clickEvent.isCancelled()) {
                LOG.debug(getId().getPackage(), "click of " + onlineProfile + " in menu " + getId() + " was cancelled by a bukkit event listener");
                return;
            }
            //done if already closed by a 3rd party listener
            if (closed) {
                return;
            }

            //run click events
            final boolean close = item.onClick(player, event.getClick());

            //check if the inventory was closed by an event (teleport event etc.)
            if (closed) {
                return;
            }

            if (getMenu(onlineProfile).equals(this)) {
                //if close was set close the menu
                if (close) {
                    this.close();
                }
                // otherwise update the contents
                else {
                    this.update();
                }
            }
        });
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof final Player player)) {
            return;
        }
        if (!player.equals(onlineProfile.getPlayer())) {
            return;
        }
        //call event
        final MenuCloseEvent closeEvent = new MenuCloseEvent(onlineProfile, getId());
        Bukkit.getPluginManager().callEvent(closeEvent);
        LOG.debug(getId().getPackage(), onlineProfile + " closed menu " + getId());
        //clean up
        HandlerList.unregisterAll(this);
        OPENED_MENUS.remove(onlineProfile.getProfileUUID());
        closed = true;
        //run close events
        this.data.runCloseEvents(player);
    }
}
