package org.betonquest.betonquest.kernel.component;

import dev.faststats.data.Metric;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.faststats.FastStatsMetricsProvider;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.configuration.ConfigurationSection;
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
        return Set.of(FileConfigAccessor.class, ConfigMetrics.class);
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
            dependencyProvider.take(ConfigMetrics.class, new ConfigMetrics(config));
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

    /**
     * Metrics provider for config settings.
     *
     * @param fileConfigAccessor the config accessor to read the settings from
     */
    private record ConfigMetrics(FileConfigAccessor fileConfigAccessor) implements FastStatsMetricsProvider {

        @Override
        public Set<Metric<?>> getMetrics() {
            return Set.of(
                    Metric.string("c_server_language", () -> fileConfigAccessor.getString("language.default")),
                    Metric.string("c_conversation_default_io", () -> fileConfigAccessor.getString("conversation.default_io")),
                    Metric.string("c_conversation_interceptor_default", () -> fileConfigAccessor.getString("conversation.interceptor.default")),
                    Metric.number("c_npc_interaction_limit", () -> fileConfigAccessor.getInt("npc.interaction_limit")),
                    Metric.stringArray("c_integration_blacklisted", this::disabledHooks)
            );
        }

        private String[] disabledHooks() {
            final ConfigurationSection hookSection = fileConfigAccessor.getConfigurationSection("hook");
            if (hookSection == null) {
                return new String[0];
            }
            return hookSection.getKeys(false).stream()
                    .filter(key -> !hookSection.getBoolean(key, true))
                    .toList().toArray(String[]::new);
        }
    }
}
