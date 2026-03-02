package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link PluginMessage}.
 */
public class PluginMessageComponent extends AbstractCoreComponent {

    /**
     * Create a new PluginMessageComponent.
     */
    public PluginMessageComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BetonQuestLoggerFactory.class, ConfigAccessorFactory.class, LanguageProvider.class,
                PlayerDataStorage.class, PlaceholderManager.class, TextParser.class, Reloader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(PluginMessage.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ConfigAccessorFactory configAccessorFactory = getDependency(ConfigAccessorFactory.class);
        final LanguageProvider languageProvider = getDependency(LanguageProvider.class);
        final PlayerDataStorage playerDataStorage = getDependency(PlayerDataStorage.class);
        final PlaceholderManager placeholderManager = getDependency(PlaceholderManager.class);
        final TextParser textParser = getDependency(TextParser.class);
        final Plugin plugin = getDependency(Plugin.class);
        final Reloader reloader = getDependency(Reloader.class);

        final PluginMessage pluginMessage = new PluginMessage(loggerFactory.create(PluginMessage.class), plugin, placeholderManager,
                playerDataStorage, textParser, configAccessorFactory, languageProvider);

        dependencyProvider.take(PluginMessage.class, pluginMessage);
        reloader.register(ReloadPhase.PACKAGES, pluginMessage::reload);
    }
}
