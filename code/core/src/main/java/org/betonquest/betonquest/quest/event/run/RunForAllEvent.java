package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestListException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.Variable;

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
    private final QuestTypeAPI questTypeAPI;

    /**
     * List of Events to run.
     */
    private final Variable<List<EventID>> events;

    /**
     * List of conditions each profile must meet to run the events.
     */
    private final Variable<List<ConditionID>> conditions;

    /**
     * Create a new RunForAllEvent instance.
     *
     * @param profileCollectionSupplier the supplier for generating the profiles
     * @param questTypeAPI              the Quest Type API
     * @param events                    the events to run
     * @param conditions                the conditions each profile must meet to run the events
     */
    public RunForAllEvent(final Supplier<? extends Iterable<? extends Profile>> profileCollectionSupplier,
                          final QuestTypeAPI questTypeAPI, final Variable<List<EventID>> events,
                          final Variable<List<ConditionID>> conditions) {
        this.profileCollectionSupplier = profileCollectionSupplier;
        this.questTypeAPI = questTypeAPI;
        this.events = events;
        this.conditions = conditions;
    }

    @Override
    public void execute() throws QuestException {
        final QuestListException questListException = new QuestListException("Could not run events for all profiles:");
        for (final Profile profile : profileCollectionSupplier.get()) {
            try {
                final List<ConditionID> resolvedConditions = conditions.getValue(profile);
                if (resolvedConditions.isEmpty() || questTypeAPI.conditions(profile, resolvedConditions.toArray(new ConditionID[0]))) {
                    for (final EventID event : events.getValue(profile)) {
                        questTypeAPI.event(profile, event);
                    }
                }
            } catch (final QuestException e) {
                questListException.addException(profile.toString(), e);
            }
        }
        questListException.throwIfNotEmpty();
    }
}
