package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class EventAddLevel extends QuestEvent {
    private final String sJobName;
    private final Integer nAddLevel;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public EventAddLevel(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        if (instruction.size() < 3) {
            throw new InstructionParseException("Not enough arguments");
        }
        for (final Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(instruction.getPart(1))) {
                sJobName = job.getName();
                try {
                    this.nAddLevel = Integer.parseInt(instruction.getPart(2));
                } catch (NumberFormatException e) {
                    throw new InstructionParseException("NUJobs_AddLevel: Unable to parse the level amount", e);
                }
                return;
            }
        }
        throw new InstructionParseException("Jobs Reborn job " + instruction.getPart(1) + " does not exist");
    }

    @Override
    protected Void execute(final String playerID) {
        final Player oPlayer = PlayerConverter.getPlayer(playerID);

        final List<JobProgression> oJobs = Jobs.getPlayerManager().getJobsPlayer(oPlayer).getJobProgression();
        for (final JobProgression oJob : oJobs) {
            if (oJob.getJob().getName().equalsIgnoreCase(sJobName)) {
                oJob.setLevel(this.nAddLevel + oJob.getLevel());
            }
        }
        return null;
    }
}
