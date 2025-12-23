package org.betonquest.betonquest.kernel.processor.quest;

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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Does the logic around Conditions.
 */
public class ConditionProcessor extends TypedQuestProcessor<ConditionID, ConditionAdapter> {

    /**
     * The Bukkit scheduler to run sync tasks.
     */
    private final BukkitScheduler scheduler;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Create a new Condition Processor to store Conditions and checks them.
     *
     * @param log            the custom logger for this class
     * @param variables      the variable processor to create and resolve variables
     * @param packManager    the quest package manager to get quest packages from
     * @param conditionTypes the available condition types
     * @param scheduler      the bukkit scheduler to run sync tasks
     * @param plugin         the plugin instance
     */
    public ConditionProcessor(final BetonQuestLogger log, final Variables variables, final QuestPackageManager packManager,
                              final ConditionTypeRegistry conditionTypes, final BukkitScheduler scheduler,
                              final Plugin plugin) {
        super(log, variables, packManager, conditionTypes, "Condition", "conditions");
        this.scheduler = scheduler;
        this.plugin = plugin;
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
     * @param matchAll     true if all conditions have to be met, false if only one condition has to be met
     * @return if all conditions are met
     */
    public boolean checks(@Nullable final Profile profile, final Collection<ConditionID> conditionIDs, final boolean matchAll) {
        final Function<Stream<ConditionID>, Boolean> allOrAnyMatch = matchAll
                ? stream -> stream.allMatch(id -> check(profile, id))
                : stream -> stream.anyMatch(id -> check(profile, id));

        if (Bukkit.isPrimaryThread()) {
            return allOrAnyMatch.apply(conditionIDs.stream());
        }

        final List<ConditionID> syncList = new ArrayList<>();
        final List<ConditionID> asyncList = new ArrayList<>();
        conditionIDs.forEach(id -> {
            final ConditionAdapter adapter = values.get(id);
            final boolean syncAsync = adapter != null && adapter.isPrimaryThreadEnforced();
            (syncAsync ? syncList : asyncList).add(id);
        });

        final Future<Boolean> syncFuture = syncList.isEmpty() ? CompletableFuture.completedFuture(matchAll)
                : scheduler.callSyncMethod(plugin, () -> allOrAnyMatch.apply(syncList.stream()));
        final boolean asyncResult = allOrAnyMatch.apply(asyncList.stream());

        try {
            return matchAll ? asyncResult && syncFuture.get() : asyncResult || syncFuture.get();
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
