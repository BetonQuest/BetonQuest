package org.betonquest.betonquest.quest.action.random;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Fires randomly actions by weight and amount.
 */
public class PickRandomAction implements NullableAction {

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * The actions with there weight.
     */
    private final Argument<List<RandomAction>> actions;

    /**
     * The amount of actions to fire.
     */
    @Nullable
    private final Argument<Number> amount;

    /**
     * Creates a new PickRandomAction.
     *
     * @param actionManager the action manager
     * @param actions       the actions with there weight
     * @param amount        the amount of actions to fire
     */
    public PickRandomAction(final ActionManager actionManager, final Argument<List<RandomAction>> actions, @Nullable final Argument<Number> amount) {
        this.actionManager = actionManager;
        this.actions = actions;
        this.amount = amount;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final List<RandomAction> resolvedActions = new ArrayList<>(actions.getValue(profile));
        double total = resolvedActions.stream().mapToDouble(RandomAction::weight).sum();

        int pick = this.amount == null ? 1 : this.amount.getValue(profile).intValue();
        while (pick > 0 && !resolvedActions.isEmpty()) {
            pick--;
            double random = Math.random() * total;
            final Iterator<RandomAction> iterator = resolvedActions.iterator();
            while (iterator.hasNext()) {
                final RandomAction action = iterator.next();
                random -= action.weight();
                if (random < 0) {
                    actionManager.run(profile, action.actionID());
                    iterator.remove();
                    total -= action.weight();
                    break;
                }
            }
        }
    }
}
