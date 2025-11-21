package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.processor.adapter.ConditionAdapter;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Does the logic around Conditions.
 */
public class ConditionProcessor extends TypedQuestProcessor<ConditionID, ConditionAdapter> {
    /**
     * Create a new Condition Processor to store Conditions and checks them.
     *
     * @param log            the custom logger for this class
     * @param packManager    the quest package manager to get quest packages from
     * @param conditionTypes the available condition types
     */
    public ConditionProcessor(final BetonQuestLogger log, final QuestPackageManager packManager,
                              final ConditionTypeRegistry conditionTypes) {
        super(log, packManager, conditionTypes, "Condition", "conditions");
    }

    @Override
    protected ConditionID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new ConditionID(packManager, pack, identifier);
    }

    /**
     * Checks if the conditions described by conditionID are met.
     *
     * @param profile      the {@link Profile} of the player which should be checked
     * @param conditionIDs IDs of the conditions to check
     * @return if all conditions are met
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    public boolean checks(@Nullable final Profile profile, final ConditionID... conditionIDs) {
        if (Bukkit.isPrimaryThread()) {
            for (final ConditionID id : conditionIDs) {
                if (!check(profile, id)) {
                    return false;
                }
            }
        } else {
            final List<CompletableFuture<Boolean>> conditions = new ArrayList<>();
            for (final ConditionID id : conditionIDs) {
                final CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
                        () -> check(profile, id));
                conditions.add(future);
            }
            for (final CompletableFuture<Boolean> condition : conditions) {
                try {
                    if (!condition.get()) {
                        return false;
                    }
                } catch (final InterruptedException | ExecutionException e) {
                    // Currently conditions that are forced to be sync cause every CompletableFuture.get() call
                    // to delay the check by one tick.
                    // If this happens during a shutdown, the check will be delayed past the last tick.
                    // This will throw a CancellationException and IllegalPluginAccessExceptions.
                    if (Bukkit.getServer().isStopping()) {
                        log.debug("Exception during shutdown while checking conditions (expected):", e);
                        return false;
                    }
                    log.reportException(e);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the condition described by conditionID is met.
     *
     * @param conditionID ID of the condition to check
     * @param profile     the {@link Profile} of the player which should be checked
     * @return if the condition is met
     */
    public boolean check(@Nullable final Profile profile, final ConditionID conditionID) {
        final ConditionAdapter condition = values.get(conditionID);
        if (condition == null) {
            log.warn(conditionID.getPackage(), "The condition " + conditionID + " is not defined!");
            return false;
        }
        if (profile == null && !condition.allowsPlayerless()) {
            log.warn(conditionID.getPackage(),
                    "Cannot check non-static condition '" + conditionID + "' without a player, returning false");
            return false;
        }
        final boolean outcome;
        try {
            outcome = condition.check(profile);
        } catch (final QuestException e) {
            log.warn(conditionID.getPackage(), "Error while checking '" + conditionID + "' condition: " + e.getMessage(), e);
            return false;
        }
        final boolean isMet = outcome != conditionID.inverted();
        log.debug(conditionID.getPackage(),
                (isMet ? "TRUE" : "FALSE") + ": " + (conditionID.inverted() ? "inverted" : "") + " condition "
                        + conditionID + " for " + profile);
        return isMet;
    }
}
