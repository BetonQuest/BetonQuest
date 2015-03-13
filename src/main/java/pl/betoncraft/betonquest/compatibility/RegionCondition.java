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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Checks if the player is in specified region
 * 
 * @author Coosh
 */
public class RegionCondition extends Condition {

    private WorldGuardPlugin worldGuard;
    private ProtectedRegion region;
    private Player player;
    
    public RegionCondition(String playerID, String instructions) {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            Debug.error("Error in instruction string in: " + instructions);
            return;
        }
        player = PlayerConverter.getPlayer(playerID);
        worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        RegionManager manager = worldGuard.getRegionManager(player.getWorld());
        region = manager.getRegion(parts[1]);
    }

    @Override
    public boolean isMet() {
        if (region != null) {
            ApplicableRegionSet set = worldGuard.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
            for (ProtectedRegion compare : set) {
                if (compare.equals(region)) return true;
            }
        }
        return false;
    }

}
