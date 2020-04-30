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

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * This event opens/closes/toggles doors, trapdoors and gates.
 *
 * @author Jakub Sapalski
 */
@SuppressWarnings("deprecation")
public class DoorEvent extends QuestEvent {

    private LocationData loc;
    private ToggleType type;

    public DoorEvent(Instruction instruction) throws InstructionParseException {
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
        BlockState state = block.getState();
        MaterialData data = state.getData();
        if (data instanceof Openable) {
            Openable openable = (Openable) data;
            switch (type) {
                case ON:
                    openable.setOpen(true);
                    break;
                case OFF:
                    openable.setOpen(false);
                    break;
                case TOGGLE:
                    openable.setOpen(!openable.isOpen());
                    break;
            }
            state.setData((MaterialData) openable);
            state.update();
        }
    }

    private enum ToggleType {
        ON, OFF, TOGGLE
    }

}
