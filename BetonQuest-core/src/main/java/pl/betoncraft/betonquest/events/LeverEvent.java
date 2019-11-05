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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * This event turns on, of or toggles levers.
 *
 * @author Jakub Sapalski
 */
public class LeverEvent extends QuestEvent {

    private LocationData loc;
    private ToggleType type;

    public LeverEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        staticness = true;
        persistent = true;
        loc = instruction.getLocation();
        String action = instruction.next();
        try {
            type = ToggleType.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InstructionParseException("Unknown action type '" + action + "', allowed are: on, off, toggle", e);
        }
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        Block block = loc.getLocation(playerID).getBlock();
        if (!block.getType().equals(Material.LEVER)) {
            return;
        }

        Powerable lever = (Powerable) block.getBlockData();

        switch (type) {
            case ON:
                lever.setPowered(true);
                break;
            case OFF:
                lever.setPowered(false);
                break;
            case TOGGLE:
                lever.setPowered(!lever.isPowered());
                break;
        }
    }

    private enum ToggleType {
        ON, OFF, TOGGLE
    }

}
