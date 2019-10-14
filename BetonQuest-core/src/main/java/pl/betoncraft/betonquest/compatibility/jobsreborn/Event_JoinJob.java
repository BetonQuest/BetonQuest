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
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Event_JoinJob extends QuestEvent {
    private String sJobName;

    public Event_JoinJob(Instruction instructions) throws InstructionParseException {
        super(instructions);

        if (instructions.size() < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        for (Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(instructions.getPart(1))) {
                sJobName = job.getName();
                return;
            }
        }
        throw new InstructionParseException("Jobs Reborn job " + instructions.getPart(1) + " does not exist");
    }

    @Override
    public void run(String playerID) {
        Player oPlayer = PlayerConverter.getPlayer(playerID);
        for (Job job : Jobs.getJobs()) {
            if (job.getName().equalsIgnoreCase(sJobName)) {
                Jobs.getPlayerManager().getJobsPlayer(oPlayer).joinJob(job);
                return;
            }
        }
    }
}
