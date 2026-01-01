package org.betonquest.betonquest.quest.placeholder.eval;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.nullable.NullablePlaceholder;
import org.jetbrains.annotations.Nullable;

/**
 * A placeholder which evaluates to another placeholder.
 */
public class EvalPlaceholder implements NullablePlaceholder {

    /**
     * The original instruction.
     */
    private final Instruction instruction;

    /**
     * The evaluation input.
     */
    private final Argument<String> evaluation;

    /**
     * Create a new Eval placeholder.
     *
     * @param instruction the original instruction
     * @param evaluation  the evaluation input
     */
    public EvalPlaceholder(final Instruction instruction, final Argument<String> evaluation) {
        this.instruction = instruction;
        this.evaluation = evaluation;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        return instruction.chainForArgument("%" + evaluation.getValue(profile) + "%").string().get().getValue(profile);
    }
}
