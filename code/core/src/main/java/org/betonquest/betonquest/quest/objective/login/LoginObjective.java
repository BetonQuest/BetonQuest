package org.betonquest.betonquest.quest.objective.login;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Requires the player to join the server.
 */
public class LoginObjective extends DefaultObjective {

    /**
     * Constructor for the LoginObjective.
     *
     * @param instruction the instruction that created this objective
     * @throws QuestException if there is an error in the instruction
     */
    public LoginObjective(final Instruction instruction) throws QuestException {
        super(instruction);
    }

    /**
     * Check if the player has joined the server.
     *
     * @param event         the event that triggers when the player joins
     * @param onlineProfile the profile of the player that joined
     */
    public void onJoin(final PlayerJoinEvent event, final OnlineProfile onlineProfile) {
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
