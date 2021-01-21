package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;

@SuppressWarnings("PMD.CommentRequired")
public class ConditionJobFull extends Condition {
    private final String sJobName;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ConditionJobFull(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        if (instruction.size() < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        for (final Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(instruction.getPart(1))) {
                sJobName = job.getName();
                return;
            }
        }
        throw new InstructionParseException("Jobs Reborn job " + instruction.getPart(1) + " does not exist");
    }

    @Override
    protected Boolean execute(final String playerID) {
        for (final Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(sJobName)) {
                if (job.getMaxSlots() == null) {
                    return false;
                }
                if (job.getTotalPlayers() >= job.getMaxSlots()) {
                    return true;
                }
            }
        }
        return false;
    }
}
