package org.betonquest.betonquest.quest.variable.eval;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.Variable;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.jetbrains.annotations.Nullable;

/**
 * A variable which evaluates to another variable.
 */
public class EvalVariable implements Variable {
    /**
     * The package.
     */
    private final QuestPackage pack;

    /**
     * The evaluation input.
     */
    private final VariableString evaluation;

    /**
     * Create a new Eval variable.
     *
     * @param pack       the package
     * @param evaluation the evaluation input
     */
    public EvalVariable(final QuestPackage pack, final VariableString evaluation) {
        this.pack = pack;
        this.evaluation = evaluation;
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        try {
            return new VariableString(pack, "%" + evaluation.getString(profile) + "%").getString(profile);
        } catch (final InstructionParseException e) {
            return "";
        }
    }
}
