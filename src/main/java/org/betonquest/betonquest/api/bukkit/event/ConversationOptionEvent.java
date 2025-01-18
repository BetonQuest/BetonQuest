package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ResolvedOption;
import org.bukkit.event.HandlerList;

/**
 * Signals that a player has selected an option in a conversation.
 */
@SuppressWarnings("PMD.DataClass")
public class ConversationOptionEvent extends ProfileEvent {
    /**
     * A list of all handlers for this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

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
     * Get the HandlerList of this event.
     *
     * @return the HandlerList.
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Get the conversation.
     *
     * @return the conversation in which the option was selected
     */
    public Conversation getConversation() {
        return conv;
    }

    /**
     * Get the selected option.
     *
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

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
