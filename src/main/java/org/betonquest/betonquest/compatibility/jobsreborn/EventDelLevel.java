package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class EventDelLevel extends QuestEvent {
    private final String sJobName;

    private final Integer nAddLevel;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public EventDelLevel(final Instruction instructions) throws QuestException {
        super(instructions, true);

        if (instructions.size() < 3) {
            throw new QuestException("Not enough arguments");
        }
        for (final Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(instructions.getPart(1))) {
                sJobName = job.getName();
                try {
                    this.nAddLevel = Integer.parseInt(instructions.getPart(2));
                } catch (final NumberFormatException e) {
                    throw new QuestException("NUJobs_DelLevel: Unable to parse the level amount", e);
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
            if (oJob.getJob().getName().equalsIgnoreCase(sJobName)) {
                //User has the job, return true
                oJob.setLevel(oJob.getLevel() - this.nAddLevel);
                if (oJob.getLevel() <= 0) {
                    oJob.setLevel(1);
                }
            }
        }
        return null;
    }
}
