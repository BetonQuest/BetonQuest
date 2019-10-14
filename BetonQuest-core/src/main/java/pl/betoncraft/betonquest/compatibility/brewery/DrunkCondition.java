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

package pl.betoncraft.betonquest.compatibility.brewery;

import com.dre.brewery.BPlayer;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class DrunkCondition extends Condition {

    private Integer drunkness;

    public DrunkCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);

        drunkness = instruction.getInt();

        if (drunkness < 0 || drunkness > 100) {
            throw new InstructionParseException("Drunkness can only be between 0 and 100!");
        }
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        BPlayer bPlayer = BPlayer.get(PlayerConverter.getPlayer(playerID));
        return bPlayer != null && bPlayer.getDrunkeness() >= drunkness;
    }
}
