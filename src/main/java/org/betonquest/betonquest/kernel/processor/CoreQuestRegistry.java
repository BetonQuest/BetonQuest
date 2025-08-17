package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.EventProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
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
) implements QuestTypeApi {

    /**
     * Create a new Registry for storing and using Core Quest Types.
     *
     * @param loggerFactory       the logger factory used for new custom logger instances
     * @param questPackageManager the quest package manager to use for the instruction
     * @param questTypeRegistries the available quest types
     */
    public CoreQuestRegistry(final BetonQuestLoggerFactory loggerFactory, final QuestPackageManager questPackageManager, final QuestTypeRegistries questTypeRegistries) {
        this(
                new ConditionProcessor(loggerFactory.create(ConditionProcessor.class), questPackageManager, questTypeRegistries.condition()),
                new EventProcessor(loggerFactory.create(EventProcessor.class), questPackageManager, questTypeRegistries.event()),
                new ObjectiveProcessor(loggerFactory.create(ObjectiveProcessor.class), questPackageManager, questTypeRegistries.objective()),
                new VariableProcessor(loggerFactory.create(VariableProcessor.class), questPackageManager, questTypeRegistries.variable())
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

    @Override
    public boolean conditions(@Nullable final Profile profile, final Collection<ConditionID> conditionIDs) {
        final ConditionID[] ids = new ConditionID[conditionIDs.size()];
        int index = 0;
        for (final ConditionID id : conditionIDs) {
            ids[index++] = id;
        }
        return conditions(profile, ids);
    }

    @Override
    public boolean conditions(@Nullable final Profile profile, final ConditionID... conditionIDs) {
        return conditions().checks(profile, conditionIDs);
    }

    @Override
    public boolean condition(@Nullable final Profile profile, final ConditionID conditionID) {
        return conditions().check(profile, conditionID);
    }

    @Override
    public boolean event(@Nullable final Profile profile, final EventID eventID) {
        return events().execute(profile, eventID);
    }

    @Override
    public void newObjective(final Profile profile, final ObjectiveID objectiveID) {
        objectives().start(profile, objectiveID);
    }

    @Override
    public void resumeObjective(final Profile profile, final ObjectiveID objectiveID, final String instruction) {
        objectives().resume(profile, objectiveID, instruction);
    }

    @Override
    public void renameObjective(final ObjectiveID name, final ObjectiveID rename) {
        objectives().renameObjective(name, rename);
    }

    @Override
    public List<Objective> getPlayerObjectives(final Profile profile) {
        return objectives().getActive(profile);
    }

    @Override
    public Objective getObjective(final ObjectiveID objectiveID) throws QuestException {
        return objectives().get(objectiveID);
    }
}
