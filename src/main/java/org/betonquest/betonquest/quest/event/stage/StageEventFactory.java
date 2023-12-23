package org.betonquest.betonquest.quest.event.stage;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.objectives.StageObjective;
import org.jetbrains.annotations.Nullable;

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
     * Creates the stage event factory.
     *
     * @param betonQuest BetonQuest instance
     */
    public StageEventFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final ObjectiveID objectiveID = instruction.getObjective();
        final String action = instruction.next();
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "set" -> createSetEvent(instruction, objectiveID);
            case "increase" -> createIncreaseEvent(instruction, objectiveID);
            case "decrease" -> createDecreaseEvent(instruction, objectiveID);
            default -> throw new InstructionParseException("Unknown action '" + action + "'");
        };
    }

    private Event createSetEvent(final Instruction instruction, final ObjectiveID objectiveID) throws InstructionParseException {
        final VariableString variableString = new VariableString(instruction.getPackage(), instruction.next());
        return new StageEvent(profile -> getStageObjective(objectiveID).setStage(profile, variableString.getString(profile)));
    }

    private Event createIncreaseEvent(final Instruction instruction, final ObjectiveID objectiveID) throws InstructionParseException {
        final VariableNumber amount = getVariableNumber(instruction);
        return new StageEvent(profile -> getStageObjective(objectiveID).increaseStage(profile, getAmount(profile, amount)));
    }

    private Event createDecreaseEvent(final Instruction instruction, final ObjectiveID objectiveID) throws InstructionParseException {
        final VariableNumber amount = getVariableNumber(instruction);
        return new StageEvent(profile -> getStageObjective(objectiveID).decreaseStage(profile, getAmount(profile, amount)));
    }

    @Nullable
    private VariableNumber getVariableNumber(final Instruction instruction) throws InstructionParseException {
        if (instruction.hasNext()) {
            final String stringAmount = instruction.next();
            if (!stringAmount.matches("condition(s)?:.+")) {
                return new VariableNumber(instruction.getPackage(), stringAmount);
            }
        }
        return null;
    }

    private int getAmount(final Profile profile, final VariableNumber amount) throws QuestRuntimeException {
        if (amount == null) {
            return 1;
        }
        final int targetAmount = amount.getInt(profile);
        if (targetAmount <= 0) {
            throw new QuestRuntimeException("Amount must be greater than 0");
        }
        return targetAmount;
    }

    private StageObjective getStageObjective(final ObjectiveID objectiveID) throws QuestRuntimeException {
        try {
            return (StageObjective) betonQuest.getObjective(objectiveID);
        } catch (final ClassCastException e) {
            throw new QuestRuntimeException("Objective '" + objectiveID.getFullID() + "' is not a stage objective", e);
        }
    }
}
