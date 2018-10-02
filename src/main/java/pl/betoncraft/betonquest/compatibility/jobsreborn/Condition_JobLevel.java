package pl.betoncraft.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.List;

public class Condition_JobLevel extends Condition
{
	private String sJobName;
	private int nMinLevel;
	private int nMaxLevel;
	
	public Condition_JobLevel(Instruction instruction) throws InstructionParseException
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
				try {
					this.nMinLevel = Integer.parseInt(instruction.getPart(2));
					this.nMaxLevel = Integer.parseInt(instruction.getPart(3));
				} catch (Exception err)
				{
					throw new InstructionParseException("NUJobs_Joblevel: Unable to parse the min or max level" );
				}
				return;
			}
		}
		throw new InstructionParseException("Jobs Reborn job " + instruction.getPart(1) + " does not exist" );
	}

	public boolean check(String playerID)
	{
		Player oPlayer =  PlayerConverter.getPlayer(playerID);
		
		List<JobProgression> oJobs = Jobs.getPlayerManager().getJobsPlayer(oPlayer).getJobProgression();
		for (JobProgression oJob : oJobs) 
		{
			if (oJob.getJob().getName().equalsIgnoreCase(sJobName))
			{
				//User has the job, return true
				if (oJob.getLevel() >= nMinLevel && oJob.getLevel() <= nMaxLevel)
					return true;
			}
		}
		return false;
	}
}
