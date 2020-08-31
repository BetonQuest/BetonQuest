/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.config.QuestCanceler;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.ItemID;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Represents a chest GUI for the backpack displayed to the player.
 *
 * @author Jakub Sapalski
 */
public class Backpack implements Listener {

    /**
     * ID of the player
     */
    private final String playerID;
    /**
     * The player object
     */
    private final Player player;
    /**
     * Instance of the BetonQuest plugin
     */
    private final BetonQuest instance;
    /**
     * Database handler for the player
     */
    private final PlayerData playerData;
    /**
     * The inventory created by this object
     */
    private Inventory inv;
    /**
     * Currently displayed page
     */
    private Display display;
    /**
     * Language of the player
     */
    private String lang;
    private Backpack backpack;

    /**
     * Creates new backpack GUI opened at given page type.
     *
     * @param playerID ID of the player
     * @param type     type of the display
     */
    public Backpack(final String playerID, final DisplayType type) {
        // fill required fields
        this.playerID = playerID;
        lang = BetonQuest.getInstance().getPlayerData(playerID).getLanguage();
        player = PlayerConverter.getPlayer(playerID);
        instance = BetonQuest.getInstance();
        playerData = instance.getPlayerData(playerID);
        backpack = this;
        // create display
        switch (type) {
            case DEFAULT:
                display = new Page(0);
                break;
            case CANCEL:
                display = new Cancelers();
                break;
            case COMPASS:
                display = new Compass();
                break;
        }
    }

    /**
     * Creates new backpack GUI.
     *
     * @param playerID ID of the player
     */
    public Backpack(final String playerID) {
        this(playerID, DisplayType.DEFAULT);
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(final InventoryClickEvent event) {
        if (event.getWhoClicked().equals(player)) {
            // if the player clicked, then cancel this event
            event.setCancelled(true);
            // if the click was outside of the inventory, do nothing
            if (event.getRawSlot() < 0) {
                return;
            }
            // pass the click to the Display
            display.click(event.getRawSlot(), event.getSlot(), event.getClick());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClosing(final InventoryCloseEvent event) {
        if (event.getPlayer().equals(player)) {
            HandlerList.unregisterAll(this);
        }
    }

    public enum DisplayType {
        DEFAULT, CANCEL, COMPASS
    }

    /**
     * Represents a display that can be shown as the backpack.
     *
     * @author Jakub Sapalski
     */
    private abstract class Display {
        private Display() {}

        protected abstract void click(int slot, int playerSlot, ClickType click);
    }

    /**
     * Standard page with quest items.
     *
     * @author Jakub Sapalski
     */
    @SuppressWarnings("PMD.ShortClassName")
    private class Page extends Display {

        private final int page;

        /**
         * Creates and displays to the player a given page.
         *
         * @param page number of the page to display, starting from 0
         */
        public Page(final int page) {
            super();
            this.page = page;
            final List<ItemStack> backpackItems = playerData.getBackpack();
            // amount of pages, considering that the first contains 44
            // items and all others 45
            final int pages = backpackItems.size() < 45 ? 1
                    : backpackItems.size() + 1 % 45 == 0 ? (backpackItems.size() + 1) / 45
                    : (int) Math.floor((backpackItems.size() + 1) / 45) + 1;
            // prepare the inventory
            inv = Bukkit.createInventory(null, 54, Config.getMessage(lang, "backpack_title")
                    + (pages == 1 ? "" : " (" + (page + 1) + "/" + pages + ")"));
            final ItemStack[] content = new ItemStack[54];
            int index = 0;
            // insert the journal if the player doesn't have it in his inventory
            if (page == 0) {
                if (Journal.hasJournal(playerID)) {
                    content[0] = null;
                } else {
                    content[0] = playerData.getJournal().getAsItem();
                }
                index++;
            } else {
            }
            // set all the items
            while (index < 45 && index + (page * 45) <= backpackItems.size()) {
                final ItemStack item = backpackItems.get(index + (page * 45) - 1);
                content[index] = item;
                index++;
            }
            // if there are other pages, place the buttons
            if (page > 0) {
                ItemStack previous = null;
                try {
                    previous = new QuestItem(new ItemID(Config.getDefaultPackage(), "previous_button")).generate(1);
                } catch (ObjectNotFoundException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not find item: " + e.getMessage());
                    LogUtils.logThrowable(e);
                    previous = new ItemStack(Material.GLOWSTONE_DUST);
                } catch (InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not load previous button: " + e.getMessage());
                    LogUtils.logThrowable(e);
                    player.closeInventory();
                    return;
                }
                final ItemMeta meta = previous.getItemMeta();
                meta.setDisplayName(Config.getMessage(lang, "previous").replaceAll("&", "§"));
                previous.setItemMeta(meta);
                content[48] = previous;
            }
            if (backpackItems.size() > (page + 1) * 45 - 1) {
                ItemStack next;
                try {
                    next = new QuestItem(new ItemID(Config.getDefaultPackage(), "next_button")).generate(1);
                } catch (ObjectNotFoundException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not find item: " + e.getMessage());
                    LogUtils.logThrowable(e);
                    next = new ItemStack(Material.REDSTONE);
                } catch (InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not load next button: " + e.getMessage());
                    LogUtils.logThrowable(e);
                    player.closeInventory();
                    return;
                }
                final ItemMeta meta = next.getItemMeta();
                meta.setDisplayName(Config.getMessage(lang, "next").replaceAll("&", "§"));
                next.setItemMeta(meta);
                content[50] = next;
            }
            // set "cancel quest" button
            ItemStack cancel;
            try {
                cancel = new QuestItem(new ItemID(Config.getDefaultPackage(), "cancel_button")).generate(1);
            } catch (ObjectNotFoundException e) {
                LogUtils.getLogger().log(Level.WARNING, "Could not find object: " + e.getMessage());
                LogUtils.logThrowable(e);
                cancel = new ItemStack(Material.BONE);
            } catch (InstructionParseException e) {
                LogUtils.getLogger().log(Level.WARNING, "Could not load cancel button: " + e.getMessage());
                LogUtils.logThrowable(e);
                player.closeInventory();
                return;
            }
            final ItemMeta meta = cancel.getItemMeta();
            meta.setDisplayName(Config.getMessage(lang, "cancel").replaceAll("&", "§"));
            cancel.setItemMeta(meta);
            content[45] = cancel;
            // set "compass targets" button
            ItemStack compassItem;
            try {
                compassItem = new QuestItem(new ItemID(Config.getDefaultPackage(), "compass_button")).generate(1);
            } catch (ObjectNotFoundException e) {
                LogUtils.getLogger().log(Level.WARNING, "Could not find item: " + e.getMessage());
                LogUtils.logThrowable(e);
                compassItem = new ItemStack(Material.COMPASS);
            } catch (InstructionParseException e) {
                LogUtils.getLogger().log(Level.WARNING, "Could not load compass button: " + e.getMessage());
                LogUtils.logThrowable(e);
                player.closeInventory();
                return;
            }
            final ItemMeta compassMeta = compassItem.getItemMeta();
            compassMeta.setDisplayName(Config.getMessage(lang, "compass").replace('&', '&'));
            compassItem.setItemMeta(compassMeta);
            content[46] = compassItem;
            // set the inventory and display it
            inv.setContents(content);
            player.openInventory(inv);
            Bukkit.getPluginManager().registerEvents(backpack, BetonQuest.getInstance());
        }

        @Override
        protected void click(final int slot, final int playerSlot, final ClickType click) {
            if (page == 0 && slot == 0) {
                // first page on first slot should contain the journal
                playerData.getJournal().addToInv(Integer.parseInt(Config.getString("config.default_journal_slot")));
                display = new Page(page);
            } else if (slot < 45) {
                // raw slot lower than 45 is a quest item
                // read the id of the item from clicked slot
                final int slotId = page * 45 + slot - 1;
                ItemStack item = null;
                // get the item if it exists
                if (playerData.getBackpack().size() > slotId) {
                    item = playerData.getBackpack().get(slotId);
                }
                if (item != null) {
                    // if the item exists, put it in player's inventory
                    final int backpackAmount = item.getAmount();
                    int getAmount = 0;
                    // left click is one item, right is the whole stack
                    switch (click) {
                        case LEFT:
                            getAmount = 1;
                            break;
                        case RIGHT:
                            getAmount = backpackAmount;
                            break;
                        default:
                            break;
                    }
                    if (getAmount != 0) {
                        // add desired amount of items to player's inventory
                        final ItemStack newItem = item.clone();
                        newItem.setAmount(getAmount);
                        final ItemStack leftItems = player.getInventory().addItem(newItem).get(0);
                        // remove from backpack only those items that were
                        // actually added to player's inventory
                        int leftAmount = 0;
                        if (leftItems != null) {
                            leftAmount = leftItems.getAmount();
                        }
                        item.setAmount(backpackAmount - getAmount + leftAmount);
                        if (backpackAmount - getAmount + leftAmount == 0) {
                            final List<ItemStack> backpackItems = playerData.getBackpack();
                            backpackItems.remove(slotId);
                            playerData.setBackpack(backpackItems);
                        }
                    }
                    display = new Page(page);
                }
            } else if (slot > 53) {
                // slot above 53 is player's inventory, so handle item storing
                final ItemStack item = player.getInventory().getItem(playerSlot);
                if (item != null) {
                    // if the item exists continue
                    if (Utils.isQuestItem(item)) {
                        // if it is a quest item, add it to the backpack
                        int amount = 0;
                        // left click is one item, right is all items
                        switch (click) {
                            case LEFT:
                                amount = 1;
                                break;
                            case RIGHT:
                                amount = item.getAmount();
                                break;
                            default:
                                break;
                        }
                        // add item to backpack and remove it from player's
                        // inventory
                        playerData.addItem(item.clone(), amount);
                        if (item.getAmount() - amount == 0) {
                            player.getInventory().setItem(playerSlot, null);
                        } else {
                            item.setAmount(item.getAmount() - amount);
                            player.getInventory().setItem(playerSlot, item);
                        }
                    } else if (Journal.isJournal(playerID, item)) {
                        // if it's a journal, remove it so it appears in
                        // backpack again
                        playerData.getJournal().removeFromInv();
                    }
                    display = new Page(page);
                }
            } else if (slot == 48 && page > 0) {
                // if it was a previous/next button turn the pages
                display = new Page(page - 1);
            } else if (slot == 50 && playerData.getBackpack().size() > (page + 1) * 45 - 1) {
                display = new Page(page + 1);
            } else if (slot == 45) {
                // slot 45 is a slot with quest cancelers
                display = new Cancelers();
            } else if (slot == 46) {
                // slot 46 is a slot with compass pointers
                display = new Compass();
            }
        }

    }

    /**
     * The page with quest cancelers.
     *
     * @author Jakub Sapalski
     */
    private class Cancelers extends Display {

        private HashMap<Integer, QuestCanceler> map = new HashMap<>();

        /**
         * Creates a page with quest cancelers and displays it to the player.
         */
        public Cancelers() {
            super();
            final HashMap<String, QuestCanceler> cancelers = new HashMap<>();
            // get all quest cancelers that can be shown to the player
            for (final String name : Config.getCancelers().keySet()) {
                final QuestCanceler canceler = Config.getCancelers().get(name);
                if (canceler.show(playerID)) {
                    cancelers.put(name, canceler);
                }
            }
            // generate the inventory view
            final int size = cancelers.size();
            int numberOfRows = (size - size % 9) / 9 + 1;
            if (numberOfRows > 6) {
                numberOfRows = 6;
                LogUtils.getLogger().log(Level.WARNING, "Player " + player.getName() + " has too many active quests, please"
                        + " don't allow for so many of them. It slows down your server!");
            }
            inv = Bukkit.createInventory(null, numberOfRows * 9, Config.getMessage(lang, "cancel_page"));
            final ItemStack[] content = new ItemStack[numberOfRows * 9];
            int index = 0;
            for (final String name : cancelers.keySet()) {
                final QuestCanceler canceler = cancelers.get(name);
                content[index] = canceler.getItem(playerID);
                map.put(index, canceler);
                index++;
            }
            inv.setContents(content);
            player.openInventory(inv);
            Bukkit.getPluginManager().registerEvents(backpack, BetonQuest.getInstance());
        }

        @Override
        protected void click(final int slot, final int playerSlot, final ClickType click) {
            final QuestCanceler cancel = map.get(slot);
            if (cancel == null) {
                return;
            }
            // cancel the chosen quests
            cancel.cancel(playerID);
            player.closeInventory();
        }
    }

    private class Compass extends Display {

        private HashMap<Integer, Location> locations = new HashMap<>();
        private HashMap<Integer, String> names = new HashMap<>();
        private HashMap<Integer, String> items = new HashMap<>();

        public Compass() {
            super();
            Integer counter = 0;
            // for every package
            for (final ConfigPackage pack : Config.getPackages().values()) {
                final String packName = pack.getName();
                // loop all compass locations
                final ConfigurationSection section = pack.getMain().getConfig().getConfigurationSection("compass");
                if (section != null) {
                    for (final String key : section.getKeys(false)) {
                        final String location = pack.getString("main.compass." + key + ".location");
                        String name = null;
                        if (section.isConfigurationSection(key + ".name")) {
                            name = pack.getString("main.compass." + key + ".name." + lang);
                            if (name == null) {
                                name = pack.getString("main.compass." + key + ".name." + Config.getLanguage());
                            }
                            if (name == null) {
                                name = pack.getString("main.compass." + key + ".name.en");
                            }
                        } else {
                            name = pack.getString("main.compass." + key + ".name");
                        }
                        if (name == null) {
                            LogUtils.getLogger().log(Level.WARNING, "Name not defined in a compass pointer in " + packName + " package: " + key);
                            continue;
                        }
                        if (location == null) {
                            LogUtils.getLogger().log(Level.WARNING,
                                    "Location not defined in a compass pointer in " + packName + " package: " + key);
                            continue;
                        }
                        // check if the player has special compass tag
                        if (!playerData.hasTag(packName + ".compass-" + key)) {
                            continue;
                        }
                        // if the tag is present, continue
                        final String[] parts = location.split(";");
                        if (parts.length != 4) {
                            LogUtils.getLogger().log(Level.WARNING, "Could not parse location in a compass pointer in " + packName + " package: "
                                    + key);
                            continue;
                        }
                        final World world = Bukkit.getWorld(parts[3]);
                        if (world == null) {
                            LogUtils.getLogger().log(Level.WARNING,
                                    "World does not exist in a compass pointer in " + packName + " package: " + key);
                        }
                        final int locX;
                        final int locY;
                        final int locZ;
                        try {
                            locX = Integer.parseInt(parts[0]);
                            locY = Integer.parseInt(parts[1]);
                            locZ = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            LogUtils.getLogger().log(Level.WARNING, "Could not parse location coordinates in a compass pointer in " + packName
                                    + " package: " + key);
                            LogUtils.logThrowable(e);
                            player.closeInventory();
                            return;
                        }
                        final Location loc = new Location(world, locX, locY, locZ);
                        // put location with next number
                        locations.put(counter, loc);
                        names.put(counter, name);
                        final String itemName = pack.getString("main.compass." + key + ".item");
                        if (itemName != null) {
                            items.put(counter, packName + "." + itemName);
                        }
                        counter++;
                    }
                }
            }
            // solve number of needed rows
            final int size = locations.size();
            int numberOfRows = (size - size % 9) / 9 + 1;
            if (numberOfRows > 6) {
                numberOfRows = 6;
                LogUtils.getLogger().log(Level.WARNING, "Player " + player.getName() + " has too many compass pointers, please"
                        + " don't allow for so many of them. It slows down your server!");
                player.closeInventory();
                return;
            }
            inv = Bukkit.createInventory(null, numberOfRows * 9, Config.getMessage(lang, "compass_page"));
            final ItemStack[] content = new ItemStack[numberOfRows * 9];
            int index = 0;
            for (final Integer slot : locations.keySet()) {
                final String item = items.get(slot);
                ItemStack compass = null;
                try {
                    compass = new QuestItem(new ItemID(Config.getDefaultPackage(), item)).generate(1);
                } catch (InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not load compass button: " + e.getMessage());
                    LogUtils.logThrowable(e);
                    player.closeInventory();
                    return;
                } catch (NullPointerException e) {
                    if (e.getMessage().equals("Item instruction is null")) {
                        LogUtils.getLogger().log(Level.WARNING, "Item instruction is null.");
                        LogUtils.logThrowable(e);
                        player.closeInventory();
                        return;
                    } else {
                        LogUtils.logThrowableReport(e);
                        return;
                    }
                } catch (ObjectNotFoundException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not find item: " + e.getMessage());
                    LogUtils.logThrowable(e);
                    compass = new ItemStack(Material.COMPASS);
                }
                final ItemMeta meta = compass.getItemMeta();
                final String name = names.get(slot);
                meta.setDisplayName(name.replace("_", " ").replace("&", "§"));
                compass.setItemMeta(meta);
                content[index] = compass;
                index++;
            }
            inv.setContents(content);
            player.openInventory(inv);
            Bukkit.getPluginManager().registerEvents(backpack, BetonQuest.getInstance());
        }

        @Override
        protected void click(final int slot, final int layerSlot, final ClickType click) {
            final Location loc = locations.get(slot);
            if (loc == null) {
                return;
            }
            // set the location of the compass
            player.setCompassTarget(loc);
            player.closeInventory();
        }
    }
}
