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
public class EventAddExp extends QuestEvent {
    private final String sJobName;

    private final double nAddExperience;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public EventAddExp(final Instruction instruction) throws QuestException {
        super(instruction, true);
        if (instruction.size() < 2) {
            throw new QuestException("Not enough arguments");
        }
        for (final Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(instruction.getPart(1))) {
                sJobName = job.getName();
                try {
                    this.nAddExperience = Double.parseDouble(instruction.getPart(2));
                } catch (final NumberFormatException err) {
                    throw new QuestException("NUJobs_AddExp: Unable to parse the experience amount", err);
                }
                return;
            }
        }
        throw new QuestException("Jobs Reborn job " + instruction.getPart(1) + " does not exist");
    }

    @Override
    protected Void execute(final Profile profile) {
        final List<JobProgression> oJobs = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression();
        for (final JobProgression oJob : oJobs) {
            if (oJob.getJob().getName().equalsIgnoreCase(sJobName)) {
                //User has the job, return true
                oJob.addExperience(nAddExperience);
            }
        }
        return null;
    }
}
