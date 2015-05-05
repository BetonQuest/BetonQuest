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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class MobKillObjective extends Objective implements Listener {

    private EntityType mobType;
    private int amount;
    private String name;
    private boolean notify;

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public MobKillObjective(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        mobType = EntityType.valueOf(parts[1]);
        amount = Integer.valueOf(parts[2]);
        for (String part : parts) {
            if (part.contains("name:")) {
                name = part.substring(5);
            }
            if (part.equalsIgnoreCase("notify")) {
                notify = true;
            }
        }
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (name != null && (event.getEntity().getCustomName() == null || !event.getEntity()
                .getCustomName().equals(name.replaceAll("_", " ")))) {
            return;
        }
        if (event.getEntity().getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)) {
            EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) event.getEntity()
                    .getLastDamageCause();
            if (damage.getDamager() instanceof Player && ((Player) damage.getDamager())
                    .equals(PlayerConverter.getPlayer(playerID)) && damage.getEntity().getType()
                    .equals(mobType) && checkConditions()) {
                amount--;
                if (amount == 0) {
                    HandlerList.unregisterAll(this);
                    completeObjective();
                } else if (notify) {
                    Player player = PlayerConverter.getPlayer(playerID);
                    player.sendMessage(ConfigHandler.getString("messages." + ConfigHandler
                            .getString("config.language") + ".mobs_to_kill")
                            .replaceAll("%amount%", String.valueOf(amount)).replaceAll("&", "§"));
                }
            }
        } else if (event.getEntity().getLastDamageCause().getCause().equals(DamageCause.PROJECTILE)) {
            Projectile projectile = (Projectile) ((EntityDamageByEntityEvent) event.getEntity()
                    .getLastDamageCause()).getDamager();
            if (projectile.getShooter() instanceof Player
                && ((Player) projectile.getShooter()).equals(PlayerConverter.getPlayer(playerID))
                && event.getEntity().getType().equals(mobType) && checkConditions()) {
                amount--;
                if (amount == 0) {
                    HandlerList.unregisterAll(this);
                    completeObjective();
                } else if (notify) {
                    Player player = PlayerConverter.getPlayer(playerID);
                    player.sendMessage(ConfigHandler.getString("messages." + ConfigHandler
                            .getString("config.language") + ".mobs_to_kill")
                            .replaceAll("%amount%", String.valueOf(amount)));
                }
            }
        }
    }

    @Override
    public String getInstructions() {
        HandlerList.unregisterAll(this);
        String namePart = "";
        if (name != null) {
            namePart = " name:" + name + " ";
        }
        return "mobkill " + mobType.toString() + " " + amount + namePart + " " + conditions + " "
            + events + " tag:" + tag;
    }

}
