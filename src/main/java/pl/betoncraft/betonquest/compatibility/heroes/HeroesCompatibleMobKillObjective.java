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
package pl.betoncraft.betonquest.compatibility.heroes;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import com.herocraftonline.heroes.api.events.HeroKillCharacterEvent;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.objectives.MobKillObjective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Handles kills done by Heroes plugin.
 * 
 * @author Jakub Sapalski
 */
public class HeroesCompatibleMobKillObjective extends MobKillObjective {

    public HeroesCompatibleMobKillObjective(String packName, String label,
            String instruction) throws InstructionParseException {
        super(packName, label, instruction);
    }
    
    @EventHandler
    public void onHeroesKill(HeroKillCharacterEvent event) {
        String playerID = PlayerConverter.getID(event.getAttacker().getPlayer());
        // check if the player has this objective
        if (!containsPlayer(playerID)) return;
        // check the entity
        Entity entity = event.getDefender().getEntity();
        if (entity.getType() != mobType) return;
        // if the entity should have a name and it does not match, return
        if (name != null && (entity.getCustomName() == null ||
                !entity.getCustomName().equals(name))) {
            return;
        }
        // check conditions and update the objective
        if (checkConditions(playerID)) {
            // the right mob was killed, handle data update
            MobData playerData = (MobData) dataMap.get(playerID);
            playerData.subtract();
            // complete the objective if there are no mobs left
            if (playerData.isZero()) {
                completeObjective(playerID);
            } else if (notify) {
                // send a notification if there are more mobs to kill
                Config.sendMessage(playerID, "mobs_to_kill",
                        new String[]{String.valueOf(playerData.getAmount())});
            }
        }
    }
    
    @Override
    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        // do nothing, everything is handled by onHeroesKill
    }

}
