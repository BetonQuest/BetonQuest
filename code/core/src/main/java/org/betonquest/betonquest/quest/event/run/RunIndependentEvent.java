package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.event.CallPlayerlessEventAdapter;

import java.util.List;

/**
 * Runs specified events player independently.
 * <p>
 * Although the implementation is a {@link PlayerlessEvent}, using it in a static context does not make much sense.
 * Recommended usage is to wrap it in a {@link CallPlayerlessEventAdapter} and using it to call static events
 * from non-static context.
 */
public class RunIndependentEvent implements PlayerlessEvent {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * List of Events to run.
     */
    private final Variable<List<EventID>> events;

    /**
     * Create a new RunIndependentEvent instance.
     *
     * @param questTypeAPI the Quest Type API
     * @param events       the events to run
     */
    public RunIndependentEvent(final QuestTypeAPI questTypeAPI, final Variable<List<EventID>> events) {
        this.questTypeAPI = questTypeAPI;
        this.events = events;
    }

    @Override
    public void execute() throws QuestException {
        for (final EventID event : events.getValue(null)) {
            questTypeAPI.event(null, event);
        }
    }
}
