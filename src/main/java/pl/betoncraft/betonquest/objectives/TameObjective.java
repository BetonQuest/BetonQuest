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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class TameObjective extends Objective implements Listener {

    public enum TamableMobs {
        WOLF, OCELOT, HORSE;
    }

    private TamableMobs type;
    private int amount;

    /**
     * Constructor method
     * 
     * @param playerID
     * @param instructions
     */
    public TameObjective(String playerID, String instructions) {
        super(playerID, instructions);
        type = TamableMobs.valueOf(instructions.split(" ")[1]);
        amount = Integer.parseInt(instructions.split(" ")[2]);
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler
    public void onTaming(EntityTameEvent event) {
        if (!((Player) event.getOwner()).equals(PlayerConverter.getPlayer(playerID))) {
            return;
        }
        LivingEntity entity = event.getEntity();
        switch (type) {
            case WOLF:
                if (entity.getType().equals(EntityType.WOLF) && checkConditions()) {
                    amount--;
                }
                break;
            case OCELOT:
                if (entity.getType().equals(EntityType.OCELOT) && checkConditions()) {
                    amount--;
                }
                break;
            case HORSE:
                if (entity.getType().equals(EntityType.HORSE) && checkConditions()) {
                    amount--;
                }
                break;
            default:
                break;
        }
        if (amount <= 0) {
            HandlerList.unregisterAll(this);
            completeObjective();
        }
    }

    @Override
    public String getInstructions() {
        HandlerList.unregisterAll(this);
        return "tame " + type + " " + amount + " " + conditions + " " + events + " label:" + tag;
    }

}
