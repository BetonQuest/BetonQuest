package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Placeholders;
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
public class ConditionProcessor extends TypedQuestProcessor<ConditionIdentifier, ConditionAdapter> {

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
     * @param log                        the custom logger for this class
     * @param placeholders               the {@link Placeholders} to create and resolve placeholders
     * @param packManager                the quest package manager to get quest packages from
     * @param conditionTypes             the available condition types
     * @param scheduler                  the bukkit scheduler to run sync tasks
     * @param conditionIdentifierFactory the factory to create condition identifiers
     * @param plugin                     the plugin instance
     * @param instructionApi             the instruction api
     */
    public ConditionProcessor(final BetonQuestLogger log, final Placeholders placeholders, final QuestPackageManager packManager,
                              final ConditionTypeRegistry conditionTypes, final BukkitScheduler scheduler,
                              final IdentifierFactory<ConditionIdentifier> conditionIdentifierFactory, final Plugin plugin,
                              final InstructionApi instructionApi) {
        super(log, conditionTypes, conditionIdentifierFactory, instructionApi, "Condition", "conditions");
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    /**
     * Checks if the conditions described by conditionID are met.
     *
     * @param profile      the {@link Profile} of the player which should be checked
     * @param conditionIDs IDs of the conditions to check
     * @param matchAll     true if all conditions have to be met, false if only one condition has to be met
     * @return if all conditions are met
     */
    public boolean checks(@Nullable final Profile profile, final Collection<ConditionIdentifier> conditionIDs, final boolean matchAll) {
        final Function<Stream<ConditionIdentifier>, Boolean> allOrAnyMatch = matchAll
                ? stream -> stream.allMatch(id -> check(profile, id))
                : stream -> stream.anyMatch(id -> check(profile, id));

        if (Bukkit.isPrimaryThread()) {
            return allOrAnyMatch.apply(conditionIDs.stream());
        }

        final List<ConditionIdentifier> syncList = new ArrayList<>();
        final List<ConditionIdentifier> asyncList = new ArrayList<>();
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
    public boolean check(@Nullable final Profile profile, final ConditionIdentifier conditionID) {
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
        if (condition.isPrimaryThreadEnforced() && !Bukkit.isPrimaryThread()) {
            return checkOutcomeSync(profile, conditionID, condition);
        }
        return checkOutcome(profile, conditionID, condition);
    }

    private boolean checkOutcomeSync(@Nullable final Profile profile, final ConditionIdentifier conditionID, final ConditionAdapter condition) {
        try {
            return scheduler.callSyncMethod(plugin, () -> checkOutcome(profile, conditionID, condition)).get();
        } catch (final InterruptedException | ExecutionException e) {
            log.reportException(e);
            return false;
        }
    }

    private boolean checkOutcome(@Nullable final Profile profile, final ConditionIdentifier conditionID, final ConditionAdapter condition) {
        final boolean outcome;
        try {
            outcome = condition.check(profile);
        } catch (final QuestException e) {
            log.warn(conditionID.getPackage(), "Error while checking '" + conditionID + "' condition: " + e.getMessage(), e);
            return false;
        }
        final boolean isMet = outcome != conditionID.isInverted();
        log.debug(conditionID.getPackage(),
                (isMet ? "TRUE" : "FALSE") + ": " + (conditionID.isInverted() ? "inverted" : "") + " condition "
                        + conditionID + " for " + profile);
        return isMet;
    }
}
