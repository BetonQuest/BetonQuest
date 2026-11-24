package org.betonquest.betonquest.quest.placeholder.version;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholderAdapter;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Factory to create version placeholders from {@link Instruction}s.
 */
public class VersionPlaceholderFactory implements PlayerlessPlaceholderFactory, PlayerPlaceholderFactory {

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
        return parse(instruction);
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        return parse(instruction);
    }

    private NullablePlaceholderAdapter parse(final Instruction instruction) throws QuestException {
        final Argument<Plugin> plugin;
        if (instruction.hasNext()) {
            plugin = instruction.string().validate(name -> Bukkit.getPluginManager().getPlugin(name) != null, "Plugin '%s' does not exist")
                    .map(Bukkit.getPluginManager()::getPlugin).get();
        } else {
            plugin = profile -> this.fallBackPlugin;
        }
        return new NullablePlaceholderAdapter(new VersionPlaceholder(plugin));
    }
}
