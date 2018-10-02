package pl.betoncraft.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Event_LeaveJob extends QuestEvent
{
	private String sJobName;
	
    public Event_LeaveJob(Instruction instructions) throws InstructionParseException
    {
        super(instructions);

		if (instructions.size() < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		for (Job job : Jobs.getJobs()) 
		{
			if (job.getName().equalsIgnoreCase(instructions.getPart(1)))
			{
				sJobName = job.getName();
				return;
			}
		}
		throw new InstructionParseException("Jobs Reborn job " + instructions.getPart(1) + " does not exist" );
    }

    @Override
    public void run(String playerID) 
    {
		Player oPlayer =  PlayerConverter.getPlayer(playerID);
		for (Job job : Jobs.getJobs()) 
		{
			if (job.getName().equalsIgnoreCase(sJobName))
			{
				Jobs.getPlayerManager().getJobsPlayer(oPlayer).leaveJob(job);
				return;
			}
		}
    }
}
