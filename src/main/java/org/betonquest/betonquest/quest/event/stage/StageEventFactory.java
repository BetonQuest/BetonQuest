package org.betonquest.betonquest.quest.event.stage;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.objectives.StageObjective;

import java.util.Locale;

/**
 * Factory to create stage events to modify a StageObjective.
 */
public class StageEventFactory implements EventFactory {
    /**
     * BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Create the stage event factory.
     *
     * @param betonQuest BetonQuest instance
     */
    public StageEventFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final StageObjective stageObjective;
        try {
            stageObjective = (StageObjective) betonQuest.getObjective(instruction.getObjective());
        } catch (final ClassCastException e) {
            throw new InstructionParseException("Objective '" + instruction.getObjective() + "' is not a stage objective", e);
        }
        final String action = instruction.next();
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "set" -> createSetEvent(instruction, stageObjective);
            case "increase" -> createIncreaseEvent(instruction, stageObjective);
            case "decrease" -> createDecreaseEvent(instruction, stageObjective);
            default -> throw new InstructionParseException("Unknown action '" + action + "'");
        };
    }

    private Event createSetEvent(final Instruction instruction, final StageObjective stageObjective) throws InstructionParseException {
        final VariableString variableString = new VariableString(instruction.getPackage(), instruction.next());
        return new StageEvent(profile -> stageObjective.setStage(profile, variableString.getString(profile)));
    }

    private Event createIncreaseEvent(final Instruction instruction, final StageObjective stageObjective) throws InstructionParseException {
        final VariableNumber amount = instruction.getVarNum();
        return new StageEvent(profile -> stageObjective.increaseStage(profile, amount.getInt(profile)));
    }

    private Event createDecreaseEvent(final Instruction instruction, final StageObjective stageObjective) throws InstructionParseException {
        final VariableNumber amount = instruction.getVarNum();
        return new StageEvent(profile -> stageObjective.decreaseStage(profile, amount.getInt(profile)));
    }
}
