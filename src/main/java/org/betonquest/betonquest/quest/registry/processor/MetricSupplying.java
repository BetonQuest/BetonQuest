package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.bstats.CompositeInstructionMetricsSupplier;

/**
 * A Processor supplying BStats Metrics.
 */
@FunctionalInterface
public interface MetricSupplying {
    /**
     * Gets the bstats metric supplier for registered and active types.
     *
     * @return the metric
     */
    CompositeInstructionMetricsSupplier<?> metricsSupplier();
}
