package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;

/**
 * Objective that tracks the level up of a player in a specific job.
 */
public class LevelUpObjective extends DefaultObjective {

    /**
     * Job to level up.
     */
    private final Argument<Job> job;

    /**
     * Constructor for the LevelUpObjective.
     *
     * @param instruction the instruction of the objective
     * @param job         the job to level up
     * @throws QuestException if the instruction is invalid
     */
    public LevelUpObjective(final Instruction instruction, final Argument<Job> job) throws QuestException {
        super(instruction);
        this.job = job;
    }

    /**
     * Handles the JobsLevelUpEvent.
     *
     * @param event         the event that triggered the level up
     * @param onlineProfile the profile related to the player that leveled up
     */
    public void onJobsLevelUpEvent(final JobsLevelUpEvent event, final OnlineProfile onlineProfile) {
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
