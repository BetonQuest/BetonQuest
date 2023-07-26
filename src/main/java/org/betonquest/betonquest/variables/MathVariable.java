package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.math.Tokenizer;
import org.betonquest.betonquest.utils.math.tokens.Token;

import java.util.Locale;

/**
 * This variable evaluates the given calculation and returns the result.
 */
@SuppressWarnings({"PMD.CommentRequired", "deprecation"})
public class MathVariable extends Variable {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final Token calculation;

    public MathVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        final String instructionString = instruction.getInstruction();
        if (!instructionString.matches("math\\.calc:.+")) {
            throw new InstructionParseException("invalid format");
        }
        final String expression = instructionString.substring("math.calc:".length());
        this.calculation = new Tokenizer(instruction.getPackage()).tokenize(expression);
    }

    @Override
    public String getValue(final Profile profile) {
        try {
            final double value = this.calculation.resolve(profile);
            if (value % 1 == 0) {
                return String.format(Locale.US, "%.0f", value);
            }
            return String.valueOf(value);
        } catch (final QuestRuntimeException e) {
            log.warn(instruction.getPackage(), "Could not calculate '" + calculation + "' (" + e.getMessage() + "). Returning 0 instead.", e);
            return "0";
        }
    }
}
