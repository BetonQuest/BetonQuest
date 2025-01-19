package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.conversation.Conversation;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fires when profile starts a conversation with an NPC.
 */
public class PlayerConversationStartEvent extends ProfileEvent implements Cancellable {
    /**
     * A list of all handlers for this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The conversation to start.
     */
    private final Conversation conversation;

    /**
     * If the event is cancelled.
     */
    private boolean canceled;

    /**
     * Creates new conversation end event.
     *
     * @param who          the {@link Profile} who started the conversation
     * @param conversation conversation which is about to start
     */
    public PlayerConversationStartEvent(final Profile who, final Conversation conversation) {
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
     * Get the conversation to start.
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

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        canceled = cancel;
    }
}
