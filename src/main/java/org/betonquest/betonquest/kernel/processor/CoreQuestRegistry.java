package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.EventProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;

import java.util.Map;

/**
 * Stores the active core quest type Processors to store and execute type logic.
 *
 * @param conditions Condition logic.
 * @param events     Event logic.
 * @param objectives Objective logic.
 * @param variables  Variable logic.
 */
public record CoreQuestRegistry(
        ConditionProcessor conditions,
        EventProcessor events,
        ObjectiveProcessor objectives,
        VariableProcessor variables
) {

    /**
     * Create a new Registry for storing and using Core Quest Types.
     *
     * @param loggerFactory       the logger factory used for new custom logger instances
     * @param questTypeRegistries the available quest types
     */
    public CoreQuestRegistry(final BetonQuestLoggerFactory loggerFactory, final QuestTypeRegistries questTypeRegistries) {
        this(
                new ConditionProcessor(loggerFactory.create(ConditionProcessor.class), questTypeRegistries.condition()),
                new EventProcessor(loggerFactory.create(EventProcessor.class), questTypeRegistries.event()),
                new ObjectiveProcessor(loggerFactory.create(ObjectiveProcessor.class), questTypeRegistries.objective()),
                new VariableProcessor(loggerFactory.create(VariableProcessor.class), questTypeRegistries.variable())
        );
    }

    /**
     * Clears the loaded Core Quest Types. Used before reloading all QuestPackages.
     */
    public void clear() {
        conditions.clear();
        events.clear();
        objectives.clear();
        variables.clear();
    }

    /**
     * Load all Core Quest Types from the QuestPackage.
     *
     * @param pack to load the core quest types from
     */
    public void load(final QuestPackage pack) {
        events.load(pack);
        conditions.load(pack);
        objectives.load(pack);
        variables.load(pack);
    }

    /**
     * Gets the bstats metric supplier for registered and active quest types.
     *
     * @return instruction metrics for core quest types
     */
    public Map<String, InstructionMetricsSupplier<? extends InstructionIdentifier>> metricsSupplier() {
        return Map.ofEntries(
                conditions.metricsSupplier(),
                events.metricsSupplier(),
                objectives.metricsSupplier(),
                variables.metricsSupplier()
        );
    }

    /**
     * Gets the amount of current loaded Core Quest Types with their readable name.
     *
     * @return the value size with the identifier
     */
    public String readableSize() {
        return String.join(", ", conditions.readableSize(), events.readableSize(),
                objectives.readableSize(), variables.readableSize());
    }
}
