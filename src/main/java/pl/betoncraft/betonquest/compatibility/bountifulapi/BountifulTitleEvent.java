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
package pl.betoncraft.betonquest.compatibility.bountifulapi;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.events.TitleEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

public class BountifulTitleEvent extends TitleEvent {

    public BountifulTitleEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        String lang = BetonQuest.getInstance().getPlayerData(playerID).getLanguage();
        String message = messages.get(lang);
        if (message == null) {
            message = messages.get(Config.getLanguage());
        }
        if (message == null) {
            message = messages.values().iterator().next();
        }
        for (String variable : variables) {
            message = message.replace(variable,
                    BetonQuest.getInstance().getVariableValue(instruction.getPackage().getName(), variable, playerID));
        }
        if (fadeIn == 0 && stay == 0 && fadeOut == 0) {
            fadeIn = 20;
            stay = 100;
            fadeOut = 20;
        }
        Player player = PlayerConverter.getPlayer(playerID);
        switch (type) {
            case TITLE:
                BountifulAPI.sendTitle(player, fadeIn, stay, fadeOut, Utils.format(message), null);
                break;
            case SUBTITLE:
                BountifulAPI.sendTitle(player, fadeIn, stay, fadeOut, null, Utils.format(message));
                break;
        }
    }

}
