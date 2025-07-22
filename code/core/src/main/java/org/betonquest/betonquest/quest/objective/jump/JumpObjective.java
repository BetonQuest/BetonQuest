package org.betonquest.betonquest.quest.objective.jump;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Requires the player to jump a certain number of times.
 */
public class JumpObjective extends CountingObjective implements Listener {

    /**
     * Constructor for the JumpObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the target amount of jumps
     * @throws QuestException if there is an error in the instruction
     */
    public JumpObjective(final Instruction instruction, final Variable<Number> targetAmount) throws QuestException {
        super(instruction, targetAmount, "times_to_jump");
    }

    /**
     * Check if the player jumped.
     *
     * @param event the event that triggered the jump
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJump(final PlayerJumpEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }
}
