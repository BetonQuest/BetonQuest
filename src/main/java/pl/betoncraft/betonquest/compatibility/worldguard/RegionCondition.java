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
package pl.betoncraft.betonquest.compatibility.worldguard;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;


/**
 * Checks if the player is in specified region
 *
 * @author Jakub Sapalski
 */
public class RegionCondition extends Condition {

	private final String name;

	public RegionCondition(final Instruction instruction) throws InstructionParseException {
		super(instruction, true);
		name = instruction.next();
	}

	@Override
	protected Boolean execute(final String playerID) {
		final Player player = PlayerConverter.getPlayer(playerID);
		final WorldGuardPlatform worldguardPlatform = WorldGuard.getInstance().getPlatform();
		final RegionManager manager = worldguardPlatform.getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
		if(manager == null) {
			return false;
		}
		final ProtectedRegion region = manager.getRegion(name);
		final ApplicableRegionSet set = manager.getApplicableRegions(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation()
				.getZ()));
		for(final ProtectedRegion compare : set) {
			if(compare.equals(region))
				return true;
		}
		return false;
	}

}
