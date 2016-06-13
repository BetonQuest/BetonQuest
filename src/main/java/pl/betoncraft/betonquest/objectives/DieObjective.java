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
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player needs to die. Death can be canceled, also respawn location can be set
 * 
 * @author Jakub Sapalski
 */
public class DieObjective extends Objective implements Listener {

	private final boolean cancel;
	private final Location location;

	public DieObjective(String packName, String label, String instruction) throws InstructionParseException {
		super(packName, label, instruction);
		template = ObjectiveData.class;
		boolean tempCancel = false;
		Location tempLoc = null;
		for (String part : instructions.split(" ")) {
			if (part.startsWith("respawn:")) {
				String[] rawLocParts = part.substring(8).split(";");
				if (rawLocParts.length == 4 || rawLocParts.length == 6) {
					World world = Bukkit.getWorld(rawLocParts[3]);
					if (world == null) {
						throw new InstructionParseException("World " + rawLocParts[3] + " does not exist");
					}
					double x, y, z;
					try {
						x = Double.parseDouble(rawLocParts[0]);
						y = Double.parseDouble(rawLocParts[1]);
						z = Double.parseDouble(rawLocParts[2]);
					} catch (NumberFormatException e) {
						throw new InstructionParseException("Could not parse coordinated");
					}
					float yaw = 0, pitch = 0;
					if (rawLocParts.length == 6) {
						try {
							yaw = Float.parseFloat(rawLocParts[4]);
							pitch = Float.parseFloat(rawLocParts[5]);
						} catch (NumberFormatException e) {
							throw new InstructionParseException("Could not parse direction");
						}
					}
					tempLoc = new Location(world, x, y, z, yaw, pitch);
				} else {
					throw new InstructionParseException("Could not parse location");
				}
			} else if (part.equalsIgnoreCase("cancel")) {
				tempCancel = true;
			}
		}
		cancel = tempCancel;
		location = tempLoc;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(EntityDeathEvent event) {
		if (cancel) {
			return;
		}
		if (event.getEntity() instanceof Player) {
			String playerID = PlayerConverter.getID((Player) event.getEntity());
			if (containsPlayer(playerID) && checkConditions(playerID)) {
				completeObjective(playerID);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onLastDamage(EntityDamageEvent event) {
		if (event.isCancelled() || !cancel) {
			return;
		}
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			final String playerID = PlayerConverter.getID(player);
			if (containsPlayer(playerID) && player.getHealth() - event.getFinalDamage() <= 0
					&& checkConditions(playerID)) {
				event.setCancelled(true);
				player.setHealth(player.getMaxHealth());
				player.setFoodLevel(20);
				player.setExhaustion(4);
				player.setSaturation(20);
				for (PotionEffect effect : player.getActivePotionEffects()) {
					player.removePotionEffect(effect.getType());
				}
				if (location != null) {
					player.teleport(location);
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						player.setFireTicks(0);

					}
				}.runTaskLater(BetonQuest.getInstance(), 1);
				completeObjective(playerID);
			}
		}
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
