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
package pl.betoncraft.betonquest.variables;


import org.bukkit.Location;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Locale;

public class LocationVariable extends Variable {

    public LocationVariable(Instruction instruction) {
        super(instruction);
    }

    @Override
    public String getValue(String playerID) {
        Location loc = PlayerConverter.getPlayer(playerID).getLocation();
        return String.format(Locale.US, "%.2f;%.2f;%.2f;%s;%.2f;%.2f",
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getWorld().getName(),
                loc.getYaw(),
                loc.getPitch());
    }

}
