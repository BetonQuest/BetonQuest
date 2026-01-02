package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsLeaveEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;

/**
 * Objective that tracks the leave of a player from a specific job.
 */
public class LeaveJobObjective extends DefaultObjective {

    /**
     * Job to leave.
     */
    private final Argument<Job> job;

    /**
     * Constructor for the LeaveJobObjective.
     *
     * @param instruction the instruction of the objective
     * @param job         the job to leave
     * @throws QuestException if the instruction is invalid
     */
    public LeaveJobObjective(final Instruction instruction, final Argument<Job> job) throws QuestException {
        super(instruction);
        this.job = job;
    }

    /**
     * Handles the JobsLeaveEvent.
     *
     * @param event         the event that triggered the leave
     * @param onlineProfile the profile related to the player that left the job
     */
    public void onJobsLeaveEvent(final JobsLeaveEvent event, final OnlineProfile onlineProfile) {
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
