package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link FileConfigAccessor}.
 */
public class ConfigComponent extends AbstractCoreComponent {

    /**
     * The configuration file name.
     */
    public static final String CONFIG_FILE = "config.yml";

    /**
     * Create a new ConfigComponent.
     */
    public ConfigComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BetonQuestLoggerFactory.class, ConfigAccessorFactory.class, Reloader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(FileConfigAccessor.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final ConfigAccessorFactory configAccessorFactory = getDependency(ConfigAccessorFactory.class);
        final Reloader reloader = getDependency(Reloader.class);

        final File dataFolder = plugin.getDataFolder();
        final File configurationFile = new File(dataFolder, CONFIG_FILE);

        try {
            final FileConfigAccessor config = configAccessorFactory.createPatching(configurationFile, plugin, CONFIG_FILE);
            dependencyProvider.take(FileConfigAccessor.class, config);
            reloader.register(ReloadPhase.CONFIG, () -> reload(config));
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            throw new IllegalStateException("Could not load the %s file!".formatted(CONFIG_FILE), e);
        }
    }

    private void reload(final FileConfigAccessor config) {
        try {
            config.reload();
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to reload the %s file!".formatted(CONFIG_FILE), e);
        }
    }
}
