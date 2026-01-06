package org.betonquest.betonquest.quest.event.teleport;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.betonquest.betonquest.conversation.Conversation;
import org.bukkit.Location;

/**
 * Teleports the player to specified location.
 */
public class TeleportEvent implements OnlineAction {

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Location to teleport to.
     */
    private final Argument<Location> location;

    /**
     * Create a new teleport event that teleports the player to the given location.
     *
     * @param conversationApi the Conversation API
     * @param location        location to teleport to
     */
    public TeleportEvent(final ConversationApi conversationApi, final Argument<Location> location) {
        this.conversationApi = conversationApi;
        this.location = location;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Conversation conv = conversationApi.getActive(profile);
        if (conv != null) {
            conv.endConversation();
        }
        final Location playerLocation = location.getValue(profile);
        profile.getPlayer().teleport(playerLocation);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
