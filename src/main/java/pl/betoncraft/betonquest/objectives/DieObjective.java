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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class DieObjective extends Objective implements Listener {
	
	private boolean cancel = false;
	private Location location = null;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public DieObjective(String playerID, String instructions) {
		super(playerID, instructions);
		if (instructions.contains("cancel")) {
			cancel = true;
		}
		for (String part : instructions.split(" ")) {
			if (part.contains("respawn:")) {
				String[] rawLocParts = part.substring(8).split(";");
				if (rawLocParts.length == 4) {
					location = new Location(Bukkit.getWorld(rawLocParts[3]), Double.parseDouble(rawLocParts[0]), Double.parseDouble(rawLocParts[1]), Double.parseDouble(rawLocParts[2]));
				} else if (rawLocParts.length == 6) {
					location = new Location(Bukkit.getWorld(rawLocParts[3]), Double.parseDouble(rawLocParts[0]), Double.parseDouble(rawLocParts[1]), Double.parseDouble(rawLocParts[2]), Float.parseFloat(rawLocParts[4]), Float.parseFloat(rawLocParts[5]));
				}
			}
		}
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (cancel) {
			return;
		}
		if (event.getEntity() instanceof Player && ((Player) event.getEntity()).equals(PlayerConverter.getPlayer(playerID)) && checkConditions()) {
			HandlerList.unregisterAll(this);
			completeObjective();
		}
	}
	
	@EventHandler
	public void onLastDamage(EntityDamageEvent event) {
		if (!cancel) {
			return;
		}
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			if (player.equals(PlayerConverter.getPlayer(playerID)) && player.getHealth() - event.getDamage() <= 0 && checkConditions()) {
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
				HandlerList.unregisterAll(this);
				new BukkitRunnable() {
					
					@Override
					public void run() {
						player.setFireTicks(0);
						
					}
				}.runTaskLater(BetonQuest.getInstance(), 1);
				completeObjective();
			}
		}
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return instructions;
	}

}
