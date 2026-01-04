package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
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
    private final Argument<List<RandomEvent>> events;

    /**
     * The amount of events to fire.
     */
    @Nullable
    private final Argument<Number> amount;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Creates a new PickRandomEvent.
     *
     * @param events       the events with there weight
     * @param amount       the amount of events to fire
     * @param questTypeApi the Quest Type API
     */
    public PickRandomEvent(final Argument<List<RandomEvent>> events, @Nullable final Argument<Number> amount, final QuestTypeApi questTypeApi) {
        this.events = events;
        this.amount = amount;
        this.questTypeApi = questTypeApi;
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
                    questTypeApi.event(profile, event.actionID());
                    iterator.remove();
                    total -= event.weight();
                    break;
                }
            }
        }
    }
}
