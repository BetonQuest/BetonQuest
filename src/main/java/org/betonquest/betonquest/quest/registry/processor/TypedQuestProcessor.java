package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.bstats.CompositeInstructionMetricsSupplier;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.quest.registry.type.QuestTypeRegistry;

import java.util.Map;

/**
 * Does the logic around a quest type and stores their type registry.
 * Also provides their BStats metrics.
 *
 * @param <I> the {@link ID} identifying the type
 * @param <T> the legacy type
 */
public abstract class TypedQuestProcessor<I extends ID, T> extends QuestProcessor<I, T> {
    /**
     * Available types.
     */
    protected final QuestTypeRegistry<?, ?, ?, T> types;

    /**
     * BStats topic identifier.
     */
    private final String metricTopic;

    /**
     * Create a new QuestProcessor to store and execute type logic.
     *
     * @param log         the custom logger for this class
     * @param types       the available types
     * @param metricTopic the bstats topic identifier
     */
    public TypedQuestProcessor(final BetonQuestLogger log, final QuestTypeRegistry<?, ?, ?, T> types, final String metricTopic) {
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
