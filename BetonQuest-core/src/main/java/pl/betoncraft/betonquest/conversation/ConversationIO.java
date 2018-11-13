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
package pl.betoncraft.betonquest.conversation;

/**
 * Used to display messages in conversation
 *
 * @author Jakub Sapalski
 */
public interface ConversationIO {

    /**
     * Set the text of response chosen by the NPC. Should be called once per
     * conversation cycle.
     *
     * @param npcName  the name of the NPC
     * @param response the text the NPC chose
     */
    void setNpcResponse(String npcName, String response);

    /**
     * Adds the text of the player option. Should be called for each option in a
     * conversation cycle.
     *
     * @param option the text of an option
     */
    void addPlayerOption(String option);

    /**
     * Displays all data to the player. Should be called after setting all
     * options.
     */
    void display();

    /**
     * Clears the data. Should be called before the cycle begins to ensure
     * nothing is left from previous one.
     */
    void clear();

    /**
     * Ends the work of this conversation IO. Should be called when the
     * conversation ends.
     */
    void end();

    /**
     * @return if this conversationIO should send messages to the player when the conversation starts and ends
     */
    default boolean printMessages() {
        return true;
    }

    /**
     * Send message through ConversationIO
     */
    default void print(String message) {
    }
}
