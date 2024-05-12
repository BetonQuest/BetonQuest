package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.quest.condition.legacy.LegacyConditionFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows for checking multiple conditions with one instruction string.
 */
public class CheckCondition extends Condition {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Conditions that will be checked by this condition. All must be true for this condition to be true as well.
     */
    private final List<Condition> internalConditions = new ArrayList<>();

    /**
     * Create a check condition for the given instruction.
     *
     * @param instruction instruction defining this condition
     * @throws InstructionParseException if the instruction is not a valid check condition
     */
    public CheckCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        persistent = true;
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        final String[] parts = instruction.getAllParts();
        if (parts.length == 0) {
            throw new InstructionParseException("Not enough arguments");
        }
        StringBuilder builder = new StringBuilder();
        for (final String part : parts) {
            if (!part.isEmpty() && part.charAt(0) == '^') {
                if (!builder.isEmpty()) {
                    internalConditions.add(createCondition(builder.toString().trim()));
                    builder = new StringBuilder();
                }
                builder.append(part.substring(1)).append(' ');
            } else {
                builder.append(part).append(' ');
            }
        }
        internalConditions.add(createCondition(builder.toString().trim()));
    }

    /**
     * Constructs a condition with given instruction and returns it.
     */
    @Nullable
    private Condition createCondition(final String instruction) throws InstructionParseException {
        final String[] parts = instruction.split(" ");
        if (parts.length == 0) {
            throw new InstructionParseException("Not enough arguments in internal condition");
        }
        final LegacyConditionFactory conditionFactory = BetonQuest.getInstance().getQuestRegistries().getConditionTypes().getFactory(parts[0]);
        if (conditionFactory == null) {
            // if it's null then there is no such type registered, log an error
            throw new InstructionParseException("Condition type " + parts[0] + " is not registered, check if it's"
                    + " spelled correctly in internal condition");
        }
        try {
            final Instruction innerInstruction = new Instruction(BetonQuest.getInstance().getLoggerFactory().create(Instruction.class), this.instruction.getPackage(), new NoID(this.instruction.getPackage()), instruction);
            return conditionFactory.parseConditionInstruction(innerInstruction);
        } catch (final ObjectNotFoundException e) {
            if (e.getCause() instanceof InstructionParseException) {
                throw new InstructionParseException("Error in internal condition: " + e.getCause().getMessage(), e);
            } else {
                log.reportException(this.instruction.getPackage(), e);
            }
        }
        return null;
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        for (final Condition condition : internalConditions) {
            if (!condition.handle(profile)) {
                return false;
            }
        }
        return true;
    }
}
