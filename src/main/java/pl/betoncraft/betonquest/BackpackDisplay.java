/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import pl.betoncraft.betonquest.database.DatabaseHandler;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Represents a chest GUI for the backpack displayed to the player.
 * 
 * @author Jakub Sapalski
 */
public class BackpackDisplay implements Listener {

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
    private final DatabaseHandler dbHandler;
    /**
     * The inventory created by this object
     */
    private Inventory inv;
    /**
     * Currently displayed page
     */
    private int page;
    /**
     * Stores assignments of quest cancelers to inventory slots
     */
    private HashMap<Integer,String> map;
    private String lang;

    /**
     * Creates new GUI for the specified player and displays it to them.
     * It will show the first page.
     * 
     * @param playerID
     *            ID of the player
     */
    public BackpackDisplay(String playerID) {
        this(playerID, 0);
    }

    /**
     * Creates new GUI with items for the specified player at the specified page and
     * displays it to them.
     * 
     * @param playerID
     *            ID of the player
     * @param page
     *            page to display
     */
    public BackpackDisplay(String playerID, int page) {
        // fill those fields
        this.playerID = playerID;
        this.lang = BetonQuest.getInstance().getDBHandler(playerID).getLanguage();
        this.page = page;
        player = PlayerConverter.getPlayer(playerID);
        instance = BetonQuest.getInstance();
        dbHandler = instance.getDBHandler(playerID);
        // handle page -1, for canceling the quests
        if (page == -1) {
            map = new HashMap<>();
            HashMap<String, String> cancelers = new HashMap<>();
            // for every package
            for (String packName : Config.getPackageNames()) {
                // loop all quest cancelers
                ConfigurationSection s = Config.getPackage(packName).getMain()
                        .getConfig().getConfigurationSection("cancel");
                for (String key : s.getKeys(false)) {
                    // and for each canceler
                    String canceler = s.getString(key);
                    boolean isMet = true;
                    String name = null;
                    String defName = null;
                    for (String part : canceler.split(" ")) {
                        // check conditions
                        if (part.startsWith("conditions:")) {
                            for (String condition : part.substring(11).split(",")) {
                                if (!condition.contains(".")) {
                                    condition = packName + "." + condition;
                                }
                                if (!BetonQuest.condition(playerID, condition)) {
                                    isMet = false;
                                    break;
                                }
                            }
                        // and parse the name
                        } else if (part.startsWith("name_" + lang + ":")) {
                            name = part.substring(6 + lang.length());
                        } else {
                            if (part.startsWith("name_" + Config.getLanguage() + ":")) {
                                defName = part.substring(6 + Config.getLanguage().length());
                            } else if (part.startsWith("name:")) {
                                defName = part.substring(5);
                            }
                        }
                    }
                    if (name == null) {
                        name = defName;
                    }
                    if (name == null) {
                        Debug.error("Default name not defined in quest canceler: "
                                + packName + "." + key);
                        continue;
                    }
                    // now if canceler meets the conditions
                    if (isMet) {
                        // put it into the map
                        cancelers.put(packName + "." + key, name);
                    }
                }
            }
            // not all cancelers that meet conditions and their names are in the map
            int size = cancelers.size();
            int numberOfRows = ((size - size%9) / 9) + 1;
            if (numberOfRows > 6) {
                numberOfRows = 6;
                Debug.error("Player " + player.getName() + " has too many active quests, please"
                    + " don't allow for so many of them. It slows down your server!");
            }
            inv = Bukkit.createInventory(null, numberOfRows*9, Config.getMessage(lang, "cancel_page"));
            ItemStack[] content = new ItemStack[numberOfRows*9];
            int i = 0;
            for (String address : cancelers.keySet()) {
                String name = cancelers.get(address);
                ItemStack canceler = null;
                String item = Config.getString(address.substring(0, address.indexOf('.'))
                        + ".items.cancel_button");
                if (item != null) {
                    try {
                        canceler = new QuestItem(item).generateItem(1);
                    } catch (InstructionParseException e) {
                        Debug.error("Could not load cancel button: " + e.getCause().getMessage());
                        return;
                    }
                } else {
                    canceler = new ItemStack(Material.BONE);
                }
                ItemMeta meta = canceler.getItemMeta();
                meta.setDisplayName(name.replace("_", " ").replace("&", "§"));
                canceler.setItemMeta(meta);
                content[i] = canceler;
                map.put(i, address);
                i++;
            }
            inv.setContents(content);
            player.openInventory(inv);
            Bukkit.getPluginManager().registerEvents(this, instance);
            return;
        }
        
        List<ItemStack> backpack = dbHandler.getBackpack();
        // amount of pages, considering that the first contains 44
        // items and all others 45
        int pages = (backpack.size() < 45 ? 1 : (backpack.size() + 1 % 45 == 0 ? (int) (backpack
                .size() + 1) / 45 : (int) Math.floor((backpack.size() + 1) / 45) + 1));
        // prepare the inventory
        inv = Bukkit.createInventory(null, 54, Config.getMessage(lang, "backpack_title") + (pages == 1 ? "" : " ("
                + (page + 1) + "/" + pages + ")"));
        ItemStack[] content = new ItemStack[54];
        int i = 0;
        // insert the journal if the player doesn't have it in his inventory
        if (page == 0) {
            if (!Journal.hasJournal(playerID)) {
                content[0] = dbHandler.getJournal().getAsItem();
            } else {
                content[0] = null;
            }
            i++;
        } else {
        }
        // set all the items
        while (i < 45 && i + (page * 45) <= backpack.size()) {
            ItemStack item = backpack.get(i + (page * 45) - 1);
            content[i] = item;
            i++;
        }
        // if there are other pages, place the buttons
        if (page > 0) {
            ItemStack previous = null;
            String item = Config.getString("default.items.previous_button");
            if (item != null) {
                try {
                    previous = new QuestItem(item).generateItem(1);
                } catch (InstructionParseException e) {
                    Debug.error("Could not load previous button: " + e.getCause().getMessage());
                    return;
                }
            } else {
                previous = new ItemStack(Material.GLOWSTONE_DUST);
            }
            ItemMeta meta = previous.getItemMeta();
            meta.setDisplayName(Config.getMessage(lang, "previous").replaceAll("&", "§"));
            previous.setItemMeta(meta);
            content[48] = previous;
        }
        if (backpack.size() > (page + 1) * 45 - 1) {
            ItemStack next;
            String item = Config.getString("default.items.next_button");
            if (item != null) {
                try {
                    next = new QuestItem(item).generateItem(1);
                } catch (InstructionParseException e) {
                    Debug.error("Could not load next button: " + e.getCause().getMessage());
                    return;
                }
            } else {
                next = new ItemStack(Material.REDSTONE);
            }
            ItemMeta meta = next.getItemMeta();
            meta.setDisplayName(Config.getMessage(lang, "next").replaceAll("&", "§"));
            next.setItemMeta(meta);
            content[50] = next;
        }
        // set "cancel quest" button
        ItemStack cancel;
        String item = Config.getString("default.items.cancel_button");
        if (item != null) {
            try {
                cancel = new QuestItem(item).generateItem(1);
            } catch (InstructionParseException e) {
                Debug.error("Could not load cancel button: " + e.getCause().getMessage());
                return;
            }
        } else {
            cancel = new ItemStack(Material.BONE);
        }
        ItemMeta meta = cancel.getItemMeta();
        meta.setDisplayName(Config.getMessage(lang, "cancel").replaceAll("&", "§"));
        cancel.setItemMeta(meta);
        content[45] = cancel;
        // set the inventory and display it
        inv.setContents(content);
        player.openInventory(inv);
        // register listener
        Bukkit.getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked().equals(player)) {
            // if the player clicked, then cancel this event
            event.setCancelled(true);
            // if the click was outside of the inventory, do nothing
            if (event.getRawSlot() < 0) {
                return;
            }
            if (page == -1) {
                // handle quest canceling
                String address = map.get(event.getRawSlot());
                if (address == null) {
                    return;
                }
                dbHandler.cancelQuest(address);
                player.closeInventory();
                return;
            } else if (page == 0 && event.getRawSlot() == 0) {
                // first page on first slot should contain the journal
                dbHandler.getJournal().addToInv(Integer.parseInt(Config
                        .getString("config.default_journal_slot")));
                new BackpackDisplay(playerID, page);
            } else if (event.getRawSlot() < 45) {
                // raw slot lower than 45 is a quest item
                // read the id of the item from clicked slot
                int id = page * 45 + event.getRawSlot() - 1;
                ItemStack item = null;
                // get the item if it exists
                if (dbHandler.getBackpack().size() > id) {
                    item = dbHandler.getBackpack().get(id);
                }
                if (item != null) {
                    // if the item exists, put it in player's inventory 
                    int backpackAmount = item.getAmount();
                    int getAmount = 0;
                    // left click is one item, right is the whole stack
                    switch (event.getClick()) {
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
                        ItemStack newItem = item.clone();
                        newItem.setAmount(getAmount);
                        ItemStack leftItems = player.getInventory().addItem(newItem).get(0);
                        // remove from backpack only those items that were
                        // actually added to player's inventory
                        int leftAmount = 0;
                        if (leftItems != null) {
                            leftAmount = leftItems.getAmount();
                        }
                        item.setAmount(backpackAmount - getAmount + leftAmount);
                        if (backpackAmount - getAmount + leftAmount == 0) {
                            dbHandler.getBackpack().remove(id);
                        }
                    }
                    new BackpackDisplay(playerID, page);
                }
            } else if (event.getRawSlot() > 53) {
                // slot above 53 is player's inventory, so handle item storing
                final int slot = event.getSlot();
                final ClickType click = event.getClick();
                ItemStack item = player.getInventory().getItem(slot);
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
                        // add item to backpack and remove it from player's inventory
                        dbHandler.addItem(item.clone(), amount);
                        if (item.getAmount() - amount == 0) {
                            player.getInventory().setItem(slot, null);
                        } else {
                            item.setAmount(item.getAmount() - amount);
                            player.getInventory().setItem(slot, item);
                        }
                    } else if (Journal.isJournal(playerID, item)) {
                        // if it's a journal, remove it so it appears in backpack again
                        dbHandler.getJournal().removeFromInv();
                    }
                    new BackpackDisplay(playerID, page);
                }
            } else if (event.getRawSlot() == 48 && page > 0) {
                // if it was a previous/next button turn the pages
                new BackpackDisplay(playerID, page - 1);
            } else if (event.getRawSlot() == 50 && dbHandler.getBackpack().size() > (page + 1) 
                    * 45 - 1) {
                new BackpackDisplay(playerID, page + 1);
            } else if (event.getRawSlot() == 45) {
                new BackpackDisplay(playerID, -1);
            }
        }
    }

    @EventHandler
    public void onInventoryClosing(InventoryCloseEvent event) {
        if (event.getPlayer().equals(player)) {
            HandlerList.unregisterAll(this);
        }
    }
}
