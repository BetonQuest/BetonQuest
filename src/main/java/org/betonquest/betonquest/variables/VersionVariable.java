package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Displays version of the plugin.
 */
@SuppressWarnings("PMD.CommentRequired")
public class VersionVariable extends Variable {

    private final Plugin plugin;

    public VersionVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        final int pointIndex = instruction.getInstruction().indexOf("\\.");
        if (pointIndex == -1) {
            plugin = BetonQuest.getInstance();
        } else {
            final String pluginName = instruction.getInstruction().substring(pointIndex + 1);
            plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (plugin == null) {
                throw new InstructionParseException("Plugin " + pluginName + "does not exist!");
            }
        }
    }

    @Override
    public String getValue(final String playerID) {
        return plugin.getDescription().getVersion();
    }

}
