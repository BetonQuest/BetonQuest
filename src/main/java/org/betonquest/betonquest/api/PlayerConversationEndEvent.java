package org.betonquest.betonquest.api;

import org.betonquest.betonquest.conversation.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fires when a player starts a conversation with an NPC
 */
@SuppressWarnings("PMD.CommentRequired")
public class PlayerConversationEndEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Conversation conversation;

    /**
     * Creates new conversation start event
     *
     * @param who          player
     * @param conversation conversation which has been started
     */
    public PlayerConversationEndEvent(final Player who, final Conversation conversation) {
        super(who);
        this.conversation = conversation;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * @return the conversation which has been started
     */
    public Conversation getConversation() {
        return conversation;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
