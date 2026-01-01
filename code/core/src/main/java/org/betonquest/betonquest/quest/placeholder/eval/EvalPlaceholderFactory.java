package org.betonquest.betonquest.quest.placeholder.eval;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.nullable.NullablePlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.nullable.NullablePlaceholderAdapter;

/**
 * A factory for creating Eval placeholders.
 */
public class EvalPlaceholderFactory implements PlayerPlaceholderFactory, PlayerlessPlaceholderFactory {

    /**
     * Create a new Eval placeholder factory.
     */
    public EvalPlaceholderFactory() {
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    /**
     * Parse an instruction into an {@link NullablePlaceholder}.
     *
     * @param instruction the instruction to parse
     * @return the parsed {@link NullablePlaceholder}
     * @throws QuestException if the instruction is invalid
     */
    protected NullablePlaceholder parseNullablePlaceholder(final Instruction instruction) throws QuestException {
        final String rawInstruction = String.join(".", instruction.getValueParts());
        return new EvalPlaceholder(instruction, instruction.chainForArgument(rawInstruction).string().get());
    }

    private NullablePlaceholderAdapter parseInstruction(final Instruction instruction) throws QuestException {
        return new NullablePlaceholderAdapter(parseNullablePlaceholder(instruction));
    }
}
