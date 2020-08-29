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

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the player has specified condition.
 *
 * @author Jakub Sapalski
 */
public class AdvancementCondition extends Condition {

    private final Advancement advancement;

    @SuppressWarnings("deprecation")
    public AdvancementCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String advancementString = instruction.next();
        if (!advancementString.contains(":")) {
            throw new InstructionParseException("The advancement '" + advancementString + "' is missing a namespace!");
        }
        final String[] split = advancementString.split(":");
        advancement = Bukkit.getServer().getAdvancement(new NamespacedKey(split[0], split[1]));
        if (advancement == null) {
            throw new InstructionParseException("No such advancement: " + advancementString);
        }
    }

    @Override
    protected Boolean execute(final String playerID) {
        final AdvancementProgress progress = PlayerConverter.getPlayer(playerID).getAdvancementProgress(advancement);
        return progress.isDone();
    }

}
