package org.betonquest.betonquest.faststats;

import dev.faststats.data.Metric;

import java.util.Set;

/**
 * Interface for providing metrics to FastStats.
 */
@FunctionalInterface
public interface FastStatsMetricsProvider {

    /**
     * Returns the metrics for this provider.
     *
     * @return the metrics
     */
    Set<Metric<?>> getMetrics();

    /**
     * Called when metrics have been flushed by FastStats.
     */
    default void metricsFlushed() {
        // Empty
    }
}
