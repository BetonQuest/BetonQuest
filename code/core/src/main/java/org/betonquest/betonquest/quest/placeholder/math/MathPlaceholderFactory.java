package org.betonquest.betonquest.quest.placeholder.math;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.nullable.NullablePlaceholderAdapter;
import org.betonquest.betonquest.util.math.Tokenizer;
import org.betonquest.betonquest.util.math.tokens.Token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory to create math placeholders from {@link Instruction}s.
 */
public class MathPlaceholderFactory implements PlayerPlaceholderFactory, PlayerlessPlaceholderFactory {

    /**
     * Regular expression that matches calculation expressions.
     * The regex has a named group 'expression' that contains only the math part without the identifier.
     */
    public static final Pattern CALC_REGEX = Pattern.compile("calc:(?<expression>.+)");

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * Create a new factory to create Math Placeholders.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     */
    public MathPlaceholderFactory(final Placeholders placeholders) {
        this.placeholders = placeholders;
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @SuppressWarnings("deprecation")
    private NullablePlaceholderAdapter parseInstruction(final Instruction instruction) throws QuestException {
        final Matcher expressionMatcher = CALC_REGEX.matcher(String.join(".", instruction.getValueParts()));
        if (!expressionMatcher.matches()) {
            throw new QuestException("invalid format");
        }
        final String expression = expressionMatcher.group("expression");
        final Token token = new Tokenizer(placeholders, instruction.getPackage()).tokenize(expression);
        return new NullablePlaceholderAdapter(new MathPlaceholder(token));
    }
}
