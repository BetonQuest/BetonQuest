package org.betonquest.betonquest.quest.variable.version;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Factory to create version variables from {@link Instruction}s.
 */
public class VersionVariableFactory implements PlayerlessVariableFactory {

    /**
     * The fallback plugin to use if no plugin is specified.
     */
    private final Plugin fallBackPlugin;

    /**
     * Create a new factory to create Version Variables.
     *
     * @param fallbackPlugin The fallback plugin instance.
     */
    public VersionVariableFactory(final Plugin fallbackPlugin) {
        this.fallBackPlugin = fallbackPlugin;
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        final Plugin plugin;
        if (instruction.hasNext()) {
            final String pluginName = String.join(".", instruction.getValueParts());
            plugin = Utils.getNN(Bukkit.getPluginManager().getPlugin(pluginName),
                    "Plugin " + pluginName + "does not exist!");
        } else {
            plugin = this.fallBackPlugin;
        }
        return new VersionVariable(plugin);
    }
}
