/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

public class Condition_JobLevel extends Condition {
    private String sJobName;
    private int nMinLevel;
    private int nMaxLevel;

    public Condition_JobLevel(Instruction instruction) throws InstructionParseException {
        super(instruction);
        if (instruction.size() < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        for (Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(instruction.getPart(1))) {
                sJobName = job.getName();
                try {
                    this.nMinLevel = Integer.parseInt(instruction.getPart(2));
                    this.nMaxLevel = Integer.parseInt(instruction.getPart(3));
                } catch (Exception err) {
                    throw new InstructionParseException("NUJobs_Joblevel: Unable to parse the min or max level");
                }
                return;
            }
        }
        throw new InstructionParseException("Jobs Reborn job " + instruction.getPart(1) + " does not exist");
    }

    public boolean check(String playerID) {
        Player oPlayer = PlayerConverter.getPlayer(playerID);

        List<JobProgression> oJobs = Jobs.getPlayerManager().getJobsPlayer(oPlayer).getJobProgression();
        for (JobProgression oJob : oJobs) {
            if (oJob.getJob().getName().equalsIgnoreCase(sJobName)) {
                //User has the job, return true
                if (oJob.getLevel() >= nMinLevel && oJob.getLevel() <= nMaxLevel)
                    return true;
            }
        }
        return false;
    }
}
