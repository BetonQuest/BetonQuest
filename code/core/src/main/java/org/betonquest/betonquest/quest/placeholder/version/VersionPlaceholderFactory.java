package org.betonquest.betonquest.quest.placeholder.version;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Factory to create version placeholders from {@link Instruction}s.
 */
public class VersionPlaceholderFactory implements PlayerlessPlaceholderFactory {

    /**
     * The fallback plugin to use if no plugin is specified.
     */
    private final Plugin fallBackPlugin;

    /**
     * Create a new factory to create Version Placeholders.
     *
     * @param fallbackPlugin The fallback plugin instance.
     */
    public VersionPlaceholderFactory(final Plugin fallbackPlugin) {
        this.fallBackPlugin = fallbackPlugin;
    }

    @Override
    public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
        final Plugin plugin;
        if (instruction.hasNext()) {
            final String pluginName = String.join(".", instruction.getValueParts());
            plugin = Utils.getNN(Bukkit.getPluginManager().getPlugin(pluginName),
                    "Plugin " + pluginName + "does not exist!");
        } else {
            plugin = this.fallBackPlugin;
        }
        return new VersionPlaceholder(plugin);
    }
}
