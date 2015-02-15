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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.betoncraft.betonquest.config.ConfigHandler;

import com.google.common.collect.Lists;

/**
 * Represents player's journal.
 * 
 * @author Co0sh
 */
public class Journal {

    /**
     * List of pointers in this Journal.
     */
    private List<Pointer> pointers;
    /**
     * List of texts generated from pointers.
     */
    private List<String> texts = new ArrayList<String>();

    /**
     * Creates new Journal instance from List of Pointers.
     * 
     * @param list
     *            list of pointers to journal entries
     */
    public Journal(List<Pointer> list) {
        // generate texts from list of pointers
        pointers = list;
        generateTexts();
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
    public void addPointer(String pointer, Timestamp timestamp) {
        pointers.add(new Pointer(pointer, timestamp));
        generateTexts();
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
    public void generateTexts() {
        texts.clear();
        for (Pointer pointer : pointers) {
            String date = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(pointer.getTimestamp());
            String day = "§" + ConfigHandler.getString("config.journal_colors.date.day")
                + date.split(" ")[0];
            String hour = "§" + ConfigHandler.getString("config.journal_colors.date.hour")
                + date.split(" ")[1];
            texts.add(day + " " + hour + "§"
                + ConfigHandler.getString("config.journal_colors.text") + "\n"
                + ConfigHandler.getString("journal." + pointer.getPointer()));
        }
    }

    /**
     * Clears the Journal completely.
     */
    public void clear() {
        texts.clear();
        pointers.clear();
    }
}
