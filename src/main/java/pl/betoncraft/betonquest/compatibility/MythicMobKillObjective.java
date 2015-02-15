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
package pl.betoncraft.betonquest.compatibility;

import net.elseland.xikage.MythicMobs.API.Events.MythicMobDeathEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * @author co0sh
 *
 */
public class MythicMobKillObjective extends Objective implements Listener {

    private String name;
    private int amount = 1;

    /**
     * @param playerID
     * @param instructions
     */
    public MythicMobKillObjective(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        name = parts[1];
        for (String part : parts) {
            if (part.contains("amount:")) {
                amount = Integer.parseInt(part.substring(7));
                break;
            }
        }
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler
    public void onBossKill(MythicMobDeathEvent event) {
        if (event.getMobType().getInternalName().equals(name)
            && event.getKiller() instanceof Player) {
            Player player = (Player) event.getKiller();
            if (player.equals(PlayerConverter.getPlayer(playerID)) && checkConditions()) {
                amount--;
            }
        }
        if (amount <= 0) {
            completeObjective();
            HandlerList.unregisterAll(this);
        }
    }

    @Override
    public String getInstructions() {
        HandlerList.unregisterAll(this);
        return "mmobkill " + name + " amount:" + amount + " " + events + " " + conditions + " tag:"
            + tag;
    }

}
