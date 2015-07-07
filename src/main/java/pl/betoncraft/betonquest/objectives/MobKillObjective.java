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
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.core.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to kill specified amount of specified mobs.
 * It can also require the player to kill specifically named mobs
 * and notify them about the required amount.
 * 
 * @author Jakub Sapalski
 */
public class MobKillObjective extends Objective implements Listener {

    private final EntityType mobType;
    private final int amount;
    private final String name;
    private final boolean notify;

    public MobKillObjective(String packName, String label, String instruction)
            throws InstructionParseException {
        super(packName, label, instruction);
        template = MobData.class;
        String[] parts = instructions.split(" ");
        if (parts.length < 3) {
            throw new InstructionParseException("Not enough arguments");
        }
        try {
            mobType = EntityType.valueOf(parts[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InstructionParseException("Unknown entity type: " + parts[1]);
        }
        try {
            amount = Integer.valueOf(parts[2]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse amount");
        }
        if (amount < 1) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
        String tempName = null;
        boolean tempNotify = false;
        for (String part : parts) {
            if (part.startsWith("name:")) {
                tempName = part.substring(5).replace("_", " ");
            } else if (part.equalsIgnoreCase("notify")) {
                tempNotify = true;
            }
        }
        name = tempName;
        notify = tempNotify;
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        // check if the damage cause actually exists; if it does not,
        // the whole checking is pointless
        if (event.getEntity() == null || event.getEntity().getLastDamageCause()
                == null || event.getEntity().getLastDamageCause().getCause()
                == null) {
            return;
        }
        // check if it's the right entity type
        if (!event.getEntity().getType().equals(mobType)) {
            return;
        }
        // if the entity should have a name and it does not match, return
        if (name != null && (event.getEntity().getCustomName() == null ||
                !event.getEntity().getCustomName().equals(name))) {
            return;
        }
        // handle the normal attack
        if (event.getEntity().getLastDamageCause().getCause()
                .equals(DamageCause.ENTITY_ATTACK)) {
            EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent)
                    event.getEntity().getLastDamageCause();
            // if the damager is player, check if he has this objective
            if (damage.getDamager() instanceof Player) {
                String playerID = PlayerConverter.getID((Player) damage
                        .getDamager());
                if (containsPlayer(playerID) && checkConditions(playerID)) {
                    // the right mob was killed, handle data update
                    MobData playerData = (MobData) dataMap.get(playerID);
                    playerData.subtract();
                    if (playerData.isZero()) {
                        completeObjective(playerID);
                    } else if (notify) {
                        // send a notification
                        Config.sendMessage(playerID, "mobs_to_kill",
                                new String[]{String.valueOf(amount)});
                    }
                }
            }
        // handle projectile attack
        } else if (event.getEntity().getLastDamageCause().getCause()
                .equals(DamageCause.PROJECTILE)) {
            Projectile projectile = (Projectile) ((EntityDamageByEntityEvent)
                    event.getEntity().getLastDamageCause()).getDamager();
            // check if the shooter was a player
            if (projectile.getShooter() instanceof Player) {
                String playerID = PlayerConverter.getID((Player) projectile
                        .getShooter());
                // check if that player has this objective
                if (containsPlayer(playerID) && checkConditions(playerID)) {
                    // handle data update
                    MobData playerData = (MobData) dataMap.get(playerID);
                    playerData.subtract();
                    if (playerData.isZero()) {
                        completeObjective(playerID);
                    } else if (notify) {
                        // send a notification
                        Config.sendMessage(playerID, "mobs_to_kill",
                                new String[]{String.valueOf(amount)});
                    }
                }
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
        return Integer.toString(amount);
    }

    public static class MobData extends ObjectiveData {

        private int amount;

        public MobData(String instruction, String playerID, String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }
        
        private void subtract() {
            amount--;
            update();
        }
        
        private boolean isZero() {
            return amount <= 0;
        }

        @Override
        public String toString() {
            return Integer.toString(amount);
        }
        
    }
}
