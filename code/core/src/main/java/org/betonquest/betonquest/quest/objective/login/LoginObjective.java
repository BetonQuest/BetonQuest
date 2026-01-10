package org.betonquest.betonquest.quest.objective.login;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveService;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Requires the player to join the server.
 */
public class LoginObjective extends DefaultObjective {

    /**
     * Constructor for the LoginObjective.
     *
     * @param service the objective factory service
     * @throws QuestException if there is an error in the instruction
     */
    public LoginObjective(final ObjectiveService service) throws QuestException {
        super(service);
    }

    /**
     * Check if the player has joined the server.
     *
     * @param event         the event that triggers when the player joins
     * @param onlineProfile the profile of the player that joined
     */
    public void onJoin(final PlayerJoinEvent event, final OnlineProfile onlineProfile) {
        getService().complete(onlineProfile);
    }
}
