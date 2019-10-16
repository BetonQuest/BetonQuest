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

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

/**
 * Gives the player specified potion effect
 *
 * @author Jakub Sapalski
 */
public class EffectEvent extends QuestEvent {

    private final PotionEffectType effect;
    private final VariableNumber duration;
    private final VariableNumber amplifier;
    private final boolean ambient;
    private final boolean hidden;
    private final boolean icon;

    public EffectEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String type = instruction.next();
        effect = PotionEffectType.getByName(type);
        if (effect == null) {
            throw new InstructionParseException("Effect type '" + type + "' does not exist");
        }
        try {
            duration = instruction.getVarNum();
            amplifier = instruction.getVarNum();
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse number arguments", e);
        }

        if (instruction.hasArgument("--ambient")) {
            LogUtils.getLogger().log(Level.WARNING, instruction.getID().getFullID() + ": Effect event uses \"--ambient\" which is deprecated. Please use \"ambient\"");
            ambient = true;
        } else {
            ambient = instruction.hasArgument("ambient");
        }

        hidden = instruction.hasArgument("hidden");
        icon = !instruction.hasArgument("noicon");
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        PlayerConverter.getPlayer(playerID).addPotionEffect(
                new PotionEffect(effect, duration.getInt(playerID) * 20, amplifier.getInt(playerID) - 1, ambient, !hidden, icon));
    }

}
