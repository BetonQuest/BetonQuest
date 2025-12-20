package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsLeaveEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Objective that tracks the leave of a player from a specific job.
 */
public class LeaveJobObjective extends Objective implements Listener {

    /**
     * Job to leave.
     */
    private final Variable<Job> job;

    /**
     * Constructor for the LeaveJobObjective.
     *
     * @param instruction the instruction of the objective
     * @param job         the job to leave
     * @throws QuestException if the instruction is invalid
     */
    public LeaveJobObjective(final DefaultInstruction instruction, final Variable<Job> job) throws QuestException {
        super(instruction);
        this.job = job;
    }

    /**
     * Handles the JobsLeaveEvent.
     *
     * @param event the event that triggered the leave
     */
    @EventHandler(ignoreCancelled = true)
    public void onJobsLeaveEvent(final JobsLeaveEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer().getPlayer());
        qeHandler.handle(() -> {
            if (event.getJob().isSame(this.job.getValue(onlineProfile)) && containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
        });
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
