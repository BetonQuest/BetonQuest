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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the player is close to a npc
 * <p>
 * Created on 30.09.2018.
 *
 * @author Jonas Blocher
 */
public class NPCDistanceCondition extends Condition {

    private final int id;
    private final VariableNumber distance;

    public NPCDistanceCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        id = instruction.getInt();
        distance = instruction.getVarNum();
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        Player player = PlayerConverter.getPlayer(playerID);
        NPC npc = CitizensAPI.getNPCRegistry().getById(id);
        double distance = this.distance.getDouble(playerID);
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + id + " does not exist");
        }
        Entity npcEntity = npc.getEntity();
        if (npcEntity == null) return false;
        if (!npcEntity.getWorld().equals(player.getWorld())) return false;
        return npcEntity.getLocation().distanceSquared(player.getLocation()) <= distance * distance;
    }
}
