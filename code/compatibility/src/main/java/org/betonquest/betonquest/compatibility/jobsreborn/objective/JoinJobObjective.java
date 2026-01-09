package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * Objective that tracks the join of a player to a specific job.
 */
public class JoinJobObjective extends DefaultObjective {

    /**
     * Job to join.
     */
    private final Argument<Job> job;

    /**
     * Constructor for the JoinJobObjective.
     *
     * @param service the objective factory service
     * @param job     the job to join
     * @throws QuestException if the instruction is invalid
     */
    public JoinJobObjective(final ObjectiveFactoryService service, final Argument<Job> job) throws QuestException {
        super(service);
        this.job = job;
    }

    /**
     * Handles the JobsJoinEvent.
     *
     * @param event         the event that triggered the join
     * @param onlineProfile the profile related to the player that joined
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onJobsJoinEvent(final JobsJoinEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (event.getJob().isSame(this.job.getValue(onlineProfile))) {
            getService().complete(onlineProfile);
        }
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
