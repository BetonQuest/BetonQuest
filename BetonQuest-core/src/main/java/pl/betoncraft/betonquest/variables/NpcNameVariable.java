/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.variables;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.conversation.Conversation;

/**
 * This variable resolves into the name of the NPC.
 *
 * @author Jakub Sapalski
 */
public class NpcNameVariable extends Variable {

    public NpcNameVariable(Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @Override
    public String getValue(String playerID) {
        Conversation conv = Conversation.getConversation(playerID);
        if (conv == null)
            return "";
        return conv.getData().getQuester(BetonQuest.getInstance().getPlayerData(playerID).getLanguage());
    }

}
