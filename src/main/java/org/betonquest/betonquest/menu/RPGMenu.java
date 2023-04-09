package org.betonquest.betonquest.menu;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.menu.betonquest.MenuCondition;
import org.betonquest.betonquest.menu.betonquest.MenuObjective;
import org.betonquest.betonquest.menu.betonquest.MenuQuestEvent;
import org.betonquest.betonquest.menu.betonquest.MenuVariable;
import org.betonquest.betonquest.menu.commands.RPGMenuCommand;
import org.betonquest.betonquest.menu.config.RPGMenuConfig;
import org.betonquest.betonquest.menu.events.MenuOpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class RPGMenu {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(RPGMenu.class);

    private final Map<MenuID, Menu> menus;
    private RPGMenuConfig config;
    private RPGMenuCommand pluginCommand;

    public RPGMenu() {
        menus = new HashMap<>();
    }

    /**
     * If the player of the {@link OnlineProfile} has an open menu it closes it
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     */
    public static void closeMenu(final OnlineProfile onlineProfile) {
        OpenedMenu.closeMenu(onlineProfile);
    }

    /**
     * Returns if the player has opened the specified menu
     *
     * @param onlineProfile the player form the {@link OnlineProfile} for which should be checked
     * @param menuID        the id of the menu the player should have opened,
     *                      null will return true if the player has any menu opened
     * @return true if the player has opened the specified menu, false otherwise
     */
    public static boolean hasOpenedMenu(final OnlineProfile onlineProfile, final MenuID menuID) {
        final OpenedMenu menu = OpenedMenu.getMenu(onlineProfile);
        if (menu == null) {
            return false;
        }
        if (menuID == null) {
            return true;
        }
        return menu.getId().equals(menuID);
    }

    /**
     * Returns if the player has opened any menu
     *
     * @param onlineProfile guess what: the onlineprofile of the player!
     * @return true if player has opened a menu, false if not
     */
    public static boolean hasOpenedMenu(final OnlineProfile onlineProfile) {
        return RPGMenu.hasOpenedMenu(onlineProfile, null);
    }

    /**
     * @return the config of the plugin
     */
    public RPGMenuConfig getConfiguration() {
        return config;
    }

    /**
     * Open a menu for a player
     *
     * @param onlineProfile the player of the {@link OnlineProfile} for which the menu should be opened
     * @param menuID        id of the menu
     */
    public void openMenu(final OnlineProfile onlineProfile, final MenuID menuID) {
        final Menu menu = menus.get(menuID);
        if (menu == null) {
            LOG.error(menuID.getPackage(), "Could not open menu '" + menuID + "': Unknown menu");
            return;
        }
        final MenuOpenEvent openEvent = new MenuOpenEvent(onlineProfile, menuID);
        Bukkit.getPluginManager().callEvent(openEvent);
        if (openEvent.isCancelled()) {
            LOG.debug(menu.getPackage(), "A Bukkit listener canceled opening of menu " + menuID + " for " + onlineProfile);
            return;
        }
        new OpenedMenu(onlineProfile, menu);
        LOG.debug(menu.getPackage(), "opening menu " + menuID + " for " + onlineProfile);
    }

    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public void onEnable() {
        //register events, objectives and conditions
        BetonQuest.getInstance().registerConditions("menu", MenuCondition.class);
        BetonQuest.getInstance().registerObjectives("menu", MenuObjective.class);
        BetonQuest.getInstance().registerEvents("menu", MenuQuestEvent.class);
        BetonQuest.getInstance().registerVariable("menu", MenuVariable.class);
        //load the plugin command
        this.pluginCommand = new RPGMenuCommand();
        //create config if it doesn't exist
        final File config = new File(BetonQuest.getInstance().getDataFolder(), "menuConfig.yml");
        try {
            ConfigAccessor.create(config, BetonQuest.getInstance(), "menuConfig.yml");
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public void onDisable() {
        //close all menus
        OpenedMenu.closeAll();
        //disable listeners
        HandlerList.unregisterAll(BetonQuest.getInstance());
        this.pluginCommand.unregister();
    }

    /**
     * Reload all plugin data
     *
     * @return information if the reload was successful
     */
    @SuppressWarnings({"PMD.NPathComplexity", "PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public ReloadInformation reloadData() {
        if (!menus.isEmpty()) {
            final Iterator<Menu> iterator = this.menus.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().unregister();
                iterator.remove();
            }
        }
        final ReloadInformation info = new ReloadInformation();
        try {
            this.config = new RPGMenuConfig();
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            LOG.error("Invalid Configuration.", e);
            info.addError(e);
            info.result = ReloadResult.FAILED;
            return info;
        }
        //load files for all packages
        for (final QuestPackage pack : Config.getPackages().values()) {
            final ConfigurationSection menus = pack.getConfig().getConfigurationSection("menus");
            if (menus == null) {
                continue;
            }
            for (final String name : menus.getKeys(false)) {
                try {
                    final MenuID menuID = new MenuID(pack, name);
                    this.menus.put(menuID, new Menu(menuID));
                    info.loaded++;
                } catch (final InvalidConfigurationException e) {
                    LOG.warn(pack, e.getMessage());
                    info.addError(e);
                    info.result = ReloadResult.SUCCESS;
                } catch (final ObjectNotFoundException e) {
                    LOG.error(pack, "Strange unhandled exception during loading: " + e);
                    info.result = ReloadResult.FAILED;
                    return info;
                }
            }
        }
        final ChatColor color = (info.result == ReloadResult.FULL_SUCCESS) ? ChatColor.GREEN : ChatColor.YELLOW;
        LOG.info(color + "Reloaded " + info.loaded + color + " menus");
        return info;
    }

    /**
     * Reloads only one menu with the given menuID
     *
     * @param menuID menuID of the menu which should be reloaded
     * @return information if the reload was successful
     */
    public ReloadInformation reloadMenu(final MenuID menuID) {
        //unregister old menu if it exists
        if (this.menus.containsKey(menuID)) {
            this.menus.get(menuID).unregister();
            this.menus.remove(menuID);
        }
        final ReloadInformation info = new ReloadInformation();
        try {
            this.menus.put(menuID, new Menu(menuID));
            info.result = ReloadResult.FULL_SUCCESS;
            info.loaded = 1;
            LOG.info(menuID.getPackage(), "Reloaded menu " + menuID);
        } catch (final InvalidConfigurationException e) {
            LOG.warn(menuID.getPackage(), e.getMessage());
            info.result = ReloadResult.FAILED;
            info.addError(e);
        }
        return info;
    }

    /**
     * @return a collection containing all loaded menus
     */
    public Collection<MenuID> getMenus() {
        return menus.keySet();
    }

    /**
     * @param menuID menuID of the menu
     * @return menu with the given menuID
     */
    public Menu getMenu(final MenuID menuID) {
        return menus.get(menuID);
    }

    /**
     * Tells whether a reload was successful
     */
    public enum ReloadResult {
        /**
         * If all data could be successfully loaded
         */
        FULL_SUCCESS,
        /**
         * If reload was successful but some menus could not be loaded
         */
        SUCCESS,
        /**
         * If reload completely failed
         */
        FAILED
    }

    /**
     * Class containing all information about a reload
     */
    public static class ReloadInformation {

        private final List<String> errorMessages = new ArrayList<>();
        private int loaded;
        private ReloadResult result = ReloadResult.FULL_SUCCESS;

        private void addError(final Throwable throwable) {
            errorMessages.add(throwable.getMessage());
        }

        /**
         * @return a list containing all errors that where thrown while reloading
         */
        public List<String> getErrorMessages() {
            return errorMessages;
        }

        /**
         * @return amount of menus that were loaded
         */
        public int getLoaded() {
            return loaded;
        }

        /**
         * @return the result of the reload (if it was fully successful, partially successful, or failed)
         */
        public ReloadResult getResult() {
            return result;
        }
    }
}
