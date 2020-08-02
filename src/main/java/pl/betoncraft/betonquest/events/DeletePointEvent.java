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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Point;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.database.Connector;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.database.Saver;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.logging.Level;

/**
 * Modifies player's points
 *
 * @author Jakub Sapalski
 */
public class DeletePointEvent extends QuestEvent {

    protected final String category;

    public DeletePointEvent(Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        persistent = true;
        staticness = true;
        category = Utils.addPackage(instruction.getPackage(), instruction.next());
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        if (playerID == null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(p));
                playerData.removePointsCategory(category);
            }
            BetonQuest.getInstance().getSaver().add(new Saver.Record(Connector.UpdateType.REMOVE_ALL_POINTS, new String[]{
                    category
            }));
        } else if (PlayerConverter.getPlayer(playerID) == null) {
            PlayerData playerData = new PlayerData(playerID);
            playerData.removePointsCategory(category);
        } else {
            PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
            playerData.removePointsCategory(category);
        }
        return null;
    }
}
