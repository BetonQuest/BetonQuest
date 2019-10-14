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
 * Changes the weather on the server
 *
 * @author Jakub Sapalski
 */
public class WeatherEvent extends QuestEvent {

    private final boolean storm;
    private final boolean thunder;

    public WeatherEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String part = instruction.next();
        switch (part) {
            case "sun":
            case "clear":
                storm = false;
                thunder = false;
                break;
            case "rain":
            case "rainy":
                storm = true;
                thunder = false;
                break;
            case "storm":
            case "thunder":
                storm = false;
                thunder = true;
                break;
            default:
                throw new InstructionParseException("Weather type '" + part + "' does not exist");
        }
    }

    @Override
    public void run(String playerID) {
        World world = PlayerConverter.getPlayer(playerID).getWorld();
        world.setStorm(storm);
        world.setThundering(thunder);
    }

}
