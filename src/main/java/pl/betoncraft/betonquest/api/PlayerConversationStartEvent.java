package pl.betoncraft.betonquest.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import pl.betoncraft.betonquest.conversation.Conversation;

/**
 * Fires when player ends a conversation with an NPC
 */
@SuppressWarnings("PMD.CommentRequired")
public class PlayerConversationStartEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Conversation conversation;
    private boolean canceled = false;

    /**
     * Creates new conversation end event.
     *
     * @param who          the player who started the conversation
     * @param conversation conversation which is about to start
     */
    public PlayerConversationStartEvent(final Player who, final Conversation conversation) {
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
