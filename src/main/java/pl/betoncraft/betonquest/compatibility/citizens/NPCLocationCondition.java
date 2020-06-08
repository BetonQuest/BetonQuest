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
package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Checks if a npc is at a specific location
 * <p>
 * Created on 01.10.2018.
 *
 * @author Jonas Blocher
 */
public class NPCLocationCondition extends Condition {

    private final int ID;
    private final LocationData location;
    private final VariableNumber radius;

    public NPCLocationCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        super.persistent = true;
        super.staticness = true;
        ID = instruction.getInt();
        location = instruction.getLocation();
        radius = instruction.getVarNum();
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        NPC npc = CitizensAPI.getNPCRegistry().getById(ID);
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + ID + " does not exist");
        }
        Entity npcEntity = npc.getEntity();
        if (npcEntity == null) return false;
        double radius = this.radius.getDouble(playerID);
        Location location = this.location.getLocation(playerID);
        if (!location.getWorld().equals(npcEntity.getWorld())) return false;
        return npcEntity.getLocation().distanceSquared(location) <= radius * radius;
    }
}
