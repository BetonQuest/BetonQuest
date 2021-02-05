package org.betonquest.betonquest.api;

import org.betonquest.betonquest.conversation.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class ConversationOptionEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Conversation conv;
    private final String selectedOption;
    private final String npcResponse;

    public ConversationOptionEvent(final Player player, final Conversation conv, final String playerChosen, final String npcResponse) {
        super();
        this.player = player;
        this.conv = conv;
        this.selectedOption = playerChosen;
        this.npcResponse = npcResponse;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
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

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
