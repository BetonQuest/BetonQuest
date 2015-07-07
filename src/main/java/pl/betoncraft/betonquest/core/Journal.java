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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import com.google.common.collect.Lists;

/**
 * Represents player's journal.
 * 
 * @author Co0sh
 */
public class Journal {

    private String playerID;
    private List<Pointer> pointers;
    private List<String> texts = new ArrayList<>();
    private String lang;

    /**
     * Creates new Journal instance from List of Pointers.
     * 
     * @param list
     *            list of pointers to journal entries
     */
    public Journal(String playerID, String lang, List<Pointer> list) {
        // generate texts from list of pointers
        this.playerID = playerID;
        this.lang = lang;
        pointers = list;
        generateTexts(lang);
    }

    /**
     * Retrieves the list of pointers in this journal.
     * 
     * @return this Journal's list of pointers to journal entries
     */
    public List<Pointer> getPointers() {
        return pointers;
    }

    /**
     * Adds pointer to the journal and regenerates the texts
     * 
     * @param pointer
     *            the pointer to be added
     * @param timestamp
     *            Timestamp object containing the date of aquiring the entry
     */
    public void addPointer(Pointer pointer) {
        Debug.info("Adding new pointer \"" + pointer.getPointer() + "\" / " + pointer
                .getTimestamp() + " to journal for player " + PlayerConverter.getName(playerID));
        pointers.add(pointer);
        generateTexts(lang);
    }

    /**
     * Removes the pointer from journal and regenerates it
     * 
     * @param pointer
     *          the pointer to remove
     */
    public void removePointer(String pointer) {
        for (Iterator<Pointer> iterator = pointers.iterator(); iterator.hasNext();) {
            Pointer pointerr = (Pointer) iterator.next();
            if (pointerr.getPointer().equalsIgnoreCase(pointer)) {
                iterator.remove();
                break;
            }
        }
        generateTexts(lang);
    }

    /**
     * Retrieves the list of generated texts.
     * 
     * @return list of Strings - texts for every journal entry
     */
    public List<String> getText() {
        return Lists.reverse(texts);
    }

    /**
     * Generates texts for every pointer and places them inside a List
     */
    public void generateTexts(String lang) {
        texts.clear();
        this.lang = lang; 
        for (Pointer pointer : pointers) {
            String date = new SimpleDateFormat(Config.getString("messages.global.date_format")).format(pointer.getTimestamp());
            String[] dateParts = date.split(" ");
            String day = "§" + Config.getString("config.journal_colors.date.day") + dateParts[0];
            String hour = "";
            if (dateParts.length > 1) {
                hour = "§" + Config.getString("config.journal_colors.date.hour") + dateParts[1];
            }
            String[] parts = pointer.getPointer().split("\\.");
            String packName = parts[0];
            ConfigPackage pack = Config.getPackage(packName);
            if (pack == null) {
                continue;
            }
            String pointerName = parts[1];
            String text;
            if (pack.getJournal().getConfig().isConfigurationSection(pointerName)) {
                text = pack.getString("journal." + pointerName + "." + lang);
                if (text == null) {
                    text = pack.getString("journal." + pointerName + "." + Config.getLanguage());
                }
                if (text == null) {
                    Debug.error("No default language defined for journal pointer " + pointerName);
                    text = "error";
                }
            } else {
                text = pack.getString("journal." + pointerName);
            }
            texts.add(day + " " + hour + "§" + Config.getString("config.journal_colors.text")
                    + "\n" + text);
        }
    }

    /**
     * Clears the Journal completely.
     */
    public void clear() {
        texts.clear();
        pointers.clear();
    }
    
    /**
     * Adds journal to player inventory.
     * 
     * @param slot
     *            slot number for adding the journal
     */
    public void addToInv(int slot) {
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
        ItemStack item = getAsItem();
        if (inventory.firstEmpty() >= 0) {
            ItemStack oldItem = inventory.getItem(slot);
            inventory.setItem(slot, item);
            // move the item that was previously there
            if (oldItem != null) {
                inventory.addItem(oldItem);
            }
        } else {
            // if there is no place for the item then print a message about it
            Config.sendMessage(playerID, "inventory_full", null, "full");
        }
    }
    
    /**
     * Generates the journal as ItemStack
     * 
     * @param playerID
     *            ID of the player
     * @return the journal ItemStack
     */
    public ItemStack getAsItem() {
        // create the book with default title/author
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) item.getItemMeta();
        meta.setTitle(Config.getMessage(lang, "journal_title").replaceAll("&", "§"));
        meta.setAuthor(PlayerConverter.getPlayer(playerID).getName());
        List<String> lore = new ArrayList<String>();
        lore.add(Config.getMessage(lang, "journal_lore").replaceAll("&", "§"));
        meta.setLore(lore);

        // logic for converting entries into single text and then to pages
        StringBuilder stringBuilder = new StringBuilder();
        for (String entry : getText()) {
            stringBuilder.append(entry.replaceAll("&", "§") + "\n§"
                + Config.getString("config.journal_colors.line") + "---------------\n");
        }
        String wholeString = stringBuilder.toString().trim();

        // return ready journal ItemStack
        meta.setPages(Utils.pagesFromString(wholeString, true));
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Updates journal by removing it and adding it again
     * 
     * @param playerID
     *            ID of the player
     */
    public void update() {
        if (hasJournal(playerID)) {
            int slot = removeFromInv();
            generateTexts(lang);
            addToInv(slot);
        }

    }
    
    /**
     * Removes journal from player's inventory.
     * 
     * @param playerID
     *            ID of the player
     * @return the slot from which the journal was removed
     */
    public int removeFromInv() {
        // loop all items and check if any of them is a journal
        Inventory inventory = PlayerConverter.getPlayer(playerID).getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (isJournal(playerID, inventory.getItem(i))) {
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
    public static boolean isJournal(String playerID, ItemStack item) {
        // if there is no item then it's not a journal
        if (item == null) {
            return false;
        }
        // get language
        String playerLang = BetonQuest.getInstance().getDBHandler(playerID).getLanguage();
        // check all properties of the item and return the result
        return (item.getType().equals(Material.WRITTEN_BOOK)
            && ((BookMeta) item.getItemMeta()).hasTitle()
            && ((BookMeta) item.getItemMeta()).getTitle().equals(Config.getMessage(playerLang, "journal_title"))
            && item.getItemMeta().hasLore()
            && item.getItemMeta().getLore().contains(Config.getMessage(playerLang, "journal_lore")));
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
            if (isJournal(playerID, item)) {
                return true;
            }
        }
        return false;
    }
}
