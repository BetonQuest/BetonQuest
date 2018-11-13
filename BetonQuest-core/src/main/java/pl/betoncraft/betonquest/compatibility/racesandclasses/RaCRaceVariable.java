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
package pl.betoncraft.betonquest.compatibility.racesandclasses;

import de.tobiyas.racesandclasses.APIs.RaceAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Prints RaC race.
 *
 * @author Jakub Sapalski
 */
public class RaCRaceVariable extends Variable {

    public RaCRaceVariable(Instruction instruction) {
        super(instruction);
    }

    @Override
    public String getValue(String playerID) {
        RaceContainer race = RaceAPI.getRaceOfPlayer(PlayerConverter.getPlayer(playerID));
        return race == null ? "none" : race.getDisplayName();
    }

}
