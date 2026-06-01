package org.betonquest.betonquest.faststats;

import dev.faststats.ErrorTracker;
import dev.faststats.Token;
import dev.faststats.bukkit.BukkitContext;
import dev.faststats.data.Metric;
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
     * The context instance to send metrics and tracked errors to FastStats.
     */
    private final BukkitContext context;

    /**
     * Create a new FastStatsMetrics instance using the given metrics providers and token.
     *
     * @param plugin           the plugin instance
     * @param token            the token to use for metrics publication
     * @param metricsProviders the metrics providers to use for metrics publication
     */
    public FastStatsMetrics(final Plugin plugin, @Token final String token, final Set<FastStatsMetricsProvider> metricsProviders) {
        this.errorTracker = ErrorTracker.contextAware();
        configureErrorTracker();
        final BukkitContext.Factory context = new BukkitContext.Factory(plugin, token);
        context.metrics(factory -> {
            for (final FastStatsMetricsProvider provider : metricsProviders) {
                final Set<Metric<?>> providerMetrics = provider.getMetrics();
                providerMetrics.forEach(factory::addMetric);
            }
            return factory.create();
        });
        context.errorTrackerService(this.errorTracker);
        this.context = context.create();
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
        this.context.ready();
    }

    /**
     * Disable the metrics.
     */
    public void disable() {
        this.context.shutdown();
    }
}
