package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.profiles.ProfileEvent;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ResolvedOption;
import org.bukkit.event.HandlerList;

/**
 * Signals that a player has selected an option in a conversation.
 */
@SuppressWarnings({"PMD.DataClass"})
public class ConversationOptionEvent extends ProfileEvent {
    /**
     * A list of all handlers for this event.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The conversation in which the option was selected.
     */
    private final Conversation conv;

    /**
     * The option chosen by the player.
     */
    private final ResolvedOption selectedOption;

    /**
     * The option that will be shown to the player as the NPC's response. Might be in another conversation.
     */
    private final ResolvedOption nextNPCOption;

    /**
     * Creates a new ConversationOptionEvent.
     *
     * @param profile              the profile that is in the conversation
     * @param conv                 the conversation in which the option was selected
     * @param selectedPlayerOption the option chosen by the player
     * @param nextNPCOption        the option which is NPC's response
     */
    public ConversationOptionEvent(final Profile profile, final Conversation conv, final ResolvedOption selectedPlayerOption, final ResolvedOption nextNPCOption) {
        super(profile);
        this.conv = conv;
        this.selectedOption = selectedPlayerOption;
        this.nextNPCOption = nextNPCOption;
    }

    /**
     * @return a list of all handlers for this event
     */
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
    public ResolvedOption getSelectedOption() {
        return selectedOption;
    }

    /**
     * The option that will be shown to the player as the NPC's response. Might be in another conversation.
     *
     * @return the option which is the NPC's response
     */
    public ResolvedOption getNextNPCOption() {
        return nextNPCOption;
    }

    /**
     * @return a list of all handlers for this event
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
