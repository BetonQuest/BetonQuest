package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.profiles.ProfileEvent;
import org.betonquest.betonquest.conversation.Conversation;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fires when profile ends a conversation with an NPC
 */
@SuppressWarnings("PMD.CommentRequired")
public class PlayerConversationStartEvent extends ProfileEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Conversation conversation;

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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * @return the conversation which has been ended
     */
    public Conversation getConversation() {
        return conversation;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(final boolean arg) {
        canceled = arg;
    }

}
