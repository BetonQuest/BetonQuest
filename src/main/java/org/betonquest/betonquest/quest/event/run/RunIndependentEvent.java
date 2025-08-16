package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
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
    private final QuestTypeApi questTypeApi;

    /**
     * List of Events to run.
     */
    private final Variable<List<EventID>> events;

    /**
     * Create a new RunIndependentEvent instance.
     *
     * @param questTypeApi the Quest Type API
     * @param events       the events to run
     */
    public RunIndependentEvent(final QuestTypeApi questTypeApi, final Variable<List<EventID>> events) {
        this.questTypeApi = questTypeApi;
        this.events = events;
    }

    @Override
    public void execute() throws QuestException {
        for (final EventID event : events.getValue(null)) {
            questTypeApi.event(null, event);
        }
    }
}
