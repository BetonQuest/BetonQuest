package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;

import java.util.List;
import java.util.function.Supplier;

/**
 * Run given set of events for all profiles supplied by {@link #profileCollectionSupplier} that meet the
 * conditions.
 */
public class RunForAllEvent implements StaticEvent {

    /**
     * The supplier for generating the profiles.
     * <p>
     * Usually a list of all online players.
     */
    private final Supplier<? extends Iterable<? extends Profile>> profileCollectionSupplier;

    /**
     * List of Events to run.
     */
    private final List<EventID> events;

    /**
     * List of conditions each profile must meet to run the events.
     */
    private final List<ConditionID> conditions;

    /**
     * Create a new RunForAllEvent instance.
     *
     * @param profileCollectionSupplier the supplier for generating the profiles
     * @param events                    the events to run
     * @param conditions                the conditions each profile must meet to run the events
     */
    public RunForAllEvent(final Supplier<? extends Iterable<? extends Profile>> profileCollectionSupplier, final List<EventID> events, final List<ConditionID> conditions) {
        this.profileCollectionSupplier = profileCollectionSupplier;
        this.events = events;
        this.conditions = conditions;
    }

    @Override
    public void execute() throws QuestException {
        for (final Profile profile : profileCollectionSupplier.get()) {
            if (conditions.isEmpty() || BetonQuest.conditions(profile, conditions.toArray(new ConditionID[0]))) {
                for (final EventID event : events) {
                    BetonQuest.event(profile, event);
                }
            }
        }
    }
}
