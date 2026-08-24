package org.betonquest.betonquest.compatibility.discordsrv.objective;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * Objective that tracks the linking of a player's Discord account.
 */
public class DiscordLinkObjective extends DefaultObjective {

    /**
     * Creates a new instance of the objective.
     *
     * @param service the {@link ObjectiveService} for this objective
     */
    public DiscordLinkObjective(final ObjectiveService service) {
        super(service);
    }

    /**
     * Called when the player has linked their Discord account.
     *
     * @param profile the player's profile
     */
    public void onLink(final Profile profile) {
        getService().complete(profile);
    }
}
