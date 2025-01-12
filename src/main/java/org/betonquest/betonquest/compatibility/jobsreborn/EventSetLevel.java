package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class EventSetLevel extends QuestEvent {
    private final String sJobName;

    private final Integer nLevel;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public EventSetLevel(final Instruction instructions) throws QuestException {
        super(instructions, true);

        if (instructions.size() < 3) {
            throw new QuestException("Not enough arguments");
        }
        for (final Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(instructions.getPart(1))) {
                sJobName = job.getName();
                try {
                    this.nLevel = Integer.parseInt(instructions.getPart(2));
                } catch (final NumberFormatException e) {
                    throw new QuestException("NUJobs_SetLevel: Unable to parse the level amount", e);
                }
                return;
            }
        }
        throw new QuestException("Jobs Reborn job " + instructions.getPart(1) + " does not exist");
    }

    @Override
    protected Void execute(final Profile profile) {
        final List<JobProgression> oJobs = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression();
        for (final JobProgression oJob : oJobs) {
            if (oJob.getJob().getName().equalsIgnoreCase(sJobName) && oJob.getJob().getMaxLevel() <= this.nLevel) {
                oJob.setLevel(this.nLevel);
            }
        }
        return null;
    }
}
