package org.betonquest.betonquest.conditions;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows for checking multiple conditions with one instruction string.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class CheckCondition extends Condition {

    private final List<Condition> internalConditions = new ArrayList<>();

    public CheckCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        final String[] parts = instruction.getInstruction().substring(5).trim().split(" ");
        if (parts.length <= 0) {
            throw new InstructionParseException("Not enough arguments");
        }
        StringBuilder builder = new StringBuilder();
        for (final String part : parts) {
            if (!part.isEmpty() && part.charAt(0) == '^') {
                if (builder.length() != 0) {
                    internalConditions.add(createCondition(builder.toString().trim()));
                    builder = new StringBuilder();
                }
                builder.append(part.substring(1)).append(" ");
            } else {
                builder.append(part).append(" ");
            }
        }
        internalConditions.add(createCondition(builder.toString().trim()));
    }

    /**
     * Constructs a condition with given instruction and returns it.
     */
    private Condition createCondition(final String instruction) throws InstructionParseException {
        final String[] parts = instruction.split(" ");
        if (parts.length <= 0) {
            throw new InstructionParseException("Not enough arguments in internal condition");
        }
        final Class<? extends Condition> conditionClass = BetonQuest.getInstance().getConditionClass(parts[0]);
        if (conditionClass == null) {
            // if it's null then there is no such type registered, log an error
            throw new InstructionParseException("Condition type " + parts[0] + " is not registered, check if it's"
                    + " spelled correctly in internal condition");
        }
        try {
            return conditionClass.getConstructor(Instruction.class).newInstance(
                    new Instruction(this.instruction.getPackage(), null, instruction));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            if (e.getCause() instanceof InstructionParseException) {
                throw new InstructionParseException("Error in internal condition: " + e.getCause().getMessage(), e);
            } else {
                LOG.reportException(this.instruction.getPackage(), e);
            }
        }
        return null;

    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        for (final Condition condition : internalConditions) {
            if (!condition.handle(playerID)) {
                return false;
            }
        }
        return true;
    }
}
