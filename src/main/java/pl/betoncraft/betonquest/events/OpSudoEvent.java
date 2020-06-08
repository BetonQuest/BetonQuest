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
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

public class OpSudoEvent extends QuestEvent {

    private final String[] commands;

    public OpSudoEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        try {
            String string = instruction.getInstruction();
            commands = string.trim().substring(string.indexOf(" ") + 1).split("\\|");
        } catch (Exception e) {
            throw new InstructionParseException("Could not parse commands", e);
        }
    }

    @Override
    public void run(String playerID) {
        Player player = PlayerConverter.getPlayer(playerID);
        new BukkitRunnable() {

            @Override
            public void run() {
                boolean previousOp = player.isOp();
                try {
                    player.setOp(true);
                    for (String command : commands)
                        player.performCommand(command.replace("%player%", player.getName()));
                } catch (Exception e) {
                    LogUtils.getLogger().log(Level.WARNING, "Couldn't run OpSudoEvent.", e);
                    LogUtils.logThrowable(e);
                } finally {
                    player.setOp(previousOp);
                }
            }
        }.runTask(BetonQuest.getInstance());

    }

}
