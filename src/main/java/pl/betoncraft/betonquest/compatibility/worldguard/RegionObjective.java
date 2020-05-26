/*
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
package pl.betoncraft.betonquest.compatibility.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to enter the WorldGuard region
 *
 * @author Jakub Sapalski
 */
public class RegionObjective extends Objective implements Listener {

    private final String name;
    private boolean entry;
    private boolean exit;

    public RegionObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        name = instruction.next();
        entry = instruction.hasArgument("entry");
        exit = instruction.hasArgument("exit");
    }

    /**
     * Return true if location is inside region
     *
     * @param loc Location to Check
     * @return boolean True if in region
     */
    private boolean isInsideRegion(Location loc) {
        if (loc == null || loc.getWorld() == null) {
            return false;
        }

        WorldGuardPlatform worldguardPlatform = WorldGuard.getInstance().getPlatform();
        RegionManager manager = worldguardPlatform.getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
        if (manager == null) {
            return false;
        }

        ProtectedRegion region = manager.getRegion(name);
        if (region == null) {
            return false;
        }

        return region.contains(BukkitAdapter.asBlockVector(loc));
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID)) {
            return;
        }

        if (entry && isInsideRegion(event.getTo()) && !isInsideRegion(event.getFrom()) && checkConditions(playerID)) {
            completeObjective(playerID);
        }

        if (exit && isInsideRegion(event.getFrom()) && !isInsideRegion(event.getTo()) && checkConditions(playerID)) {
            completeObjective(playerID);
        }

        if (!entry && !exit && isInsideRegion(event.getTo()) && checkConditions(playerID)) {
            completeObjective(playerID);
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
