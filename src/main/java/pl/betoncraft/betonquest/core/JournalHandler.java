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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Handler for Journals.
 * 
 * @author Co0sh
 */
public class JournalHandler implements Listener {

    /**
     * Registers the Journal handler as Listener.
     */
    public JournalHandler() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler
    public void onJournalDrop(PlayerDropItemEvent event) {
        // if journal is dropped, remove it so noone else can pick it up
        if (isJournal(event.getItemDrop().getItemStack())) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onJournalMove(InventoryClickEvent event) {
        // canceling all action that could lead to transfering the journal
        if (isJournal(event.getCursor())
        // this blocks normal clicking outside of the inventory
            && (event.getAction().equals(InventoryAction.PLACE_ALL)
                || event.getAction().equals(InventoryAction.PLACE_ONE) || event.getAction().equals(
                    InventoryAction.PLACE_SOME))) {
            event.setCancelled(event.getRawSlot() < (event.getView().countSlots() - 36));
            return;
        } else if (isJournal(event.getCurrentItem())
        // this is just moving it to other inventory
            && event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onJournalDrag(InventoryDragEvent event) {
        // this is moving the item across the inventory outside of Player's
        // inventory
        if (isJournal(event.getOldCursor())) {
            for (Integer slot : event.getRawSlots()) {
                if (slot < (event.getView().countSlots() - 36)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        // this prevents the journal from dropping on death by removing it from
        // the
        // list of drops
        List<ItemStack> drops = event.getDrops();
        ListIterator<ItemStack> litr = drops.listIterator();
        while (litr.hasNext()) {
            ItemStack stack = litr.next();
            if (isJournal(stack)) {
                litr.remove();
                return;
            }
        }
    }

    @EventHandler
    public void onItemFrameClick(PlayerInteractEntityEvent event) {
        // this prevents the journal from being placed inside of item frame
        if (event.getRightClicked() instanceof ItemFrame
            && isJournal(event.getPlayer().getItemInHand())) {
            event.setCancelled(true);
        }
    }

    /**
     * Adds journal to player inventory.
     * 
     * @param playerID
     *            ID of the player
     * @param slot
     *            slot number for adding the journal
     */
    public static void addJournal(String playerID, int slot) {
        // do nothing if the player already has a journal
        if (hasJournal(playerID)) {
            return;
        }
        Inventory inventory = PlayerConverter.getPlayer(playerID).getInventory();
        // if the slot is less than 0 then use default slot
        if (slot < 0) {
            slot = 8;
        }
        // generate journal and place it in the slot
        ItemStack item = generateJournal(playerID);
        if (inventory.firstEmpty() >= 0) {
            ItemStack oldItem = inventory.getItem(slot);
            inventory.setItem(slot, item);
            // move the item that was previously there
            if (oldItem != null) {
                inventory.addItem(oldItem);
            }
        } else {
            // if there is no place for the item then print a message about it
            SimpleTextOutput.sendSystemMessage(
                    playerID,
                    ConfigHandler.getString("messages."
                        + ConfigHandler.getString("config.language") + ".inventory_full"),
                    ConfigHandler.getString("config.sounds.full"));
        }
    }

    /**
     * Updates journal by removing it and adding it again
     * 
     * @param playerID
     *            ID of the player
     */
    public static void updateJournal(String playerID) {
        if (hasJournal(playerID)) {
            int slot = removeJournal(playerID);
            addJournal(playerID, slot);
        }

    }

    /**
     * Checks if the player has his journal in the inventory
     * 
     * @param playerID
     *            ID of the player
     * @return true if the player has his journal, false otherwise
     */
    public static boolean hasJournal(String playerID) {
        for (ItemStack item : PlayerConverter.getPlayer(playerID).getInventory().getContents()) {
            if (isJournal(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes journal from player's inventory.
     * 
     * @param playerID
     *            ID of the player
     * @return the slot from which the journal was removed
     */
    public static int removeJournal(String playerID) {
        // loop all items and check if any of them is a journal
        Inventory inventory = PlayerConverter.getPlayer(playerID).getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (isJournal(inventory.getItem(i))) {
                inventory.setItem(i, new ItemStack(Material.AIR));
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks if the item is journal
     * 
     * @param item
     *            ItemStack to check against being the journal
     * @return true if the ItemStack is the journal, false otherwise
     */
    private static boolean isJournal(ItemStack item) {
        // if there is no item then it's not a journal
        if (item == null) {
            return false;
        }
        // check all properties of the item and return the result
        return (item.getType().equals(Material.WRITTEN_BOOK)
            && ((BookMeta) item.getItemMeta()).hasTitle()
            && ((BookMeta) item.getItemMeta()).getTitle().equals(
                    ConfigHandler.getString(
                            "messages." + ConfigHandler.getString("config.language")
                                + ".journal_title").replaceAll("&", "§"))
            && item.getItemMeta().hasLore() && item
                .getItemMeta()
                .getLore()
                .contains(
                        ConfigHandler.getString(
                                "messages." + ConfigHandler.getString("config.language")
                                    + ".journal_lore").replaceAll("&", "§")));
    }

    /**
     * Generates the journal using specified player's Journal object
     * 
     * @param playerID
     *            ID of the player
     * @return the journal ItemStack
     */
    private static ItemStack generateJournal(String playerID) {
        // create the book with default title/author
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) item.getItemMeta();
        meta.setTitle(ConfigHandler.getString(
                "messages." + ConfigHandler.getString("config.language") + ".journal_title")
                .replaceAll("&", "§"));
        meta.setAuthor(PlayerConverter.getPlayer(playerID).getName());
        List<String> lore = new ArrayList<String>();
        lore.add(ConfigHandler.getString(
                "messages." + ConfigHandler.getString("config.language") + ".journal_lore")
                .replaceAll("&", "§"));
        meta.setLore(lore);

        // logic for converting entries into single text and then to pages
        StringBuilder stringBuilder = new StringBuilder();
        for (String entry : BetonQuest.getInstance().getDBHandler(playerID).getJournal().getText()) {
            stringBuilder.append(entry.replaceAll("&", "§") + "\n§"
                + ConfigHandler.getString("config.journal_colors.line") + "---------------\n");
        }
        String wholeString = stringBuilder.toString().trim();

        // return ready journal ItemStack
        meta.setPages(Utils.pagesFromString(wholeString, true));
        item.setItemMeta(meta);
        return item;
    }
}
