/**
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
package pl.betoncraft.betonquest.compatibility.denizen;

import org.bukkit.entity.Player;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizencore.scripts.ScriptRegistry;
import net.aufdemrand.denizencore.scripts.containers.core.TaskScriptContainer;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Runs specified Denizen task script.
 * 
 * @author Jakub Sapalski
 */
public class DenizenTaskScriptEvent extends QuestEvent {

	private TaskScriptContainer taskScript;

	public DenizenTaskScriptEvent(Instruction instruction) throws InstructionParseException {
		super(instruction);
		String string = instruction.next();
		taskScript = ScriptRegistry.getScriptContainerAs(string, TaskScriptContainer.class);
		if (taskScript == null) {
			throw new InstructionParseException("Denizen script '" + string + "' is not defined");
		}
	}

	@Override
	public void run(String playerID) {
		Player player = PlayerConverter.getPlayer(playerID);
		BukkitScriptEntryData data = new BukkitScriptEntryData(dPlayer.mirrorBukkitPlayer(player), null);
		taskScript.runTaskScript(data, null);
	}

}
