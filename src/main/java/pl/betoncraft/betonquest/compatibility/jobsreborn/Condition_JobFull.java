package pl.betoncraft.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Condition;

public class Condition_JobFull extends Condition
{
	private String sJobName;
	
	public Condition_JobFull(Instruction instruction) throws InstructionParseException
	{
		super(instruction);
		if (instruction.size() < 2) 
		{
			throw new InstructionParseException("Not enough arguments");
		}
		for (Job job : Jobs.getJobs()) 
		{
			if (job.getName().equalsIgnoreCase(instruction.getPart(1)))
			{
				sJobName = job.getName();
				return;
			}
		}
		throw new InstructionParseException("Jobs Reborn job " + instruction.getPart(1) + " does not exist" );
	}

	public boolean check(String playerID)
	{
		for (Job job : Jobs.getJobs()) 
		{
			if (job.getName().equalsIgnoreCase(sJobName))
			{
				if (job.getMaxSlots() == null)
					return false;
				if (job.getTotalPlayers() >= job.getMaxSlots())
					return true;
			}
		}
		return false;
	}
}
