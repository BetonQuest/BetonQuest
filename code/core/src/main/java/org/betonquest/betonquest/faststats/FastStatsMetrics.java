package org.betonquest.betonquest.faststats;

import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.Token;
import dev.faststats.core.data.Metric;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The metrics handler implementation for FastStats.
 */
public class FastStatsMetrics {

    /**
     * The metrics instance to send metrics to FastStats.
     */
    private final BukkitMetrics metrics;

    /**
     * Create a new FastStatsMetrics instance using the given metrics providers and token.
     *
     * @param plugin           the plugin instance
     * @param token            the token to use for metrics publication
     * @param metricsProviders the metrics providers to use for metrics publication
     */
    public FastStatsMetrics(final Plugin plugin, @Token final String token, final Set<FastStatsMetricsProvider> metricsProviders) {
        final BukkitMetrics.Factory metricsFactory = BukkitMetrics.factory();
        for (final FastStatsMetricsProvider provider : metricsProviders) {
            final Set<Metric<?>> providerMetrics = provider.getMetrics();
            providerMetrics.forEach(metricsFactory::addMetric);
            metricsFactory.onFlush(provider::metricsFlushed);
        }
        this.metrics = metricsFactory.token(token).create(plugin);
    }

    /**
     * Enable the metrics.
     */
    public void enable() {
        this.metrics.ready();
    }

    /**
     * Disable the metrics.
     */
    public void disable() {
        this.metrics.shutdown();
    }
}
