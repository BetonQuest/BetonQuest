package org.betonquest.betonquest.quest.objective.logout;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Requires the player to leave the server.
 */
public class LogoutObjective extends DefaultObjective {

    /**
     * Constructor for the LogoutObjective.
     *
     * @param service the objective factory service
     * @throws QuestException if there is an error in the instruction
     */
    public LogoutObjective(final ObjectiveFactoryService service) throws QuestException {
        super(service);
    }

    /**
     * Check if the player has left the server.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that left
     */
    public void onQuit(final PlayerQuitEvent event, final OnlineProfile onlineProfile) {
        getService().complete(onlineProfile);
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
