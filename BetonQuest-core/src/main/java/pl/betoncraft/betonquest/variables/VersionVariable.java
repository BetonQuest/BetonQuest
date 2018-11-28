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
package pl.betoncraft.betonquest.variables;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Variable;

/**
 * Displays version of the plugin.
 *
 * @author Jakub Sapalski
 */
public class VersionVariable extends Variable {

    private final Plugin plugin;

    public VersionVariable(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String[] parts = instruction.getInstruction().split("\\.");
        if (parts.length > 1) {
            plugin = Bukkit.getPluginManager().getPlugin(parts[1]);
            if (plugin == null)
                throw new InstructionParseException("Plugin " + parts[1] + "does not exist!");
        } else {
            plugin = BetonQuest.getInstance().getJavaPlugin();
        }
    }

    @Override
    public String getValue(String playerID) {
        return plugin.getDescription().getVersion();
    }

}
