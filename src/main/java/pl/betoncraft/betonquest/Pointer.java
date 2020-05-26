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

/**
 * Represents the journal pointer.
 *
 * @author Jakub Sapalski
 */
public class Pointer {

    /**
     * String pointing to the journal entry in journal.yml
     */
    private final String pointer;
    /**
     * Timestamp indicating date of this entry
     */
    private final long timestamp;

    /**
     * Creates a new Pointer from the pointer string and relevant timestamp.
     *
     * @param pointer   the name of the journal entry
     * @param timestamp exact date this entry was added to journal
     */
    public Pointer(String pointer, long timestamp) {
        this.pointer = pointer;
        this.timestamp = timestamp;
    }

    /**
     * Returns the name of the journal entry from journal.yml.
     *
     * @return the name of the journal entry
     */
    public String getPointer() {
        return pointer;
    }

    /**
     * Returns the timestamp of the journal entry.
     *
     * @return the timestamp of the journal entry
     */
    public long getTimestamp() {
        return timestamp;
    }
}
