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
package pl.betoncraft.betonquest.events;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Gives the player specified amount of experience
 *
 * @author Jonas Blocher
 */
public class EXPEvent extends QuestEvent {

    private final VariableNumber amount;
    private final boolean level;

    public EXPEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.amount = instruction.getVarNum();
        this.level = instruction.hasArgument("level") || instruction.hasArgument("l");
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        Player player = PlayerConverter.getPlayer(playerID);
        int amount = this.amount.getInt(playerID);
        if (level) {
            player.giveExpLevels(amount);
        } else {
            player.giveExp(amount);
        }
    }
}
