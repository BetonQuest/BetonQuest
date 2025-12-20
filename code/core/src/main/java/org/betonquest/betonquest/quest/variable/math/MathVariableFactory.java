package org.betonquest.betonquest.quest.variable.math;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariableAdapter;
import org.betonquest.betonquest.util.math.Tokenizer;
import org.betonquest.betonquest.util.math.tokens.Token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory to create math variables from {@link DefaultInstruction}s.
 */
public class MathVariableFactory implements PlayerVariableFactory, PlayerlessVariableFactory {

    /**
     * Regular expression that matches calculation expressions.
     * The regex has a named group 'expression' that contains only the math part without the identifier.
     */
    public static final Pattern CALC_REGEX = Pattern.compile("calc:(?<expression>.+)");

    /**
     * The variable processor to use.
     */
    private final Variables variables;

    /**
     * Create a new factory to create Math Variables.
     *
     * @param variables the variable processor to create and resolve variables
     */
    public MathVariableFactory(final Variables variables) {
        this.variables = variables;
    }

    @Override
    public PlayerVariable parsePlayer(final DefaultInstruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerlessVariable parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @SuppressWarnings("deprecation")
    private NullableVariableAdapter parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Matcher expressionMatcher = CALC_REGEX.matcher(String.join(".", instruction.getValueParts()));
        if (!expressionMatcher.matches()) {
            throw new QuestException("invalid format");
        }
        final String expression = expressionMatcher.group("expression");
        final Token token = new Tokenizer(variables, instruction.getPackage()).tokenize(expression);
        return new NullableVariableAdapter(new MathVariable(token));
    }
}
