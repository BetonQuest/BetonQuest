package org.betonquest.betonquest.quest.event.variable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;

/**
 * Factory to create variable events from {@link Instruction}s.
 */
public class VariableEventFactory implements PlayerEventFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Create a new factory for {@link VariableEvent}s.
     *
     * @param questTypeApi the Quest Type API
     */
    public VariableEventFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<ObjectiveID> objectiveID = instruction.parse(ObjectiveID::new).get();
        final Variable<String> key = instruction.string().get();
        final Variable<String> value = instruction.string().get();
        return new VariableEvent(questTypeApi, objectiveID, key, value);
    }
}
