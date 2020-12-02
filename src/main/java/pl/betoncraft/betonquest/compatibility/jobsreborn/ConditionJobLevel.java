package pl.betoncraft.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.List;

public class ConditionJobLevel extends Condition {
    private final String sJobName;
    private final int nMinLevel;
    private final int nMaxLevel;

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
