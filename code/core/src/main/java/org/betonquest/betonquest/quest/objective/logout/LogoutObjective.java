package org.betonquest.betonquest.quest.objective.logout;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Requires the player to leave the server.
 */
public class LogoutObjective extends DefaultObjective {

    /**
     * Constructor for the LogoutObjective.
     *
     * @param instruction the instruction that created this objective
     * @throws QuestException if there is an error in the instruction
     */
    public LogoutObjective(final Instruction instruction) throws QuestException {
        super(instruction);
    }

    /**
     * Check if the player has left the server.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that left
     */
    public void onQuit(final PlayerQuitEvent event, final OnlineProfile onlineProfile) {
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            completeObjective(onlineProfile);
        }
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
