package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.bstats.CompositeInstructionMetricsSupplier;
import org.betonquest.betonquest.id.ID;

import java.util.Map;

/**
 * Does the logic around {@link T} and stores their available types.
 * Also provides their BStats metrics.
 *
 * @param <I> the {@link ID} identifying {@link T}
 * @param <T> the quest type being processed
 * @param <U> the type of a {@link T}
 */
public abstract class TypedQuestProcessor<I extends ID, T, U> extends QuestProcessor<I, T> {
    /**
     * Available types of the {@link T}.
     */
    protected final Map<String, ? extends U> types;

    /**
     * BStats topic identifier.
     */
    private final String metricTopic;

    /**
     * Create a new QuestProcessor to store and execute {@link T} logic.
     *
     * @param log         the custom logger for this class
     * @param types       the available types of {@link T}
     * @param metricTopic the bstats topic identifier
     */
    public TypedQuestProcessor(final BetonQuestLogger log, final Map<String, ? extends U> types, final String metricTopic) {
        super(log);
        this.types = types;
        this.metricTopic = metricTopic;
    }

    /**
     * Gets the bstats metric supplier for registered and active types.
     *
     * @return the metric with its type identifier
     */
    public Map.Entry<String, CompositeInstructionMetricsSupplier<?>> metricsSupplier() {
        return Map.entry(metricTopic, new CompositeInstructionMetricsSupplier<>(values::keySet, types::keySet));
    }
}
