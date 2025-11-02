package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Objective that tracks the join of a player to a specific job.
 */
public class JoinJobObjective extends Objective implements Listener {

    /**
     * Job to join.
     */
    private final Variable<Job> job;

    /**
     * Constructor for the JoinJobObjective.
     *
     * @param instruction the instruction of the objective
     * @param job         the job to join
     * @throws QuestException if the instruction is invalid
     */
    public JoinJobObjective(final Instruction instruction, final Variable<Job> job) throws QuestException {
        super(instruction);
        this.job = job;
    }

    /**
     * Handles the JobsJoinEvent.
     *
     * @param event the event that triggered the join
     */
    @EventHandler(ignoreCancelled = true)
    public void onJobsJoinEvent(final JobsJoinEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer().getPlayer());
        qeHandler.handle(() -> {
            if (event.getJob().isSame(this.job.getValue(onlineProfile)) && containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
        });
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
