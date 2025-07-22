package org.betonquest.betonquest.quest.event.variable;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory to create variable events from {@link Instruction}s.
 */
public class VariableEventFactory implements PlayerEventFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Create a new factory for {@link VariableEvent}s.
     *
     * @param questTypeAPI the Quest Type API
     */
    public VariableEventFactory(final QuestTypeAPI questTypeAPI) {
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<ObjectiveID> objectiveID = instruction.get(ObjectiveID::new);
        final Variable<String> key = instruction.get(Argument.STRING);
        final Variable<String> value = instruction.get(Argument.STRING);
        return new VariableEvent(questTypeAPI, objectiveID, key, value);
    }
}
