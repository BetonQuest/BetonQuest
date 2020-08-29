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
package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

/**
 * Provides information about a citizen npc.
 * <p>
 * Format:
 * {@code %citizen.<id>.<type>%}
 * <p>
 * Types:
 * * name - Return citizen name
 * * full_name - Full Citizen name
 * * location  - Return citizen location. x;y;z;world;yaw;pitch
 */
public class CitizensVariable extends Variable {

    private int npcId;
    private TYPE key;

    public CitizensVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        npcId = instruction.getInt();
        try {
            key = TYPE.valueOf(instruction.next().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InstructionParseException("Invalid Type: " + instruction.current(), e);
        }
    }

    @Override
    public String getValue(final String playerID) {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            return "";
        }

        switch (key) {
            case NAME:
                return npc.getName();
            case FULL_NAME:
                return npc.getFullName();
            case LOCATION:
                if (npc.getEntity() != null) {
                    final Location loc = npc.getEntity().getLocation();
                    return String.format("%.2f;%.2f;%.2f;%s;%.2f;%.2f",
                            loc.getX(),
                            loc.getY(),
                            loc.getZ(),
                            loc.getWorld().getName(),
                            loc.getYaw(),
                            loc.getPitch());
                }
                break;
        }
        return "";
    }

    private enum TYPE {
        NAME,
        FULL_NAME,
        LOCATION
    }

}
