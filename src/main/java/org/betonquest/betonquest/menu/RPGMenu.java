package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
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
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The RPGMenu instance.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.CouplingBetweenObjects"})
public class RPGMenu {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    private final ConfigAccessor config;

    private final PluginMessage pluginMessage;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    private final Map<MenuID, Menu> menus;

    private final RPGMenuCommand pluginCommand;

    public RPGMenu(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final ConfigAccessor config,
                   final PluginMessage pluginMessage, final QuestTypeAPI questTypeAPI,
                   final FeatureAPI featureAPI, final ProfileProvider profileProvider) {
        this.log = log;
        this.loggerFactory = loggerFactory;
        this.config = config;
        this.pluginMessage = pluginMessage;
        this.questTypeAPI = questTypeAPI;
        this.featureAPI = featureAPI;
        this.profileProvider = profileProvider;
        this.menus = new HashMap<>();
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
        final Menu menu = menus.get(menuID);
        if (menu == null) {
            log.error(menuID.getPackage(), "Could not open menu '" + menuID + "': Unknown menu");
            return;
        }
        final MenuOpenEvent openEvent = new MenuOpenEvent(onlineProfile, menuID);
        Bukkit.getPluginManager().callEvent(openEvent);
        if (openEvent.isCancelled()) {
            log.debug(menu.getPackage(), "A Bukkit listener canceled opening of menu " + menuID + " for " + onlineProfile);
            return;
        }
        new OpenedMenu(loggerFactory.create(OpenedMenu.class), onlineProfile, menu);
        log.debug(menu.getPackage(), "opening menu " + menuID + " for " + onlineProfile);
    }

    /**
     * Disables and closes all Menus.
     */
    public void onDisable() {
        //close all menus
        OpenedMenu.closeAll();
        //disable listeners
        HandlerList.unregisterAll(BetonQuest.getInstance());
        this.pluginCommand.unregister();
    }

    /**
     * Reload all plugin data.
     *
     * @param packs the Quest Packages to load
     */
    public void reloadData(final Collection<QuestPackage> packs) {
        final Iterator<Menu> iterator = this.menus.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().unregister();
            iterator.remove();
        }
        for (final QuestPackage pack : packs) {
            final ConfigurationSection menus = pack.getConfig().getConfigurationSection("menus");
            if (menus == null) {
                continue;
            }
            for (final String name : menus.getKeys(false)) {
                try {
                    final MenuID menuID = new MenuID(pack, name);
                    this.menus.put(menuID, new Menu(loggerFactory.create(Menu.class), loggerFactory, this, config,
                            pluginMessage, questTypeAPI, featureAPI, profileProvider, menuID));
                } catch (final InvalidConfigurationException e) {
                    log.warn(pack, e.getMessage());
                } catch (final QuestException e) {
                    log.error(pack, "Strange unhandled exception during loading: " + e);
                    return;
                }
            }
        }
        log.info("Reloaded " + menus.size() + " menus");
    }

    /**
     * Get all loaded menu's ids.
     *
     * @return a collection containing all loaded menus
     */
    public Collection<MenuID> getMenus() {
        return menus.keySet();
    }

    /**
     * Get a menu by their id.
     *
     * @param menuID menuID of the menu
     * @return menu with the given menuID
     */
    @Nullable
    public Menu getMenu(final MenuID menuID) {
        return menus.get(menuID);
    }
}
