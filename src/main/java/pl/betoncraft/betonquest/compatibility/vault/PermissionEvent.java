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
package pl.betoncraft.betonquest.compatibility.vault;

import net.milkbowl.vault.permission.Permission;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Manages player's permissions
 * 
 * @author Jakub Sapalski
 */
public class PermissionEvent extends QuestEvent {

	private final String world, permission;
	private final boolean add, perm;

	public PermissionEvent(Instruction instruction) throws InstructionParseException {
		super(instruction);
		add = instruction.next().equalsIgnoreCase("add");
		perm = instruction.next().equalsIgnoreCase("perm");
		permission = instruction.next();
		if (instruction.size() >= 5) {
			world = instruction.next();
		} else {
			world = null;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run(String playerID) {
		Permission vault = VaultIntegrator.getPermission();
		String player = PlayerConverter.getPlayer(playerID).getName();
		if (add) {
			if (perm) {
				// world add perm
				vault.playerAdd(world, player, permission);
			} else {
				// world add group
				vault.playerAddGroup(world, player, permission);
			}
		} else {
			if (perm) {
				// world remove perm
				vault.playerRemove(world, player, permission);
			} else {
				// world remove group
				vault.playerRemoveGroup(world, player, permission);
			}
		}
	}

}
