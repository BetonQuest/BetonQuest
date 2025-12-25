package org.betonquest.betonquest.quest.variable.eval;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.jetbrains.annotations.Nullable;

/**
 * A variable which evaluates to another variable.
 */
public class EvalVariable implements NullableVariable {

    /**
     * The original instruction.
     */
    private final Instruction instruction;

    /**
     * The evaluation input.
     */
    private final Argument<String> evaluation;

    /**
     * Create a new Eval variable.
     *
     * @param instruction the original instruction
     * @param evaluation  the evaluation input
     */
    public EvalVariable(final Instruction instruction, final Argument<String> evaluation) {
        this.instruction = instruction;
        this.evaluation = evaluation;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        return instruction.get("%" + evaluation.getValue(profile) + "%", instruction.getParsers().string()).getValue(profile);
    }
}
