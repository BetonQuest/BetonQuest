package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

/**
 * Fires randomly events by weight and amount.
 */
public class PickRandomEvent implements NullableEvent {
    /**
     * The events with there weight.
     */
    private final Variable<List<RandomEvent>> events;

    /**
     * The amount of events to fire.
     */
    @Nullable
    private final Variable<Number> amount;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Creates a new PickRandomEvent.
     *
     * @param events       the events with there weight
     * @param amount       the amount of events to fire
     * @param questTypeAPI the Quest Type API
     */
    public PickRandomEvent(final Variable<List<RandomEvent>> events, @Nullable final Variable<Number> amount, final QuestTypeAPI questTypeAPI) {
        this.events = events;
        this.amount = amount;
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final List<RandomEvent> resolvedEvents = events.getValue(profile);
        double total = resolvedEvents.stream().mapToDouble(RandomEvent::weight).sum();

        int pick = this.amount == null ? 1 : this.amount.getValue(profile).intValue();
        while (pick > 0 && !resolvedEvents.isEmpty()) {
            pick--;
            double random = Math.random() * total;
            final Iterator<RandomEvent> iterator = resolvedEvents.iterator();
            while (iterator.hasNext()) {
                final RandomEvent event = iterator.next();
                random -= event.weight();
                if (random < 0) {
                    questTypeAPI.event(profile, event.eventID());
                    iterator.remove();
                    total -= event.weight();
                    break;
                }
            }
        }
    }
}
