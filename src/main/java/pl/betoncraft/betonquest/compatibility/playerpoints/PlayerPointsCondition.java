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
package pl.betoncraft.betonquest.compatibility.playerpoints;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.UUID;

/**
 * Checks if the player has specified amount of PlayerPoints points.
 *
 * @author Jakub Sapalski
 */
public class PlayerPointsCondition extends Condition {

    private VariableNumber count;
    private PlayerPointsAPI api;

    public PlayerPointsCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        count = instruction.getVarNum();
        api = ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI();
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        UUID uuid = PlayerConverter.getPlayer(playerID).getUniqueId();
        return api.look(uuid) >= count.getInt(playerID);
    }

}
