package org.betonquest.betonquest.compatibility.discordsrv.condition;

import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;

/**
 * Checks if the player is linked to Discord.
 */
public class DiscordLinkedCondition implements PlayerCondition {

    /**
     * The account link manager.
     */
    private final AccountLinkManager linkManager;

    /**
     * Create the condition.
     *
     * @param linkManager the account link manager
     */
    public DiscordLinkedCondition(final AccountLinkManager linkManager) {
        this.linkManager = linkManager;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return linkManager.getDiscordId(profile.getPlayerUUID()) != null;
    }
}
