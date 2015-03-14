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
package pl.betoncraft.betonquest.core;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.database.DatabaseHandler;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Represents a chest GUI for the backpack displayed to the player.
 * 
 * @author Coosh
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
     * Creates new GUI for the specified player at the specified page and
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
        this.page = page;
        player = PlayerConverter.getPlayer(playerID);
        instance = BetonQuest.getInstance();
        dbHandler = instance.getDBHandler(playerID);
        List<ItemStack> backpack = dbHandler.getBackpack();
        // amount of pages, considering that the first contains 44
        // items and all others 45
        int pages = (backpack.size() < 45 ? 1 : (backpack.size() + 1 % 45 == 0 ? (int) (backpack
                .size() + 1) / 45 : (int) Math.floor((backpack.size() + 1) / 45) + 1));
        Debug.info("Generating backpack for " + playerID + ", page " + page);
        // prepare the inventory
        inv = Bukkit.createInventory(null, 54, ConfigHandler.getString("messages." + ConfigHandler
                .getString("config.language")+ ".backpack_title") + (pages == 1 ? "" : " ("
                + (page + 1) + "/" + pages + ")"));
        ItemStack[] content = new ItemStack[54];
        int i = 0;
        // insert the journal if the player doesn't have it in his inventory
        if (page == 0) {
            Debug.info("  First page, checking journal");
            if (!Journal.hasJournal(playerID)) {
                Debug.info("    Adding the journal");
                content[0] = dbHandler.getJournal().generateJournal();
            } else {
                Debug.info("    Player has his journal, not adding");
                content[0] = null;
            }
            i++;
        } else {
            Debug.info("  Page is not first, skipping journal check");
        }
        // set all the items
        Debug.info("  Setting " + backpack.size() + " items");
        while (i < 45 && i + (page * 45) <= backpack.size()) {
            ItemStack item = backpack.get(i + (page * 45) - 1);
            content[i] = item;
            i++;
        }
        // if there are other pages, place the buttons
        Debug.info("  Placing buttons");
        if (page > 0) {
            ItemStack previous;
            String item = ConfigHandler.getString("items.previous_button");
            if (item != null) {
                previous = new QuestItem(item).generateItem(1);
            } else {
                previous = new ItemStack(Material.GLOWSTONE_DUST);
            }
            ItemMeta meta = previous.getItemMeta();
            meta.setDisplayName(ConfigHandler.getString(
                    "messages." + ConfigHandler.getString("config.language") + ".previous")
                    .replaceAll("&", "§"));
            previous.setItemMeta(meta);
            Debug.info("    There is a previous button");
            content[48] = previous;
        }
        if (backpack.size() > (page + 1) * 45 - 1) {
            ItemStack next;
            String item = ConfigHandler.getString("items.next_button");
            if (item != null) {
                next = new QuestItem(item).generateItem(1);
            } else {
                next = new ItemStack(Material.REDSTONE);
            }
            ItemMeta meta = next.getItemMeta();
            meta.setDisplayName(ConfigHandler.getString(
                    "messages." + ConfigHandler.getString("config.language") + ".next").replaceAll(
                    "&", "§"));
            next.setItemMeta(meta);
            Debug.info("    There is a next button");
            content[50] = next;
        }
        // set the inventory and display it
        Debug.info("Done, setting the content");
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
            Debug.info("Player " + playerID + " clicked in backpack");
            if (page == 0 && event.getRawSlot() == 0) {
                // first page on first slot should contain the journal
                Debug.info("  Journal slot was clicked, adding journal");
                dbHandler.getJournal().addJournal(Integer.parseInt(ConfigHandler
                        .getString("config.default_journal_slot")));
                new BackpackDisplay(playerID, page);
            } else if (event.getRawSlot() < 45) {
                // raw slot lower than 45 is a quest item
                Debug.info("  Quest Item has been clicked");
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
                Debug.info("  Player's inventory was clicked");
                final int slot = event.getSlot();
                final ClickType click = event.getClick();
                ItemStack item = player.getInventory().getItem(slot);
                if (item != null) {
                    // if the item exists continue
                    if (Utils.isQuestItem(item)) {
                        // if it is a quest item, add it to the backpack
                        Debug.info("    Slot " + slot + ", click " + click + ", item " + item.getType());
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
                    } else if (Journal.isJournal(item)) {
                        // if it's a journal, remove it so it appears in backpack again
                        dbHandler.getJournal().removeJournal();
                    }
                    new BackpackDisplay(playerID, page);
                }
            } else if (event.getRawSlot() == 48 && page > 0) {
                // if it was a previous/next button turn the pages
                Debug.info("  Previous button has been clicked");
                new BackpackDisplay(playerID, page - 1);
            } else if (event.getRawSlot() == 50 && dbHandler.getBackpack().size() > (page + 1) 
                    * 45 - 1) {
                Debug.info("  Next button has been clicked");
                new BackpackDisplay(playerID, page + 1);
            }
        }
    }

    @EventHandler
    public void onInventoryClosing(InventoryCloseEvent event) {
        if (event.getPlayer().equals(player)) {
            Debug.info("Player " + playerID + " closed his backpack, terminating");
            HandlerList.unregisterAll(this);
        }
    }
}
