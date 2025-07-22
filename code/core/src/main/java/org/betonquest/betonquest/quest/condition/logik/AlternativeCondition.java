package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * One of specified conditions has to be true.
 */
public class AlternativeCondition implements NullableCondition {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * List of condition IDs.
     */
    private final Variable<List<ConditionID>> conditionIDs;

    /**
     * The quest package.
     */
    private final QuestPackage questPackage;

    /**
     * Create a new alternative condition.
     *
     * @param log          the logger
     * @param conditionIDs the condition IDs
     * @param questPackage the quest package
     */
    public AlternativeCondition(final BetonQuestLogger log, final Variable<List<ConditionID>> conditionIDs, final QuestPackage questPackage) {
        this.log = log;
        this.conditionIDs = conditionIDs;
        this.questPackage = questPackage;
    }

    @Override
    @SuppressWarnings("PMD.CognitiveComplexity")
    public boolean check(@Nullable final Profile profile) throws QuestException {
        if (Bukkit.isPrimaryThread()) {
            for (final ConditionID id : conditionIDs.getValue(profile)) {
                if (BetonQuest.getInstance().getQuestTypeAPI().condition(profile, id)) {
                    return true;
                }
            }
        } else {
            final List<CompletableFuture<Boolean>> conditions = new ArrayList<>();
            for (final ConditionID id : conditionIDs.getValue(profile)) {
                final CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
                        () -> BetonQuest.getInstance().getQuestTypeAPI().condition(profile, id));
                conditions.add(future);
            }
            for (final CompletableFuture<Boolean> condition : conditions) {
                try {
                    if (condition.get()) {
                        return true;
                    }
                } catch (final InterruptedException | ExecutionException e) {
                    log.reportException(questPackage, e);
                    return false;
                }
            }
        }
        return false;
    }
}
