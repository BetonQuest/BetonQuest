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
package pl.betoncraft.betonquest.compatibility.legendquest;

import me.sablednah.legendquest.Main;
import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks player's race.
 *
 * @author Jakub Sapalski
 */
public class LQRaceCondition extends Condition {

    private Main lq;
    private String raceName;

    public LQRaceCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        raceName = instruction.next();
        lq = ((Main) Bukkit.getPluginManager().getPlugin("LegendQuest"));
        if (lq.getRaces().getRace(raceName) == null) {
            throw new InstructionParseException("Race " + raceName + " does not exist");
        }
    }

    @Override
    public boolean check(String playerID) {
        return lq.getPlayers().getPC(PlayerConverter.getPlayer(playerID)).race.name.equalsIgnoreCase(raceName);
    }

}
