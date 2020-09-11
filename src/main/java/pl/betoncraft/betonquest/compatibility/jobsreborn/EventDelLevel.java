package pl.betoncraft.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.List;

public class EventDelLevel extends QuestEvent {
    private String sJobName;
    private Integer nAddLevel;

    public EventDelLevel(final Instruction instructions) throws InstructionParseException {
        super(instructions, true);

        if (instructions.size() < 3) {
            throw new InstructionParseException("Not enough arguments");
        }
        for (final Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(instructions.getPart(1))) {
                sJobName = job.getName();
                try {
                    this.nAddLevel = Integer.parseInt(instructions.getPart(2));
                } catch (Exception e) {
                    throw new InstructionParseException("NUJobs_DelLevel: Unable to parse the level amount", e);
                }
                return;
            }
        }
        throw new InstructionParseException("Jobs Reborn job " + instructions.getPart(1) + " does not exist");
    }

    @Override
    protected Void execute(final String playerID) {
        final Player oPlayer = PlayerConverter.getPlayer(playerID);

        final List<JobProgression> oJobs = Jobs.getPlayerManager().getJobsPlayer(oPlayer).getJobProgression();
        for (final JobProgression oJob : oJobs) {
            if (oJob.getJob().getName().equalsIgnoreCase(sJobName)) {
                //User has the job, return true
                oJob.setLevel(oJob.getLevel() - this.nAddLevel);
                if (oJob.getLevel() < 1) {
                    oJob.setLevel(1);
                }
            }
        }
        return null;
    }
}
