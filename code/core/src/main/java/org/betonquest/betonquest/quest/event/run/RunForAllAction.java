package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.QuestListException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.condition.ConditionID;

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
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * List of Actions to run.
     */
    private final Argument<List<ActionID>> actions;

    /**
     * List of conditions each profile must meet to run the actions.
     */
    private final Argument<List<ConditionID>> conditions;

    /**
     * Create a new RunForAllAction instance.
     *
     * @param profileCollectionSupplier the supplier for generating the profiles
     * @param questTypeApi              the Quest Type API
     * @param actions                   the actions to run
     * @param conditions                the conditions each profile must meet to run the actions
     */
    public RunForAllAction(final Supplier<? extends Iterable<? extends Profile>> profileCollectionSupplier,
                           final QuestTypeApi questTypeApi, final Argument<List<ActionID>> actions,
                           final Argument<List<ConditionID>> conditions) {
        this.profileCollectionSupplier = profileCollectionSupplier;
        this.questTypeApi = questTypeApi;
        this.actions = actions;
        this.conditions = conditions;
    }

    @Override
    public void execute() throws QuestException {
        final QuestListException questListException = new QuestListException("Could not run actions for all profiles:");
        for (final Profile profile : profileCollectionSupplier.get()) {
            try {
                final List<ConditionID> resolvedConditions = conditions.getValue(profile);
                if (resolvedConditions.isEmpty() || questTypeApi.conditions(profile, resolvedConditions)) {
                    questTypeApi.actions(profile, actions.getValue(profile));
                }
            } catch (final QuestException e) {
                questListException.addException(profile.toString(), e);
            }
        }
        questListException.throwIfNotEmpty();
    }
}
