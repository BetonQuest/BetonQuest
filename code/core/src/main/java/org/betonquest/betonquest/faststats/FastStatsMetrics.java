package org.betonquest.betonquest.faststats;

import dev.faststats.ErrorTracker;
import dev.faststats.Token;
import dev.faststats.bukkit.BukkitContext;
import dev.faststats.data.Metric;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * The metrics handler implementation for FastStats.
 */
public class FastStatsMetrics {

    /**
     * The error tracker to use for faststats error tracking.
     */
    @Nullable
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
     * @param logErrors        whether to log errors
     */
    public FastStatsMetrics(final Plugin plugin, @Token final String token, final Set<FastStatsMetricsProvider> metricsProviders,
                            final boolean logErrors) {
        final BukkitContext.Factory context = new BukkitContext.Factory(plugin, token);
        context.metrics(factory -> {
            for (final FastStatsMetricsProvider provider : metricsProviders) {
                final Set<Metric<?>> providerMetrics = provider.getMetrics();
                providerMetrics.forEach(factory::addMetric);
            }
            factory.onFlush(() -> metricsProviders.forEach(FastStatsMetricsProvider::metricsFlushed));
            return factory.create();
        });
        if (logErrors) {
            this.errorTracker = ErrorTracker.contextAware();
            configureErrorTracker();
            context.errorTrackerService(this.errorTracker);
        } else {
            this.errorTracker = null;
        }
        this.context = context.create();
    }

    private void configureErrorTracker() {
        if (errorTracker == null) {
            return;
        }
        errorTracker
                .anonymize("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", "[[E-MAIL]]")
                .anonymize("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "[[UUID]]");
    }

    /**
     * Get the error tracker used by this metrics instance.
     *
     * @return the error tracker
     */
    public Optional<ErrorTracker> getErrorTracker() {
        return Optional.ofNullable(errorTracker);
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
