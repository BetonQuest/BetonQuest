package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link BossBarNotifyIO}s.
 */
public class BossBarNotifyIOFactory implements NotifyIOFactory {

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * Plugin to start tasks.
     */
    private final Plugin plugin;

    /**
     * Create a new Factory.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param plugin       the plugin to start tasks
     */
    public BossBarNotifyIOFactory(final Placeholders placeholders, final Plugin plugin) {
        this.placeholders = placeholders;
        this.plugin = plugin;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new BossBarNotifyIO(placeholders, pack, categoryData, plugin);
    }
}
