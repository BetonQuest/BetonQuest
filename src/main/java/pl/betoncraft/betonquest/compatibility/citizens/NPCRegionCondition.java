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
package pl.betoncraft.betonquest.compatibility.citizens;

import org.bukkit.entity.Entity;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;

/**
 * Checks if a npc is inside a region
 * <p>
 * Created on 01.10.2018.
 *
 * @author Jonas Blocher
 */
public class NPCRegionCondition extends Condition {

    private final int ID;
    private final String region;

    public NPCRegionCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        super.persistent = true;
        super.staticness = true;
        ID = instruction.getInt();
        region = instruction.next();
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        NPC npc = CitizensAPI.getNPCRegistry().getById(ID);
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + ID + " does not exist");
        }
        Entity npcEntity = npc.getEntity();
        if (npcEntity == null) return false;
        RegionManager manager = WorldGuardPlugin.inst().getRegionManager(npcEntity.getWorld());
        ProtectedRegion region = manager.getRegion(this.region);
        ApplicableRegionSet set = manager.getApplicableRegions(npcEntity.getLocation());
        for (ProtectedRegion compare : set) {
            if (compare.equals(region))
                return true;
        }
        return false;
    }
}
