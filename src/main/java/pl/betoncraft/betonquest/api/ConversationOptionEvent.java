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
package pl.betoncraft.betonquest.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.betoncraft.betonquest.conversation.Conversation;

public class ConversationOptionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Conversation conv;
    private String selectedOption;
    private String npcResponse;

    public ConversationOptionEvent(Player player, Conversation conv, String playerChosen, String npcResponse) {
        this.player = player;
        this.conv = conv;
        this.selectedOption = playerChosen;
        this.npcResponse = npcResponse;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return the player who is having a conversation
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the conversation in which the option was selected
     */
    public Conversation getConversation() {
        return conv;
    }

    /**
     * @return the option chosen by the player
     */
    public String getSelectedOption() {
        return selectedOption;
    }

    /**
     * @return the option which is NPC's response
     */
    public String getNpcResponse() {
        return npcResponse;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}
