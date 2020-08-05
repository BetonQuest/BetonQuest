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

import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Plays a sound for the player
 *
 * @author Jonas Blocher
 */
public class PlaysoundEvent extends QuestEvent {

    private final String sound;
    private final LocationData location;
    private final SoundCategory soundCategoty;
    private final float volume;
    private final float pitch;

    public PlaysoundEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        sound = instruction.next();
        location = instruction.getLocation(instruction.getOptional("location"));
        final String category = instruction.getOptional("category");
        if (category == null) {
            soundCategoty = SoundCategory.MASTER;
        } else {
            soundCategoty = instruction.getEnum(category, SoundCategory.class);
        }
        volume = (float) instruction.getDouble(instruction.getOptional("volume"), 1D);
        pitch = (float) instruction.getDouble(instruction.getOptional("pitch"), 1D);
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        if (location == null) {
            player.playSound(player.getLocation(), sound, soundCategoty, volume, pitch);
        } else {
            player.playSound(location.getLocation(playerID), sound, soundCategoty, volume, pitch);
        }
        return null;
    }
}
