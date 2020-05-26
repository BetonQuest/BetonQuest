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
package pl.betoncraft.betonquest.conditions;

import org.bukkit.potion.PotionEffectType;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to have active potion effect
 *
 * @author Jakub Sapalski
 */
public class EffectCondition extends Condition {

    private final PotionEffectType type;

    public EffectCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String string = instruction.next();
        type = PotionEffectType.getByName(string);
        if (type == null) {
            throw new InstructionParseException("Effect " + string + " does not exist");
        }
    }

    @Override
    public boolean check(String playerID) {
        return PlayerConverter.getPlayer(playerID).hasPotionEffect(type);
    }

}
