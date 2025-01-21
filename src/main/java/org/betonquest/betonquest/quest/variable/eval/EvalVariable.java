package org.betonquest.betonquest.quest.variable.eval;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.jetbrains.annotations.Nullable;

/**
 * A variable which evaluates to another variable.
 */
public class EvalVariable implements NullableVariable {
    /**
     * The variable processor used to create the evaluated variable.
     */
    private final VariableProcessor variableProcessor;

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
     * @param variableProcessor the variable processor
     * @param pack              the package
     * @param evaluation        the evaluation input
     */
    public EvalVariable(final VariableProcessor variableProcessor, final QuestPackage pack, final VariableString evaluation) {
        this.variableProcessor = variableProcessor;
        this.pack = pack;
        this.evaluation = evaluation;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        return new VariableString(variableProcessor, pack, "%" + evaluation.getValue(profile) + "%").getValue(profile);
    }
}
