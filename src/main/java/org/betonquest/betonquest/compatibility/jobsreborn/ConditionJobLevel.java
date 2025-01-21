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
public class ConditionJobLevel extends Condition {
    private final String sJobName;

    private final int nMinLevel;

    private final int nMaxLevel;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ConditionJobLevel(final Instruction instruction) throws QuestException {
        super(instruction, true);
        if (instruction.size() < 2) {
            throw new QuestException("Not enough arguments");
        }
        for (final Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(instruction.getPart(1))) {
                sJobName = job.getName();
                try {
                    this.nMinLevel = Integer.parseInt(instruction.getPart(2));
                    this.nMaxLevel = Integer.parseInt(instruction.getPart(3));
                } catch (final QuestException e) {
                    throw new QuestException("NUJobs_Joblevel: Unable to parse the min or max level", e);
                }
                return;
            }
        }
        throw new QuestException("Jobs Reborn job " + instruction.getPart(1) + " does not exist");
    }

    @Override
    protected Boolean execute(final Profile profile) {
        final List<JobProgression> oJobs = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression();
        for (final JobProgression oJob : oJobs) {
            if (oJob.getJob().getName().equalsIgnoreCase(sJobName) && oJob.getLevel() >= nMinLevel && oJob.getLevel() <= nMaxLevel) {
                return true;
            }
        }
        return false;
    }
}
