package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Fires randomly events by chance and amount.
 */
public class PickRandomEvent implements Event {
    /**
     * The events with there chance
     */
    private final List<RandomEvent> events;

    /**
     * The amount of events to fire.
     */
    private final VariableNumber amount;

    /**
     * Creates a new PickRandomEvent.
     *
     * @param events the events with there chance
     * @param amount the amount of events to fire
     */
    public PickRandomEvent(final List<RandomEvent> events, final VariableNumber amount) {
        this.events = events;
        this.amount = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final List<ResolvedRandomEvent> resolvedEvents = new LinkedList<>();
        for (final RandomEvent randomEvent : events) {
            resolvedEvents.add(randomEvent.resolveFor(profile));
        }
        double total = resolvedEvents.stream().mapToDouble(ResolvedRandomEvent::chance).sum();

        int pick = this.amount == null ? 1 : this.amount.getInt(profile);
        while (pick > 0 && !resolvedEvents.isEmpty()) {
            pick--;
            double random = Math.random() * total;
            final Iterator<ResolvedRandomEvent> iterator = resolvedEvents.iterator();
            while (iterator.hasNext()) {
                final ResolvedRandomEvent event = iterator.next();
                random -= event.chance();
                if (random < 0) {
                    BetonQuest.event(profile, event.eventID());
                    iterator.remove();
                    total -= event.chance();
                    break;
                }
            }
        }
    }
}
