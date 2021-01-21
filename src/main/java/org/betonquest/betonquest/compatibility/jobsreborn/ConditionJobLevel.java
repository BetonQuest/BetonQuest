package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class ConditionJobLevel extends Condition {
    private final String sJobName;
    private final int nMinLevel;
    private final int nMaxLevel;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ConditionJobLevel(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        if (instruction.size() < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        for (final Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(instruction.getPart(1))) {
                sJobName = job.getName();
                try {
                    this.nMinLevel = Integer.parseInt(instruction.getPart(2));
                    this.nMaxLevel = Integer.parseInt(instruction.getPart(3));
                } catch (InstructionParseException e) {
                    throw new InstructionParseException("NUJobs_Joblevel: Unable to parse the min or max level", e);
                }
                return;
            }
        }
        throw new InstructionParseException("Jobs Reborn job " + instruction.getPart(1) + " does not exist");
    }

    @Override
    protected Boolean execute(final String playerID) {
        final Player oPlayer = PlayerConverter.getPlayer(playerID);

        final List<JobProgression> oJobs = Jobs.getPlayerManager().getJobsPlayer(oPlayer).getJobProgression();
        for (final JobProgression oJob : oJobs) {
            if (oJob.getJob().getName().equalsIgnoreCase(sJobName) && oJob.getLevel() >= nMinLevel && oJob.getLevel() <= nMaxLevel) {
                return true;
            }
        }
        return false;
    }
}
