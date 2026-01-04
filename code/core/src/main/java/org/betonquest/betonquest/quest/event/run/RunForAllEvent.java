package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.QuestListException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * Run given set of events for all profiles supplied by {@link #profileCollectionSupplier} that meet the
 * conditions.
 */
public class RunForAllEvent implements PlayerlessEvent {

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
     * List of Events to run.
     */
    private final Argument<List<ActionID>> events;

    /**
     * List of conditions each profile must meet to run the events.
     */
    private final Argument<List<ConditionID>> conditions;

    /**
     * Create a new RunForAllEvent instance.
     *
     * @param profileCollectionSupplier the supplier for generating the profiles
     * @param questTypeApi              the Quest Type API
     * @param events                    the events to run
     * @param conditions                the conditions each profile must meet to run the events
     */
    public RunForAllEvent(final Supplier<? extends Iterable<? extends Profile>> profileCollectionSupplier,
                          final QuestTypeApi questTypeApi, final Argument<List<ActionID>> events,
                          final Argument<List<ConditionID>> conditions) {
        this.profileCollectionSupplier = profileCollectionSupplier;
        this.questTypeApi = questTypeApi;
        this.events = events;
        this.conditions = conditions;
    }

    @Override
    public void execute() throws QuestException {
        final QuestListException questListException = new QuestListException("Could not run events for all profiles:");
        for (final Profile profile : profileCollectionSupplier.get()) {
            try {
                final List<ConditionID> resolvedConditions = conditions.getValue(profile);
                if (resolvedConditions.isEmpty() || questTypeApi.conditions(profile, resolvedConditions)) {
                    questTypeApi.events(profile, events.getValue(profile));
                }
            } catch (final QuestException e) {
                questListException.addException(profile.toString(), e);
            }
        }
        questListException.throwIfNotEmpty();
    }
}
