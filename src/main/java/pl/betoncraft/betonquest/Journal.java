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

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
import pl.betoncraft.betonquest.database.Saver.Record;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Represents player's journal.
 *
 * @author Jakub Sapalski
 */
public class Journal {

    private String playerID;
    private List<Pointer> pointers;
    private List<String> texts = new ArrayList<>();
    private String lang;
    private String mainPage;

    /**
     * Creates new Journal instance from List of Pointers.
     *
     * @param playerID ID of the player whose journal is created
     * @param list     list of pointers to journal entries
     * @param lang     default language to use when generating the journal
     */
    public Journal(final String playerID, final String lang, final List<Pointer> list) {
        // generate texts from list of pointers
        this.playerID = playerID;
        this.lang = lang;
        pointers = list;
    }

    /**
     * Checks if the item is journal
     *
     * @param playerID ID of the player
     * @param item     ItemStack to check against being the journal
     * @return true if the ItemStack is the journal, false otherwise
     */
    public static boolean isJournal(final String playerID, final ItemStack item) {
        // if there is no item then it's not a journal
        if (item == null) {
            return false;
        }
        // get language
        final String playerLang = BetonQuest.getInstance().getPlayerData(playerID).getLanguage();
        // check all properties of the item and return the result
        return item.getType().equals(Material.WRITTEN_BOOK) && ((BookMeta) item.getItemMeta()).hasTitle()
                && ((BookMeta) item.getItemMeta()).getTitle().equals(Config.getMessage(playerLang, "journal_title"))
                && item.getItemMeta().hasLore()
                && item.getItemMeta().getLore().contains(Config.getMessage(playerLang, "journal_lore"));
    }

    /**
     * Checks if the player has his journal in the inventory. Returns false if
     * the player is not online.
     *
     * @param playerID ID of the player
     * @return true if the player has his journal, false otherwise
     */
    public static boolean hasJournal(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        if (player == null) {
            return false;
        }
        for (final ItemStack item : player.getInventory().getContents()) {
            if (isJournal(playerID, item)) {
                return true;
            }
        }
        return false;
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
     * Adds pointer to the journal. It needs to be updated now.
     *
     * @param pointer the pointer to be added
     */
    public void addPointer(final Pointer pointer) {
        pointers.add(pointer);
        // SQLite doesn't accept formatted date and MySQL doesn't accept numeric
        // timestamp
        final String date = BetonQuest.getInstance().isMySQLUsed()
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(pointer.getTimestamp()))
                : Long.toString(pointer.getTimestamp());
        BetonQuest.getInstance().getSaver()
                .add(new Record(UpdateType.ADD_JOURNAL, new String[]{playerID, pointer.getPointer(), date}));
    }

    /**
     * Removes the pointer from journal. It needs to be updated now.
     *
     * @param pointerName the name of the pointer to remove
     */
    public void removePointer(final String pointerName) {
        for (final Iterator<Pointer> iterator = pointers.iterator(); iterator.hasNext(); ) {
            final Pointer pointer = iterator.next();
            if (pointer.getPointer().equalsIgnoreCase(pointerName)) {
                final String date = BetonQuest.getInstance().isMySQLUsed()
                        ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(pointer.getTimestamp()))
                        : Long.toString(pointer.getTimestamp());
                BetonQuest.getInstance().getSaver()
                        .add(new Record(UpdateType.REMOVE_JOURNAL, new String[]{playerID, pointer.getPointer(), date}));
                pointers.remove(pointer);
                break;
            }
        }
    }

    /**
     * Retrieves the list of generated texts.
     *
     * @return list of Strings - texts for every journal entry
     */
    public List<String> getText() {
        final List<String> list;
        if (Config.getString("config.journal.reversed_order").equalsIgnoreCase("true")) {
            list = Lists.reverse(texts);
        } else {
            list = new ArrayList<>(texts);
        }
        final List<String> pagesList = new ArrayList<>();
        for (final String entry : list) {
            pagesList.addAll(Utils.pagesFromString(entry));
        }
        return pagesList;
    }

    /**
     * Generates texts for every pointer and places them inside a List
     *
     * @param lang the language to use while generating text
     */
    public void generateTexts(final String lang) {
        // remove previous texts
        texts.clear();
        this.lang = lang;
        // generate the first page
        mainPage = generateMainPage();
        for (final Pointer pointer : pointers) {
            // if date should not be hidden, generate the date prefix
            String datePrefix = "";
            if (Config.getString("config.journal.hide_date").equalsIgnoreCase("false")) {
                final String date = new SimpleDateFormat(Config.getString("config.date_format"))
                        .format(pointer.getTimestamp());
                final String[] dateParts = date.split(" ");
                final String day = "§" + Config.getString("config.journal_colors.date.day") + dateParts[0];
                String hour = "";
                if (dateParts.length > 1) {
                    hour = "§" + Config.getString("config.journal_colors.date.hour") + dateParts[1];
                }
                datePrefix = day + " " + hour + "\n";
            }
            // get package and name of the pointer
            final String[] parts = pointer.getPointer().split("\\.");
            final String packName = parts[0];
            final ConfigPackage pack = Config.getPackages().get(packName);
            if (pack == null) {
                continue;
            }
            final String pointerName = parts[1];
            // resolve the text in player's language
            String text;
            if (pack.getJournal().getConfig().contains(pointerName)) {
                if (pack.getJournal().getConfig().isConfigurationSection(pointerName)) {
                    text = pack.getFormattedString("journal." + pointerName + "." + lang);
                    if (text == null) {
                        text = pack.getFormattedString("journal." + pointerName + "." + Config.getLanguage());
                    }
                } else {
                    text = pack.getFormattedString("journal." + pointerName);
                }
            } else {
                LogUtils.getLogger().log(Level.WARNING, "No defined journal entry " + pointerName + " in package " + pack.getName());
                text = "error";
            }

            // handle case when the text isn't defined
            if (text == null) {
                LogUtils.getLogger().log(Level.WARNING, "No text defined for journal entry " + pointerName + " in language " + lang);
                text = "error";
            }

            // resolve variables
            for (final String variable : BetonQuest.resolveVariables(text)) {
                try {
                    BetonQuest.createVariable(pack, variable);
                } catch (InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Error while creating variable '" + variable + "' on journal page '" + pointerName + "' in "
                            + PlayerConverter.getName(playerID) + "'s journal: " + e.getMessage());
                    LogUtils.logThrowable(e);
                }
                text = text.replace(variable,
                        BetonQuest.getInstance().getVariableValue(packName, variable, playerID));
            }

            // add the entry to the list
            texts.add(datePrefix + "§" + Config.getString("config.journal_colors.text") + text);
        }
    }

    /**
     * Generates the main page for this journal.
     *
     * @return the main page string or null, if there is no main page
     */
    private String generateMainPage() {
        final HashMap<Integer, ArrayList<String>> lines = new HashMap<>(); // holds text lines with their priority
        final HashSet<Integer> numbers = new HashSet<>(); // stores numbers that are used, so there's no need to search them
        for (final ConfigPackage pack : Config.getPackages().values()) {
            final String packName = pack.getName();
            final ConfigurationSection section = pack.getMain().getConfig().getConfigurationSection("journal_main_page");
            if (section == null) {
                continue;
            }
            // handle every entry
            keys:
            for (final String key : section.getKeys(false)) {
                final int number = section.getInt(key + ".priority", -1);
                // only add entry if the priority is set and not doubled
                if (number >= 0) {
                    // check conditions and continue loop if not met
                    final String rawConditions = section.getString(key + ".conditions");
                    if (rawConditions != null && rawConditions.length() > 0) {
                        for (final String condition : rawConditions.split(",")) {
                            try {
                                final ConditionID conditionID = new ConditionID(pack, condition);
                                if (!BetonQuest.condition(playerID, conditionID)) {
                                    continue keys;
                                }
                            } catch (ObjectNotFoundException e) {
                                LogUtils.getLogger().log(Level.WARNING, "Error while generatin main page in " + PlayerConverter.getPlayer(playerID)
                                        + "'s journal - condition '" + condition + "' not found: " + e.getMessage());
                                LogUtils.logThrowable(e);
                                continue keys;
                            }
                        }
                    }
                    // here conditions are met, get the text in player's language
                    String text;
                    if (section.isConfigurationSection(key + ".text")) {
                        text = section.getString(key + ".text." + lang);
                        if (text == null) {
                            text = section.getString(key + ".text." + Config.getLanguage());
                        }
                        if (text == null) {
                            text = section.getString(key + ".text.en");
                        }
                    } else {
                        text = section.getString(key + ".text");
                    }
                    if (text == null || text.length() <= 0) {
                        continue;
                    }
                    // resolve variables
                    for (final String variable : BetonQuest.resolveVariables(text)) {
                        try {
                            BetonQuest.createVariable(pack, variable);
                        } catch (InstructionParseException e) {
                            LogUtils.getLogger().log(Level.WARNING, "Error while creating variable '" + variable + "' on main page in "
                                    + PlayerConverter.getName(playerID) + "'s journal: " + e.getMessage());
                            LogUtils.logThrowable(e);
                        }
                        text = text.replace(variable,
                                BetonQuest.getInstance().getVariableValue(packName, variable, playerID));
                    }
                    text = pack.subst(text);
                    // add the text to HashMap
                    numbers.add(number);
                    final ArrayList<String> linesOrder;
                    if (lines.containsKey(number)) {
                        linesOrder = lines.get(number);
                    } else {
                        linesOrder = new ArrayList<>();
                        lines.put(number, linesOrder);
                    }
                    linesOrder.add(text + "§r"); // reset the formatting
                } else {
                    LogUtils.getLogger().log(Level.WARNING, "Priority of " + packName + "." + key
                            + " journal main page line is not defined");
                    continue;
                }
            }
        }
        if (numbers.isEmpty()) {
            return null;
        }
        // now all lines from all packages are extracted, sort numbers
        Integer[] sorted = new Integer[numbers.size()];
        sorted = numbers.toArray(sorted);
        Arrays.sort(sorted);
        // build the string and return it
        final ArrayList<String> sortedLines = new ArrayList<>();
        for (final int i : sorted) {
            final ArrayList<String> linesOrder = lines.get(i);
            String[] sortedlinesOrder = new String[linesOrder.size()];
            sortedlinesOrder = linesOrder.toArray(sortedlinesOrder);
            Arrays.sort(sortedlinesOrder);
            for (final String s : sortedlinesOrder) {
                sortedLines.add(s);
            }
        }
        return StringUtils.join(sortedLines, '\n').replace('&', '§');
    }

    /**
     * Clears the Journal completely but doesn't touch the database.
     */
    public void clear() {
        texts.clear();
        pointers.clear();
    }

    /**
     * Adds journal to player inventory.
     *
     * @param slot slot number for adding the journal
     */
    public void addToInv(int slot) {
        // remove the old journal if it exists
        if (hasJournal(playerID)) {
            slot = removeFromInv();
        }
        // update the texts
        generateTexts(lang);
        final Inventory inventory = PlayerConverter.getPlayer(playerID).getInventory();
        // if the slot is less than 0 then use default slot
        if (slot < 0) {
            slot = 8;
        }
        // generate journal and place it in the slot
        final ItemStack item = getAsItem();
        if (inventory.firstEmpty() >= 0) {
            final ItemStack oldItem = inventory.getItem(slot);
            inventory.setItem(slot, item);
            // move the item that was previously there
            if (oldItem != null) {
                inventory.addItem(oldItem);
            }
        } else {
            // if there is no place for the item then print a message about it
            Config.sendNotify(playerID, "inventory_full", null, "inventory_full,error");
        }
    }

    /**
     * Generates the journal as ItemStack
     *
     * @return the journal ItemStack
     */
    public ItemStack getAsItem() {
        // create the book with default title/author
        final ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta meta = (BookMeta) item.getItemMeta();
        meta.setTitle(Utils.format(Config.getMessage(lang, "journal_title")));
        meta.setAuthor(PlayerConverter.getPlayer(playerID).getName());
        final List<String> lore = new ArrayList<>();
        lore.add(Utils.format(Config.getMessage(lang, "journal_lore")));
        meta.setLore(lore);
        // add main page and generate pages from texts
        final List<String> finalList = new ArrayList<>();
        if (Config.getString("config.journal.one_entry_per_page").equalsIgnoreCase("false")) {
            final String color = Config.getString("config.journal_colors.line");
            String separator = Config.parseMessage(playerID, "journal_separator", null);
            if (separator == null) {
                separator = "---------------";
            }
            String line = "\n§" + color + separator + "\n";

            if (Config.getString("config.journal.show_separator") != null &&
                    Config.getString("config.journal.show_separator").equalsIgnoreCase("false")) {
                line = "\n";
            }

            final StringBuilder stringBuilder = new StringBuilder();
            for (final String entry : getText()) {
                stringBuilder.append(entry).append(line);
            }
            if (mainPage != null && mainPage.length() > 0) {
                if (Config.getString("config.journal.full_main_page").equalsIgnoreCase("true")) {
                    finalList.addAll(Utils.pagesFromString(mainPage));
                } else {
                    stringBuilder.insert(0, mainPage + line);
                }
            }
            final String wholeString = stringBuilder.toString().trim();
            finalList.addAll(Utils.pagesFromString(wholeString));
        } else {
            if (mainPage != null && mainPage.length() > 0) {
                finalList.addAll(Utils.pagesFromString(mainPage));
            }
            finalList.addAll(getText());
        }
        if (finalList.size() > 0) {
            meta.setPages(Utils.multiLineColorCodes(finalList, "§" + Config.getString("config.journal_colors.line")));
        } else {
            meta.addPage("");
        }
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Updates journal by removing it and adding it again
     */
    public void update() {
        if (hasJournal(playerID)) {
            lang = BetonQuest.getInstance().getPlayerData(playerID).getLanguage();
            final int slot = removeFromInv();
            addToInv(slot);
        }
    }

    /**
     * Removes journal from player's inventory.
     *
     * @return the slot from which the journal was removed
     */
    public int removeFromInv() {
        // loop all items and check if any of them is a journal
        final Inventory inventory = PlayerConverter.getPlayer(playerID).getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (isJournal(playerID, inventory.getItem(i))) {
                inventory.setItem(i, new ItemStack(Material.AIR));
                return i;
            }
        }
        return -1;
    }
}
