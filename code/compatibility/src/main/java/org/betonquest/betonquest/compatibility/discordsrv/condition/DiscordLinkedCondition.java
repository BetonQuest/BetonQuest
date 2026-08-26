package org.betonquest.betonquest.compatibility.discordsrv.condition;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;

/**
 * Checks if the player is linked to Discord.
 */
public class DiscordLinkedCondition implements PlayerCondition {

    /**
     * Create the condition.
     */
    public DiscordLinkedCondition() {
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final AccountLinkManager linkManager = DiscordSRV.getPlugin().getAccountLinkManager();
        if (linkManager == null) {
            throw new QuestException("The account link manager of DiscordSRV is not correctly enabled!");
        }
        return linkManager.getDiscordId(profile.getPlayerUUID()) != null;
    }
}
