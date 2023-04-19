package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.profiles.ProfileEvent;
import org.betonquest.betonquest.conversation.Conversation;
import org.bukkit.event.HandlerList;

@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class ConversationOptionEvent extends ProfileEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Conversation conv;

    private final String selectedOption;

    private final String npcResponse;

    public ConversationOptionEvent(final Profile profile, final Conversation conv, final String selectedOption, final String npcResponse) {
        super(profile);
        this.conv = conv;
        this.selectedOption = selectedOption;
        this.npcResponse = npcResponse;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
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
