package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.processor.adapter.ConditionAdapter;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Does the logic around Conditions.
 */
public class ConditionProcessor extends TypedQuestProcessor<ConditionID, ConditionAdapter> {

    /**
     * Create a new Condition Processor to store Conditions and checks them.
     *
     * @param log            the custom logger for this class
     * @param variables      the variable processor to create and resolve variables
     * @param packManager    the quest package manager to get quest packages from
     * @param conditionTypes the available condition types
     */
    public ConditionProcessor(final BetonQuestLogger log, final Variables variables, final QuestPackageManager packManager,
                              final ConditionTypeRegistry conditionTypes) {
        super(log, variables, packManager, conditionTypes, "Condition", "conditions");
    }

    @Override
    protected ConditionID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new ConditionID(variables, packManager, pack, identifier);
    }

    /**
     * Checks if the conditions described by conditionID are met.
     *
     * @param profile      the {@link Profile} of the player which should be checked
     * @param conditionIDs IDs of the conditions to check
     * @return if all conditions are met
     */
    public boolean checks(@Nullable final Profile profile, final Collection<ConditionID> conditionIDs) {
        if (Bukkit.isPrimaryThread()) {
            return conditionIDs.stream().allMatch(id -> check(profile, id));
        }

        final Map<Boolean, List<ConditionID>> syncAsyncList = new HashMap<>();
        syncAsyncList.put(true, new ArrayList<>());
        syncAsyncList.put(false, new ArrayList<>());

        conditionIDs.forEach(id -> {
            final ConditionAdapter adapter = values.get(id);
            syncAsyncList.get(adapter != null && adapter.isPrimaryThreadEnforced()).add(id);
        });

        final Future<Boolean> syncFuture = Bukkit.getScheduler().callSyncMethod(BetonQuest.getInstance(),
                () -> syncAsyncList.get(true).stream().allMatch(id -> check(profile, id)));
        final boolean asyncResult = syncAsyncList.get(false).stream().allMatch((id) -> check(profile, id));

        try {
            return asyncResult && syncFuture.get();
        } catch (final InterruptedException | ExecutionException e) {
            log.reportException(e);
            return false;
        }
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
