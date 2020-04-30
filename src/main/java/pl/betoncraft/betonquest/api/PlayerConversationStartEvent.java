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
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import pl.betoncraft.betonquest.conversation.Conversation;

/**
 * Fires when player ends a conversation with an NPC
 *
 * @author Jakub Sapalski
 */
public class PlayerConversationStartEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Conversation conversation;
    private boolean canceled = false;

    /**
     * Creates new conversation end event.
     *
     * @param who          the player who started the conversation
     * @param conversation conversation which is about to start
     */
    public PlayerConversationStartEvent(Player who, Conversation conversation) {
        super(who);
        this.conversation = conversation;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return the conversation which has been ended
     */
    public Conversation getConversation() {
        return conversation;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean arg) {
        canceled = arg;
    }

}
