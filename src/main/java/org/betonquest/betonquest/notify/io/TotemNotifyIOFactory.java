package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link TotemNotifyIO}s.
 */
public class TotemNotifyIOFactory implements NotifyIOFactory {
    /**
     * Plugin to start tasks.
     */
    private final Plugin plugin;

    /**
     * Create a new factory.
     *
     * @param plugin the plugin to start tasks
     */
    public TotemNotifyIOFactory(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new TotemNotifyIO(pack, categoryData, plugin);
    }
}
