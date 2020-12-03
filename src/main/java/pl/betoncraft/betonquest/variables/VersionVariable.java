package pl.betoncraft.betonquest.variables;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

/**
 * Displays version of the plugin.
 */
@SuppressWarnings("PMD.CommentRequired")
public class VersionVariable extends Variable {

    private final Plugin plugin;

    public VersionVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        final String[] parts = instruction.getInstruction().split("\\.");
        if (parts.length > 1) {
            plugin = Bukkit.getPluginManager().getPlugin(parts[1]);
            if (plugin == null) {
                throw new InstructionParseException("Plugin " + parts[1] + "does not exist!");
            }
        } else {
            plugin = BetonQuest.getInstance();
        }
    }

    @Override
    public String getValue(final String playerID) {
        return plugin.getDescription().getVersion();
    }

}
