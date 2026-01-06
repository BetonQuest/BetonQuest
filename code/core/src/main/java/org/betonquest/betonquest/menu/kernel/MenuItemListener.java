package org.betonquest.betonquest.menu.kernel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.menu.Menu;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Opens the menu when its bound item is interacted with.
 */
public class MenuItemListener implements Listener {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * RPG Menu instance.
     */
    private final RPGMenu rpgMenu;

    /**
     * Menu Processor to iterate Menus.
     */
    private final MenuProcessor menuProcessor;

    /**
     * Profile Provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The sender for no permission notifications.
     */
    private final IngameNotificationSender noPermissionSender;

    /**
     * The default Constructor.
     *
     * @param log             the custom logger for this class
     * @param rpgMenu         the rpg menu instance to open menus
     * @param menuProcessor   the menu processor to get menus
     * @param profileProvider the Profile Provider
     * @param pluginMessage   the Plugin Message instance
     */
    public MenuItemListener(final BetonQuestLogger log, final RPGMenu rpgMenu, final MenuProcessor menuProcessor,
                            final ProfileProvider profileProvider, final PluginMessage pluginMessage) {
        this.log = log;
        this.rpgMenu = rpgMenu;
        this.menuProcessor = menuProcessor;
        this.profileProvider = profileProvider;
        this.noPermissionSender = new IngameNotificationSender(log, pluginMessage, null,
                "Menu Bound Item Listener", NotificationLevel.ERROR, "no_permission");
    }

    /**
     * Opens a menu on bound item interaction.
     *
     * @param event the event to process
     */
    @EventHandler
    public void onItemClick(final PlayerInteractEvent event) {
        MenuID menuID = null;
        boolean denied = false;
        final OnlineProfile profile = profileProvider.getProfile(event.getPlayer());
        for (final Menu menu : menuProcessor.getValues().values()) {
            try {
                final Argument<ItemWrapper> boundItem = menu.getBoundItem();
                if (boundItem == null || !boundItem.getValue(profile).matches(event.getItem(), profile)) {
                    continue;
                }
            } catch (final QuestException e) {
                log.warn(menu.getMenuID().getPackage(), "Exception while getting Menu Interaction Item: " + e.getMessage(), e);
            }
            menuID = menu.getMenuID();
            log.debug(menuID.getPackage(), profile + " used bound item of menu " + menuID);
            if (menu.mayOpen(profile)) {
                denied = false;
                break;
            }
            denied = true;
        }
        if (menuID == null) {
            return;
        }
        event.setCancelled(true);
        if (denied) {
            noPermissionSender.sendNotification(profile);
            return;
        }
        try {
            rpgMenu.openMenu(profile, menuID);
        } catch (final QuestException e) {
            log.error(menuID.getPackage(), "Could not open menu '" + menuID + "': " + e.getMessage(), e);
        }
    }
}
