package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link AdvancementNotifyIO}s.
 */
public class AdvancementNotifyIOFactory implements NotifyIOFactory {

    /**
     * Variable processor to create and resolve variables.
     */
    private final Variables variables;

    /**
     * Plugin to start tasks.
     */
    private final Plugin plugin;

    /**
     * Create a new factory.
     *
     * @param variables the variable processor to create and resolve variables
     * @param plugin    the plugin to start tasks
     */
    public AdvancementNotifyIOFactory(final Variables variables, final Plugin plugin) {
        this.variables = variables;
        this.plugin = plugin;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new AdvancementNotifyIO(variables, pack, categoryData, plugin);
    }
}
