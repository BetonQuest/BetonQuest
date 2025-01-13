package org.betonquest.betonquest.quest.event.variable;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableString;

/**
 * Factory to create variable events from {@link Instruction}s.
 */
public class VariableEventFactory implements EventFactory {

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
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final ObjectiveID objectiveID = instruction.getID(ObjectiveID::new);
        final VariableString key = instruction.get(VariableArgument.STRING_REPLACE_UNDERSCORES);
        final VariableString value = instruction.get(VariableArgument.STRING_REPLACE_UNDERSCORES);
        return new VariableEvent(questTypeAPI, objectiveID, key, value);
    }
}
