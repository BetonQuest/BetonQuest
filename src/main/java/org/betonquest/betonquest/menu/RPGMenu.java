package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.menu.betonquest.MenuConditionFactory;
import org.betonquest.betonquest.menu.betonquest.MenuEventFactory;
import org.betonquest.betonquest.menu.betonquest.MenuObjectiveFactory;
import org.betonquest.betonquest.menu.betonquest.MenuVariableFactory;
import org.betonquest.betonquest.menu.command.RPGMenuCommand;
import org.betonquest.betonquest.menu.event.MenuOpenEvent;
import org.betonquest.betonquest.menu.kernel.MenuItemListener;
import org.betonquest.betonquest.menu.kernel.MenuItemProcessor;
import org.betonquest.betonquest.menu.kernel.MenuProcessor;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * The RPGMenu instance.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class RPGMenu {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Menu command.
     */
    private final RPGMenuCommand pluginCommand;

    /**
     * Stores and loads MenuItems.
     */
    private final MenuItemProcessor menuItemProcessor;

    /**
     * Stores and loads Menus.
     */
    private final MenuProcessor menuProcessor;

    /**
     * Opens menus when its bound item is interacted with.
     */
    private final MenuItemListener menuItemListener;

    /**
     * Create a new RPG menu instance.
     *
     * @param log               the custom logger for this class
     * @param loggerFactory     the factory to crete new custom logger instances
     * @param menuItemProcessor the processor to create and store Menu Items
     * @param pluginMessage     the plugin message instance
     * @param questTypeAPI      the Quest Type API
     * @param featureAPI        the Feature API
     * @param profileProvider   the profile provider instance
     */
    public RPGMenu(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final MenuItemProcessor menuItemProcessor,
                   final PluginMessage pluginMessage, final QuestTypeAPI questTypeAPI,
                   final FeatureAPI featureAPI, final ProfileProvider profileProvider) {
        this.log = log;
        this.loggerFactory = loggerFactory;
        final BetonQuest betonQuest = BetonQuest.getInstance();
        final String menu = "menu";
        final QuestTypeRegistries questRegistries = betonQuest.getQuestRegistries();
        final Server server = betonQuest.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), betonQuest);
        questRegistries.condition().register(menu, new MenuConditionFactory(loggerFactory, data));
        questRegistries.objective().register(menu, new MenuObjectiveFactory(loggerFactory, this));
        questRegistries.event().register(menu, new MenuEventFactory(loggerFactory, data, this));
        questRegistries.variable().register(menu, new MenuVariableFactory());
        this.pluginCommand = new RPGMenuCommand(loggerFactory.create(RPGMenuCommand.class), this);
        this.menuItemProcessor = menuItemProcessor;
        this.menuProcessor = new MenuProcessor(loggerFactory.create(MenuProcessor.class), loggerFactory, questTypeAPI,
                betonQuest.getVariableProcessor(), featureAPI, this, profileProvider);
        this.menuItemListener = new MenuItemListener(loggerFactory.create(MenuItemListener.class), this,
                menuProcessor, profileProvider, pluginMessage);
        server.getPluginManager().registerEvents(menuItemListener, betonQuest);
    }

    /**
     * If the player of the {@link OnlineProfile} has an open menu it closes it.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     */
    public static void closeMenu(final OnlineProfile onlineProfile) {
        OpenedMenu.closeMenu(onlineProfile);
    }

    /**
     * Returns if the player has opened the specified menu.
     *
     * @param onlineProfile the player form the {@link OnlineProfile} for which should be checked
     * @param menuID        the id of the menu the player should have opened,
     *                      null will return true if the player has any menu opened
     * @return true if the player has opened the specified menu, false otherwise
     */
    public static boolean hasOpenedMenu(final OnlineProfile onlineProfile, @Nullable final MenuID menuID) {
        final OpenedMenu menu = OpenedMenu.getMenu(onlineProfile);
        return menu != null && (menuID == null || menu.getId().equals(menuID));
    }

    /**
     * Returns if the player has opened any menu.
     *
     * @param onlineProfile guess what: the onlineprofile of the player!
     * @return true if player has opened a menu, false if not
     */
    public static boolean hasOpenedMenu(final OnlineProfile onlineProfile) {
        return hasOpenedMenu(onlineProfile, null);
    }

    /**
     * Open a menu for a player.
     *
     * @param onlineProfile the player of the {@link OnlineProfile} for which the menu should be opened
     * @param menuID        id of the menu
     */
    public void openMenu(final OnlineProfile onlineProfile, final MenuID menuID) {
        final Menu menu;
        try {
            menu = menuProcessor.get(menuID);
        } catch (final QuestException e) {
            log.error(menuID.getPackage(), "Could not open menu: " + e.getMessage(), e);
            return;
        }
        final MenuOpenEvent openEvent = new MenuOpenEvent(onlineProfile, menuID);
        Bukkit.getPluginManager().callEvent(openEvent);
        if (openEvent.isCancelled()) {
            log.debug(menu.getMenuID().getPackage(), "A Bukkit listener canceled opening of menu " + menuID + " for " + onlineProfile);
            return;
        }
        try {
            new OpenedMenu(loggerFactory.create(OpenedMenu.class), onlineProfile, menu);
        } catch (final QuestException e) {
            log.error(menu.getMenuID().getPackage(), "Could not open menu '" + menuID + "': " + e.getMessage(), e);
            return;
        }
        log.debug(menu.getMenuID().getPackage(), "opening menu " + menuID + " for " + onlineProfile);
    }

    /**
     * Disables and closes all Menus.
     */
    public void onDisable() {
        //close all menus
        OpenedMenu.closeAll();
        //disable listeners
        HandlerList.unregisterAll(menuItemListener);
        HandlerList.unregisterAll(BetonQuest.getInstance());
        menuItemProcessor.clear();
        menuProcessor.clear();
        this.pluginCommand.unregister();
    }

    /**
     * Reload all Menus and Menu Items.
     *
     * @param packs the Quest Packages to load
     */
    public void reloadData(final Collection<QuestPackage> packs) {
        menuProcessor.clear();
        menuItemProcessor.clear();
        for (final QuestPackage pack : packs) {
            menuItemProcessor.load(pack);
            menuProcessor.load(pack);
        }
        log.info("Reloaded " + menuProcessor.readableSize() + " and " + menuItemProcessor.readableSize());
    }

    /**
     * Get all loaded menu's ids.
     *
     * @return a collection containing all loaded menus
     */
    public Collection<MenuID> getMenus() {
        return menuProcessor.getValues().keySet();
    }

    /**
     * Get a menu by their id.
     *
     * @param menuID menuID of the menu
     * @return menu with the given menuID
     */
    @Nullable
    public Menu getMenu(final MenuID menuID) {
        return menuProcessor.getValues().get(menuID);
    }

    /**
     * Gets a loaded menu item bei their id.
     *
     * @param menuItemID the id to get the menu item for
     * @return the menu item
     * @throws QuestException when there is no such object
     */
    public MenuItem getMenuItem(final MenuItemID menuItemID) throws QuestException {
        return menuItemProcessor.get(menuItemID);
    }
}
