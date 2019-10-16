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

import org.bukkit.World;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Changes time on the server
 *
 * @author Jakub Sapalski
 */
public class TimeEvent extends QuestEvent {

    private final long amount;
    private final boolean add;

    public TimeEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String time = instruction.next();
        try {
            if (add = time.startsWith("+")) {
                amount = Long.valueOf(time.substring(1)) * 1000;
            } else {
                amount = Long.valueOf(time) * 1000 + 18000;
            }
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse time amount", e);
        }
    }

    @Override
    public void run(String playerID) {
        World world = PlayerConverter.getPlayer(playerID).getWorld();
        long time = amount;
        if (add) {
            time += world.getTime();
        }
        world.setTime(time % 24000);
    }

}
