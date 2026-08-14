package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.id.IdentifierUtil;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.processor.adapter.ConditionAdapter;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.condition.logik.ConjunctionCondition;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Does the logic around Conditions.
 */
public class ConditionProcessor extends TypedQuestProcessor<ConditionIdentifier, ConditionAdapter> implements ConditionManager {

    /**
     * The Bukkit scheduler to run sync tasks.
     */
    private final BukkitScheduler scheduler;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Factory to create section conditions for configuration sections.
     */
    private final SectionFactory<ConditionIdentifier, ConditionAdapter> sectionFactory;

    /**
     * Create a new Condition Processor to store Conditions and checks them.
     *
     * @param log                        the custom logger for this class
     * @param conditionTypes             the available condition types
     * @param scheduler                  the bukkit scheduler to run sync tasks
     * @param conditionIdentifierFactory the factory to create condition identifiers
     * @param plugin                     the plugin instance
     * @param instructionApi             the instruction api
     */
    public ConditionProcessor(final BetonQuestLogger log,
                              final ConditionTypeRegistry conditionTypes, final BukkitScheduler scheduler,
                              final IdentifierFactory<ConditionIdentifier> conditionIdentifierFactory, final Plugin plugin,
                              final Instructions instructionApi) {
        super(log, conditionTypes, conditionIdentifierFactory, instructionApi, "Condition", "conditions");
        this.scheduler = scheduler;
        this.plugin = plugin;
        this.sectionFactory = identifier -> {
            final List<ConditionIdentifier> identifiers = IdentifierUtil.subsectionIdentifiers(identifierFactory, identifier);
            final NullableConditionAdapter adapter = new NullableConditionAdapter(new ConjunctionCondition(profile -> identifiers, this));
            return new ConditionAdapter(identifier.getPackage(), adapter, adapter);
        };
    }

    @Override
    @Nullable
    protected SectionFactory<ConditionIdentifier, ConditionAdapter> getSectionFactory() {
        return sectionFactory;
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
                ? stream -> stream.allMatch(id -> test(profile, id))
                : stream -> stream.anyMatch(id -> test(profile, id));

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

    @Override
    public boolean testAll(@Nullable final Profile profile, final Collection<ConditionIdentifier> conditionIdentifiers) {
        return checks(profile, conditionIdentifiers, true);
    }

    @Override
    public boolean testAny(@Nullable final Profile profile, final Collection<ConditionIdentifier> conditionIdentifiers) {
        return checks(profile, conditionIdentifiers, false);
    }

    @Override
    public boolean test(@Nullable final Profile profile, final ConditionIdentifier conditionID) {
        final ConditionAdapter condition = values.get(conditionID);
        if (condition == null) {
            log.warn(conditionID.getPackage(), "The condition " + conditionID + " is not defined!");
            return false;
        }
        if (profile == null && !condition.allowsPlayerless()) {
            log.warn(conditionID.getPackage(),
                    "Cannot check player-dependent condition '%s' without a player, returning false".formatted(conditionID));
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

    @Override
    public boolean test(@Nullable final Profile profile, final Collection<ConditionIdentifier> conditionIdentifiers,
                        final TestStrategy testStrategy) {
        final Map<ConditionIdentifier, ConditionAdapter> conditions = new HashMap<>();
        boolean requiresMainThread = false;
        for (final ConditionIdentifier identifier : conditionIdentifiers) {
            final ConditionAdapter adapter = values.get(identifier);
            if (adapter == null) {
                log.warn(identifier.getPackage(), "The condition " + identifier + " is not defined!");
                return false;
            }
            conditions.put(identifier, adapter);
            requiresMainThread |= adapter.isPrimaryThreadEnforced();
        }
        if (!requiresMainThread || Bukkit.isPrimaryThread()) {
            return testStrategy(profile, conditions, testStrategy);
        }
        try {
            return scheduler.callSyncMethod(plugin, () -> testStrategy(profile, conditions, testStrategy)).get();
        } catch (final InterruptedException | ExecutionException e) {
            log.reportException(e);
            return false;
        }
    }

    private boolean testStrategy(@Nullable final Profile profile, final Map<ConditionIdentifier, ConditionAdapter> conditions,
                                 final TestStrategy testStrategy) {
        int positive = 0;
        int negative = 0;
        int remaining = conditions.size();
        for (final Map.Entry<ConditionIdentifier, ConditionAdapter> entry : conditions.entrySet()) {
            final ConditionAdapter adapter = entry.getValue();
            if (checkOutcome(profile, entry.getKey(), adapter)) {
                positive++;
            } else {
                negative++;
            }
            remaining--;
            final Optional<Boolean> result = testStrategy.getResult(positive, negative, remaining);
            if (result.isPresent()) {
                return result.get();
            }
        }
        return testStrategy.getResult(positive, negative, remaining).orElse(false);
    }
}
