package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.profiles.ProfileEvent;
import org.betonquest.betonquest.conversation.Conversation;
import org.bukkit.event.HandlerList;

/**
 * Fires when a profile starts a conversation with an NPC
 */
@SuppressWarnings("PMD.CommentRequired")
public class PlayerConversationEndEvent extends ProfileEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Conversation conversation;

    /**
     * Creates new conversation start event
     *
     * @param who          the {@link Profile} who ended the conversation
     * @param conversation conversation which has been ended
     */
    public PlayerConversationEndEvent(final Profile who, final Conversation conversation) {
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
