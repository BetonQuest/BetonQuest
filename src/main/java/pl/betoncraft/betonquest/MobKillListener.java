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
package pl.betoncraft.betonquest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

import pl.betoncraft.betonquest.api.MobKillNotifier;

/**
 * Listens to standard kills and adds them to MobKillNotifier.
 * 
 * @author Jakub Sapalski
 */
public class MobKillListener implements Listener {

	public MobKillListener() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		// check if the damage cause actually exists; if it does not,
		// the whole checking is pointless
		if (event.getEntity() == null || event.getEntity().getLastDamageCause() == null
				|| event.getEntity().getLastDamageCause().getCause() == null) {
			return;
		}
		// handle the normal attack
		if (event.getEntity().getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)) {
			EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
			// if the damager is player, check if he has this objective
			if (damage.getDamager() instanceof Player) {
				Player player = (Player) damage.getDamager();
				MobKillNotifier.addKill(player, event.getEntity());
			}
			// handle projectile attack
		} else if (event.getEntity().getLastDamageCause().getCause().equals(DamageCause.PROJECTILE)) {
			Projectile projectile = (Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause())
					.getDamager();
			// check if the shooter was a player
			if (projectile.getShooter() instanceof Player) {
				Player player = (Player) projectile.getShooter();
				MobKillNotifier.addKill(player, event.getEntity());
			}
		}
	}

}
