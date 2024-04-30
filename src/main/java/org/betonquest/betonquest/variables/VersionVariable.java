package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;
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
        staticness = true;
        final int pointIndex = instruction.getInstruction().indexOf("\\.");
        if (pointIndex == -1) {
            plugin = BetonQuest.getInstance();
        } else {
            final String pluginName = instruction.getInstruction().substring(pointIndex + 1);
            plugin = Utils.getNN(Bukkit.getPluginManager().getPlugin(pluginName),
                    "Plugin " + pluginName + "does not exist!");
        }
    }

    @Override
    public String getValue(final Profile profile) {
        return plugin.getDescription().getVersion();
    }

}
