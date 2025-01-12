package org.betonquest.betonquest.quest.event.variable;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

/**
 * Factory to create variable events from {@link Instruction}s.
 */
public class VariableEventFactory implements EventFactory {

    /**
     * BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new factory for {@link VariableEvent}s.
     *
     * @param betonQuest        the BetonQuest instance
     * @param variableProcessor the processor to create new variables
     */
    public VariableEventFactory(final BetonQuest betonQuest, final VariableProcessor variableProcessor) {
        this.betonQuest = betonQuest;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final ObjectiveID objectiveID = instruction.getObjective();
        final VariableString key = new VariableString(variableProcessor, instruction.getPackage(), instruction.next(), true);
        final VariableString value = new VariableString(variableProcessor, instruction.getPackage(), instruction.next(), true);
        return new VariableEvent(objectiveID, key, value, betonQuest);
    }
}
