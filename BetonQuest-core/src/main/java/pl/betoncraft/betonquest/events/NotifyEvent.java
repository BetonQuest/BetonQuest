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

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.notify.Notify;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Send a Notification Message
 */

public class NotifyEvent extends QuestEvent {

    private Map<String, String> data;
    private String category;
    private String message;

    /**
     * Provide a Notification
     * <p>
     * Format of instruction:
     * notify message to send category:value [optional_data:value...]
     *
     * @param instruction Instruction to parse
     * @throws InstructionParseException
     */

    public NotifyEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);

        data = new HashMap<>();
        StringJoiner messageBuilder = new StringJoiner(" ");
        for (int i = 0; i < instruction.size() - 1; i++) {
            instruction.next();
            if (!instruction.current().contains(":")) {
                messageBuilder.add(instruction.current());
                continue;
            }
            String[] parts = instruction.current().split(":", 2);

            if (parts[0].trim().equalsIgnoreCase("category")) {
                category = parts[1].trim();
                continue;
            }

            data.put(parts[0].trim(), parts[1].trim());
        }

        message = messageBuilder.toString();
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        Player player = PlayerConverter.getPlayer(playerID);
        Notify.get(category, data).sendNotify(message, player);
    }

}
