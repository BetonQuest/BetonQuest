/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016 Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.betoncraft.betonquest.compatibility.jobsreborn;

import java.util.List;

import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;


public class Event_DelLevel extends QuestEvent {
	private String	sJobName;
	private Integer	nAddLevel;

	public Event_DelLevel(final Instruction instructions) throws InstructionParseException {
		super(instructions, true);

		if(instructions.size() < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		for(final Job job : Jobs.getJobs()) {
			if(job.getName().equalsIgnoreCase(instructions.getPart(1))) {
				sJobName = job.getName();
				try {
					this.nAddLevel = Integer.parseInt(instructions.getPart(2));
				} catch(final Exception e) {
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
		for(final JobProgression oJob : oJobs) {
			if(oJob.getJob().getName().equalsIgnoreCase(sJobName)) {
				// User has the job, return true
				oJob.setLevel(oJob.getLevel() - this.nAddLevel);
				if(oJob.getLevel() < 1)
					oJob.setLevel(1);
			}
		}
		return null;
	}
}
