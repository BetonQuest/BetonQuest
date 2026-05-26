package org.betonquest.betonquest.faststats;

import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.ErrorTracker;
import dev.faststats.core.Token;
import dev.faststats.core.data.Metric;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The metrics handler implementation for FastStats.
 */
public class FastStatsMetrics {

    /**
     * The error tracker to use for faststats error tracking.
     */
    private final ErrorTracker errorTracker;

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
        }
        metricsFactory.onFlush(() -> metricsProviders.forEach(FastStatsMetricsProvider::metricsFlushed));
        this.errorTracker = ErrorTracker.contextAware();
        configureErrorTracker();
        this.metrics = metricsFactory
                .errorTracker(errorTracker)
                .token(token)
                .create(plugin);
    }

    private void configureErrorTracker() {
        errorTracker
                .anonymize("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", "[[E-MAIL]]")
                .anonymize("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "[[UUID]]");
    }

    /**
     * Get the error tracker used by this metrics instance.
     *
     * @return the error tracker
     */
    public ErrorTracker getErrorTracker() {
        return errorTracker;
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
