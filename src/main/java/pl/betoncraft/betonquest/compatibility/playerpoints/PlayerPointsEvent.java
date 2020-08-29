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
package pl.betoncraft.betonquest.compatibility.playerpoints;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.UUID;

/**
 * Adds/removes/multiplies/divides PlayerPoints points.
 *
 * @author Jakub Sapalski
 */
public class PlayerPointsEvent extends QuestEvent {

    private VariableNumber count;
    private boolean multi;
    private PlayerPointsAPI api;

    public PlayerPointsEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        String string = instruction.next();
        if (string.startsWith("*")) {
            multi = true;
            string = string.replace("*", "");
        } else {
            multi = false;
        }
        try {
            count = new VariableNumber(instruction.getPackage().getName(), string);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse point amount", e);
        }
        api = ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final UUID uuid = PlayerConverter.getPlayer(playerID).getUniqueId();
        if (multi) {
            api.set(uuid, (int) Math.floor(api.look(uuid) * count.getDouble(playerID)));
        } else {
            final double amount = count.getDouble(playerID);
            if (amount < 0) {
                api.take(uuid, (int) Math.floor(-amount));
            } else {
                api.give(uuid, (int) Math.floor(amount));
            }
        }
        return null;
    }

}
