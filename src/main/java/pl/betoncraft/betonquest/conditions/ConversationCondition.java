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

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.conversation.ConversationData;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Checks if the conversation with player has at least one possible option
 * <p>
 * Example:
 * {@code conversation <name of conversation>}
 **/
public class ConversationCondition extends Condition {

    private String conversationID;

    public ConversationCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);

        if (instruction.next() == null) {
            throw new InstructionParseException("Missing conversation parameter");
        }

        conversationID = instruction.current();
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {

        ConversationData conversation = BetonQuest.getInstance().getConversation(Utils.addPackage(instruction.getPackage(), conversationID));

        if (conversation == null) {
            throw new QuestRuntimeException("Conversation does not exist: " + instruction.getPackage().getName() + conversationID);
        }

        return conversation.isReady(playerID);
    }

}
