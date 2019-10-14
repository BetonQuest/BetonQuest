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

import org.bukkit.World;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the weather to be of specific type
 *
 * @author Jakub Sapalski
 */
public class WeatherCondition extends Condition {

    private final String weather;

    public WeatherCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        weather = instruction.next().toLowerCase().trim();
        if (!weather.equals("sun") && !weather.equals("clear") && !weather.equals("rain") && !weather.equals("rainy")
                && !weather.equals("storm") && !weather.equals("thunder")) {
            throw new InstructionParseException("Weather type '" + weather + "' does not exist");
        }
    }

    @Override
    public boolean check(String playerID) {
        World world = PlayerConverter.getPlayer(playerID).getWorld();
        switch (weather) {
            case "sun":
            case "clear":
                if (!world.isThundering() && !world.hasStorm()) {
                    return true;
                }
                break;
            case "rain":
            case "rainy":
                if (world.hasStorm()) {
                    return true;
                }
                break;
            case "storm":
            case "thunder":
                if (world.isThundering()) {
                    return true;
                }
                break;
        }
        return false;
    }

}
