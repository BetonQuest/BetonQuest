package org.betonquest.betonquest.quest.objective.jump;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;

/**
 * Requires the player to jump a certain number of times.
 */
public class JumpObjective extends CountingObjective {

    /**
     * Constructor for the JumpObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the target amount of jumps
     * @throws QuestException if there is an error in the instruction
     */
    public JumpObjective(final Instruction instruction, final Argument<Number> targetAmount) throws QuestException {
        super(instruction, targetAmount, "times_to_jump");
    }

    /**
     * Check if the player jumped.
     *
     * @param event         the event that triggered the jump
     * @param onlineProfile the profile of the player that jumped
     */
    public void onPlayerJump(final PlayerJumpEvent event, final OnlineProfile onlineProfile) {
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
        }
    }
}
