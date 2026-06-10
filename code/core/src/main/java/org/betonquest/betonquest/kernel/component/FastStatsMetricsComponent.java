package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.version.Version;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.faststats.FastStatsMetrics;
import org.betonquest.betonquest.faststats.FastStatsMetricsProvider;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.lib.version.BetonQuestVersion;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link FastStatsMetrics}.
 */
public class FastStatsMetricsComponent extends AbstractCoreComponent {

    /**
     * The token to use for metrics publication to FastStats.
     * According to FastStats' documentation, this token is safe for shipping with the plugin's code.
     */
    private static final String TOKEN = "a1afd23bbd7adcf82c3f6cc61fa1b684";

    /**
     * Create a new FastStatsMetricsComponent instance.
     */
    public FastStatsMetricsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(JavaPlugin.class, Compatibility.class, PluginDescriptionFile.class, BetonQuestLoggerFactory.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(FastStatsMetrics.class);
    }

    @Override
    protected boolean requires(final Class<?> type) {
        return FastStatsMetricsProvider.class.isAssignableFrom(type) || super.requires(type);
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    protected void load(final DependencyProvider dependencyProvider) {
        final JavaPlugin plugin = getDependency(JavaPlugin.class);
        final PluginDescriptionFile descriptionFile = getDependency(PluginDescriptionFile.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);

        final Version version = BetonQuestVersion.parse(descriptionFile.getVersion());
        final Optional<String> typeElement = version.getNamedElement("type");
        final boolean fastStatsErrorTrackingEnabled = typeElement.isPresent() && !"DEV-UNOFFICIAL".equals(typeElement.get());

        final Set<FastStatsMetricsProvider> fastStatsMetricsProviders = injectedDependencies.stream()
                .filter(injectedDependency -> FastStatsMetricsProvider.class.isAssignableFrom(injectedDependency.type()))
                .map(injectedDependency -> (FastStatsMetricsProvider) injectedDependency.dependency())
                .collect(Collectors.toSet());
        try {
            final FastStatsMetrics fastStatsMetrics = new FastStatsMetrics(plugin, TOKEN, fastStatsMetricsProviders, fastStatsErrorTrackingEnabled);
            fastStatsMetrics.enable();
            dependencyProvider.take(FastStatsMetrics.class, fastStatsMetrics);
        } catch (final Exception e) {
            loggerFactory.create(FastStatsMetrics.class).error("Could not enable FastStats metrics!", e);
        }
    }
}
