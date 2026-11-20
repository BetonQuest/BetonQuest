package org.betonquest.betonquest.quest.action.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.QuestListException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;

import java.util.List;
import java.util.function.Supplier;

/**
 * Run given set of actions for all profiles supplied by {@link #profileCollectionSupplier} that meet the
 * conditions.
 */
public class RunForAllAction implements PlayerlessAction {

    /**
     * The supplier for generating the profiles.
     * <p>
     * Usually a list of all online players.
     */
    private final Supplier<? extends Iterable<? extends Profile>> profileCollectionSupplier;

    /**
     * The Action Manager.
     */
    private final ActionManager actionManager;

    /**
     * The Condition Manager.
     */
    private final ConditionManager conditionManager;

    /**
     * List of Actions to run.
     */
    private final Argument<List<ActionIdentifier>> actions;

    /**
     * List of conditions each profile must meet to run the actions.
     */
    private final Argument<List<ConditionIdentifier>> conditions;

    /**
     * Create a new RunForAllAction instance.
     *
     * @param profileCollectionSupplier the supplier for generating the profiles
     * @param actions                   the actions to run
     * @param actionManager             the Action Manager
     * @param conditionManager          the Condition Manager
     * @param conditions                the conditions each profile must meet to run the actions
     */
    public RunForAllAction(final Supplier<? extends Iterable<? extends Profile>> profileCollectionSupplier,
                           final Argument<List<ActionIdentifier>> actions, final ActionManager actionManager, final ConditionManager conditionManager,
                           final Argument<List<ConditionIdentifier>> conditions) {
        this.profileCollectionSupplier = profileCollectionSupplier;
        this.actions = actions;
        this.actionManager = actionManager;
        this.conditionManager = conditionManager;
        this.conditions = conditions;
    }

    @Override
    public void execute() throws QuestException {
        final QuestListException questListException = new QuestListException("Could not run actions for all profiles");
        for (final Profile profile : profileCollectionSupplier.get()) {
            try {
                final List<ConditionIdentifier> resolvedConditions = conditions.getValue(profile);
                if (resolvedConditions.isEmpty() || conditionManager.testAll(profile, resolvedConditions)) {
                    actionManager.run(profile, actions.getValue(profile));
                }
            } catch (final QuestException e) {
                questListException.addException(profile.toString(), e);
            }
        }
        questListException.throwIfNotEmpty();
    }
}
