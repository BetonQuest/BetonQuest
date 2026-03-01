package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.MenuIdentifier;
import org.betonquest.betonquest.api.identifier.MenuItemIdentifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderRegistry;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.menu.betonquest.MenuActionFactory;
import org.betonquest.betonquest.menu.betonquest.MenuConditionFactory;
import org.betonquest.betonquest.menu.betonquest.MenuObjectiveFactory;
import org.betonquest.betonquest.menu.betonquest.MenuPlaceholderFactory;
import org.betonquest.betonquest.menu.command.RPGMenuCommand;
import org.betonquest.betonquest.menu.event.MenuOpenEvent;
import org.betonquest.betonquest.menu.kernel.MenuItemListener;
import org.betonquest.betonquest.menu.kernel.MenuItemProcessor;
import org.betonquest.betonquest.menu.kernel.MenuProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
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
     * @param log                       the custom logger for this class
     * @param loggerFactory             the factory to create new custom logger instances
     * @param instructions              the instruction api to use
     * @param pluginConfig              the plugin config
     * @param pluginMessage             the plugin message instance
     * @param textCreator               the text creator to parse text
     * @param profileProvider           the profile provider instance
     * @param parsers                   the argument parsers to use
     * @param menuIdentifierFactory     the factory to create menu identifiers
     * @param menuItemIdentifierFactory the factory to create menu item identifiers
     * @param actionRegistry            the action registry
     * @param conditionRegistry         the condition registry
     * @param objectiveRegistry         the objective registry
     * @param placeholderRegistry       the placeholder registry
     * @param actionManager             the action manager
     * @param conditionManager          the condition manager
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public RPGMenu(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                   final Instructions instructions, final ConfigAccessor pluginConfig,
                   final PluginMessage pluginMessage, final ParsedSectionTextCreator textCreator,
                   final ProfileProvider profileProvider, final ArgumentParsers parsers,
                   final IdentifierFactory<MenuIdentifier> menuIdentifierFactory,
                   final IdentifierFactory<MenuItemIdentifier> menuItemIdentifierFactory,
                   final ActionRegistry actionRegistry, final ConditionRegistry conditionRegistry,
                   final ObjectiveRegistry objectiveRegistry, final PlaceholderRegistry placeholderRegistry,
                   final ActionManager actionManager, final ConditionManager conditionManager) {
        this.log = log;
        this.loggerFactory = loggerFactory;
        final BetonQuest betonQuest = BetonQuest.getInstance();
        final String menu = "menu";
        final Server server = betonQuest.getServer();
        conditionRegistry.register(menu, new MenuConditionFactory());
        objectiveRegistry.register(menu, new MenuObjectiveFactory(loggerFactory, this));
        actionRegistry.register(menu, new MenuActionFactory(this));
        placeholderRegistry.register(menu, new MenuPlaceholderFactory());
        this.pluginCommand = new RPGMenuCommand(loggerFactory.create(RPGMenuCommand.class), this, menuIdentifierFactory);
        pluginCommand.register();
        pluginCommand.syncCraftBukkitCommands();
        this.menuItemProcessor = new MenuItemProcessor(loggerFactory.create(MenuItemProcessor.class), loggerFactory,
                instructions, textCreator, menuItemIdentifierFactory, pluginConfig, parsers, actionManager, conditionManager);
        this.menuProcessor = new MenuProcessor(loggerFactory.create(MenuProcessor.class), loggerFactory, instructions,
                textCreator, actionManager, conditionManager, parsers, this, menuIdentifierFactory, profileProvider);
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
    public static boolean hasOpenedMenu(final OnlineProfile onlineProfile, @Nullable final MenuIdentifier menuID) {
        final OpenedMenu menu = OpenedMenu.getMenu(onlineProfile);
        return menu != null && (menuID == null || menu.getId().equals(menuID));
    }

    /**
     * Open a menu for a player.
     *
     * @param onlineProfile the player of the {@link OnlineProfile} for which the menu should be opened
     * @param menuID        id of the menu
     * @throws QuestException when the menu is not loaded or an error while constructing occurred
     */
    public void openMenu(final OnlineProfile onlineProfile, final MenuIdentifier menuID) throws QuestException {
        final Menu menu = menuProcessor.get(menuID);
        if (new MenuOpenEvent(onlineProfile, menuID).callEvent()) {
            new OpenedMenu(loggerFactory.create(OpenedMenu.class), onlineProfile, menu);
            log.debug(menu.getMenuID().getPackage(), "opening menu " + menuID + " for " + onlineProfile);
        } else {
            log.debug(menu.getMenuID().getPackage(), "A Bukkit listener canceled opening of menu " + menuID + " for " + onlineProfile);
        }
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
        this.pluginCommand.syncCraftBukkitCommands();
    }

    /**
     * Get the menu processor.
     *
     * @return the menu processor
     */
    public MenuProcessor getMenuProcessor() {
        return menuProcessor;
    }

    /**
     * Get the menu item processor.
     *
     * @return the menu item processor
     */
    public MenuItemProcessor getMenuItemProcessor() {
        return menuItemProcessor;
    }

    /**
     * Syncs the command map for (Menu) commands.
     */
    public void syncCommands() {
        pluginCommand.syncCraftBukkitCommands();
        log.info("Synced Command Map for (Menu) Commands…");
    }

    /**
     * Get all loaded menu's ids.
     *
     * @return a collection containing all loaded menus
     */
    public Collection<MenuIdentifier> getMenus() {
        return menuProcessor.getValues().keySet();
    }

    /**
     * Get a menu by their id.
     *
     * @param menuID menuID of the menu
     * @return menu with the given menuID
     */
    @Nullable
    public Menu getMenu(final MenuIdentifier menuID) {
        return menuProcessor.getValues().get(menuID);
    }

    /**
     * Gets a loaded menu item bei their id.
     *
     * @param menuItemID the id to get the menu item for
     * @return the menu item
     * @throws QuestException when there is no such object
     */
    public MenuItem getMenuItem(final MenuItemIdentifier menuItemID) throws QuestException {
        return menuItemProcessor.get(menuItemID);
    }
}
