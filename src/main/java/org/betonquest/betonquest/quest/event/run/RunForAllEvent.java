package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
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
    private final List<EventID> eventIDS;

    /**
     * List of conditions each profile must meet to run the events.
     */
    private final List<ConditionID> conditionIDS;

    /**
     * Create a new RunForAllEvent instance.
     *
     * @param profileCollectionSupplier the supplier for generating the profiles
     * @param eventIDS                  the events to run
     * @param conditionIDS              the conditions each profile must meet to run the events
     */
    public RunForAllEvent(final Supplier<? extends Iterable<? extends Profile>> profileCollectionSupplier, final List<EventID> eventIDS, final List<ConditionID> conditionIDS) {
        this.profileCollectionSupplier = profileCollectionSupplier;
        this.eventIDS = eventIDS;
        this.conditionIDS = conditionIDS;
    }

    @Override
    public void execute() throws QuestRuntimeException {
        for (final Profile profile : profileCollectionSupplier.get()) {
            if (conditionIDS.isEmpty() || BetonQuest.conditions(profile, conditionIDS.toArray(new ConditionID[0]))) {
                for (final EventID event : eventIDS) {
                    BetonQuest.event(profile, event);
                }
            }
        }
    }
}
