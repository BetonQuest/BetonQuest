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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Teleports the player to specified location
 *
 * @author Jakub Sapalski
 */
public class TeleportEvent extends QuestEvent {

    private final LocationData loc;

    public TeleportEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        loc = instruction.getLocation();
    }

    public void run(String playerID) throws QuestRuntimeException {
        Conversation conv = Conversation.getConversation(playerID);
        if (conv != null)
            conv.endConversation();

        Location playerLocation = loc.getLocation(playerID);

        // Execute in main thread
        Bukkit.getScheduler().runTask(BetonQuest.getInstance(),
                () -> PlayerConverter.getPlayer(playerID).teleport(playerLocation));
    }
}
