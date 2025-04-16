package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.menu.command.SimpleCommand;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Class representing a menu.
 */
@SuppressWarnings({"PMD.ShortClassName", "PMD.CouplingBetweenObjects"})
public class Menu implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The RPGMenu "plugin" instance to open menus.
     */
    private final RPGMenu rpgMenu;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The internal id of the menu.
     */
    private final MenuID menuID;

    /**
     * The general Menu Data.
     */
    private final MenuData data;

    /**
     * Item this menu is bound to or is empty if none is bound.
     */
    @Nullable
    private final Item boundItem;

    /**
     * Optional which contains the command this menu is bound to or is empty if none is bound.
     */
    @Nullable
    private final MenuBoundCommand boundCommand;

    /**
     * The sender for no permission notifications.
     */
    private final IngameNotificationSender noPermissionSender;

    /**
     * Creates a new Menu.
     *
     * @param log                the custom logger for this class
     * @param loggerFactory      the logger factory for new class specific custom logger
     * @param rpgMenu            the rpg menu instance to open menus
     * @param menuID             the id of the menu
     * @param profileProvider    the profile provider instance
     * @param questTypeAPI       the Quest Type API
     * @param menuData           the Menu Data
     * @param boundItem          the optional bound Item
     * @param command            the optional bound command string
     * @param noPermissionSender the ingame sender to use if the profile can't open the menu by missing permissions
     * @throws QuestException if the bound command is invalid
     */
    public Menu(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final RPGMenu rpgMenu,
                final MenuID menuID, final ProfileProvider profileProvider, final QuestTypeAPI questTypeAPI,
                final MenuData menuData, @Nullable final Item boundItem, @Nullable final String command,
                final IngameNotificationSender noPermissionSender) throws QuestException {
        this.log = log;
        this.questTypeAPI = questTypeAPI;
        this.profileProvider = profileProvider;
        this.menuID = menuID;
        this.data = menuData;
        this.boundItem = boundItem;
        this.rpgMenu = rpgMenu;
        this.noPermissionSender = noPermissionSender;
        if (command == null) {
            this.boundCommand = null;
        } else {
            this.boundCommand = getBoundCommand(loggerFactory, command);
            this.boundCommand.register();
        }

        if (this.boundItem != null) {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }
    }

    private MenuBoundCommand getBoundCommand(final BetonQuestLoggerFactory loggerFactory, final String command)
            throws QuestException {
        String trimmed = command.trim();
        if (!trimmed.matches("/*[0-9A-Za-z\\-]+")) {
            throw new QuestException("command is invalid!");
        }
        if (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1);
        }
        return new MenuBoundCommand(loggerFactory.create(MenuBoundCommand.class), trimmed);
    }

    /**
     * Checks whether a player of the {@link Profile} may open this menu.
     *
     * @param profile the {@link Profile} to check
     * @return true if all opening conditions are true, false otherwise
     */
    public boolean mayOpen(final Profile profile) {
        final List<ConditionID> resolved;
        try {
            resolved = data.openConditions.getValue(profile);
        } catch (final QuestException exception) {
            log.warn(menuID.getPackage(), "Error while resolving open_conditions in menu '" + menuID + "': " + exception.getMessage(), exception);
            return false;
        }
        for (final ConditionID conditionID : resolved) {
            if (!questTypeAPI.condition(profile, conditionID)) {
                log.debug(menuID.getPackage(), "Denied opening of " + menuID + ": Condition " + conditionID + "returned false.");
                return false;
            }
        }
        return true;
    }

    /**
     * Unregisters listeners and commands for this menu.
     * <p>
     * Run this method on reload
     */
    public void unregister() {
        if (boundCommand != null) {
            boundCommand.unregister();
        }
        if (boundItem != null) {
            HandlerList.unregisterAll(this);
        }
    }

    /**
     * Opens the menu on bound item interaction.
     *
     * @param event the event to process
     */
    @EventHandler
    public void onItemClick(final PlayerInteractEvent event) {
        try {
            if (boundItem == null || !boundItem.matches(event.getItem())) {
                return;
            }
        } catch (final QuestException e) {
            log.warn(menuID.getPackage(), "Exception while getting Menu Interaction Item: " + e.getMessage(), e);
        }
        event.setCancelled(true);
        final OnlineProfile onlineprofile = profileProvider.getProfile(event.getPlayer());
        if (!mayOpen(onlineprofile)) {
            noPermissionSender.sendNotification(onlineprofile);
            return;
        }
        log.debug(menuID.getPackage(), onlineprofile + " used bound item of menu " + this.menuID);
        rpgMenu.openMenu(onlineprofile, this.menuID);
    }

    /**
     * Runs all open events for the specified player of the {@link Profile}.
     *
     * @param profile the {@link Profile} to run the events for
     */
    public void runOpenEvents(final OnlineProfile profile) {
        executeEvents(data.openEvents, profile, "open");
    }

    /**
     * Runs all close events for the specified player of the {@link Profile}.
     *
     * @param profile the profile of the player to run the events for
     */
    public void runCloseEvents(final OnlineProfile profile) {
        executeEvents(data.closeEvents, profile, "close");
    }

    private void executeEvents(final VariableList<EventID> events, final OnlineProfile profile, final String type) {
        log.debug(menuID.getPackage(), "Menu " + menuID + ": Running " + type + " events");
        final List<EventID> resolved;
        try {
            resolved = events.getValue(profile);
        } catch (final QuestException exception) {
            log.warn(menuID.getPackage(), "Error while resolving " + type + " events in menu item '" + menuID + "': "
                    + exception.getMessage(), exception);
            return;
        }
        for (final EventID event : resolved) {
            log.debug(menuID.getPackage(), "Menu " + menuID + ": Run event " + event);
            questTypeAPI.event(profile, event);
        }
    }

    /**
     * @return the menu id of this menu
     */
    public MenuID getMenuID() {
        return menuID;
    }

    /**
     * @return the size of the menu in slots
     */
    public final int getSize() {
        return data.height * 9;
    }

    /**
     * @param profile the {@link Profile} of the player
     * @return the title of the menu
     * @throws QuestException if the title cannot be parsed
     */
    public String getTitle(final Profile profile) throws QuestException {
        return ChatColor.translateAlternateColorCodes('&', data.title.getValue(profile));
    }

    /**
     * @param profile the player of the {@link Profile} to get the items for
     * @return get the items for all slots
     */
    public MenuItem[] getItems(final Profile profile) {
        final MenuItem[] items = new MenuItem[this.getSize()];
        for (int i = 0; i < items.length; i++) {
            items[i] = this.getItem(profile, i);
        }
        return items;
    }

    /**
     * Get a menu item for a specific slot.
     *
     * @param profile the player {@link Profile} to get the item for
     * @param slot    for which the item should be get
     * @return menu item for that slot or null if none is specified
     */
    @Nullable
    public MenuItem getItem(final Profile profile, final int slot) {
        for (final Slots slots : data.slots) {
            if (slots.containsSlot(slot)) {
                try {
                    return slots.getItem(profile, slot);
                } catch (final QuestException e) {
                    log.warn("Could not get Item for slot '" + slots + "': " + e.getMessage(), e);
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Core Data of a Menu.
     *
     * @param title          The title of the menu.
     * @param height         The height of the menu in slots.
     * @param slots          List of all slots objects.
     * @param openConditions Conditions which have to be matched to open the menu.
     * @param openEvents     Events which are fired when the menu is opened.
     * @param closeEvents    Events which are fired when the menu is closed.
     */
    public record MenuData(Variable<String> title, int height, List<Slots> slots,
                           VariableList<ConditionID> openConditions,
                           VariableList<EventID> openEvents, VariableList<EventID> closeEvents) {

    }

    /**
     * A command which can be used to open the gui.
     * To perform the command a player must match all open conditions.
     */
    private class MenuBoundCommand extends SimpleCommand {

        /**
         * Creates a new command for opening this menu.
         *
         * @param log  the custom logger
         * @param name the command name
         */
        public MenuBoundCommand(final BetonQuestLogger log, final String name) {
            super(log, name, 0);
        }

        @Override
        public boolean simpleCommand(final CommandSender sender, final String alias, final String[] args) {
            if (!(sender instanceof final Player player)) {
                sender.sendMessage("Command can only be run by players!");
                return false;
            }
            final OnlineProfile onlineProfile = profileProvider.getProfile(player);
            if (mayOpen(onlineProfile)) {
                log.debug(menuID.getPackage(), onlineProfile + " run bound command of " + menuID);
                rpgMenu.openMenu(onlineProfile, menuID);
                return true;
            } else {
                sendMessage(sender, "no_permission");
                return false;
            }
        }
    }
}
