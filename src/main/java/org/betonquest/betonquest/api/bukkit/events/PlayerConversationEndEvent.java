package org.betonquest.betonquest.api.bukkit.events;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.profiles.ProfileEvent;
import org.betonquest.betonquest.conversation.Conversation;
import org.bukkit.event.HandlerList;

/**
 * Fires when a profile starts a conversation with an NPC.
 */
public class PlayerConversationEndEvent extends ProfileEvent {
    /**
     * A list of all handlers for this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The conversation that ended.
     */
    private final Conversation conversation;

    /**
     * Creates new conversation end event.
     *
     * @param who          the {@link Profile} who ended the conversation
     * @param conversation conversation which has been ended
     */
    public PlayerConversationEndEvent(final Profile who, final Conversation conversation) {
        super(who);
        this.conversation = conversation;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList.
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Get the ended conversation.
     *
     * @return the conversation which has been ended
     */
    public Conversation getConversation() {
        return conversation;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
