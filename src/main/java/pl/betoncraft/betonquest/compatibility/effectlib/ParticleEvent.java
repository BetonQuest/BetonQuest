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
package pl.betoncraft.betonquest.compatibility.effectlib;

import de.slikey.effectlib.util.DynamicLocation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Displays an effect.
 *
 * @author Jakub Sapalski
 */
public class ParticleEvent extends QuestEvent {

    private String effectClass;
    private ConfigurationSection parameters;
    private LocationData loc;
    private boolean pr1vate;

    public ParticleEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.next();
        parameters = instruction.getPackage().getCustom().getConfig().getConfigurationSection("effects." + string);
        if (parameters == null) {
            throw new InstructionParseException("Effect '" + string + "' does not exist!");
        }
        effectClass = parameters.getString("class");
        if (effectClass == null) {
            throw new InstructionParseException("Effect '" + string + "' is incorrectly defined");
        }
        loc = instruction.getLocation(instruction.getOptional("loc"));
        pr1vate = instruction.hasArgument("private");

    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final Location location = (loc == null) ? player.getLocation() : loc.getLocation(playerID);
        // This is not used at the moment
        // Entity originEntity = (loc == null) ? p : null;
        final Player targetPlayer = pr1vate ? player : null;
        EffectLibIntegrator.getEffectManager().start(effectClass,
                parameters,
                new DynamicLocation(location, null),
                new DynamicLocation(null, null),
                (ConfigurationSection) null,
                targetPlayer);
        return null;
    }

}
