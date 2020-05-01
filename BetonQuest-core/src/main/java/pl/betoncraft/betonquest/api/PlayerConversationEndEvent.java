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

/**
 * Fires when a player starts a conversation with an NPC
 *
 * @author Jakub Sapalski
 */
public class PlayerConversationEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Conversation conversation;

    /**
     * Creates new conversation start event
     *
     * @param conversation conversation which has been started
     */
    public PlayerConversationEndEvent(Conversation conversation) {
        super(false);
        this.conversation = conversation;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return the conversation which has been started
     */
    public Conversation getConversation() {
        return conversation;
    }

    public Player getPlayer() { return conversation.getPlayer(); }

    public HandlerList getHandlers() {
        return handlers;
    }

}
