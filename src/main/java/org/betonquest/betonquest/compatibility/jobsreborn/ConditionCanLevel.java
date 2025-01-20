package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class ConditionCanLevel extends Condition {
    private final String sJobName;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ConditionCanLevel(final Instruction instruction) throws QuestException {
        super(instruction, true);
        if (instruction.size() < 2) {
            throw new QuestException("Not enough arguments");
        }
        for (final Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(instruction.getPart(1))) {
                sJobName = job.getName();
                return;
            }
        }
        throw new QuestException("Jobs Reborn job " + instruction.getPart(1) + " does not exist");
    }

    @Override
    protected Boolean execute(final Profile profile) {
        final List<JobProgression> oJobs = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression();
        for (final JobProgression oJob : oJobs) {
            if (oJob.getJob().getName().equalsIgnoreCase(sJobName) && oJob.canLevelUp()) {
                return true;
            }
        }
        return false;
    }
}
