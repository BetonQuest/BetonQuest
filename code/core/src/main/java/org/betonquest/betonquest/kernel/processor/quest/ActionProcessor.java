package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.id.IdentifierUtil;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.processor.adapter.ActionAdapter;
import org.betonquest.betonquest.kernel.registry.quest.ActionTypeRegistry;
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

/**
 * Stores Actions and execute them.
 */
public class ActionProcessor extends TypedQuestProcessor<ActionIdentifier, ActionAdapter> implements ActionManager {

    /**
     * The Bukkit scheduler to run sync tasks.
     */
    private final BukkitScheduler scheduler;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Factory to create section actions for configuration sections.
     */
    private final SectionFactory<ActionIdentifier, ActionAdapter> sectionFactory;

    /**
     * Create a new Action Processor to store actions and execute them.
     *
     * @param log                     the custom logger for this class
     * @param actionIdentifierFactory the factory to create action identifiers
     * @param actionTypes             the available action types
     * @param scheduler               the bukkit scheduler to run sync tasks
     * @param instructionApi          the instruction api
     * @param plugin                  the plugin instance
     * @param conditionManager        the condition manager instance
     */
    public ActionProcessor(final BetonQuestLogger log,
                           final IdentifierFactory<ActionIdentifier> actionIdentifierFactory,
                           final ActionTypeRegistry actionTypes, final BukkitScheduler scheduler,
                           final Instructions instructionApi, final Plugin plugin, final ConditionManager conditionManager) {
        super(log, actionTypes, actionIdentifierFactory, instructionApi, "Action", "actions");
        this.scheduler = scheduler;
        this.plugin = plugin;
        this.sectionFactory = identifier -> {
            final List<ActionIdentifier> identifiers = IdentifierUtil.subsectionIdentifiers(identifierFactory, identifier);
            final NullableActionAdapter adapter = new NullableActionAdapter(profile -> this.run(profile, identifiers));
            return new ActionAdapter(log, conditionManager, identifier, adapter, adapter, profile -> List.of());
        };
    }

    @Override
    @Nullable
    protected SectionFactory<ActionIdentifier, ActionAdapter> getSectionFactory() {
        return sectionFactory;
    }

    @Override
    public boolean run(@Nullable final Profile profile, final Collection<ActionIdentifier> actionIDS) {
        if (Bukkit.isPrimaryThread()) {
            return actionIDS.stream().map(actionID -> run(profile, actionID)).reduce(true, Boolean::logicalAnd);
        }

        final List<ActionIdentifier> syncList = new ArrayList<>();
        final List<ActionIdentifier> asyncList = new ArrayList<>();
        actionIDS.forEach(id -> {
            final ActionAdapter adapter = values.get(id);
            final boolean syncAsync = adapter != null && adapter.isPrimaryThreadEnforced();
            (syncAsync ? syncList : asyncList).add(id);
        });

        final Future<Boolean> syncFuture = syncList.isEmpty() ? CompletableFuture.completedFuture(true)
                : scheduler.callSyncMethod(plugin, () -> syncList.stream().map(actionID -> run(profile, actionID))
                .reduce(true, Boolean::logicalAnd));
        final boolean asyncResult = asyncList.stream().map(actionID -> run(profile, actionID)).reduce(true, Boolean::logicalAnd);

        try {
            return asyncResult && syncFuture.get();
        } catch (final InterruptedException | ExecutionException e) {
            log.reportException(e);
            return true;
        }
    }

    @Override
    public boolean run(@Nullable final Profile profile, final ActionIdentifier actionID) {
        final ActionAdapter action = values.get(actionID);
        if (action == null) {
            log.warn(actionID.getPackage(), "Action " + actionID + " is not defined");
            return false;
        }
        if (profile == null) {
            log.debug(actionID.getPackage(), "Firing action " + actionID + " player independent");
        } else {
            log.debug(actionID.getPackage(), "Firing action " + actionID + " for " + profile);
        }
        if (action.isPrimaryThreadEnforced() && !Bukkit.isPrimaryThread()) {
            return callActionSync(profile, actionID, action);
        }
        return callAction(profile, actionID, action);
    }

    private boolean callActionSync(@Nullable final Profile profile, final ActionIdentifier actionID, final ActionAdapter action) {
        try {
            return scheduler.callSyncMethod(plugin, () -> callAction(profile, actionID, action)).get();
        } catch (final InterruptedException | ExecutionException e) {
            log.reportException(e);
            return true;
        }
    }

    private boolean callAction(@Nullable final Profile profile, final ActionIdentifier actionID, final ActionAdapter action) {
        try {
            return action.fire(profile);
        } catch (final QuestException e) {
            log.warn(actionID.getPackage(), "Error while firing '" + actionID + "' action: " + e.getMessage(), e);
            return true;
        }
    }
}
