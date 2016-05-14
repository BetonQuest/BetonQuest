/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to shoot a target with a bow
 * 
 * @author Jakub Sapalski
 */
public class ArrowShootObjective extends Objective implements Listener {

	private final Location location;
	private final double precision;

	public ArrowShootObjective(String packName, String label, String instruction) throws InstructionParseException {
		super(packName, label, instruction);
		template = ObjectiveData.class;
		String[] parts = instructions.split(" ");
		if (parts.length < 2) {
			throw new InstructionParseException("Not enough arguments");
		}
		String[] partsOfLoc = parts[1].split(";");
		if (partsOfLoc.length < 5) {
			throw new InstructionParseException("Wrong location format");
		}
		World world = Bukkit.getWorld(partsOfLoc[3]);
		if (world == null) {
			throw new InstructionParseException("World does not exist: " + partsOfLoc[3]);
		}
		double x, y, z;
		try {
			x = Double.valueOf(partsOfLoc[0]);
			y = Double.valueOf(partsOfLoc[1]);
			z = Double.valueOf(partsOfLoc[2]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse coordinates");
		}
		location = new Location(world, x, y, z);
		try {
			precision = Double.valueOf(partsOfLoc[4]);
			if (precision <= 0) {
				throw new InstructionParseException("Precision must be positive");
			}
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse precision");
		}
	}

	@EventHandler
	public void onArrowHit(ProjectileHitEvent event) {
		// check if it's the arrow shot by the player with active objectve
		final Projectile arrow = event.getEntity();
		if (arrow.getType() != EntityType.ARROW) {
			return;
		}
		if (!(arrow.getShooter() instanceof Player)) {
			return;
		}
		final Player player = (Player) arrow.getShooter();
		final String playerID = PlayerConverter.getID(player);
		if (!containsPlayer(playerID)) {
			return;
		}
		// check if the arrow is in the right place in the next tick
		// wait one tick, let the arrow land completely
		new BukkitRunnable() {
			@Override
			public void run() {
				Location arrowLocation = arrow.getLocation();
				if (arrowLocation == null) {
					return;
				}
				if (arrowLocation.getWorld().equals(location.getWorld())
						&& arrowLocation.distanceSquared(location) < precision * precision
						&& checkConditions(playerID)) {
					completeObjective(playerID);
				}
			}
		}.runTask(BetonQuest.getInstance());
	}

	@Override
	public void start() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}

	@Override
	public String getDefaultDataInstruction() {
		return "";
	}

}
