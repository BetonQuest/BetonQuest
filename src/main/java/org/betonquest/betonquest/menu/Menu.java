package org.betonquest.betonquest.menu;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.menu.commands.SimpleCommand;
import org.betonquest.betonquest.menu.config.RPGMenuConfig;
import org.betonquest.betonquest.menu.config.SimpleYMLSection;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


/**
 * Class representing a menu
 */
@CustomLog
@SuppressWarnings({"PMD.GodClass", "PMD.ShortClassName", "PMD.CommentRequired"})
public class Menu extends SimpleYMLSection implements Listener {

    /**
     * The internal id of the menu
     */
    private final MenuID menuID;

    /**
     * The height of the menu in slots
     */
    private final int height;

    /**
     * The title of the menu
     */
    private final String title;

    /*
     * Hashmap with a items id as key and the menu item object containing all data of the item
     *
     * Currently this information isn't needed after initialising the Menu
     */
    //private final HashMap<String, MenuItem> itemsMap;

    /**
     * List of all slots objects as defined in the slots section
     */
    private final List<Slots> slots;

    /**
     * Optional which contains the item this menu is bound to or is empty if none is bound
     */
    private final Optional<QuestItem> boundItem;

    /**
     * Conditions which have to be matched to open the menu
     */
    private final List<ConditionID> openConditions;

    /**
     * Events which are fired when the menu is opened
     */
    private final List<EventID> openEvents;

    /**
     * Events which are fired when the menu is closed
     */
    private final List<EventID> closeEvents;

    /**
     * Optional which contains the command this menu is bound to or is empty if none is bound
     */
    private final Optional<MenuBoundCommand> boundCommand;

    @SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
    private final RPGMenu menu = BetonQuest.getInstance().getRpgMenu();

    @SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.NPathComplexity", "PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public Menu(final MenuID menuID) throws InvalidConfigurationException {
        super(menuID.getFullID(), menuID.getConfig());
        this.menuID = menuID;
        //load size
        this.height = getInt("height");
        if (this.height < 1 || this.height > 6) {
            throw new Invalid("height");
        }
        //load title
        this.title = ChatColor.translateAlternateColorCodes('&', getString("title"));
        //load opening conditions
        this.openConditions = new ArrayList<>();
        try {
            this.openConditions.addAll(getConditions("open_conditions", this.menuID.getPackage()));
        } catch (final Missing ignored) {
        }
        //load opening events
        this.openEvents = new ArrayList<>();
        try {
            this.openEvents.addAll(getEvents("open_events", this.menuID.getPackage()));
        } catch (final Missing ignored) {
        }
        //load closing events
        this.closeEvents = new ArrayList<>();
        try {
            this.closeEvents.addAll(getEvents("close_events", this.menuID.getPackage()));
        } catch (final Missing ignored) {
        }
        //load bound item
        this.boundItem = new OptionalSetting<QuestItem>() {
            @Override
            @SuppressWarnings("PMD.ShortMethodName")
            protected QuestItem of() throws Missing, Invalid {
                try {
                    return new QuestItem(new ItemID(Menu.this.menuID.getPackage(), getString("bind")));
                } catch (ObjectNotFoundException | InstructionParseException e) {
                    throw new Invalid("bind", e);
                }
            }
        }.get();
        //load bound command
        this.boundCommand = new OptionalSetting<MenuBoundCommand>() {
            @Override
            @SuppressWarnings("PMD.ShortMethodName")
            protected MenuBoundCommand of() throws Missing, Invalid {
                String command = getString("command").trim();
                if (!command.matches("/*[0-9A-Za-z\\-]+")) {
                    throw new Invalid("command");
                }
                if (command.startsWith("/")) {
                    command = command.substring(1);
                }
                return new MenuBoundCommand(command);
            }
        }.get();
        // load items
        if (!config.isConfigurationSection("items")) {
            throw new Missing("items");
        }
        final HashMap<String, MenuItem> itemsMap = new HashMap<>();
        for (final String key : config.getConfigurationSection("items").getKeys(false)) {
            itemsMap.put(key, new MenuItem(this.menuID.getPackage(), key, config.getConfigurationSection("items." + key)));
        }
        //load slots
        this.slots = new ArrayList<>();
        if (!config.isConfigurationSection("slots")) {
            throw new Missing("slots");
        }
        for (final String key : config.getConfigurationSection("slots").getKeys(false)) {
            final List<MenuItem> itemsList = new ArrayList<>();
            //check if items from list are all valid
            for (final String item : getStrings("slots." + key)) {
                if (itemsMap.containsKey(item)) {
                    itemsList.add(itemsMap.get(item));
                } else {
                    throw new Invalid("slots." + key, "item " + item + " not found");
                }
            }
            // create a new slots object and add it to list
            try {
                this.slots.add(new Slots(key, itemsList));
            } catch (final IllegalArgumentException e) {
                throw new Invalid("slots", e);
            }
        }
        //check for doubled assigned slots
        try {
            Slots.checkSlots(this.slots, this.getSize());
        } catch (final Slots.SlotException e) {
            throw new Invalid("slots." + e.getSlots(), e);
        }

        //load command and register listener
        this.boundCommand.ifPresent(SimpleCommand::register);
        if (this.boundItem.isPresent()) {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }
    }

    /**
     * Checks whether a player may open this menu
     *
     * @param player the player to check
     * @return true if all opening conditions are true, false otherwise
     */
    public boolean mayOpen(final Player player) {
        final String playerId = PlayerConverter.getID(player);
        for (final ConditionID conditionID : openConditions) {
            if (!BetonQuest.condition(playerId, conditionID)) {
                LOG.debug(getPackage(), "Denied opening of " + name + ": Condition " + conditionID + "returned false.");
                return false;
            }
        }
        return true;
    }

    /**
     * Unregisters listeners and commands for this menu
     * <p>
     * Run this method on reload
     */
    public void unregister() {
        boundCommand.ifPresent(SimpleCommand::unregister);
        if (boundItem.isPresent()) {
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onItemClick(final PlayerInteractEvent event) {
        //check if item is bound item
        if (!boundItem.get().compare(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        if (!mayOpen(event.getPlayer())) {
            RPGMenuConfig.sendMessage(event.getPlayer(), "menu_do_not_open");
            return;
        }
        //open the menu
        LOG.debug(getPackage(), event.getPlayer().getName() + " used bound item of menu " + this.menuID);
        menu.openMenu(event.getPlayer(), this.menuID);
    }

    /**
     * Runs all open events for the specified player
     *
     * @param player the player to run the events for
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public void runOpenEvents(final Player player) {
        LOG.debug(getPackage(), "Menu " + menuID + ": Running open events");
        for (final EventID event : this.openEvents) {
            BetonQuest.event(PlayerConverter.getID(player), event);
            LOG.debug(getPackage(), "Menu " + menuID + ": Run event " + event);
        }
    }

    /**
     * Runs all close events for the specified player
     *
     * @param player the player to run the events for
     */
    public void runCloseEvents(final Player player) {
        LOG.debug(getPackage(), "Menu " + menuID + ": Running close events");
        for (final EventID event : this.closeEvents) {
            BetonQuest.event(PlayerConverter.getID(player), event);
            LOG.debug(getPackage(), "Menu " + menuID + ": Run event " + event);
        }
    }

    /**
     * @return the menu id of this menu
     */
    public MenuID getMenuID() {
        return menuID;
    }

    /**
     * @return the package this menu is located in
     */
    public QuestPackage getPackage() {
        return this.menuID.getPackage();
    }

    /**
     * @return the height of the menu in slots
     */
    public int getHeight() {
        return height;
    }


    /**
     * @return the size of the menu in slots
     */
    public final int getSize() {
        return height * 9;
    }

    /**
     * @return the title of the menu
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param player the player to get the items for
     * @return get the items for all slots
     */
    public MenuItem[] getItems(final Player player) {
        final MenuItem[] items = new MenuItem[this.getSize()];
        for (int i = 0; i < items.length; i++) {
            items[i] = this.getItem(player, i);
        }
        return items;
    }

    /**
     * Get a menu item for a specific slot
     *
     * @param player the player to get the item for
     * @param slot   for which the item should be get
     * @return menu item for that slot or null if none is specified
     */
    public MenuItem getItem(final Player player, final int slot) {
        for (final Slots slots : this.slots) {
            if (slots.containsSlot(slot)) {
                return slots.getItem(player, slot);
            }
        }
        return null;
    }

    /**
     * @return the item this inventory is bound to
     */
    public Optional<QuestItem> getBoundItem() {
        return boundItem;
    }

    /**
     * @return a list containing all conditions which have to be met to open the menu
     */
    public List<ConditionID> getOpenConditions() {
        return openConditions;
    }

    /**
     * @return the command this inventory is bound to
     */
    public Optional<MenuBoundCommand> getBoundCommand() {
        return boundCommand;
    }

    /**
     * A command which can be used to open the gui
     * To perform the command a player must match all open conditions
     */
    private class MenuBoundCommand extends SimpleCommand {

        public MenuBoundCommand(final String name) {
            super(name, 0);
        }

        @Override
        public boolean simpleCommand(final CommandSender sender, final String alias, final String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cCommand can only be run by players!");
                return false;
            }
            final Player player = (Player) sender;
            if (mayOpen(player)) {
                LOG.debug(getPackage(), player.getName() + " run bound command of " + menuID);
                menu.openMenu(player, menuID);
                return true;
            } else {
                player.sendMessage(this.noPermissionMessage(sender));
                return false;
            }
        }

        @Override
        protected String noPermissionMessage(final CommandSender sender) {
            return RPGMenuConfig.getMessage(sender, "menu_do_not_open");
        }
    }
}
